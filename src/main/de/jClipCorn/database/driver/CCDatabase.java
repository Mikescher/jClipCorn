package de.jClipCorn.database.driver;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.covertab.*;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBElementTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.database.migration.DatabaseMigration;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.datetime.CCTime;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.sqlwrapper.*;

import java.awt.*;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.*;

import static de.jClipCorn.database.driver.DatabaseStructure.*;
import static de.jClipCorn.database.driver.Statements.*;

public class CCDatabase {

	public final static String XML_NAME = "database/ClipCornSchema.xml"; //$NON-NLS-1$

	private final String databaseDirectory; // = most of the time the working directory
	private final String databaseName;      // = the PROP_DATABASE_NAME

	private final GenericDatabase db;
	public  final DatabaseMigration upgrader;
	private final ICoverCache coverCache;
	private final CCDatabaseHistory _history;

	private final boolean _readonly;

	private CCDatabase(CCDatabaseDriver driver, String dbDir, String dbName, boolean readonly) {
		super();

		_readonly = readonly;

		databaseDirectory = dbDir;
		databaseName = dbName;

		if (driver == null) driver = autoDetermineDriver(dbDir, dbName);

		switch (driver) {
		case DERBY:
			db = new DerbyDatabase(readonly);
			coverCache = new CCDefaultCoverCache(this);
			break;
		case SQLITE:
			db = new SQLiteDatabase(readonly);
			coverCache = new CCDefaultCoverCache(this);
			break;
		case STUB:
			db = new StubDatabase();
			coverCache = new CCStubCoverCache();
			break;
		case INMEMORY:
			db = new MemoryDatabase();
			coverCache = new CCMemoryCoverCache(this);
			break;
		default:
			CCLog.addDefaultSwitchError(this, driver);
			coverCache = null;
			db = null;
		}
		
		_history = new CCDatabaseHistory(this);
		
		upgrader = new DatabaseMigration(db, databaseDirectory, databaseName, readonly);
	}

	private static CCDatabaseDriver autoDetermineDriver(String dbDir, String dbName) {
		var sqlite = PathFormatter.combine(dbDir, dbName, dbName + ".db"); //$NON-NLS-1$
		if (new File(sqlite).exists()) return CCDatabaseDriver.SQLITE;

		var derby = PathFormatter.combine(dbDir, dbName, "seg0"); //$NON-NLS-1$
		if (new File(derby).exists()) return CCDatabaseDriver.DERBY;

		CCLog.addWarning("Could not identify DB at path: " + dbDir + " | " + dbName); //$NON-NLS-1$
		return CCDatabaseDriver.SQLITE; // fallback
	}

	public boolean isReadonly() {
		return _readonly;
	}

	public void resetForTestReload() {
		if (!(coverCache instanceof CCMemoryCoverCache)) throw new Error();
		((CCMemoryCoverCache)coverCache).resetForTestReload();
	}
	
	public static CCDatabase create(CCDatabaseDriver dbDriver, String dbPath, String dbName, boolean dbReadonly) {
		return new CCDatabase(dbDriver, dbPath, dbName, dbReadonly);
	}
	
	public static CCDatabase createStub() {
		return new CCDatabase(CCDatabaseDriver.STUB, "", "STUB", false); //$NON-NLS-1$
	}
	
	public static CCDatabase createInMemory() {
		return new CCDatabase(CCDatabaseDriver.INMEMORY, "", "INMEMORY", false); //$NON-NLS-1$
	}
	
	public boolean exists() {
		return db.databaseExists(databaseDirectory, databaseName);
	}

	public DatabaseConnectResult tryconnect() {
		if (db.databaseExists(databaseDirectory, databaseName)) {
			if (driverConnect()) {
				CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.DBConnect", getDBPath())); //$NON-NLS-1$
				
				return DatabaseConnectResult.SUCESS_CONNECTED;
			} else {
				CCLog.addDebug("Cannot connect because of reason:\n" + db.getLastError()); //$NON-NLS-1$
				
				return DatabaseConnectResult.ERROR_CANTCONNECT;
			}
		} else {
			if (driverCreate()) {
				CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.DBCreated", getDBPath())); //$NON-NLS-1$
				
				return DatabaseConnectResult.SUCCESS_CREATED;
			} else {
				CCLog.addDebug("Cannot create because of reason:\n" + db.getLastError()); //$NON-NLS-1$
				
				return DatabaseConnectResult.ERROR_CANTCREATE;
			}
		}
	}

	private boolean driverConnect() {
		try {
			if (! db.databaseExists(databaseDirectory, databaseName)) return false;
			
			db.establishDBConnection(databaseDirectory, databaseName);
			
			upgrader.tryUpgrade();
			
			Statements.intialize(this);

			coverCache.init();

			return true;
		} catch (SQLException e) {
			db.setLastError(e);
			
			Exception next = e.getNextException();
			if (next != null) db.setLastError(e);
			
			return false;
		} catch (Exception e) {
			db.setLastError(e);
			return false;
		} catch (Error e) {
			return false;
		}
	}

	private boolean driverCreate() {
		boolean res = db.createNewDatabasefromResourceXML('/' + XML_NAME, databaseDirectory, databaseName);
		if (res) {
			Statements.intialize(this);

			coverCache.init();

			writeInformationToDB(DatabaseStructure.INFOKEY_DBVERSION,   Main.DBVERSION);
			writeInformationToDB(DatabaseStructure.INFOKEY_DATE,        CCDate.getCurrentDate().toStringSQL());
			writeInformationToDB(DatabaseStructure.INFOKEY_TIME,        CCTime.getCurrentTime().toStringSQL());
			writeInformationToDB(DatabaseStructure.INFOKEY_USERNAME,    ApplicationHelper.getCurrentUsername());
			writeInformationToDB(DatabaseStructure.INFOKEY_HISTORY,     "0"); //$NON-NLS-1$
			writeInformationToDB(DatabaseStructure.INFOKEY_LASTID,      "0"); //$NON-NLS-1$
			writeInformationToDB(DatabaseStructure.INFOKEY_LASTCOVERID, "-1"); //$NON-NLS-1$
			writeInformationToDB(DatabaseStructure.INFOKEY_DUUID,       UUID.randomUUID().toString());
		}

		return res;
	}
	
	public void disconnect(boolean cleanshutdown) {
		try {
			if (db.isConnected()) {
				shutdownStatements();
				db.closeDBConnection(databaseDirectory, databaseName, cleanshutdown);
			}
		} catch (SQLException e) {
			CCLog.addError(LocaleBundle.getString("LogMessage.CouldNotDisconnectFromDB"), e); //$NON-NLS-1$
		}
	}
	
	public void reconnect() {
		if (! driverConnect()) {
			CCLog.addFatalError(LocaleBundle.getString("LogMessage.CouldNotReconnectToDB"), db.getLastError()); //$NON-NLS-1$
		}
	}

	/**
	 * @return the ammount of Movies and Series in the Database
	 */
	public int getDBElementCount() {
		return db.getRowCount(DatabaseStructure.TAB_MAIN.Name);
	}

	private CCDatabaseElement createDatabaseElementFromDatabase(CCSQLResultSet rs, CCMovieList ml, boolean fillSeries) throws SQLException, CCFormatException, SQLWrapperException {
		if (rs.getInt(DatabaseStructure.COL_MAIN_TYPE) == CCDBElementTyp.MOVIE.asInt()) {
			CCMovie mov = new CCMovie(ml, rs.getInt(DatabaseStructure.COL_MAIN_LOCALID));

			mov.beginUpdating();

			updateMovieFromResultSet(rs, mov);

			mov.abortUpdating();

			return mov;
		} else {
			int lid = rs.getInt(DatabaseStructure.COL_MAIN_LOCALID);
			CCSeries ser = new CCSeries(ml, lid);

			ser.beginUpdating();

			updateSeriesFromResultSet(rs, ser);

			ser.abortUpdating();

			if (fillSeries) fillSeries(ser);

			return ser;
		}
	}

	private CCSeason createSeasonFromDatabase(CCSQLResultSet rs, CCSeries ser, boolean fillSeason) throws SQLException, SQLWrapperException {
		CCSeason seas = new CCSeason(ser, rs.getInt(DatabaseStructure.COL_SEAS_SEASONID));

		seas.beginUpdating();

		updateSeasonFromResultSet(rs, seas);

		seas.abortUpdating();

		if (fillSeason) fillSeason(seas);

		return seas;
	}

	private CCEpisode createEpisodeFromDatabase(CCSQLResultSet rs, CCSeason se) throws SQLException, CCFormatException, SQLWrapperException {
		CCEpisode ep = new CCEpisode(se, rs.getInt(DatabaseStructure.COL_EPIS_LOCALID));

		ep.beginUpdating();

		updateEpisodeFromResultSet(rs, ep);

		ep.abortUpdating();

		return ep;
	}

	private void updateEpisodeFromResultSet(CCSQLResultSet rs, CCEpisode ep) throws SQLException, CCFormatException, SQLWrapperException {
		ep.EpisodeNumber.set(rs.getInt(DatabaseStructure.COL_EPIS_EPISODE));
		ep.Title.set(rs.getString(DatabaseStructure.COL_EPIS_NAME));
		ep.ViewedHistory.set(rs.getString(DatabaseStructure.COL_EPIS_VIEWEDHISTORY));
		ep.Length.set(rs.getInt(DatabaseStructure.COL_EPIS_LENGTH));
		ep.Format.set(rs.getInt(DatabaseStructure.COL_EPIS_FORMAT));
		ep.FileSize.set(rs.getLong(DatabaseStructure.COL_EPIS_FILESIZE));
		ep.Part.set(rs.getString(DatabaseStructure.COL_EPIS_PART_1));
		ep.AddDate.set(rs.getDate(DatabaseStructure.COL_EPIS_ADDDATE));
		ep.Tags.set(rs.getShort(DatabaseStructure.COL_EPIS_TAGS));
		ep.Language.set(rs.getLong(DatabaseStructure.COL_EPIS_LANGUAGE));
		ep.MediaInfo.set(CCMediaInfo.createFromDB(
			rs.getNullableLong(DatabaseStructure.COL_EPIS_MI_FILESIZE),
			rs.getNullableLong(DatabaseStructure.COL_EPIS_MI_CDATE),
			rs.getNullableLong(DatabaseStructure.COL_EPIS_MI_MDATE),
			rs.getNullableString(DatabaseStructure.COL_EPIS_MI_AFORMAT),
			rs.getNullableString(DatabaseStructure.COL_EPIS_MI_VFORMAT),
			rs.getNullableInt(DatabaseStructure.COL_EPIS_MI_WIDTH),
			rs.getNullableInt(DatabaseStructure.COL_EPIS_MI_HEIGHT),
			rs.getNullableFloat(DatabaseStructure.COL_EPIS_MI_FRAMERATE),
			rs.getNullableFloat(DatabaseStructure.COL_EPIS_MI_DURATION),
			rs.getNullableInt(DatabaseStructure.COL_EPIS_MI_BITDEPTH),
			rs.getNullableInt(DatabaseStructure.COL_EPIS_MI_BITRATE),
			rs.getNullableInt(DatabaseStructure.COL_EPIS_MI_FRAMECOUNT),
			rs.getNullableInt(DatabaseStructure.COL_EPIS_MI_ACHANNELS),
			rs.getNullableString(DatabaseStructure.COL_EPIS_MI_VCODEC),
			rs.getNullableString(DatabaseStructure.COL_EPIS_MI_ACODEC),
			rs.getNullableInt(DatabaseStructure.COL_EPIS_MI_SAMPLERATE),
			rs.getNullableString(DatabaseStructure.COL_EPIS_MI_CHECKSUM)));
	}

	private void updateSeasonFromResultSet(CCSQLResultSet rs, CCSeason seas) throws SQLException, SQLWrapperException {
		seas.Title.set(rs.getString(DatabaseStructure.COL_SEAS_NAME));
		seas.Year.set(rs.getInt(DatabaseStructure.COL_SEAS_YEAR));

		seas.setCover(rs.getInt(DatabaseStructure.COL_SEAS_COVERID));
	}

	private void updateSeriesFromResultSet(CCSQLResultSet rs, CCSeries ser) throws SQLException, CCFormatException, SQLWrapperException {
		ser.Title.set(rs.getString(DatabaseStructure.COL_MAIN_NAME));
		ser.Genres.set(rs.getLong(DatabaseStructure.COL_MAIN_GENRE));
		ser.OnlineScore.set(rs.getInt(DatabaseStructure.COL_MAIN_ONLINESCORE));
		ser.FSK.set(rs.getInt(DatabaseStructure.COL_MAIN_FSK));
		ser.Score.set(rs.getInt(DatabaseStructure.COL_MAIN_SCORE));
		ser.OnlineReference.set(rs.getString(DatabaseStructure.COL_MAIN_ONLINEREF));
		ser.Tags.set(rs.getShort(DatabaseStructure.COL_MAIN_TAGS));

		ser.setCover(rs.getInt(DatabaseStructure.COL_MAIN_COVERID));
		ser.setGroups(rs.getString(DatabaseStructure.COL_MAIN_GROUPS));
	}

	private void updateMovieFromResultSet(CCSQLResultSet rs, CCMovie mov) throws SQLException, CCFormatException, SQLWrapperException {
		mov.Title.set(rs.getString(DatabaseStructure.COL_MAIN_NAME));
		mov.ViewedHistory.set(rs.getString(DatabaseStructure.COL_MAIN_VIEWEDHISTORY));
		mov.Zyklus.set(rs.getString(DatabaseStructure.COL_MAIN_ZYKLUS), rs.getInt(DatabaseStructure.COL_MAIN_ZYKLUSNUMBER));
		mov.Language.set(rs.getLong(DatabaseStructure.COL_MAIN_LANGUAGE));
		mov.Genres.set(rs.getLong(DatabaseStructure.COL_MAIN_GENRE));
		mov.Length.set(rs.getInt(DatabaseStructure.COL_MAIN_LENGTH));
		mov.AddDate.set(rs.getDate(DatabaseStructure.COL_MAIN_ADDDATE));
		mov.OnlineScore.set(rs.getInt(DatabaseStructure.COL_MAIN_ONLINESCORE));
		mov.FSK.set(rs.getInt(DatabaseStructure.COL_MAIN_FSK));
		mov.Format.set(rs.getInt(DatabaseStructure.COL_MAIN_FORMAT));
		mov.Year.set(rs.getInt(DatabaseStructure.COL_MAIN_MOVIEYEAR));
		mov.OnlineReference.set(rs.getString(DatabaseStructure.COL_MAIN_ONLINEREF));
		mov.FileSize.set(rs.getLong(DatabaseStructure.COL_MAIN_FILESIZE));
		mov.Tags.set(rs.getShort(DatabaseStructure.COL_MAIN_TAGS));
		mov.Parts.set(0, rs.getString(DatabaseStructure.COL_MAIN_PART_1));
		mov.Parts.set(1, rs.getString(DatabaseStructure.COL_MAIN_PART_2));
		mov.Parts.set(2, rs.getString(DatabaseStructure.COL_MAIN_PART_3));
		mov.Parts.set(3, rs.getString(DatabaseStructure.COL_MAIN_PART_4));
		mov.Parts.set(4, rs.getString(DatabaseStructure.COL_MAIN_PART_5));
		mov.Parts.set(5, rs.getString(DatabaseStructure.COL_MAIN_PART_6));
		mov.Score.set(rs.getInt(DatabaseStructure.COL_MAIN_SCORE));
		mov.MediaInfo.set(CCMediaInfo.createFromDB(
			rs.getNullableLong(DatabaseStructure.COL_MAIN_MI_FILESIZE),
			rs.getNullableLong(DatabaseStructure.COL_MAIN_MI_CDATE),
			rs.getNullableLong(DatabaseStructure.COL_MAIN_MI_MDATE),
			rs.getNullableString(DatabaseStructure.COL_MAIN_MI_AFORMAT),
			rs.getNullableString(DatabaseStructure.COL_MAIN_MI_VFORMAT),
			rs.getNullableInt(DatabaseStructure.COL_MAIN_MI_WIDTH),
			rs.getNullableInt(DatabaseStructure.COL_MAIN_MI_HEIGHT),
			rs.getNullableFloat(DatabaseStructure.COL_MAIN_MI_FRAMERATE),
			rs.getNullableFloat(DatabaseStructure.COL_MAIN_MI_DURATION),
			rs.getNullableInt(DatabaseStructure.COL_MAIN_MI_BITDEPTH),
			rs.getNullableInt(DatabaseStructure.COL_MAIN_MI_BITRATE),
			rs.getNullableInt(DatabaseStructure.COL_MAIN_MI_FRAMECOUNT),
			rs.getNullableInt(DatabaseStructure.COL_MAIN_MI_ACHANNELS),
			rs.getNullableString(DatabaseStructure.COL_MAIN_MI_VCODEC),
			rs.getNullableString(DatabaseStructure.COL_MAIN_MI_ACODEC),
			rs.getNullableInt(DatabaseStructure.COL_MAIN_MI_SAMPLERATE),
			rs.getNullableString(DatabaseStructure.COL_MAIN_MI_CHECKSUM)));

		mov.setCover(rs.getInt(DatabaseStructure.COL_MAIN_COVERID));
		mov.setGroups(rs.getString(DatabaseStructure.COL_MAIN_GROUPS));
	}

	private int getNewID() {
		try {
			// increment
			newDatabaseIDStatement1.execute();

			// read back
			return newDatabaseIDStatement2.executeQueryInt(this);
		} catch (SQLException e) {
			CCLog.addError(LocaleBundle.getString("LogMessage.NoNewDatabaseID"), e); //$NON-NLS-1$
			return -1;
		}
	}

	@SuppressWarnings("nls")
	private boolean addEmptyMainRow(int id) {
		try {
			CCSQLStatement stmt = addEmptyMainTabStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_MAIN_LOCALID,       id);
			stmt.setStr(DatabaseStructure.COL_MAIN_NAME,          "");
			stmt.setStr(DatabaseStructure.COL_MAIN_VIEWEDHISTORY, "");
			stmt.setStr(DatabaseStructure.COL_MAIN_ZYKLUS,        "");
			stmt.setInt(DatabaseStructure.COL_MAIN_ZYKLUSNUMBER,  0);
			stmt.setInt(DatabaseStructure.COL_MAIN_LANGUAGE,      0);
			stmt.setInt(DatabaseStructure.COL_MAIN_GENRE,         0);
			stmt.setInt(DatabaseStructure.COL_MAIN_LENGTH,        0);
			stmt.setStr(DatabaseStructure.COL_MAIN_ADDDATE,       CCDate.MIN_SQL);
			stmt.setInt(DatabaseStructure.COL_MAIN_ONLINESCORE,   0);
			stmt.setInt(DatabaseStructure.COL_MAIN_FSK,           0);
			stmt.setInt(DatabaseStructure.COL_MAIN_FORMAT,        0);
			stmt.setInt(DatabaseStructure.COL_MAIN_MOVIEYEAR,     0);
			stmt.setStr(DatabaseStructure.COL_MAIN_ONLINEREF,     "");
			stmt.setStr(DatabaseStructure.COL_MAIN_GROUPS,        "");
			stmt.setInt(DatabaseStructure.COL_MAIN_FILESIZE,      0);
			stmt.setInt(DatabaseStructure.COL_MAIN_TAGS,          0);
			stmt.setStr(DatabaseStructure.COL_MAIN_PART_1,        "");
			stmt.setStr(DatabaseStructure.COL_MAIN_PART_2,        "");
			stmt.setStr(DatabaseStructure.COL_MAIN_PART_3,        "");
			stmt.setStr(DatabaseStructure.COL_MAIN_PART_4,        "");
			stmt.setStr(DatabaseStructure.COL_MAIN_PART_5,        "");
			stmt.setStr(DatabaseStructure.COL_MAIN_PART_6,        "");
			stmt.setInt(DatabaseStructure.COL_MAIN_SCORE,         0);
			stmt.setInt(DatabaseStructure.COL_MAIN_COVERID,       -1);
			stmt.setInt(DatabaseStructure.COL_MAIN_TYPE,          0);

			stmt.executeUpdate();

			return true;
		} catch (SQLException | SQLWrapperException e) {
			db.setLastError(e);
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.NoNewRow", id), e);
			return false;
		}
	}

	@SuppressWarnings("nls")
	private boolean addEmptySeasonsRow(int seasid, int serid) {
		try {
			CCSQLStatement stmt = addEmptySeasonTabStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_SEAS_SEASONID,  seasid);
			stmt.setInt(DatabaseStructure.COL_SEAS_SERIESID,  serid);
			stmt.setStr(DatabaseStructure.COL_SEAS_NAME,      "");
			stmt.setInt(DatabaseStructure.COL_SEAS_YEAR,      0);
			stmt.setInt(DatabaseStructure.COL_SEAS_COVERID,   -1);

			stmt.executeUpdate();

			return true;
		} catch (SQLException | SQLWrapperException e) {
			db.setLastError(e);
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.NoNewSeasonRow", seasid, serid), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	private boolean addEmptyEpisodeRow(int eid, int sid) {
		try {
			CCSQLStatement stmt = addEmptyEpisodeTabStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_EPIS_LOCALID,       eid);
			stmt.setInt(DatabaseStructure.COL_EPIS_SEASONID,      sid);
			stmt.setInt(DatabaseStructure.COL_EPIS_EPISODE,       0);
			stmt.setStr(DatabaseStructure.COL_EPIS_NAME,          "");
			stmt.setStr(DatabaseStructure.COL_EPIS_VIEWEDHISTORY, "");
			stmt.setInt(DatabaseStructure.COL_EPIS_LENGTH,        0);
			stmt.setInt(DatabaseStructure.COL_EPIS_FORMAT,        0);
			stmt.setInt(DatabaseStructure.COL_EPIS_FILESIZE,      0);
			stmt.setStr(DatabaseStructure.COL_EPIS_PART_1,        "");
			stmt.setInt(DatabaseStructure.COL_EPIS_TAGS,          0);
			stmt.setStr(DatabaseStructure.COL_EPIS_ADDDATE,       CCDate.MIN_SQL);
			stmt.setInt(DatabaseStructure.COL_EPIS_LANGUAGE,      0);

			stmt.executeUpdate();

			return true;
		} catch (SQLException | SQLWrapperException e) {
			db.setLastError(e);
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.NoNewEpisodeRow", eid, sid), e);
			return false;
		}
	}

	public CCMovie createNewEmptyMovie(CCMovieList list) {
		int nlid = getNewID();

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
		int nlid = getNewID();

		if (nlid == -1) {
			return null;
		}

		if (! addEmptyMainRow(nlid)) {
			return null;
		}

		CCSeries result = new CCSeries(list, nlid);
		result.setDefaultValues(false);

		return result;
	}

	public CCSeason createNewEmptySeason(CCSeries s) {
		int sid = getNewID();

		if (sid == -1) {
			return null;
		}

		if (! addEmptySeasonsRow(sid, s.getLocalID())) {
			return null;
		}

		CCSeason result = new CCSeason(s, sid);
		result.setDefaultValues(false);

		return result;
	}

	public CCEpisode createNewEmptyEpisode(CCSeason s) {
		int eid = getNewID();

		if (eid == -1) {
			return null;
		}

		if (! addEmptyEpisodeRow(eid, s.getLocalID())) {
			return null;
		}

		CCEpisode result = new CCEpisode(s, eid);
		result.setDefaultValues(false);

		return result;
	}

	@SuppressWarnings("nls")
	public boolean updateMovieInDatabase(CCMovie mov) {
		try {
			CCSQLStatement stmt = updateMainTabStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_MAIN_LOCALID,       mov.getLocalID());

			stmt.setStr(DatabaseStructure.COL_MAIN_NAME,          mov.Title.get());
			stmt.setStr(DatabaseStructure.COL_MAIN_VIEWEDHISTORY, mov.ViewedHistory.get().toSerializationString());
			stmt.setStr(DatabaseStructure.COL_MAIN_ZYKLUS,        mov.Zyklus.get().getTitle());
			stmt.setInt(DatabaseStructure.COL_MAIN_ZYKLUSNUMBER,  mov.Zyklus.get().getNumber());
			stmt.setLng(DatabaseStructure.COL_MAIN_LANGUAGE,      mov.Language.get().serializeToLong());
			stmt.setLng(DatabaseStructure.COL_MAIN_GENRE,         mov.Genres.get().getAllGenres());
			stmt.setInt(DatabaseStructure.COL_MAIN_LENGTH,        mov.Length.get());
			stmt.setStr(DatabaseStructure.COL_MAIN_ADDDATE,       mov.AddDate.get().toStringSQL());
			stmt.setInt(DatabaseStructure.COL_MAIN_ONLINESCORE,   mov.OnlineScore.get().asInt());
			stmt.setInt(DatabaseStructure.COL_MAIN_FSK,           mov.FSK.get().asInt());
			stmt.setInt(DatabaseStructure.COL_MAIN_FORMAT,        mov.Format.get().asInt());
			stmt.setInt(DatabaseStructure.COL_MAIN_MOVIEYEAR,     mov.Year.get());
			stmt.setStr(DatabaseStructure.COL_MAIN_ONLINEREF,     mov.OnlineReference.get().toSerializationString());
			stmt.setLng(DatabaseStructure.COL_MAIN_FILESIZE,      mov.FileSize.get().getBytes());
			stmt.setSht(DatabaseStructure.COL_MAIN_TAGS,          mov.Tags.get().asShort());
			stmt.setStr(DatabaseStructure.COL_MAIN_PART_1,        mov.Parts.get(0));
			stmt.setStr(DatabaseStructure.COL_MAIN_PART_2,        mov.Parts.get(1));
			stmt.setStr(DatabaseStructure.COL_MAIN_PART_3,        mov.Parts.get(2));
			stmt.setStr(DatabaseStructure.COL_MAIN_PART_4,        mov.Parts.get(3));
			stmt.setStr(DatabaseStructure.COL_MAIN_PART_5,        mov.Parts.get(4));
			stmt.setStr(DatabaseStructure.COL_MAIN_PART_6,        mov.Parts.get(5));
			stmt.setInt(DatabaseStructure.COL_MAIN_SCORE,         mov.Score.get().asInt());

			stmt.setStr(DatabaseStructure.COL_MAIN_GROUPS,        mov.getGroups().toSerializationString());
			stmt.setInt(DatabaseStructure.COL_MAIN_COVERID,       mov.getCoverID());
			stmt.setInt(DatabaseStructure.COL_MAIN_TYPE,          mov.getType().asInt());

			CCMediaInfo mi = mov.MediaInfo.get();
			if (mi.isSet())
			{
				stmt.setLng(DatabaseStructure.COL_MAIN_MI_FILESIZE,   mi.getFilesize());
				stmt.setLng(DatabaseStructure.COL_MAIN_MI_CDATE,      mi.getCDate());
				stmt.setLng(DatabaseStructure.COL_MAIN_MI_MDATE,      mi.getMDate());
				stmt.setStr(DatabaseStructure.COL_MAIN_MI_AFORMAT,    mi.getAudioFormat());
				stmt.setStr(DatabaseStructure.COL_MAIN_MI_VFORMAT,    mi.getVideoFormat());
				stmt.setInt(DatabaseStructure.COL_MAIN_MI_WIDTH,      mi.getWidth());
				stmt.setInt(DatabaseStructure.COL_MAIN_MI_HEIGHT,     mi.getHeight());
				stmt.setFlt(DatabaseStructure.COL_MAIN_MI_FRAMERATE,  mi.getFramerate());
				stmt.setFlt(DatabaseStructure.COL_MAIN_MI_DURATION,   mi.getDuration());
				stmt.setInt(DatabaseStructure.COL_MAIN_MI_BITDEPTH,   mi.getBitdepth());
				stmt.setInt(DatabaseStructure.COL_MAIN_MI_BITRATE,    mi.getBitrate());
				stmt.setInt(DatabaseStructure.COL_MAIN_MI_FRAMECOUNT, mi.getFramecount());
				stmt.setInt(DatabaseStructure.COL_MAIN_MI_ACHANNELS,  mi.getAudioChannels());
				stmt.setStr(DatabaseStructure.COL_MAIN_MI_VCODEC,     mi.getVideoCodec());
				stmt.setStr(DatabaseStructure.COL_MAIN_MI_ACODEC,     mi.getAudioCodec());
				stmt.setInt(DatabaseStructure.COL_MAIN_MI_SAMPLERATE, mi.getAudioSamplerate());
				stmt.setStr(DatabaseStructure.COL_MAIN_MI_CHECKSUM,   mi.getChecksum());
			}
			else
			{
				stmt.setNull(DatabaseStructure.COL_MAIN_MI_FILESIZE);
				stmt.setNull(DatabaseStructure.COL_MAIN_MI_CDATE);
				stmt.setNull(DatabaseStructure.COL_MAIN_MI_MDATE);
				stmt.setNull(DatabaseStructure.COL_MAIN_MI_AFORMAT);
				stmt.setNull(DatabaseStructure.COL_MAIN_MI_VFORMAT);
				stmt.setNull(DatabaseStructure.COL_MAIN_MI_WIDTH);
				stmt.setNull(DatabaseStructure.COL_MAIN_MI_HEIGHT);
				stmt.setNull(DatabaseStructure.COL_MAIN_MI_FRAMERATE);
				stmt.setNull(DatabaseStructure.COL_MAIN_MI_DURATION);
				stmt.setNull(DatabaseStructure.COL_MAIN_MI_BITDEPTH);
				stmt.setNull(DatabaseStructure.COL_MAIN_MI_BITRATE);
				stmt.setNull(DatabaseStructure.COL_MAIN_MI_FRAMECOUNT);
				stmt.setNull(DatabaseStructure.COL_MAIN_MI_ACHANNELS);
				stmt.setNull(DatabaseStructure.COL_MAIN_MI_VCODEC);
				stmt.setNull(DatabaseStructure.COL_MAIN_MI_ACODEC);
				stmt.setNull(DatabaseStructure.COL_MAIN_MI_SAMPLERATE);
				stmt.setNull(DatabaseStructure.COL_MAIN_MI_CHECKSUM);
			}

			stmt.execute();

			return true;
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateMovie", mov.Title.get(), mov.getLocalID()), e);
			return false;
		}
	}

	@SuppressWarnings("nls")
	public boolean updateSeriesInDatabase(CCSeries ser) {
		try {
			CCSQLStatement stmt = updateSeriesTabStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_MAIN_NAME,        ser.Title.get());
			stmt.setLng(DatabaseStructure.COL_MAIN_GENRE,       ser.Genres.get().getAllGenres());
			stmt.setInt(DatabaseStructure.COL_MAIN_ONLINESCORE, ser.OnlineScore.get().asInt());
			stmt.setInt(DatabaseStructure.COL_MAIN_FSK,         ser.FSK.get().asInt());
			stmt.setStr(DatabaseStructure.COL_MAIN_ONLINEREF,   ser.OnlineReference.get().toSerializationString());
			stmt.setInt(DatabaseStructure.COL_MAIN_SCORE,       ser.Score.get().asInt());
			stmt.setSht(DatabaseStructure.COL_MAIN_TAGS,        ser.Tags.get().asShort());

			stmt.setInt(DatabaseStructure.COL_MAIN_TYPE,        ser.getType().asInt());
			stmt.setInt(DatabaseStructure.COL_MAIN_COVERID,     ser.getCoverID());
			stmt.setStr(DatabaseStructure.COL_MAIN_GROUPS,      ser.getGroups().toSerializationString());

			stmt.setInt(DatabaseStructure.COL_MAIN_LOCALID,     ser.getLocalID());

			stmt.executeUpdate();

			return true;
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateSeries", ser.Title.get(), ser.getLocalID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateSeasonInDatabase(CCSeason ser) {
		try {
			CCSQLStatement stmt = updateSeasonTabStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_SEAS_SERIESID,  ser.getSeries().getLocalID());

			stmt.setStr(DatabaseStructure.COL_SEAS_NAME,      ser.Title.get());
			stmt.setInt(DatabaseStructure.COL_SEAS_YEAR,      ser.Year.get());

			stmt.setInt(DatabaseStructure.COL_SEAS_COVERID,   ser.getCoverID());

			stmt.setInt(DatabaseStructure.COL_SEAS_SEASONID,  ser.getLocalID());

			stmt.executeUpdate();

			return true;
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateSeason", ser.Title.get(), ser.getLocalID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateEpisodeInDatabase(CCEpisode ep) {
		try {
			CCSQLStatement stmt = updateEpisodeTabStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_EPIS_SEASONID,      ep.getSeason().getLocalID());

			stmt.setInt(DatabaseStructure.COL_EPIS_EPISODE,       ep.EpisodeNumber.get());
			stmt.setStr(DatabaseStructure.COL_EPIS_NAME,          ep.Title.get());
			stmt.setStr(DatabaseStructure.COL_EPIS_VIEWEDHISTORY, ep.ViewedHistory.get().toSerializationString());
			stmt.setInt(DatabaseStructure.COL_EPIS_LENGTH,        ep.Length.get());
			stmt.setInt(DatabaseStructure.COL_EPIS_FORMAT,        ep.Format.get().asInt());
			stmt.setLng(DatabaseStructure.COL_EPIS_FILESIZE,      ep.FileSize.get().getBytes());
			stmt.setStr(DatabaseStructure.COL_EPIS_PART_1,        ep.Part.get());
			stmt.setSht(DatabaseStructure.COL_EPIS_TAGS,          ep.Tags.get().asShort());
			stmt.setStr(DatabaseStructure.COL_EPIS_ADDDATE,       ep.AddDate.get().toStringSQL());
			stmt.setLng(DatabaseStructure.COL_EPIS_LANGUAGE,      ep.Language.get().serializeToLong());

			CCMediaInfo mi = ep.MediaInfo.get();
			if (mi.isSet())
			{
				stmt.setLng(DatabaseStructure.COL_EPIS_MI_FILESIZE,   mi.getFilesize());
				stmt.setLng(DatabaseStructure.COL_EPIS_MI_CDATE,      mi.getCDate());
				stmt.setLng(DatabaseStructure.COL_EPIS_MI_MDATE,      mi.getMDate());
				stmt.setStr(DatabaseStructure.COL_EPIS_MI_AFORMAT,    mi.getAudioFormat());
				stmt.setStr(DatabaseStructure.COL_EPIS_MI_VFORMAT,    mi.getVideoFormat());
				stmt.setInt(DatabaseStructure.COL_EPIS_MI_WIDTH,      mi.getWidth());
				stmt.setInt(DatabaseStructure.COL_EPIS_MI_HEIGHT,     mi.getHeight());
				stmt.setFlt(DatabaseStructure.COL_EPIS_MI_FRAMERATE,  mi.getFramerate());
				stmt.setFlt(DatabaseStructure.COL_EPIS_MI_DURATION,   mi.getDuration());
				stmt.setInt(DatabaseStructure.COL_EPIS_MI_BITDEPTH,   mi.getBitdepth());
				stmt.setInt(DatabaseStructure.COL_EPIS_MI_BITRATE,    mi.getBitrate());
				stmt.setInt(DatabaseStructure.COL_EPIS_MI_FRAMECOUNT, mi.getFramecount());
				stmt.setInt(DatabaseStructure.COL_EPIS_MI_ACHANNELS,  mi.getAudioChannels());
				stmt.setStr(DatabaseStructure.COL_EPIS_MI_VCODEC,     mi.getVideoCodec());
				stmt.setStr(DatabaseStructure.COL_EPIS_MI_ACODEC,     mi.getAudioCodec());
				stmt.setInt(DatabaseStructure.COL_EPIS_MI_SAMPLERATE, mi.getAudioSamplerate());
				stmt.setStr(DatabaseStructure.COL_EPIS_MI_CHECKSUM,   mi.getChecksum());
			}
			else
			{
				stmt.setNull(DatabaseStructure.COL_EPIS_MI_FILESIZE);
				stmt.setNull(DatabaseStructure.COL_EPIS_MI_CDATE);
				stmt.setNull(DatabaseStructure.COL_EPIS_MI_MDATE);
				stmt.setNull(DatabaseStructure.COL_EPIS_MI_AFORMAT);
				stmt.setNull(DatabaseStructure.COL_EPIS_MI_VFORMAT);
				stmt.setNull(DatabaseStructure.COL_EPIS_MI_WIDTH);
				stmt.setNull(DatabaseStructure.COL_EPIS_MI_HEIGHT);
				stmt.setNull(DatabaseStructure.COL_EPIS_MI_FRAMERATE);
				stmt.setNull(DatabaseStructure.COL_EPIS_MI_DURATION);
				stmt.setNull(DatabaseStructure.COL_EPIS_MI_BITDEPTH);
				stmt.setNull(DatabaseStructure.COL_EPIS_MI_BITRATE);
				stmt.setNull(DatabaseStructure.COL_EPIS_MI_FRAMECOUNT);
				stmt.setNull(DatabaseStructure.COL_EPIS_MI_ACHANNELS);
				stmt.setNull(DatabaseStructure.COL_EPIS_MI_VCODEC);
				stmt.setNull(DatabaseStructure.COL_EPIS_MI_ACODEC);
				stmt.setNull(DatabaseStructure.COL_EPIS_MI_SAMPLERATE);
				stmt.setNull(DatabaseStructure.COL_EPIS_MI_CHECKSUM);
			}

			stmt.setInt(DatabaseStructure.COL_EPIS_LOCALID,       ep.getLocalID());

			stmt.execute();

			return true;
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateEpisode", ep.Title.get(), ep.getLocalID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateMovieFromDatabase(CCMovie mov) {
		try {
			CCSQLStatement stmt = selectSingleMainTabStatement;
			stmt.clearParameters();
			stmt.setInt(DatabaseStructure.COL_MAIN_LOCALID, mov.getLocalID());
			CCSQLResultSet rs = stmt.executeQuery(this);
		
			if (rs.next()) { 
				updateMovieFromResultSet(rs, mov);
			}
			
			rs.close();
			return true;
		} catch (SQLException | CCFormatException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateMovie", mov.Title.get(), mov.getLocalID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateSeriesFromDatabase(CCSeries ser) {
		try {
			CCSQLStatement stmt = selectSingleMainTabStatement;
			stmt.clearParameters();
			stmt.setInt(DatabaseStructure.COL_MAIN_LOCALID, ser.getLocalID());
			CCSQLResultSet rs = stmt.executeQuery(this);
		
			if (rs.next()) { 
				updateSeriesFromResultSet(rs, ser);
			}
			
			rs.close();
			return true;
		} catch (SQLException | CCFormatException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateSeries", ser.Title.get(), ser.getLocalID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateSeasonFromDatabase(CCSeason sea) {
		try {
			CCSQLStatement stmt = selectSingleSeasonTabStatement;
			stmt.clearParameters();
			stmt.setInt(DatabaseStructure.COL_SEAS_SEASONID, sea.getLocalID());
			CCSQLResultSet rs = stmt.executeQuery(this);
		
			if (rs.next()) { 
				updateSeasonFromResultSet(rs, sea);
			}
			
			rs.close();
			return true;
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateSeason", sea.Title.get(), sea.getLocalID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateEpisodeFromDatabase(CCEpisode epi) {
		try {
			CCSQLStatement stmt = selectSingleEpisodeTabStatement;
			stmt.clearParameters();
			stmt.setInt(DatabaseStructure.COL_EPIS_LOCALID, epi.getLocalID());
			CCSQLResultSet rs = stmt.executeQuery(this);
		
			if (rs.next()) { 
				updateEpisodeFromResultSet(rs, epi);
			}
			
			rs.close();
			return true;
		} catch (SQLException | CCFormatException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateEpisode", epi.Title.get(), epi.getLocalID()), e);
			return false;
		}
	}

	public void fillMovieList(CCMovieList ml) {
		try
		{
			if (CCProperties.getInstance().PROP_LOADING_LIVEUPDATE.getValue())
			{
				CCSQLStatement stmt = selectAllMainTabStatement;
				stmt.clearParameters();

				CCSQLResultSet rs = stmt.executeQuery(this);

				while (rs.next()) ml.directlyInsert(createDatabaseElementFromDatabase(rs, ml, true));

				rs.close();
			}
			else
			{
				HashMap<Integer, CCSeries> seriesMap = new HashMap<>();

				// MOVIES & SERIES
				{
					CCSQLStatement stmt = selectAllMainTabStatement;
					stmt.clearParameters();

					CCSQLResultSet rs = stmt.executeQuery(this);

					List<CCDatabaseElement> temp = new ArrayList<>();
					while (rs.next()) {
						CCDatabaseElement de = createDatabaseElementFromDatabase(rs, ml, false);
						temp.add(de);
						if (de.getClass() == CCSeries.class) seriesMap.put(de.getLocalID(), (CCSeries) de);
					}
					ml.directlyInsert(temp);

					rs.close();
				}

				HashMap<Integer, CCSeason> seasonMap = new HashMap<>();

				// SEASONS
				{
					CCSQLStatement stmt = selectAllSeasonTabStatement;
					stmt.clearParameters();

					CCSeries lastSeries = null;

					CCSQLResultSet rs = stmt.executeQuery(this);
					while (rs.next()) {
						int sid = rs.getInt(DatabaseStructure.COL_SEAS_SERIESID);
						CCSeries ser = lastSeries;
						if (ser == null || ser.getLocalID() != sid) ser = seriesMap.get(sid);
						lastSeries = ser;

						ser.beginUpdating();
						CCSeason season = createSeasonFromDatabase(rs, ser, false);
						ser.directlyInsertSeason(season);
						seasonMap.put(season.getLocalID(), season);
						ser.abortUpdating();
					}
					rs.close();
				}

				// EPISODES
				{
					CCSQLStatement stmt = selectAllEpisodeTabStatement;
					stmt.clearParameters();

					CCSeason lastSeason = null;

					CCSQLResultSet rs = stmt.executeQuery(this);
					while (rs.next()) {
						int sid = rs.getInt(DatabaseStructure.COL_EPIS_SEASONID);
						CCSeason sea = lastSeason;
						if (sea == null || sea.getLocalID() != sid) sea = seasonMap.get(sid);
						lastSeason = sea;

						sea.beginUpdating();
						sea.directlyInsertEpisode(createEpisodeFromDatabase(rs, sea));
						sea.abortUpdating();
					}
					rs.close();
				}

				for (CCSeason s : seasonMap.values()) s.enforceOrder();
				for (CCSeries s : seriesMap.values()) s.enforceOrder();
			}
		} catch (SQLException | CCFormatException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}

	public void fillGroups(CCMovieList ml) {
		try
		{
			// GROUPS

			CCSQLStatement stmt = selectGroupsStatement;
			stmt.clearParameters();

			CCSQLResultSet rs = stmt.executeQuery(this);

			while (rs.next()) {

				String gn  = rs.getString(DatabaseStructure.COL_GRPS_NAME);
				int go     = rs.getInt(DatabaseStructure.COL_GRPS_ORDER);
				int gc     = rs.getInt(DatabaseStructure.COL_GRPS_COLOR);
				boolean gs = rs.getBoolean(DatabaseStructure.COL_GRPS_SERIALIZE);
				String gp  = rs.getString(DatabaseStructure.COL_GRPS_PARENT);
				boolean gv = rs.getBoolean(DatabaseStructure.COL_GRPS_VISIBLE);
				if (gp == null) gp = Str.Empty;

				ml.addGroupInternal(CCGroup.create(gn, go, gc, gs, gp, gv));
			}

			rs.close();

		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}

	public void fillCoverCache(boolean loadAll) {
		try
		{
			if (loadAll)
			{
				CCSQLStatement stmt = selectCoversFullStatement;
				stmt.clearParameters();

				CCSQLResultSet rs = stmt.executeQuery(this);

				while (rs.next()) {
					int id        = rs.getInt(DatabaseStructure.COL_CVRS_ID);
					String fn     = rs.getString(DatabaseStructure.COL_CVRS_FILENAME);
					int ww        = rs.getInt(DatabaseStructure.COL_CVRS_WIDTH);
					int hh        = rs.getInt(DatabaseStructure.COL_CVRS_HEIGHT);
					String cs     = rs.getString(DatabaseStructure.COL_CVRS_HASH_FILE);
					long fs       = rs.getLong(DatabaseStructure.COL_CVRS_FILESIZE);
					byte[] pv     = rs.getBlob(DatabaseStructure.COL_CVRS_PREVIEW);
					int pt        = rs.getInt(DatabaseStructure.COL_CVRS_PREVIEWTYPE);
					CCDateTime ts = rs.getDateTime(DatabaseStructure.COL_CVRS_CREATED);

					coverCache.addInternal(new CCCoverData(id, fn, ww, hh, cs, fs, pv, pt, ts));
				}
				rs.close();
			}
			else
			{
				CCSQLStatement stmt = selectCoversFastStatement;
				stmt.clearParameters();

				CCSQLResultSet rs = stmt.executeQuery(this);

				while (rs.next()) {
					int id        = rs.getInt(DatabaseStructure.COL_CVRS_ID);
					String fn     = rs.getString(DatabaseStructure.COL_CVRS_FILENAME);
					int ww        = rs.getInt(DatabaseStructure.COL_CVRS_WIDTH);
					int hh        = rs.getInt(DatabaseStructure.COL_CVRS_HEIGHT);
					long fs       = rs.getLong(DatabaseStructure.COL_CVRS_FILESIZE);
					int pt        = rs.getInt(DatabaseStructure.COL_CVRS_PREVIEWTYPE);
					CCDateTime ts = rs.getDateTime(DatabaseStructure.COL_CVRS_CREATED);
					String cs     = rs.getString(DatabaseStructure.COL_CVRS_HASH_FILE);

					coverCache.addInternal(new CCCoverData(id, fn, ww, hh, cs, fs, pt, ts));
				}
				rs.close();
			}
		} catch (SQLException | SQLWrapperException | CCFormatException e) {
			CCLog.addError(e);
		}
	}

	public byte[] getPreviewForCover(CCCoverData cce) {
		try
		{
			CCSQLStatement stmt = selectCoversSingleStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_CVRS_ID, cce.ID);

			CCSQLResultSet rs = stmt.executeQuery(this);

			rs.next();
			byte[] result = rs.getBlob(DatabaseStructure.COL_CVRS_PREVIEW);

			rs.close();

			return result;
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
			return null;
		}
	}

	private void fillSeries(CCSeries ser) {
		try {
			CCSQLStatement stmt = selectSeasonTabStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_SEAS_SERIESID, ser.getLocalID());
			
			CCSQLResultSet rs = stmt.executeQuery(this);

			ser.beginUpdating();

			while (rs.next()) {
				ser.directlyInsertSeason(createSeasonFromDatabase(rs, ser, true));
			}

			ser.abortUpdating();
			ser.enforceOrder();
			
			rs.close();
			
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}

	private void fillSeason(CCSeason se) {
		try {
			CCSQLStatement stmt = selectEpisodeTabStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_EPIS_SEASONID, se.getLocalID());
			
			CCSQLResultSet rs = stmt.executeQuery(this);

			se.beginUpdating();

			while (rs.next()) {
				se.directlyInsertEpisode(createEpisodeFromDatabase(rs, se));
			}

			se.abortUpdating();
			se.enforceOrder();
			
			rs.close();
			
		} catch (SQLException | CCFormatException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}

	public String getDBPath() {
		return PathFormatter.combine(databaseDirectory, databaseName);
	}

	public void removeFromMain(int localID) {	
		try {
			CCSQLStatement stmt = deleteMainTabStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_MAIN_LOCALID, localID);

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}
	
	public void removeFromSeasons(int seasonID) {
		try {
			CCSQLStatement stmt = deleteSeasonTabStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_SEAS_SEASONID, seasonID);

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}
	
	public void removeFromEpisodes(int localID) {
		try {
			CCSQLStatement stmt = deleteEpisodeTabStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_EPIS_LOCALID, localID);

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}
	
	public String getInformation_DBVersion() {
		return readInformationFromDB(DatabaseStructure.INFOKEY_DBVERSION, "0"); //$NON-NLS-1$
	}

	public String getInformation_DUUID() {
		String duuid = readInformationFromDB(DatabaseStructure.INFOKEY_DUUID, null);
		if (duuid == null) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.RegenerateDUUID")); //$NON-NLS-1$
			writeInformationToDB(DatabaseStructure.INFOKEY_DUUID, duuid = UUID.randomUUID().toString());
		}
		
		return duuid;
	}

	public void resetInformation_DUUID() {
		writeInformationToDB(DatabaseStructure.INFOKEY_DUUID, UUID.randomUUID().toString());
	}
	
	public String readInformationFromDB(CCSQLKVKey key, String defaultValue) {
		try {
			String value;
			
			CCSQLStatement stmt = readInfoKeyStatement;
			stmt.clearParameters();
			stmt.setStr(DatabaseStructure.COL_INFO_KEY, key.Key);
			
			CCSQLResultSet rs = stmt.executeQuery(this);
			if (rs.next()) 
				value = rs.getStringDirect(1);
			else 
				value = defaultValue;

			rs.close();
			
			return value;
			
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
			return defaultValue;
		}
	}

	public void writeInformationToDB(CCSQLKVKey key, String value) {
		try {
			CCSQLStatement stmt = writeInfoKeyStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_INFO_KEY, key.Key);
			stmt.setStr(DatabaseStructure.COL_INFO_VALUE, value);

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}
	
	public void removeGroup(String name) {
		try {
			CCSQLStatement stmt = removeGroupStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_GRPS_NAME, name);

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}

	public void addGroup(String name, int order, Color color, boolean doSerialize, String parent, boolean visible) {
		try {
			CCSQLStatement stmt = insertGroupStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_GRPS_NAME,      name);
			stmt.setInt(DatabaseStructure.COL_GRPS_ORDER,     order);
			stmt.setInt(DatabaseStructure.COL_GRPS_COLOR,     color.getRGB());
			stmt.setBoo(DatabaseStructure.COL_GRPS_SERIALIZE, doSerialize);
			stmt.setStr(DatabaseStructure.COL_GRPS_PARENT,    parent);
			stmt.setBoo(DatabaseStructure.COL_GRPS_VISIBLE,   visible);

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}

	public void updateGroup(String name, int order, Color color, boolean doSerialize, String parent, boolean visible) {
		try {
			CCSQLStatement stmt = updateGroupStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_GRPS_ORDER,     order);
			stmt.setInt(DatabaseStructure.COL_GRPS_COLOR,     color.getRGB());
			stmt.setBoo(DatabaseStructure.COL_GRPS_SERIALIZE, doSerialize);
			stmt.setStr(DatabaseStructure.COL_GRPS_PARENT,    parent);
			stmt.setBoo(DatabaseStructure.COL_GRPS_VISIBLE,   visible);

			stmt.setStr(DatabaseStructure.COL_GRPS_NAME,      name);

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}

	public boolean insertCoverEntry(CCCoverData cce) {
		try {
			{
				CCSQLStatement stmt = insertCoversStatement;
				stmt.clearParameters();

				stmt.setInt(DatabaseStructure.COL_CVRS_ID,          cce.ID);
				stmt.setStr(DatabaseStructure.COL_CVRS_FILENAME,    cce.Filename);
				stmt.setInt(DatabaseStructure.COL_CVRS_WIDTH,       cce.Width);
				stmt.setInt(DatabaseStructure.COL_CVRS_HEIGHT,      cce.Height);
				stmt.setStr(DatabaseStructure.COL_CVRS_HASH_FILE,   cce.Checksum);
				stmt.setLng(DatabaseStructure.COL_CVRS_FILESIZE,    cce.Filesize);
				stmt.setBlb(DatabaseStructure.COL_CVRS_PREVIEW,     cce.getPreviewOrNull());
				stmt.setInt(DatabaseStructure.COL_CVRS_PREVIEWTYPE, cce.PreviewType.asInt());
				stmt.setCDT(DatabaseStructure.COL_CVRS_CREATED,     cce.Timestamp);

				stmt.executeUpdate();
			}

			writeInformationToDB(DatabaseStructure.INFOKEY_LASTCOVERID, Integer.toString(cce.ID));

			return true;
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);

			return false;
		}
	}

	public void deleteCoverEntry(CCCoverData cce) {
		try {
			CCSQLStatement stmt = removeCoversStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_CVRS_ID, cce.ID);

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}

	public void clearGroups() {
		try {
			Statements.removeAllGroupsStatement.execute();
		} catch (SQLException e) {
			CCLog.addError(e);
		}
	}

	private void shutdownStatements() {
		shutdown();
	}

	public Exception getLastError() {
		return db.getLastError();
	}

	public PreparedStatement createPreparedStatement(String sql) throws SQLException {
		return db.createPreparedStatement(sql);
	}

	public String getDBTypeName() {
		return db.getDBTypeName();
	}

	public boolean isInMemory() {
		return db.isInMemory();
	}

	public ICoverCache getCoverCache() {
		return coverCache;
	}

	public boolean supportsDateType() {
		return db.supportsDateType();
	}

	public PublicDatabaseInterface getInternalDatabaseAccess() {
		return db;
	}

	public CCDatabaseHistory getHistory() {
		return _history;
	}

	public int getHistoryCount() {
		try {
			return countHistory.executeQueryInt(this);
		} catch (SQLException e) {
			CCLog.addError(e);
			return 0;
		}
	}

	public List<Tuple<String, String>> listTrigger() throws SQLException {
		return db.listTriggerWithStatements();
	}

	@SuppressWarnings("nls")
	public void deleteTrigger(String name, boolean ifExists) throws SQLException {
		if (ifExists)
			db.executeSQLThrow("DROP TRIGGER IF EXISTS " + SQLBuilderHelper.forceSQLEscape(name));
		else
			db.executeSQLThrow("DROP TRIGGER " + SQLBuilderHelper.forceSQLEscape(name));
	}

	public void createTrigger(String sql) throws SQLException {
		db.executeSQLThrow(sql);
	}

	public List<String[]> queryHistory(CCDateTime start, String idfilter) {
		try {
			List<String[]> result = new ArrayList<>();

			CCSQLResultSet rs;
			if (idfilter != null) {
				if (start != null) {
					CCSQLStatement stmt = queryHistoryStatementFilteredLimited;
					stmt.clearParameters();
					stmt.setStr(COL_HISTORY_ID, idfilter);
					stmt.setStr(COL_HISTORY_DATE, start.toUTC(TimeZone.getDefault()).toStringSQL());
					rs = stmt.executeQuery(this);
				} else {
					CCSQLStatement stmt = queryHistoryStatementFiltered;
					stmt.clearParameters();
					stmt.setStr(COL_HISTORY_ID, idfilter);
					rs = stmt.executeQuery(this);
				}
			} else {
				if (start != null) {
					CCSQLStatement stmt = queryHistoryStatementLimited;
					stmt.clearParameters();
					stmt.setStr(COL_HISTORY_DATE, start.toUTC(TimeZone.getDefault()).toStringSQL());
					rs = stmt.executeQuery(this);
				} else {
					CCSQLStatement stmt = queryHistoryStatement;
					stmt.clearParameters();
					rs = stmt.executeQuery(this);
				}
			}

			while (rs.next()) {
				String[] arr = new String[7];
				arr[0] = rs.getString(COL_HISTORY_TABLE);
				arr[1] = rs.getString(COL_HISTORY_ID);
				arr[2] = rs.getString(COL_HISTORY_DATE);
				arr[3] = rs.getString(COL_HISTORY_ACTION);
				arr[4] = rs.getString(COL_HISTORY_FIELD);
				arr[5] = rs.getNullableString(COL_HISTORY_OLD);
				arr[6] = rs.getNullableString(COL_HISTORY_NEW);
				result.add(arr);
			}

			rs.close();

			return result;

		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
			return new ArrayList<>();
		}
	}
}
