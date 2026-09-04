package de.jClipCorn.database.migration;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.features.backupManager.BackupManager;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.lambda.Func4to1;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.jClipCorn.database.driver.DatabaseStructure.*;

/**
 * Migrates {@code ClipCornUserData.db}. It versions independently of the shared main database, so
 * {@link Main#USERDATA_DBVERSION} has its own sequence starting at 1.
 *
 * A new user-data schema version needs three things: a {@link UserDataMigration} subclass, its entry
 * in {@link #MIGRATION_LIST} and a bumped {@link Main#USERDATA_DBVERSION}.
 */
public class UserDataDatabaseMigrator {

	private static final List<Func4to1<GenericDatabase, FSPath, String, Boolean, DBMigration>> MIGRATION_LIST = List.of(
			// UserDataMigration_01_02::new
	);

	private final List<DBMigration> migrations;

	private final GenericDatabase db;
	private final FSPath dbpath;
	private final boolean readonly;

	private final List<UpgradeAction> afterConnectActions = new ArrayList<>();

	public UserDataDatabaseMigrator(GenericDatabase db, FSPath dbpath, String dbName, boolean readonly) {
		super();

		this.db       = db;
		this.dbpath   = dbpath;
		this.readonly = readonly;

		this.migrations = MIGRATION_LIST.stream().map(p -> p.invoke(db, dbpath, dbName, readonly)).collect(Collectors.toList());
	}

	/** @return null when there is no user database yet - {@code ensureUserDataDatabase} creates it at the current version */
	@SuppressWarnings("nls")
	private String getDBVersion() throws SQLException {
		var infoExists = db.querySingleIntSQLThrow(String.format("SELECT COUNT(*) FROM %s.sqlite_master WHERE type='table' AND name='%s'",
				SCHEMA_USERDATA,
				TAB_UD_INFO.Name), 0);
		if (infoExists == 0) return null;

		return db.querySingleStringSQLThrow(String.format("SELECT %s FROM %s WHERE %s = '%s'",
				COL_INFO_VALUE.Name,
				TAB_UD_INFO.qualifiedName(),
				COL_INFO_KEY.Name,
				INFOKEY_DBVERSION.Key), 0);
	}

	private void setDBVersion(String version) throws SQLException {
		db.executeSQLThrow(String.format("UPDATE %s SET %s='%s' WHERE %s='%s'",  //$NON-NLS-1$
				TAB_UD_INFO.qualifiedName(),
				COL_INFO_VALUE.Name,
				version,
				COL_INFO_KEY.Name,
				INFOKEY_DBVERSION.Key));
	}

	private static boolean isNewerThanApplication(String version) {
		try {
			return Integer.parseInt(version.trim()) > Integer.parseInt(Main.USERDATA_DBVERSION);
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * @param referror receives a localized, actionable message when the user database cannot be
	 *                 brought to {@link Main#USERDATA_DBVERSION}
	 */
	@SuppressWarnings("nls")
	public boolean tryUpgrade(RefParam<String> referror) {
		try {
			String version = getDBVersion();

			if (version == null) return true;
			if (version.equals(Main.USERDATA_DBVERSION)) return true;

			if (isNewerThanApplication(version)) {
				referror.Value = LocaleBundle.getFormattedString("LogMessage.UserDataDatabaseTooNew", version, Main.USERDATA_DBVERSION);
				return false;
			}

			CCLog.addInformation("Migrate user-data database from " + version + " to " + Main.USERDATA_DBVERSION);
			CCLog.addInformation(LocaleBundle.getString("LogMessage.DatabaseUpgradeStarted"));

			if (readonly) {
				CCLog.addInformation(LocaleBundle.getString("LogMessage.MigrationFailedDueToReadOnly"));
				return true;
			}

			if (!db.isInMemory()) BackupManager.createMigrationBackup("userdata-" + version, dbpath);

			var restoreTrigger = false;

			while (!version.equals(Main.USERDATA_DBVERSION)) {
				var fromVersion = version;
				var migration = migrations.stream().filter(m -> m.getFromVersion().equals(fromVersion)).findFirst();
				if (migration.isEmpty()) throw new Exception("no migration found to migrate from userdata-db-version " + fromVersion);

				this.afterConnectActions.addAll(migration.get().migrate());

				if (migration.get().backupAndRestoreTrigger()) {
					restoreTrigger = true;
				}

				setDBVersion(version = migration.get().getToVersion());
			}

			if (restoreTrigger) {
				if ("1".equals(db.querySingleStringSQL("SELECT IVALUE FROM userdata.INFO WHERE IKEY = 'HISTORY_ENABLED'", 0)))
				{
					for (var trigger : CCDatabaseHistory.createTriggerStatements()) db.executeSQLThrow(trigger.Item2);
				}
			}

			if (! Main.USERDATA_DBVERSION.equals(getDBVersion())) {
				throw new Exception("version mismatch after migration");
			}

			CCLog.addInformation(LocaleBundle.getString("LogMessage.DatabaseUpgradeSucess"));

			return true;

		} catch (Exception e) {
			referror.Value = LocaleBundle.getString("LogMessage.DatabaseUpgradeFailed") + "\n" + e;
			return false;
		}
	}

	public void onAfterConnect(CCMovieList ml, CCDatabase db) {
		for (UpgradeAction action : afterConnectActions) {
			action.onAfterConnect(ml, db);
		}
	}
}
