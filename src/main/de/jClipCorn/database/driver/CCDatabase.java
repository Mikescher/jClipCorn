package de.jClipCorn.database.driver;

import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.database.util.Statements;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCTime;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.helper.ApplicationHelper;

public class CCDatabase {
	public final static String TAB_MAIN            = "ELEMENTS";  //$NON-NLS-1$
	public final static String TAB_SEASONS         = "SEASONS";   //$NON-NLS-1$
	public final static String TAB_EPISODES        = "EPISODES";  //$NON-NLS-1$
	public final static String TAB_INFO            = "INFO";      //$NON-NLS-1$
	public final static String TAB_GROUPS          = "GROUPS";    //$NON-NLS-1$

	private final static String XML_NAME = "database/ClipCornSchema.xml"; //$NON-NLS-1$

	public final static String INFOKEY_DBVERSION	= "VERSION_DB";   							//$NON-NLS-1$
	public final static String INFOKEY_DATE			= "CREATION_DATE";         					//$NON-NLS-1$
	public final static String INFOKEY_TIME			= "CREATION_TIME";         					//$NON-NLS-1$
	public final static String INFOKEY_USERNAME		= "CREATION_USERNAME";     					//$NON-NLS-1$
	public final static String INFOKEY_DUUID      	= "DATABASE_UNIVERSALLY_UNIQUE_IDENTIFIER";	//$NON-NLS-1$
	public final static String INFOKEY_RAND      	= "RAND";									//$NON-NLS-1$

	public final static String[] INFOKEYS = new String[]{INFOKEY_DBVERSION, INFOKEY_DATE, INFOKEY_TIME, INFOKEY_USERNAME, INFOKEY_DUUID};
	
	public final static String TAB_INFO_COLUMN_KEY                 = "IKEY";            //$NON-NLS-1$
	public final static String TAB_INFO_COLUMN_VALUE               = "IVALUE";          //$NON-NLS-1$
	
	public final static String TAB_MAIN_COLUMN_LOCALID             = "LOCALID";         //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_NAME                = "NAME";            //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_VIEWED              = "VIEWED";          //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_VIEWEDHISTORY       = "VIEWED_HISTORY";  //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_ZYKLUS              = "ZYKLUS";          //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_ZYKLUSNUMBER        = "ZYKLUSNUMBER";    //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_QUALITY             = "QUALITY";         //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_LANGUAGE            = "LANGUAGE";        //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_GENRE               = "GENRE";           //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_LENGTH              = "LENGTH";          //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_ADDDATE             = "ADDDATE";         //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_ONLINESCORE         = "ONLINESCORE";     //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_FSK                 = "FSK";             //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_FORMAT              = "FORMAT";          //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_MOVIEYEAR           = "MOVIEYEAR";       //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_ONLINEREF           = "ONLINEREF";       //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_GROUPS              = "GROUPS";          //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_FILESIZE            = "FILESIZE";        //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_TAGS                = "TAGS";            //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_PART_1              = "PART1";           //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_PART_2              = "PART2";           //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_PART_3              = "PART3";           //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_PART_4              = "PART4";           //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_PART_5              = "PART5";           //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_PART_6              = "PART6";           //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_SCORE               = "SCORE";           //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_COVER               = "COVERNAME";       //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_TYPE                = "TYPE";            //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_SERIES_ID           = "SERIESID";        //$NON-NLS-1$

	public final static String TAB_SEASONS_COLUMN_SEASONID         = "SEASONID";        //$NON-NLS-1$
	public final static String TAB_SEASONS_COLUMN_SERIESID         = "SERIESID";        //$NON-NLS-1$
	public final static String TAB_SEASONS_COLUMN_NAME             = "NAME";            //$NON-NLS-1$
	public final static String TAB_SEASONS_COLUMN_YEAR             = "SEASONYEAR";      //$NON-NLS-1$
	public final static String TAB_SEASONS_COLUMN_COVERNAME        = "COVERNAME";       //$NON-NLS-1$

	public final static String TAB_EPISODES_COLUMN_LOCALID         = "LOCALID";         //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_SEASONID        = "SEASONID";        //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_EPISODE         = "EPISODE";         //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_NAME            = "NAME";            //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_VIEWED          = "VIEWED";          //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_VIEWEDHISTORY   = "VIEWED_HISTORY";  //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_QUALITY         = "QUALITY";         //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_LENGTH          = "LENGTH";          //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_FORMAT          = "FORMAT";          //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_FILESIZE        = "FILESIZE";        //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_PART_1          = "PART1";           //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_TAGS            = "TAGS";            //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_ADDDATE         = "ADDDATE";         //$NON-NLS-1$

	public final static String TAB_GROUPS_COLUMN_NAME              = "NAME";            //$NON-NLS-1$
	public final static String TAB_GROUPS_COLUMN_ORDER             = "ORDERING";        //$NON-NLS-1$
	public final static String TAB_GROUPS_COLUMN_COLOR             = "COLOR";           //$NON-NLS-1$
	public final static String TAB_GROUPS_COLUMN_SERIALIZE         = "SERIALIZE";       //$NON-NLS-1$
	
	private String databasePath;
	private GenericDatabase db;
		
	public final DatabaseUpgradeAssistant upgrader;
	
	private CCDatabase(CCDatabaseDriver driver) {
		super();
		
		switch (driver) {
		case DERBY:
			db = new DerbyDatabase();
			break;
		case SQLITE:
			db = new SQLiteDatabase();
			break;
		case STUB:
			db = new StubDatabase();
			break;
		case INMEMORY:
			db = new MemoryDatabase();
			break;
		}
		
		upgrader = new DatabaseUpgradeAssistant(db);
	}
	
	public static CCDatabase create() {
		return new CCDatabase(CCProperties.getInstance().PROP_DATABASE_DRIVER.getValue());
	}
	
	public static CCDatabase createStub() {
		return new CCDatabase(CCDatabaseDriver.STUB);
	}
	
	public static CCDatabase createInMemory() {
		return new CCDatabase(CCDatabaseDriver.INMEMORY);
	}
	
	public boolean exists(String path) {
		return db.databaseExists(path);
	}

	public DatabaseConnectResult tryconnect(String path) {
		if (db.databaseExists(path)) {
			if (connect(path)) {
				CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.DBConnect", path)); //$NON-NLS-1$
				
				return DatabaseConnectResult.SUCESS_CONNECTED;
			} else {
				CCLog.addDebug("Cannot connect because of reason:\n" + db.getLastError()); //$NON-NLS-1$
				
				return DatabaseConnectResult.ERROR_CANTCONNECT;
			}
		} else {
			if (create(path)) {
				CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.DBCreated", path)); //$NON-NLS-1$
				
				return DatabaseConnectResult.SUCCESS_CREATED;
			} else {
				CCLog.addDebug("Cannot create because of reason:\n" + db.getLastError()); //$NON-NLS-1$
				
				return DatabaseConnectResult.ERROR_CANTCREATE;
			}
		}
	}

	private boolean connect(String dbpath) {
		try {
			if (! db.databaseExists(dbpath)) return false;
			
			db.establishDBConnection(dbpath);
			databasePath = dbpath;
			
			upgrader.tryUpgrade();
			
			Statements.intialize(this);
			return true;
		} catch (SQLException e) {
			db.setLastError(e);
			
			Exception next = e.getNextException();
			if (next != null) db.setLastError(e);
			
			return false;
		} catch (Exception e) {
			db.setLastError(e);
			return false;
		}
	}
	
	public void disconnect(boolean cleanshutdown) {
		try {
			if (db.isConnected()) {
				shutdownStatements();
				db.closeDBConnection(getDBPath(), cleanshutdown);
			}
		} catch (SQLException e) {
			CCLog.addError(LocaleBundle.getString("LogMessage.CouldNotDisconnectFromDB"), e); //$NON-NLS-1$
		}
	}
	
	public void reconnect() {
		if (! connect(getDBPath())) {
			CCLog.addFatalError(LocaleBundle.getString("LogMessage.CouldNotReconnectToDB"), db.getLastError()); //$NON-NLS-1$
		}
	}

	private boolean create(String dbpath) {
		boolean res = db.createNewDatabasefromResourceXML('/' + XML_NAME, dbpath);
		if (res) {
			databasePath = dbpath;
			Statements.intialize(this);

			writeNewInformationToDB(INFOKEY_DBVERSION, Main.DBVERSION);
			writeNewInformationToDB(INFOKEY_DATE, CCDate.getCurrentDate().toStringSQL());
			writeNewInformationToDB(INFOKEY_TIME, CCTime.getCurrentTime().toStringSQL());
			writeNewInformationToDB(INFOKEY_USERNAME, ApplicationHelper.getCurrentUsername());
		}
		return res;
	}

	/**
	 * @return the ammount of Movies and Series in the Database
	 */
	public int getDBElementCount() {
		return db.getRowCount(TAB_MAIN);
	}

	private CCDatabaseElement createDatabaseElementFromDatabase(ResultSet rs, CCMovieList ml) throws SQLException, CCFormatException {
		if (rs.getInt(TAB_MAIN_COLUMN_TYPE) == CCMovieTyp.MOVIE.asInt()) {
			CCMovie mov = new CCMovie(ml, rs.getInt(TAB_MAIN_COLUMN_LOCALID));

			mov.beginUpdating();

			updateMovieFromResultSet(rs, mov);

			mov.abortUpdating();

			return mov;
		} else {
			int lid = rs.getInt(TAB_MAIN_COLUMN_LOCALID);
			int sid = rs.getInt(TAB_MAIN_COLUMN_SERIES_ID);
			CCSeries ser = new CCSeries(ml, lid, sid);

			ser.beginUpdating();

			updateSeriesFromResultSet(rs, ser);

			ser.abortUpdating();

			fillSeries(ser);

			return ser;
		}
	}

	private CCSeason createSeasonFromDatabase(ResultSet rs, CCSeries ser) throws SQLException {
		CCSeason seas = new CCSeason(ser, rs.getInt(TAB_SEASONS_COLUMN_SEASONID));

		seas.beginUpdating();

		updateSeasonFromResultSet(rs, seas);

		seas.abortUpdating();

		fillSeason(seas);

		return seas;
	}

	private CCEpisode createEpisodeFromDatabase(ResultSet rs, CCSeason se) throws SQLException, CCFormatException {
		CCEpisode ep = new CCEpisode(se, rs.getInt(TAB_EPISODES_COLUMN_LOCALID));

		ep.beginUpdating();

		updateEpisodeFromResultSet(rs, ep);

		ep.abortUpdating();

		return ep;
	}

	private void updateEpisodeFromResultSet(ResultSet rs, CCEpisode ep) throws SQLException, CCFormatException {
		ep.setEpisodeNumber(rs.getInt(TAB_EPISODES_COLUMN_EPISODE));
		ep.setTitle(rs.getString(TAB_EPISODES_COLUMN_NAME));
		ep.setViewed(rs.getBoolean(TAB_EPISODES_COLUMN_VIEWED));
		ep.setViewedHistory(rs.getString(TAB_EPISODES_COLUMN_VIEWEDHISTORY));
		ep.setQuality(rs.getInt(TAB_EPISODES_COLUMN_QUALITY));
		ep.setLength(rs.getInt(TAB_EPISODES_COLUMN_LENGTH));
		ep.setFormat(rs.getInt(TAB_EPISODES_COLUMN_FORMAT));
		ep.setFilesize(rs.getLong(TAB_EPISODES_COLUMN_FILESIZE));
		ep.setPart(rs.getString(TAB_EPISODES_COLUMN_PART_1));
		ep.setAddDate(getDateFromResultSet(rs, TAB_EPISODES_COLUMN_ADDDATE));
		ep.setTags(rs.getShort(TAB_EPISODES_COLUMN_TAGS));
	}
	
	private void updateSeasonFromResultSet(ResultSet rs, CCSeason seas) throws SQLException {
		seas.setTitle(rs.getString(TAB_SEASONS_COLUMN_NAME));
		seas.setYear(rs.getInt(TAB_SEASONS_COLUMN_YEAR));
		seas.setCover(rs.getString(TAB_SEASONS_COLUMN_COVERNAME));
	}
	
	private void updateSeriesFromResultSet(ResultSet rs, CCSeries ser) throws SQLException, CCFormatException {
		ser.setTitle(rs.getString(TAB_MAIN_COLUMN_NAME));
		ser.setLanguage(rs.getInt(TAB_MAIN_COLUMN_LANGUAGE));
		ser.setGenres(rs.getLong(TAB_MAIN_COLUMN_GENRE));
		ser.setOnlinescore(rs.getInt(TAB_MAIN_COLUMN_ONLINESCORE));
		ser.setFsk(rs.getInt(TAB_MAIN_COLUMN_FSK));
		ser.setScore(rs.getInt(TAB_MAIN_COLUMN_SCORE));
		ser.setCover(rs.getString(TAB_MAIN_COLUMN_COVER));
		ser.setOnlineReference(rs.getString(TAB_MAIN_COLUMN_ONLINEREF));
		ser.setGroups(rs.getString(TAB_MAIN_COLUMN_GROUPS));
	}

	private void updateMovieFromResultSet(ResultSet rs, CCMovie mov) throws SQLException, CCFormatException {
		mov.setTitle(rs.getString(TAB_MAIN_COLUMN_NAME));
		mov.setViewed(rs.getBoolean(TAB_MAIN_COLUMN_VIEWED));
		mov.setViewedHistory(rs.getString(TAB_MAIN_COLUMN_VIEWEDHISTORY));
		mov.setZyklusTitle(rs.getString(TAB_MAIN_COLUMN_ZYKLUS));
		mov.setZyklusID(rs.getInt(TAB_MAIN_COLUMN_ZYKLUSNUMBER));
		mov.setQuality(rs.getInt(TAB_MAIN_COLUMN_QUALITY));
		mov.setLanguage(rs.getInt(TAB_MAIN_COLUMN_LANGUAGE));
		mov.setGenres(rs.getLong(TAB_MAIN_COLUMN_GENRE));
		mov.setLength(rs.getInt(TAB_MAIN_COLUMN_LENGTH));
		mov.setAddDate(getDateFromResultSet(rs, TAB_MAIN_COLUMN_ADDDATE));
		mov.setOnlinescore(rs.getInt(TAB_MAIN_COLUMN_ONLINESCORE));
		mov.setFsk(rs.getInt(TAB_MAIN_COLUMN_FSK));
		mov.setFormat(rs.getInt(TAB_MAIN_COLUMN_FORMAT));
		mov.setYear(rs.getInt(TAB_MAIN_COLUMN_MOVIEYEAR));
		mov.setOnlineReference(rs.getString(TAB_MAIN_COLUMN_ONLINEREF));
		mov.setGroups(rs.getString(TAB_MAIN_COLUMN_GROUPS));
		mov.setFilesize(rs.getLong(TAB_MAIN_COLUMN_FILESIZE));
		mov.setTags(rs.getShort(TAB_MAIN_COLUMN_TAGS));
		mov.setPart(0, rs.getString(TAB_MAIN_COLUMN_PART_1));
		mov.setPart(1, rs.getString(TAB_MAIN_COLUMN_PART_2));
		mov.setPart(2, rs.getString(TAB_MAIN_COLUMN_PART_3));
		mov.setPart(3, rs.getString(TAB_MAIN_COLUMN_PART_4));
		mov.setPart(4, rs.getString(TAB_MAIN_COLUMN_PART_5));
		mov.setPart(5, rs.getString(TAB_MAIN_COLUMN_PART_6));
		mov.setScore(rs.getInt(TAB_MAIN_COLUMN_SCORE));
		mov.setCover(rs.getString(TAB_MAIN_COLUMN_COVER));
	}
	
	private CCDate getDateFromResultSet(ResultSet rs, String columnName) throws SQLException, CCFormatException {
		if (db.supportsDateType()) {
			return CCDate.create(rs.getDate(TAB_MAIN_COLUMN_ADDDATE));
		} else {
			return CCDate.createFromSQL(rs.getString(TAB_MAIN_COLUMN_ADDDATE));
		}
	}

	private int getIntFromSet(ResultSet rs) throws SQLException {
		int result = 0;

		rs.next();
		result = rs.getInt(1);

		return result;
	}

	private int getNewLocalID() {
		try {
			return getIntFromSet(Statements.newLocalIDStatement.executeQuery()) + 1;
		} catch (SQLException e) {
			CCLog.addError(LocaleBundle.getString("LogMessage.NoNewMovieID"), e); //$NON-NLS-1$
			return -1;
		}
	}

	private int getNewSeriesID() {
		try {
			return getIntFromSet(Statements.newSeriesIDStatement.executeQuery()) + 1;
		} catch (SQLException e) {
			CCLog.addError(LocaleBundle.getString("LogMessage.NoNewSeriesID"), e); //$NON-NLS-1$
			return -1;
		}
	}

	private int getNewSeasonID() {
		try {
			return getIntFromSet(Statements.newSeasonIDStatement.executeQuery()) + 1;
		} catch (SQLException e) {
			CCLog.addError(LocaleBundle.getString("LogMessage.NoNewSeasonID"), e); //$NON-NLS-1$
			return -1;
		}
	}
	
	private int getNewEpisodeID() {
		try {
			return getIntFromSet(Statements.newEpisodeIDStatement.executeQuery()) + 1;
		} catch (SQLException e) {
			CCLog.addError(LocaleBundle.getString("LogMessage.NoNewEpisodeID"), e); //$NON-NLS-1$
			return -1;
		}
	}

	@SuppressWarnings("nls")
	private boolean addEmptyMainRow(int id) {
		try {
			PreparedStatement s = Statements.addEmptyMainTabStatement;
			s.clearParameters();

			s.setInt(1, id);                              // 01   TAB_MAIN_COLUMN_LOCALID               
			s.setString(2, "");                           // 02   TAB_MAIN_COLUMN_NAME                  
			s.setInt(3, 0);                               // 03   TAB_MAIN_COLUMN_VIEWED                
			s.setString(4, "");                           // 04   TAB_MAIN_COLUMN_VIEWEDHISTORY         
			s.setString(5, "");                           // 05   TAB_MAIN_COLUMN_ZYKLUS                
			s.setInt(6, 0);                               // 06   TAB_MAIN_COLUMN_ZYKLUSNUMBER          
			s.setInt(7, 0);                               // 07   TAB_MAIN_COLUMN_QUALITY               
			s.setInt(8, 0);                               // 08   TAB_MAIN_COLUMN_LANGUAGE              
			s.setInt(9, 0);                               // 09   TAB_MAIN_COLUMN_GENRE                 
			s.setInt(10, 0);                              // 10   TAB_MAIN_COLUMN_LENGTH                
			s.setString(11, CCDate.MIN_SQL);              // 11   TAB_MAIN_COLUMN_ADDDATE               
			s.setInt(12, 0);                              // 12   TAB_MAIN_COLUMN_ONLINESCORE           
			s.setInt(13, 0);                              // 13   TAB_MAIN_COLUMN_FSK                   
			s.setInt(14, 0);                              // 14   TAB_MAIN_COLUMN_FORMAT                
			s.setInt(15, 0);                              // 15   TAB_MAIN_COLUMN_MOVIEYEAR             
			s.setString(16, "");                          // 16   TAB_MAIN_COLUMN_ONLINEREF             
			s.setString(17, "");                          // 17   TAB_MAIN_COLUMN_GROUPS                
			s.setInt(18, 0);                              // 18   TAB_MAIN_COLUMN_FILESIZE              
			s.setInt(19, 0);                              // 19   TAB_MAIN_COLUMN_TAGS                  
			s.setString(20, "");                          // 20   TAB_MAIN_COLUMN_PART_1                
			s.setString(21, "");                          // 21   TAB_MAIN_COLUMN_PART_2                
			s.setString(22, "");                          // 22   TAB_MAIN_COLUMN_PART_3                
			s.setString(23, "");                          // 23   TAB_MAIN_COLUMN_PART_4                
			s.setString(24, "");                          // 24   TAB_MAIN_COLUMN_PART_5                
			s.setString(25, "");                          // 25   TAB_MAIN_COLUMN_PART_6                
			s.setInt(26, 0);                              // 26   TAB_MAIN_COLUMN_SCORE                 
			s.setString(27, "");                          // 27   TAB_MAIN_COLUMN_COVER                 
			s.setInt(28, CCMovieTyp.MOVIE.asInt());       // 28   TAB_MAIN_COLUMN_TYPE                  
			s.setInt(29, -1);                             // 29   TAB_MAIN_COLUMN_SERIES_ID             

			s.executeUpdate();

			return true;
		} catch (SQLException e) {
			db.setLastError(e);
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.NoNewRow", id), e); //$NON-NLS-1$
			return false;
		}
	}

	@SuppressWarnings("nls")
	private boolean addEmptySeasonsRow(int seasid, int serid) {
		try {
			PreparedStatement s = Statements.addEmptySeasonTabStatement;
			s.clearParameters();

			s.setInt(1, seasid);
			s.setInt(2, serid);
			s.setString(3, "");
			s.setInt(4, 0);
			s.setString(5, "");

			s.executeUpdate();

			return true;
		} catch (SQLException e) {
			db.setLastError(e);
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.NoNewSeasonRow", seasid, serid), e); //$NON-NLS-1$
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	private boolean addEmptyEpisodeRow(int eid, int sid) {
		try {
			PreparedStatement s = Statements.addEmptyEpisodeTabStatement;
			s.clearParameters();

			s.setInt(1, eid);                            // 01   TAB_EPISODES_COLUMN_LOCALID        
			s.setInt(2, sid);                            // 02   TAB_EPISODES_COLUMN_SEASONID       
			s.setInt(3, 0);                              // 03   TAB_EPISODES_COLUMN_EPISODE        
			s.setBoolean(4, false);                      // 04   TAB_EPISODES_COLUMN_NAME           
			s.setString(5, "");                          // 05   TAB_EPISODES_COLUMN_VIEWED         
			s.setString(6, "");                          // 06   TAB_EPISODES_COLUMN_VIEWEDHISTORY  
			s.setInt(7, 0);                              // 07   TAB_EPISODES_COLUMN_QUALITY        
			s.setInt(8, 0);                              // 08   TAB_EPISODES_COLUMN_LENGTH         
			s.setInt(9, 0);                              // 09   TAB_EPISODES_COLUMN_FORMAT         
			s.setInt(10, 0);                             // 10   TAB_EPISODES_COLUMN_FILESIZE       
			s.setString(11, "");                         // 11   TAB_EPISODES_COLUMN_PART_1         
			s.setInt(12, 0);                             // 12   TAB_EPISODES_COLUMN_TAGS           
			s.setString(13, CCDate.MIN_SQL);             // 13   TAB_EPISODES_COLUMN_ADDDATE        

			s.executeUpdate();

			return true;
		} catch (SQLException e) {
			db.setLastError(e);
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.NoNewEpisodeRow", eid, sid), e); //$NON-NLS-1$
			return false;
		}
	}

	public CCMovie createNewEmptyMovie(CCMovieList list) {
		int nlid = getNewLocalID();

		if (nlid == -1) {
			return null;
		}

		if (! addEmptyMainRow(nlid)) {
			return null;
		}

		CCMovie result = new CCMovie(list, nlid);
		result.setDefaultValues(false);

		return result;
	}

	public CCSeries createNewEmptySeries(CCMovieList list) {
		int nlid = getNewLocalID();
		int sid = getNewSeriesID();

		if (nlid == -1) {
			return null;
		}

		if (! addEmptyMainRow(nlid)) {
			return null;
		}

		CCSeries result = new CCSeries(list, nlid, sid);
		result.setDefaultValues(false);

		return result;
	}

	public CCSeason createNewEmptySeason(CCSeries s) {
		int sid = getNewSeasonID();

		if (sid == -1) {
			return null;
		}

		if (! addEmptySeasonsRow(sid, s.getSeriesID())) {
			return null;
		}

		CCSeason result = new CCSeason(s, sid);
		result.setDefaultValues(false);

		return result;
	}

	public CCEpisode createNewEmptyEpisode(CCSeason s) {
		int eid = getNewEpisodeID();

		if (eid == -1) {
			return null;
		}

		if (! addEmptyEpisodeRow(eid, s.getSeasonID())) {
			return null;
		}

		CCEpisode result = new CCEpisode(s, eid);
		result.setDefaultValues(false);

		return result;
	}

	@SuppressWarnings("nls")
	public boolean updateMovieInDatabase(CCMovie mov) {
		try {
			PreparedStatement s = Statements.updateMainTabStatement;
			s.clearParameters();

			s.setString(1, mov.getTitle());                                    // TAB_MAIN_COLUMN_LOCALID
			s.setBoolean(2, mov.isViewed());                                   // TAB_MAIN_COLUMN_NAME
			s.setString(3, mov.getViewedHistory().toSerializationString());    // TAB_MAIN_COLUMN_VIEWED
			s.setString(4, mov.getZyklus().getTitle());                        // TAB_MAIN_COLUMN_VIEWEDHISTORY
			s.setInt(5, mov.getZyklus().getNumber());                          // TAB_MAIN_COLUMN_ZYKLUS
			s.setInt(6, mov.getQuality().asInt());                             // TAB_MAIN_COLUMN_ZYKLUSNUMBER
			s.setInt(7, mov.getLanguage().asInt());                            // TAB_MAIN_COLUMN_QUALITY
			s.setLong(8, mov.getGenres().getAllGenres());                      // TAB_MAIN_COLUMN_LANGUAGE
			s.setInt(9, mov.getLength());                                      // TAB_MAIN_COLUMN_GENRE
			s.setString(10, mov.getAddDate().toStringSQL());    			   // TAB_MAIN_COLUMN_LENGTH
			s.setInt(11, mov.getOnlinescore().asInt());                        // TAB_MAIN_COLUMN_ADDDATE
			s.setInt(12, mov.getFSK().asInt());                                // TAB_MAIN_COLUMN_ONLINESCORE
			s.setInt(13, mov.getFormat().asInt());                             // TAB_MAIN_COLUMN_FSK
			s.setInt(14, mov.getYear());                                       // TAB_MAIN_COLUMN_FORMAT
			s.setString(15, mov.getOnlineReference().toSerializationString()); // TAB_MAIN_COLUMN_MOVIEYEAR
			s.setString(16, mov.getGroups().toSerializationString());          // TAB_MAIN_COLUMN_ONLINEREF
			s.setLong(17, mov.getFilesize().getBytes());                       // TAB_MAIN_COLUMN_GROUPS
			s.setShort(18, mov.getTags().asShort());                           // TAB_MAIN_COLUMN_FILESIZE
			s.setString(19, mov.getPart(0));                                   // TAB_MAIN_COLUMN_TAGS
			s.setString(20, mov.getPart(1));                                   // TAB_MAIN_COLUMN_PART_1
			s.setString(21, mov.getPart(2));                                   // TAB_MAIN_COLUMN_PART_2
			s.setString(22, mov.getPart(3));                                   // TAB_MAIN_COLUMN_PART_3
			s.setString(23, mov.getPart(4));                                   // TAB_MAIN_COLUMN_PART_4
			s.setString(24, mov.getPart(5));                                   // TAB_MAIN_COLUMN_PART_5
			s.setInt(25, mov.getScore().asInt());                              // TAB_MAIN_COLUMN_PART_6
			s.setString(26, mov.getCoverName());                               // TAB_MAIN_COLUMN_SCORE
			s.setInt(27, mov.getType().asInt());                               // TAB_MAIN_COLUMN_COVER
			s.setInt(28, mov.getSeriesID());                                   // TAB_MAIN_COLUMN_TYPE
			
			s.setInt(29, mov.getLocalID());                                    // TAB_MAIN_COLUMN_SERIES_ID

			s.execute();

			return true;
		} catch (SQLException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateMovie", mov.getTitle(), mov.getLocalID()), e);
			return false;
		}
	}

	@SuppressWarnings("nls")
	public boolean updateSeriesInDatabase(CCSeries ser) {
		try {
			PreparedStatement s = Statements.updateSeriesTabStatement;
			s.clearParameters();

			s.setString(1, ser.getTitle());                                     // 01     TAB_MAIN_COLUMN_NAME        
			s.setInt(2, ser.getLanguage().asInt());                             // 02     TAB_MAIN_COLUMN_LANGUAGE    
			s.setLong(3, ser.getGenres().getAllGenres());                       // 03     TAB_MAIN_COLUMN_GENRE       
			s.setInt(4, ser.getOnlinescore().asInt());                          // 04     TAB_MAIN_COLUMN_ONLINESCORE 
			s.setInt(5, ser.getFSK().asInt());                                  // 05     TAB_MAIN_COLUMN_FSK         
			s.setString(6, ser.getOnlineReference().toSerializationString());   // 06     TAB_MAIN_COLUMN_ONLINEREF   
			s.setString(7, ser.getGroups().toSerializationString());            // 07     TAB_MAIN_COLUMN_GROUPS      
			s.setInt(8, ser.getScore().asInt());                                // 08     TAB_MAIN_COLUMN_SCORE       
			s.setString(9, ser.getCoverName());                                 // 09     TAB_MAIN_COLUMN_COVER       
			s.setInt(10, ser.getType().asInt());                                // 10     TAB_MAIN_COLUMN_TYPE        
			s.setInt(11, ser.getSeriesID());                                    // 11     TAB_MAIN_COLUMN_SERIES_ID   

			s.setInt(12, ser.getLocalID());

			s.executeUpdate();

			return true;
		} catch (SQLException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateSeries", ser.getTitle(), ser.getLocalID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateSeasonInDatabase(CCSeason ser) {
		try {
			PreparedStatement s = Statements.updateSeasonTabStatement;
			s.clearParameters();

			s.setInt(1, ser.getSeries().getSeriesID());
			s.setString(2, ser.getTitle());
			s.setInt(3, ser.getYear());
			s.setString(4, ser.getCoverName());

			s.setInt(5, ser.getSeasonID());

			s.executeUpdate();
			return true;
		} catch (SQLException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateSeason", ser.getTitle(), ser.getSeasonID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateEpisodeInDatabase(CCEpisode ep) {
		try {
			PreparedStatement s = Statements.updateEpisodeTabStatement;
			s.clearParameters();

			s.setInt(1, ep.getSeason().getSeasonID());                       // TAB_EPISODES_COLUMN_SEASONID
			s.setInt(2, ep.getEpisodeNumber());                              // TAB_EPISODES_COLUMN_EPISODE
			s.setString(3, ep.getTitle());                                   // TAB_EPISODES_COLUMN_NAME
			s.setBoolean(4, ep.isViewed());                                  // TAB_EPISODES_COLUMN_VIEWED
			s.setString(5, ep.getViewedHistory().toSerializationString());   // TAB_EPISODES_COLUMN_VIEWEDHISTORY
			s.setInt(6, ep.getQuality().asInt());                            // TAB_EPISODES_COLUMN_QUALITY
			s.setInt(7, ep.getLength());                                     // TAB_EPISODES_COLUMN_LENGTH
			s.setInt(8, ep.getFormat().asInt());                             // TAB_EPISODES_COLUMN_FORMAT
			s.setLong(9, ep.getFilesize().getBytes());                       // TAB_EPISODES_COLUMN_FILESIZE
			s.setString(10, ep.getPart());                                   // TAB_EPISODES_COLUMN_PART_1
			s.setShort(11, ep.getTags().asShort());                          // TAB_EPISODES_COLUMN_TAGS
			s.setString(12, ep.getAddDate().toStringSQL());					 // TAB_EPISODES_COLUMN_ADDDATE

			s.setInt(13, ep.getLocalID());

			s.execute();

			return true;
		} catch (SQLException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateEpisode", ep.getTitle(), ep.getLocalID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateMovieFromDatabase(CCMovie mov) {
		try {
			PreparedStatement s = Statements.selectSingleMainTabStatement;
			s.clearParameters();
			s.setInt(1, mov.getLocalID());
			ResultSet rs = s.executeQuery();
		
			if (rs.next()) { 
				updateMovieFromResultSet(rs, mov);
			}
			
			rs.close();
			return true;
		} catch (SQLException | CCFormatException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateMovie", mov.getTitle(), mov.getLocalID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateSeriesFromDatabase(CCSeries ser) {
		try {
			PreparedStatement s = Statements.selectSingleMainTabStatement;
			s.clearParameters();
			s.setInt(1, ser.getLocalID());
			ResultSet rs = s.executeQuery();
		
			if (rs.next()) { 
				updateSeriesFromResultSet(rs, ser);
			}
			
			rs.close();
			return true;
		} catch (SQLException | CCFormatException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateSeries", ser.getTitle(), ser.getLocalID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateSeasonFromDatabase(CCSeason sea) {
		try {
			PreparedStatement s = Statements.selectSingleSeasonTabStatement;
			s.clearParameters();
			s.setInt(1, sea.getSeasonID());
			ResultSet rs = s.executeQuery();
		
			if (rs.next()) { 
				updateSeasonFromResultSet(rs, sea);
			}
			
			rs.close();
			return true;
		} catch (SQLException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateSeason", sea.getTitle(), sea.getSeasonID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateEpisodeFromDatabase(CCEpisode epi) {
		try {
			PreparedStatement s = Statements.selectSingleSeasonTabStatement;
			s.clearParameters();
			s.setInt(1, epi.getLocalID());
			ResultSet rs = s.executeQuery();
		
			if (rs.next()) { 
				updateEpisodeFromResultSet(rs, epi);
			}
			
			rs.close();
			return true;
		} catch (SQLException | CCFormatException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateEpisode", epi.getTitle(), epi.getLocalID()), e);
			return false;
		}
	}

	public void fillMovieList(CCMovieList ml) {
		try {
			{
				PreparedStatement s = Statements.selectGroupsStatement;
				s.clearParameters();
				
				ResultSet rs = s.executeQuery();
				
				while (rs.next()) {
					ml.directlyAddGroup(CCGroup.create(rs.getString(TAB_GROUPS_COLUMN_NAME), rs.getInt(TAB_GROUPS_COLUMN_ORDER), rs.getInt(TAB_GROUPS_COLUMN_COLOR), rs.getBoolean(TAB_GROUPS_COLUMN_SERIALIZE)));
				}
				
				rs.close();
			}
			
			{
				PreparedStatement s = Statements.selectMainTabStatement;
				s.clearParameters();
				
				ResultSet rs = s.executeQuery();
				
				if (CCProperties.getInstance().PROP_LOADING_LIVEUPDATE.getValue()) {
					while (rs.next()) {
						ml.directlyInsert(createDatabaseElementFromDatabase(rs, ml));
					}
				} else {
					List<CCDatabaseElement> temp = new ArrayList<>();
					while (rs.next()) {
						temp.add(createDatabaseElementFromDatabase(rs, ml));
					}
					ml.directlyInsert(temp);
				}
				
				rs.close();
			}
		} catch (SQLException | CCFormatException e) {
			CCLog.addError(e);
		}
	}

	public void fillSeries(CCSeries ser) {
		try {
			PreparedStatement s = Statements.selectSeasonTabStatement;
			s.clearParameters();
			
			s.setInt(1, ser.getSeriesID());
			
			ResultSet rs = s.executeQuery();

			ser.beginUpdating();

			while (rs.next()) {
				ser.directlyInsertSeason(createSeasonFromDatabase(rs, ser));
			}

			ser.abortUpdating();
			
			rs.close();
			
		} catch (SQLException e) {
			CCLog.addError(e);
		}
	}

	public void fillSeason(CCSeason se) {
		try {
			PreparedStatement s = Statements.selectEpisodeTabStatement;
			s.clearParameters();
			
			s.setInt(1, se.getSeasonID());
			
			ResultSet rs = s.executeQuery();

			se.beginUpdating();

			while (rs.next()) {
				se.directlyInsertEpisode(createEpisodeFromDatabase(rs, se));
			}

			se.abortUpdating();
			
			rs.close();
			
		} catch (SQLException | CCFormatException e) {
			CCLog.addError(e);
		}
	}

	public String getDBPath() {
		return databasePath;
	}

	public void removeFromMain(int localID) {	
		try {
			PreparedStatement s = Statements.deleteMainTabStatement;
			s.clearParameters();
			
			s.setInt(1, localID);
			
			s.executeUpdate();
		} catch (SQLException e) {
			CCLog.addError(e);
		}
	}
	
	public void removeFromSeasons(int seasonID) {
		try {
			PreparedStatement s = Statements.deleteSeasonTabStatement;
			s.clearParameters();
			
			s.setInt(1, seasonID);
			
			s.executeUpdate();
		} catch (SQLException e) {
			CCLog.addError(e);
		}
	}
	
	public void removeFromEpisodes(int localID) {
		try {
			PreparedStatement s = Statements.deleteEpisodeTabStatement;
			s.clearParameters();
			
			s.setInt(1, localID);
			
			s.executeUpdate();
		} catch (SQLException e) {
			CCLog.addError(e);
		}
	}
	
	public String getInformation_DBVersion() {
		return getInformationFromDB(INFOKEY_DBVERSION);
	}
	
	public CCDate getInformation_CreationDate() {
		try {
			return CCDate.createFromSQL(getInformationFromDB(INFOKEY_DATE));
		} catch (CCFormatException e) {
			CCLog.addError(e);
			return CCDate.getMinimumDate();
		}
	}
	
	public CCTime getInformation_CreationTime() {
		try {
			return CCTime.createFromSQL(getInformationFromDB(INFOKEY_TIME));
		} catch (CCFormatException e) {
			CCLog.addError(e);
			return CCTime.getMidnight();
		}
	}
	
	public String getInformation_CreationUsername() {
		return getInformationFromDB(INFOKEY_USERNAME);
	}
	
	public String getInformation_DUUID() {
		if (!hasInformationInDB(INFOKEY_DUUID)) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.RegenerateDUUID")); //$NON-NLS-1$
			
			writeNewInformationToDB(INFOKEY_DUUID, UUID.randomUUID().toString());
		}
		
		return getInformationFromDB(INFOKEY_DUUID);
	}

	public void resetInformation_DUUID() {
		updateInformationInDB(INFOKEY_DUUID, UUID.randomUUID().toString());
	}
	
	public String getInformationFromDB(String key) {
		try {
			String value;
			
			PreparedStatement s = Statements.selectInfoKeyStatement;
			s.clearParameters();
			s.setString(1, key);
			
			ResultSet rs = s.executeQuery();
			if (rs.next()) 
				value = rs.getString(1);
			else 
				value = ""; //$NON-NLS-1$

			rs.close();
			
			return value;
			
		} catch (SQLException e) {
			CCLog.addError(e);
			return null;
		}
	}
	
	private boolean hasInformationInDB(String key) {
		try {
			boolean value;
			
			PreparedStatement s = Statements.selectInfoKeyStatement;
			s.clearParameters();
			s.setString(1, key);
			
			ResultSet rs = s.executeQuery();
			value = rs.next();

			rs.close();
			
			return value;
			
		} catch (SQLException e) {
			CCLog.addError(e);
			return false;
		}
	}
	
	private void writeNewInformationToDB(String key, String value) {
		try {
			PreparedStatement s = Statements.addInfoKeyStatement;
			s.clearParameters();

			s.setString(1, key);
			s.setString(2, value);
			
			s.executeUpdate();
		} catch (SQLException e) {
			CCLog.addError(e);
		}
	}
	
	private void updateInformationInDB(String key, String value) {
		try {
			PreparedStatement s = Statements.updateInfoKeyStatement;
			s.clearParameters();

			s.setString(1, value);
			s.setString(2, key);
			
			s.executeUpdate();
		} catch (SQLException e) {
			CCLog.addError(e);
		}
	}
	
	public void removeGroup(String name) {
		try {
			PreparedStatement s = Statements.removeGroupStatement;
			s.clearParameters();

			s.setString(1, name);
			
			s.executeUpdate();
		} catch (SQLException e) {
			CCLog.addError(e);
		}
	}

	public void addGroup(String name, int order, Color color, boolean doSerialize) {
		try {
			PreparedStatement s = Statements.insertGroupStatement;
			s.clearParameters();

			s.setString(1, name);
			s.setInt(2, order);
			s.setInt(3, color.getRGB());
			s.setBoolean(4, doSerialize);
			
			s.executeUpdate();
		} catch (SQLException e) {
			CCLog.addError(e);
		}
	}

	public void updateGroup(String name, int order, Color color, boolean doSerialize) {
		try {
			PreparedStatement s = Statements.updateGroupStatement;
			s.clearParameters();

			s.setInt(1, order);
			s.setInt(2, color.getRGB());
			s.setBoolean(3, doSerialize);
			
			s.setString(4, name);
			
			s.executeUpdate();
		} catch (SQLException e) {
			CCLog.addError(e);
		}
	}

	public void clearGroups() {
		try {
			db.executeSQLThrow("DELETE FROM " + TAB_GROUPS); //$NON-NLS-1$
		} catch (SQLException e) {
			CCLog.addError(e);
		}
	}

	private void shutdownStatements() {
		Statements.shutdown();
	}

	public Exception getLastError() {
		return db.getLastError();
	}

	public PreparedStatement createPreparedStatement(String sql) throws SQLException {
		return db.createPreparedStatement(sql);
	}

	public String GetDBTypeName() {
		return db.GetDBTypeName();
	}

	public boolean IsInMemory() {
		return db.IsInMemory();
	}
}
