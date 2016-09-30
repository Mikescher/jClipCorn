package de.jClipCorn.database.driver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.util.backupManager.BackupManager;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.helper.DialogHelper;

public class DatabaseUpgradeAssistant {
	private final GenericDatabase db;
	
	public interface UpgradeAction {
		public void onAfterConnect(CCMovieList ml, CCDatabase db);
	}
	
	private final List<UpgradeAction> afterConnectActions = new ArrayList<>();
	
	public DatabaseUpgradeAssistant(GenericDatabase db) {
		super();
		
		this.db = db;
	}
	
	private String getDBVersion() throws SQLException {
		return db.querySingleStringSQLThrow(String.format("SELECT %s FROM %s WHERE %s = '%s'",  //$NON-NLS-1$
				CCDatabase.TAB_INFO_COLUMN_VALUE, 
				CCDatabase.TAB_INFO, 
				CCDatabase.TAB_INFO_COLUMN_KEY, 
				CCDatabase.INFOKEY_DBVERSION), 0);
	}

	private void setDBVersion(String version) throws SQLException {
		db.executeSQLThrow(String.format("UPDATE %s SET %s='%s' WHERE %s='%s'",  //$NON-NLS-1$
				CCDatabase.TAB_INFO, 
				CCDatabase.TAB_INFO_COLUMN_VALUE, 
				version,
				CCDatabase.TAB_INFO_COLUMN_KEY, 
				CCDatabase.INFOKEY_DBVERSION));
	}
	
	@SuppressWarnings("nls")
	public void tryUpgrade() {
		try {
			String version = getDBVersion();
			
			if (version.equals(Main.DBVERSION)) return;

			DialogHelper.showLocalInformation(MainFrame.getInstance() != null ? MainFrame.getInstance() : new JFrame(), "Dialogs.DatabaseMigration");
			
			CCLog.addInformation(LocaleBundle.getString("LogMessage.DatabaseUpgradeStarted"));

			if (CCProperties.getInstance().ARG_READONLY) {
				CCLog.addInformation(LocaleBundle.getString("LogMessage.MigrationFailedDueToReadOnly")); //$NON-NLS-1$
				return;
			}
			
			BackupManager.getInstance().createMigrationBackup(version);
			
			if (version.equals("1.8")) {
				upgrade_18_19();
				setDBVersion("1.9");
			}
						
			if (! getDBVersion().equals(Main.DBVERSION)) {
				throw new Exception("version mismatch after migration");
			}
			
			CCLog.addInformation(LocaleBundle.getString("LogMessage.DatabaseUpgradeSucess"));
			
			DialogHelper.showLocalInformation(MainFrame.getInstance() != null ? MainFrame.getInstance() : new JFrame(), "Dialogs.DatabaseMigrationSucess");
			
		} catch (Exception e) {
			CCLog.addFatalError(LocaleBundle.getString("LogMessage.DatabaseUpgradeFailed"), e);
		}
	}

	@SuppressWarnings("nls")
	private void upgrade_18_19() throws SQLException {
		CCLog.addInformation("[UPGRADE 1.8 -> 1.9] Create Table Groups");
		
		db.executeSQLThrow("CREATE TABLE GROUPS(NAME VARCHAR(256) PRIMARY KEY,ORDERING INTEGER NOT NULL,COLOR INTEGER NOT NULL,SERIALIZE BIT NOT NULL)");
		
		afterConnectActions.add(new UpgradeAction() {
			@Override
			public void onAfterConnect(CCMovieList ml, CCDatabase db) {
				ml.recalculateGroupCache(false);
			}
		});
	}

	public void onAfterConnect(CCMovieList ml, CCDatabase db) {
		for (UpgradeAction action : afterConnectActions) {
			action.onAfterConnect(ml, db);
		}
	}
}
