package de.jClipCorn.database.driver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.util.Statements;
import de.jClipCorn.features.backupManager.BackupManager;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCTime;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.stream.CCStreams;

public class DatabaseMigration {
	private final GenericDatabase db;
	
	public interface UpgradeAction {
		public void onAfterConnect(CCMovieList ml, CCDatabase db);
	}
	
	private final List<UpgradeAction> afterConnectActions = new ArrayList<>();
	
	public DatabaseMigration(GenericDatabase db) {
		super();
		
		this.db = db;
	}
	
	private String getDBVersion() throws SQLException {
		return db.querySingleStringSQLThrow(String.format("SELECT %s FROM %s WHERE %s = '%s'",  //$NON-NLS-1$
				Statements.COL_INFO_VALUE.Name,
				Statements.TAB_INFO,
				Statements.COL_INFO_KEY.Name,
				CCDatabase.INFOKEY_DBVERSION), 0);
	}

	private void setDBVersion(String version) throws SQLException {
		db.executeSQLThrow(String.format("UPDATE %s SET %s='%s' WHERE %s='%s'",  //$NON-NLS-1$
				Statements.TAB_INFO,
				Statements.COL_INFO_VALUE.Name,
				version,
				Statements.COL_INFO_KEY.Name,
				CCDatabase.INFOKEY_DBVERSION));
	}
	
	@SuppressWarnings("nls")
	public void tryUpgrade() {
		try {
			String version = getDBVersion();
			
			if (version.equals(Main.DBVERSION)) return;

			DialogHelper.showDispatchLocalInformation(MainFrame.getInstance() != null ? MainFrame.getInstance() : new JFrame(), "Dialogs.DatabaseMigration");
			
			CCLog.addInformation(LocaleBundle.getString("LogMessage.DatabaseUpgradeStarted"));

			if (CCProperties.getInstance().ARG_READONLY) {
				CCLog.addInformation(LocaleBundle.getString("LogMessage.MigrationFailedDueToReadOnly")); //$NON-NLS-1$
				return;
			}
			
			BackupManager.getInstanceDirect().createMigrationBackupWithWait(version);
			
			if (version.equals("1.5")) {
				upgrade_06_07();
				setDBVersion("1.6");
				version = "1.6";
			}
			
			if (version.equals("1.6")) {
				upgrade_07_08();
				setDBVersion("1.7");
				version = "1.7";
			}
			
			if (version.equals("1.7")) {
				upgrade_08_09();
				setDBVersion("1.8");
				version = "1.8";
			}
			
			if (version.equals("1.8")) {
				upgrade_09_10();
				setDBVersion("1.9");
				version = "1.9";
			}

			if (version.equals("1.9")) {
				upgrade_10_11();
				setDBVersion("11");
				version = "11";
			}

			if (version.equals("11")) {
				upgrade_11_12();
				setDBVersion("12");
				version = "12";
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

	@SuppressWarnings("nls")
	private void upgrade_06_07() throws SQLException {
		CCLog.addInformation("[UPGRADE 1.5 -> 1.6] Rename column 'STATUS' to 'TAGS'");

		db.executeSQLThrow("ALTER TABLE MOVIES ADD COLUMN TAGS SMALLINT");
		db.executeSQLThrow("UPDATE MOVIES SET TAGS = STATUS");
		db.executeSQLThrow("ALTER TABLE MOVIES DROP COLUMN STATUS");

		db.executeSQLThrow("ALTER TABLE EPISODES ADD COLUMN TAGS SMALLINT");
		db.executeSQLThrow("UPDATE EPISODES SET TAGS = STATUS");
		db.executeSQLThrow("ALTER TABLE EPISODES DROP COLUMN STATUS");
	}

	@SuppressWarnings("nls")
	private void upgrade_07_08() throws SQLException {
		CCLog.addInformation("[UPGRADE 1.6 -> 1.7] Add Info Table");

		String date = CCDate.getCurrentDate().toStringSQL();
		String time = CCTime.getCurrentTime().toStringSQL();
		
		db.executeSQLThrow("CREATE TABLE INFO (\"IKEY\" VARCHAR(256) NOT NULL, IVALUE VARCHAR(256) NOT NULL, PRIMARY KEY (\"IKEY\"))");

		db.executeSQLThrow("INSERT INTO INFO (\"IKEY\", IVALUE) VALUES ('VERSION_DB', '1.7')");
		db.executeSQLThrow("INSERT INTO INFO (\"IKEY\", IVALUE) VALUES ('CREATION_DATE', '"+date+"')");
		db.executeSQLThrow("INSERT INTO INFO (\"IKEY\", IVALUE) VALUES ('CREATION_TIME', '"+time+"')");
		db.executeSQLThrow("INSERT INTO INFO (\"IKEY\", IVALUE) VALUES ('CREATION_USERNAME', '" + System.getProperty("user.name") + "')");
	}

	@SuppressWarnings("nls")
	private void upgrade_08_09() throws SQLException {
		CCLog.addInformation("[UPGRADE 1.7 -> 1.8] Added Groups to movies and series");
		CCLog.addInformation("[UPGRADE 1.7 -> 1.8] Added viewed-history to movies and series (and removed LastViewed)");
		CCLog.addInformation("[UPGRADE 1.7 -> 1.8] Added OnlineReference to Movies and series");

		db.executeSQLThrow("ALTER TABLE MOVIES ADD COLUMN VIEWED_HISTORY VARCHAR(4096)");
		db.executeSQLThrow("UPDATE MOVIES SET VIEWED_HISTORY = \"\"");
		
		db.executeSQLThrow("ALTER TABLE MOVIES ADD COLUMN ONLINEREF VARCHAR(128)");
		db.executeSQLThrow("UPDATE MOVIES SET ONLINEREF = \"\"");
		
		db.executeSQLThrow("ALTER TABLE MOVIES ADD COLUMN GROUPS VARCHAR(4096)");
		db.executeSQLThrow("UPDATE MOVIES SET GROUPS = \"\"");

		db.executeSQLThrow("ALTER TABLE EPISODES ADD COLUMN VIEWED_HISTORY VARCHAR(4096)");
		db.executeSQLThrow("UPDATE EPISODES SET VIEWED_HISTORY = LASTVIEWED");
		db.executeSQLThrow("ALTER TABLE EPISODES DROP COLUMN LASTVIEWED");
	}
	
	@SuppressWarnings("nls")
	private void upgrade_09_10() throws SQLException {
		CCLog.addInformation("[UPGRADE 1.8 -> 1.9] Create Table Groups");
		
		db.executeSQLThrow("CREATE TABLE GROUPS(NAME VARCHAR(256) PRIMARY KEY,ORDERING INTEGER NOT NULL,COLOR INTEGER NOT NULL,SERIALIZE BIT NOT NULL)");
		
		//afterConnectActions.add(new UpgradeAction() {
		//	@Override
		//	public void onAfterConnect(CCMovieList ml, CCDatabase db) {
		//		ml.recalculateGroupCache(false);
		//	}
		//});
	}

	@SuppressWarnings("nls")
	private void upgrade_10_11() throws SQLException {
		CCLog.addInformation("[UPGRADE 1.9 -> v11] Added ParentGroup identifier to groups");

		db.executeSQLThrow("ALTER TABLE GROUPS ADD COLUMN PARENTGROUP VARCHAR(256)");
		db.executeSQLThrow("UPDATE GROUPS SET PARENTGROUP = \"\"");

		db.executeSQLThrow("ALTER TABLE GROUPS ADD COLUMN VISIBLE BIT");
		db.executeSQLThrow("UPDATE GROUPS SET VISIBLE = 1");
	}

	@SuppressWarnings("nls")
	private void upgrade_11_12() throws SQLException {
		CCLog.addInformation("[UPGRADE v11 -> v12] Extend language column size in elements table");
		CCLog.addInformation("[UPGRADE v11 -> v12] Convert language column in elements table");
		CCLog.addInformation("[UPGRADE v11 -> v12] Add language column to episodes");
		CCLog.addInformation("[UPGRADE v11 -> v12] Update language for episodes");
		CCLog.addInformation("[UPGRADE v11 -> v12] Update language for series");
		CCLog.addInformation("[UPGRADE v11 -> v12] Convert language column in episodes table");

		if (db.getDBType() != CCDatabaseDriver.SQLITE) db.executeSQLThrow("ALTER TABLE ELEMENTS ALTER COLUMN LANGUAGE BIGINT");

		db.executeSQLThrow("ALTER TABLE EPISODES ADD COLUMN LANGUAGE BIGINT");

		for (Object[] series : db.querySQL("SELECT LANGUAGE, SERIESID, TYPE FROM ELEMENTS WHERE TYPE=1", 3))
		{
			for (Object[] season : db.querySQL("SELECT SEASONID, SERIESID FROM SEASONS WHERE SERIESID="+series[1], 2))
			{
				db.executeSQLThrow("UPDATE EPISODES SET LANGUAGE="+series[0]+" WHERE SEASONID="+season[0]);
			}
		}

		for (CCDBLanguage lng : CCStreams.iterate(CCDBLanguage.values()).autosortByProperty(CCDBLanguage::asInt).reverse())
		{
			db.executeSQLThrow("UPDATE ELEMENTS SET LANGUAGE = "+lng.asBitMask()+" WHERE LANGUAGE = "+lng.asInt());
			db.executeSQLThrow("UPDATE EPISODES SET LANGUAGE = "+lng.asBitMask()+" WHERE LANGUAGE = "+lng.asInt());
		}

		db.executeSQLThrow("UPDATE ELEMENTS SET LANGUAGE=0 WHERE TYPE=1");
	}

	public void onAfterConnect(CCMovieList ml, CCDatabase db) {
		for (UpgradeAction action : afterConnectActions) {
			action.onAfterConnect(ml, db);
		}
	}
}
