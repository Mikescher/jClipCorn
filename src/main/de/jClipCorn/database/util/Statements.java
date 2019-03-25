package de.jClipCorn.database.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.datatypes.DoubleString;
import de.jClipCorn.util.sql.SQLDeleteHelper;
import de.jClipCorn.util.sql.SQLInsertHelper;
import de.jClipCorn.util.sql.SQLOrder;
import de.jClipCorn.util.sql.SQLSelectHelper;
import de.jClipCorn.util.sql.SQLUpdateHelper;

@SuppressWarnings("nls")
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
	
	public static PreparedStatement selectAllMainTabStatement;
	public static PreparedStatement selectAllSeasonTabStatement;
	public static PreparedStatement selectAllEpisodeTabStatement;

	public static PreparedStatement selectSeasonTabStatement;
	public static PreparedStatement selectEpisodeTabStatement;
	
	public static PreparedStatement deleteMainTabStatement;
	public static PreparedStatement deleteSeasonTabStatement;
	public static PreparedStatement deleteEpisodeTabStatement;
	
	public static PreparedStatement selectSingleMainTabStatement;
	public static PreparedStatement selectSingleSeasonTabStatement;
	public static PreparedStatement selectSingleEpisodeTabStatement;

	public static PreparedStatement addInfoKeyStatement;
	public static PreparedStatement selectInfoKeyStatement;
	public static PreparedStatement updateInfoKeyStatement;

	public static PreparedStatement selectGroupsStatement;
	public static PreparedStatement insertGroupStatement;
	public static PreparedStatement removeGroupStatement;
	public static PreparedStatement updateGroupStatement;

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

			intialize_selectGroups(d);
			intialize_insertGroup(d);
			intialize_updateGroup(d);
			intialize_removeGroup(d);
			
		} catch (SQLException e) {
			CCLog.addFatalError(LocaleBundle.getString("LogMessage.CouldNotCreatePreparedStatement"), e);
		}
	}
	
	public static void shutdown() {
		try {
			TryClose(addEmptyMainTabStatement);
			TryClose(addEmptySeasonTabStatement);
			TryClose(addEmptyEpisodeTabStatement);
					
			TryClose(newLocalIDStatement);
			TryClose(newSeriesIDStatement);
			TryClose(newSeasonIDStatement);
			TryClose(newEpisodeIDStatement);
					
			TryClose(updateMainTabStatement);
			TryClose(updateSeriesTabStatement);
			TryClose(updateSeasonTabStatement);
			TryClose(updateEpisodeTabStatement);

			TryClose(selectAllMainTabStatement);
			TryClose(selectAllSeasonTabStatement);
			TryClose(selectAllEpisodeTabStatement);

			TryClose(selectSeasonTabStatement);
			TryClose(selectEpisodeTabStatement);
					
			TryClose(deleteMainTabStatement);
			TryClose(deleteSeasonTabStatement);
			TryClose(deleteEpisodeTabStatement);
			
			TryClose(selectSingleMainTabStatement);
			TryClose(selectSingleSeasonTabStatement);
			TryClose(selectSingleEpisodeTabStatement);

			TryClose(addInfoKeyStatement);
			TryClose(selectInfoKeyStatement);
			TryClose(updateInfoKeyStatement);

			TryClose(selectGroupsStatement);
			TryClose(insertGroupStatement);
			TryClose(removeGroupStatement);
			TryClose(updateGroupStatement);
			
		} catch (Exception e) {
			CCLog.addFatalError(e);
		}	
	}
	
	private static void TryClose(PreparedStatement stmt) throws SQLException {
		if (stmt == null) return;
		if (stmt.isClosed()) return;
		
		stmt.close();
	}
	
	private static void intialize_addMainTab(CCDatabase d) throws SQLException {
		SQLInsertHelper ih = new SQLInsertHelper(CCDatabase.TAB_MAIN);
		
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_LOCALID);            // 01   TAB_MAIN_COLUMN_LOCALID
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_NAME);               // 02   TAB_MAIN_COLUMN_NAME
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_VIEWED);             // 03   TAB_MAIN_COLUMN_VIEWED
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_VIEWEDHISTORY);      // 04   TAB_MAIN_COLUMN_VIEWEDHISTORY
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_ZYKLUS);             // 05   TAB_MAIN_COLUMN_ZYKLUS
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_ZYKLUSNUMBER);       // 06   TAB_MAIN_COLUMN_ZYKLUSNUMBER
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_QUALITY);            // 07   TAB_MAIN_COLUMN_QUALITY
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_LANGUAGE);           // 08   TAB_MAIN_COLUMN_LANGUAGE
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_GENRE);              // 09   TAB_MAIN_COLUMN_GENRE
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_LENGTH);             // 10   TAB_MAIN_COLUMN_LENGTH
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_ADDDATE);            // 11   TAB_MAIN_COLUMN_ADDDATE
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_ONLINESCORE);        // 12   TAB_MAIN_COLUMN_ONLINESCORE
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_FSK);                // 13   TAB_MAIN_COLUMN_FSK
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_FORMAT);             // 14   TAB_MAIN_COLUMN_FORMAT
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_MOVIEYEAR);          // 15   TAB_MAIN_COLUMN_MOVIEYEAR
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_ONLINEREF);          // 16   TAB_MAIN_COLUMN_ONLINEREF
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_GROUPS);             // 17   TAB_MAIN_COLUMN_GROUPS
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_FILESIZE);           // 18   TAB_MAIN_COLUMN_FILESIZE
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_TAGS);               // 19   TAB_MAIN_COLUMN_TAGS
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_PART_1);             // 20   TAB_MAIN_COLUMN_PART_1
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_PART_2);             // 21   TAB_MAIN_COLUMN_PART_2
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_PART_3);             // 22   TAB_MAIN_COLUMN_PART_3
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_PART_4);             // 23   TAB_MAIN_COLUMN_PART_4
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_PART_5);             // 24   TAB_MAIN_COLUMN_PART_5
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_PART_6);             // 25   TAB_MAIN_COLUMN_PART_6
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_SCORE);              // 26   TAB_MAIN_COLUMN_SCORE
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_COVER);              // 27   TAB_MAIN_COLUMN_COVER
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_TYPE);               // 28   TAB_MAIN_COLUMN_TYPE
		ih.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_SERIES_ID);          // 29   TAB_MAIN_COLUMN_SERIES_ID
		
		addEmptyMainTabStatement = ih.prepare(d);
	}
	
	private static void intialize_addSeasonTab(CCDatabase d) throws SQLException {
		SQLInsertHelper ih = new SQLInsertHelper(CCDatabase.TAB_SEASONS);

		ih.addPreparedField(CCDatabase.TAB_SEASONS_COLUMN_SEASONID);
		ih.addPreparedField(CCDatabase.TAB_SEASONS_COLUMN_SERIESID);
		ih.addPreparedField(CCDatabase.TAB_SEASONS_COLUMN_NAME);
		ih.addPreparedField(CCDatabase.TAB_SEASONS_COLUMN_YEAR);
		ih.addPreparedField(CCDatabase.TAB_SEASONS_COLUMN_COVERNAME);
		
		addEmptySeasonTabStatement = ih.prepare(d);
	}
	
	private static void intialize_addEpisodeTab(CCDatabase d) throws SQLException {
		SQLInsertHelper ih = new SQLInsertHelper(CCDatabase.TAB_EPISODES);
		
		ih.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_LOCALID);           // 01   TAB_EPISODES_COLUMN_LOCALID
		ih.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_SEASONID);          // 02   TAB_EPISODES_COLUMN_SEASONID
		ih.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_EPISODE);           // 03   TAB_EPISODES_COLUMN_EPISODE
		ih.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_NAME);              // 04   TAB_EPISODES_COLUMN_NAME
		ih.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_VIEWED);            // 05   TAB_EPISODES_COLUMN_VIEWED
		ih.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_VIEWEDHISTORY);     // 06   TAB_EPISODES_COLUMN_VIEWEDHISTORY
		ih.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_QUALITY);           // 07   TAB_EPISODES_COLUMN_QUALITY
		ih.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_LENGTH);            // 08   TAB_EPISODES_COLUMN_LENGTH
		ih.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_FORMAT);            // 09   TAB_EPISODES_COLUMN_FORMAT
		ih.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_FILESIZE);          // 10   TAB_EPISODES_COLUMN_FILESIZE
		ih.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_PART_1);            // 11   TAB_EPISODES_COLUMN_PART_1
		ih.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_TAGS);              // 12   TAB_EPISODES_COLUMN_TAGS
		ih.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_ADDDATE);           // 13   TAB_EPISODES_COLUMN_ADDDATE
		
		addEmptyEpisodeTabStatement = ih.prepare(d);
	}
	
	private static void intialize_newID(CCDatabase d) throws SQLException {
		newLocalIDStatement   = d.createPreparedStatement(String.format("SELECT COALESCE(MAX(%s), -1) FROM %s", CCDatabase.TAB_MAIN_COLUMN_LOCALID, CCDatabase.TAB_MAIN));
		newSeriesIDStatement  = d.createPreparedStatement(String.format("SELECT COALESCE(MAX(%s), -1) FROM %s", CCDatabase.TAB_MAIN_COLUMN_SERIES_ID, CCDatabase.TAB_MAIN));
		newSeasonIDStatement  = d.createPreparedStatement(String.format("SELECT COALESCE(MAX(%s), -1) FROM %s", CCDatabase.TAB_SEASONS_COLUMN_SEASONID, CCDatabase.TAB_SEASONS));
		newEpisodeIDStatement = d.createPreparedStatement(String.format("SELECT COALESCE(MAX(%s), -1) FROM %s", CCDatabase.TAB_EPISODES_COLUMN_LOCALID, CCDatabase.TAB_EPISODES));
	}
	
	private static void intialize_updateMainTab(CCDatabase d) throws SQLException {
		SQLUpdateHelper uh = new SQLUpdateHelper(CCDatabase.TAB_MAIN, DoubleString.createPrepared(CCDatabase.TAB_MAIN_COLUMN_LOCALID));

		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_NAME);                // 01   TAB_MAIN_COLUMN_NAME          
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_VIEWED);              // 02   TAB_MAIN_COLUMN_VIEWED        
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_VIEWEDHISTORY);       // 03   TAB_MAIN_COLUMN_VIEWEDHISTORY 
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_ZYKLUS);              // 04   TAB_MAIN_COLUMN_ZYKLUS        
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_ZYKLUSNUMBER);        // 05   TAB_MAIN_COLUMN_ZYKLUSNUMBER  
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_QUALITY);             // 06   TAB_MAIN_COLUMN_QUALITY       
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_LANGUAGE);            // 07   TAB_MAIN_COLUMN_LANGUAGE      
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_GENRE);               // 08   TAB_MAIN_COLUMN_GENRE         
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_LENGTH);              // 09   TAB_MAIN_COLUMN_LENGTH        
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_ADDDATE);             // 10   TAB_MAIN_COLUMN_ADDDATE       
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_ONLINESCORE);         // 11   TAB_MAIN_COLUMN_ONLINESCORE   
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_FSK);                 // 12   TAB_MAIN_COLUMN_FSK           
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_FORMAT);              // 13   TAB_MAIN_COLUMN_FORMAT        
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_MOVIEYEAR);           // 14   TAB_MAIN_COLUMN_MOVIEYEAR     
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_ONLINEREF);           // 15   TAB_MAIN_COLUMN_ONLINEREF     
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_GROUPS);              // 16   TAB_MAIN_COLUMN_GROUPS        
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_FILESIZE);            // 17   TAB_MAIN_COLUMN_FILESIZE      
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_TAGS);                // 18   TAB_MAIN_COLUMN_TAGS          
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_PART_1);              // 29   TAB_MAIN_COLUMN_PART_1        
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_PART_2);              // 30   TAB_MAIN_COLUMN_PART_2        
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_PART_3);              // 21   TAB_MAIN_COLUMN_PART_3        
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_PART_4);              // 22   TAB_MAIN_COLUMN_PART_4        
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_PART_5);              // 23   TAB_MAIN_COLUMN_PART_5        
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_PART_6);              // 24   TAB_MAIN_COLUMN_PART_6        
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_SCORE);               // 25   TAB_MAIN_COLUMN_SCORE         
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_COVER);               // 26   TAB_MAIN_COLUMN_COVER         
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_TYPE);                // 27   TAB_MAIN_COLUMN_TYPE          
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_SERIES_ID);           // 28   TAB_MAIN_COLUMN_SERIES_ID     
		
		updateMainTabStatement = uh.prepare(d);
	}
	
	private static void intialize_updatesSeriesTab(CCDatabase d) throws SQLException {
		SQLUpdateHelper uh = new SQLUpdateHelper(CCDatabase.TAB_MAIN, DoubleString.createPrepared(CCDatabase.TAB_MAIN_COLUMN_LOCALID));
		
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_NAME);           // 01     TAB_MAIN_COLUMN_NAME
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_LANGUAGE);       // 02     TAB_MAIN_COLUMN_LANGUAGE
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_GENRE);          // 03     TAB_MAIN_COLUMN_GENRE
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_ONLINESCORE);    // 04     TAB_MAIN_COLUMN_ONLINESCORE
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_FSK);            // 05     TAB_MAIN_COLUMN_FSK
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_ONLINEREF);      // 06     TAB_MAIN_COLUMN_ONLINEREF
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_GROUPS);         // 07     TAB_MAIN_COLUMN_GROUPS
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_SCORE);          // 08     TAB_MAIN_COLUMN_SCORE
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_COVER);          // 09     TAB_MAIN_COLUMN_COVER
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_TYPE);           // 10     TAB_MAIN_COLUMN_TYPE
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_SERIES_ID);      // 11     TAB_MAIN_COLUMN_SERIES_ID
		uh.addPreparedField(CCDatabase.TAB_MAIN_COLUMN_TAGS);           // 12     TAB_MAIN_COLUMN_TAGS
		
		updateSeriesTabStatement = uh.prepare(d);
	}
	
	private static void intialize_updatesSeasonTab(CCDatabase d) throws SQLException {
		SQLUpdateHelper uh = new SQLUpdateHelper(CCDatabase.TAB_SEASONS, DoubleString.createPrepared(CCDatabase.TAB_SEASONS_COLUMN_SEASONID));
		
		uh.addPreparedField(CCDatabase.TAB_SEASONS_COLUMN_SERIESID);
		uh.addPreparedField(CCDatabase.TAB_SEASONS_COLUMN_NAME);
		uh.addPreparedField(CCDatabase.TAB_SEASONS_COLUMN_YEAR);
		uh.addPreparedField(CCDatabase.TAB_SEASONS_COLUMN_COVERNAME);
		
		updateSeasonTabStatement = uh.prepare(d);
	}
	
	private static void intialize_updatesEpisodeTab(CCDatabase d) throws SQLException {
		SQLUpdateHelper uh = new SQLUpdateHelper(CCDatabase.TAB_EPISODES, DoubleString.createPrepared(CCDatabase.TAB_EPISODES_COLUMN_LOCALID));
		
		uh.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_SEASONID);         // TAB_EPISODES_COLUMN_SEASONID
		uh.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_EPISODE);          // TAB_EPISODES_COLUMN_EPISODE
		uh.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_NAME);             // TAB_EPISODES_COLUMN_NAME
		uh.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_VIEWED);           // TAB_EPISODES_COLUMN_VIEWED
		uh.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_VIEWEDHISTORY);    // TAB_EPISODES_COLUMN_VIEWEDHISTORY
		uh.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_QUALITY);          // TAB_EPISODES_COLUMN_QUALITY
		uh.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_LENGTH);           // TAB_EPISODES_COLUMN_LENGTH
		uh.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_FORMAT);           // TAB_EPISODES_COLUMN_FORMAT
		uh.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_FILESIZE);         // TAB_EPISODES_COLUMN_FILESIZE
		uh.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_PART_1);           // TAB_EPISODES_COLUMN_PART_1
		uh.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_TAGS);             // TAB_EPISODES_COLUMN_TAGS
		uh.addPreparedField(CCDatabase.TAB_EPISODES_COLUMN_ADDDATE);          // TAB_EPISODES_COLUMN_ADDDATE
		
		updateEpisodeTabStatement = uh.prepare(d);
	}
	
	private static void intialize_selectMainTab(CCDatabase d) throws SQLException {
		{
			SQLSelectHelper sh = new SQLSelectHelper(CCDatabase.TAB_MAIN);

			sh.setOrderBy(CCDatabase.TAB_MAIN_COLUMN_LOCALID);
			sh.setOrder(SQLOrder.ASC);

			selectAllMainTabStatement = sh.prepare(d);
		}
		{
			SQLSelectHelper sh = new SQLSelectHelper(CCDatabase.TAB_SEASONS);

			sh.setOrderBy(CCDatabase.TAB_SEASONS_COLUMN_SERIESID);
			sh.setOrder(SQLOrder.ASC);

			selectAllSeasonTabStatement = sh.prepare(d);
		}
		{
			SQLSelectHelper sh = new SQLSelectHelper(CCDatabase.TAB_EPISODES);

			sh.setOrderBy(CCDatabase.TAB_EPISODES_COLUMN_SEASONID);
			sh.setOrder(SQLOrder.ASC);

			selectAllEpisodeTabStatement = sh.prepare(d);
		}
	}
	
	private static void intialize_selectSeasonTab(CCDatabase d) throws SQLException {
		SQLSelectHelper sh = new SQLSelectHelper(CCDatabase.TAB_SEASONS);
		
		sh.addPreparedWhere(CCDatabase.TAB_SEASONS_COLUMN_SERIESID);
		
		sh.setOrderBy(CCDatabase.TAB_SEASONS_COLUMN_NAME);
		sh.setOrder(SQLOrder.ASC);
		
		selectSeasonTabStatement = sh.prepare(d);
	}
	
	private static void intialize_selectEpisodeTab(CCDatabase d) throws SQLException {
		SQLSelectHelper sh = new SQLSelectHelper(CCDatabase.TAB_EPISODES);
		
		sh.addPreparedWhere(CCDatabase.TAB_EPISODES_COLUMN_SEASONID);
		
		sh.setOrderBy(CCDatabase.TAB_EPISODES_COLUMN_EPISODE);
		sh.setOrder(SQLOrder.ASC);
		
		selectEpisodeTabStatement = sh.prepare(d);
	}
	
	private static void intialize_deleteTab(CCDatabase d) throws SQLException {
		deleteMainTabStatement = new SQLDeleteHelper(CCDatabase.TAB_MAIN, DoubleString.createPrepared(CCDatabase.TAB_MAIN_COLUMN_LOCALID)).prepare(d);
		deleteSeasonTabStatement = new SQLDeleteHelper(CCDatabase.TAB_SEASONS, DoubleString.createPrepared(CCDatabase.TAB_SEASONS_COLUMN_SEASONID)).prepare(d);
		deleteEpisodeTabStatement = new SQLDeleteHelper(CCDatabase.TAB_EPISODES, DoubleString.createPrepared(CCDatabase.TAB_EPISODES_COLUMN_LOCALID)).prepare(d);
	}
	
	private static void intialize_selectSingleMainTab(CCDatabase d) throws SQLException {
		SQLSelectHelper sh = new SQLSelectHelper(CCDatabase.TAB_MAIN);
		
		sh.addPreparedWhere(CCDatabase.TAB_MAIN_COLUMN_LOCALID);
		
		selectSingleMainTabStatement = sh.prepare(d);
	}
	
	private static void intialize_selectSingleSeasonTab(CCDatabase d) throws SQLException {
		SQLSelectHelper sh = new SQLSelectHelper(CCDatabase.TAB_SEASONS);
		
		sh.addPreparedWhere(CCDatabase.TAB_SEASONS_COLUMN_SEASONID);
		
		selectSingleSeasonTabStatement = sh.prepare(d);
	}
	
	private static void intialize_selectSingleEpisodeTab(CCDatabase d) throws SQLException {
		SQLSelectHelper sh = new SQLSelectHelper(CCDatabase.TAB_MAIN);
		
		sh.addPreparedWhere(CCDatabase.TAB_EPISODES_COLUMN_LOCALID);
		
		selectSingleEpisodeTabStatement = sh.prepare(d);
	}

	private static void intialize_selectInfoKey(CCDatabase d) throws SQLException {
		SQLSelectHelper sh = new SQLSelectHelper(CCDatabase.TAB_INFO);
		
		sh.addPreparedWhere(CCDatabase.TAB_INFO_COLUMN_KEY);
		sh.setTarget(CCDatabase.TAB_INFO_COLUMN_VALUE);
		
		selectInfoKeyStatement = sh.prepare(d);
	}

	private static void intialize_addInfoKey(CCDatabase d) throws SQLException {
		SQLInsertHelper ih = new SQLInsertHelper(CCDatabase.TAB_INFO);
		
		ih.addPreparedField(CCDatabase.TAB_INFO_COLUMN_KEY);
		ih.addPreparedField(CCDatabase.TAB_INFO_COLUMN_VALUE);
		
		addInfoKeyStatement = ih.prepare(d);
	}

	private static void intialize_updateInfoKey(CCDatabase d) throws SQLException {
		SQLUpdateHelper ih = new SQLUpdateHelper(CCDatabase.TAB_INFO, DoubleString.createPrepared(CCDatabase.TAB_INFO_COLUMN_KEY));
		
		ih.addPreparedField(CCDatabase.TAB_INFO_COLUMN_VALUE);
		
		updateInfoKeyStatement = ih.prepare(d);
	}

	private static void intialize_selectGroups(CCDatabase d) throws SQLException {
		SQLSelectHelper sh = new SQLSelectHelper(CCDatabase.TAB_GROUPS);
		
		selectGroupsStatement = sh.prepare(d);
	}

	private static void intialize_insertGroup(CCDatabase d) throws SQLException {
		SQLInsertHelper ih = new SQLInsertHelper(CCDatabase.TAB_GROUPS);
		
		ih.addPreparedField(CCDatabase.TAB_GROUPS_COLUMN_NAME);
		ih.addPreparedField(CCDatabase.TAB_GROUPS_COLUMN_ORDER);
		ih.addPreparedField(CCDatabase.TAB_GROUPS_COLUMN_COLOR);
		ih.addPreparedField(CCDatabase.TAB_GROUPS_COLUMN_SERIALIZE);
		ih.addPreparedField(CCDatabase.TAB_GROUPS_COLUMN_PARENT);
		ih.addPreparedField(CCDatabase.TAB_GROUPS_COLUMN_VISIBLE);
		
		insertGroupStatement = ih.prepare(d);
	}

	private static void intialize_updateGroup(CCDatabase d) throws SQLException {
		SQLUpdateHelper uh = new SQLUpdateHelper(CCDatabase.TAB_GROUPS, DoubleString.createPrepared(CCDatabase.TAB_GROUPS_COLUMN_NAME));

		uh.addPreparedField(CCDatabase.TAB_GROUPS_COLUMN_ORDER);
		uh.addPreparedField(CCDatabase.TAB_GROUPS_COLUMN_COLOR);
		uh.addPreparedField(CCDatabase.TAB_GROUPS_COLUMN_SERIALIZE);
		uh.addPreparedField(CCDatabase.TAB_GROUPS_COLUMN_PARENT);
		uh.addPreparedField(CCDatabase.TAB_GROUPS_COLUMN_VISIBLE);
		
		updateGroupStatement = uh.prepare(d);
	}

	private static void intialize_removeGroup(CCDatabase d) throws SQLException {
		removeGroupStatement = new SQLDeleteHelper(CCDatabase.TAB_GROUPS, DoubleString.createPrepared(CCDatabase.TAB_GROUPS_COLUMN_NAME)).prepare(d);
	}
}
