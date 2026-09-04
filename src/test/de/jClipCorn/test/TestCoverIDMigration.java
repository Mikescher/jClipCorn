package de.jClipCorn.test;

import de.jClipCorn.database.driver.MemoryDatabase;
import de.jClipCorn.database.migration.Migration_36_37;
import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.filesystem.FSPath;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Rewinds a freshly created database to the v36 COVERID layout (TEXT NOT NULL, nil uuid = "no cover")
 * and runs the migration that makes the column nullable.
 */
@SuppressWarnings("nls")
public class TestCoverIDMigration extends ClipCornBaseTest {

	private static final CCUUID MOV_WITH    = CCUUID.generate();
	private static final CCUUID MOV_WITHOUT = CCUUID.generate();
	private static final CCUUID SER         = CCUUID.generate();
	private static final CCUUID SEAS        = CCUUID.generate();

	private static final CCUUID CVR_MOV = CCUUID.generate();
	private static final CCUUID CVR_SER = CCUUID.generate();

	private static MemoryDatabase createV36Database() throws Exception {
		MemoryDatabase db = new MemoryDatabase();
		assertTrue(db.createNewDatabase(FSPath.Empty, "TEST"));

		for (String t : List.of("MOVIES", "SERIES", "SEASONS")) {
			db.executeSQLThrow("ALTER TABLE main." + t + " RENAME COLUMN COVERID TO COVERID_TMP");
			db.executeSQLThrow("ALTER TABLE main." + t + " ADD COLUMN COVERID TEXT NOT NULL DEFAULT ''");
			db.executeSQLThrow("ALTER TABLE main." + t + " DROP COLUMN COVERID_TMP");
		}

		db.executeSQLThrow("INSERT INTO main.INFO (IKEY, IVALUE) VALUES ('VERSION_DB','36')");

		insertRow(db, "MOVIES",  MOV_WITH,    "WithCover",    CVR_MOV.toString());
		insertRow(db, "MOVIES",  MOV_WITHOUT, "WithoutCover", CCUUID.EMPTY.toString());
		insertRow(db, "SERIES",  SER,         "Series",       CVR_SER.toString());
		insertRow(db, "SEASONS", SEAS,        "Season",       CCUUID.EMPTY.toString());

		return db;
	}

	/** Fills every column the table declares, so the fixture does not have to spell them all out. */
	private static void insertRow(MemoryDatabase db, String table, CCUUID id, String name, String coverid) throws Exception {
		StringBuilder names = new StringBuilder();
		StringBuilder vals  = new StringBuilder();

		for (Object[] col : db.querySQL("PRAGMA main.table_info(" + table + ")", 6)) {
			String colname = (String) col[1];
			String type    = ((String) col[2]).toUpperCase();

			Object v;
			if (colname.equals("ID"))            v = id.toString();
			else if (colname.equals("COVERID"))  v = coverid;
			else if (colname.equals("NAME"))     v = name;
			else if (colname.equals("SERIESID")) v = SER.toString();
			else if (type.equals("TEXT"))        v = "";
			else if (type.equals("DATE"))        v = "2020-01-01";
			else                                 v = 0;

			if (names.length() > 0) { names.append(","); vals.append(","); }
			names.append("[").append(colname).append("]");
			vals.append((v instanceof Integer) ? String.valueOf(v) : ("'" + v + "'"));
		}

		db.executeSQLThrow("INSERT INTO main." + table + " (" + names + ") VALUES (" + vals + ")");
	}

	private static MemoryDatabase migrated() throws Exception {
		MemoryDatabase db = createV36Database();
		new Migration_36_37(db, FSPath.Empty, "ClipCornDB", false).migrate();
		return db;
	}

	private static boolean isNotNull(MemoryDatabase db, String table) throws Exception {
		for (Object[] col : db.querySQL("PRAGMA main.table_info(" + table + ")", 6)) {
			if ("COVERID".equals(col[1])) return ((Number) col[3]).intValue() != 0;
		}
		fail("no COVERID column in " + table);
		return false;
	}

	@Test
	public void testColumnBecomesNullable() throws Exception {
		MemoryDatabase before = createV36Database();
		for (String t : List.of("MOVIES", "SERIES", "SEASONS")) assertTrue(t, isNotNull(before, t));

		MemoryDatabase db = migrated();
		for (String t : List.of("MOVIES", "SERIES", "SEASONS")) assertFalse(t, isNotNull(db, t));
	}

	@Test
	public void testNilSentinelBecomesNull() throws Exception {
		MemoryDatabase db = migrated();

		assertEquals(1, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM main.MOVIES  WHERE COVERID IS NULL", 0));
		assertEquals(1, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM main.SEASONS WHERE COVERID IS NULL", 0));

		assertEquals(MOV_WITHOUT.toString(), db.querySingleStringSQLThrow("SELECT ID FROM main.MOVIES WHERE COVERID IS NULL", 0));

		// no row keeps the sentinel
		assertEquals(0, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM main.MOVIES WHERE COVERID = '" + CCUUID.EMPTY + "'", 0));
	}

	@Test
	public void testVersionIsBumped() throws Exception {
		assertEquals("37", migrated().querySingleStringSQLThrow("SELECT IVALUE FROM main.INFO WHERE IKEY='VERSION_DB'", 0));
	}

	@Test
	public void testRealCoverIdsAreKept() throws Exception {
		MemoryDatabase db = migrated();

		assertEquals(CVR_MOV.toString(), db.querySingleStringSQLThrow("SELECT COVERID FROM main.MOVIES WHERE ID='" + MOV_WITH + "'", 0));
		assertEquals(CVR_SER.toString(), db.querySingleStringSQLThrow("SELECT COVERID FROM main.SERIES WHERE ID='" + SER + "'", 0));

		// the rest of the row is untouched
		assertEquals("WithCover", db.querySingleStringSQLThrow("SELECT NAME FROM main.MOVIES WHERE ID='" + MOV_WITH + "'", 0));
		assertEquals(2, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM main.MOVIES", 0));
	}
}
