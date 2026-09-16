package de.jClipCorn.test;

import de.jClipCorn.Main;
import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.database.driver.MemoryDatabase;
import de.jClipCorn.database.migration.UpgradeAction;
import de.jClipCorn.database.migration.UserDataDatabaseMigrator;
import de.jClipCorn.database.migration.UserDataMigration;
import de.jClipCorn.database.migration.UserDataMigration_01_02;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import org.json.JSONArray;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Drives the migrator itself: the two paths that do nothing, the two that refuse, and a throwaway
 * {@link UserDataMigration} for the shared machinery. The real migrations are tested further down.
 */
@SuppressWarnings("nls")
public class TestUserDataMigration extends ClipCornBaseTest {

	private static MemoryDatabase createDatabase(String userDataVersion) throws Exception {
		MemoryDatabase db = new MemoryDatabase();
		assertTrue(db.createNewDatabase(FSPath.Empty, "TEST"));

		db.executeSQLThrow("INSERT INTO main.INFO (IKEY, IVALUE) VALUES ('VERSION_DB','" + Main.DBVERSION + "')");
		db.executeSQLThrow("INSERT INTO userdata.INFO (IKEY, IVALUE) VALUES ('VERSION_DB','" + userDataVersion + "'), ('HISTORY_ENABLED','0')");

		return db;
	}

	private static UserDataDatabaseMigrator migrator(MemoryDatabase db) {
		return new UserDataDatabaseMigrator(db, FSPath.Empty, "ClipCornDB", false);
	}

	private static String userDataVersion(MemoryDatabase db) throws Exception {
		return db.querySingleStringSQLThrow("SELECT IVALUE FROM userdata.INFO WHERE IKEY='VERSION_DB'", 0);
	}

	@Test
	public void testCurrentVersionIsANoOp() throws Exception {
		MemoryDatabase db = createDatabase(Main.USERDATA_DBVERSION);

		var referror = new RefParam<String>();
		assertTrue(migrator(db).tryUpgrade(referror));
		assertNull(referror.Value);
		assertEquals(Main.USERDATA_DBVERSION, userDataVersion(db));
	}

	@Test
	public void testMissingUserDatabaseIsIgnored() throws Exception {
		MemoryDatabase db = createDatabase(Main.USERDATA_DBVERSION);
		db.executeSQLThrow("DROP TABLE userdata.INFO");

		var referror = new RefParam<String>();
		assertTrue(migrator(db).tryUpgrade(referror));
		assertNull(referror.Value);
	}

	@Test
	public void testNewerUserDatabaseIsRefused() throws Exception {
		MemoryDatabase db = createDatabase("99");

		var referror = new RefParam<String>();
		assertFalse(migrator(db).tryUpgrade(referror));
		assertNotNull(referror.Value);
		assertTrue(referror.Value, referror.Value.contains("99"));
		assertEquals("99", userDataVersion(db));
	}

	/** Nothing leads out of a version the (empty) chain does not cover. */
	@Test
	public void testUnreachableVersionIsRefused() throws Exception {
		MemoryDatabase db = createDatabase("0");

		var referror = new RefParam<String>();
		assertFalse(migrator(db).tryUpgrade(referror));
		assertNotNull(referror.Value);
		assertEquals("0", userDataVersion(db));
	}

	@Test
	public void testMigrationBumpsOnlyTheUserDataVersion() throws Exception {
		MemoryDatabase db = createDatabase("1");

		new DummyMigration(db, false).migrate();

		assertEquals("2", userDataVersion(db));
		assertEquals(Main.DBVERSION, db.querySingleStringSQLThrow("SELECT IVALUE FROM main.INFO WHERE IKEY='VERSION_DB'", 0));
		assertEquals(1, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM userdata.sqlite_master WHERE name='DUMMY'", 0));
	}

	@Test
	public void testFailedMigrationRollsBack() throws Exception {
		MemoryDatabase db = createDatabase("1");

		try {
			new DummyMigration(db, true).migrate();
			fail("migration did not throw");
		} catch (Exception e) {
			// expected
		}

		assertEquals("1", userDataVersion(db));
		assertEquals(0, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM userdata.sqlite_master WHERE name='DUMMY'", 0));
	}

	private static String legacyVar(String host, String key, String value) {
		return Str.toBase64(host) + ";" + Str.toBase64(key) + ";" + Str.toBase64(value);
	}

	private static void insertProperty(MemoryDatabase db, String key, String value) throws Exception {
		db.executeSQLThrow("INSERT INTO userdata.PROPERTIES (PKEY, PVALUE, LAST_CHANGED) VALUES ('" + key + "', '" + value + "', '2020-01-01 00:00:00')");
	}

	private static String property(MemoryDatabase db, String key) throws Exception {
		return db.querySingleStringSQLThrow("SELECT PVALUE FROM userdata.PROPERTIES WHERE PKEY='" + key + "'", 0);
	}

	@Test
	public void testMigration_01_02_MergesPathVariables() throws Exception {
		MemoryDatabase db = createDatabase("1");

		insertProperty(db, "PROP_PATHSYNTAX_VAR1",  legacyVar("", "mov", "/data/mov"));
		insertProperty(db, "PROP_PATHSYNTAX_VAR2",  ";;");
		insertProperty(db, "PROP_PATHSYNTAX_VAR7",  Str.toBase64("ser") + ";" + Str.toBase64("/data/ser"));
		insertProperty(db, "PROP_PATHSYNTAX_VAR8",  "garbage");
		insertProperty(db, "PROP_PATHSYNTAX_VAR10", legacyVar("myhost", "k'e\"y", "/da;ta/x"));
		insertProperty(db, "PROP_PATHSYNTAX_SELF",  "true");

		new UserDataMigration_01_02(db, FSPath.Empty, "ClipCornDB", false).migrate();

		assertEquals("2", userDataVersion(db));

		var jarr = new JSONArray(property(db, "PROP_PATHSYNTAX_VARIABLES"));
		assertEquals(3, jarr.length());
		assertEquals("",          jarr.getJSONObject(0).getString("host"));
		assertEquals("mov",       jarr.getJSONObject(0).getString("key"));
		assertEquals("/data/mov", jarr.getJSONObject(0).getString("value"));
		assertEquals("",          jarr.getJSONObject(1).getString("host"));
		assertEquals("ser",       jarr.getJSONObject(1).getString("key"));
		assertEquals("myhost",    jarr.getJSONObject(2).getString("host"));
		assertEquals("k'e\"y",   jarr.getJSONObject(2).getString("key"));
		assertEquals("/da;ta/x",  jarr.getJSONObject(2).getString("value"));

		assertEquals(0, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM userdata.PROPERTIES WHERE PKEY LIKE 'PROP_PATHSYNTAX_VAR_%' AND PKEY <> 'PROP_PATHSYNTAX_VARIABLES'", 0));
		assertEquals("true", property(db, "PROP_PATHSYNTAX_SELF"));

		var props = CCProperties.createInMemory();
		props.setProperty("PROP_PATHSYNTAX_VARIABLES", property(db, "PROP_PATHSYNTAX_VARIABLES"));
		var vars = props.PROP_PATHSYNTAX_VARIABLES.getValue().Values;
		assertEquals(3, vars.size());
		assertEquals(CCPath.create("/data/mov"), vars.get(0).Value);
		assertEquals("myhost", vars.get(2).Hostname);
	}

	@Test
	public void testMigration_01_02_WithoutPathVariables() throws Exception {
		MemoryDatabase db = createDatabase("1");

		insertProperty(db, "PROP_PATHSYNTAX_SELF", "true");

		new UserDataMigration_01_02(db, FSPath.Empty, "ClipCornDB", false).migrate();

		assertEquals("2", userDataVersion(db));
		assertNull(property(db, "PROP_PATHSYNTAX_VARIABLES"));
		assertEquals("true", property(db, "PROP_PATHSYNTAX_SELF"));
	}

	@Test
	public void testChainUpgradesVersion1() throws Exception {
		MemoryDatabase db = createDatabase("1");

		var referror = new RefParam<String>();
		assertTrue(migrator(db).tryUpgrade(referror));
		assertNull(referror.Value);
		assertEquals(Main.USERDATA_DBVERSION, userDataVersion(db));
	}

	private static class DummyMigration extends UserDataMigration {
		private final boolean fail;

		DummyMigration(GenericDatabase db, boolean fail) {
			super(db, FSPath.Empty, "ClipCornDB", false);
			this.fail = fail;
		}

		@Override public String getFromVersion() { return "1"; }
		@Override public String getToVersion()   { return "2"; }

		@Override
		protected List<UpgradeAction> run() throws Exception {
			db.executeSQLThrow("CREATE TABLE userdata.DUMMY ([ID] INTEGER NOT NULL PRIMARY KEY)");
			if (fail) throw new Exception("migration failed");
			return new ArrayList<>();
		}
	}
}
