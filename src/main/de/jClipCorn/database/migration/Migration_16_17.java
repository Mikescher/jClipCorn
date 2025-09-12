package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.filesystem.FSPath;

import java.util.ArrayList;
import java.util.List;

public class Migration_16_17 extends DBMigration {

	public Migration_16_17(GenericDatabase db, CCProperties ccprops, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, ccprops, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "16";
	}

	@Override
	public String getToVersion() {
		return "17";
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return true;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[UPGRADE v16 -> v17] Remove column Main.SeriesID");
		CCLog.addInformation("[UPGRADE v16 -> v17] Update Season.SeriesID to new values");

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

		return new ArrayList<>();
	}
}
