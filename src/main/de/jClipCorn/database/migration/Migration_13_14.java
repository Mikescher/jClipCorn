package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.List;

public class Migration_13_14 extends DBMigration {

	public Migration_13_14(GenericDatabase db, CCProperties ccprops, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, ccprops, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "13";
	}

	@Override
	public String getToVersion() {
		return "14";
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
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

		return new ArrayList<>();
	}
}
