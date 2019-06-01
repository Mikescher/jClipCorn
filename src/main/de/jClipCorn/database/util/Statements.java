package de.jClipCorn.database.util;

import java.sql.SQLException;
import java.util.ArrayList;

import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.datatypes.DoubleString;
import de.jClipCorn.util.sql.SQLDeleteHelper;
import de.jClipCorn.util.sql.SQLInsertHelper;
import de.jClipCorn.util.sqlwrapper.SQLOrder;
import de.jClipCorn.util.sql.SQLSelectHelper;
import de.jClipCorn.util.sql.SQLUpdateHelper;
import de.jClipCorn.util.sqlwrapper.*;

@SuppressWarnings("nls")
public class Statements {
	public final static String TAB_MAIN                  = "ELEMENTS";  //$NON-NLS-1$
	public final static String TAB_SEASONS               = "SEASONS";   //$NON-NLS-1$
	public final static String TAB_EPISODES              = "EPISODES";  //$NON-NLS-1$
	public final static String TAB_INFO                  = "INFO";      //$NON-NLS-1$
	public final static String TAB_GROUPS                = "GROUPS";    //$NON-NLS-1$

	public final static CCSQLColDef COL_INFO_KEY             = new CCSQLColDef("IKEY",            CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_INFO_VALUE           = new CCSQLColDef("IVALUE",          CCSQLType.VARCHAR);    //$NON-NLS-1$

	public final static CCSQLColDef COL_MAIN_LOCALID          = new CCSQLColDef("LOCALID",        CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_NAME             = new CCSQLColDef("NAME",           CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_VIEWED           = new CCSQLColDef("VIEWED",         CCSQLType.BIT);        //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_VIEWEDHISTORY    = new CCSQLColDef("VIEWED_HISTORY", CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_ZYKLUS           = new CCSQLColDef("ZYKLUS",         CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_ZYKLUSNUMBER     = new CCSQLColDef("ZYKLUSNUMBER",   CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_QUALITY          = new CCSQLColDef("QUALITY",        CCSQLType.TINYINT);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_LANGUAGE         = new CCSQLColDef("LANGUAGE",       CCSQLType.BIGINT);     //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_GENRE            = new CCSQLColDef("GENRE",          CCSQLType.BIGINT);     //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_LENGTH           = new CCSQLColDef("LENGTH",         CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_ADDDATE          = new CCSQLColDef("ADDDATE",        CCSQLType.DATE);       //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_ONLINESCORE      = new CCSQLColDef("ONLINESCORE",    CCSQLType.TINYINT);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_FSK              = new CCSQLColDef("FSK",            CCSQLType.TINYINT);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_FORMAT           = new CCSQLColDef("FORMAT",         CCSQLType.TINYINT);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_MOVIEYEAR        = new CCSQLColDef("MOVIEYEAR",      CCSQLType.SMALLINT);   //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_ONLINEREF        = new CCSQLColDef("ONLINEREF",      CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_GROUPS           = new CCSQLColDef("GROUPS",         CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_FILESIZE         = new CCSQLColDef("FILESIZE",       CCSQLType.BIGINT);     //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_TAGS             = new CCSQLColDef("TAGS",           CCSQLType.SMALLINT);   //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_PART_1           = new CCSQLColDef("PART1",          CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_PART_2           = new CCSQLColDef("PART2",          CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_PART_3           = new CCSQLColDef("PART3",          CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_PART_4           = new CCSQLColDef("PART4",          CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_PART_5           = new CCSQLColDef("PART5",          CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_PART_6           = new CCSQLColDef("PART6",          CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_SCORE            = new CCSQLColDef("SCORE",          CCSQLType.TINYINT);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_COVER            = new CCSQLColDef("COVERNAME",      CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_TYPE             = new CCSQLColDef("TYPE",           CCSQLType.TINYINT);    //$NON-NLS-1$
	public final static CCSQLColDef COL_MAIN_SERIES_ID        = new CCSQLColDef("SERIESID",       CCSQLType.INTEGER);    //$NON-NLS-1$

	public final static CCSQLColDef COL_SEAS_SEASONID         = new CCSQLColDef("SEASONID",       CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_SEAS_SERIESID         = new CCSQLColDef("SERIESID",       CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_SEAS_NAME             = new CCSQLColDef("NAME",           CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_SEAS_YEAR             = new CCSQLColDef("SEASONYEAR",     CCSQLType.SMALLINT);   //$NON-NLS-1$
	public final static CCSQLColDef COL_SEAS_COVERNAME        = new CCSQLColDef("COVERNAME",      CCSQLType.VARCHAR);    //$NON-NLS-1$

	public final static CCSQLColDef COL_EPIS_LOCALID          = new CCSQLColDef("LOCALID",        CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_SEASONID         = new CCSQLColDef("SEASONID",       CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_EPISODE          = new CCSQLColDef("EPISODE",        CCSQLType.SMALLINT);   //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_NAME             = new CCSQLColDef("NAME",           CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_VIEWED           = new CCSQLColDef("VIEWED",         CCSQLType.BIT);        //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_VIEWEDHISTORY    = new CCSQLColDef("VIEWED_HISTORY", CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_QUALITY          = new CCSQLColDef("QUALITY",        CCSQLType.TINYINT);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_LENGTH           = new CCSQLColDef("LENGTH",         CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_FORMAT           = new CCSQLColDef("FORMAT",         CCSQLType.TINYINT);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_FILESIZE         = new CCSQLColDef("FILESIZE",       CCSQLType.BIGINT);     //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_PART_1           = new CCSQLColDef("PART1",          CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_TAGS             = new CCSQLColDef("TAGS",           CCSQLType.SMALLINT);   //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_ADDDATE          = new CCSQLColDef("ADDDATE",        CCSQLType.DATE);       //$NON-NLS-1$
	public final static CCSQLColDef COL_EPIS_LANGUAGE         = new CCSQLColDef("LANGUAGE",       CCSQLType.BIGINT);     //$NON-NLS-1$

	public final static CCSQLColDef COL_GRPS_NAME             = new CCSQLColDef("NAME",           CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_GRPS_ORDER            = new CCSQLColDef("ORDERING",       CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_GRPS_COLOR            = new CCSQLColDef("COLOR",          CCSQLType.INTEGER);    //$NON-NLS-1$
	public final static CCSQLColDef COL_GRPS_SERIALIZE        = new CCSQLColDef("SERIALIZE",      CCSQLType.BIT);        //$NON-NLS-1$
	public final static CCSQLColDef COL_GRPS_PARENT           = new CCSQLColDef("PARENTGROUP",    CCSQLType.VARCHAR);    //$NON-NLS-1$
	public final static CCSQLColDef COL_GRPS_VISIBLE          = new CCSQLColDef("VISIBLE",        CCSQLType.BIT);        //$NON-NLS-1$

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

	private static ArrayList<CCSQLStatement> statements = new ArrayList<>();

	public static void intialize(CCDatabase d) {
		try {
			addEmptyMainTabStatement = SQLBuilder.createInsert(TAB_MAIN)
					.addPreparedFields(COL_MAIN_LOCALID, COL_MAIN_NAME, COL_MAIN_VIEWED, COL_MAIN_VIEWEDHISTORY, COL_MAIN_ZYKLUS, COL_MAIN_ZYKLUSNUMBER)
					.addPreparedFields(COL_MAIN_QUALITY, COL_MAIN_LANGUAGE, COL_MAIN_GENRE, COL_MAIN_LENGTH, COL_MAIN_ADDDATE, COL_MAIN_ONLINESCORE)
					.addPreparedFields(COL_MAIN_FSK, COL_MAIN_FORMAT, COL_MAIN_MOVIEYEAR, COL_MAIN_ONLINEREF, COL_MAIN_GROUPS, COL_MAIN_FILESIZE, COL_MAIN_TAGS)
					.addPreparedFields(COL_MAIN_PART_1, COL_MAIN_PART_2, COL_MAIN_PART_3, COL_MAIN_PART_4, COL_MAIN_PART_5, COL_MAIN_PART_6)
					.addPreparedFields(COL_MAIN_SCORE, COL_MAIN_COVER, COL_MAIN_TYPE, COL_MAIN_SERIES_ID)
				.build(d, statements);

			addEmptySeasonTabStatement = SQLBuilder.createInsert(TAB_SEASONS)
					.addPreparedFields(COL_SEAS_SEASONID, COL_SEAS_SERIESID, COL_SEAS_NAME, COL_SEAS_YEAR, COL_SEAS_COVERNAME)
					.build(d, statements);

			addEmptyEpisodeTabStatement = SQLBuilder.createInsert(TAB_EPISODES)
					.addPreparedFields(COL_EPIS_LOCALID, COL_EPIS_SEASONID, COL_EPIS_EPISODE, COL_EPIS_NAME, COL_EPIS_VIEWED, COL_EPIS_VIEWEDHISTORY)
					.addPreparedFields(COL_EPIS_QUALITY, COL_EPIS_LENGTH, COL_EPIS_FORMAT, COL_EPIS_FILESIZE, COL_EPIS_PART_1, COL_EPIS_TAGS)
					.addPreparedFields(COL_EPIS_ADDDATE, COL_EPIS_LANGUAGE)
					.build(d, statements);

			newLocalIDStatement  = SQLBuilder.createCustom(TAB_MAIN).setSQL("SELECT COALESCE(MAX(%s), -1) FROM {TAB}", COL_MAIN_LOCALID.Name).build(d, statements);

			newSeriesIDStatement = SQLBuilder.createCustom(TAB_MAIN).setSQL("SELECT COALESCE(MAX(%s), -1) FROM {TAB}", COL_MAIN_SERIES_ID.Name).build(d, statements);

			newSeasonIDStatement = SQLBuilder.createCustom(TAB_SEASONS) .setSQL("SELECT COALESCE(MAX(%s), -1) FROM {TAB}", COL_SEAS_SEASONID.Name).build(d, statements);

			newEpisodeIDStatement = SQLBuilder.createCustom(TAB_EPISODES).setSQL("SELECT COALESCE(MAX(%s), -1) FROM {TAB}", COL_EPIS_LOCALID.Name).build(d, statements);

			updateMainTabStatement = SQLBuilder.createUpdate(TAB_MAIN)
					.addPreparedWhereCondition(COL_MAIN_LOCALID)
					.addPreparedFields(COL_MAIN_NAME, COL_MAIN_VIEWED, COL_MAIN_VIEWEDHISTORY, COL_MAIN_ZYKLUS, COL_MAIN_ZYKLUSNUMBER, COL_MAIN_QUALITY)
					.addPreparedFields(COL_MAIN_LANGUAGE, COL_MAIN_GENRE, COL_MAIN_LENGTH, COL_MAIN_ADDDATE, COL_MAIN_ONLINESCORE, COL_MAIN_FSK, COL_MAIN_FORMAT)
					.addPreparedFields(COL_MAIN_MOVIEYEAR, COL_MAIN_ONLINEREF, COL_MAIN_GROUPS, COL_MAIN_FILESIZE, COL_MAIN_TAGS)
					.addPreparedFields(COL_MAIN_PART_1, COL_MAIN_PART_2, COL_MAIN_PART_3, COL_MAIN_PART_4, COL_MAIN_PART_5, COL_MAIN_PART_6)
					.addPreparedFields(COL_MAIN_SCORE, COL_MAIN_COVER, COL_MAIN_TYPE, COL_MAIN_SERIES_ID)
					.build(d, statements);

			updateSeriesTabStatement = SQLBuilder.createUpdate(TAB_MAIN)
					.addPreparedWhereCondition(COL_MAIN_LOCALID)
					.addPreparedFields(COL_MAIN_NAME, COL_MAIN_GENRE, COL_MAIN_ONLINESCORE, COL_MAIN_FSK, COL_MAIN_ONLINEREF, COL_MAIN_GROUPS)
					.addPreparedFields(COL_MAIN_SCORE, COL_MAIN_COVER, COL_MAIN_TYPE, COL_MAIN_SERIES_ID, COL_MAIN_TAGS)
					.build(d, statements);

			updateSeasonTabStatement = SQLBuilder.createUpdate(TAB_SEASONS)
					.addPreparedWhereCondition(COL_SEAS_SEASONID)
					.addPreparedFields(COL_SEAS_SERIESID, COL_SEAS_NAME, COL_SEAS_YEAR, COL_SEAS_COVERNAME)
					.build(d, statements);

			updateEpisodeTabStatement = SQLBuilder.createUpdate(TAB_EPISODES)
					.addPreparedWhereCondition(COL_EPIS_LOCALID)
					.addPreparedFields(COL_EPIS_SEASONID, COL_EPIS_EPISODE, COL_EPIS_NAME, COL_EPIS_VIEWED, COL_EPIS_VIEWEDHISTORY, COL_EPIS_QUALITY)
					.addPreparedFields(COL_EPIS_LENGTH, COL_EPIS_FORMAT, COL_EPIS_FILESIZE, COL_EPIS_PART_1, COL_EPIS_TAGS, COL_EPIS_ADDDATE, COL_EPIS_LANGUAGE)
					.build(d, statements);

			selectAllMainTabStatement = SQLBuilder.createSelect(TAB_MAIN)
					.addSelectFields(COL_MAIN_LOCALID, COL_MAIN_NAME, COL_MAIN_VIEWED, COL_MAIN_VIEWEDHISTORY, COL_MAIN_ZYKLUS, COL_MAIN_ZYKLUSNUMBER)
					.addSelectFields(COL_MAIN_QUALITY, COL_MAIN_LANGUAGE, COL_MAIN_GENRE, COL_MAIN_LENGTH, COL_MAIN_ADDDATE, COL_MAIN_ONLINESCORE)
					.addSelectFields(COL_MAIN_FSK, COL_MAIN_FORMAT, COL_MAIN_MOVIEYEAR, COL_MAIN_ONLINEREF, COL_MAIN_GROUPS, COL_MAIN_FILESIZE, COL_MAIN_TAGS)
					.addSelectFields(COL_MAIN_PART_1, COL_MAIN_PART_2, COL_MAIN_PART_3, COL_MAIN_PART_4, COL_MAIN_PART_5, COL_MAIN_PART_6)
					.addSelectFields(COL_MAIN_SCORE, COL_MAIN_COVER, COL_MAIN_TYPE, COL_MAIN_SERIES_ID)
					.setOrder(COL_MAIN_LOCALID, SQLOrder.ASC)
					.build(d, statements);

			selectAllSeasonTabStatement = SQLBuilder.createSelect(TAB_SEASONS)
					.addSelectFields(COL_SEAS_SERIESID, COL_SEAS_SEASONID, COL_SEAS_NAME, COL_SEAS_YEAR, COL_SEAS_COVERNAME)
					.setOrder(COL_SEAS_SERIESID, SQLOrder.ASC)
					.build(d, statements);

			selectAllEpisodeTabStatement = SQLBuilder.createSelect(TAB_EPISODES)
					.addSelectFields(COL_EPIS_LOCALID, COL_EPIS_SEASONID, COL_EPIS_EPISODE, COL_EPIS_NAME, COL_EPIS_VIEWED, COL_EPIS_VIEWEDHISTORY)
					.addSelectFields(COL_EPIS_QUALITY, COL_EPIS_LENGTH, COL_EPIS_FORMAT, COL_EPIS_FILESIZE, COL_EPIS_PART_1, COL_EPIS_TAGS)
					.addSelectFields(COL_EPIS_ADDDATE, COL_EPIS_LANGUAGE)
					.setOrder(COL_EPIS_SEASONID, SQLOrder.ASC)
					.build(d, statements);

			selectSeasonTabStatement = SQLBuilder.createSelect(TAB_SEASONS)
					.addSelectFields(COL_SEAS_SERIESID, COL_SEAS_SEASONID, COL_SEAS_NAME, COL_SEAS_YEAR, COL_SEAS_COVERNAME)
					.addPreparedWhereCondition(COL_SEAS_SERIESID)
					.setOrder(COL_SEAS_SERIESID, SQLOrder.ASC)
					.build(d, statements);

			selectEpisodeTabStatement = SQLBuilder.createSelect(TAB_EPISODES)
					.addSelectFields(COL_EPIS_LOCALID, COL_EPIS_SEASONID, COL_EPIS_EPISODE, COL_EPIS_NAME, COL_EPIS_VIEWED, COL_EPIS_VIEWEDHISTORY)
					.addSelectFields(COL_EPIS_QUALITY, COL_EPIS_LENGTH, COL_EPIS_FORMAT, COL_EPIS_FILESIZE, COL_EPIS_PART_1, COL_EPIS_TAGS)
					.addSelectFields(COL_EPIS_ADDDATE, COL_EPIS_LANGUAGE)
					.addPreparedWhereCondition(COL_EPIS_SEASONID)
					.setOrder(COL_EPIS_EPISODE, SQLOrder.ASC)
					.build(d, statements);

			deleteMainTabStatement = SQLBuilder.createDelete(TAB_MAIN).addPreparedWhereCondition(COL_MAIN_LOCALID).build(d, statements);

			deleteSeasonTabStatement = SQLBuilder.createDelete(TAB_SEASONS).addPreparedWhereCondition(COL_SEAS_SEASONID).build(d, statements);

			deleteEpisodeTabStatement = SQLBuilder.createDelete(TAB_EPISODES).addPreparedWhereCondition(COL_EPIS_LOCALID).build(d, statements);

			selectSingleMainTabStatement = SQLBuilder.createSelect(TAB_MAIN)
					.addSelectFields(COL_MAIN_LOCALID, COL_MAIN_NAME, COL_MAIN_VIEWED, COL_MAIN_VIEWEDHISTORY, COL_MAIN_ZYKLUS, COL_MAIN_ZYKLUSNUMBER)
					.addSelectFields(COL_MAIN_QUALITY, COL_MAIN_LANGUAGE, COL_MAIN_GENRE, COL_MAIN_LENGTH, COL_MAIN_ADDDATE, COL_MAIN_ONLINESCORE)
					.addSelectFields(COL_MAIN_FSK, COL_MAIN_FORMAT, COL_MAIN_MOVIEYEAR, COL_MAIN_ONLINEREF, COL_MAIN_GROUPS, COL_MAIN_FILESIZE, COL_MAIN_TAGS)
					.addSelectFields(COL_MAIN_PART_1, COL_MAIN_PART_2, COL_MAIN_PART_3, COL_MAIN_PART_4, COL_MAIN_PART_5, COL_MAIN_PART_6)
					.addSelectFields(COL_MAIN_SCORE, COL_MAIN_COVER, COL_MAIN_TYPE, COL_MAIN_SERIES_ID)
					.addPreparedWhereCondition(COL_MAIN_LOCALID)
					.build(d, statements);

			selectSingleSeasonTabStatement = SQLBuilder.createSelect(TAB_SEASONS)
					.addSelectFields(COL_SEAS_SERIESID, COL_SEAS_SEASONID, COL_SEAS_NAME, COL_SEAS_YEAR, COL_SEAS_COVERNAME)
					.addPreparedWhereCondition(COL_SEAS_SEASONID)
					.build(d, statements);

			selectSingleEpisodeTabStatement = SQLBuilder.createSelect(TAB_EPISODES)
					.addSelectFields(COL_EPIS_LOCALID, COL_EPIS_SEASONID, COL_EPIS_EPISODE, COL_EPIS_NAME, COL_EPIS_VIEWED, COL_EPIS_VIEWEDHISTORY)
					.addSelectFields(COL_EPIS_QUALITY, COL_EPIS_LENGTH, COL_EPIS_FORMAT, COL_EPIS_FILESIZE, COL_EPIS_PART_1, COL_EPIS_TAGS)
					.addSelectFields(COL_EPIS_ADDDATE, COL_EPIS_LANGUAGE)
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

			CCLog.addDebug(String.format("%d SQL Statements prepared", statements.size())); //$NON-NLS-1$

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
