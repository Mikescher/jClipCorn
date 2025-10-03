package de.jClipCorn.database.migration;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.backupManager.BackupManager;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.lambda.Func5to1;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.jClipCorn.database.driver.DatabaseStructure.*;

public class DatabaseMigrator {
	private static final List<Func5to1<GenericDatabase, CCProperties,FSPath, String, Boolean, DBMigration>> MIGRRATION_LIST = List.of(
			Migration_06_07::new,
			Migration_07_08::new,
			Migration_08_09::new,
			Migration_09_10::new,
			Migration_10_11::new,
			Migration_11_12::new,
			Migration_12_13::new,
			Migration_13_14::new,
			Migration_14_15::new,
			Migration_15_16::new,
			Migration_16_17::new,
			Migration_17_18::new,
			Migration_18_19::new,
			Migration_19_20::new,
			Migration_20_21::new,
			Migration_21_22::new,
			Migration_22_23::new,
			Migration_23_24::new,
			Migration_24_25::new,
			Migration_25_26::new,
			Migration_26_27::new
	);

	private final List<DBMigration> migrations;

	private final GenericDatabase db;
	private final boolean readonly;

	private final List<UpgradeAction> afterConnectActions = new ArrayList<>();

	public DatabaseMigrator(CCProperties ccprops, GenericDatabase db, FSPath dbpath, String dbName, boolean readonly) {
		super();
		
		this.db       = db;
		this.readonly = readonly;

		this.migrations = MIGRRATION_LIST.stream().map(p -> p.invoke(db, ccprops, dbpath, dbName, readonly)).collect(Collectors.toList());

		CCLog.addDebug(this.migrations.size() + " database migrations are loaded"); //$NON-NLS-1$
	}

	private String getDBVersion() throws SQLException {
		return db.querySingleStringSQLThrow(String.format("SELECT %s FROM %s WHERE %s = '%s'",  //$NON-NLS-1$
				COL_INFO_VALUE.Name,
				TAB_INFO.Name,
				COL_INFO_KEY.Name,
				INFOKEY_DBVERSION.Key), 0);
	}

	private void setDBVersion(String version) throws SQLException {
		db.executeSQLThrow(String.format("UPDATE %s SET %s='%s' WHERE %s='%s'",  //$NON-NLS-1$
				TAB_INFO.Name,
				COL_INFO_VALUE.Name,
				version,
				COL_INFO_KEY.Name,
				INFOKEY_DBVERSION.Key));
	}
	
	@SuppressWarnings("nls")
	public void tryUpgrade() {
		try {
			String version = getDBVersion();
			
			if (version.equals(Main.DBVERSION)) return;

			CCLog.addInformation("Migrate from " + version + " to " + Main.DBVERSION);

			DialogHelper.showDispatchLocalInformation(MainFrame.getInstance() != null ? MainFrame.getInstance() : new JFrame(), "Dialogs.DatabaseMigration");
			
			CCLog.addInformation(LocaleBundle.getString("LogMessage.DatabaseUpgradeStarted"));

			if (readonly) {
				CCLog.addInformation(LocaleBundle.getString("LogMessage.MigrationFailedDueToReadOnly")); //$NON-NLS-1$
				return;
			}
			
			BackupManager.getInstanceDirect().createMigrationBackupWithWait(version);

			while (!version.equals(Main.DBVERSION)) {
				var fromVersion = version;
				var migration = migrations.stream().filter(m -> m.getFromVersion().equals(fromVersion)).findFirst();
				if (migration.isEmpty()) throw new Exception("no migration found to migrate from db-version " + fromVersion);

				var actions = migration.get().migrate();
				this.afterConnectActions.addAll(actions);

				setDBVersion(version = migration.get().getToVersion());
			}

			if (! getDBVersion().equals(Main.DBVERSION)) {
				throw new Exception("version mismatch after migration");
			}
			
			CCLog.addInformation(LocaleBundle.getString("LogMessage.DatabaseUpgradeSucess"));
			
			DialogHelper.showDispatchLocalInformation(MainFrame.getInstance() != null ? MainFrame.getInstance() : new JFrame(), "Dialogs.DatabaseMigrationSucess");
			
		} catch (Exception e) {
			CCLog.addFatalError(LocaleBundle.getString("LogMessage.DatabaseUpgradeFailed"), e);
		}
	}

	public void onAfterConnect(CCMovieList ml, CCDatabase db) {
		for (UpgradeAction action : afterConnectActions) {
			action.onAfterConnect(ml, db);
		}
	}
}
