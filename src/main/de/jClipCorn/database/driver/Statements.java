package de.jClipCorn.database.driver;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.sqlwrapper.CCSQLStatement;
import de.jClipCorn.util.sqlwrapper.SQLBuilder;
import de.jClipCorn.util.sqlwrapper.SQLOrder;
import de.jClipCorn.util.sqlwrapper.SQLWrapperException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static de.jClipCorn.database.driver.DatabaseStructure.*;

@SuppressWarnings("nls")
public class Statements {

	//--------------------------------------------------------------------------------------------------

	public static CCSQLStatement addEmptyMovieTabStatement;
	public static CCSQLStatement addEmptySeriesTabStatement;
	public static CCSQLStatement addEmptySeasonTabStatement;
	public static CCSQLStatement addEmptyEpisodeTabStatement;

	public static CCSQLStatement newDatabaseIDStatement1;
	public static CCSQLStatement newDatabaseIDStatement2;

	public static CCSQLStatement updateMovieTabStatement;
	public static CCSQLStatement updateSeriesTabStatement;
	public static CCSQLStatement updateSeasonTabStatement;
	public static CCSQLStatement updateEpisodeTabStatement;

	public static CCSQLStatement selectAllMoviesTabStatement;
	public static CCSQLStatement selectAllSeriesTabStatement;
	public static CCSQLStatement selectAllSeasonTabStatement;
	public static CCSQLStatement selectAllEpisodeTabStatement;

	public static CCSQLStatement selectSeasonTabStatement;
	public static CCSQLStatement selectEpisodeTabStatement;

	public static CCSQLStatement deleteMovieTabStatement;
	public static CCSQLStatement deleteSeriesTabStatement;
	public static CCSQLStatement deleteSeasonTabStatement;
	public static CCSQLStatement deleteEpisodeTabStatement;

	public static CCSQLStatement selectSingleMovieTabStatement;
	public static CCSQLStatement selectSingleSeriesTabStatement;
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

			addEmptyMovieTabStatement   = SQLBuilder.createInsertSingle(TAB_MOVIES).build(d, statements);
			addEmptySeriesTabStatement  = SQLBuilder.createInsertSingle(TAB_SERIES).build(d, statements);
			addEmptySeasonTabStatement  = SQLBuilder.createInsertSingle(TAB_SEASONS).build(d, statements);
			addEmptyEpisodeTabStatement = SQLBuilder.createInsertSingle(TAB_EPISODES).build(d, statements);

			updateMovieTabStatement   = SQLBuilder.createUpdateSingle(TAB_MOVIES,   COL_MOV_LOCALID).build(d, statements);
			updateSeriesTabStatement  = SQLBuilder.createUpdateSingle(TAB_SERIES,   COL_SER_LOCALID).build(d, statements);
			updateSeasonTabStatement  = SQLBuilder.createUpdateSingle(TAB_SEASONS,  COL_SEAS_LOCALID).build(d, statements);
			updateEpisodeTabStatement = SQLBuilder.createUpdateSingle(TAB_EPISODES, COL_EPIS_LOCALID).build(d, statements);

			selectAllMoviesTabStatement  = SQLBuilder.createSelectAll(TAB_MOVIES).setOrder(COL_MOV_LOCALID,     SQLOrder.ASC).build(d, statements);
			selectAllSeriesTabStatement  = SQLBuilder.createSelectAll(TAB_SERIES).setOrder(COL_SER_LOCALID,     SQLOrder.ASC).build(d, statements);
			selectAllSeasonTabStatement  = SQLBuilder.createSelectAll(TAB_SEASONS).setOrder(COL_SEAS_SERIESID,  SQLOrder.ASC).build(d, statements);
			selectAllEpisodeTabStatement = SQLBuilder.createSelectAll(TAB_EPISODES).setOrder(COL_EPIS_SEASONID, SQLOrder.ASC).build(d, statements);

			deleteMovieTabStatement   = SQLBuilder.createDelete(TAB_MOVIES).addPreparedWhereCondition(COL_MOV_LOCALID).build(d, statements);
			deleteSeriesTabStatement  = SQLBuilder.createDelete(TAB_SERIES).addPreparedWhereCondition(COL_SER_LOCALID).build(d, statements);
			deleteSeasonTabStatement  = SQLBuilder.createDelete(TAB_SEASONS).addPreparedWhereCondition(COL_SEAS_LOCALID).build(d, statements);
			deleteEpisodeTabStatement = SQLBuilder.createDelete(TAB_EPISODES).addPreparedWhereCondition(COL_EPIS_LOCALID).build(d, statements);

			selectSeasonTabStatement = SQLBuilder.createSelectAll(TAB_SEASONS)
					.addPreparedWhereCondition(COL_SEAS_SERIESID)
					.setOrder(COL_SEAS_SERIESID, SQLOrder.ASC)
					.build(d, statements);

			selectEpisodeTabStatement = SQLBuilder.createSelectAll(TAB_EPISODES)
					.addPreparedWhereCondition(COL_EPIS_SEASONID)
					.setOrder(COL_EPIS_EPISODE, SQLOrder.ASC)
					.build(d, statements);

			newDatabaseIDStatement1 = SQLBuilder
					.createCustom(TAB_INFO)
					.setSQL("REPLACE INTO [{TAB}] SELECT '{2}', (CAST([{1}] AS INTEGER)+1) FROM [{TAB}] WHERE [{0}]='{2}'", COL_INFO_KEY.Name, COL_INFO_VALUE.Name, INFOKEY_LASTID.Key)
					.build(d, statements);

			newDatabaseIDStatement2 = SQLBuilder
					.createCustom(TAB_INFO)
					.setSQL("SELECT CAST([{1}] AS INTEGER) FROM [{TAB}] WHERE [{0}]='{2}'", COL_INFO_KEY.Name, COL_INFO_VALUE.Name, INFOKEY_LASTID.Key)
					.build(d, statements);

			selectSingleMovieTabStatement   = SQLBuilder.createSelectSingle(TAB_MOVIES,   COL_MOV_LOCALID).build(d, statements);
			selectSingleSeriesTabStatement  = SQLBuilder.createSelectSingle(TAB_SERIES,   COL_SER_LOCALID).build(d, statements);
			selectSingleSeasonTabStatement  = SQLBuilder.createSelectSingle(TAB_SEASONS,  COL_SEAS_LOCALID).build(d, statements);
			selectSingleEpisodeTabStatement = SQLBuilder.createSelectSingle(TAB_EPISODES, COL_EPIS_LOCALID).build(d, statements);

			readInfoKeyStatement  = SQLBuilder.createSelect(TAB_INFO).addSelectField(COL_INFO_VALUE).addPreparedWhereCondition(COL_INFO_KEY).build(d, statements);
			writeInfoKeyStatement = SQLBuilder.createInsertOrReplace(TAB_INFO).addPreparedField(COL_INFO_KEY).addPreparedField(COL_INFO_VALUE).build(d, statements);

			selectGroupsStatement    = SQLBuilder.createSelectAll(TAB_GROUPS).build(d, statements);
			insertGroupStatement     = SQLBuilder.createInsertSingle(TAB_GROUPS).build(d, statements);
			updateGroupStatement     = SQLBuilder.createUpdateSingle(TAB_GROUPS, COL_GRPS_NAME).build(d, statements);
			removeGroupStatement     = SQLBuilder.createDelete(TAB_GROUPS).addPreparedWhereCondition(COL_GRPS_NAME).build(d, statements);
			removeAllGroupsStatement = SQLBuilder.createDelete(TAB_GROUPS).build(d, statements);

			selectCoversFullStatement = SQLBuilder.createSelectAll(TAB_COVERS).build(d, statements);
			selectCoversFastStatement = SQLBuilder.createSelectAll(TAB_COVERS).remSelectField(COL_CVRS_PREVIEW).build(d, statements);
			insertCoversStatement     = SQLBuilder.createInsertSingle(TAB_COVERS).build(d, statements);
			removeCoversStatement     = SQLBuilder.createDelete(TAB_COVERS).addPreparedWhereCondition(COL_CVRS_ID).build(d, statements);

			countHistory = SQLBuilder
					.createCustom(TAB_HISTORY)
					.setSQL("SELECT COUNT(*) FROM HISTORY")
					.build(d, statements);

			queryHistoryStatement = SQLBuilder.createSelectAll(TAB_HISTORY).setOrder(COL_HISTORY_DATE, SQLOrder.ASC).build(d, statements);

			queryHistoryStatementFiltered = SQLBuilder.createSelectAll(TAB_HISTORY)
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
