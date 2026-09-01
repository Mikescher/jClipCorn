package de.jClipCorn.test;

import de.jClipCorn.database.driver.MemoryDatabase;
import de.jClipCorn.database.migration.Migration_34_35;
import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.filesystem.FSPath;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Builds a (reduced) v34 database, runs the UUID migration on it and checks the result.
 * The migration derives the new table layout from PRAGMA table_info, so leaving out the columns
 * that are just copied through does not change what is exercised here.
 */
@SuppressWarnings("nls")
public class TestUUIDMigration extends ClipCornBaseTest {

	private static CCUUID uuid(String table, int oldId) {
		return CCUUID.deriveMigrationUUID(table, oldId);
	}

	private static MemoryDatabase createV34Database() throws Exception {
		MemoryDatabase db = new MemoryDatabase();
		assertTrue(db.createNewDatabase(FSPath.Empty, "TEST"));

		for (String t : List.of("MOVIES", "SERIES", "SEASONS", "EPISODES", "COVERS")) db.executeSQLThrow("DROP TABLE " + t);

		db.executeSQLThrow("CREATE TABLE MOVIES   (LOCALID INTEGER PRIMARY KEY, NAME TEXT NOT NULL, COVERID INTEGER NOT NULL)");
		db.executeSQLThrow("CREATE TABLE SERIES   (LOCALID INTEGER PRIMARY KEY, NAME TEXT NOT NULL, COVERID INTEGER NOT NULL)");
		db.executeSQLThrow("CREATE TABLE SEASONS  (LOCALID INTEGER PRIMARY KEY, SERIESID INTEGER NOT NULL, NAME TEXT NOT NULL, COVERID INTEGER NOT NULL, FOREIGN KEY(SERIESID) REFERENCES SERIES(LOCALID))");
		db.executeSQLThrow("CREATE TABLE EPISODES (LOCALID INTEGER PRIMARY KEY, SEASONID INTEGER NOT NULL, NAME TEXT NOT NULL, EPISODE SMALLINT NOT NULL, FOREIGN KEY(SEASONID) REFERENCES SEASONS(LOCALID))");
		db.executeSQLThrow("CREATE TABLE COVERS   (ID INTEGER PRIMARY KEY, FILENAME TEXT NOT NULL, WIDTH INTEGER NOT NULL)");

		db.executeSQLThrow("INSERT INTO INFO (IKEY, IVALUE) VALUES ('VERSION_DB','34'),('LAST_ID','7'),('LAST_COVERID','2')");

		db.executeSQLThrow("INSERT INTO MOVIES   VALUES (1, 'Movie A', 0), (2, 'Movie B', -1), (3, 'Movie C', 99)");
		db.executeSQLThrow("INSERT INTO SERIES   VALUES (4, 'Series A', 1)");
		db.executeSQLThrow("INSERT INTO SEASONS  VALUES (5, 4, 'Season A', 2)");
		db.executeSQLThrow("INSERT INTO EPISODES VALUES (6, 5, 'Episode A', 1), (7, 5, 'Episode B', 2)");
		db.executeSQLThrow("INSERT INTO COVERS   VALUES (0, 'cover_00000.png', 100), (1, 'cover_00001.png', 100), (2, 'cover_00002.jpg', 100)");

		return db;
	}

	private static void runMigration(MemoryDatabase db, FSPath dbDir) throws Exception {
		new Migration_34_35(db, dbDir, "ClipCornDB", false).migrate();
	}

	@Test
	public void testIdsAreDerivedDeterministically() throws Exception {
		MemoryDatabase db = createV34Database();
		runMigration(db, FSPath.Empty);

		assertEquals(uuid("MOVIES", 1).toString(), db.querySingleStringSQLThrow("SELECT ID FROM MOVIES WHERE NAME='Movie A'", 0));
		assertEquals(uuid("MOVIES", 3).toString(), db.querySingleStringSQLThrow("SELECT ID FROM MOVIES WHERE NAME='Movie C'", 0));
		assertEquals(uuid("SERIES", 4).toString(), db.querySingleStringSQLThrow("SELECT ID FROM SERIES", 0));
		assertEquals(uuid("SEASONS", 5).toString(), db.querySingleStringSQLThrow("SELECT ID FROM SEASONS", 0));
		assertEquals(uuid("EPISODES", 6).toString(), db.querySingleStringSQLThrow("SELECT ID FROM EPISODES WHERE EPISODE=1", 0));
		assertEquals(uuid("COVERS", 2).toString(), db.querySingleStringSQLThrow("SELECT ID FROM COVERS WHERE WIDTH=100 AND FILENAME LIKE '%.jpg'", 0));

		assertEquals("35", db.querySingleStringSQLThrow("SELECT IVALUE FROM INFO WHERE IKEY='VERSION_DB'", 0));
	}

	@Test
	public void testReferencesAreRewritten() throws Exception {
		MemoryDatabase db = createV34Database();
		runMigration(db, FSPath.Empty);

		assertEquals(uuid("SERIES", 4).toString(), db.querySingleStringSQLThrow("SELECT SERIESID FROM SEASONS", 0));
		assertEquals(uuid("SEASONS", 5).toString(), db.querySingleStringSQLThrow("SELECT SEASONID FROM EPISODES WHERE EPISODE=1", 0));

		assertEquals(uuid("COVERS", 0).toString(), db.querySingleStringSQLThrow("SELECT COVERID FROM MOVIES WHERE NAME='Movie A'", 0));
		assertEquals(uuid("COVERS", 1).toString(), db.querySingleStringSQLThrow("SELECT COVERID FROM SERIES", 0));
		assertEquals(uuid("COVERS", 2).toString(), db.querySingleStringSQLThrow("SELECT COVERID FROM SEASONS", 0));

		// -1 was the "no cover" sentinel
		assertEquals(CCUUID.EMPTY.toString(), db.querySingleStringSQLThrow("SELECT COVERID FROM MOVIES WHERE NAME='Movie B'", 0));

		// a dangling reference stays dangling instead of silently becoming "no cover"
		assertEquals(uuid("COVERS", 99).toString(), db.querySingleStringSQLThrow("SELECT COVERID FROM MOVIES WHERE NAME='Movie C'", 0));
	}

	@Test
	public void testSchemaAfterMigration() throws Exception {
		MemoryDatabase db = createV34Database();
		runMigration(db, FSPath.Empty);

		for (String t : List.of("MOVIES", "SERIES", "SEASONS", "EPISODES", "COVERS")) {
			List<Object[]> cols = db.querySQL("PRAGMA table_info(" + t + ")", 6);

			Object[] pk = cols.stream().filter(c -> ((Number) c[5]).intValue() == 1).findFirst().orElse(null);
			assertNotNull(t + " has no primary key", pk);
			assertEquals(t + ".ID", "ID", pk[1]);
			assertEquals(t + ".ID type", "TEXT", pk[2]);

			assertFalse(t + " still has LOCALID", cols.stream().anyMatch(c -> "LOCALID".equals(c[1])));
		}

		assertTrue(db.listTables().stream().noneMatch(t -> t.startsWith("MIGRATION_")));

		// the id counters are gone
		assertEquals(0, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM INFO WHERE IKEY IN ('LAST_ID','LAST_COVERID')", 0));
	}

	@Test
	public void testHistoryIsRewritten() throws Exception {
		MemoryDatabase db = createV34Database();

		db.executeSQLThrow("INSERT INTO HISTORY ([TABLE],[ID],[DATE],[ACTION],[FIELD],[OLD],[NEW]) VALUES " +
				"('MOVIES',   '1', '2020-01-01', 'UPDATE', 'NAME',     'x',   'y'),   " +
				"('SEASONS',  '5', '2020-01-01', 'UPDATE', 'SERIESID', '4',   '4'),   " +
				"('EPISODES', '6', '2020-01-01', 'UPDATE', 'SEASONID', '5',   '5'),   " +
				"('MOVIES',   '2', '2020-01-01', 'UPDATE', 'COVERID',  '-1',  '0'),   " +
				"('MOVIES', '404', '2020-01-01', 'DELETE', 'NAME',     'gone', NULL), " +
				"('GROUPS',  'Grp', '2020-01-01', 'UPDATE', 'ORDERING', '1',  '2'),   " +
				"('INFO', 'LAST_ID', '2020-01-01', 'UPDATE', 'IVALUE',  '6',  '7'),   " +
				"('INFO', 'LAST_COVERID', '2020-01-01', 'UPDATE', 'IVALUE', '1', '2')");

		runMigration(db, FSPath.Empty);

		assertEquals(uuid("MOVIES", 1).toString(), db.querySingleStringSQLThrow("SELECT [ID] FROM HISTORY WHERE [FIELD]='NAME' AND [NEW]='y'", 0));

		assertEquals(uuid("SERIES", 4).toString(), db.querySingleStringSQLThrow("SELECT [OLD] FROM HISTORY WHERE [FIELD]='SERIESID'", 0));
		assertEquals(uuid("SEASONS", 5).toString(), db.querySingleStringSQLThrow("SELECT [NEW] FROM HISTORY WHERE [FIELD]='SEASONID'", 0));

		assertEquals(CCUUID.EMPTY.toString(), db.querySingleStringSQLThrow("SELECT [OLD] FROM HISTORY WHERE [FIELD]='COVERID'", 0));
		assertEquals(uuid("COVERS", 0).toString(), db.querySingleStringSQLThrow("SELECT [NEW] FROM HISTORY WHERE [FIELD]='COVERID'", 0));

		// a row of an entity that no longer exists is mapped too - the derivation is a pure function
		assertEquals(uuid("MOVIES", 404).toString(), db.querySingleStringSQLThrow("SELECT [ID] FROM HISTORY WHERE [OLD]='gone'", 0));

		// non-entity primary keys are untouched
		assertEquals("Grp", db.querySingleStringSQLThrow("SELECT [ID] FROM HISTORY WHERE [TABLE]='GROUPS'", 0));

		// the counter history is dropped
		assertEquals(0, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM HISTORY WHERE [TABLE]='INFO'", 0));
	}

	@Test
	public void testCoverFilesAreRenamed() throws Exception {
		MemoryDatabase db = createV34Database();

		FSPath dbDir = createAutocleanedDir("uuidmigration");
		FSPath coverDir = dbDir.append("ClipCornDB", "cover");
		coverDir.mkdirsWithException();

		coverDir.append("cover_00000.png").writeAsUTF8TextFile("a");
		coverDir.append("cover_00001.png").writeAsUTF8TextFile("b");
		// cover_00002.jpg is intentionally missing on disk

		runMigration(db, dbDir);

		assertTrue(coverDir.append(uuid("COVERS", 0) + ".png").fileExists());
		assertTrue(coverDir.append(uuid("COVERS", 1) + ".png").fileExists());
		assertFalse(coverDir.append("cover_00000.png").fileExists());

		assertEquals(uuid("COVERS", 0) + ".png", db.querySingleStringSQLThrow("SELECT FILENAME FROM COVERS WHERE ID='" + uuid("COVERS", 0) + "'", 0));
		assertEquals(uuid("COVERS", 2) + ".jpg", db.querySingleStringSQLThrow("SELECT FILENAME FROM COVERS WHERE ID='" + uuid("COVERS", 2) + "'", 0));
	}

	@Test
	public void testCoverRenameIsResumable() throws Exception {
		MemoryDatabase db = createV34Database();

		FSPath dbDir = createAutocleanedDir("uuidmigration_resume");
		FSPath coverDir = dbDir.append("ClipCornDB", "cover");
		coverDir.mkdirsWithException();

		// as if a previous (aborted) run had already renamed this one
		coverDir.append(uuid("COVERS", 0) + ".png").writeAsUTF8TextFile("a");
		coverDir.append("cover_00001.png").writeAsUTF8TextFile("b");

		runMigration(db, dbDir);

		assertEquals("a", coverDir.append(uuid("COVERS", 0) + ".png").readAsUTF8TextFile());
		assertEquals("b", coverDir.append(uuid("COVERS", 1) + ".png").readAsUTF8TextFile());
	}
}
