package de.jClipCorn.database.driver;

import java.sql.SQLException;
import java.util.ArrayList;

import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.sqlwrapper.SQLOrder;
import de.jClipCorn.util.sqlwrapper.*;

@SuppressWarnings("nls")
public class Statements {
	public final static String TAB_MAIN                  = "ELEMENTS";  //$NON-NLS-1$
	public final static String TAB_SEASONS               = "SEASONS";   //$NON-NLS-1$
	public final static String TAB_EPISODES              = "EPISODES";  //$NON-NLS-1$
	public final static String TAB_INFO                  = "INFO";      //$NON-NLS-1$
	public final static String TAB_GROUPS                = "GROUPS";    //$NON-NLS-1$
	public final static String TAB_COVERS                = "COVERS";    //$NON-NLS-1$
	public final static String TAB_HISTORY               = "HISTORY";   //$NON-NLS-1$
	public final static String TAB_TEMP                  = "TEMP";      //$NON-NLS-1$

	public final static CCSQLColDef COL_TEMP_KEY             = new CCSQLColDef("IKEY",                  CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_TEMP_VALUE           = new CCSQLColDef("IVALUE",                CCSQLType.VARCHAR);    //$NON-NLS-1$

	public final static CCSQLColDef COL_INFO_KEY             = new CCSQLColDef("IKEY",                  CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_INFO_VALUE           = new CCSQLColDef("IVALUE",                CCSQLType.VARCHAR);    //$NON-NLS-1$

	public final static CCSQLColDef COL_HISTORY_TABLE         = new CCSQLColDef("TABLE",                CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_HISTORY_ID            = new CCSQLColDef("ID",                   CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_HISTORY_DATE          = new CCSQLColDef("DATE",                 CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_HISTORY_ACTION        = new CCSQLColDef("ACTION",               CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_HISTORY_FIELD         = new CCSQLColDef("FIELD",                CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_HISTORY_OLD           = new CCSQLColDef("OLD",                  CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_HISTORY_NEW           = new CCSQLColDef("NEW",                  CCSQLType.VARCHAR);    //$NON-NLS-1$

	public final static CCSQLColDef COL_MAIN_LOCALID          = new CCSQLColDef("LOCALID",              CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_NAME             = new CCSQLColDef("NAME",                 CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_VIEWED           = new CCSQLColDef("VIEWED",               CCSQLType.BIT);        //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_VIEWEDHISTORY    = new CCSQLColDef("VIEWED_HISTORY",       CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_ZYKLUS           = new CCSQLColDef("ZYKLUS",               CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_ZYKLUSNUMBER     = new CCSQLColDef("ZYKLUSNUMBER",         CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_LANGUAGE         = new CCSQLColDef("LANGUAGE",             CCSQLType.BIGINT);     //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_GENRE            = new CCSQLColDef("GENRE",                CCSQLType.BIGINT);     //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_LENGTH           = new CCSQLColDef("LENGTH",               CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_ADDDATE          = new CCSQLColDef("ADDDATE",              CCSQLType.DATE);       //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_ONLINESCORE      = new CCSQLColDef("ONLINESCORE",          CCSQLType.TINYINT);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_FSK              = new CCSQLColDef("FSK",                  CCSQLType.TINYINT);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_FORMAT           = new CCSQLColDef("FORMAT",               CCSQLType.TINYINT);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_MOVIEYEAR        = new CCSQLColDef("MOVIEYEAR",            CCSQLType.SMALLINT);   //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_ONLINEREF        = new CCSQLColDef("ONLINEREF",            CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_GROUPS           = new CCSQLColDef("GROUPS",               CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_FILESIZE         = new CCSQLColDef("FILESIZE",             CCSQLType.BIGINT);     //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_TAGS             = new CCSQLColDef("TAGS",                 CCSQLType.SMALLINT);   //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_PART_1           = new CCSQLColDef("PART1",                CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_PART_2           = new CCSQLColDef("PART2",                CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_PART_3           = new CCSQLColDef("PART3",                CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_PART_4           = new CCSQLColDef("PART4",                CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_PART_5           = new CCSQLColDef("PART5",                CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_PART_6           = new CCSQLColDef("PART6",                CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_SCORE            = new CCSQLColDef("SCORE",                CCSQLType.TINYINT);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_COVERID          = new CCSQLColDef("COVERID",              CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_TYPE             = new CCSQLColDef("TYPE",                 CCSQLType.TINYINT);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_SERIES_ID        = new CCSQLColDef("SERIESID",             CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_MI_FILESIZE      = new CCSQLColDef("MEDIAINFO.FILESIZE",   CCSQLType.BIGINT);     //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_MI_CDATE         = new CCSQLColDef("MEDIAINFO.CDATE",      CCSQLType.BIGINT);     //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_MI_MDATE         = new CCSQLColDef("MEDIAINFO.MDATE",      CCSQLType.BIGINT);     //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_MI_AFORMAT       = new CCSQLColDef("MEDIAINFO.AFORMAT",    CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_MI_VFORMAT       = new CCSQLColDef("MEDIAINFO.VFORMAT",    CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_MI_WIDTH         = new CCSQLColDef("MEDIAINFO.WIDTH",      CCSQLType.SMALLINT);   //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_MI_HEIGHT        = new CCSQLColDef("MEDIAINFO.HEIGHT",     CCSQLType.SMALLINT);   //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_MI_FRAMERATE     = new CCSQLColDef("MEDIAINFO.FRAMERATE",  CCSQLType.REAL);       //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_MI_DURATION      = new CCSQLColDef("MEDIAINFO.DURATION",   CCSQLType.REAL);       //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_MI_BITDEPTH      = new CCSQLColDef("MEDIAINFO.BITDEPTH",   CCSQLType.TINYINT);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_MI_BITRATE       = new CCSQLColDef("MEDIAINFO.BITRATE",    CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_MI_FRAMECOUNT    = new CCSQLColDef("MEDIAINFO.FRAMECOUNT", CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_MI_ACHANNELS     = new CCSQLColDef("MEDIAINFO.ACHANNELS",  CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_MI_VCODEC        = new CCSQLColDef("MEDIAINFO.VCODEC",     CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_MI_ACODEC        = new CCSQLColDef("MEDIAINFO.ACODEC",     CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_MI_SAMPLERATE    = new CCSQLColDef("MEDIAINFO.SAMPLERATE", CCSQLType.INTEGER);    //$NON-NLS-1$

	public final static CCSQLColDef COL_SEAS_SEASONID         = new CCSQLColDef("SEASONID",             CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_SEAS_SERIESID         = new CCSQLColDef("SERIESID",             CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_SEAS_NAME             = new CCSQLColDef("NAME",                 CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_SEAS_YEAR             = new CCSQLColDef("SEASONYEAR",           CCSQLType.SMALLINT);   //$NON-NLS-1$
	public final static CCSQLColDef COL_SEAS_COVERID          = new CCSQLColDef("COVERID",              CCSQLType.INTEGER);    //$NON-NLS-1$

	public final static CCSQLColDef COL_EPIS_LOCALID          = new CCSQLColDef("LOCALID",              CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_SEASONID         = new CCSQLColDef("SEASONID",             CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_EPISODE          = new CCSQLColDef("EPISODE",              CCSQLType.SMALLINT);   //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_NAME             = new CCSQLColDef("NAME",                 CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_VIEWED           = new CCSQLColDef("VIEWED",               CCSQLType.BIT);        //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_VIEWEDHISTORY    = new CCSQLColDef("VIEWED_HISTORY",       CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_LENGTH           = new CCSQLColDef("LENGTH",               CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_FORMAT           = new CCSQLColDef("FORMAT",               CCSQLType.TINYINT);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_FILESIZE         = new CCSQLColDef("FILESIZE",             CCSQLType.BIGINT);     //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_PART_1           = new CCSQLColDef("PART1",                CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_TAGS             = new CCSQLColDef("TAGS",                 CCSQLType.SMALLINT);   //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_ADDDATE          = new CCSQLColDef("ADDDATE",              CCSQLType.DATE);       //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_LANGUAGE         = new CCSQLColDef("LANGUAGE",             CCSQLType.BIGINT);     //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_MI_FILESIZE      = new CCSQLColDef("MEDIAINFO.FILESIZE",   CCSQLType.BIGINT);     //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_MI_CDATE         = new CCSQLColDef("MEDIAINFO.CDATE",      CCSQLType.BIGINT);     //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_MI_MDATE         = new CCSQLColDef("MEDIAINFO.MDATE",      CCSQLType.BIGINT);     //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_MI_AFORMAT       = new CCSQLColDef("MEDIAINFO.AFORMAT",    CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_MI_VFORMAT       = new CCSQLColDef("MEDIAINFO.VFORMAT",    CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_MI_WIDTH         = new CCSQLColDef("MEDIAINFO.WIDTH",      CCSQLType.SMALLINT);   //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_MI_HEIGHT        = new CCSQLColDef("MEDIAINFO.HEIGHT",     CCSQLType.SMALLINT);   //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_MI_FRAMERATE     = new CCSQLColDef("MEDIAINFO.FRAMERATE",  CCSQLType.REAL);       //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_MI_DURATION      = new CCSQLColDef("MEDIAINFO.DURATION",   CCSQLType.REAL);       //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_MI_BITDEPTH      = new CCSQLColDef("MEDIAINFO.BITDEPTH",   CCSQLType.TINYINT);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_MI_BITRATE       = new CCSQLColDef("MEDIAINFO.BITRATE",    CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_MI_FRAMECOUNT    = new CCSQLColDef("MEDIAINFO.FRAMECOUNT", CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_MI_ACHANNELS     = new CCSQLColDef("MEDIAINFO.ACHANNELS",  CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_MI_VCODEC        = new CCSQLColDef("MEDIAINFO.VCODEC",     CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_MI_ACODEC        = new CCSQLColDef("MEDIAINFO.ACODEC",     CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_MI_SAMPLERATE    = new CCSQLColDef("MEDIAINFO.SAMPLERATE", CCSQLType.INTEGER);    //$NON-NLS-1$

	public final static CCSQLColDef COL_GRPS_NAME             = new CCSQLColDef("NAME",                 CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_GRPS_ORDER            = new CCSQLColDef("ORDERING",             CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_GRPS_COLOR            = new CCSQLColDef("COLOR",                CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_GRPS_SERIALIZE        = new CCSQLColDef("SERIALIZE",            CCSQLType.BIT);        //$NON-NLS-1$
	public final static CCSQLColDef COL_GRPS_PARENT           = new CCSQLColDef("PARENTGROUP",          CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_GRPS_VISIBLE          = new CCSQLColDef("VISIBLE",              CCSQLType.BIT);        //$NON-NLS-1$

	public final static CCSQLColDef COL_CVRS_ID               = new CCSQLColDef("ID",                   CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_CVRS_FILENAME         = new CCSQLColDef("FILENAME",             CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_CVRS_WIDTH            = new CCSQLColDef("WIDTH",                CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_CVRS_HEIGHT           = new CCSQLColDef("HEIGHT",               CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_CVRS_HASH_FILE        = new CCSQLColDef("HASH_FILE",            CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_CVRS_FILESIZE         = new CCSQLColDef("FILESIZE",             CCSQLType.BIGINT);     //$NON-NLS-1$
	public final static CCSQLColDef COL_CVRS_PREVIEWTYPE      = new CCSQLColDef("PREVIEW_TYPE",         CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_CVRS_PREVIEW          = new CCSQLColDef("PREVIEW",              CCSQLType.BLOB);       //$NON-NLS-1$
	public final static CCSQLColDef COL_CVRS_CREATED          = new CCSQLColDef("CREATED",              CCSQLType.VARCHAR);    //$NON-NLS-1$

	//--------------------------------------------------------------------------------------------------

	public static CCSQLStatement addEmptyMainTabStatement;
	public static CCSQLStatement addEmptySeasonTabStatement;
	public static CCSQLStatement addEmptyEpisodeTabStatement;
	
	public static CCSQLStatement newLocalIDStatement;
	public static CCSQLStatement newSeriesIDStatement;
	public static CCSQLStatement newSeasonIDStatement;
	public static CCSQLStatement newEpisodeIDStatement;

	public static CCSQLStatement updateMainTabStatement;
	public static CCSQLStatement updateSeriesTabStatement;
	public static CCSQLStatement updateSeasonTabStatement;
	public static CCSQLStatement updateEpisodeTabStatement;
	
	public static CCSQLStatement selectAllMainTabStatement;
	public static CCSQLStatement selectAllSeasonTabStatement;
	public static CCSQLStatement selectAllEpisodeTabStatement;

	public static CCSQLStatement selectSeasonTabStatement;
	public static CCSQLStatement selectEpisodeTabStatement;
	
	public static CCSQLStatement deleteMainTabStatement;
	public static CCSQLStatement deleteSeasonTabStatement;
	public static CCSQLStatement deleteEpisodeTabStatement;
	
	public static CCSQLStatement selectSingleMainTabStatement;
	public static CCSQLStatement selectSingleSeasonTabStatement;
	public static CCSQLStatement selectSingleEpisodeTabStatement;

	public static CCSQLStatement addInfoKeyStatement;
	public static CCSQLStatement selectInfoKeyStatement;
	public static CCSQLStatement updateInfoKeyStatement;

	public static CCSQLStatement selectGroupsStatement;
	public static CCSQLStatement insertGroupStatement;
	public static CCSQLStatement removeGroupStatement;
	public static CCSQLStatement updateGroupStatement;
	public static CCSQLStatement removeAllGroupsStatement;

	public static CCSQLStatement selectCoversFullStatement;
	public static CCSQLStatement selectCoversFastStatement;
	public static CCSQLStatement selectCoversSingleStatement;
	public static CCSQLStatement insertCoversStatement;
	public static CCSQLStatement removeCoversStatement;

	private static ArrayList<CCSQLStatement> statements = new ArrayList<>();

	public static void intialize(CCDatabase d) {
		try {
			statements = new ArrayList<>();

			addEmptyMainTabStatement = SQLBuilder.createInsert(TAB_MAIN)
					.addPreparedFields(COL_MAIN_LOCALID, COL_MAIN_NAME, COL_MAIN_VIEWED, COL_MAIN_VIEWEDHISTORY, COL_MAIN_ZYKLUS, COL_MAIN_ZYKLUSNUMBER)
					.addPreparedFields(COL_MAIN_LANGUAGE, COL_MAIN_GENRE, COL_MAIN_LENGTH, COL_MAIN_ADDDATE, COL_MAIN_ONLINESCORE)
					.addPreparedFields(COL_MAIN_FSK, COL_MAIN_FORMAT, COL_MAIN_MOVIEYEAR, COL_MAIN_ONLINEREF, COL_MAIN_GROUPS, COL_MAIN_FILESIZE, COL_MAIN_TAGS)
					.addPreparedFields(COL_MAIN_PART_1, COL_MAIN_PART_2, COL_MAIN_PART_3, COL_MAIN_PART_4, COL_MAIN_PART_5, COL_MAIN_PART_6)
					.addPreparedFields(COL_MAIN_SCORE, COL_MAIN_COVERID, COL_MAIN_TYPE, COL_MAIN_SERIES_ID)
				.build(d, statements);

			addEmptySeasonTabStatement = SQLBuilder.createInsert(TAB_SEASONS)
					.addPreparedFields(COL_SEAS_SEASONID, COL_SEAS_SERIESID, COL_SEAS_NAME, COL_SEAS_YEAR, COL_SEAS_COVERID)
					.build(d, statements);

			addEmptyEpisodeTabStatement = SQLBuilder.createInsert(TAB_EPISODES)
					.addPreparedFields(COL_EPIS_LOCALID, COL_EPIS_SEASONID, COL_EPIS_EPISODE, COL_EPIS_NAME, COL_EPIS_VIEWED, COL_EPIS_VIEWEDHISTORY)
					.addPreparedFields(COL_EPIS_LENGTH, COL_EPIS_FORMAT, COL_EPIS_FILESIZE, COL_EPIS_PART_1, COL_EPIS_TAGS)
					.addPreparedFields(COL_EPIS_ADDDATE, COL_EPIS_LANGUAGE)
					.build(d, statements);

			newLocalIDStatement  = SQLBuilder.createCustom(TAB_MAIN).setSQL("SELECT COALESCE(MAX(%s), -1) FROM {TAB}", COL_MAIN_LOCALID.Name).build(d, statements);

			newSeriesIDStatement = SQLBuilder.createCustom(TAB_MAIN).setSQL("SELECT COALESCE(MAX(%s), -1) FROM {TAB}", COL_MAIN_SERIES_ID.Name).build(d, statements);

			newSeasonIDStatement = SQLBuilder.createCustom(TAB_SEASONS) .setSQL("SELECT COALESCE(MAX(%s), -1) FROM {TAB}", COL_SEAS_SEASONID.Name).build(d, statements);

			newEpisodeIDStatement = SQLBuilder.createCustom(TAB_EPISODES).setSQL("SELECT COALESCE(MAX(%s), -1) FROM {TAB}", COL_EPIS_LOCALID.Name).build(d, statements);

			updateMainTabStatement = SQLBuilder.createUpdate(TAB_MAIN)
					.addPreparedWhereCondition(COL_MAIN_LOCALID)
					.addPreparedFields(COL_MAIN_NAME, COL_MAIN_VIEWED, COL_MAIN_VIEWEDHISTORY, COL_MAIN_ZYKLUS, COL_MAIN_ZYKLUSNUMBER)
					.addPreparedFields(COL_MAIN_LANGUAGE, COL_MAIN_GENRE, COL_MAIN_LENGTH, COL_MAIN_ADDDATE, COL_MAIN_ONLINESCORE, COL_MAIN_FSK, COL_MAIN_FORMAT)
					.addPreparedFields(COL_MAIN_MOVIEYEAR, COL_MAIN_ONLINEREF, COL_MAIN_GROUPS, COL_MAIN_FILESIZE, COL_MAIN_TAGS)
					.addPreparedFields(COL_MAIN_PART_1, COL_MAIN_PART_2, COL_MAIN_PART_3, COL_MAIN_PART_4, COL_MAIN_PART_5, COL_MAIN_PART_6)
					.addPreparedFields(COL_MAIN_SCORE, COL_MAIN_COVERID, COL_MAIN_TYPE, COL_MAIN_SERIES_ID)
					.addPreparedFields(COL_MAIN_MI_FILESIZE, COL_MAIN_MI_CDATE, COL_MAIN_MI_MDATE, COL_MAIN_MI_AFORMAT, COL_MAIN_MI_VFORMAT, COL_MAIN_MI_WIDTH)
					.addPreparedFields(COL_MAIN_MI_HEIGHT, COL_MAIN_MI_FRAMERATE, COL_MAIN_MI_DURATION, COL_MAIN_MI_BITDEPTH, COL_MAIN_MI_BITRATE, COL_MAIN_MI_FRAMECOUNT)
					.addPreparedFields(COL_MAIN_MI_ACHANNELS, COL_MAIN_MI_VCODEC, COL_MAIN_MI_ACODEC, COL_MAIN_MI_SAMPLERATE)
					.build(d, statements);

			updateSeriesTabStatement = SQLBuilder.createUpdate(TAB_MAIN)
					.addPreparedWhereCondition(COL_MAIN_LOCALID)
					.addPreparedFields(COL_MAIN_NAME, COL_MAIN_GENRE, COL_MAIN_ONLINESCORE, COL_MAIN_FSK, COL_MAIN_ONLINEREF, COL_MAIN_GROUPS)
					.addPreparedFields(COL_MAIN_SCORE, COL_MAIN_COVERID, COL_MAIN_TYPE, COL_MAIN_SERIES_ID, COL_MAIN_TAGS)
					.build(d, statements);

			updateSeasonTabStatement = SQLBuilder.createUpdate(TAB_SEASONS)
					.addPreparedWhereCondition(COL_SEAS_SEASONID)
					.addPreparedFields(COL_SEAS_SERIESID, COL_SEAS_NAME, COL_SEAS_YEAR, COL_SEAS_COVERID)
					.build(d, statements);

			updateEpisodeTabStatement = SQLBuilder.createUpdate(TAB_EPISODES)
					.addPreparedWhereCondition(COL_EPIS_LOCALID)
					.addPreparedFields(COL_EPIS_SEASONID, COL_EPIS_EPISODE, COL_EPIS_NAME, COL_EPIS_VIEWED, COL_EPIS_VIEWEDHISTORY)
					.addPreparedFields(COL_EPIS_LENGTH, COL_EPIS_FORMAT, COL_EPIS_FILESIZE, COL_EPIS_PART_1, COL_EPIS_TAGS, COL_EPIS_ADDDATE, COL_EPIS_LANGUAGE)
					.addPreparedFields(COL_EPIS_MI_FILESIZE, COL_EPIS_MI_CDATE, COL_EPIS_MI_MDATE, COL_EPIS_MI_AFORMAT, COL_EPIS_MI_VFORMAT, COL_EPIS_MI_WIDTH)
					.addPreparedFields(COL_EPIS_MI_HEIGHT, COL_EPIS_MI_FRAMERATE, COL_EPIS_MI_DURATION, COL_EPIS_MI_BITDEPTH, COL_EPIS_MI_BITRATE, COL_EPIS_MI_FRAMECOUNT)
					.addPreparedFields(COL_EPIS_MI_ACHANNELS, COL_EPIS_MI_VCODEC, COL_EPIS_MI_ACODEC, COL_EPIS_MI_SAMPLERATE)
					.build(d, statements);

			selectAllMainTabStatement = SQLBuilder.createSelect(TAB_MAIN)
					.addSelectFields(COL_MAIN_LOCALID, COL_MAIN_NAME, COL_MAIN_VIEWED, COL_MAIN_VIEWEDHISTORY, COL_MAIN_ZYKLUS, COL_MAIN_ZYKLUSNUMBER)
					.addSelectFields(COL_MAIN_LANGUAGE, COL_MAIN_GENRE, COL_MAIN_LENGTH, COL_MAIN_ADDDATE, COL_MAIN_ONLINESCORE)
					.addSelectFields(COL_MAIN_FSK, COL_MAIN_FORMAT, COL_MAIN_MOVIEYEAR, COL_MAIN_ONLINEREF, COL_MAIN_GROUPS, COL_MAIN_FILESIZE, COL_MAIN_TAGS)
					.addSelectFields(COL_MAIN_PART_1, COL_MAIN_PART_2, COL_MAIN_PART_3, COL_MAIN_PART_4, COL_MAIN_PART_5, COL_MAIN_PART_6)
					.addSelectFields(COL_MAIN_SCORE, COL_MAIN_COVERID, COL_MAIN_TYPE, COL_MAIN_SERIES_ID)
					.addSelectFields(COL_MAIN_MI_FILESIZE, COL_MAIN_MI_CDATE, COL_MAIN_MI_MDATE, COL_MAIN_MI_AFORMAT, COL_MAIN_MI_VFORMAT, COL_MAIN_MI_WIDTH)
					.addSelectFields(COL_MAIN_MI_HEIGHT, COL_MAIN_MI_FRAMERATE, COL_MAIN_MI_DURATION, COL_MAIN_MI_BITDEPTH, COL_MAIN_MI_BITRATE, COL_MAIN_MI_FRAMECOUNT)
					.addSelectFields(COL_MAIN_MI_ACHANNELS, COL_MAIN_MI_VCODEC, COL_MAIN_MI_ACODEC, COL_MAIN_MI_SAMPLERATE)
					.setOrder(COL_MAIN_LOCALID, SQLOrder.ASC)
					.build(d, statements);

			selectAllSeasonTabStatement = SQLBuilder.createSelect(TAB_SEASONS)
					.addSelectFields(COL_SEAS_SERIESID, COL_SEAS_SEASONID, COL_SEAS_NAME, COL_SEAS_YEAR, COL_SEAS_COVERID)
					.setOrder(COL_SEAS_SERIESID, SQLOrder.ASC)
					.build(d, statements);

			selectAllEpisodeTabStatement = SQLBuilder.createSelect(TAB_EPISODES)
					.addSelectFields(COL_EPIS_LOCALID, COL_EPIS_SEASONID, COL_EPIS_EPISODE, COL_EPIS_NAME, COL_EPIS_VIEWED, COL_EPIS_VIEWEDHISTORY)
					.addSelectFields(COL_EPIS_LENGTH, COL_EPIS_FORMAT, COL_EPIS_FILESIZE, COL_EPIS_PART_1, COL_EPIS_TAGS)
					.addSelectFields(COL_EPIS_ADDDATE, COL_EPIS_LANGUAGE)
					.addSelectFields(COL_EPIS_MI_FILESIZE, COL_EPIS_MI_CDATE, COL_EPIS_MI_MDATE, COL_EPIS_MI_AFORMAT, COL_EPIS_MI_VFORMAT, COL_EPIS_MI_WIDTH)
					.addSelectFields(COL_EPIS_MI_HEIGHT, COL_EPIS_MI_FRAMERATE, COL_EPIS_MI_DURATION, COL_EPIS_MI_BITDEPTH, COL_EPIS_MI_BITRATE, COL_EPIS_MI_FRAMECOUNT)
					.addSelectFields(COL_EPIS_MI_ACHANNELS, COL_EPIS_MI_VCODEC, COL_EPIS_MI_ACODEC, COL_EPIS_MI_SAMPLERATE)
					.setOrder(COL_EPIS_SEASONID, SQLOrder.ASC)
					.build(d, statements);

			selectSeasonTabStatement = SQLBuilder.createSelect(TAB_SEASONS)
					.addSelectFields(COL_SEAS_SERIESID, COL_SEAS_SEASONID, COL_SEAS_NAME, COL_SEAS_YEAR, COL_SEAS_COVERID)
					.addPreparedWhereCondition(COL_SEAS_SERIESID)
					.setOrder(COL_SEAS_SERIESID, SQLOrder.ASC)
					.build(d, statements);

			selectEpisodeTabStatement = SQLBuilder.createSelect(TAB_EPISODES)
					.addSelectFields(COL_EPIS_LOCALID, COL_EPIS_SEASONID, COL_EPIS_EPISODE, COL_EPIS_NAME, COL_EPIS_VIEWED, COL_EPIS_VIEWEDHISTORY)
					.addSelectFields(COL_EPIS_LENGTH, COL_EPIS_FORMAT, COL_EPIS_FILESIZE, COL_EPIS_PART_1, COL_EPIS_TAGS)
					.addSelectFields(COL_EPIS_ADDDATE, COL_EPIS_LANGUAGE)
					.addSelectFields(COL_EPIS_MI_FILESIZE, COL_EPIS_MI_CDATE, COL_EPIS_MI_MDATE, COL_EPIS_MI_AFORMAT, COL_EPIS_MI_VFORMAT, COL_EPIS_MI_WIDTH)
					.addSelectFields(COL_EPIS_MI_HEIGHT, COL_EPIS_MI_FRAMERATE, COL_EPIS_MI_DURATION, COL_EPIS_MI_BITDEPTH, COL_EPIS_MI_BITRATE, COL_EPIS_MI_FRAMECOUNT)
					.addSelectFields(COL_EPIS_MI_ACHANNELS, COL_EPIS_MI_VCODEC, COL_EPIS_MI_ACODEC, COL_EPIS_MI_SAMPLERATE)
					.addPreparedWhereCondition(COL_EPIS_SEASONID)
					.setOrder(COL_EPIS_EPISODE, SQLOrder.ASC)
					.build(d, statements);

			deleteMainTabStatement = SQLBuilder.createDelete(TAB_MAIN).addPreparedWhereCondition(COL_MAIN_LOCALID).build(d, statements);

			deleteSeasonTabStatement = SQLBuilder.createDelete(TAB_SEASONS).addPreparedWhereCondition(COL_SEAS_SEASONID).build(d, statements);

			deleteEpisodeTabStatement = SQLBuilder.createDelete(TAB_EPISODES).addPreparedWhereCondition(COL_EPIS_LOCALID).build(d, statements);

			selectSingleMainTabStatement = SQLBuilder.createSelect(TAB_MAIN)
					.addSelectFields(COL_MAIN_LOCALID, COL_MAIN_NAME, COL_MAIN_VIEWED, COL_MAIN_VIEWEDHISTORY, COL_MAIN_ZYKLUS, COL_MAIN_ZYKLUSNUMBER)
					.addSelectFields(COL_MAIN_LANGUAGE, COL_MAIN_GENRE, COL_MAIN_LENGTH, COL_MAIN_ADDDATE, COL_MAIN_ONLINESCORE)
					.addSelectFields(COL_MAIN_FSK, COL_MAIN_FORMAT, COL_MAIN_MOVIEYEAR, COL_MAIN_ONLINEREF, COL_MAIN_GROUPS, COL_MAIN_FILESIZE, COL_MAIN_TAGS)
					.addSelectFields(COL_MAIN_PART_1, COL_MAIN_PART_2, COL_MAIN_PART_3, COL_MAIN_PART_4, COL_MAIN_PART_5, COL_MAIN_PART_6)
					.addSelectFields(COL_MAIN_SCORE, COL_MAIN_COVERID, COL_MAIN_TYPE, COL_MAIN_SERIES_ID)
					.addSelectFields(COL_MAIN_MI_FILESIZE, COL_MAIN_MI_CDATE, COL_MAIN_MI_MDATE, COL_MAIN_MI_AFORMAT, COL_MAIN_MI_VFORMAT, COL_MAIN_MI_WIDTH)
					.addSelectFields(COL_MAIN_MI_HEIGHT, COL_MAIN_MI_FRAMERATE, COL_MAIN_MI_DURATION, COL_MAIN_MI_BITDEPTH, COL_MAIN_MI_BITRATE, COL_MAIN_MI_FRAMECOUNT)
					.addSelectFields(COL_MAIN_MI_ACHANNELS, COL_MAIN_MI_VCODEC, COL_MAIN_MI_ACODEC, COL_MAIN_MI_SAMPLERATE)
					.addPreparedWhereCondition(COL_MAIN_LOCALID)
					.build(d, statements);

			selectSingleSeasonTabStatement = SQLBuilder.createSelect(TAB_SEASONS)
					.addSelectFields(COL_SEAS_SERIESID, COL_SEAS_SEASONID, COL_SEAS_NAME, COL_SEAS_YEAR, COL_SEAS_COVERID)
					.addPreparedWhereCondition(COL_SEAS_SEASONID)
					.build(d, statements);

			selectSingleEpisodeTabStatement = SQLBuilder.createSelect(TAB_EPISODES)
					.addSelectFields(COL_EPIS_LOCALID, COL_EPIS_SEASONID, COL_EPIS_EPISODE, COL_EPIS_NAME, COL_EPIS_VIEWED, COL_EPIS_VIEWEDHISTORY)
					.addSelectFields(COL_EPIS_LENGTH, COL_EPIS_FORMAT, COL_EPIS_FILESIZE, COL_EPIS_PART_1, COL_EPIS_TAGS)
					.addSelectFields(COL_EPIS_ADDDATE, COL_EPIS_LANGUAGE)
					.addSelectFields(COL_EPIS_MI_FILESIZE, COL_EPIS_MI_CDATE, COL_EPIS_MI_MDATE, COL_EPIS_MI_AFORMAT, COL_EPIS_MI_VFORMAT, COL_EPIS_MI_WIDTH)
					.addSelectFields(COL_EPIS_MI_HEIGHT, COL_EPIS_MI_FRAMERATE, COL_EPIS_MI_DURATION, COL_EPIS_MI_BITDEPTH, COL_EPIS_MI_BITRATE, COL_EPIS_MI_FRAMECOUNT)
					.addSelectFields(COL_EPIS_MI_ACHANNELS, COL_EPIS_MI_VCODEC, COL_EPIS_MI_ACODEC, COL_EPIS_MI_SAMPLERATE)
					.addPreparedWhereCondition(COL_EPIS_LOCALID)
					.build(d, statements);

			selectInfoKeyStatement = SQLBuilder.createSelect(TAB_INFO)
					.addSelectField(COL_INFO_VALUE)
					.addPreparedWhereCondition(COL_INFO_KEY)
					.build(d, statements);

			addInfoKeyStatement = SQLBuilder.createInsert(TAB_INFO)
					.addPreparedFields(COL_INFO_KEY, COL_INFO_VALUE)
					.build(d, statements);

			updateInfoKeyStatement = SQLBuilder.createUpdate(TAB_INFO)
					.addPreparedWhereCondition(COL_INFO_KEY)
					.addPreparedField(COL_INFO_VALUE)
					.build(d, statements);

			selectGroupsStatement = SQLBuilder.createSelect(TAB_GROUPS)
					.addSelectFields(COL_GRPS_NAME, COL_GRPS_COLOR, COL_GRPS_ORDER, COL_GRPS_PARENT, COL_GRPS_SERIALIZE, COL_GRPS_VISIBLE)
					.build(d, statements);

			insertGroupStatement = SQLBuilder.createInsert(TAB_GROUPS)
					.addPreparedFields(COL_GRPS_NAME, COL_GRPS_COLOR, COL_GRPS_ORDER, COL_GRPS_PARENT, COL_GRPS_SERIALIZE, COL_GRPS_VISIBLE)
					.build(d, statements);

			updateGroupStatement = SQLBuilder.createUpdate(TAB_GROUPS)
					.addPreparedWhereCondition(COL_GRPS_NAME)
					.addPreparedFields(COL_GRPS_COLOR, COL_GRPS_ORDER, COL_GRPS_PARENT, COL_GRPS_SERIALIZE, COL_GRPS_VISIBLE)
					.build(d, statements);

			removeGroupStatement = SQLBuilder.createDelete(TAB_GROUPS).addPreparedWhereCondition(COL_GRPS_NAME).build(d, statements);

			removeAllGroupsStatement = SQLBuilder.createDelete(TAB_GROUPS).build(d, statements);

			selectCoversFullStatement = SQLBuilder.createSelect(TAB_COVERS)
					.addSelectFields(COL_CVRS_ID, COL_CVRS_FILENAME, COL_CVRS_WIDTH, COL_CVRS_HEIGHT, COL_CVRS_HASH_FILE)
					.addSelectFields(COL_CVRS_FILESIZE, COL_CVRS_PREVIEW, COL_CVRS_PREVIEWTYPE, COL_CVRS_CREATED)
					.build(d, statements);

			selectCoversFastStatement = SQLBuilder.createSelect(TAB_COVERS)
					.addSelectFields(COL_CVRS_ID, COL_CVRS_FILENAME, COL_CVRS_WIDTH, COL_CVRS_HEIGHT, COL_CVRS_FILESIZE)
					.addSelectFields(COL_CVRS_HASH_FILE, COL_CVRS_CREATED, COL_CVRS_PREVIEWTYPE)
					.build(d, statements);

			selectCoversSingleStatement = SQLBuilder.createSelect(TAB_COVERS)
					.addSelectFields(COL_CVRS_PREVIEWTYPE)
					.addPreparedWhereCondition(COL_CVRS_ID)
					.build(d, statements);

			insertCoversStatement = SQLBuilder.createInsert(TAB_COVERS)
					.addPreparedFields(COL_CVRS_ID, COL_CVRS_FILENAME, COL_CVRS_WIDTH, COL_CVRS_HEIGHT, COL_CVRS_HASH_FILE)
					.addPreparedFields(COL_CVRS_FILESIZE, COL_CVRS_PREVIEW, COL_CVRS_PREVIEWTYPE, COL_CVRS_CREATED)
					.build(d, statements);

			removeCoversStatement = SQLBuilder.createDelete(TAB_COVERS)
					.addPreparedWhereCondition(COL_CVRS_ID)
					.build(d, statements);

			if (!CCLog.isUnitTest()) CCLog.addDebug(String.format("%d SQL Statements prepared", statements.size())); //$NON-NLS-1$

		} catch (SQLException | SQLWrapperException e) {
			CCLog.addFatalError(LocaleBundle.getString("LogMessage.CouldNotCreatePreparedStatement"), e);
		}
	}
	
	public static void shutdown() {
		try {
			for (CCSQLStatement stmt : statements) stmt.tryClose();
		} catch (Exception e) {
			CCLog.addFatalError(e);
		}	
	}
}
