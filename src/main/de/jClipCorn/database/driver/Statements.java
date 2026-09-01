package de.jClipCorn.database.driver;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.sqlwrapper.CCSQLStatement;
import de.jClipCorn.util.sqlwrapper.SQLBuilder;
import de.jClipCorn.util.sqlwrapper.SQLOrder;
import de.jClipCorn.util.sqlwrapper.SQLWrapperException;

import java.sql.SQLException;
import java.util.ArrayList;

import static de.jClipCorn.database.driver.DatabaseStructure.*;

@SuppressWarnings("nls")
public class Statements {

	//--------------------------------------------------------------------------------------------------

	public CCSQLStatement addEmptyMovieTabStatement;
	public CCSQLStatement addEmptySeriesTabStatement;
	public CCSQLStatement addEmptySeasonTabStatement;
	public CCSQLStatement addEmptyEpisodeTabStatement;

	public CCSQLStatement updateMovieTabStatement;
	public CCSQLStatement updateSeriesTabStatement;
	public CCSQLStatement updateSeasonTabStatement;
	public CCSQLStatement updateEpisodeTabStatement;

	public CCSQLStatement upsertMovieUserDataStatement;
	public CCSQLStatement upsertSeriesUserDataStatement;
	public CCSQLStatement upsertSeasonUserDataStatement;
	public CCSQLStatement upsertEpisodeUserDataStatement;

	public CCSQLStatement deleteMovieUserDataStatement;
	public CCSQLStatement deleteSeriesUserDataStatement;
	public CCSQLStatement deleteSeasonUserDataStatement;
	public CCSQLStatement deleteEpisodeUserDataStatement;

	public CCSQLStatement selectAllMoviesTabStatement;
	public CCSQLStatement selectAllSeriesTabStatement;
	public CCSQLStatement selectAllSeasonTabStatement;
	public CCSQLStatement selectAllEpisodeTabStatement;

	public CCSQLStatement selectSeasonTabStatement;
	public CCSQLStatement selectEpisodeTabStatement;

	public CCSQLStatement deleteMovieTabStatement;
	public CCSQLStatement deleteSeriesTabStatement;
	public CCSQLStatement deleteSeasonTabStatement;
	public CCSQLStatement deleteEpisodeTabStatement;

	public CCSQLStatement selectSingleMovieTabStatement;
	public CCSQLStatement selectSingleSeriesTabStatement;
	public CCSQLStatement selectSingleSeasonTabStatement;
	public CCSQLStatement selectSingleEpisodeTabStatement;

	public CCSQLStatement writeInfoKeyStatement;
	public CCSQLStatement readInfoKeyStatement;

	public CCSQLStatement writeUserDataInfoKeyStatement;
	public CCSQLStatement readUserDataInfoKeyStatement;

	public CCSQLStatement readAllPropertiesStatement;
	public CCSQLStatement writePropertyKeyStatement;

	public CCSQLStatement selectGroupsStatement;
	public CCSQLStatement insertGroupStatement;
	public CCSQLStatement removeGroupStatement;
	public CCSQLStatement updateGroupStatement;
	public CCSQLStatement removeAllGroupsStatement;

	public CCSQLStatement selectFiltersStatement;
	public CCSQLStatement insertFilterStatement;
	public CCSQLStatement removeAllFiltersStatement;

	public CCSQLStatement selectCoversFullStatement;
	public CCSQLStatement selectCoversFastStatement;
	public CCSQLStatement selectSingleCoverStatement;
	public CCSQLStatement insertCoversStatement;
	public CCSQLStatement removeCoversStatement;

	private ArrayList<CCSQLStatement> statements = new ArrayList<>();

	public Statements() {

	}

	public void initialize(CCDatabase d) throws SQLException, SQLWrapperException {
		try {
			statements = new ArrayList<>();

			addEmptyMovieTabStatement   = SQLBuilder.createInsertSingle(TAB_MOVIES).build(d, statements);
			addEmptySeriesTabStatement  = SQLBuilder.createInsertSingle(TAB_SERIES).build(d, statements);
			addEmptySeasonTabStatement  = SQLBuilder.createInsertSingle(TAB_SEASONS).build(d, statements);
			addEmptyEpisodeTabStatement = SQLBuilder.createInsertSingle(TAB_EPISODES).build(d, statements);

			updateMovieTabStatement   = SQLBuilder.createUpdateSingle(TAB_MOVIES,   COL_MOV_ID).build(d, statements);
			updateSeriesTabStatement  = SQLBuilder.createUpdateSingle(TAB_SERIES,   COL_SER_ID).build(d, statements);
			updateSeasonTabStatement  = SQLBuilder.createUpdateSingle(TAB_SEASONS,  COL_SEAS_ID).build(d, statements);
			updateEpisodeTabStatement = SQLBuilder.createUpdateSingle(TAB_EPISODES, COL_EPIS_ID).build(d, statements);

			upsertMovieUserDataStatement   = SQLBuilder.createUpsertSingle(TAB_UD_MOVIES).build(d, statements);
			upsertSeriesUserDataStatement  = SQLBuilder.createUpsertSingle(TAB_UD_SERIES).build(d, statements);
			upsertSeasonUserDataStatement  = SQLBuilder.createUpsertSingle(TAB_UD_SEASONS).build(d, statements);
			upsertEpisodeUserDataStatement = SQLBuilder.createUpsertSingle(TAB_UD_EPISODES).build(d, statements);

			deleteMovieUserDataStatement   = SQLBuilder.createDelete(TAB_UD_MOVIES).addPreparedWhereCondition(COL_UD_MOV_ID).build(d, statements);
			deleteSeriesUserDataStatement  = SQLBuilder.createDelete(TAB_UD_SERIES).addPreparedWhereCondition(COL_UD_SER_ID).build(d, statements);
			deleteSeasonUserDataStatement  = SQLBuilder.createDelete(TAB_UD_SEASONS).addPreparedWhereCondition(COL_UD_SEAS_ID).build(d, statements);
			deleteEpisodeUserDataStatement = SQLBuilder.createDelete(TAB_UD_EPISODES).addPreparedWhereCondition(COL_UD_EPIS_ID).build(d, statements);

			selectAllMoviesTabStatement  = SQLBuilder.createSelectAllJoined(TAB_MOVIES,   TAB_UD_MOVIES  ).setOrder(COL_MOV_ID,         SQLOrder.ASC).build(d, statements);
			selectAllSeriesTabStatement  = SQLBuilder.createSelectAllJoined(TAB_SERIES,   TAB_UD_SERIES  ).setOrder(COL_SER_ID,         SQLOrder.ASC).build(d, statements);
			selectAllSeasonTabStatement  = SQLBuilder.createSelectAllJoined(TAB_SEASONS,  TAB_UD_SEASONS ).setOrder(COL_SEAS_SERIESID,  SQLOrder.ASC).build(d, statements);
			selectAllEpisodeTabStatement = SQLBuilder.createSelectAllJoined(TAB_EPISODES, TAB_UD_EPISODES).setOrder(COL_EPIS_SEASONID,  SQLOrder.ASC).build(d, statements);

			deleteMovieTabStatement   = SQLBuilder.createDelete(TAB_MOVIES).addPreparedWhereCondition(COL_MOV_ID).build(d, statements);
			deleteSeriesTabStatement  = SQLBuilder.createDelete(TAB_SERIES).addPreparedWhereCondition(COL_SER_ID).build(d, statements);
			deleteSeasonTabStatement  = SQLBuilder.createDelete(TAB_SEASONS).addPreparedWhereCondition(COL_SEAS_ID).build(d, statements);
			deleteEpisodeTabStatement = SQLBuilder.createDelete(TAB_EPISODES).addPreparedWhereCondition(COL_EPIS_ID).build(d, statements);

			selectSeasonTabStatement = SQLBuilder.createSelectAllJoined(TAB_SEASONS, TAB_UD_SEASONS)
					.addPreparedWhereCondition(COL_SEAS_SERIESID)
					.setOrder(COL_SEAS_SERIESID, SQLOrder.ASC)
					.build(d, statements);

			selectEpisodeTabStatement = SQLBuilder.createSelectAllJoined(TAB_EPISODES, TAB_UD_EPISODES)
					.addPreparedWhereCondition(COL_EPIS_SEASONID)
					.setOrder(COL_EPIS_EPISODE, SQLOrder.ASC)
					.build(d, statements);

			selectSingleMovieTabStatement   = SQLBuilder.createSelectAllJoined(TAB_MOVIES,   TAB_UD_MOVIES  ).addPreparedWhereCondition(COL_MOV_ID).build(d, statements);
			selectSingleSeriesTabStatement  = SQLBuilder.createSelectAllJoined(TAB_SERIES,   TAB_UD_SERIES  ).addPreparedWhereCondition(COL_SER_ID).build(d, statements);
			selectSingleSeasonTabStatement  = SQLBuilder.createSelectAllJoined(TAB_SEASONS,  TAB_UD_SEASONS ).addPreparedWhereCondition(COL_SEAS_ID).build(d, statements);
			selectSingleEpisodeTabStatement = SQLBuilder.createSelectAllJoined(TAB_EPISODES, TAB_UD_EPISODES).addPreparedWhereCondition(COL_EPIS_ID).build(d, statements);

			readInfoKeyStatement  = SQLBuilder.createSelect(TAB_INFO).addSelectField(COL_INFO_VALUE).addPreparedWhereCondition(COL_INFO_KEY).build(d, statements);
			writeInfoKeyStatement = SQLBuilder.createUpsert(TAB_INFO).addPreparedField(COL_INFO_KEY).addPreparedField(COL_INFO_VALUE).build(d, statements);

			readUserDataInfoKeyStatement  = SQLBuilder.createSelect(TAB_UD_INFO).addSelectField(COL_INFO_VALUE).addPreparedWhereCondition(COL_INFO_KEY).build(d, statements);
			writeUserDataInfoKeyStatement = SQLBuilder.createUpsert(TAB_UD_INFO).addPreparedField(COL_INFO_KEY).addPreparedField(COL_INFO_VALUE).build(d, statements);

			readAllPropertiesStatement = SQLBuilder.createSelect(TAB_PROPERTIES).addSelectField(COL_PROP_KEY).addSelectField(COL_PROP_VALUE).build(d, statements);
			writePropertyKeyStatement  = SQLBuilder.createInsertOrReplace(TAB_PROPERTIES).addPreparedField(COL_PROP_KEY).addPreparedField(COL_PROP_VALUE).addPreparedField(COL_PROP_LAST_CHANGED).build(d, statements);

			selectGroupsStatement    = SQLBuilder.createSelectAll(TAB_GROUPS).build(d, statements);
			insertGroupStatement     = SQLBuilder.createInsertSingle(TAB_GROUPS).build(d, statements);
			updateGroupStatement     = SQLBuilder.createUpdateSingle(TAB_GROUPS, COL_GRPS_NAME).build(d, statements);
			removeGroupStatement     = SQLBuilder.createDelete(TAB_GROUPS).addPreparedWhereCondition(COL_GRPS_NAME).build(d, statements);
			removeAllGroupsStatement = SQLBuilder.createDelete(TAB_GROUPS).build(d, statements);

			selectFiltersStatement    = SQLBuilder.createSelectAll(TAB_FILTERS).setOrder(COL_FILT_SORT, SQLOrder.ASC).build(d, statements);
			insertFilterStatement     = SQLBuilder.createInsertSingle(TAB_FILTERS).build(d, statements);
			removeAllFiltersStatement = SQLBuilder.createDelete(TAB_FILTERS).build(d, statements);

			selectCoversFullStatement = SQLBuilder.createSelectAll(TAB_COVERS).build(d, statements);
			selectCoversFastStatement = SQLBuilder.createSelectAll(TAB_COVERS).remSelectField(COL_CVRS_PREVIEW).build(d, statements);
			selectSingleCoverStatement = SQLBuilder.createSelectSingle(TAB_COVERS, COL_CVRS_ID).build(d, statements);
			insertCoversStatement     = SQLBuilder.createInsertSingle(TAB_COVERS).build(d, statements);
			removeCoversStatement     = SQLBuilder.createDelete(TAB_COVERS).addPreparedWhereCondition(COL_CVRS_ID).build(d, statements);

			if (!CCLog.isUnitTest()) CCLog.addDebug(String.format("%d SQL Statements prepared", statements.size())); //$NON-NLS-1$

		} catch (SQLException | SQLWrapperException e) {
			CCLog.addFatalError(LocaleBundle.getString("LogMessage.CouldNotCreatePreparedStatement"), e);
			throw e;
		}
	}
	
	public void shutdown() {
		try {
			for (CCSQLStatement stmt : statements) stmt.tryClose();
		} catch (Exception e) {
			CCLog.addFatalError(e);
		}	
	}
}
