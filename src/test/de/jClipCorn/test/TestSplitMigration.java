package de.jClipCorn.test;

import de.jClipCorn.Main;
import de.jClipCorn.database.driver.MemoryDatabase;
import de.jClipCorn.database.migration.Migration_35_36;
import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.filesystem.FSPath;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Rewinds a freshly created (v36) in-memory database to the v35 layout and runs the split
 * migration on it.
 */
@SuppressWarnings("nls")
public class TestSplitMigration extends ClipCornBaseTest {

	private static final CCUUID MOV_A = CCUUID.generate();
	private static final CCUUID MOV_B = CCUUID.generate();
	private static final CCUUID SER_A = CCUUID.generate();

	private static MemoryDatabase createV35Database() throws Exception {
		MemoryDatabase db = new MemoryDatabase();
		assertTrue(db.createNewDatabase(FSPath.Empty, "TEST"));

		for (String t : List.of("MOVIES", "SERIES", "SEASONS", "EPISODES", "INFO", "TEMP", "HISTORY", "FILTERS", "PROPERTIES")) {
			db.executeSQLThrow("DROP TABLE userdata." + t);
		}

		for (String t : List.of("MOVIES", "EPISODES")) {
			db.executeSQLThrow("ALTER TABLE main." + t + " ADD COLUMN [VIEWED_HISTORY] TEXT NOT NULL DEFAULT '[]'");
		}
		for (String t : List.of("MOVIES", "SERIES", "EPISODES")) {
			db.executeSQLThrow("ALTER TABLE main." + t + " ADD COLUMN [TAGS] TEXT NOT NULL DEFAULT '[]'");
		}
		for (String t : List.of("MOVIES", "SERIES", "SEASONS", "EPISODES")) {
			db.executeSQLThrow("ALTER TABLE main." + t + " ADD COLUMN [SCORE] TINYINT NOT NULL DEFAULT 6");
			db.executeSQLThrow("ALTER TABLE main." + t + " ADD COLUMN [SCORECOMMENT] TEXT NOT NULL DEFAULT ''");
		}

		db.executeSQLThrow("CREATE TABLE main.FILTERS ([ID] INTEGER PRIMARY KEY, [SORT] INTEGER NOT NULL, [NAME] TEXT NOT NULL, [DEFINITION] TEXT NOT NULL)");
		db.executeSQLThrow("CREATE TABLE main.PROPERTIES ([PKEY] TEXT PRIMARY KEY, [PVALUE] TEXT NOT NULL, [LAST_CHANGED] TEXT NOT NULL)");

		db.executeSQLThrow("INSERT INTO main.INFO (IKEY, IVALUE) VALUES " +
				"('VERSION_DB','35'), ('DATABASE_UNIVERSALLY_UNIQUE_IDENTIFIER','5cb2e19c-6d1b-4d78-9dd6-2c3d78d5a3aa'), ('HISTORY_ENABLED','1')");

		insertRow(db, "MOVIES", Map.of("ID", MOV_A.toString(), "NAME", "Rated",     "SCORE", 3, "SCORECOMMENT", "nice"));
		insertRow(db, "MOVIES", Map.of("ID", MOV_B.toString(), "NAME", "Untouched"));
		insertRow(db, "SERIES", Map.of("ID", SER_A.toString(), "NAME", "Series",    "TAGS", "[1]"));

		db.executeSQLThrow("INSERT INTO main.FILTERS VALUES (1, 0, 'Filter', 'def')");
		db.executeSQLThrow("INSERT INTO main.PROPERTIES VALUES ('PROP_X', 'y', '2020-01-01 00:00:00')");

		return db;
	}

	/** Fills every column the v35 schema declares, so the fixture does not have to spell them all out. */
	private static void insertRow(MemoryDatabase db, String table, Map<String, Object> values) throws Exception {
		StringBuilder names = new StringBuilder();
		StringBuilder vals  = new StringBuilder();

		for (Object[] col : db.querySQL("PRAGMA main.table_info(" + table + ")", 6)) {
			String name = (String) col[1];
			String type = ((String) col[2]).toUpperCase();

			Object v = values.get(name);
			if (v == null) {
				if (name.equals("SCORE"))              v = 6;
				else if (name.endsWith("COVERID"))     v = CCUUID.EMPTY.toString();
				else if (name.endsWith("HISTORY") || name.equals("TAGS") || name.equals("GENRE") ||
				         name.equals("LANGUAGE") || name.equals("SUBTITLES") || name.equals("ONLINEREF") ||
				         name.equals("PARTS"))         v = "[]";
				else if (type.equals("TEXT"))          v = "";
				else if (type.equals("DATE"))          v = "1900-01-01";
				else                                   v = 0;
			}

			if (names.length() > 0) { names.append(","); vals.append(","); }
			names.append("[").append(name).append("]");
			vals.append((v instanceof Integer) ? String.valueOf(v) : ("'" + v + "'"));
		}

		db.executeSQLThrow("INSERT INTO main." + table + " (" + names + ") VALUES (" + vals + ")");
	}

	private static MemoryDatabase migrated() throws Exception {
		MemoryDatabase db = createV35Database();
		new Migration_35_36(db, FSPath.Empty, "ClipCornDB", false).migrate();
		return db;
	}

	@Test
	public void testUserRowsAreMovedSparsely() throws Exception {
		MemoryDatabase db = migrated();

		// only the rows that carry a non-default value are moved
		assertEquals(1, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM userdata.MOVIES", 0));
		assertEquals(MOV_A.toString(), db.querySingleStringSQLThrow("SELECT ID FROM userdata.MOVIES", 0));
		assertEquals(3, db.querySingleIntSQLThrow("SELECT SCORE FROM userdata.MOVIES", 0));
		assertEquals("nice", db.querySingleStringSQLThrow("SELECT SCORECOMMENT FROM userdata.MOVIES", 0));

		// a non-default TAGS value is enough on its own
		assertEquals(1, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM userdata.SERIES", 0));
		assertEquals("[1]", db.querySingleStringSQLThrow("SELECT TAGS FROM userdata.SERIES", 0));

		assertEquals(0, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM userdata.SEASONS", 0));
		assertEquals(0, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM userdata.EPISODES", 0));
	}

	@Test
	public void testUserColumnsAreGoneFromMain() throws Exception {
		MemoryDatabase db = migrated();

		for (String t : List.of("MOVIES", "SERIES", "SEASONS", "EPISODES")) {
			var cols = db.querySQL("PRAGMA main.table_info(" + t + ")", 6);
			for (Object[] c : cols) {
				assertFalse(t + "." + c[1], List.of("VIEWED_HISTORY", "TAGS", "SCORE", "SCORECOMMENT").contains(c[1]));
			}
		}

		// the shared rows themselves are untouched
		assertEquals(2, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM main.MOVIES", 0));
		assertEquals("Rated", db.querySingleStringSQLThrow("SELECT NAME FROM main.MOVIES WHERE ID='" + MOV_A + "'", 0));
	}

	@Test
	public void testFiltersAndPropertiesMoveToUserData() throws Exception {
		MemoryDatabase db = migrated();

		assertEquals("Filter", db.querySingleStringSQLThrow("SELECT NAME FROM userdata.FILTERS", 0));
		assertEquals("y", db.querySingleStringSQLThrow("SELECT PVALUE FROM userdata.PROPERTIES WHERE PKEY='PROP_X'", 0));

		var mainTables = db.querySQL("SELECT name FROM main.sqlite_master WHERE type='table'", 1, a -> (String) a[0]);
		assertFalse(mainTables.contains("FILTERS"));
		assertFalse(mainTables.contains("PROPERTIES"));
	}

	@Test
	public void testInfoIsReorganised() throws Exception {
		MemoryDatabase db = migrated();

		assertEquals("36", db.querySingleStringSQLThrow("SELECT IVALUE FROM main.INFO WHERE IKEY='VERSION_DB'", 0));
		assertEquals(Main.USERDATA_DBVERSION, db.querySingleStringSQLThrow("SELECT IVALUE FROM userdata.INFO WHERE IKEY='VERSION_DB'", 0));

		// the user database is bound to the main database it was split off from
		assertEquals("5cb2e19c-6d1b-4d78-9dd6-2c3d78d5a3aa", db.querySingleStringSQLThrow("SELECT IVALUE FROM userdata.INFO WHERE IKEY='MAINDB_DUUID'", 0));
		assertNotEquals("5cb2e19c-6d1b-4d78-9dd6-2c3d78d5a3aa", db.querySingleStringSQLThrow("SELECT IVALUE FROM userdata.INFO WHERE IKEY='DATABASE_UNIVERSALLY_UNIQUE_IDENTIFIER'", 0));

		// HISTORY_ENABLED is per user and moves out of the shared file
		assertEquals("1", db.querySingleStringSQLThrow("SELECT IVALUE FROM userdata.INFO WHERE IKEY='HISTORY_ENABLED'", 0));
		assertEquals(0, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM main.INFO WHERE IKEY='HISTORY_ENABLED'", 0));
	}
}
