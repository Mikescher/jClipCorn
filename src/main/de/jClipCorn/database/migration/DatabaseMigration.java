package de.jClipCorn.database.migration;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.covertab.CCDefaultCoverCache;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.database.driver.Statements;
import de.jClipCorn.features.backupManager.BackupManager;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.colorquantizer.ColorQuantizer;
import de.jClipCorn.util.colorquantizer.ColorQuantizerException;
import de.jClipCorn.util.colorquantizer.ColorQuantizerMethod;
import de.jClipCorn.util.colorquantizer.util.ColorQuantizerConverter;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.datetime.CCTime;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.sqlwrapper.CCSQLStatement;
import de.jClipCorn.util.sqlwrapper.SQLBuilder;
import de.jClipCorn.util.sqlwrapper.SQLWrapperException;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.codec.digest.DigestUtils;

import static de.jClipCorn.database.driver.Statements.*;
import static de.jClipCorn.database.driver.Statements.COL_CVRS_CREATED;

public class DatabaseMigration {
	private final GenericDatabase db;
	private final String databasepath;

	public interface UpgradeAction {
		public void onAfterConnect(CCMovieList ml, CCDatabase db);
	}
	
	private final List<UpgradeAction> afterConnectActions = new ArrayList<>();
	
	public DatabaseMigration(GenericDatabase db, String dbpath) {
		super();
		
		this.db = db;
		this.databasepath = dbpath;
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

			if (version.equals("12")) {
				upgrade_12_13();
				setDBVersion("13");
				version = "13";
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

	@SuppressWarnings("nls")
	private void upgrade_12_13() throws SQLException, SQLWrapperException, IOException, ColorQuantizerException {
		CCLog.addInformation("[UPGRADE v12 -> v13] Change 'covername' column to 'coverid' in main table");
		CCLog.addInformation("[UPGRADE v12 -> v13] Change 'covername' column to 'coverid' in season table");
		CCLog.addInformation("[UPGRADE v12 -> v13] Add 'covers' table");
		CCLog.addInformation("[UPGRADE v12 -> v13] Fill 'covers' table with data");
		CCLog.addInformation("[UPGRADE v12 -> v13] Calculate cover previews");
		CCLog.addInformation("[UPGRADE v12 -> v13] Calculate cover checksums");

		db.executeSQLThrow("ALTER TABLE ELEMENTS ADD COLUMN COVERID INTEGER");
		db.executeSQLThrow("ALTER TABLE SEASONS ADD COLUMN COVERID INTEGER");

		db.executeSQLThrow("CREATE TABLE COVERS(ID INTEGER PRIMARY KEY,FILENAME VARCHAR NOT NULL,WIDTH INTEGER NOT NULL,HEIGHT INTEGER NOT NULL,HASH_FILE VARCHAR(64) NOT NULL,FILESIZE BIGINT NOT NULL,PREVIEW_TYPE INTEGER NOT NULL,PREVIEW BLOB NOT NULL,CREATED VARCHAR NOT NULL)");

		File cvrdir = new File(PathFormatter.combine(PathFormatter.getRealSelfDirectory(), databasepath, CCDefaultCoverCache.COVER_DIRECTORY));

		final String prefix = CCProperties.getInstance().PROP_COVER_PREFIX.getValue();
		final String suffix = "." + CCProperties.getInstance().PROP_COVER_TYPE.getValue();  //$NON-NLS-1$

		String[] files = cvrdir.list((path, name) -> name.startsWith(prefix) && name.endsWith(suffix));
		for (String _f : files) {
			String f = PathFormatter.combine(cvrdir.getAbsolutePath(), _f);
			BufferedImage img = ImageIO.read(new File( f ));

			int id = Integer.parseInt(PathFormatter.getFilename(f).substring(prefix.length()));
			String fn = PathFormatter.getFilenameWithExt(f);

			String checksum;
			try (FileInputStream fis = new FileInputStream(f)) { checksum = DigestUtils.sha256Hex(fis).toUpperCase(); }

			ColorQuantizerMethod ptype = CCProperties.getInstance().PROP_DATABASE_COVER_QUANTIZER.getValue();
			ColorQuantizer quant = ptype.create();
			quant.analyze(img, 16);
			byte[] preview = ColorQuantizerConverter.quantizeTo4BitRaw(quant, ColorQuantizerConverter.shrink(img, ColorQuantizerConverter.PREVIEW_WIDTH));

			CCSQLStatement stmt = SQLBuilder.createInsert(Statements.TAB_COVERS)
					.addPreparedFields(Statements.COL_CVRS_ID, Statements.COL_CVRS_FILENAME, Statements.COL_CVRS_WIDTH)
					.addPreparedFields(Statements.COL_CVRS_HEIGHT, Statements.COL_CVRS_HASH_FILE)
					.addPreparedFields(Statements.COL_CVRS_FILESIZE, Statements.COL_CVRS_PREVIEW, Statements.COL_CVRS_PREVIEWTYPE, Statements.COL_CVRS_CREATED)
					.build(db::createPreparedStatement, new ArrayList<>());

			stmt.clearParameters();

			stmt.setInt(COL_CVRS_ID,          id);
			stmt.setStr(COL_CVRS_FILENAME,    fn);
			stmt.setInt(COL_CVRS_WIDTH,       img.getWidth());
			stmt.setInt(COL_CVRS_HEIGHT,      img.getHeight());
			stmt.setStr(COL_CVRS_HASH_FILE,   checksum);
			stmt.setLng(COL_CVRS_FILESIZE,    new File(f).length());
			stmt.setBlb(COL_CVRS_PREVIEW,     preview);
			stmt.setInt(COL_CVRS_PREVIEWTYPE, ptype.asInt());
			stmt.setCDT(COL_CVRS_CREATED,     CCDateTime.createFromFileTimestamp(new File(f).lastModified(), TimeZone.getDefault()));

			stmt.executeUpdate();

			db.executeSQLThrow("UPDATE ELEMENTS SET COVERID = "+id+" WHERE COVERNAME = '"+fn+"'");
			db.executeSQLThrow("UPDATE SEASONS  SET COVERID = "+id+" WHERE COVERNAME = '"+fn+"'");

			CCLog.addDebug("Migrated file: " + fn);
		}

		db.executeSQLThrow("CREATE TABLE _ELEMENTS(LOCALID INTEGER PRIMARY KEY,NAME VARCHAR(128) NOT NULL,VIEWED BIT NOT NULL,VIEWED_HISTORY VARCHAR(4096) NOT NULL,ZYKLUS VARCHAR(128) NOT NULL,ZYKLUSNUMBER INTEGER NOT NULL,QUALITY TINYINT NOT NULL,LANGUAGE BIGINT NOT NULL,GENRE BIGINT NOT NULL,LENGTH INTEGER NOT NULL,ADDDATE DATE NOT NULL,ONLINESCORE TINYINT NOT NULL,FSK TINYINT NOT NULL,FORMAT TINYINT NOT NULL,MOVIEYEAR SMALLINT NOT NULL,ONLINEREF VARCHAR(128) NOT NULL,GROUPS VARCHAR(4096) NOT NULL,FILESIZE BIGINT NOT NULL,TAGS SMALLINT NOT NULL,PART1 VARCHAR(512) NOT NULL,PART2 VARCHAR(512) NOT NULL,PART3 VARCHAR(512) NOT NULL,PART4 VARCHAR(512) NOT NULL,PART5 VARCHAR(512) NOT NULL,PART6 VARCHAR(512) NOT NULL,SCORE TINYINT NOT NULL,COVERID INTEGER(256) NOT NULL,TYPE TINYINT NOT NULL,SERIESID INTEGER NOT NULL)");
		db.executeSQLThrow("CREATE TABLE _SEASONS(SEASONID INTEGER PRIMARY KEY,SERIESID INTEGER NOT NULL,NAME VARCHAR(128) NOT NULL,SEASONYEAR SMALLINT NOT NULL,COVERID INTEGER(256) NOT NULL,FOREIGN KEY(SERIESID) REFERENCES ELEMENTS(LOCALID))");

		db.executeSQLThrow("INSERT INTO _ELEMENTS SELECT LOCALID,NAME,VIEWED,VIEWED_HISTORY,ZYKLUS,ZYKLUSNUMBER,QUALITY,LANGUAGE,GENRE,LENGTH,ADDDATE,ONLINESCORE,FSK,FORMAT,MOVIEYEAR,ONLINEREF,GROUPS,FILESIZE,TAGS,PART1,PART2,PART3,PART4,PART5,PART6,SCORE,COVERID,TYPE,SERIESID FROM ELEMENTS");
		db.executeSQLThrow("INSERT INTO _SEASONS SELECT SEASONID,SERIESID,NAME,SEASONYEAR,COVERID FROM SEASONS");

		db.executeSQLThrow("DROP TABLE ELEMENTS");
		db.executeSQLThrow("DROP TABLE SEASONS");

		db.executeSQLThrow("ALTER TABLE _ELEMENTS RENAME TO ELEMENTS");
		db.executeSQLThrow("ALTER TABLE _SEASONS RENAME TO SEASONS");

		File cc = new File(PathFormatter.combine(cvrdir.getAbsolutePath(), "covercache.xml"));
		if (cc.exists()) cc.delete();
	}

	public void onAfterConnect(CCMovieList ml, CCDatabase db) {
		for (UpgradeAction action : afterConnectActions) {
			action.onAfterConnect(ml, db);
		}
	}
}