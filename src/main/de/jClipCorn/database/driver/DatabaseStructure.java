package de.jClipCorn.database.driver;

import de.jClipCorn.util.sqlwrapper.CCSQLColDef;
import de.jClipCorn.util.sqlwrapper.CCSQLTableDef;
import de.jClipCorn.util.sqlwrapper.CCSQLType;

@SuppressWarnings("nls")
public class DatabaseStructure
{
	private static final boolean NULLABLE = false;
	private static final boolean NON_NULLABLE = true;

	public final static CCSQLColDef COL_TEMP_KEY             = new CCSQLColDef("IKEY",                  CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_TEMP_VALUE           = new CCSQLColDef("IVALUE",                CCSQLType.VARCHAR,     NON_NULLABLE);

	public final static CCSQLColDef COL_INFO_KEY             = new CCSQLColDef("IKEY",                  CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_INFO_VALUE           = new CCSQLColDef("IVALUE",                CCSQLType.VARCHAR,     NON_NULLABLE);

	public final static CCSQLColDef COL_HISTORY_TABLE         = new CCSQLColDef("TABLE",                CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_HISTORY_ID            = new CCSQLColDef("ID",                   CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_HISTORY_DATE          = new CCSQLColDef("DATE",                 CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_HISTORY_ACTION        = new CCSQLColDef("ACTION",               CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_HISTORY_FIELD         = new CCSQLColDef("FIELD",                CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_HISTORY_OLD           = new CCSQLColDef("OLD",                  CCSQLType.VARCHAR,     NULLABLE);
	public final static CCSQLColDef COL_HISTORY_NEW           = new CCSQLColDef("NEW",                  CCSQLType.VARCHAR,     NULLABLE);

	public final static CCSQLColDef COL_MAIN_LOCALID          = new CCSQLColDef("LOCALID",              CCSQLType.INTEGER,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_NAME             = new CCSQLColDef("NAME",                 CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_VIEWED           = new CCSQLColDef("VIEWED",               CCSQLType.BIT,         NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_VIEWEDHISTORY    = new CCSQLColDef("VIEWED_HISTORY",       CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_ZYKLUS           = new CCSQLColDef("ZYKLUS",               CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_ZYKLUSNUMBER     = new CCSQLColDef("ZYKLUSNUMBER",         CCSQLType.INTEGER,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_LANGUAGE         = new CCSQLColDef("LANGUAGE",             CCSQLType.BIGINT,      NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_GENRE            = new CCSQLColDef("GENRE",                CCSQLType.BIGINT,      NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_LENGTH           = new CCSQLColDef("LENGTH",               CCSQLType.INTEGER,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_ADDDATE          = new CCSQLColDef("ADDDATE",              CCSQLType.DATE,        NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_ONLINESCORE      = new CCSQLColDef("ONLINESCORE",          CCSQLType.TINYINT,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_FSK              = new CCSQLColDef("FSK",                  CCSQLType.TINYINT,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_FORMAT           = new CCSQLColDef("FORMAT",               CCSQLType.TINYINT,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_MOVIEYEAR        = new CCSQLColDef("MOVIEYEAR",            CCSQLType.SMALLINT,    NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_ONLINEREF        = new CCSQLColDef("ONLINEREF",            CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_GROUPS           = new CCSQLColDef("GROUPS",               CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_FILESIZE         = new CCSQLColDef("FILESIZE",             CCSQLType.BIGINT,      NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_TAGS             = new CCSQLColDef("TAGS",                 CCSQLType.SMALLINT,    NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_PART_1           = new CCSQLColDef("PART1",                CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_PART_2           = new CCSQLColDef("PART2",                CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_PART_3           = new CCSQLColDef("PART3",                CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_PART_4           = new CCSQLColDef("PART4",                CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_PART_5           = new CCSQLColDef("PART5",                CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_PART_6           = new CCSQLColDef("PART6",                CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_SCORE            = new CCSQLColDef("SCORE",                CCSQLType.TINYINT,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_COVERID          = new CCSQLColDef("COVERID",              CCSQLType.INTEGER,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_TYPE             = new CCSQLColDef("TYPE",                 CCSQLType.TINYINT,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_SERIES_ID        = new CCSQLColDef("SERIESID",             CCSQLType.INTEGER,     NON_NULLABLE);
	public final static CCSQLColDef COL_MAIN_MI_FILESIZE      = new CCSQLColDef("MEDIAINFO.FILESIZE",   CCSQLType.BIGINT,      NULLABLE);
	public final static CCSQLColDef COL_MAIN_MI_CDATE         = new CCSQLColDef("MEDIAINFO.CDATE",      CCSQLType.BIGINT,      NULLABLE);
	public final static CCSQLColDef COL_MAIN_MI_MDATE         = new CCSQLColDef("MEDIAINFO.MDATE",      CCSQLType.BIGINT,      NULLABLE);
	public final static CCSQLColDef COL_MAIN_MI_AFORMAT       = new CCSQLColDef("MEDIAINFO.AFORMAT",    CCSQLType.VARCHAR,     NULLABLE);
	public final static CCSQLColDef COL_MAIN_MI_VFORMAT       = new CCSQLColDef("MEDIAINFO.VFORMAT",    CCSQLType.VARCHAR,     NULLABLE);
	public final static CCSQLColDef COL_MAIN_MI_WIDTH         = new CCSQLColDef("MEDIAINFO.WIDTH",      CCSQLType.SMALLINT,    NULLABLE);
	public final static CCSQLColDef COL_MAIN_MI_HEIGHT        = new CCSQLColDef("MEDIAINFO.HEIGHT",     CCSQLType.SMALLINT,    NULLABLE);
	public final static CCSQLColDef COL_MAIN_MI_FRAMERATE     = new CCSQLColDef("MEDIAINFO.FRAMERATE",  CCSQLType.REAL,        NULLABLE);
	public final static CCSQLColDef COL_MAIN_MI_DURATION      = new CCSQLColDef("MEDIAINFO.DURATION",   CCSQLType.REAL,        NULLABLE);
	public final static CCSQLColDef COL_MAIN_MI_BITDEPTH      = new CCSQLColDef("MEDIAINFO.BITDEPTH",   CCSQLType.TINYINT,     NULLABLE);
	public final static CCSQLColDef COL_MAIN_MI_BITRATE       = new CCSQLColDef("MEDIAINFO.BITRATE",    CCSQLType.INTEGER,     NULLABLE);
	public final static CCSQLColDef COL_MAIN_MI_FRAMECOUNT    = new CCSQLColDef("MEDIAINFO.FRAMECOUNT", CCSQLType.INTEGER,     NULLABLE);
	public final static CCSQLColDef COL_MAIN_MI_ACHANNELS     = new CCSQLColDef("MEDIAINFO.ACHANNELS",  CCSQLType.INTEGER,     NULLABLE);
	public final static CCSQLColDef COL_MAIN_MI_VCODEC        = new CCSQLColDef("MEDIAINFO.VCODEC",     CCSQLType.VARCHAR,     NULLABLE);
	public final static CCSQLColDef COL_MAIN_MI_ACODEC        = new CCSQLColDef("MEDIAINFO.ACODEC",     CCSQLType.VARCHAR,     NULLABLE);
	public final static CCSQLColDef COL_MAIN_MI_SAMPLERATE    = new CCSQLColDef("MEDIAINFO.SAMPLERATE", CCSQLType.INTEGER,     NULLABLE);

	public final static CCSQLColDef COL_SEAS_SEASONID         = new CCSQLColDef("SEASONID",             CCSQLType.INTEGER,     NON_NULLABLE);
	public final static CCSQLColDef COL_SEAS_SERIESID         = new CCSQLColDef("SERIESID",             CCSQLType.INTEGER,     NON_NULLABLE);
	public final static CCSQLColDef COL_SEAS_NAME             = new CCSQLColDef("NAME",                 CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_SEAS_YEAR             = new CCSQLColDef("SEASONYEAR",           CCSQLType.SMALLINT,    NON_NULLABLE);
	public final static CCSQLColDef COL_SEAS_COVERID          = new CCSQLColDef("COVERID",              CCSQLType.INTEGER,     NON_NULLABLE);

	public final static CCSQLColDef COL_EPIS_LOCALID          = new CCSQLColDef("LOCALID",              CCSQLType.INTEGER,     NON_NULLABLE);
	public final static CCSQLColDef COL_EPIS_SEASONID         = new CCSQLColDef("SEASONID",             CCSQLType.INTEGER,     NON_NULLABLE);
	public final static CCSQLColDef COL_EPIS_EPISODE          = new CCSQLColDef("EPISODE",              CCSQLType.SMALLINT,    NON_NULLABLE);
	public final static CCSQLColDef COL_EPIS_NAME             = new CCSQLColDef("NAME",                 CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_EPIS_VIEWED           = new CCSQLColDef("VIEWED",               CCSQLType.BIT,         NON_NULLABLE);
	public final static CCSQLColDef COL_EPIS_VIEWEDHISTORY    = new CCSQLColDef("VIEWED_HISTORY",       CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_EPIS_LENGTH           = new CCSQLColDef("LENGTH",               CCSQLType.INTEGER,     NON_NULLABLE);
	public final static CCSQLColDef COL_EPIS_FORMAT           = new CCSQLColDef("FORMAT",               CCSQLType.TINYINT,     NON_NULLABLE);
	public final static CCSQLColDef COL_EPIS_FILESIZE         = new CCSQLColDef("FILESIZE",             CCSQLType.BIGINT,      NON_NULLABLE);
	public final static CCSQLColDef COL_EPIS_PART_1           = new CCSQLColDef("PART1",                CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_EPIS_TAGS             = new CCSQLColDef("TAGS",                 CCSQLType.SMALLINT,    NON_NULLABLE);
	public final static CCSQLColDef COL_EPIS_ADDDATE          = new CCSQLColDef("ADDDATE",              CCSQLType.DATE,        NON_NULLABLE);
	public final static CCSQLColDef COL_EPIS_LANGUAGE         = new CCSQLColDef("LANGUAGE",             CCSQLType.BIGINT,      NON_NULLABLE);
	public final static CCSQLColDef COL_EPIS_MI_FILESIZE      = new CCSQLColDef("MEDIAINFO.FILESIZE",   CCSQLType.BIGINT,      NULLABLE);
	public final static CCSQLColDef COL_EPIS_MI_CDATE         = new CCSQLColDef("MEDIAINFO.CDATE",      CCSQLType.BIGINT,      NULLABLE);
	public final static CCSQLColDef COL_EPIS_MI_MDATE         = new CCSQLColDef("MEDIAINFO.MDATE",      CCSQLType.BIGINT,      NULLABLE);
	public final static CCSQLColDef COL_EPIS_MI_AFORMAT       = new CCSQLColDef("MEDIAINFO.AFORMAT",    CCSQLType.VARCHAR,     NULLABLE);
	public final static CCSQLColDef COL_EPIS_MI_VFORMAT       = new CCSQLColDef("MEDIAINFO.VFORMAT",    CCSQLType.VARCHAR,     NULLABLE);
	public final static CCSQLColDef COL_EPIS_MI_WIDTH         = new CCSQLColDef("MEDIAINFO.WIDTH",      CCSQLType.SMALLINT,    NULLABLE);
	public final static CCSQLColDef COL_EPIS_MI_HEIGHT        = new CCSQLColDef("MEDIAINFO.HEIGHT",     CCSQLType.SMALLINT,    NULLABLE);
	public final static CCSQLColDef COL_EPIS_MI_FRAMERATE     = new CCSQLColDef("MEDIAINFO.FRAMERATE",  CCSQLType.REAL,        NULLABLE);
	public final static CCSQLColDef COL_EPIS_MI_DURATION      = new CCSQLColDef("MEDIAINFO.DURATION",   CCSQLType.REAL,        NULLABLE);
	public final static CCSQLColDef COL_EPIS_MI_BITDEPTH      = new CCSQLColDef("MEDIAINFO.BITDEPTH",   CCSQLType.TINYINT,     NULLABLE);
	public final static CCSQLColDef COL_EPIS_MI_BITRATE       = new CCSQLColDef("MEDIAINFO.BITRATE",    CCSQLType.INTEGER,     NULLABLE);
	public final static CCSQLColDef COL_EPIS_MI_FRAMECOUNT    = new CCSQLColDef("MEDIAINFO.FRAMECOUNT", CCSQLType.INTEGER,     NULLABLE);
	public final static CCSQLColDef COL_EPIS_MI_ACHANNELS     = new CCSQLColDef("MEDIAINFO.ACHANNELS",  CCSQLType.INTEGER,     NULLABLE);
	public final static CCSQLColDef COL_EPIS_MI_VCODEC        = new CCSQLColDef("MEDIAINFO.VCODEC",     CCSQLType.VARCHAR,     NULLABLE);
	public final static CCSQLColDef COL_EPIS_MI_ACODEC        = new CCSQLColDef("MEDIAINFO.ACODEC",     CCSQLType.VARCHAR,     NULLABLE);
	public final static CCSQLColDef COL_EPIS_MI_SAMPLERATE    = new CCSQLColDef("MEDIAINFO.SAMPLERATE", CCSQLType.INTEGER,     NULLABLE);

	public final static CCSQLColDef COL_GRPS_NAME             = new CCSQLColDef("NAME",                 CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_GRPS_ORDER            = new CCSQLColDef("ORDERING",             CCSQLType.INTEGER,     NON_NULLABLE);
	public final static CCSQLColDef COL_GRPS_COLOR            = new CCSQLColDef("COLOR",                CCSQLType.INTEGER,     NON_NULLABLE);
	public final static CCSQLColDef COL_GRPS_SERIALIZE        = new CCSQLColDef("SERIALIZE",            CCSQLType.BIT,         NON_NULLABLE);
	public final static CCSQLColDef COL_GRPS_PARENT           = new CCSQLColDef("PARENTGROUP",          CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_GRPS_VISIBLE          = new CCSQLColDef("VISIBLE",              CCSQLType.BIT,         NON_NULLABLE);

	public final static CCSQLColDef COL_CVRS_ID               = new CCSQLColDef("ID",                   CCSQLType.INTEGER,     NON_NULLABLE);
	public final static CCSQLColDef COL_CVRS_FILENAME         = new CCSQLColDef("FILENAME",             CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_CVRS_WIDTH            = new CCSQLColDef("WIDTH",                CCSQLType.INTEGER,     NON_NULLABLE);
	public final static CCSQLColDef COL_CVRS_HEIGHT           = new CCSQLColDef("HEIGHT",               CCSQLType.INTEGER,     NON_NULLABLE);
	public final static CCSQLColDef COL_CVRS_HASH_FILE        = new CCSQLColDef("HASH_FILE",            CCSQLType.VARCHAR,     NON_NULLABLE);
	public final static CCSQLColDef COL_CVRS_FILESIZE         = new CCSQLColDef("FILESIZE",             CCSQLType.BIGINT,      NON_NULLABLE);
	public final static CCSQLColDef COL_CVRS_PREVIEWTYPE      = new CCSQLColDef("PREVIEW_TYPE",         CCSQLType.INTEGER,     NON_NULLABLE);
	public final static CCSQLColDef COL_CVRS_PREVIEW          = new CCSQLColDef("PREVIEW",              CCSQLType.BLOB,        NON_NULLABLE);
	public final static CCSQLColDef COL_CVRS_CREATED          = new CCSQLColDef("CREATED",              CCSQLType.VARCHAR,     NON_NULLABLE);

	//--------------------------------------------------------------------------------------------------

	public final static CCSQLTableDef TAB_MAIN = new CCSQLTableDef(
			"ELEMENTS",
			COL_MAIN_LOCALID,
			COL_MAIN_NAME, COL_MAIN_VIEWED, COL_MAIN_VIEWEDHISTORY, COL_MAIN_ZYKLUS, COL_MAIN_ZYKLUSNUMBER, COL_MAIN_LANGUAGE,
			COL_MAIN_GENRE, COL_MAIN_LENGTH, COL_MAIN_ADDDATE, COL_MAIN_ONLINESCORE, COL_MAIN_FSK, COL_MAIN_FORMAT,
			COL_MAIN_MOVIEYEAR, COL_MAIN_ONLINEREF, COL_MAIN_GROUPS, COL_MAIN_FILESIZE, COL_MAIN_TAGS,
			COL_MAIN_PART_1, COL_MAIN_PART_2, COL_MAIN_PART_3, COL_MAIN_PART_4, COL_MAIN_PART_5, COL_MAIN_PART_6,
			COL_MAIN_SCORE, COL_MAIN_COVERID, COL_MAIN_TYPE, COL_MAIN_SERIES_ID, COL_MAIN_MI_FILESIZE, COL_MAIN_MI_CDATE,
			COL_MAIN_MI_MDATE, COL_MAIN_MI_AFORMAT, COL_MAIN_MI_VFORMAT, COL_MAIN_MI_WIDTH, COL_MAIN_MI_HEIGHT,
			COL_MAIN_MI_FRAMERATE, COL_MAIN_MI_DURATION, COL_MAIN_MI_BITDEPTH, COL_MAIN_MI_BITRATE, COL_MAIN_MI_FRAMECOUNT,
			COL_MAIN_MI_ACHANNELS, COL_MAIN_MI_VCODEC, COL_MAIN_MI_ACODEC, COL_MAIN_MI_SAMPLERATE);

	public final static CCSQLTableDef TAB_SEASONS = new CCSQLTableDef(
			"SEASONS",
			COL_SEAS_SEASONID,
			COL_SEAS_SERIESID, COL_SEAS_NAME, COL_SEAS_YEAR, COL_SEAS_COVERID);

	public final static CCSQLTableDef TAB_EPISODES = new CCSQLTableDef(
			"EPISODES",
			COL_EPIS_LOCALID,
			COL_EPIS_SEASONID, COL_EPIS_EPISODE, COL_EPIS_NAME, COL_EPIS_VIEWED, COL_EPIS_VIEWEDHISTORY,
			COL_EPIS_LENGTH, COL_EPIS_FORMAT, COL_EPIS_FILESIZE, COL_EPIS_PART_1, COL_EPIS_TAGS,
			COL_EPIS_ADDDATE, COL_EPIS_LANGUAGE, COL_EPIS_MI_FILESIZE, COL_EPIS_MI_CDATE, COL_EPIS_MI_MDATE,
			COL_EPIS_MI_AFORMAT, COL_EPIS_MI_VFORMAT, COL_EPIS_MI_WIDTH, COL_EPIS_MI_HEIGHT, COL_EPIS_MI_FRAMERATE,
			COL_EPIS_MI_DURATION, COL_EPIS_MI_BITDEPTH, COL_EPIS_MI_BITRATE, COL_EPIS_MI_FRAMECOUNT,
			COL_EPIS_MI_ACHANNELS, COL_EPIS_MI_VCODEC, COL_EPIS_MI_ACODEC, COL_EPIS_MI_SAMPLERATE);

	public final static CCSQLTableDef TAB_INFO = new CCSQLTableDef(
			"INFO",
			COL_INFO_KEY,
			COL_INFO_VALUE);

	public final static CCSQLTableDef TAB_GROUPS = new CCSQLTableDef(
			"GROUPS",
			COL_GRPS_NAME,
			COL_GRPS_ORDER, COL_GRPS_COLOR, COL_GRPS_SERIALIZE, COL_GRPS_PARENT, COL_GRPS_VISIBLE);

	public final static CCSQLTableDef TAB_COVERS = new CCSQLTableDef(
			"COVERS",
			COL_CVRS_ID,
			COL_CVRS_FILENAME, COL_CVRS_WIDTH, COL_CVRS_HEIGHT, COL_CVRS_HASH_FILE,
			COL_CVRS_FILESIZE, COL_CVRS_PREVIEWTYPE, COL_CVRS_PREVIEW, COL_CVRS_CREATED);

	public final static CCSQLTableDef TAB_HISTORY = new CCSQLTableDef(
			"HISTORY",
			null,
			COL_HISTORY_TABLE, COL_HISTORY_ID, COL_HISTORY_DATE, COL_HISTORY_ACTION,
			COL_HISTORY_FIELD, COL_HISTORY_OLD, COL_HISTORY_NEW);

	public final static CCSQLTableDef TAB_TEMP = new CCSQLTableDef(
			"TEMP",
			COL_TEMP_KEY,
			COL_TEMP_VALUE);

	//--------------------------------------------------------------------------------------------------

	public final static CCSQLTableDef[] TABLES = new CCSQLTableDef[]{ TAB_MAIN, TAB_SEASONS, TAB_EPISODES, TAB_INFO, TAB_GROUPS, TAB_COVERS, TAB_HISTORY, TAB_TEMP };

	//--------------------------------------------------------------------------------------------------

	public final static String INFOKEY_DBVERSION    = "VERSION_DB";                             //$NON-NLS-1$
	public final static String INFOKEY_DATE         = "CREATION_DATE";                          //$NON-NLS-1$
	public final static String INFOKEY_TIME         = "CREATION_TIME";                          //$NON-NLS-1$
	public final static String INFOKEY_USERNAME     = "CREATION_USERNAME";                      //$NON-NLS-1$
	public final static String INFOKEY_DUUID        = "DATABASE_UNIVERSALLY_UNIQUE_IDENTIFIER"; //$NON-NLS-1$
	public final static String INFOKEY_HISTORY      = "HISTORY_ENABLED";                        //$NON-NLS-1$
	public final static String INFOKEY_LASTID       = "LAST_ID";                                //$NON-NLS-1$
	public final static String INFOKEY_LASTCOVERID  = "LAST_COVERID";                           //$NON-NLS-1$

	public final static String[] INFOKEYS = new String[]{ INFOKEY_DBVERSION, INFOKEY_DATE, INFOKEY_TIME, INFOKEY_USERNAME, INFOKEY_DUUID, INFOKEY_HISTORY, INFOKEY_LASTID, INFOKEY_LASTCOVERID };

}
