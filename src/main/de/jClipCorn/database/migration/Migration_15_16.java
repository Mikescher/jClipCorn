package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.stream.CCStreams;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Migration_15_16 extends DBMigration {

	public Migration_15_16(GenericDatabase db, CCProperties ccprops, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, ccprops, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "15";
	}

	@Override
	public String getToVersion() {
		return "16";
	}

	@Override
	@SuppressWarnings("nls")
	public List<UpgradeAction> migrate() throws Exception {
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

		return new ArrayList<>();
	}
}
