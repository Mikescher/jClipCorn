package de.jClipCorn.test;

import de.jClipCorn.Main;
import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.database.driver.MemoryDatabase;
import de.jClipCorn.database.migration.UpgradeAction;
import de.jClipCorn.database.migration.UserDataDatabaseMigrator;
import de.jClipCorn.database.migration.UserDataMigration;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.filesystem.FSPath;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * The user-database migration chain is still empty, so these drive the migrator itself: the two
 * paths that do nothing, the two that refuse, and a throwaway {@link UserDataMigration} for the
 * machinery the first real one will use.
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
