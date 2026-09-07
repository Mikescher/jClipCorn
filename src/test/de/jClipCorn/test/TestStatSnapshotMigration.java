package de.jClipCorn.test;

import de.jClipCorn.database.driver.DatabaseStructure;
import de.jClipCorn.database.driver.MemoryDatabase;
import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.database.migration.Migration_37_38;
import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.filesystem.FSPath;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Rewinds a freshly created database to v37 (no STATSNAPSHOTS table) and runs the migration that adds it
 * and fills it retroactively.
 */
@SuppressWarnings("nls")
public class TestStatSnapshotMigration extends ClipCornBaseTest {

	private static final CCUUID MOV_A = CCUUID.generate();
	private static final CCUUID MOV_B = CCUUID.generate();

	private static MemoryDatabase createV37Database() throws Exception {
		MemoryDatabase db = new MemoryDatabase();
		assertTrue(db.createNewDatabase(FSPath.Empty, "TEST"));

		db.executeSQLThrow("DROP TABLE main.STATSNAPSHOTS");
		db.executeSQLThrow("INSERT INTO main.INFO (IKEY, IVALUE) VALUES ('VERSION_DB','37')");

		insertMovie(db, MOV_A, "MovieA", 4L * 1024 * 1024 * 1024, 120, "2020-03-01");
		insertMovie(db, MOV_B, "MovieB", 1024L * 1024 * 1024,      90, "2021-07-15");

		return db;
	}

	/** Fills every column the table declares, so the fixture does not have to spell them all out. */
	private static void insertMovie(MemoryDatabase db, CCUUID id, String name, long filesize, int length, String adddate) throws Exception {
		StringBuilder names = new StringBuilder();
		StringBuilder vals  = new StringBuilder();

		for (Object[] col : db.querySQL("PRAGMA main.table_info(MOVIES)", 6)) {
			String colname = (String) col[1];
			String type    = ((String) col[2]).toUpperCase();

			Object v;
			if (colname.equals("ID"))            v = id.toString();
			else if (colname.equals("NAME"))     v = name;
			else if (colname.equals("FILESIZE")) v = filesize;
			else if (colname.equals("LENGTH"))   v = length;
			else if (colname.equals("ADDDATE"))  v = adddate;
			else if (type.equals("TEXT"))        v = "";
			else if (type.equals("DATE"))        v = "2020-01-01";
			else                                 v = 0;

			if (names.length() > 0) { names.append(","); vals.append(","); }
			names.append("[").append(colname).append("]");
			vals.append((v instanceof String) ? ("'" + v + "'") : String.valueOf(v));
		}

		db.executeSQLThrow("INSERT INTO main.MOVIES (" + names + ") VALUES (" + vals + ")");
	}

	private static MemoryDatabase migrated() throws Exception {
		MemoryDatabase db = createV37Database();
		new Migration_37_38(db, FSPath.Empty, "ClipCornDB", false).migrate();
		return db;
	}

	private static List<Object[]> tableInfo(MemoryDatabase db) throws Exception {
		return db.querySQL("PRAGMA main.table_info(STATSNAPSHOTS)", 6);
	}

	@Test
	public void testVersionIsBumped() throws Exception {
		assertEquals("38", migrated().querySingleStringSQLThrow("SELECT IVALUE FROM main.INFO WHERE IKEY='VERSION_DB'", 0));
	}

	@Test
	public void testTableShape() throws Exception {
		List<Object[]> cols = tableInfo(migrated());

		assertEquals(DatabaseStructure.TAB_STATSNAPSHOTS.Columns.size(), cols.size());

		for (Object[] c : cols) {
			assertEquals("column " + c[1] + " must be NOT NULL", 1, ((Number) c[3]).intValue());
		}

		Object[] date = cols.stream().filter(c -> "DATE".equals(c[1])).findFirst().orElseThrow();
		// a TEXT/DATE primary key does not imply NOT NULL in SQLite, the DDL has to say so explicitly
		assertEquals(1, ((Number) date[5]).intValue());
		assertEquals(1, ((Number) date[3]).intValue());

		assertEquals(1, cols.stream().filter(c -> ((Number) c[5]).intValue() != 0).count());
	}

	/**
	 * The migration creates the table from a frozen SQL literal while a fresh database creates it from the
	 * table definition - if those two ever drift apart, an upgraded database differs from a new one.
	 */
	@Test
	public void testMigratedShapeEqualsFreshShape() throws Exception {
		MemoryDatabase fresh = new MemoryDatabase();
		assertTrue(fresh.createNewDatabase(FSPath.Empty, "TEST"));

		assertEquals(describe(tableInfo(fresh)), describe(tableInfo(migrated())));
	}

	private static List<String> describe(List<Object[]> cols) {
		List<String> res = new ArrayList<>();
		for (Object[] c : cols) res.add(c[1] + "|" + c[2] + "|" + c[3] + "|" + c[4] + "|" + c[5]);
		res.sort(String::compareTo);
		return res;
	}

	@Test
	public void testTableIsNotHistoryTracked() {
		for (Tuple3<String, String, String> t : CCDatabaseHistory.createTriggerStatements()) {
			assertFalse("STATSNAPSHOTS must not be tracked: " + t.Item1, t.Item1.contains("STATSNAPSHOTS"));
		}
	}

	/**
	 * Without a change archive every day is a back-projection from the add-dates. The newest row still has
	 * to match the actual totals, and the series has to start empty before the first element was added.
	 */
	@Test
	public void testRetroactiveFillWithoutHistory() throws Exception {
		MemoryDatabase db = migrated();

		assertTrue(db.querySingleIntSQLThrow("SELECT COUNT(*) FROM main.STATSNAPSHOTS", 0) > 0);
		assertEquals(0, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM main.STATSNAPSHOTS WHERE EXACT <> 0", 0));

		assertEquals(2,                          db.querySingleIntSQLThrow("SELECT MAX(MOV_COUNT) FROM main.STATSNAPSHOTS", 0));
		assertEquals(5L * 1024 * 1024 * 1024,    ((Number) db.querySingleSQLThrow("SELECT MAX(MOV_BYTES) FROM main.STATSNAPSHOTS", 0)).longValue());
		assertEquals(210,                        db.querySingleIntSQLThrow("SELECT MAX(MOV_MINUTES) FROM main.STATSNAPSHOTS", 0));

		// the day before the first element was added
		assertEquals("2020-02-29", db.querySingleStringSQLThrow("SELECT MIN([DATE]) FROM main.STATSNAPSHOTS", 0));
		assertEquals(0,            db.querySingleIntSQLThrow("SELECT MOV_COUNT FROM main.STATSNAPSHOTS ORDER BY [DATE] ASC LIMIT 1", 0));

		// only MovieA existed between the two add-dates - rows are sparse, so read the newest row at or
		// before that day, exactly as StatSnapshotSeries does
		assertEquals(1, valueOn(db, "2021-07-14"));
		assertEquals(2, valueOn(db, "2021-07-15"));
		assertEquals(1, valueOn(db, "2020-03-01"));
		assertEquals(0, valueOn(db, "2020-02-29"));
	}

	private static int valueOn(MemoryDatabase db, String day) throws Exception {
		return db.querySingleIntSQLThrow("SELECT MOV_COUNT FROM main.STATSNAPSHOTS WHERE [DATE] <= '" + day + "' ORDER BY [DATE] DESC LIMIT 1", 0);
	}

	@Test
	public void testHistogramsAreWritten() throws Exception {
		MemoryDatabase db = migrated();

		String json = db.querySingleStringSQLThrow("SELECT MOV_HISTOGRAMS FROM main.STATSNAPSHOTS ORDER BY [DATE] DESC LIMIT 1", 0);

		assertTrue(json, json.contains("\"FORMAT\""));
		assertTrue(json, json.contains("\"v\""));
	}

	@Test
	public void testExistingTablesAreUntouched() throws Exception {
		assertEquals(2, migrated().querySingleIntSQLThrow("SELECT COUNT(*) FROM main.MOVIES", 0));
	}
}
