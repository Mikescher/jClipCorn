package de.jClipCorn.database.migration;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.covertab.CCDefaultCoverCache;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.features.backupManager.BackupManager;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.colorquantizer.ColorQuantizer;
import de.jClipCorn.util.colorquantizer.ColorQuantizerException;
import de.jClipCorn.util.colorquantizer.ColorQuantizerMethod;
import de.jClipCorn.util.colorquantizer.util.ColorQuantizerConverter;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.datetime.CCTime;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.sqlwrapper.CCSQLStatement;
import de.jClipCorn.util.sqlwrapper.SQLBuilder;
import de.jClipCorn.util.sqlwrapper.SQLWrapperException;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.codec.digest.DigestUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static de.jClipCorn.database.driver.DatabaseStructure.*;

public class DatabaseMigration {
	private final GenericDatabase db;
	private final CCProperties ccprops;
	private final FSPath databaseDirectory;
	private final String databaseName;
	private final boolean readonly;

	public interface UpgradeAction {
		void onAfterConnect(CCMovieList ml, CCDatabase db);
	}
	
	private final List<UpgradeAction> afterConnectActions = new ArrayList<>();
	
	public DatabaseMigration(CCProperties ccprops, GenericDatabase db, FSPath dbpath, String dbName, boolean readonly) {
		super();
		
		this.db                = db;
		this.databaseDirectory = dbpath;
		this.databaseName      = dbName;
		this.readonly          = readonly;
		this.ccprops           = ccprops;
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
			
			if (version.equals("1.5")) {
				upgrade_06_07();
				setDBVersion(version = "1.6");
			}
			
			if (version.equals("1.6")) {
				upgrade_07_08();
				setDBVersion(version = "1.7");
			}

			if (version.equals("1.7")) {
				upgrade_08_09();
				setDBVersion(version = "1.8");
			}
			
			if (version.equals("1.8")) {
				upgrade_09_10();
				setDBVersion(version = "1.9");
			}

			if (version.equals("1.9")) {
				upgrade_10_11();
				setDBVersion(version = "11");
			}

			if (version.equals("11")) {
				upgrade_11_12();
				setDBVersion(version = "12");
			}

			if (version.equals("12")) {
				upgrade_12_13();
				setDBVersion(version = "13");
			}

			if (version.equals("13")) {
				upgrade_13_14();
				setDBVersion(version = "14");
			}

			if (version.equals("14")) {
				upgrade_14_15();
				setDBVersion(version = "15");
			}

			if (version.equals("15")) {
				upgrade_15_16();
				setDBVersion(version = "16");
			}

			if (version.equals("16")) {
				upgrade_16_17();
				setDBVersion(version = "17");
			}

			if (version.equals("17")) {
				upgrade_17_18();
				setDBVersion(version = "18");
			}

			if (version.equals("18")) {
				upgrade_18_19();
				setDBVersion(version = "19");
			}

			if (version.equals("19")) {
				upgrade_19_20();
				setDBVersion(version = "20");
			}

			if (version.equals("20")) {
				upgrade_20_21();
				setDBVersion(version = "21");
			}

			if (version.equals("21")) {
				upgrade_21_22();
				setDBVersion(version = "22");
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

		var cvrdir = databaseDirectory.append(databaseName, CCDefaultCoverCache.COVER_DIRECTORY_NAME);

		final String prefix = ccprops.PROP_COVER_PREFIX.getValue();
		final String suffix = "." + ccprops.PROP_COVER_TYPE.getValue();  //$NON-NLS-1$

		String[] files = cvrdir.listFilenames((path, name) -> name.startsWith(prefix) && name.endsWith(suffix));
		for (String _f : files) {
			var f = cvrdir.append(_f);
			BufferedImage img = ImageIO.read(f.toFile());

			int id = Integer.parseInt(f.getFilenameWithoutExt().substring(prefix.length()));
			String fn = f.getFilenameWithExt();

			String checksum;
			try (FileInputStream fis = new FileInputStream(f.toFile())) { checksum = DigestUtils.sha256Hex(fis).toUpperCase(); }

			ColorQuantizerMethod ptype = ccprops.PROP_DATABASE_COVER_QUANTIZER.getValue();
			ColorQuantizer quant = ptype.create();
			quant.analyze(img, 16);
			byte[] preview = ColorQuantizerConverter.quantizeTo4BitRaw(quant, ColorQuantizerConverter.shrink(img, ColorQuantizerConverter.PREVIEW_WIDTH));

			CCSQLStatement stmt = SQLBuilder.createInsert(TAB_COVERS)
					.addPreparedFields(COL_CVRS_ID, COL_CVRS_FILENAME, COL_CVRS_WIDTH)
					.addPreparedFields(COL_CVRS_HEIGHT, COL_CVRS_HASH_FILE)
					.addPreparedFields(COL_CVRS_FILESIZE, COL_CVRS_PREVIEW, COL_CVRS_PREVIEWTYPE, COL_CVRS_CREATED)
					.build(db::createPreparedStatement, new ArrayList<>());

			stmt.clearParameters();

			stmt.setInt(COL_CVRS_ID,          id);
			stmt.setStr(COL_CVRS_FILENAME,    fn);
			stmt.setInt(COL_CVRS_WIDTH,       img.getWidth());
			stmt.setInt(COL_CVRS_HEIGHT,      img.getHeight());
			stmt.setStr(COL_CVRS_HASH_FILE,   checksum);
			stmt.setLng(COL_CVRS_FILESIZE,    f.filesize().getBytes());
			stmt.setBlb(COL_CVRS_PREVIEW,     preview);
			stmt.setInt(COL_CVRS_PREVIEWTYPE, ptype.asInt());
			stmt.setCDT(COL_CVRS_CREATED,     CCDateTime.createFromFileTimestamp(f.toFile().lastModified(), TimeZone.getDefault()));

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

		var cc = cvrdir.append("covercache.xml");
		if (cc.exists()) cc.deleteWithException();
	}

	@SuppressWarnings("nls")
	private void upgrade_13_14() throws SQLException {
		CCLog.addInformation("[UPGRADE v13 -> v14] Remove existing [LOGTRIGGER_*] trigger");
		CCLog.addInformation("[UPGRADE v13 -> v14] Remove column 'quality' in main table");
		CCLog.addInformation("[UPGRADE v13 -> v14] Remove column 'quality' in episodes table");
		CCLog.addInformation("[UPGRADE v13 -> v14] Add 16 MediaInfo.* columns in main table (default=NULL)");
		CCLog.addInformation("[UPGRADE v13 -> v14] Add 16 MediaInfo.* columns in episodes table (default=NULL)");
		CCLog.addInformation("[UPGRADE v13 -> v14] Remove [rand] entry from info table");
		CCLog.addInformation("[UPGRADE v13 -> v14] Create [temp] table");
		CCLog.addInformation("[UPGRADE v13 -> v14] Add history=false to info table");
		CCLog.addInformation("[UPGRADE v13 -> v14] Remove [RAND] entries from history table");
		CCLog.addInformation("[UPGRADE v13 -> v14] Create history table (if not exists)");
		CCLog.addInformation("[UPGRADE v13 -> v14] Make ID's unique");
		CCLog.addInformation("[UPGRADE v13 -> v14] Add [LAST_ID] entry");

		for (String trigger : CCStreams.iterate(db.listTrigger()).filter(t -> t.startsWith("LOGTRIGGER_")))
		{
			db.executeSQLThrow("DROP TRIGGER ["+trigger+"]");
		}

		db.executeSQLThrow("CREATE TABLE _ELEMENTS (LOCALID INTEGER PRIMARY KEY,NAME VARCHAR(128) NOT NULL,VIEWED BIT NOT NULL,VIEWED_HISTORY VARCHAR(4096) NOT NULL,ZYKLUS VARCHAR(128) NOT NULL,ZYKLUSNUMBER INTEGER NOT NULL,LANGUAGE BIGINT NOT NULL,GENRE BIGINT NOT NULL,LENGTH INTEGER NOT NULL,ADDDATE DATE NOT NULL,ONLINESCORE TINYINT NOT NULL,FSK TINYINT NOT NULL,FORMAT TINYINT NOT NULL,MOVIEYEAR SMALLINT NOT NULL,ONLINEREF VARCHAR(128) NOT NULL,GROUPS VARCHAR(4096) NOT NULL,FILESIZE BIGINT NOT NULL,TAGS SMALLINT NOT NULL,PART1 VARCHAR(512) NOT NULL,PART2 VARCHAR(512) NOT NULL,PART3 VARCHAR(512) NOT NULL,PART4 VARCHAR(512) NOT NULL,PART5 VARCHAR(512) NOT NULL,PART6 VARCHAR(512) NOT NULL,SCORE TINYINT NOT NULL,COVERID INTEGER(256) NOT NULL,TYPE TINYINT NOT NULL,SERIESID INTEGER NOT NULL, [MEDIAINFO.FILESIZE] BIGINT, [MEDIAINFO.CDATE] BIGINT, [MEDIAINFO.MDATE] BIGINT, [MEDIAINFO.AFORMAT] VARCHAR(128), [MEDIAINFO.VFORMAT] VARCHAR(128), [MEDIAINFO.WIDTH] SMALLINT, [MEDIAINFO.HEIGHT] SMALLINT, [MEDIAINFO.FRAMERATE] REAL, [MEDIAINFO.DURATION] REAL, [MEDIAINFO.BITDEPTH] TINYINT, [MEDIAINFO.BITRATE] INTEGER, [MEDIAINFO.FRAMECOUNT] INTEGER, [MEDIAINFO.ACHANNELS] INTEGER, [MEDIAINFO.VCODEC] VARCHAR(128), [MEDIAINFO.ACODEC] VARCHAR(128), [MEDIAINFO.SAMPLERATE] INTEGER)");
		db.executeSQLThrow("INSERT INTO _ELEMENTS SELECT LOCALID,NAME,VIEWED,VIEWED_HISTORY,ZYKLUS,ZYKLUSNUMBER,LANGUAGE,GENRE,LENGTH,ADDDATE,ONLINESCORE,FSK,FORMAT,MOVIEYEAR,ONLINEREF,GROUPS,FILESIZE,TAGS,PART1,PART2,PART3,PART4,PART5,PART6,SCORE,COVERID,TYPE,SERIESID,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null FROM ELEMENTS");
		db.executeSQLThrow("DROP TABLE ELEMENTS");
		db.executeSQLThrow("ALTER TABLE _ELEMENTS RENAME TO ELEMENTS");

		db.executeSQLThrow("CREATE TABLE _EPISODES (LOCALID INTEGER PRIMARY KEY,SEASONID INTEGER NOT NULL,EPISODE SMALLINT NOT NULL,NAME VARCHAR(128) NOT NULL,VIEWED BIT NOT NULL,VIEWED_HISTORY VARCHAR(4096) NOT NULL,LENGTH INTEGER NOT NULL,FORMAT TINYINT NOT NULL,FILESIZE BIGINT NOT NULL,PART1 VARCHAR(512) NOT NULL,ADDDATE DATE NOT NULL,TAGS SMALLINT NOT NULL, LANGUAGE BIGINT, [MEDIAINFO.FILESIZE] BIGINT, [MEDIAINFO.CDATE] BIGINT, [MEDIAINFO.MDATE] BIGINT, [MEDIAINFO.AFORMAT] VARCHAR(128), [MEDIAINFO.VFORMAT] VARCHAR(128), [MEDIAINFO.WIDTH] SMALLINT, [MEDIAINFO.HEIGHT] SMALLINT, [MEDIAINFO.FRAMERATE] REAL, [MEDIAINFO.DURATION] REAL, [MEDIAINFO.BITDEPTH] TINYINT, [MEDIAINFO.BITRATE] INTEGER, [MEDIAINFO.FRAMECOUNT] INTEGER, [MEDIAINFO.ACHANNELS] INTEGER, [MEDIAINFO.VCODEC] VARCHAR(128), [MEDIAINFO.ACODEC] VARCHAR(128), [MEDIAINFO.SAMPLERATE] INTEGER,FOREIGN KEY(SEASONID) REFERENCES SEASONS(SEASONID))");
		db.executeSQLThrow("INSERT INTO _EPISODES SELECT LOCALID,SEASONID,EPISODE,NAME,VIEWED,VIEWED_HISTORY,LENGTH,FORMAT,FILESIZE,PART1,ADDDATE,TAGS,LANGUAGE,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null FROM EPISODES");
		db.executeSQLThrow("DROP TABLE EPISODES");
		db.executeSQLThrow("ALTER TABLE _EPISODES RENAME TO EPISODES");

		db.executeSQLThrow("DELETE FROM INFO WHERE IKEY='RAND'");

		db.executeSQLThrow("CREATE TABLE TEMP (IKEY TEXT NOT NULL, IVALUE TEXT NOT NULL, PRIMARY KEY(IKEY))");

		db.executeSQLThrow("INSERT INTO INFO (IKEY, IVALUE) VALUES ('HISTORY_ENABLED', '0')");

		if (!db.listTables().contains("HISTORY"))
		{
			db.executeSQLThrow("CREATE TABLE [HISTORY] ([TABLE] VARCHAR(16) NOT NULL, [ID] VARCHAR(256) NOT NULL, [DATE] VARCHAR(32) NOT NULL, [ACTION] VARCHAR(32) NOT NULL, [FIELD] VARCHAR(32) NOT NULL, [OLD] VARCHAR(4096), [NEW] VARCHAR(4096))");
		}

		db.executeSQLThrow("DELETE FROM HISTORY WHERE [TABLE]='INFO' AND [ID]='RAND' AND [FIELD]='IVALUE'");

		{
			List<Integer> ids_elm = db.querySQL("SELECT LOCALID  FROM ELEMENTS ORDER BY LOCALID",               1, o -> (int)o[0]);
			List<Integer> ids_ser = db.querySQL("SELECT SERIESID FROM ELEMENTS WHERE TYPE=1 ORDER BY SERIESID", 1, o -> (int)o[0]);
			List<Integer> ids_sea = db.querySQL("SELECT SEASONID FROM SEASONS ORDER BY SEASONID",               1, o -> (int)o[0]);
			List<Integer> ids_epi = db.querySQL("SELECT LOCALID  FROM EPISODES ORDER BY LOCALID",               1, o -> (int)o[0]);

			int id = CCStreams.iterate(ids_elm, ids_ser, ids_sea, ids_epi).autoMaxOrDefault(1) + 1;

			db.executeSQLThrow("BEGIN TRANSACTION");
			for (int elmid : ids_elm) {
				id++;
				CCLog.addDebug("[MIGRATE] Convert Element ID ["+elmid+"] to ["+id+"]");
				db.executeSQLThrow("UPDATE ELEMENTS SET LOCALID = "+id+" WHERE LOCALID = " + elmid);
				db.executeSQLThrow("UPDATE HISTORY SET ID = '"+id+"' WHERE ID = '" + elmid + "' AND [TABLE]='ELEMENTS'");
			}
			for (int serid : ids_ser) {
				id++;
				CCLog.addDebug("[MIGRATE] Convert Series ID ["+serid+"] to ["+id+"]");
				db.executeSQLThrow("UPDATE ELEMENTS SET SERIESID = "+id+" WHERE SERIESID = " + serid);
				db.executeSQLThrow("UPDATE SEASONS SET SERIESID = "+id+" WHERE SERIESID = " + serid);
				db.executeSQLThrow("UPDATE HISTORY SET [NEW] = '"+id+"' WHERE [NEW] = '" + serid + "' AND [FIELD]='SERIESID'");
				db.executeSQLThrow("UPDATE HISTORY SET [OLD] = '"+id+"' WHERE [OLD] = '" + serid + "' AND [FIELD]='SERIESID'");
			}
			for (int seaid : ids_sea) {
				id++;
				CCLog.addDebug("[MIGRATE] Convert Season ID ["+seaid+"] to ["+id+"]");
				db.executeSQLThrow("UPDATE SEASONS SET SEASONID = "+id+" WHERE SEASONID = " + seaid);
				db.executeSQLThrow("UPDATE EPISODES SET SEASONID = "+id+" WHERE SEASONID = " + seaid);
				db.executeSQLThrow("UPDATE HISTORY SET ID = '"+id+"' WHERE ID = '" + seaid + "' AND [TABLE]='SEASONS'");
				db.executeSQLThrow("UPDATE HISTORY SET [NEW] = '"+id+"' WHERE [NEW] = '" + seaid+"' AND [FIELD]='SEASONID'");
				db.executeSQLThrow("UPDATE HISTORY SET [OLD] = '"+id+"' WHERE [OLD] = '" + seaid+"' AND [FIELD]='SEASONID'");
			}
			for (int epiid : ids_epi) {
				id++;
				CCLog.addDebug("[MIGRATE] Convert Episode ID ["+epiid+"] to ["+id+"]");
				db.executeSQLThrow("UPDATE EPISODES SET LOCALID = "+id+" WHERE LOCALID = " + epiid);
				db.executeSQLThrow("UPDATE HISTORY SET ID = '"+id+"' WHERE ID = '" + epiid + "' AND [TABLE]='EPISODES'");
			}

			db.executeSQLThrow("INSERT INTO INFO VALUES ('LAST_ID', "+id+")");

			db.executeSQLThrow("COMMIT TRANSACTION");

		}

	}

	@SuppressWarnings("nls")
	private void upgrade_14_15() {
		// nothing to do
	}

	@SuppressWarnings("nls")
	private void upgrade_15_16() throws Exception
	{
		CCLog.addInformation("[UPGRADE v15 -> v16] Remove column Movies.viewed");
		CCLog.addInformation("[UPGRADE v15 -> v16] Remove column Episodes.viewed");

		var err1 = db.querySingleIntSQL("SELECT COUNT(*) FROM ELEMENTS WHERE viewed=1 AND VIEWED_HISTORY =  '';", 0);
		if (err1 != 0) throw new Exception("Some movies are viewed but have no history");

		var err2 = db.querySingleIntSQL("SELECT COUNT(*) FROM ELEMENTS WHERE viewed=0 AND VIEWED_HISTORY <> '';", 0);
		if (err2 != 0) throw new Exception("Some movies are not viewed but have a history");

		var err3 = db.querySingleIntSQL("SELECT COUNT(*) FROM EPISODES WHERE viewed=1 AND VIEWED_HISTORY =  '';", 0);
		if (err3 != 0) throw new Exception("Some episodes are viewed but have no history");

		var err4 = db.querySingleIntSQL("SELECT COUNT(*) FROM EPISODES WHERE viewed=0 AND VIEWED_HISTORY <> '';", 0);
		if (err4 != 0) throw new Exception("Some episodes are not viewed but have a history");

		db.executeSQLThrow("BEGIN TRANSACTION");
		db.executeSQLThrow("PRAGMA foreign_keys = OFF;");
		{
			for (String trigger : CCStreams.iterate(db.listTrigger()).filter(t -> t.startsWith("JCCTRIGGER_")))
			{
				db.executeSQLThrow("DROP TRIGGER ["+trigger+"]");
			}

			db.executeSQLThrow("CREATE TABLE \"___t1\" (  \"LOCALID\" INTEGER,  \"NAME\" VARCHAR(128) NOT NULL,  \"VIEWED_HISTORY\" VARCHAR(4096) NOT NULL,  \"ZYKLUS\" VARCHAR(128) NOT NULL,  \"ZYKLUSNUMBER\" INTEGER NOT NULL,  \"LANGUAGE\" BIGINT NOT NULL,  \"GENRE\" BIGINT NOT NULL,  \"LENGTH\" INTEGER NOT NULL,  \"ADDDATE\" DATE NOT NULL,  \"ONLINESCORE\" TINYINT NOT NULL,  \"FSK\" TINYINT NOT NULL,  \"FORMAT\" TINYINT NOT NULL,  \"MOVIEYEAR\" SMALLINT NOT NULL,  \"ONLINEREF\" VARCHAR(128) NOT NULL,  \"GROUPS\" VARCHAR(4096) NOT NULL,  \"FILESIZE\" BIGINT NOT NULL,  \"TAGS\" SMALLINT NOT NULL,  \"PART1\" VARCHAR(512) NOT NULL,  \"PART2\" VARCHAR(512) NOT NULL,  \"PART3\" VARCHAR(512) NOT NULL,  \"PART4\" VARCHAR(512) NOT NULL,  \"PART5\" VARCHAR(512) NOT NULL,  \"PART6\" VARCHAR(512) NOT NULL,  \"SCORE\" TINYINT NOT NULL,  \"COVERID\" INTEGER(256) NOT NULL,  \"TYPE\" TINYINT NOT NULL,  \"SERIESID\" INTEGER NOT NULL,  \"MEDIAINFO.FILESIZE\" BIGINT,  \"MEDIAINFO.CDATE\" BIGINT,  \"MEDIAINFO.MDATE\" BIGINT,  \"MEDIAINFO.AFORMAT\" VARCHAR(128),  \"MEDIAINFO.VFORMAT\" VARCHAR(128),  \"MEDIAINFO.WIDTH\" SMALLINT,  \"MEDIAINFO.HEIGHT\" SMALLINT,  \"MEDIAINFO.FRAMERATE\" REAL,  \"MEDIAINFO.DURATION\" REAL,  \"MEDIAINFO.BITDEPTH\" TINYINT,  \"MEDIAINFO.BITRATE\" INTEGER,  \"MEDIAINFO.FRAMECOUNT\" INTEGER,  \"MEDIAINFO.ACHANNELS\" INTEGER,  \"MEDIAINFO.VCODEC\" VARCHAR(128),  \"MEDIAINFO.ACODEC\" VARCHAR(128),  \"MEDIAINFO.SAMPLERATE\" INTEGER,  PRIMARY KEY(\"LOCALID\") );");
			db.executeSQLThrow("INSERT INTO ___t1 SELECT \"LOCALID\",\"NAME\",\"VIEWED_HISTORY\",\"ZYKLUS\",\"ZYKLUSNUMBER\",\"LANGUAGE\",\"GENRE\",\"LENGTH\",\"ADDDATE\",\"ONLINESCORE\",\"FSK\",\"FORMAT\",\"MOVIEYEAR\",\"ONLINEREF\",\"GROUPS\",\"FILESIZE\",\"TAGS\",\"PART1\",\"PART2\",\"PART3\",\"PART4\",\"PART5\",\"PART6\",\"SCORE\",\"COVERID\",\"TYPE\",\"SERIESID\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.SAMPLERATE\" FROM \"main\".\"ELEMENTS\";");
			db.executeSQLThrow("DROP TABLE ELEMENTS;");
			db.executeSQLThrow("ALTER TABLE ___t1 RENAME TO ELEMENTS");

			db.executeSQLThrow("CREATE TABLE \"___t2\" ( \t\"LOCALID\"\tINTEGER, \t\"SEASONID\"\tINTEGER NOT NULL, \t\"EPISODE\"\tSMALLINT NOT NULL, \t\"NAME\"\tVARCHAR(128) NOT NULL, \t\"VIEWED_HISTORY\"\tVARCHAR(4096) NOT NULL, \t\"LENGTH\"\tINTEGER NOT NULL, \t\"FORMAT\"\tTINYINT NOT NULL, \t\"FILESIZE\"\tBIGINT NOT NULL, \t\"PART1\"\tVARCHAR(512) NOT NULL, \t\"ADDDATE\"\tDATE NOT NULL, \t\"TAGS\"\tSMALLINT NOT NULL, \t\"LANGUAGE\"\tBIGINT, \t\"MEDIAINFO.FILESIZE\"\tBIGINT, \t\"MEDIAINFO.CDATE\"\tBIGINT, \t\"MEDIAINFO.MDATE\"\tBIGINT, \t\"MEDIAINFO.AFORMAT\"\tVARCHAR(128), \t\"MEDIAINFO.VFORMAT\"\tVARCHAR(128), \t\"MEDIAINFO.WIDTH\"\tSMALLINT, \t\"MEDIAINFO.HEIGHT\"\tSMALLINT, \t\"MEDIAINFO.FRAMERATE\"\tREAL, \t\"MEDIAINFO.DURATION\"\tREAL, \t\"MEDIAINFO.BITDEPTH\"\tTINYINT, \t\"MEDIAINFO.BITRATE\"\tINTEGER, \t\"MEDIAINFO.FRAMECOUNT\"\tINTEGER, \t\"MEDIAINFO.ACHANNELS\"\tINTEGER, \t\"MEDIAINFO.VCODEC\"\tVARCHAR(128), \t\"MEDIAINFO.ACODEC\"\tVARCHAR(128), \t\"MEDIAINFO.SAMPLERATE\"\tINTEGER, \tPRIMARY KEY(\"LOCALID\"), \tFOREIGN KEY(\"SEASONID\") REFERENCES \"SEASONS\"(\"SEASONID\") );");
			db.executeSQLThrow("INSERT INTO ___t2 SELECT \"LOCALID\",\"SEASONID\",\"EPISODE\",\"NAME\",\"VIEWED_HISTORY\",\"LENGTH\",\"FORMAT\",\"FILESIZE\",\"PART1\",\"ADDDATE\",\"TAGS\",\"LANGUAGE\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.SAMPLERATE\" FROM \"main\".\"EPISODES\";");
			db.executeSQLThrow("DROP TABLE EPISODES;");
			db.executeSQLThrow("ALTER TABLE ___t2 RENAME TO EPISODES");

			if (db.querySingleStringSQL("SELECT IVALUE FROM INFO WHERE IKEY = 'HISTORY_ENABLED'", 0).equals("1"))
			{
				for (var trigger : CCDatabaseHistory.createTriggerStatements()) db.executeSQLThrow(trigger.Item2);
			}

		}
		db.executeSQLThrow("PRAGMA foreign_keys = ON;");
		db.executeSQLThrow("COMMIT TRANSACTION");

		db.executeSQLThrow("VACUUM");
	}

	@SuppressWarnings("nls")
	private void upgrade_16_17() throws SQLException
	{
		CCLog.addInformation("[UPGRADE v16 -> v17] Remove column Main.SeriesID");
		CCLog.addInformation("[UPGRADE v16 -> v17] Update Season.SeriesID to new values");

		db.executeSQLThrow("BEGIN TRANSACTION");
		db.executeSQLThrow("PRAGMA foreign_keys = OFF;");
		{
			for (String trigger : CCStreams.iterate(db.listTrigger()).filter(t -> t.startsWith("JCCTRIGGER_")))
			{
				db.executeSQLThrow("DROP TRIGGER ["+trigger+"]");
			}

			for (var d : db.querySQL("SELECT SEASONID, SERIESID FROM SEASONS", 2))
			{
				int seasonid = (Integer)d[0];
				int seriesid = (Integer)d[1];
				int localid  = db.querySingleIntSQLThrow("SELECT LOCALID FROM ELEMENTS WHERE SERIESID = " + seriesid, 0);

				db.executeSQLThrow("UPDATE SEASONS SET SERIESID = " + localid + " WHERE SEASONID = " + seasonid);
			}

			db.executeSQLThrow("CREATE TABLE \"___t1\" ( \"LOCALID\" INTEGER, \"NAME\" VARCHAR(128) NOT NULL, \"VIEWED_HISTORY\" VARCHAR(4096) NOT NULL, \"ZYKLUS\" VARCHAR(128) NOT NULL, \"ZYKLUSNUMBER\" INTEGER NOT NULL, \"LANGUAGE\" BIGINT NOT NULL, \"GENRE\" BIGINT NOT NULL, \"LENGTH\" INTEGER NOT NULL, \"ADDDATE\" DATE NOT NULL, \"ONLINESCORE\" TINYINT NOT NULL, \"FSK\" TINYINT NOT NULL, \"FORMAT\" TINYINT NOT NULL, \"MOVIEYEAR\" SMALLINT NOT NULL, \"ONLINEREF\" VARCHAR(128) NOT NULL, \"GROUPS\" VARCHAR(4096) NOT NULL, \"FILESIZE\" BIGINT NOT NULL, \"TAGS\" SMALLINT NOT NULL, \"PART1\" VARCHAR(512) NOT NULL, \"PART2\" VARCHAR(512) NOT NULL, \"PART3\" VARCHAR(512) NOT NULL, \"PART4\" VARCHAR(512) NOT NULL, \"PART5\" VARCHAR(512) NOT NULL, \"PART6\" VARCHAR(512) NOT NULL, \"SCORE\" TINYINT NOT NULL, \"COVERID\" INTEGER(256) NOT NULL, \"TYPE\" TINYINT NOT NULL, \"MEDIAINFO.FILESIZE\" BIGINT, \"MEDIAINFO.CDATE\" BIGINT, \"MEDIAINFO.MDATE\" BIGINT, \"MEDIAINFO.AFORMAT\" VARCHAR(128), \"MEDIAINFO.VFORMAT\" VARCHAR(128), \"MEDIAINFO.WIDTH\" SMALLINT, \"MEDIAINFO.HEIGHT\" SMALLINT, \"MEDIAINFO.FRAMERATE\" REAL, \"MEDIAINFO.DURATION\" REAL, \"MEDIAINFO.BITDEPTH\" TINYINT, \"MEDIAINFO.BITRATE\" INTEGER, \"MEDIAINFO.FRAMECOUNT\" INTEGER, \"MEDIAINFO.ACHANNELS\" INTEGER, \"MEDIAINFO.VCODEC\" VARCHAR(128), \"MEDIAINFO.ACODEC\" VARCHAR(128), \"MEDIAINFO.SAMPLERATE\" INTEGER, PRIMARY KEY(\"LOCALID\") );");
			db.executeSQLThrow("INSERT INTO \"main\".\"___t1\" SELECT \"LOCALID\",\"NAME\",\"VIEWED_HISTORY\",\"ZYKLUS\",\"ZYKLUSNUMBER\",\"LANGUAGE\",\"GENRE\",\"LENGTH\",\"ADDDATE\",\"ONLINESCORE\",\"FSK\",\"FORMAT\",\"MOVIEYEAR\",\"ONLINEREF\",\"GROUPS\",\"FILESIZE\",\"TAGS\",\"PART1\",\"PART2\",\"PART3\",\"PART4\",\"PART5\",\"PART6\",\"SCORE\",\"COVERID\",\"TYPE\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.SAMPLERATE\" FROM \"main\".\"ELEMENTS\";");
			db.executeSQLThrow("PRAGMA defer_foreign_keys = '1';");
			db.executeSQLThrow("DROP TABLE \"main\".\"ELEMENTS\";");
			db.executeSQLThrow("ALTER TABLE \"main\".\"___t1\" RENAME TO \"ELEMENTS\"");

			if (db.querySingleStringSQL("SELECT IVALUE FROM INFO WHERE IKEY = 'HISTORY_ENABLED'", 0).equals("1"))
			{
				for (var trigger : CCDatabaseHistory.createTriggerStatements()) db.executeSQLThrow(trigger.Item2);
			}
		}
		db.executeSQLThrow("PRAGMA foreign_keys = ON;");
		db.executeSQLThrow("COMMIT TRANSACTION");

		db.executeSQLThrow("VACUUM");
	}

	@SuppressWarnings("nls")
	private void upgrade_17_18() throws SQLException
	{
		CCLog.addInformation("[UPGRADE v17 -> v18] Add MEDIAINFO.CHECKSUM column to MAIN");
		CCLog.addInformation("[UPGRADE v17 -> v18] Add MEDIAINFO.CHECKSUM column to EPISODES");

		db.executeSQLThrow("BEGIN TRANSACTION");
		db.executeSQLThrow("PRAGMA foreign_keys = OFF;");
		{
			for (String trigger : CCStreams.iterate(db.listTrigger()).filter(t -> t.startsWith("JCCTRIGGER_")))
			{
				db.executeSQLThrow("DROP TRIGGER ["+trigger+"]");
			}

			db.executeSQLThrow("CREATE TABLE \"___t1\" (   \"LOCALID\" INTEGER,   \"NAME\" VARCHAR(128) NOT NULL,   \"VIEWED_HISTORY\" VARCHAR(4096) NOT NULL,   \"ZYKLUS\" VARCHAR(128) NOT NULL,   \"ZYKLUSNUMBER\" INTEGER NOT NULL,   \"LANGUAGE\" BIGINT NOT NULL,   \"GENRE\" BIGINT NOT NULL,   \"LENGTH\" INTEGER NOT NULL,   \"ADDDATE\" DATE NOT NULL,   \"ONLINESCORE\" TINYINT NOT NULL,   \"FSK\" TINYINT NOT NULL,   \"FORMAT\" TINYINT NOT NULL,   \"MOVIEYEAR\" SMALLINT NOT NULL,   \"ONLINEREF\" VARCHAR(128) NOT NULL,   \"GROUPS\" VARCHAR(4096) NOT NULL,   \"FILESIZE\" BIGINT NOT NULL,   \"TAGS\" SMALLINT NOT NULL,   \"PART1\" VARCHAR(512) NOT NULL,   \"PART2\" VARCHAR(512) NOT NULL,   \"PART3\" VARCHAR(512) NOT NULL,   \"PART4\" VARCHAR(512) NOT NULL,   \"PART5\" VARCHAR(512) NOT NULL,   \"PART6\" VARCHAR(512) NOT NULL,   \"SCORE\" TINYINT NOT NULL,   \"COVERID\" INTEGER(256) NOT NULL,   \"TYPE\" TINYINT NOT NULL,   \"MEDIAINFO.FILESIZE\" BIGINT,   \"MEDIAINFO.CDATE\" BIGINT,   \"MEDIAINFO.MDATE\" BIGINT,   \"MEDIAINFO.AFORMAT\" VARCHAR(128),   \"MEDIAINFO.VFORMAT\" VARCHAR(128),   \"MEDIAINFO.WIDTH\" SMALLINT,   \"MEDIAINFO.HEIGHT\" SMALLINT,   \"MEDIAINFO.FRAMERATE\" REAL,   \"MEDIAINFO.DURATION\" REAL,   \"MEDIAINFO.BITDEPTH\" TINYINT,   \"MEDIAINFO.BITRATE\" INTEGER,   \"MEDIAINFO.FRAMECOUNT\" INTEGER,   \"MEDIAINFO.ACHANNELS\" INTEGER,   \"MEDIAINFO.VCODEC\" VARCHAR(128),   \"MEDIAINFO.ACODEC\" VARCHAR(128),   \"MEDIAINFO.SAMPLERATE\" INTEGER,   \"MEDIAINFO.CHECKSUM\" VARCHAR(1024),   PRIMARY KEY(\"LOCALID\")  )");
			db.executeSQLThrow("INSERT INTO \"___t1\" (\"ADDDATE\",\"COVERID\",\"FILESIZE\",\"FORMAT\",\"FSK\",\"GENRE\",\"GROUPS\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"MOVIEYEAR\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE\",\"PART1\",\"PART2\",\"PART3\",\"PART4\",\"PART5\",\"PART6\",\"SCORE\",\"TAGS\",\"TYPE\",\"VIEWED_HISTORY\",\"ZYKLUS\",\"ZYKLUSNUMBER\") SELECT \"ADDDATE\",\"COVERID\",\"FILESIZE\",\"FORMAT\",\"FSK\",\"GENRE\",\"GROUPS\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"MOVIEYEAR\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE\",\"PART1\",\"PART2\",\"PART3\",\"PART4\",\"PART5\",\"PART6\",\"SCORE\",\"TAGS\",\"TYPE\",\"VIEWED_HISTORY\",\"ZYKLUS\",\"ZYKLUSNUMBER\" FROM \"ELEMENTS\"");
			db.executeSQLThrow("DROP TABLE \"ELEMENTS\";");
			db.executeSQLThrow("ALTER TABLE \"___t1\" RENAME TO \"ELEMENTS\"");

			db.executeSQLThrow("CREATE TABLE \"___t2\" ( \"LOCALID\" INTEGER, \"SEASONID\" INTEGER NOT NULL, \"EPISODE\" SMALLINT NOT NULL, \"NAME\" VARCHAR(128) NOT NULL, \"VIEWED_HISTORY\" VARCHAR(4096) NOT NULL, \"LENGTH\" INTEGER NOT NULL, \"FORMAT\" TINYINT NOT NULL, \"FILESIZE\" BIGINT NOT NULL, \"PART1\" VARCHAR(512) NOT NULL, \"ADDDATE\" DATE NOT NULL, \"TAGS\" SMALLINT NOT NULL, \"LANGUAGE\" BIGINT, \"MEDIAINFO.FILESIZE\" BIGINT, \"MEDIAINFO.CDATE\" BIGINT, \"MEDIAINFO.MDATE\" BIGINT, \"MEDIAINFO.AFORMAT\" VARCHAR(128), \"MEDIAINFO.VFORMAT\" VARCHAR(128), \"MEDIAINFO.WIDTH\" SMALLINT, \"MEDIAINFO.HEIGHT\" SMALLINT, \"MEDIAINFO.FRAMERATE\" REAL, \"MEDIAINFO.DURATION\" REAL, \"MEDIAINFO.BITDEPTH\" TINYINT, \"MEDIAINFO.BITRATE\" INTEGER, \"MEDIAINFO.FRAMECOUNT\" INTEGER, \"MEDIAINFO.ACHANNELS\" INTEGER, \"MEDIAINFO.VCODEC\" VARCHAR(128), \"MEDIAINFO.ACODEC\" VARCHAR(128), \"MEDIAINFO.SAMPLERATE\" INTEGER, \"MEDIAINFO.CHECKSUM\" VARCHAR(1024), PRIMARY KEY(\"LOCALID\"), FOREIGN KEY(\"SEASONID\") REFERENCES \"SEASONS\"(\"SEASONID\") );\n");
			db.executeSQLThrow("INSERT INTO \"___t2\" (\"ADDDATE\",\"EPISODE\",\"FILESIZE\",\"FORMAT\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"NAME\",\"PART1\",\"SEASONID\",\"TAGS\",\"VIEWED_HISTORY\") SELECT \"ADDDATE\",\"EPISODE\",\"FILESIZE\",\"FORMAT\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"NAME\",\"PART1\",\"SEASONID\",\"TAGS\",\"VIEWED_HISTORY\" FROM \"main\".\"EPISODES\"");
			db.executeSQLThrow("DROP TABLE \"EPISODES\";");
			db.executeSQLThrow("ALTER TABLE \"___t2\" RENAME TO \"EPISODES\"");

			if (db.querySingleStringSQL("SELECT IVALUE FROM INFO WHERE IKEY = 'HISTORY_ENABLED'", 0).equals("1"))
			{
				for (var trigger : CCDatabaseHistory.createTriggerStatements()) db.executeSQLThrow(trigger.Item2);
			}
		}
		db.executeSQLThrow("PRAGMA foreign_keys = ON;");
		db.executeSQLThrow("COMMIT TRANSACTION");

		db.executeSQLThrow("VACUUM");
	}

	@SuppressWarnings("nls")
	private void upgrade_18_19() throws Exception
	{
		CCLog.addInformation("[UPGRADE v18 -> v19] Split table ELEMENTS into MOVIES and SERIES");
		CCLog.addInformation("[UPGRADE v18 -> v19] Fix HISTORY.TABLE column in history after ELEMENTS split");
		CCLog.addInformation("[UPGRADE v18 -> v19] Rename SEASONS.SEASONID to SEASONS.LOCALID");

		if (db.getDBType() != CCDatabaseDriver.SQLITE) throw new Exception("Unsupported DB type"); // from v19 onwards only sqlite is supported

		db.executeSQLThrow("BEGIN TRANSACTION");
		db.executeSQLThrow("PRAGMA foreign_keys = OFF;");
		{
			for (String trigger : CCStreams.iterate(db.listTrigger()).filter(t -> t.startsWith("JCCTRIGGER_"))) db.executeSQLThrow("DROP TRIGGER ["+trigger+"]");

			db.executeSQLThrow("CREATE TABLE MOVIES(LOCALID INTEGER PRIMARY KEY,[NAME] TEXT NOT NULL,VIEWED_HISTORY TEXT NOT NULL,ZYKLUS TEXT NOT NULL,ZYKLUSNUMBER INTEGER NOT NULL,[LANGUAGE] BIGINT NOT NULL,GENRE BIGINT NOT NULL,LENGTH INTEGER NOT NULL,ADDDATE DATE NOT NULL,ONLINESCORE TINYINT NOT NULL,FSK TINYINT NOT NULL,FORMAT TINYINT NOT NULL,MOVIEYEAR SMALLINT NOT NULL,ONLINEREF TEXT NOT NULL,GROUPS TEXT NOT NULL,FILESIZE BIGINT NOT NULL,TAGS SMALLINT NOT NULL,PART1 TEXT NOT NULL,PART2 TEXT NOT NULL,PART3 TEXT NOT NULL,PART4 TEXT NOT NULL,PART5 TEXT NOT NULL,PART6 TEXT NOT NULL,SCORE TINYINT NOT NULL,COVERID INTEGER NOT NULL,[MEDIAINFO.FILESIZE] BIGINT,[MEDIAINFO.CDATE] BIGINT,[MEDIAINFO.MDATE] BIGINT,[MEDIAINFO.AFORMAT] TEXT,[MEDIAINFO.VFORMAT] TEXT,[MEDIAINFO.WIDTH] SMALLINT,[MEDIAINFO.HEIGHT] SMALLINT,[MEDIAINFO.FRAMERATE] REAL,[MEDIAINFO.DURATION] REAL,[MEDIAINFO.BITDEPTH] TINYINT,[MEDIAINFO.BITRATE] INTEGER,[MEDIAINFO.FRAMECOUNT] INTEGER,[MEDIAINFO.ACHANNELS] INTEGER,[MEDIAINFO.VCODEC] TEXT,[MEDIAINFO.ACODEC] TEXT,[MEDIAINFO.SAMPLERATE] INTEGER,[MEDIAINFO.CHECKSUM] TEXT)");
			db.executeSQLThrow("CREATE TABLE SERIES(LOCALID INTEGER PRIMARY KEY,[NAME] TEXT NOT NULL,GENRE BIGINT NOT NULL,ONLINESCORE TINYINT NOT NULL,FSK TINYINT NOT NULL,ONLINEREF TEXT NOT NULL,GROUPS TEXT NOT NULL,SCORE TINYINT NOT NULL,COVERID INTEGER NOT NULL,TAGS SMALLINT NOT NULL)");

			db.executeSQLThrow("INSERT INTO SERIES (LOCALID, [NAME], GENRE, ONLINESCORE, FSK, ONLINEREF, [GROUPS], SCORE, COVERID, TAGS) SELECT LOCALID, [NAME], GENRE, ONLINESCORE, FSK, ONLINEREF, [GROUPS], SCORE, COVERID, TAGS FROM ELEMENTS WHERE ELEMENTS.TYPE = 1");
			db.executeSQLThrow("INSERT INTO MOVIES (LOCALID, [NAME], VIEWED_HISTORY, ZYKLUS, ZYKLUSNUMBER, [LANGUAGE], GENRE, LENGTH, ADDDATE, ONLINESCORE, FSK, FORMAT, MOVIEYEAR, ONLINEREF, GROUPS, FILESIZE, TAGS, PART1, PART2, PART3, PART4, PART5, PART6, SCORE, COVERID, [MEDIAINFO.FILESIZE], [MEDIAINFO.CDATE], [MEDIAINFO.MDATE], [MEDIAINFO.AFORMAT], [MEDIAINFO.VFORMAT], [MEDIAINFO.WIDTH], [MEDIAINFO.HEIGHT], [MEDIAINFO.FRAMERATE], [MEDIAINFO.DURATION], [MEDIAINFO.BITDEPTH], [MEDIAINFO.BITRATE], [MEDIAINFO.FRAMECOUNT], [MEDIAINFO.ACHANNELS], [MEDIAINFO.VCODEC], [MEDIAINFO.ACODEC], [MEDIAINFO.SAMPLERATE], [MEDIAINFO.CHECKSUM]) SELECT LOCALID, [NAME], VIEWED_HISTORY, ZYKLUS, ZYKLUSNUMBER, [LANGUAGE], GENRE, LENGTH, ADDDATE, ONLINESCORE, FSK, FORMAT, MOVIEYEAR, ONLINEREF, GROUPS, FILESIZE, TAGS, PART1, PART2, PART3, PART4, PART5, PART6, SCORE, COVERID, [MEDIAINFO.FILESIZE], [MEDIAINFO.CDATE], [MEDIAINFO.MDATE], [MEDIAINFO.AFORMAT], [MEDIAINFO.VFORMAT], [MEDIAINFO.WIDTH], [MEDIAINFO.HEIGHT], [MEDIAINFO.FRAMERATE], [MEDIAINFO.DURATION], [MEDIAINFO.BITDEPTH], [MEDIAINFO.BITRATE], [MEDIAINFO.FRAMECOUNT], [MEDIAINFO.ACHANNELS], [MEDIAINFO.VCODEC], [MEDIAINFO.ACODEC], [MEDIAINFO.SAMPLERATE], [MEDIAINFO.CHECKSUM] FROM ELEMENTS WHERE ELEMENTS.TYPE = 0");

			var histids = db.querySQL("SELECT [ID] FROM [HISTORY] WHERE [TABLE]='ELEMENTS' GROUP BY [ID]", 1, o -> (String)o[0]);
			for (var hid : histids)
			{
				var type1 = db.querySingleSQL("SELECT [TYPE] FROM [ELEMENTS] WHERE [LOCALID]="+hid, 0);
				if (type1 != null)
				{
					if      (((int)type1) == 0) db.executeSQLThrow("UPDATE [HISTORY] SET [TABLE]='MOVIES' WHERE [ID]='"+hid+"'");
					else if (((int)type1) == 1) db.executeSQLThrow("UPDATE [HISTORY] SET [TABLE]='SERIES' WHERE [ID]='"+hid+"'");
					else throw new Exception("Unknown db-elem-type");
					continue;
				}

				var type2 = db.querySingleSQL("SELECT [NEW] FROM [HISTORY] WHERE [ID]='"+hid+"' AND [NEW] IS NOT NULL AND [FIELD]='TYPE' AND [TABLE]='ELEMENTS' ORDER BY [DATE] DESC LIMIT 1", 0);
				if (type2 != null)
				{
					if      (Str.equals((String) type2, "0")) db.executeSQLThrow("UPDATE [HISTORY] SET [TABLE]='MOVIES' WHERE [ID]='"+hid+"'");
					else if (Str.equals((String) type2, "1")) db.executeSQLThrow("UPDATE [HISTORY] SET [TABLE]='SERIES' WHERE [ID]='"+hid+"'");
					else throw new Exception("Unknown db-elem-type");
					continue;
				}

				var type3 = db.querySingleSQL("SELECT [OLD] FROM [HISTORY] WHERE [ID]='"+hid+"' AND [OLD] IS NOT NULL AND [FIELD]='TYPE' AND [TABLE]='ELEMENTS' ORDER BY [DATE] DESC LIMIT 1", 0);
				if (type3 == null)
					throw new Exception("Could not get type of history-entry '"+hid+"'");

				if      (Str.equals((String) type3, "0")) db.executeSQLThrow("UPDATE [HISTORY] SET [TABLE]='MOVIES' WHERE [ID]='"+hid+"'");
				else if (Str.equals((String) type3, "1")) db.executeSQLThrow("UPDATE [HISTORY] SET [TABLE]='SERIES' WHERE [ID]='"+hid+"'");
				else throw new Exception("Unknown db-elem-type");
			}

			db.executeSQLThrow("DROP TABLE ELEMENTS");

			db.executeSQLThrow("CREATE TABLE _SEASONS(LOCALID INTEGER PRIMARY KEY,SERIESID INTEGER NOT NULL,[NAME] TEXT NOT NULL,SEASONYEAR SMALLINT NOT NULL,COVERID INTEGER NOT NULL,FOREIGN KEY(SERIESID) REFERENCES SERIES(LOCALID))");
			db.executeSQLThrow("INSERT INTO _SEASONS (LOCALID,SERIESID,NAME,SEASONYEAR,COVERID) SELECT SEASONID,SERIESID,NAME,SEASONYEAR,COVERID FROM SEASONS");
			db.executeSQLThrow("DROP TABLE SEASONS");
			db.executeSQLThrow("ALTER TABLE _SEASONS RENAME TO SEASONS");

			db.executeSQLThrow("CREATE TABLE _EPISODES(LOCALID INTEGER PRIMARY KEY,SEASONID INTEGER NOT NULL,EPISODE SMALLINT NOT NULL,[NAME] TEXT NOT NULL,VIEWED_HISTORY TEXT NOT NULL,LENGTH INTEGER NOT NULL,FORMAT TINYINT NOT NULL,FILESIZE BIGINT NOT NULL,PART1 TEXT NOT NULL,TAGS SMALLINT NOT NULL,ADDDATE DATE NOT NULL,[LANGUAGE] BIGINT NOT NULL,[MEDIAINFO.FILESIZE] BIGINT,[MEDIAINFO.CDATE] BIGINT,[MEDIAINFO.MDATE] BIGINT,[MEDIAINFO.AFORMAT] TEXT,[MEDIAINFO.VFORMAT] TEXT,[MEDIAINFO.WIDTH] SMALLINT,[MEDIAINFO.HEIGHT] SMALLINT,[MEDIAINFO.FRAMERATE] REAL,[MEDIAINFO.DURATION] REAL,[MEDIAINFO.BITDEPTH] TINYINT,[MEDIAINFO.BITRATE] INTEGER,[MEDIAINFO.FRAMECOUNT] INTEGER,[MEDIAINFO.ACHANNELS] INTEGER,[MEDIAINFO.VCODEC] TEXT,[MEDIAINFO.ACODEC] TEXT,[MEDIAINFO.SAMPLERATE] INTEGER,[MEDIAINFO.CHECKSUM] TEXT,FOREIGN KEY(SEASONID) REFERENCES SEASONS(LOCALID))");
			db.executeSQLThrow("INSERT INTO _EPISODES (LOCALID,SEASONID,EPISODE,[NAME],VIEWED_HISTORY,LENGTH,FORMAT,FILESIZE,PART1,TAGS,ADDDATE,[LANGUAGE],[MEDIAINFO.FILESIZE],[MEDIAINFO.CDATE],[MEDIAINFO.MDATE],[MEDIAINFO.AFORMAT],[MEDIAINFO.VFORMAT],[MEDIAINFO.WIDTH],[MEDIAINFO.HEIGHT],[MEDIAINFO.FRAMERATE],[MEDIAINFO.DURATION],[MEDIAINFO.BITDEPTH],[MEDIAINFO.BITRATE],[MEDIAINFO.FRAMECOUNT],[MEDIAINFO.ACHANNELS],[MEDIAINFO.VCODEC],[MEDIAINFO.ACODEC],[MEDIAINFO.SAMPLERATE],[MEDIAINFO.CHECKSUM]) SELECT LOCALID,SEASONID,EPISODE,[NAME],VIEWED_HISTORY,LENGTH,FORMAT,FILESIZE,PART1,TAGS,ADDDATE,[LANGUAGE],[MEDIAINFO.FILESIZE],[MEDIAINFO.CDATE],[MEDIAINFO.MDATE],[MEDIAINFO.AFORMAT],[MEDIAINFO.VFORMAT],[MEDIAINFO.WIDTH],[MEDIAINFO.HEIGHT],[MEDIAINFO.FRAMERATE],[MEDIAINFO.DURATION],[MEDIAINFO.BITDEPTH],[MEDIAINFO.BITRATE],[MEDIAINFO.FRAMECOUNT],[MEDIAINFO.ACHANNELS],[MEDIAINFO.VCODEC],[MEDIAINFO.ACODEC],[MEDIAINFO.SAMPLERATE],[MEDIAINFO.CHECKSUM] FROM EPISODES");
			db.executeSQLThrow("DROP TABLE EPISODES");
			db.executeSQLThrow("ALTER TABLE _EPISODES RENAME TO EPISODES");


			if (db.querySingleStringSQL("SELECT IVALUE FROM INFO WHERE IKEY = 'HISTORY_ENABLED'", 0).equals("1"))
			{
				for (var trigger : CCDatabaseHistory.createTriggerStatements()) db.executeSQLThrow(trigger.Item2);
			}
		}
		db.executeSQLThrow("PRAGMA foreign_keys = ON;");
		db.executeSQLThrow("COMMIT TRANSACTION");

		db.executeSQLThrow("VACUUM");
	}

	@SuppressWarnings("nls")
	private void upgrade_19_20() throws SQLException
	{
		CCLog.addInformation("[UPGRADE v19 -> v20] Add SUBTITLES column to MOVIES");
		CCLog.addInformation("[UPGRADE v19 -> v20] Add SUBTITLES column to EPISODES");

		db.executeSQLThrow("BEGIN TRANSACTION");
		db.executeSQLThrow("PRAGMA foreign_keys = OFF;");
		{
			for (String trigger : CCStreams.iterate(db.listTrigger()).filter(t -> t.startsWith("JCCTRIGGER_")))
			{
				db.executeSQLThrow("DROP TRIGGER ["+trigger+"]");
			}

			db.executeSQLThrow("CREATE TABLE \"sqlb_temp_table_1\" (  \"LOCALID\" INTEGER,  \"NAME\" TEXT NOT NULL,  \"VIEWED_HISTORY\" TEXT NOT NULL,  \"ZYKLUS\" TEXT NOT NULL,  \"ZYKLUSNUMBER\" INTEGER NOT NULL,  \"LANGUAGE\" BIGINT NOT NULL,  \"SUBTITLES\" TEXT NOT NULL DEFAULT \"\",  \"GENRE\" BIGINT NOT NULL,  \"LENGTH\" INTEGER NOT NULL,  \"ADDDATE\" DATE NOT NULL,  \"ONLINESCORE\" TINYINT NOT NULL,  \"FSK\" TINYINT NOT NULL,  \"FORMAT\" TINYINT NOT NULL,  \"MOVIEYEAR\" SMALLINT NOT NULL,  \"ONLINEREF\" TEXT NOT NULL,  \"GROUPS\" TEXT NOT NULL,  \"FILESIZE\" BIGINT NOT NULL,  \"TAGS\" SMALLINT NOT NULL,  \"PART1\" TEXT NOT NULL,  \"PART2\" TEXT NOT NULL,  \"PART3\" TEXT NOT NULL,  \"PART4\" TEXT NOT NULL,  \"PART5\" TEXT NOT NULL,  \"PART6\" TEXT NOT NULL,  \"SCORE\" TINYINT NOT NULL,  \"COVERID\" INTEGER NOT NULL,  \"MEDIAINFO.FILESIZE\" BIGINT,  \"MEDIAINFO.CDATE\" BIGINT,  \"MEDIAINFO.MDATE\" BIGINT,  \"MEDIAINFO.AFORMAT\" TEXT,  \"MEDIAINFO.VFORMAT\" TEXT,  \"MEDIAINFO.WIDTH\" SMALLINT,  \"MEDIAINFO.HEIGHT\" SMALLINT,  \"MEDIAINFO.FRAMERATE\" REAL,  \"MEDIAINFO.DURATION\" REAL,  \"MEDIAINFO.BITDEPTH\" TINYINT,  \"MEDIAINFO.BITRATE\" INTEGER,  \"MEDIAINFO.FRAMECOUNT\" INTEGER,  \"MEDIAINFO.ACHANNELS\" INTEGER,  \"MEDIAINFO.VCODEC\" TEXT,  \"MEDIAINFO.ACODEC\" TEXT,  \"MEDIAINFO.SAMPLERATE\" INTEGER,  \"MEDIAINFO.CHECKSUM\" TEXT,  PRIMARY KEY(\"LOCALID\") );");
			db.executeSQLThrow("INSERT INTO \"main\".\"sqlb_temp_table_1\" (\"ADDDATE\",\"COVERID\",\"FILESIZE\",\"FORMAT\",\"FSK\",\"GENRE\",\"GROUPS\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.CHECKSUM\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"MOVIEYEAR\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE\",\"PART1\",\"PART2\",\"PART3\",\"PART4\",\"PART5\",\"PART6\",\"SCORE\",\"TAGS\",\"VIEWED_HISTORY\",\"ZYKLUS\",\"ZYKLUSNUMBER\") SELECT \"ADDDATE\",\"COVERID\",\"FILESIZE\",\"FORMAT\",\"FSK\",\"GENRE\",\"GROUPS\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.CHECKSUM\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"MOVIEYEAR\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE\",\"PART1\",\"PART2\",\"PART3\",\"PART4\",\"PART5\",\"PART6\",\"SCORE\",\"TAGS\",\"VIEWED_HISTORY\",\"ZYKLUS\",\"ZYKLUSNUMBER\" FROM \"main\".\"MOVIES\"");
			db.executeSQLThrow("DROP TABLE \"main\".\"MOVIES\"");
			db.executeSQLThrow("ALTER TABLE \"main\".\"sqlb_temp_table_1\" RENAME TO \"MOVIES\"");

			db.executeSQLThrow("CREATE TABLE \"sqlb_temp_table_2\" ( \"LOCALID\" INTEGER, \"SEASONID\" INTEGER NOT NULL, \"EPISODE\" SMALLINT NOT NULL, \"NAME\" TEXT NOT NULL, \"VIEWED_HISTORY\" TEXT NOT NULL, \"LENGTH\" INTEGER NOT NULL, \"FORMAT\" TINYINT NOT NULL, \"FILESIZE\" BIGINT NOT NULL, \"PART1\" TEXT NOT NULL, \"TAGS\" SMALLINT NOT NULL, \"ADDDATE\" DATE NOT NULL, \"LANGUAGE\" BIGINT NOT NULL, \"SUBTITLES\" TEXT NOT NULL DEFAULT \"\", \"MEDIAINFO.FILESIZE\" BIGINT, \"MEDIAINFO.CDATE\" BIGINT, \"MEDIAINFO.MDATE\" BIGINT, \"MEDIAINFO.AFORMAT\" TEXT, \"MEDIAINFO.VFORMAT\" TEXT, \"MEDIAINFO.WIDTH\" SMALLINT, \"MEDIAINFO.HEIGHT\" SMALLINT, \"MEDIAINFO.FRAMERATE\" REAL, \"MEDIAINFO.DURATION\" REAL, \"MEDIAINFO.BITDEPTH\" TINYINT, \"MEDIAINFO.BITRATE\" INTEGER, \"MEDIAINFO.FRAMECOUNT\" INTEGER, \"MEDIAINFO.ACHANNELS\" INTEGER, \"MEDIAINFO.VCODEC\" TEXT, \"MEDIAINFO.ACODEC\" TEXT, \"MEDIAINFO.SAMPLERATE\" INTEGER, \"MEDIAINFO.CHECKSUM\" TEXT, PRIMARY KEY(\"LOCALID\"), FOREIGN KEY(\"SEASONID\") REFERENCES \"SEASONS\"(\"LOCALID\") );");
			db.executeSQLThrow("INSERT INTO \"main\".\"sqlb_temp_table_2\" (\"ADDDATE\",\"EPISODE\",\"FILESIZE\",\"FORMAT\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.CHECKSUM\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"NAME\",\"PART1\",\"SEASONID\",\"TAGS\",\"VIEWED_HISTORY\") SELECT \"ADDDATE\",\"EPISODE\",\"FILESIZE\",\"FORMAT\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.CHECKSUM\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"NAME\",\"PART1\",\"SEASONID\",\"TAGS\",\"VIEWED_HISTORY\" FROM \"main\".\"EPISODES\"");
			db.executeSQLThrow("DROP TABLE \"main\".\"EPISODES\"");
			db.executeSQLThrow("ALTER TABLE \"main\".\"sqlb_temp_table_2\" RENAME TO \"EPISODES\"");

			if (db.querySingleStringSQL("SELECT IVALUE FROM INFO WHERE IKEY = 'HISTORY_ENABLED'", 0).equals("1"))
			{
				for (var trigger : CCDatabaseHistory.createTriggerStatements()) db.executeSQLThrow(trigger.Item2);
			}
		}
		db.executeSQLThrow("PRAGMA foreign_keys = ON;");
		db.executeSQLThrow("COMMIT TRANSACTION");

		db.executeSQLThrow("VACUUM");
	}

	@SuppressWarnings("nls")
	private void upgrade_20_21() throws SQLException
	{
		CCLog.addInformation("[UPGRADE v20 -> v21] Rename ONLINESCORE to ONLINESCORE_NUM in MOVIES");
		CCLog.addInformation("[UPGRADE v20 -> v21] Add ONLINESCORE_DENOM column to MOVIES");
		CCLog.addInformation("[UPGRADE v20 -> v21] Rename ONLINESCORE to ONLINESCORE_NUM in SERIES");
		CCLog.addInformation("[UPGRADE v20 -> v21] Add ONLINESCORE_DENOM column to SERIES");

		db.executeSQLThrow("BEGIN TRANSACTION");
		db.executeSQLThrow("PRAGMA foreign_keys = OFF;");
		{
			for (String trigger : CCStreams.iterate(db.listTrigger()).filter(t -> t.startsWith("JCCTRIGGER_")))
			{
				db.executeSQLThrow("DROP TRIGGER ["+trigger+"]");
			}

			db.executeSQLThrow("ALTER TABLE \"MOVIES\" RENAME COLUMN \"ONLINESCORE\" TO \"ONLINESCORE_NUM\"");

			db.executeSQLThrow("CREATE TABLE \"sqlb_temp_table_1\" ( \"LOCALID\" INTEGER, \"NAME\" TEXT NOT NULL, \"VIEWED_HISTORY\" TEXT NOT NULL, \"ZYKLUS\" TEXT NOT NULL, \"ZYKLUSNUMBER\" INTEGER NOT NULL, \"LANGUAGE\" BIGINT NOT NULL, \"SUBTITLES\" TEXT NOT NULL, \"GENRE\" BIGINT NOT NULL, \"LENGTH\" INTEGER NOT NULL, \"ADDDATE\" DATE NOT NULL, \"ONLINESCORE_NUM\" SMALLINT NOT NULL, \"FSK\" TINYINT NOT NULL, \"FORMAT\" TINYINT NOT NULL, \"MOVIEYEAR\" SMALLINT NOT NULL, \"ONLINEREF\" TEXT NOT NULL, \"GROUPS\" TEXT NOT NULL, \"FILESIZE\" BIGINT NOT NULL, \"TAGS\" SMALLINT NOT NULL, \"PART1\" TEXT NOT NULL, \"PART2\" TEXT NOT NULL, \"PART3\" TEXT NOT NULL, \"PART4\" TEXT NOT NULL, \"PART5\" TEXT NOT NULL, \"PART6\" TEXT NOT NULL, \"SCORE\" TINYINT NOT NULL, \"COVERID\" INTEGER NOT NULL, \"MEDIAINFO.FILESIZE\" BIGINT, \"MEDIAINFO.CDATE\" BIGINT, \"MEDIAINFO.MDATE\" BIGINT, \"MEDIAINFO.AFORMAT\" TEXT, \"MEDIAINFO.VFORMAT\" TEXT, \"MEDIAINFO.WIDTH\" SMALLINT, \"MEDIAINFO.HEIGHT\" SMALLINT, \"MEDIAINFO.FRAMERATE\" REAL, \"MEDIAINFO.DURATION\" REAL, \"MEDIAINFO.BITDEPTH\" TINYINT, \"MEDIAINFO.BITRATE\" INTEGER, \"MEDIAINFO.FRAMECOUNT\" INTEGER, \"MEDIAINFO.ACHANNELS\" SMALLINT, \"MEDIAINFO.VCODEC\" TEXT, \"MEDIAINFO.ACODEC\" TEXT, \"MEDIAINFO.SAMPLERATE\" INTEGER, \"MEDIAINFO.CHECKSUM\" TEXT, PRIMARY KEY(\"LOCALID\") );");
			db.executeSQLThrow("INSERT INTO \"main\".\"sqlb_temp_table_1\" (\"ADDDATE\",\"COVERID\",\"FILESIZE\",\"FORMAT\",\"FSK\",\"GENRE\",\"GROUPS\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.CHECKSUM\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"MOVIEYEAR\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_NUM\",\"PART1\",\"PART2\",\"PART3\",\"PART4\",\"PART5\",\"PART6\",\"SCORE\",\"SUBTITLES\",\"TAGS\",\"VIEWED_HISTORY\",\"ZYKLUS\",\"ZYKLUSNUMBER\") SELECT \"ADDDATE\",\"COVERID\",\"FILESIZE\",\"FORMAT\",\"FSK\",\"GENRE\",\"GROUPS\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.CHECKSUM\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"MOVIEYEAR\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_NUM\",\"PART1\",\"PART2\",\"PART3\",\"PART4\",\"PART5\",\"PART6\",\"SCORE\",\"SUBTITLES\",\"TAGS\",\"VIEWED_HISTORY\",\"ZYKLUS\",\"ZYKLUSNUMBER\" FROM \"main\".\"MOVIES\"");
			db.executeSQLThrow("DROP TABLE \"main\".\"MOVIES\"");
			db.executeSQLThrow("ALTER TABLE \"main\".\"sqlb_temp_table_1\" RENAME TO \"MOVIES\"");

			db.executeSQLThrow("ALTER TABLE \"main\".\"MOVIES\" ADD COLUMN \t\"ONLINESCORE_DENOM\"\tSMALLINT NOT NULL DEFAULT 10");

			db.executeSQLThrow("CREATE TABLE \"sqlb_temp_table_2\" ( \"LOCALID\" INTEGER, \"NAME\" TEXT NOT NULL, \"VIEWED_HISTORY\" TEXT NOT NULL, \"ZYKLUS\" TEXT NOT NULL, \"ZYKLUSNUMBER\" INTEGER NOT NULL, \"LANGUAGE\" BIGINT NOT NULL, \"SUBTITLES\" TEXT NOT NULL, \"GENRE\" BIGINT NOT NULL, \"LENGTH\" INTEGER NOT NULL, \"ADDDATE\" DATE NOT NULL, \"ONLINESCORE_NUM\" SMALLINT NOT NULL, \"ONLINESCORE_DENOM\" SMALLINT NOT NULL DEFAULT 10, \"FSK\" TINYINT NOT NULL, \"FORMAT\" TINYINT NOT NULL, \"MOVIEYEAR\" SMALLINT NOT NULL, \"ONLINEREF\" TEXT NOT NULL, \"GROUPS\" TEXT NOT NULL, \"FILESIZE\" BIGINT NOT NULL, \"TAGS\" SMALLINT NOT NULL, \"PART1\" TEXT NOT NULL, \"PART2\" TEXT NOT NULL, \"PART3\" TEXT NOT NULL, \"PART4\" TEXT NOT NULL, \"PART5\" TEXT NOT NULL, \"PART6\" TEXT NOT NULL, \"SCORE\" TINYINT NOT NULL, \"COVERID\" INTEGER NOT NULL, \"MEDIAINFO.FILESIZE\" BIGINT, \"MEDIAINFO.CDATE\" BIGINT, \"MEDIAINFO.MDATE\" BIGINT, \"MEDIAINFO.AFORMAT\" TEXT, \"MEDIAINFO.VFORMAT\" TEXT, \"MEDIAINFO.WIDTH\" SMALLINT, \"MEDIAINFO.HEIGHT\" SMALLINT, \"MEDIAINFO.FRAMERATE\" REAL, \"MEDIAINFO.DURATION\" REAL, \"MEDIAINFO.BITDEPTH\" TINYINT, \"MEDIAINFO.BITRATE\" INTEGER, \"MEDIAINFO.FRAMECOUNT\" INTEGER, \"MEDIAINFO.ACHANNELS\" SMALLINT, \"MEDIAINFO.VCODEC\" TEXT, \"MEDIAINFO.ACODEC\" TEXT, \"MEDIAINFO.SAMPLERATE\" INTEGER, \"MEDIAINFO.CHECKSUM\" TEXT, PRIMARY KEY(\"LOCALID\") );");
			db.executeSQLThrow("INSERT INTO \"main\".\"sqlb_temp_table_2\" (\"ADDDATE\",\"COVERID\",\"FILESIZE\",\"FORMAT\",\"FSK\",\"GENRE\",\"GROUPS\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.CHECKSUM\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"MOVIEYEAR\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_NUM\",\"PART1\",\"PART2\",\"PART3\",\"PART4\",\"PART5\",\"PART6\",\"SCORE\",\"SUBTITLES\",\"TAGS\",\"VIEWED_HISTORY\",\"ZYKLUS\",\"ZYKLUSNUMBER\") SELECT \"ADDDATE\",\"COVERID\",\"FILESIZE\",\"FORMAT\",\"FSK\",\"GENRE\",\"GROUPS\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.CHECKSUM\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"MOVIEYEAR\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_NUM\",\"PART1\",\"PART2\",\"PART3\",\"PART4\",\"PART5\",\"PART6\",\"SCORE\",\"SUBTITLES\",\"TAGS\",\"VIEWED_HISTORY\",\"ZYKLUS\",\"ZYKLUSNUMBER\" FROM \"main\".\"MOVIES\"");
			db.executeSQLThrow("DROP TABLE \"main\".\"MOVIES\"");
			db.executeSQLThrow("ALTER TABLE \"main\".\"sqlb_temp_table_2\" RENAME TO \"MOVIES\"");

			db.executeSQLThrow("CREATE TABLE \"sqlb_temp_table_3\" ( \"LOCALID\" INTEGER, \"NAME\" TEXT NOT NULL, \"VIEWED_HISTORY\" TEXT NOT NULL, \"ZYKLUS\" TEXT NOT NULL, \"ZYKLUSNUMBER\" INTEGER NOT NULL, \"LANGUAGE\" BIGINT NOT NULL, \"SUBTITLES\" TEXT NOT NULL, \"GENRE\" BIGINT NOT NULL, \"LENGTH\" INTEGER NOT NULL, \"ADDDATE\" DATE NOT NULL, \"ONLINESCORE_NUM\" SMALLINT NOT NULL, \"ONLINESCORE_DENOM\" SMALLINT NOT NULL, \"FSK\" TINYINT NOT NULL, \"FORMAT\" TINYINT NOT NULL, \"MOVIEYEAR\" SMALLINT NOT NULL, \"ONLINEREF\" TEXT NOT NULL, \"GROUPS\" TEXT NOT NULL, \"FILESIZE\" BIGINT NOT NULL, \"TAGS\" SMALLINT NOT NULL, \"PART1\" TEXT NOT NULL, \"PART2\" TEXT NOT NULL, \"PART3\" TEXT NOT NULL, \"PART4\" TEXT NOT NULL, \"PART5\" TEXT NOT NULL, \"PART6\" TEXT NOT NULL, \"SCORE\" TINYINT NOT NULL, \"COVERID\" INTEGER NOT NULL, \"MEDIAINFO.FILESIZE\" BIGINT, \"MEDIAINFO.CDATE\" BIGINT, \"MEDIAINFO.MDATE\" BIGINT, \"MEDIAINFO.AFORMAT\" TEXT, \"MEDIAINFO.VFORMAT\" TEXT, \"MEDIAINFO.WIDTH\" SMALLINT, \"MEDIAINFO.HEIGHT\" SMALLINT, \"MEDIAINFO.FRAMERATE\" REAL, \"MEDIAINFO.DURATION\" REAL, \"MEDIAINFO.BITDEPTH\" TINYINT, \"MEDIAINFO.BITRATE\" INTEGER, \"MEDIAINFO.FRAMECOUNT\" INTEGER, \"MEDIAINFO.ACHANNELS\" SMALLINT, \"MEDIAINFO.VCODEC\" TEXT, \"MEDIAINFO.ACODEC\" TEXT, \"MEDIAINFO.SAMPLERATE\" INTEGER, \"MEDIAINFO.CHECKSUM\" TEXT, PRIMARY KEY(\"LOCALID\") );");
			db.executeSQLThrow("INSERT INTO \"main\".\"sqlb_temp_table_3\" (\"ADDDATE\",\"COVERID\",\"FILESIZE\",\"FORMAT\",\"FSK\",\"GENRE\",\"GROUPS\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.CHECKSUM\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"MOVIEYEAR\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_DENOM\",\"ONLINESCORE_NUM\",\"PART1\",\"PART2\",\"PART3\",\"PART4\",\"PART5\",\"PART6\",\"SCORE\",\"SUBTITLES\",\"TAGS\",\"VIEWED_HISTORY\",\"ZYKLUS\",\"ZYKLUSNUMBER\") SELECT \"ADDDATE\",\"COVERID\",\"FILESIZE\",\"FORMAT\",\"FSK\",\"GENRE\",\"GROUPS\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.CHECKSUM\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"MOVIEYEAR\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_DENOM\",\"ONLINESCORE_NUM\",\"PART1\",\"PART2\",\"PART3\",\"PART4\",\"PART5\",\"PART6\",\"SCORE\",\"SUBTITLES\",\"TAGS\",\"VIEWED_HISTORY\",\"ZYKLUS\",\"ZYKLUSNUMBER\" FROM \"main\".\"MOVIES\"");
			db.executeSQLThrow("DROP TABLE \"main\".\"MOVIES\"");
			db.executeSQLThrow("ALTER TABLE \"main\".\"sqlb_temp_table_3\" RENAME TO \"MOVIES\"");

			db.executeSQLThrow("ALTER TABLE \"main\".\"SERIES\" RENAME COLUMN \"ONLINESCORE\" TO \"ONLINESCORE_NUM\"");

			db.executeSQLThrow("CREATE TABLE \"sqlb_temp_table_4\" ( \"LOCALID\" INTEGER, \"NAME\" TEXT NOT NULL, \"GENRE\" BIGINT NOT NULL, \"ONLINESCORE_NUM\" TINYINT NOT NULL, \"FSK\" TINYINT NOT NULL, \"ONLINEREF\" TEXT NOT NULL, \"GROUPS\" TEXT NOT NULL, \"SCORE\" TINYINT NOT NULL, \"COVERID\" INTEGER NOT NULL, \"TAGS\" SMALLINT NOT NULL, PRIMARY KEY(\"LOCALID\") );");
			db.executeSQLThrow("INSERT INTO \"main\".\"sqlb_temp_table_4\" (\"COVERID\",\"FSK\",\"GENRE\",\"GROUPS\",\"LOCALID\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_NUM\",\"SCORE\",\"TAGS\") SELECT \"COVERID\",\"FSK\",\"GENRE\",\"GROUPS\",\"LOCALID\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_NUM\",\"SCORE\",\"TAGS\" FROM \"main\".\"SERIES\"");
			db.executeSQLThrow("DROP TABLE \"main\".\"SERIES\"");
			db.executeSQLThrow("ALTER TABLE \"main\".\"sqlb_temp_table_4\" RENAME TO \"SERIES\"");

			db.executeSQLThrow("ALTER TABLE \"main\".\"SERIES\" ADD COLUMN \t\"ONLINESCORE_DENOM\"\tSMALLINT NOT NULL DEFAULT 10");

			db.executeSQLThrow("CREATE TABLE \"sqlb_temp_table_5\" ( \"LOCALID\" INTEGER, \"NAME\" TEXT NOT NULL, \"GENRE\" BIGINT NOT NULL, \"ONLINESCORE_NUM\" SMALLINT NOT NULL, \"ONLINESCORE_DENOM\" SMALLINT NOT NULL DEFAULT 10, \"FSK\" TINYINT NOT NULL, \"ONLINEREF\" TEXT NOT NULL, \"GROUPS\" TEXT NOT NULL, \"SCORE\" TINYINT NOT NULL, \"COVERID\" INTEGER NOT NULL, \"TAGS\" SMALLINT NOT NULL, PRIMARY KEY(\"LOCALID\") );");
			db.executeSQLThrow("INSERT INTO \"main\".\"sqlb_temp_table_5\" (\"COVERID\",\"FSK\",\"GENRE\",\"GROUPS\",\"LOCALID\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_NUM\",\"SCORE\",\"TAGS\") SELECT \"COVERID\",\"FSK\",\"GENRE\",\"GROUPS\",\"LOCALID\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_NUM\",\"SCORE\",\"TAGS\" FROM \"main\".\"SERIES\"");
			db.executeSQLThrow("DROP TABLE \"main\".\"SERIES\"");
			db.executeSQLThrow("ALTER TABLE \"main\".\"sqlb_temp_table_5\" RENAME TO \"SERIES\"");

			db.executeSQLThrow("CREATE TABLE \"sqlb_temp_table_6\" ( \"LOCALID\" INTEGER, \"NAME\" TEXT NOT NULL, \"GENRE\" BIGINT NOT NULL, \"ONLINESCORE_NUM\" SMALLINT NOT NULL, \"ONLINESCORE_DENOM\" SMALLINT NOT NULL, \"FSK\" TINYINT NOT NULL, \"ONLINEREF\" TEXT NOT NULL, \"GROUPS\" TEXT NOT NULL, \"SCORE\" TINYINT NOT NULL, \"COVERID\" INTEGER NOT NULL, \"TAGS\" SMALLINT NOT NULL, PRIMARY KEY(\"LOCALID\") );");
			db.executeSQLThrow("INSERT INTO \"main\".\"sqlb_temp_table_6\" (\"COVERID\",\"FSK\",\"GENRE\",\"GROUPS\",\"LOCALID\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_DENOM\",\"ONLINESCORE_NUM\",\"SCORE\",\"TAGS\") SELECT \"COVERID\",\"FSK\",\"GENRE\",\"GROUPS\",\"LOCALID\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_DENOM\",\"ONLINESCORE_NUM\",\"SCORE\",\"TAGS\" FROM \"main\".\"SERIES\"");
			db.executeSQLThrow("DROP TABLE \"main\".\"SERIES\"");
			db.executeSQLThrow("ALTER TABLE \"main\".\"sqlb_temp_table_6\" RENAME TO \"SERIES\"");

			if (db.querySingleStringSQL("SELECT IVALUE FROM INFO WHERE IKEY = 'HISTORY_ENABLED'", 0).equals("1"))
			{
				for (var trigger : CCDatabaseHistory.createTriggerStatements()) db.executeSQLThrow(trigger.Item2);
			}
		}
		db.executeSQLThrow("PRAGMA foreign_keys = ON;");
		db.executeSQLThrow("COMMIT TRANSACTION");

		db.executeSQLThrow("VACUUM");
	}

	@SuppressWarnings("nls")
	private void upgrade_21_22() throws SQLException
	{
		CCLog.addInformation("[UPGRADE v21 -> v22] Add column SCORECOMMENT in MOVIES");
		CCLog.addInformation("[UPGRADE v21 -> v22] Add column SCORECOMMENT in SERIES");
		CCLog.addInformation("[UPGRADE v21 -> v22] Add column SCORE in SEASONS");
		CCLog.addInformation("[UPGRADE v21 -> v22] Add column SCORECOMMENT in SEASONS");
		CCLog.addInformation("[UPGRADE v21 -> v22] Add column SCORE in EPISODES");
		CCLog.addInformation("[UPGRADE v21 -> v22] Add column SCORECOMMENT in EPISODES");

		db.executeSQLThrow("BEGIN TRANSACTION");
		db.executeSQLThrow("PRAGMA foreign_keys = OFF;");
		{
			for (String trigger : CCStreams.iterate(db.listTrigger()).filter(t -> t.startsWith("JCCTRIGGER_")))
			{
				db.executeSQLThrow("DROP TRIGGER ["+trigger+"]");
			}

			db.executeSQLThrow("ALTER TABLE \"main\".\"MOVIES\" ADD COLUMN \t\"SCORECOMMENT\"\tVARCHAR NOT NULL DEFAULT ''");
			db.executeSQLThrow("CREATE TABLE \"sqlb_temp_table_1\" ( \"LOCALID\" INTEGER, \"NAME\" TEXT NOT NULL, \"VIEWED_HISTORY\" TEXT NOT NULL, \"ZYKLUS\" TEXT NOT NULL, \"ZYKLUSNUMBER\" INTEGER NOT NULL, \"LANGUAGE\" BIGINT NOT NULL, \"SUBTITLES\" TEXT NOT NULL, \"GENRE\" BIGINT NOT NULL, \"LENGTH\" INTEGER NOT NULL, \"ADDDATE\" DATE NOT NULL, \"ONLINESCORE_NUM\" SMALLINT NOT NULL, \"ONLINESCORE_DENOM\" SMALLINT NOT NULL, \"FSK\" TINYINT NOT NULL, \"FORMAT\" TINYINT NOT NULL, \"MOVIEYEAR\" SMALLINT NOT NULL, \"ONLINEREF\" TEXT NOT NULL, \"GROUPS\" TEXT NOT NULL, \"FILESIZE\" BIGINT NOT NULL, \"TAGS\" SMALLINT NOT NULL, \"PART1\" TEXT NOT NULL, \"PART2\" TEXT NOT NULL, \"PART3\" TEXT NOT NULL, \"PART4\" TEXT NOT NULL, \"PART5\" TEXT NOT NULL, \"PART6\" TEXT NOT NULL, \"SCORE\" TINYINT NOT NULL, \"SCORECOMMENT\" VARCHAR NOT NULL DEFAULT '', \"COVERID\" INTEGER NOT NULL, \"MEDIAINFO.FILESIZE\" BIGINT, \"MEDIAINFO.CDATE\" BIGINT, \"MEDIAINFO.MDATE\" BIGINT, \"MEDIAINFO.AFORMAT\" TEXT, \"MEDIAINFO.VFORMAT\" TEXT, \"MEDIAINFO.WIDTH\" SMALLINT, \"MEDIAINFO.HEIGHT\" SMALLINT, \"MEDIAINFO.FRAMERATE\" REAL, \"MEDIAINFO.DURATION\" REAL, \"MEDIAINFO.BITDEPTH\" TINYINT, \"MEDIAINFO.BITRATE\" INTEGER, \"MEDIAINFO.FRAMECOUNT\" INTEGER, \"MEDIAINFO.ACHANNELS\" SMALLINT, \"MEDIAINFO.VCODEC\" TEXT, \"MEDIAINFO.ACODEC\" TEXT, \"MEDIAINFO.SAMPLERATE\" INTEGER, \"MEDIAINFO.CHECKSUM\" TEXT, PRIMARY KEY(\"LOCALID\") );");
			db.executeSQLThrow("INSERT INTO \"main\".\"sqlb_temp_table_1\" (\"ADDDATE\",\"COVERID\",\"FILESIZE\",\"FORMAT\",\"FSK\",\"GENRE\",\"GROUPS\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.CHECKSUM\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"MOVIEYEAR\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_DENOM\",\"ONLINESCORE_NUM\",\"PART1\",\"PART2\",\"PART3\",\"PART4\",\"PART5\",\"PART6\",\"SCORE\",\"SUBTITLES\",\"TAGS\",\"VIEWED_HISTORY\",\"ZYKLUS\",\"ZYKLUSNUMBER\") SELECT \"ADDDATE\",\"COVERID\",\"FILESIZE\",\"FORMAT\",\"FSK\",\"GENRE\",\"GROUPS\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.CHECKSUM\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"MOVIEYEAR\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_DENOM\",\"ONLINESCORE_NUM\",\"PART1\",\"PART2\",\"PART3\",\"PART4\",\"PART5\",\"PART6\",\"SCORE\",\"SUBTITLES\",\"TAGS\",\"VIEWED_HISTORY\",\"ZYKLUS\",\"ZYKLUSNUMBER\" FROM \"main\".\"MOVIES\"");
			db.executeSQLThrow("DROP TABLE \"main\".\"MOVIES\"");
			db.executeSQLThrow("ALTER TABLE \"main\".\"sqlb_temp_table_1\" RENAME TO \"MOVIES\"");
			db.executeSQLThrow("CREATE TABLE \"sqlb_temp_table_2\" ( \"LOCALID\" INTEGER, \"NAME\" TEXT NOT NULL, \"VIEWED_HISTORY\" TEXT NOT NULL, \"ZYKLUS\" TEXT NOT NULL, \"ZYKLUSNUMBER\" INTEGER NOT NULL, \"LANGUAGE\" BIGINT NOT NULL, \"SUBTITLES\" TEXT NOT NULL, \"GENRE\" BIGINT NOT NULL, \"LENGTH\" INTEGER NOT NULL, \"ADDDATE\" DATE NOT NULL, \"ONLINESCORE_NUM\" SMALLINT NOT NULL, \"ONLINESCORE_DENOM\" SMALLINT NOT NULL, \"FSK\" TINYINT NOT NULL, \"FORMAT\" TINYINT NOT NULL, \"MOVIEYEAR\" SMALLINT NOT NULL, \"ONLINEREF\" TEXT NOT NULL, \"GROUPS\" TEXT NOT NULL, \"FILESIZE\" BIGINT NOT NULL, \"TAGS\" SMALLINT NOT NULL, \"PART1\" TEXT NOT NULL, \"PART2\" TEXT NOT NULL, \"PART3\" TEXT NOT NULL, \"PART4\" TEXT NOT NULL, \"PART5\" TEXT NOT NULL, \"PART6\" TEXT NOT NULL, \"SCORE\" TINYINT NOT NULL, \"SCORECOMMENT\" VARCHAR NOT NULL, \"COVERID\" INTEGER NOT NULL, \"MEDIAINFO.FILESIZE\" BIGINT, \"MEDIAINFO.CDATE\" BIGINT, \"MEDIAINFO.MDATE\" BIGINT, \"MEDIAINFO.AFORMAT\" TEXT, \"MEDIAINFO.VFORMAT\" TEXT, \"MEDIAINFO.WIDTH\" SMALLINT, \"MEDIAINFO.HEIGHT\" SMALLINT, \"MEDIAINFO.FRAMERATE\" REAL, \"MEDIAINFO.DURATION\" REAL, \"MEDIAINFO.BITDEPTH\" TINYINT, \"MEDIAINFO.BITRATE\" INTEGER, \"MEDIAINFO.FRAMECOUNT\" INTEGER, \"MEDIAINFO.ACHANNELS\" SMALLINT, \"MEDIAINFO.VCODEC\" TEXT, \"MEDIAINFO.ACODEC\" TEXT, \"MEDIAINFO.SAMPLERATE\" INTEGER, \"MEDIAINFO.CHECKSUM\" TEXT, PRIMARY KEY(\"LOCALID\") );");
			db.executeSQLThrow("INSERT INTO \"main\".\"sqlb_temp_table_2\" (\"ADDDATE\",\"COVERID\",\"FILESIZE\",\"FORMAT\",\"FSK\",\"GENRE\",\"GROUPS\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.CHECKSUM\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"MOVIEYEAR\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_DENOM\",\"ONLINESCORE_NUM\",\"PART1\",\"PART2\",\"PART3\",\"PART4\",\"PART5\",\"PART6\",\"SCORE\",\"SCORECOMMENT\",\"SUBTITLES\",\"TAGS\",\"VIEWED_HISTORY\",\"ZYKLUS\",\"ZYKLUSNUMBER\") SELECT \"ADDDATE\",\"COVERID\",\"FILESIZE\",\"FORMAT\",\"FSK\",\"GENRE\",\"GROUPS\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.CHECKSUM\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"MOVIEYEAR\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_DENOM\",\"ONLINESCORE_NUM\",\"PART1\",\"PART2\",\"PART3\",\"PART4\",\"PART5\",\"PART6\",\"SCORE\",\"SCORECOMMENT\",\"SUBTITLES\",\"TAGS\",\"VIEWED_HISTORY\",\"ZYKLUS\",\"ZYKLUSNUMBER\" FROM \"main\".\"MOVIES\"");
			db.executeSQLThrow("DROP TABLE \"main\".\"MOVIES\"");
			db.executeSQLThrow("ALTER TABLE \"main\".\"sqlb_temp_table_2\" RENAME TO \"MOVIES\"");

			db.executeSQLThrow("ALTER TABLE \"main\".\"SERIES\" ADD COLUMN \t\"SCORECOMMENT\"\tVARCHAR NOT NULL DEFAULT ''");
			db.executeSQLThrow("CREATE TABLE \"sqlb_temp_table_3\" ( \"LOCALID\" INTEGER, \"NAME\" TEXT NOT NULL, \"GENRE\" BIGINT NOT NULL, \"ONLINESCORE_NUM\" SMALLINT NOT NULL, \"ONLINESCORE_DENOM\" SMALLINT NOT NULL, \"FSK\" TINYINT NOT NULL, \"ONLINEREF\" TEXT NOT NULL, \"GROUPS\" TEXT NOT NULL, \"SCORE\" TINYINT NOT NULL, \"SCORECOMMENT\" TEXT NOT NULL DEFAULT '', \"COVERID\" INTEGER NOT NULL, \"TAGS\" SMALLINT NOT NULL, PRIMARY KEY(\"LOCALID\") );\n");
			db.executeSQLThrow("INSERT INTO \"main\".\"sqlb_temp_table_3\" (\"COVERID\",\"FSK\",\"GENRE\",\"GROUPS\",\"LOCALID\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_DENOM\",\"ONLINESCORE_NUM\",\"SCORE\",\"TAGS\") SELECT \"COVERID\",\"FSK\",\"GENRE\",\"GROUPS\",\"LOCALID\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_DENOM\",\"ONLINESCORE_NUM\",\"SCORE\",\"TAGS\" FROM \"main\".\"SERIES\"\n");
			db.executeSQLThrow("DROP TABLE \"main\".\"SERIES\"");
			db.executeSQLThrow("ALTER TABLE \"main\".\"sqlb_temp_table_3\" RENAME TO \"SERIES\"");
			db.executeSQLThrow("CREATE TABLE \"sqlb_temp_table_4\" ( \"LOCALID\" INTEGER, \"NAME\" TEXT NOT NULL, \"GENRE\" BIGINT NOT NULL, \"ONLINESCORE_NUM\" SMALLINT NOT NULL, \"ONLINESCORE_DENOM\" SMALLINT NOT NULL, \"FSK\" TINYINT NOT NULL, \"ONLINEREF\" TEXT NOT NULL, \"GROUPS\" TEXT NOT NULL, \"SCORE\" TINYINT NOT NULL, \"SCORECOMMENT\" TEXT NOT NULL, \"COVERID\" INTEGER NOT NULL, \"TAGS\" SMALLINT NOT NULL, PRIMARY KEY(\"LOCALID\") );\n");
			db.executeSQLThrow("INSERT INTO \"main\".\"sqlb_temp_table_4\" (\"COVERID\",\"FSK\",\"GENRE\",\"GROUPS\",\"LOCALID\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_DENOM\",\"ONLINESCORE_NUM\",\"SCORE\",\"SCORECOMMENT\",\"TAGS\") SELECT \"COVERID\",\"FSK\",\"GENRE\",\"GROUPS\",\"LOCALID\",\"NAME\",\"ONLINEREF\",\"ONLINESCORE_DENOM\",\"ONLINESCORE_NUM\",\"SCORE\",\"SCORECOMMENT\",\"TAGS\" FROM \"main\".\"SERIES\"\n");
			db.executeSQLThrow("DROP TABLE \"main\".\"SERIES\"\n");
			db.executeSQLThrow("ALTER TABLE \"main\".\"sqlb_temp_table_4\" RENAME TO \"SERIES\"\n");

			db.executeSQLThrow("ALTER TABLE \"main\".\"SEASONS\" ADD COLUMN \t\"SCORE\"\tTINYINT NOT NULL DEFAULT 6");
			db.executeSQLThrow("CREATE TABLE \"sqlb_temp_table_5\" ( \"LOCALID\" INTEGER, \"SERIESID\" INTEGER NOT NULL, \"NAME\" TEXT NOT NULL, \"SEASONYEAR\" SMALLINT NOT NULL, \"COVERID\" INTEGER NOT NULL, \"SCORE\" TINYINT NOT NULL DEFAULT 6, \"SCORECOMMENT\" VARCHAR NOT NULL DEFAULT '', PRIMARY KEY(\"LOCALID\"), FOREIGN KEY(\"SERIESID\") REFERENCES \"SERIES\"(\"LOCALID\") );");
			db.executeSQLThrow("INSERT INTO \"main\".\"sqlb_temp_table_5\" (\"COVERID\",\"LOCALID\",\"NAME\",\"SEASONYEAR\",\"SERIESID\") SELECT \"COVERID\",\"LOCALID\",\"NAME\",\"SEASONYEAR\",\"SERIESID\" FROM \"main\".\"SEASONS\"\n");
			db.executeSQLThrow("DROP TABLE \"main\".\"SEASONS\"\n");
			db.executeSQLThrow("ALTER TABLE \"main\".\"sqlb_temp_table_5\" RENAME TO \"SEASONS\"");
			db.executeSQLThrow("CREATE TABLE \"sqlb_temp_table_6\" ( \"LOCALID\" INTEGER, \"SERIESID\" INTEGER NOT NULL, \"NAME\" TEXT NOT NULL, \"SEASONYEAR\" SMALLINT NOT NULL, \"COVERID\" INTEGER NOT NULL, \"SCORE\" TINYINT NOT NULL, \"SCORECOMMENT\" VARCHAR NOT NULL, FOREIGN KEY(\"SERIESID\") REFERENCES \"SERIES\"(\"LOCALID\"), PRIMARY KEY(\"LOCALID\") );\n");
			db.executeSQLThrow("INSERT INTO \"main\".\"sqlb_temp_table_6\" (\"COVERID\",\"LOCALID\",\"NAME\",\"SCORE\",\"SCORECOMMENT\",\"SEASONYEAR\",\"SERIESID\") SELECT \"COVERID\",\"LOCALID\",\"NAME\",\"SCORE\",\"SCORECOMMENT\",\"SEASONYEAR\",\"SERIESID\" FROM \"main\".\"SEASONS\"\n");
			db.executeSQLThrow("DROP TABLE \"main\".\"SEASONS\"\n");
			db.executeSQLThrow("ALTER TABLE \"main\".\"sqlb_temp_table_6\" RENAME TO \"SEASONS\"\n");

			db.executeSQLThrow("ALTER TABLE \"main\".\"EPISODES\" ADD COLUMN \t\"SCORE\"\tINTEGER NOT NULL DEFAULT 6\n");
			db.executeSQLThrow("CREATE TABLE \"sqlb_temp_table_7\" ( \"LOCALID\" INTEGER, \"SEASONID\" INTEGER NOT NULL, \"EPISODE\" SMALLINT NOT NULL, \"NAME\" TEXT NOT NULL, \"VIEWED_HISTORY\" TEXT NOT NULL, \"LENGTH\" INTEGER NOT NULL, \"FORMAT\" TINYINT NOT NULL, \"FILESIZE\" BIGINT NOT NULL, \"PART1\" TEXT NOT NULL, \"TAGS\" SMALLINT NOT NULL, \"ADDDATE\" DATE NOT NULL, \"LANGUAGE\" BIGINT NOT NULL, \"SUBTITLES\" TEXT NOT NULL, \"SCORE\" INTEGER NOT NULL DEFAULT 6, \"SCORECOMMENT\" INTEGER NOT NULL DEFAULT '', \"MEDIAINFO.FILESIZE\" BIGINT, \"MEDIAINFO.CDATE\" BIGINT, \"MEDIAINFO.MDATE\" BIGINT, \"MEDIAINFO.AFORMAT\" TEXT, \"MEDIAINFO.VFORMAT\" TEXT, \"MEDIAINFO.WIDTH\" SMALLINT, \"MEDIAINFO.HEIGHT\" SMALLINT, \"MEDIAINFO.FRAMERATE\" REAL, \"MEDIAINFO.DURATION\" REAL, \"MEDIAINFO.BITDEPTH\" TINYINT, \"MEDIAINFO.BITRATE\" INTEGER, \"MEDIAINFO.FRAMECOUNT\" INTEGER, \"MEDIAINFO.ACHANNELS\" SMALLINT, \"MEDIAINFO.VCODEC\" TEXT, \"MEDIAINFO.ACODEC\" TEXT, \"MEDIAINFO.SAMPLERATE\" INTEGER, \"MEDIAINFO.CHECKSUM\" TEXT, FOREIGN KEY(\"SEASONID\") REFERENCES \"SEASONS\"(\"LOCALID\"), PRIMARY KEY(\"LOCALID\") );\n");
			db.executeSQLThrow("INSERT INTO \"main\".\"sqlb_temp_table_7\" (\"ADDDATE\",\"EPISODE\",\"FILESIZE\",\"FORMAT\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.CHECKSUM\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"NAME\",\"PART1\",\"SEASONID\",\"SUBTITLES\",\"TAGS\",\"VIEWED_HISTORY\") SELECT \"ADDDATE\",\"EPISODE\",\"FILESIZE\",\"FORMAT\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.CHECKSUM\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"NAME\",\"PART1\",\"SEASONID\",\"SUBTITLES\",\"TAGS\",\"VIEWED_HISTORY\" FROM \"main\".\"EPISODES\"\n");
			db.executeSQLThrow("DROP TABLE \"main\".\"EPISODES\"\n");
			db.executeSQLThrow("ALTER TABLE \"main\".\"sqlb_temp_table_7\" RENAME TO \"EPISODES\"\n");
			db.executeSQLThrow("CREATE TABLE \"sqlb_temp_table_8\" ( \"LOCALID\" INTEGER, \"SEASONID\" INTEGER NOT NULL, \"EPISODE\" SMALLINT NOT NULL, \"NAME\" TEXT NOT NULL, \"VIEWED_HISTORY\" TEXT NOT NULL, \"LENGTH\" INTEGER NOT NULL, \"FORMAT\" TINYINT NOT NULL, \"FILESIZE\" BIGINT NOT NULL, \"PART1\" TEXT NOT NULL, \"TAGS\" SMALLINT NOT NULL, \"ADDDATE\" DATE NOT NULL, \"LANGUAGE\" BIGINT NOT NULL, \"SUBTITLES\" TEXT NOT NULL, \"SCORE\" INTEGER NOT NULL, \"SCORECOMMENT\" INTEGER NOT NULL, \"MEDIAINFO.FILESIZE\" BIGINT, \"MEDIAINFO.CDATE\" BIGINT, \"MEDIAINFO.MDATE\" BIGINT, \"MEDIAINFO.AFORMAT\" TEXT, \"MEDIAINFO.VFORMAT\" TEXT, \"MEDIAINFO.WIDTH\" SMALLINT, \"MEDIAINFO.HEIGHT\" SMALLINT, \"MEDIAINFO.FRAMERATE\" REAL, \"MEDIAINFO.DURATION\" REAL, \"MEDIAINFO.BITDEPTH\" TINYINT, \"MEDIAINFO.BITRATE\" INTEGER, \"MEDIAINFO.FRAMECOUNT\" INTEGER, \"MEDIAINFO.ACHANNELS\" SMALLINT, \"MEDIAINFO.VCODEC\" TEXT, \"MEDIAINFO.ACODEC\" TEXT, \"MEDIAINFO.SAMPLERATE\" INTEGER, \"MEDIAINFO.CHECKSUM\" TEXT, FOREIGN KEY(\"SEASONID\") REFERENCES \"SEASONS\"(\"LOCALID\"), PRIMARY KEY(\"LOCALID\") );\n");
			db.executeSQLThrow("INSERT INTO \"main\".\"sqlb_temp_table_8\" (\"ADDDATE\",\"EPISODE\",\"FILESIZE\",\"FORMAT\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.CHECKSUM\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"NAME\",\"PART1\",\"SCORE\",\"SCORECOMMENT\",\"SEASONID\",\"SUBTITLES\",\"TAGS\",\"VIEWED_HISTORY\") SELECT \"ADDDATE\",\"EPISODE\",\"FILESIZE\",\"FORMAT\",\"LANGUAGE\",\"LENGTH\",\"LOCALID\",\"MEDIAINFO.ACHANNELS\",\"MEDIAINFO.ACODEC\",\"MEDIAINFO.AFORMAT\",\"MEDIAINFO.BITDEPTH\",\"MEDIAINFO.BITRATE\",\"MEDIAINFO.CDATE\",\"MEDIAINFO.CHECKSUM\",\"MEDIAINFO.DURATION\",\"MEDIAINFO.FILESIZE\",\"MEDIAINFO.FRAMECOUNT\",\"MEDIAINFO.FRAMERATE\",\"MEDIAINFO.HEIGHT\",\"MEDIAINFO.MDATE\",\"MEDIAINFO.SAMPLERATE\",\"MEDIAINFO.VCODEC\",\"MEDIAINFO.VFORMAT\",\"MEDIAINFO.WIDTH\",\"NAME\",\"PART1\",\"SCORE\",\"SCORECOMMENT\",\"SEASONID\",\"SUBTITLES\",\"TAGS\",\"VIEWED_HISTORY\" FROM \"main\".\"EPISODES\"\n");
			db.executeSQLThrow("DROP TABLE \"main\".\"EPISODES\"\n");
			db.executeSQLThrow("ALTER TABLE \"main\".\"sqlb_temp_table_8\" RENAME TO \"EPISODES\"\n");

			if (db.querySingleStringSQL("SELECT IVALUE FROM INFO WHERE IKEY = 'HISTORY_ENABLED'", 0).equals("1"))
			{
				for (var trigger : CCDatabaseHistory.createTriggerStatements()) db.executeSQLThrow(trigger.Item2);
			}
		}
		db.executeSQLThrow("PRAGMA foreign_keys = ON;");
		db.executeSQLThrow("COMMIT TRANSACTION");

		db.executeSQLThrow("VACUUM");
	}

	public void onAfterConnect(CCMovieList ml, CCDatabase db) {
		for (UpgradeAction action : afterConnectActions) {
			action.onAfterConnect(ml, db);
		}
	}
}
