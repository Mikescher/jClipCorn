package de.jClipCorn.database.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.jClipCorn.database.CCDatabase;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.DoubleString;
import de.jClipCorn.util.sql.SQLInsertHelper;
import de.jClipCorn.util.sql.SQLOrder;
import de.jClipCorn.util.sql.SQLSelectHelper;
import de.jClipCorn.util.sql.SQLUpdateHelper;

public class Statements {
	public static PreparedStatement addEmptyMainTabStatement;
	public static PreparedStatement addEmptySeasonTabStatement;
	public static PreparedStatement addEmptyEpisodeTabStatement;
	
	public static PreparedStatement newLocalIDStatement;
	public static PreparedStatement newSeriesIDStatement;
	public static PreparedStatement newSeasonIDStatement;
	public static PreparedStatement newEpisodeIDStatement;
	
	public static PreparedStatement updateMainTabStatement;
	public static PreparedStatement updateSeriesTabStatement;
	public static PreparedStatement updateSeasonTabStatement;
	public static PreparedStatement updateEpisodeTabStatement;
	
	public static PreparedStatement selectMainTabStatement;
	public static PreparedStatement selectSeasonTabStatement;
	public static PreparedStatement selectEpisodeTabStatement;
	
	public static PreparedStatement deleteMainTabStatement;
	public static PreparedStatement deleteSeasonTabStatement;
	public static PreparedStatement deleteEpisodeTabStatement;
	
	public static PreparedStatement selectSingleMainTabStatement;
	public static PreparedStatement selectSingleSeasonTabStatement;
	public static PreparedStatement selectSingleEpisodeTabStatement;

	public static PreparedStatement addInfoKey;
	public static PreparedStatement selectInfoKey;
	public static PreparedStatement updateInfoKey;

	public static void intialize(CCDatabase d) {
		try {
			intialize_addMainTab(d);
			intialize_addSeasonTab(d);
			intialize_addEpisodeTab(d);
			
			intialize_newID(d);
			
			intialize_updateMainTab(d);
			intialize_updatesSeriesTab(d);
			intialize_updatesSeasonTab(d);
			intialize_updatesEpisodeTab(d);
			
			intialize_selectMainTab(d);
			intialize_selectSeasonTab(d);
			intialize_selectEpisodeTab(d);
			
			intialize_deleteTab(d);
			
			intialize_selectSingleMainTab(d);
			intialize_selectSingleSeasonTab(d);
			intialize_selectSingleEpisodeTab(d);

			intialize_addInfoKey(d);
			intialize_selectInfoKey(d);
			intialize_updateInfoKey(d);
			
		} catch (SQLException e) {
			CCLog.addFatalError(LocaleBundle.getString("LogMessage.CouldNotCreatePreparedStatement"), e); //$NON-NLS-1$
		}
	}
	
	public static void shutdown() {
		try {
			addEmptyMainTabStatement.close();
			addEmptySeasonTabStatement.close();
			addEmptyEpisodeTabStatement.close();
					
			newLocalIDStatement.close();
			newSeriesIDStatement.close();
			newSeasonIDStatement.close();
			newEpisodeIDStatement.close();
					
			updateMainTabStatement.close();
			updateSeriesTabStatement.close();
			updateSeasonTabStatement.close();
			updateEpisodeTabStatement.close();
					
			selectMainTabStatement.close();
			selectSeasonTabStatement.close();
			selectEpisodeTabStatement.close();
					
			deleteMainTabStatement.close();
			deleteSeasonTabStatement.close();
			deleteEpisodeTabStatement.close();
			
			selectSingleMainTabStatement.close();
			selectSingleSeasonTabStatement.close();
			selectSingleEpisodeTabStatement.close();

			addInfoKey.close();
			selectInfoKey.close();
			updateInfoKey.close();
			
		} catch (Exception e) {
			CCLog.addFatalError(e);
		}	
	}
	
	@SuppressWarnings("nls")
	private static void intialize_addMainTab(CCDatabase d) throws SQLException {
		SQLInsertHelper ih = new SQLInsertHelper(CCDatabase.TAB_MAIN);
		
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_LOCALID, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_NAME, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_VIEWED, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_ZYKLUS, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_ZYKLUSNUMBER, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_QUALITY, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_LANGUAGE, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_GENRE, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_LENGTH, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_ADDDATE, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_ONLINESCORE, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_FSK, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_FORMAT, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_MOVIEYEAR, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_FILESIZE, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_TAGS, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_PART_1, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_PART_2, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_PART_3, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_PART_4, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_PART_5, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_PART_6, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_SCORE, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_COVER, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_TYPE, "?");
		ih.addField(CCDatabase.TAB_MAIN_COLUMN_SERIES_ID, "?");
		
		addEmptyMainTabStatement = d.createPreparedStatement(ih.get());
	}
	
	@SuppressWarnings("nls")
	private static void intialize_addSeasonTab(CCDatabase d) throws SQLException {
		SQLInsertHelper ih = new SQLInsertHelper(CCDatabase.TAB_SEASONS);

		ih.addField(CCDatabase.TAB_SEASONS_COLUMN_SEASONID, "?");
		ih.addField(CCDatabase.TAB_SEASONS_COLUMN_SERIESID, "?");
		ih.addField(CCDatabase.TAB_SEASONS_COLUMN_NAME, "?");
		ih.addField(CCDatabase.TAB_SEASONS_COLUMN_YEAR, "?");
		ih.addField(CCDatabase.TAB_SEASONS_COLUMN_COVERNAME, "?");
		
		addEmptySeasonTabStatement = d.createPreparedStatement(ih.get());
	}
	
	@SuppressWarnings("nls")
	private static void intialize_addEpisodeTab(CCDatabase d) throws SQLException {
		SQLInsertHelper ih = new SQLInsertHelper(CCDatabase.TAB_EPISODES);
		
		ih.addField(CCDatabase.TAB_EPISODES_COLUMN_LOCALID, "?");
		ih.addField(CCDatabase.TAB_EPISODES_COLUMN_SEASONID, "?");
		ih.addField(CCDatabase.TAB_EPISODES_COLUMN_EPISODE, "?");
		ih.addField(CCDatabase.TAB_EPISODES_COLUMN_NAME, "?");
		ih.addField(CCDatabase.TAB_EPISODES_COLUMN_VIEWED, "?");
		ih.addField(CCDatabase.TAB_EPISODES_COLUMN_QUALITY, "?");
		ih.addField(CCDatabase.TAB_EPISODES_COLUMN_LENGTH, "?");
		ih.addField(CCDatabase.TAB_EPISODES_COLUMN_FORMAT, "?");
		ih.addField(CCDatabase.TAB_EPISODES_COLUMN_FILESIZE, "?");
		ih.addField(CCDatabase.TAB_EPISODES_COLUMN_PART_1, "?");
		ih.addField(CCDatabase.TAB_EPISODES_COLUMN_TAGS, "?");
		ih.addField(CCDatabase.TAB_EPISODES_COLUMN_LASTVIEWED, "?");
		ih.addField(CCDatabase.TAB_EPISODES_COLUMN_ADDDATE, "?");
		
		addEmptyEpisodeTabStatement = d.createPreparedStatement(ih.get());
	}
	
	@SuppressWarnings("nls")
	private static void intialize_newID(CCDatabase d) throws SQLException {
		newLocalIDStatement   = d.createPreparedStatement(String.format("SELECT COALESCE(MAX(%s), -1) FROM %s", CCDatabase.TAB_MAIN_COLUMN_LOCALID, CCDatabase.TAB_MAIN));
		newSeriesIDStatement  = d.createPreparedStatement(String.format("SELECT COALESCE(MAX(%s), -1) FROM %s", CCDatabase.TAB_MAIN_COLUMN_SERIES_ID, CCDatabase.TAB_MAIN));
		newSeasonIDStatement  = d.createPreparedStatement(String.format("SELECT COALESCE(MAX(%s), -1) FROM %s", CCDatabase.TAB_SEASONS_COLUMN_SEASONID, CCDatabase.TAB_SEASONS));
		newEpisodeIDStatement = d.createPreparedStatement(String.format("SELECT COALESCE(MAX(%s), -1) FROM %s", CCDatabase.TAB_EPISODES_COLUMN_LOCALID, CCDatabase.TAB_EPISODES));
	}
	
	@SuppressWarnings("nls")
	private static void intialize_updateMainTab(CCDatabase d) throws SQLException {
		SQLUpdateHelper uh = new SQLUpdateHelper(CCDatabase.TAB_MAIN, new DoubleString(CCDatabase.TAB_MAIN_COLUMN_LOCALID, "?"));
		
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_NAME, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_VIEWED, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_ZYKLUS, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_ZYKLUSNUMBER, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_QUALITY, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_LANGUAGE, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_GENRE, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_LENGTH, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_ADDDATE, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_ONLINESCORE, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_FSK, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_FORMAT, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_MOVIEYEAR, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_FILESIZE, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_TAGS, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_PART_1, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_PART_2, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_PART_3, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_PART_4, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_PART_5, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_PART_6, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_SCORE, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_COVER, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_TYPE, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_SERIES_ID, "?");
		
		updateMainTabStatement = d.createPreparedStatement(uh.get());
	}
	
	@SuppressWarnings("nls")
	private static void intialize_updatesSeriesTab(CCDatabase d) throws SQLException {
		SQLUpdateHelper uh = new SQLUpdateHelper(CCDatabase.TAB_MAIN, new DoubleString(CCDatabase.TAB_MAIN_COLUMN_LOCALID, "?"));
		
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_NAME, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_LANGUAGE, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_GENRE, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_ONLINESCORE, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_FSK, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_SCORE, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_COVER, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_TYPE, "?");
		uh.addField(CCDatabase.TAB_MAIN_COLUMN_SERIES_ID, "?");
		
		updateSeriesTabStatement = d.createPreparedStatement(uh.get());
	}
	
	@SuppressWarnings("nls")
	private static void intialize_updatesSeasonTab(CCDatabase d) throws SQLException {
		SQLUpdateHelper uh = new SQLUpdateHelper(CCDatabase.TAB_SEASONS, new DoubleString(CCDatabase.TAB_SEASONS_COLUMN_SEASONID, "?"));
		
		uh.addField(CCDatabase.TAB_SEASONS_COLUMN_SERIESID, "?");
		uh.addField(CCDatabase.TAB_SEASONS_COLUMN_NAME, "?");
		uh.addField(CCDatabase.TAB_SEASONS_COLUMN_YEAR, "?");
		uh.addField(CCDatabase.TAB_SEASONS_COLUMN_COVERNAME, "?");
		
		updateSeasonTabStatement = d.createPreparedStatement(uh.get());
	}
	
	@SuppressWarnings("nls")
	private static void intialize_updatesEpisodeTab(CCDatabase d) throws SQLException {
		SQLUpdateHelper uh = new SQLUpdateHelper(CCDatabase.TAB_EPISODES, new DoubleString(CCDatabase.TAB_EPISODES_COLUMN_LOCALID, "?"));
		
		uh.addField(CCDatabase.TAB_EPISODES_COLUMN_SEASONID, "?");
		uh.addField(CCDatabase.TAB_EPISODES_COLUMN_EPISODE, "?");
		uh.addField(CCDatabase.TAB_EPISODES_COLUMN_NAME, "?");
		uh.addField(CCDatabase.TAB_EPISODES_COLUMN_VIEWED, "?");
		uh.addField(CCDatabase.TAB_EPISODES_COLUMN_QUALITY, "?");
		uh.addField(CCDatabase.TAB_EPISODES_COLUMN_LENGTH, "?");
		uh.addField(CCDatabase.TAB_EPISODES_COLUMN_FORMAT, "?");
		uh.addField(CCDatabase.TAB_EPISODES_COLUMN_FILESIZE, "?");
		uh.addField(CCDatabase.TAB_EPISODES_COLUMN_PART_1, "?");
		uh.addField(CCDatabase.TAB_EPISODES_COLUMN_TAGS, "?");
		uh.addField(CCDatabase.TAB_EPISODES_COLUMN_LASTVIEWED, "?");
		uh.addField(CCDatabase.TAB_EPISODES_COLUMN_ADDDATE, "?");
		
		updateEpisodeTabStatement = d.createPreparedStatement(uh.get());
	}
	
	private static void intialize_selectMainTab(CCDatabase d) throws SQLException {
		SQLSelectHelper sh = new SQLSelectHelper(CCDatabase.TAB_MAIN);
		
		sh.setOrderBy(CCDatabase.TAB_MAIN_COLUMN_LOCALID);
		sh.setOrder(SQLOrder.ASC);		
		
		selectMainTabStatement = d.createPreparedStatement(sh.get());
	}
	
	@SuppressWarnings("nls")
	private static void intialize_selectSeasonTab(CCDatabase d) throws SQLException {
		SQLSelectHelper sh = new SQLSelectHelper(CCDatabase.TAB_SEASONS);
		
		sh.addWhere(CCDatabase.TAB_SEASONS_COLUMN_SERIESID, "?");
		
		sh.setOrderBy(CCDatabase.TAB_SEASONS_COLUMN_NAME);
		sh.setOrder(SQLOrder.ASC);
		
		selectSeasonTabStatement = d.createPreparedStatement(sh.get());
	}
	
	@SuppressWarnings("nls")
	private static void intialize_selectEpisodeTab(CCDatabase d) throws SQLException {
		SQLSelectHelper sh = new SQLSelectHelper(CCDatabase.TAB_EPISODES);
		
		sh.addWhere(CCDatabase.TAB_EPISODES_COLUMN_SEASONID, "?");
		
		sh.setOrderBy(CCDatabase.TAB_EPISODES_COLUMN_EPISODE);
		sh.setOrder(SQLOrder.ASC);
		
		selectEpisodeTabStatement = d.createPreparedStatement(sh.get());
	}
	
	@SuppressWarnings("nls")
	private static void intialize_deleteTab(CCDatabase d) throws SQLException {
		deleteMainTabStatement = d.createPreparedStatement(String.format("DELETE FROM %s WHERE %s=?", CCDatabase.TAB_MAIN, CCDatabase.TAB_MAIN_COLUMN_LOCALID));
		deleteSeasonTabStatement = d.createPreparedStatement(String.format("DELETE FROM %s WHERE %s=?", CCDatabase.TAB_SEASONS, CCDatabase.TAB_SEASONS_COLUMN_SEASONID));
		deleteEpisodeTabStatement = d.createPreparedStatement(String.format("DELETE FROM %s WHERE %s=?", CCDatabase.TAB_EPISODES, CCDatabase.TAB_EPISODES_COLUMN_LOCALID));
	}
	
	@SuppressWarnings("nls")
	private static void intialize_selectSingleMainTab(CCDatabase d) throws SQLException {
		SQLSelectHelper sh = new SQLSelectHelper(CCDatabase.TAB_MAIN);
		
		sh.addWhere(CCDatabase.TAB_MAIN_COLUMN_LOCALID, "?");
		
		selectSingleMainTabStatement = d.createPreparedStatement(sh.get());
	}
	
	@SuppressWarnings("nls")
	private static void intialize_selectSingleSeasonTab(CCDatabase d) throws SQLException {
		SQLSelectHelper sh = new SQLSelectHelper(CCDatabase.TAB_SEASONS);
		
		sh.addWhere(CCDatabase.TAB_SEASONS_COLUMN_SEASONID, "?");
		
		selectSingleSeasonTabStatement = d.createPreparedStatement(sh.get());
	}
	
	@SuppressWarnings("nls")
	private static void intialize_selectSingleEpisodeTab(CCDatabase d) throws SQLException {
		SQLSelectHelper sh = new SQLSelectHelper(CCDatabase.TAB_MAIN);
		
		sh.addWhere(CCDatabase.TAB_EPISODES_COLUMN_LOCALID, "?");
		
		selectSingleEpisodeTabStatement = d.createPreparedStatement(sh.get());
	}

	@SuppressWarnings("nls")
	private static void intialize_selectInfoKey(CCDatabase d) throws SQLException {
		SQLSelectHelper sh = new SQLSelectHelper(CCDatabase.TAB_INFO);
		
		sh.addWhere(CCDatabase.TAB_INFO_COLUMN_KEY, "?");
		sh.setTarget(CCDatabase.TAB_INFO_COLUMN_VALUE);
		
		selectInfoKey = d.createPreparedStatement(sh.get());
	}

	@SuppressWarnings("nls")
	private static void intialize_addInfoKey(CCDatabase d) throws SQLException {
		SQLInsertHelper ih = new SQLInsertHelper(CCDatabase.TAB_INFO);
		
		ih.addField(CCDatabase.TAB_INFO_COLUMN_KEY, "?");
		ih.addField(CCDatabase.TAB_INFO_COLUMN_VALUE, "?");
		
		addInfoKey = d.createPreparedStatement(ih.get());
	}

	@SuppressWarnings("nls")
	private static void intialize_updateInfoKey(CCDatabase d) throws SQLException {
		SQLUpdateHelper ih = new SQLUpdateHelper(CCDatabase.TAB_INFO, new DoubleString(CCDatabase.TAB_INFO_COLUMN_KEY, "?"));
		
		ih.addField(CCDatabase.TAB_INFO_COLUMN_VALUE, "?");
		
		updateInfoKey = d.createPreparedStatement(ih.get());
	}
	
}
