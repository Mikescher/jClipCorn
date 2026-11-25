package de.jClipCorn.test;

import de.jClipCorn.database.driver.MemoryDatabase;
import de.jClipCorn.database.migration.Migration_38_39;
import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.filesystem.FSPath;
import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

/**
 * Rewinds a freshly created database to v38 (no SERIES.SHOWSTATUS) and runs the migration that adds it.
 */
@SuppressWarnings("nls")
public class TestShowStatusMigration extends ClipCornBaseTest {

	private static final CCUUID SER = CCUUID.generate();

	private static MemoryDatabase createV38Database() throws Exception {
		MemoryDatabase db = new MemoryDatabase();
		assertTrue(db.createNewDatabase(FSPath.Empty, "TEST"));

		db.executeSQLThrow("ALTER TABLE main.SERIES DROP COLUMN SHOWSTATUS");
		db.executeSQLThrow("INSERT INTO main.INFO (IKEY, IVALUE) VALUES ('VERSION_DB','38')");

		db.executeSQLThrow("INSERT INTO main.SERIES (ID, NAME, GENRE, ONLINESCORE_NUM, ONLINESCORE_DENOM, FSK, ONLINEREF, GROUPS, COVERID, SPECIALVERSION) " +
				"VALUES ('" + SER + "', 'Series', '[]', 0, 0, 0, '[]', '[]', NULL, '[]')");

		return db;
	}

	private static Map<String, String> columns(MemoryDatabase db) throws Exception {
		Map<String, String> r = new TreeMap<>();
		for (Object[] col : db.querySQL("PRAGMA main.table_info(SERIES)", 6)) {
			r.put((String) col[1], ((String) col[2]).toUpperCase() + (((Number) col[3]).intValue() != 0 ? " NOT NULL" : ""));
		}
		return r;
	}

	@Test
	public void testMigratedSchemaEqualsFreshSchema() throws Exception {
		MemoryDatabase fresh = new MemoryDatabase();
		assertTrue(fresh.createNewDatabase(FSPath.Empty, "TEST"));

		MemoryDatabase db = createV38Database();
		assertFalse(columns(db).containsKey("SHOWSTATUS"));

		new Migration_38_39(db, FSPath.Empty, "ClipCornDB", false).migrate();

		assertEquals(columns(fresh), columns(db));
	}

	@Test
	public void testExistingSeriesBecomeUnknown() throws Exception {
		MemoryDatabase db = createV38Database();
		new Migration_38_39(db, FSPath.Empty, "ClipCornDB", false).migrate();

		assertEquals(0, db.querySingleIntSQLThrow("SELECT SHOWSTATUS FROM main.SERIES WHERE ID='" + SER + "'", 0));
		assertEquals("39", db.querySingleStringSQLThrow("SELECT IVALUE FROM main.INFO WHERE IKEY='VERSION_DB'", 0));
	}
}
