package de.jClipCorn.database.driver;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.sqlwrapper.*;

import java.sql.SQLException;
import java.util.ArrayList;

import static de.jClipCorn.database.driver.DatabaseStructure.*;

@SuppressWarnings("nls")
public class Statements {

	//--------------------------------------------------------------------------------------------------

	public static CCSQLStatement addEmptyMainTabStatement;
	public static CCSQLStatement addEmptySeasonTabStatement;
	public static CCSQLStatement addEmptyEpisodeTabStatement;

	public static CCSQLStatement newDatabaseIDStatement1;
	public static CCSQLStatement newDatabaseIDStatement2;

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

	public static CCSQLStatement writeInfoKeyStatement;
	public static CCSQLStatement readInfoKeyStatement;

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

	public static CCSQLStatement countHistory;
	public static CCSQLStatement queryHistoryStatement;
	public static CCSQLStatement queryHistoryStatementFiltered;
	public static CCSQLStatement queryHistoryStatementLimited;
	public static CCSQLStatement queryHistoryStatementFilteredLimited;

	private static ArrayList<CCSQLStatement> statements = new ArrayList<>();

	public static void intialize(CCDatabase d) {
		try {
			statements = new ArrayList<>();

			addEmptyMainTabStatement = SQLBuilder.createInsert(TAB_MAIN)
					.addPreparedFields(COL_MAIN_LOCALID, COL_MAIN_NAME, COL_MAIN_VIEWEDHISTORY, COL_MAIN_ZYKLUS, COL_MAIN_ZYKLUSNUMBER)
					.addPreparedFields(COL_MAIN_LANGUAGE, COL_MAIN_GENRE, COL_MAIN_LENGTH, COL_MAIN_ADDDATE, COL_MAIN_ONLINESCORE)
					.addPreparedFields(COL_MAIN_FSK, COL_MAIN_FORMAT, COL_MAIN_MOVIEYEAR, COL_MAIN_ONLINEREF, COL_MAIN_GROUPS, COL_MAIN_FILESIZE, COL_MAIN_TAGS)
					.addPreparedFields(COL_MAIN_PART_1, COL_MAIN_PART_2, COL_MAIN_PART_3, COL_MAIN_PART_4, COL_MAIN_PART_5, COL_MAIN_PART_6)
					.addPreparedFields(COL_MAIN_SCORE, COL_MAIN_COVERID, COL_MAIN_TYPE)
					.build(d, statements);

			addEmptySeasonTabStatement = SQLBuilder.createInsert(TAB_SEASONS)
					.addPreparedFields(COL_SEAS_SEASONID, COL_SEAS_SERIESID, COL_SEAS_NAME, COL_SEAS_YEAR, COL_SEAS_COVERID)
					.build(d, statements);

			addEmptyEpisodeTabStatement = SQLBuilder.createInsert(TAB_EPISODES)
					.addPreparedFields(COL_EPIS_LOCALID, COL_EPIS_SEASONID, COL_EPIS_EPISODE, COL_EPIS_NAME, COL_EPIS_VIEWEDHISTORY)
					.addPreparedFields(COL_EPIS_LENGTH, COL_EPIS_FORMAT, COL_EPIS_FILESIZE, COL_EPIS_PART_1, COL_EPIS_TAGS)
					.addPreparedFields(COL_EPIS_ADDDATE, COL_EPIS_LANGUAGE)
					.build(d, statements);

			newDatabaseIDStatement1 = SQLBuilder
					.createCustom(TAB_INFO)
					.setSQL("REPLACE INTO [{TAB}] SELECT '{2}', (CAST([{1}] AS INTEGER)+1) FROM [{TAB}] WHERE [{0}]='{2}'", COL_INFO_KEY.Name, COL_INFO_VALUE.Name, INFOKEY_LASTID.Key)
					.build(d, statements);

			newDatabaseIDStatement2 = SQLBuilder
					.createCustom(TAB_INFO)
					.setSQL("SELECT CAST([{1}] AS INTEGER) FROM [{TAB}] WHERE [{0}]='{2}'", COL_INFO_KEY.Name, COL_INFO_VALUE.Name, INFOKEY_LASTID.Key)
					.build(d, statements);

			updateMainTabStatement = SQLBuilder.createUpdate(TAB_MAIN)
					.addPreparedWhereCondition(COL_MAIN_LOCALID)
					.addPreparedFields(COL_MAIN_NAME, COL_MAIN_VIEWEDHISTORY, COL_MAIN_ZYKLUS, COL_MAIN_ZYKLUSNUMBER)
					.addPreparedFields(COL_MAIN_LANGUAGE, COL_MAIN_GENRE, COL_MAIN_LENGTH, COL_MAIN_ADDDATE, COL_MAIN_ONLINESCORE, COL_MAIN_FSK, COL_MAIN_FORMAT)
					.addPreparedFields(COL_MAIN_MOVIEYEAR, COL_MAIN_ONLINEREF, COL_MAIN_GROUPS, COL_MAIN_FILESIZE, COL_MAIN_TAGS)
					.addPreparedFields(COL_MAIN_PART_1, COL_MAIN_PART_2, COL_MAIN_PART_3, COL_MAIN_PART_4, COL_MAIN_PART_5, COL_MAIN_PART_6)
					.addPreparedFields(COL_MAIN_SCORE, COL_MAIN_COVERID, COL_MAIN_TYPE)
					.addPreparedFields(COL_MAIN_MI_FILESIZE, COL_MAIN_MI_CDATE, COL_MAIN_MI_MDATE, COL_MAIN_MI_AFORMAT, COL_MAIN_MI_VFORMAT, COL_MAIN_MI_WIDTH)
					.addPreparedFields(COL_MAIN_MI_HEIGHT, COL_MAIN_MI_FRAMERATE, COL_MAIN_MI_DURATION, COL_MAIN_MI_BITDEPTH, COL_MAIN_MI_BITRATE, COL_MAIN_MI_FRAMECOUNT)
					.addPreparedFields(COL_MAIN_MI_ACHANNELS, COL_MAIN_MI_VCODEC, COL_MAIN_MI_ACODEC, COL_MAIN_MI_SAMPLERATE)
					.build(d, statements);

			updateSeriesTabStatement = SQLBuilder.createUpdate(TAB_MAIN)
					.addPreparedWhereCondition(COL_MAIN_LOCALID)
					.addPreparedFields(COL_MAIN_NAME, COL_MAIN_GENRE, COL_MAIN_ONLINESCORE, COL_MAIN_FSK, COL_MAIN_ONLINEREF, COL_MAIN_GROUPS)
					.addPreparedFields(COL_MAIN_SCORE, COL_MAIN_COVERID, COL_MAIN_TYPE, COL_MAIN_TAGS)
					.build(d, statements);

			updateSeasonTabStatement = SQLBuilder.createUpdate(TAB_SEASONS)
					.addPreparedWhereCondition(COL_SEAS_SEASONID)
					.addPreparedFields(COL_SEAS_SERIESID, COL_SEAS_NAME, COL_SEAS_YEAR, COL_SEAS_COVERID)
					.build(d, statements);

			updateEpisodeTabStatement = SQLBuilder.createUpdate(TAB_EPISODES)
					.addPreparedWhereCondition(COL_EPIS_LOCALID)
					.addPreparedFields(COL_EPIS_SEASONID, COL_EPIS_EPISODE, COL_EPIS_NAME, COL_EPIS_VIEWEDHISTORY)
					.addPreparedFields(COL_EPIS_LENGTH, COL_EPIS_FORMAT, COL_EPIS_FILESIZE, COL_EPIS_PART_1, COL_EPIS_TAGS, COL_EPIS_ADDDATE, COL_EPIS_LANGUAGE)
					.addPreparedFields(COL_EPIS_MI_FILESIZE, COL_EPIS_MI_CDATE, COL_EPIS_MI_MDATE, COL_EPIS_MI_AFORMAT, COL_EPIS_MI_VFORMAT, COL_EPIS_MI_WIDTH)
					.addPreparedFields(COL_EPIS_MI_HEIGHT, COL_EPIS_MI_FRAMERATE, COL_EPIS_MI_DURATION, COL_EPIS_MI_BITDEPTH, COL_EPIS_MI_BITRATE, COL_EPIS_MI_FRAMECOUNT)
					.addPreparedFields(COL_EPIS_MI_ACHANNELS, COL_EPIS_MI_VCODEC, COL_EPIS_MI_ACODEC, COL_EPIS_MI_SAMPLERATE)
					.build(d, statements);

			selectAllMainTabStatement = SQLBuilder.createSelect(TAB_MAIN)
					.addSelectFields(COL_MAIN_LOCALID, COL_MAIN_NAME, COL_MAIN_VIEWEDHISTORY, COL_MAIN_ZYKLUS, COL_MAIN_ZYKLUSNUMBER)
					.addSelectFields(COL_MAIN_LANGUAGE, COL_MAIN_GENRE, COL_MAIN_LENGTH, COL_MAIN_ADDDATE, COL_MAIN_ONLINESCORE)
					.addSelectFields(COL_MAIN_FSK, COL_MAIN_FORMAT, COL_MAIN_MOVIEYEAR, COL_MAIN_ONLINEREF, COL_MAIN_GROUPS, COL_MAIN_FILESIZE, COL_MAIN_TAGS)
					.addSelectFields(COL_MAIN_PART_1, COL_MAIN_PART_2, COL_MAIN_PART_3, COL_MAIN_PART_4, COL_MAIN_PART_5, COL_MAIN_PART_6)
					.addSelectFields(COL_MAIN_SCORE, COL_MAIN_COVERID, COL_MAIN_TYPE)
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
					.addSelectFields(COL_EPIS_LOCALID, COL_EPIS_SEASONID, COL_EPIS_EPISODE, COL_EPIS_NAME, COL_EPIS_VIEWEDHISTORY)
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
					.addSelectFields(COL_EPIS_LOCALID, COL_EPIS_SEASONID, COL_EPIS_EPISODE, COL_EPIS_NAME, COL_EPIS_VIEWEDHISTORY)
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
					.addSelectFields(COL_MAIN_LOCALID, COL_MAIN_NAME, COL_MAIN_VIEWEDHISTORY, COL_MAIN_ZYKLUS, COL_MAIN_ZYKLUSNUMBER)
					.addSelectFields(COL_MAIN_LANGUAGE, COL_MAIN_GENRE, COL_MAIN_LENGTH, COL_MAIN_ADDDATE, COL_MAIN_ONLINESCORE)
					.addSelectFields(COL_MAIN_FSK, COL_MAIN_FORMAT, COL_MAIN_MOVIEYEAR, COL_MAIN_ONLINEREF, COL_MAIN_GROUPS, COL_MAIN_FILESIZE, COL_MAIN_TAGS)
					.addSelectFields(COL_MAIN_PART_1, COL_MAIN_PART_2, COL_MAIN_PART_3, COL_MAIN_PART_4, COL_MAIN_PART_5, COL_MAIN_PART_6)
					.addSelectFields(COL_MAIN_SCORE, COL_MAIN_COVERID, COL_MAIN_TYPE)
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
					.addSelectFields(COL_EPIS_LOCALID, COL_EPIS_SEASONID, COL_EPIS_EPISODE, COL_EPIS_NAME, COL_EPIS_VIEWEDHISTORY)
					.addSelectFields(COL_EPIS_LENGTH, COL_EPIS_FORMAT, COL_EPIS_FILESIZE, COL_EPIS_PART_1, COL_EPIS_TAGS)
					.addSelectFields(COL_EPIS_ADDDATE, COL_EPIS_LANGUAGE)
					.addSelectFields(COL_EPIS_MI_FILESIZE, COL_EPIS_MI_CDATE, COL_EPIS_MI_MDATE, COL_EPIS_MI_AFORMAT, COL_EPIS_MI_VFORMAT, COL_EPIS_MI_WIDTH)
					.addSelectFields(COL_EPIS_MI_HEIGHT, COL_EPIS_MI_FRAMERATE, COL_EPIS_MI_DURATION, COL_EPIS_MI_BITDEPTH, COL_EPIS_MI_BITRATE, COL_EPIS_MI_FRAMECOUNT)
					.addSelectFields(COL_EPIS_MI_ACHANNELS, COL_EPIS_MI_VCODEC, COL_EPIS_MI_ACODEC, COL_EPIS_MI_SAMPLERATE)
					.addPreparedWhereCondition(COL_EPIS_LOCALID)
					.build(d, statements);

			readInfoKeyStatement = SQLBuilder.createSelect(TAB_INFO)
					.addSelectField(COL_INFO_VALUE)
					.addPreparedWhereCondition(COL_INFO_KEY)
					.build(d, statements);

			writeInfoKeyStatement = SQLBuilder.createInsertOrReplace(TAB_INFO)
					.addPreparedField(COL_INFO_KEY)
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

			countHistory = SQLBuilder
					.createCustom(TAB_HISTORY)
					.setSQL("SELECT COUNT(*) FROM HISTORY")
					.build(d, statements);

			queryHistoryStatement = SQLBuilder.createSelect(TAB_HISTORY)
					.addSelectFields(COL_HISTORY_TABLE, COL_HISTORY_ID, COL_HISTORY_DATE, COL_HISTORY_ACTION, COL_HISTORY_FIELD, COL_HISTORY_OLD, COL_HISTORY_NEW)
					.setOrder(COL_HISTORY_DATE, SQLOrder.ASC)
					.build(d, statements);

			queryHistoryStatementFiltered = SQLBuilder.createSelect(TAB_HISTORY)
					.addSelectFields(COL_HISTORY_TABLE, COL_HISTORY_ID, COL_HISTORY_DATE, COL_HISTORY_ACTION, COL_HISTORY_FIELD, COL_HISTORY_OLD, COL_HISTORY_NEW)
					.addPreparedWhereCondition(COL_HISTORY_ID)
					.setOrder(COL_HISTORY_DATE, SQLOrder.ASC)
					.build(d, statements);

			queryHistoryStatementLimited = SQLBuilder.createCustom(TAB_HISTORY)
					.setSQL("SELECT [TABLE], [ID], [DATE], [ACTION], [FIELD], [OLD], [NEW] FROM [HISTORY] WHERE [DATE] > ? ORDER BY [DATE] ASC")
					.setCustomSelectField(1, COL_HISTORY_TABLE)
					.setCustomSelectField(2, COL_HISTORY_ID)
					.setCustomSelectField(3, COL_HISTORY_DATE)
					.setCustomSelectField(4, COL_HISTORY_ACTION)
					.setCustomSelectField(5, COL_HISTORY_FIELD)
					.setCustomSelectField(6, COL_HISTORY_OLD)
					.setCustomSelectField(7, COL_HISTORY_NEW)
					.setCustomPrepared(1, COL_HISTORY_DATE)
					.build(d, statements);

			queryHistoryStatementFilteredLimited = SQLBuilder.createCustom(TAB_HISTORY)
					.setSQL("SELECT [TABLE], [ID], [DATE], [ACTION], [FIELD], [OLD], [NEW] FROM [HISTORY] WHERE ([ID] = ?) AND ([DATE] > ?) ORDER BY [DATE] ASC")
					.setCustomSelectField(1, COL_HISTORY_TABLE)
					.setCustomSelectField(2, COL_HISTORY_ID)
					.setCustomSelectField(3, COL_HISTORY_DATE)
					.setCustomSelectField(4, COL_HISTORY_ACTION)
					.setCustomSelectField(5, COL_HISTORY_FIELD)
					.setCustomSelectField(6, COL_HISTORY_OLD)
					.setCustomSelectField(7, COL_HISTORY_NEW)
					.setCustomPrepared(1, COL_HISTORY_ID)
					.setCustomPrepared(2, COL_HISTORY_DATE)
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
