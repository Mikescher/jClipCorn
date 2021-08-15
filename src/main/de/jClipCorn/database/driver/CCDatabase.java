package de.jClipCorn.database.driver;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.covertab.*;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.database.migration.DatabaseMigration;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.datetime.CCTime;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.sqlwrapper.*;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.*;

import static de.jClipCorn.database.driver.DatabaseStructure.*;

public class CCDatabase {

	private final FSPath databaseDirectory; // = most of the time the working directory
	private final String databaseName;      // = the PROP_DATABASE_NAME

	private final GenericDatabase db;
	public  final DatabaseMigration upgrader;
	private final ICoverCache coverCache;
	private final Statements stmts;
	private final CCDatabaseHistory _history;

	private final boolean _readonly;

	private CCDatabase(CCDatabaseDriver driver, FSPath dbDir, String dbName, boolean readonly) {
		super();

		_readonly = readonly;

		databaseDirectory = dbDir;
		databaseName      = dbName;

		if (driver == null) driver = autoDetermineDriver(dbDir, dbName);

		switch (driver) {
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

		stmts = new Statements();
	}

	private static CCDatabaseDriver autoDetermineDriver(FSPath dbDir, String dbName) {
		var sqlite = dbDir.append(dbName, dbName + ".db"); //$NON-NLS-1$
		if (sqlite.exists()) return CCDatabaseDriver.SQLITE;

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
	
	public static CCDatabase create(CCDatabaseDriver dbDriver, FSPath dbPath, String dbName, boolean dbReadonly) {
		return new CCDatabase(dbDriver, dbPath, dbName, dbReadonly);
	}
	
	public static CCDatabase createStub() {
		return new CCDatabase(CCDatabaseDriver.STUB, FSPath.Empty, "STUB", false); //$NON-NLS-1$
	}
	
	public static CCDatabase createInMemory() {
		return new CCDatabase(CCDatabaseDriver.INMEMORY, FSPath.Empty, "INMEMORY", false); //$NON-NLS-1$
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

			stmts.initialize(this);

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
		boolean res = db.createNewDatabase(databaseDirectory, databaseName);
		if (res) {
			try {
				stmts.initialize(this);
			} catch (SQLException | SQLWrapperException e) {
				db.setLastError(e);
				return false;
			}

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
				stmts.shutdown();
				db.closeDBConnection(databaseDirectory, databaseName, cleanshutdown);
				CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.DBDisconnect", getDBPath()));
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

	private CCDatabaseElement createMovieFromDatabase(CCSQLResultSet rs, CCMovieList ml) throws SQLException, CCFormatException, SQLWrapperException {
		CCMovie mov = new CCMovie(ml, rs.getInt(DatabaseStructure.COL_MOV_LOCALID));

		mov.beginUpdating();

		updateMovieFromResultSet(rs, mov);

		mov.abortUpdating();

		mov.resetDirty();

		return mov;
	}

	private CCDatabaseElement createSeriesFromDatabase(CCSQLResultSet rs, CCMovieList ml, boolean fillSeries) throws SQLException, CCFormatException, SQLWrapperException {
		int lid = rs.getInt(DatabaseStructure.COL_SER_LOCALID);
		CCSeries ser = new CCSeries(ml, lid);

		ser.beginUpdating();

		updateSeriesFromResultSet(rs, ser);

		ser.abortUpdating();

		ser.resetDirty();

		if (fillSeries) fillSeries(ser);

		return ser;
	}

	private CCSeason createSeasonFromDatabase(CCSQLResultSet rs, CCSeries ser, boolean fillSeason) throws SQLException, SQLWrapperException {
		CCSeason seas = new CCSeason(ser, rs.getInt(DatabaseStructure.COL_SEAS_LOCALID));

		seas.beginUpdating();

		updateSeasonFromResultSet(rs, seas);

		seas.abortUpdating();

		seas.resetDirty();

		if (fillSeason) fillSeason(seas);

		return seas;
	}

	private CCEpisode createEpisodeFromDatabase(CCSQLResultSet rs, CCSeason se) throws SQLException, CCFormatException, SQLWrapperException {
		CCEpisode ep = new CCEpisode(se, rs.getInt(DatabaseStructure.COL_EPIS_LOCALID));

		ep.beginUpdating();

		updateEpisodeFromResultSet(rs, ep);

		ep.abortUpdating();

		ep.resetDirty();

		return ep;
	}

	private void updateEpisodeFromResultSet(CCSQLResultSet rs, CCEpisode ep) throws SQLException, CCFormatException, SQLWrapperException {
		ep.EpisodeNumber.set(rs.getInt(DatabaseStructure.COL_EPIS_EPISODE));
		ep.Title.set(rs.getString(DatabaseStructure.COL_EPIS_NAME));
		ep.ViewedHistory.set(rs.getString(DatabaseStructure.COL_EPIS_VIEWEDHISTORY));
		ep.Length.set(rs.getInt(DatabaseStructure.COL_EPIS_LENGTH));
		ep.Format.set(rs.getInt(DatabaseStructure.COL_EPIS_FORMAT));
		ep.FileSize.set(rs.getLong(DatabaseStructure.COL_EPIS_FILESIZE));
		ep.Part.set(CCPath.create(rs.getString(DatabaseStructure.COL_EPIS_PART_1)));
		ep.AddDate.set(rs.getDate(DatabaseStructure.COL_EPIS_ADDDATE));
		ep.Tags.set(rs.getShort(DatabaseStructure.COL_EPIS_TAGS));
		ep.Language.set(rs.getLong(DatabaseStructure.COL_EPIS_LANGUAGE));

		ep.MediaInfo.CDate.set(Opt.ofNullable(rs.getNullableLong(DatabaseStructure.COL_EPIS_MI_CDATE)));
		ep.MediaInfo.MDate.set(Opt.ofNullable(rs.getNullableLong(DatabaseStructure.COL_EPIS_MI_MDATE)));
		ep.MediaInfo.Checksum.set(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_EPIS_MI_CHECKSUM)));
		ep.MediaInfo.Filesize.set(Opt.ofNullable(rs.getNullableLong(DatabaseStructure.COL_EPIS_MI_FILESIZE)).map(CCFileSize::new));
		ep.MediaInfo.Duration.set(Opt.ofNullable(rs.getNullableFloat(DatabaseStructure.COL_EPIS_MI_DURATION)));
		ep.MediaInfo.Bitrate.set(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_EPIS_MI_BITRATE)));
		ep.MediaInfo.VideoFormat.set(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_EPIS_MI_VFORMAT)));
		ep.MediaInfo.Width.set(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_EPIS_MI_WIDTH)));
		ep.MediaInfo.Height.set(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_EPIS_MI_HEIGHT)));
		ep.MediaInfo.Framerate.set(Opt.ofNullable(rs.getNullableFloat(DatabaseStructure.COL_EPIS_MI_FRAMERATE)));
		ep.MediaInfo.Bitdepth.set(Opt.ofNullable(rs.getNullableShort(DatabaseStructure.COL_EPIS_MI_BITDEPTH)));
		ep.MediaInfo.Framecount.set(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_EPIS_MI_FRAMECOUNT)));
		ep.MediaInfo.VideoCodec.set(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_EPIS_MI_VCODEC)));
		ep.MediaInfo.AudioFormat.set(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_EPIS_MI_AFORMAT)));
		ep.MediaInfo.AudioChannels.set(Opt.ofNullable(rs.getNullableShort(DatabaseStructure.COL_EPIS_MI_ACHANNELS)));
		ep.MediaInfo.AudioCodec.set(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_EPIS_MI_ACODEC)));
		ep.MediaInfo.AudioSamplerate.set(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_EPIS_MI_SAMPLERATE)));
	}

	private void updateSeasonFromResultSet(CCSQLResultSet rs, CCSeason seas) throws SQLException, SQLWrapperException {
		seas.Title.set(rs.getString(DatabaseStructure.COL_SEAS_NAME));
		seas.Year.set(rs.getInt(DatabaseStructure.COL_SEAS_YEAR));

		seas.setCover(rs.getInt(DatabaseStructure.COL_SEAS_COVERID));
	}

	private void updateSeriesFromResultSet(CCSQLResultSet rs, CCSeries ser) throws SQLException, CCFormatException, SQLWrapperException {
		ser.Title.set(rs.getString(DatabaseStructure.COL_SER_NAME));
		ser.Genres.set(rs.getLong(DatabaseStructure.COL_SER_GENRE));
		ser.OnlineScore.set(rs.getInt(DatabaseStructure.COL_SER_ONLINESCORE));
		ser.FSK.set(rs.getInt(DatabaseStructure.COL_SER_FSK));
		ser.Score.set(rs.getInt(DatabaseStructure.COL_SER_SCORE));
		ser.OnlineReference.set(rs.getString(DatabaseStructure.COL_SER_ONLINEREF));
		ser.Tags.set(rs.getShort(DatabaseStructure.COL_SER_TAGS));

		ser.setCover(rs.getInt(DatabaseStructure.COL_SER_COVERID));
		ser.Groups.set(rs.getString(DatabaseStructure.COL_SER_GROUPS));
	}

	private void updateMovieFromResultSet(CCSQLResultSet rs, CCMovie mov) throws SQLException, CCFormatException, SQLWrapperException {
		mov.Title.set(rs.getString(DatabaseStructure.COL_MOV_NAME));
		mov.ViewedHistory.set(rs.getString(DatabaseStructure.COL_MOV_VIEWEDHISTORY));
		mov.Zyklus.set(rs.getString(DatabaseStructure.COL_MOV_ZYKLUS), rs.getInt(DatabaseStructure.COL_MOV_ZYKLUSNUMBER));
		mov.Language.set(rs.getLong(DatabaseStructure.COL_MOV_LANGUAGE));
		mov.Genres.set(rs.getLong(DatabaseStructure.COL_MOV_GENRE));
		mov.Length.set(rs.getInt(DatabaseStructure.COL_MOV_LENGTH));
		mov.AddDate.set(rs.getDate(DatabaseStructure.COL_MOV_ADDDATE));
		mov.OnlineScore.set(rs.getInt(DatabaseStructure.COL_MOV_ONLINESCORE));
		mov.FSK.set(rs.getInt(DatabaseStructure.COL_MOV_FSK));
		mov.Format.set(rs.getInt(DatabaseStructure.COL_MOV_FORMAT));
		mov.Year.set(rs.getInt(DatabaseStructure.COL_MOV_MOVIEYEAR));
		mov.OnlineReference.set(rs.getString(DatabaseStructure.COL_MOV_ONLINEREF));
		mov.FileSize.set(rs.getLong(DatabaseStructure.COL_MOV_FILESIZE));
		mov.Tags.set(rs.getShort(DatabaseStructure.COL_MOV_TAGS));

		mov.Parts.Part0.set(rs.getString(DatabaseStructure.COL_MOV_PART_1));
		mov.Parts.Part1.set(rs.getString(DatabaseStructure.COL_MOV_PART_2));
		mov.Parts.Part2.set(rs.getString(DatabaseStructure.COL_MOV_PART_3));
		mov.Parts.Part3.set(rs.getString(DatabaseStructure.COL_MOV_PART_4));
		mov.Parts.Part4.set(rs.getString(DatabaseStructure.COL_MOV_PART_5));
		mov.Parts.Part5.set(rs.getString(DatabaseStructure.COL_MOV_PART_6));

		mov.Score.set(rs.getInt(DatabaseStructure.COL_MOV_SCORE));

		mov.MediaInfo.CDate.set(Opt.ofNullable(rs.getNullableLong(DatabaseStructure.COL_MOV_MI_CDATE)));
		mov.MediaInfo.MDate.set(Opt.ofNullable(rs.getNullableLong(DatabaseStructure.COL_MOV_MI_MDATE)));
		mov.MediaInfo.Checksum.set(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_MOV_MI_CHECKSUM)));
		mov.MediaInfo.Filesize.set(Opt.ofNullable(rs.getNullableLong(DatabaseStructure.COL_MOV_MI_FILESIZE)).map(CCFileSize::new));
		mov.MediaInfo.Duration.set(Opt.ofNullable(rs.getNullableFloat(DatabaseStructure.COL_MOV_MI_DURATION)));
		mov.MediaInfo.Bitrate.set(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_MOV_MI_BITRATE)));
		mov.MediaInfo.VideoFormat.set(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_MOV_MI_VFORMAT)));
		mov.MediaInfo.Width.set(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_MOV_MI_WIDTH)));
		mov.MediaInfo.Height.set(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_MOV_MI_HEIGHT)));
		mov.MediaInfo.Framerate.set(Opt.ofNullable(rs.getNullableFloat(DatabaseStructure.COL_MOV_MI_FRAMERATE)));
		mov.MediaInfo.Bitdepth.set(Opt.ofNullable(rs.getNullableShort(DatabaseStructure.COL_MOV_MI_BITDEPTH)));
		mov.MediaInfo.Framecount.set(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_MOV_MI_FRAMECOUNT)));
		mov.MediaInfo.VideoCodec.set(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_MOV_MI_VCODEC)));
		mov.MediaInfo.AudioFormat.set(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_MOV_MI_AFORMAT)));
		mov.MediaInfo.AudioChannels.set(Opt.ofNullable(rs.getNullableShort(DatabaseStructure.COL_MOV_MI_ACHANNELS)));
		mov.MediaInfo.AudioCodec.set(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_MOV_MI_ACODEC)));
		mov.MediaInfo.AudioSamplerate.set(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_MOV_MI_SAMPLERATE)));

		mov.setCover(rs.getInt(DatabaseStructure.COL_MOV_COVERID));
		mov.Groups.set(rs.getString(DatabaseStructure.COL_MOV_GROUPS));
	}

	private int getNewID() {
		try {
			// increment
			stmts.newDatabaseIDStatement1.execute();

			// read back
			return stmts.newDatabaseIDStatement2.executeQueryInt(this);
		} catch (SQLException e) {
			CCLog.addError(LocaleBundle.getString("LogMessage.NoNewDatabaseID"), e); //$NON-NLS-1$
			return -1;
		}
	}

	@SuppressWarnings("nls")
	private boolean addEmptyMovieRow(int id) {
		try {
			CCSQLStatement stmt = stmts.addEmptyMovieTabStatement;
			stmt.clearParameters();

			for (var col : stmt.getPreparedFields()) {
				if (col == COL_MOV_LOCALID)         { stmt.setInt(col, id);             continue; }
				if (col == COL_MOV_COVERID)         { stmt.setInt(col, -1);             continue; }
				if (col == COL_MOV_ADDDATE)         { stmt.setStr(col, CCDate.MIN_SQL); continue; }

				if (!col.NonNullable)               { stmt.setNull(col);                continue; }

				if (col.Type == CCSQLType.VARCHAR)  { stmt.setStr(col, Str.Empty);      continue; }
				if (col.Type.isCallableAsInteger()) { stmt.setInt(col, 0);              continue; }

				throw new Error("Unknown default value for column " + col.Name);
			}

			stmt.executeUpdate();

			return true;
		} catch (SQLException | SQLWrapperException e) {
			db.setLastError(e);
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.NoNewRow", id), e);
			return false;
		}
	}

	@SuppressWarnings("nls")
	private boolean addEmptySeriesRow(int id) {
		try {
			CCSQLStatement stmt = stmts.addEmptySeriesTabStatement;
			stmt.clearParameters();

			for (var col : stmt.getPreparedFields()) {
				if (col == COL_SER_LOCALID)         { stmt.setInt(col, id);        continue; }
				if (col == COL_SER_COVERID)         { stmt.setInt(col, -1);        continue; }

				if (!col.NonNullable)               { stmt.setNull(col);           continue; }

				if (col.Type == CCSQLType.VARCHAR)  { stmt.setStr(col, Str.Empty); continue; }
				if (col.Type.isCallableAsInteger()) { stmt.setInt(col, 0);         continue; }

				throw new Error("Unknown default value for column " + col.Name);
			}

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
			CCSQLStatement stmt = stmts.addEmptySeasonTabStatement;
			stmt.clearParameters();

			for (var col : stmt.getPreparedFields()) {
				if (col == COL_SEAS_LOCALID)        { stmt.setInt(col, seasid);    continue; }
				if (col == COL_SEAS_SERIESID)       { stmt.setInt(col, serid);     continue; }
				if (col == COL_SEAS_COVERID)        { stmt.setInt(col, -1);        continue; }

				if (!col.NonNullable)               { stmt.setNull(col);           continue; }

				if (col.Type == CCSQLType.VARCHAR)  { stmt.setStr(col, Str.Empty); continue; }
				if (col.Type.isCallableAsInteger()) { stmt.setInt(col, 0);         continue; }

				throw new Error("Unknown default value for column " + col.Name);
			}

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
			CCSQLStatement stmt = stmts.addEmptyEpisodeTabStatement;
			stmt.clearParameters();

			for (var col : stmt.getPreparedFields()) {
				if (col == COL_EPIS_LOCALID)        { stmt.setInt(col, eid);            continue; }
				if (col == COL_EPIS_SEASONID)       { stmt.setInt(col, sid);            continue; }
				if (col == COL_EPIS_ADDDATE)        { stmt.setStr(col, CCDate.MIN_SQL); continue; }

				if (!col.NonNullable)               { stmt.setNull(col);                continue; }

				if (col.Type == CCSQLType.VARCHAR)  { stmt.setStr(col, Str.Empty);      continue; }
				if (col.Type.isCallableAsInteger()) { stmt.setInt(col, 0);              continue; }

				throw new Error("Unknown default value for column " + col.Name);
			}

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

		if (! addEmptyMovieRow(nlid)) {
			return null;
		}

		CCMovie result = new CCMovie(list, nlid);
		result.setDefaultValues(false);
		result.resetDirty();

		return result;
	}

	public CCSeries createNewEmptySeries(CCMovieList list) {
		int nlid = getNewID();

		if (nlid == -1) {
			return null;
		}

		if (! addEmptySeriesRow(nlid)) {
			return null;
		}

		CCSeries result = new CCSeries(list, nlid);
		result.setDefaultValues(false);
		result.resetDirty();

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
		result.resetDirty();

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
		result.resetDirty();

		return result;
	}

	@SuppressWarnings("nls")
	public boolean updateMovieInDatabase(CCMovie mov) {
		try {
			CCSQLStatement stmt = stmts.updateMovieTabStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_MOV_LOCALID,       mov.getLocalID());

			stmt.setStr(DatabaseStructure.COL_MOV_NAME,          mov.Title.get());
			stmt.setStr(DatabaseStructure.COL_MOV_VIEWEDHISTORY, mov.ViewedHistory.get().toSerializationString());
			stmt.setStr(DatabaseStructure.COL_MOV_ZYKLUS,        mov.Zyklus.get().getTitle());
			stmt.setInt(DatabaseStructure.COL_MOV_ZYKLUSNUMBER,  mov.Zyklus.get().getNumber());
			stmt.setLng(DatabaseStructure.COL_MOV_LANGUAGE,      mov.Language.get().serializeToLong());
			stmt.setLng(DatabaseStructure.COL_MOV_GENRE,         mov.Genres.get().getAllGenres());
			stmt.setInt(DatabaseStructure.COL_MOV_LENGTH,        mov.Length.get());
			stmt.setStr(DatabaseStructure.COL_MOV_ADDDATE,       mov.AddDate.get().toStringSQL());
			stmt.setInt(DatabaseStructure.COL_MOV_ONLINESCORE,   mov.OnlineScore.get().asInt());
			stmt.setInt(DatabaseStructure.COL_MOV_FSK,           mov.FSK.get().asInt());
			stmt.setInt(DatabaseStructure.COL_MOV_FORMAT,        mov.Format.get().asInt());
			stmt.setInt(DatabaseStructure.COL_MOV_MOVIEYEAR,     mov.Year.get());
			stmt.setStr(DatabaseStructure.COL_MOV_ONLINEREF,     mov.OnlineReference.get().toSerializationString());
			stmt.setLng(DatabaseStructure.COL_MOV_FILESIZE,      mov.FileSize.get().getBytes());
			stmt.setSht(DatabaseStructure.COL_MOV_TAGS,          mov.Tags.get().asShort());
			stmt.setStr(DatabaseStructure.COL_MOV_PART_1,        mov.Parts.get(0).toString());
			stmt.setStr(DatabaseStructure.COL_MOV_PART_2,        mov.Parts.get(1).toString());
			stmt.setStr(DatabaseStructure.COL_MOV_PART_3,        mov.Parts.get(2).toString());
			stmt.setStr(DatabaseStructure.COL_MOV_PART_4,        mov.Parts.get(3).toString());
			stmt.setStr(DatabaseStructure.COL_MOV_PART_5,        mov.Parts.get(4).toString());
			stmt.setStr(DatabaseStructure.COL_MOV_PART_6,        mov.Parts.get(5).toString());
			stmt.setInt(DatabaseStructure.COL_MOV_SCORE,         mov.Score.get().asInt());

			stmt.setStr(DatabaseStructure.COL_MOV_GROUPS,        mov.getGroups().toSerializationString());
			stmt.setInt(DatabaseStructure.COL_MOV_COVERID,       mov.getCoverID());

			var mi = mov.MediaInfo.getPartial();

			stmt.setNullableLng(DatabaseStructure.COL_MOV_MI_FILESIZE,   mi.Filesize.mapOrElse(CCFileSize::getBytes, null));
			stmt.setNullableLng(DatabaseStructure.COL_MOV_MI_CDATE,      mi.CDate.orElse(null));
			stmt.setNullableLng(DatabaseStructure.COL_MOV_MI_MDATE,      mi.MDate.orElse(null));
			stmt.setNullableStr(DatabaseStructure.COL_MOV_MI_AFORMAT,    mi.AudioFormat.orElse(null));
			stmt.setNullableStr(DatabaseStructure.COL_MOV_MI_VFORMAT,    mi.VideoFormat.orElse(null));
			stmt.setNullableInt(DatabaseStructure.COL_MOV_MI_WIDTH,      mi.Width.orElse(null));
			stmt.setNullableInt(DatabaseStructure.COL_MOV_MI_HEIGHT,     mi.Height.orElse(null));
			stmt.setNullableFlt(DatabaseStructure.COL_MOV_MI_FRAMERATE,  mi.Framerate.orElse(null));
			stmt.setNullableFlt(DatabaseStructure.COL_MOV_MI_DURATION,   mi.Duration.orElse(null));
			stmt.setNullableSht(DatabaseStructure.COL_MOV_MI_BITDEPTH,   mi.Bitdepth.orElse(null));
			stmt.setNullableInt(DatabaseStructure.COL_MOV_MI_BITRATE,    mi.Bitrate.orElse(null));
			stmt.setNullableInt(DatabaseStructure.COL_MOV_MI_FRAMECOUNT, mi.Framecount.orElse(null));
			stmt.setNullableSht(DatabaseStructure.COL_MOV_MI_ACHANNELS,  mi.AudioChannels.orElse(null));
			stmt.setNullableStr(DatabaseStructure.COL_MOV_MI_VCODEC,     mi.VideoCodec.orElse(null));
			stmt.setNullableStr(DatabaseStructure.COL_MOV_MI_ACODEC,     mi.AudioCodec.orElse(null));
			stmt.setNullableInt(DatabaseStructure.COL_MOV_MI_SAMPLERATE, mi.AudioSamplerate.orElse(null));
			stmt.setNullableStr(DatabaseStructure.COL_MOV_MI_CHECKSUM,   mi.Checksum.orElse(null));

			stmt.execute();
			mov.resetDirty();

			return true;
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateMovie", mov.Title.get(), mov.getLocalID()), e);
			return false;
		}
	}

	@SuppressWarnings("nls")
	public boolean updateSeriesInDatabase(CCSeries ser) {
		try {
			CCSQLStatement stmt = stmts.updateSeriesTabStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_SER_NAME,        ser.Title.get());
			stmt.setLng(DatabaseStructure.COL_SER_GENRE,       ser.Genres.get().getAllGenres());
			stmt.setInt(DatabaseStructure.COL_SER_ONLINESCORE, ser.OnlineScore.get().asInt());
			stmt.setInt(DatabaseStructure.COL_SER_FSK,         ser.FSK.get().asInt());
			stmt.setStr(DatabaseStructure.COL_SER_ONLINEREF,   ser.OnlineReference.get().toSerializationString());
			stmt.setInt(DatabaseStructure.COL_SER_SCORE,       ser.Score.get().asInt());
			stmt.setSht(DatabaseStructure.COL_SER_TAGS,        ser.Tags.get().asShort());

			stmt.setInt(DatabaseStructure.COL_SER_COVERID,     ser.getCoverID());
			stmt.setStr(DatabaseStructure.COL_SER_GROUPS,      ser.getGroups().toSerializationString());

			stmt.setInt(DatabaseStructure.COL_SER_LOCALID,     ser.getLocalID());

			stmt.executeUpdate();
			ser.resetDirty();

			return true;
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateSeries", ser.Title.get(), ser.getLocalID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateSeasonInDatabase(CCSeason sea) {
		try {
			CCSQLStatement stmt = stmts.updateSeasonTabStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_SEAS_SERIESID,  sea.getSeries().getLocalID());

			stmt.setStr(DatabaseStructure.COL_SEAS_NAME,      sea.Title.get());
			stmt.setInt(DatabaseStructure.COL_SEAS_YEAR,      sea.Year.get());

			stmt.setInt(DatabaseStructure.COL_SEAS_COVERID,   sea.getCoverID());

			stmt.setInt(DatabaseStructure.COL_SEAS_LOCALID,   sea.getLocalID());

			stmt.executeUpdate();
			sea.resetDirty();

			return true;
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateSeason", sea.Title.get(), sea.getLocalID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateEpisodeInDatabase(CCEpisode ep) {
		try {
			CCSQLStatement stmt = stmts.updateEpisodeTabStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_EPIS_SEASONID,      ep.getSeason().getLocalID());

			stmt.setInt(DatabaseStructure.COL_EPIS_EPISODE,       ep.EpisodeNumber.get());
			stmt.setStr(DatabaseStructure.COL_EPIS_NAME,          ep.Title.get());
			stmt.setStr(DatabaseStructure.COL_EPIS_VIEWEDHISTORY, ep.ViewedHistory.get().toSerializationString());
			stmt.setInt(DatabaseStructure.COL_EPIS_LENGTH,        ep.Length.get());
			stmt.setInt(DatabaseStructure.COL_EPIS_FORMAT,        ep.Format.get().asInt());
			stmt.setLng(DatabaseStructure.COL_EPIS_FILESIZE,      ep.FileSize.get().getBytes());
			stmt.setStr(DatabaseStructure.COL_EPIS_PART_1,        ep.Part.get().toString());
			stmt.setSht(DatabaseStructure.COL_EPIS_TAGS,          ep.Tags.get().asShort());
			stmt.setStr(DatabaseStructure.COL_EPIS_ADDDATE,       ep.AddDate.get().toStringSQL());
			stmt.setLng(DatabaseStructure.COL_EPIS_LANGUAGE,      ep.Language.get().serializeToLong());

			var mi = ep.MediaInfo.getPartial();

			stmt.setNullableLng(DatabaseStructure.COL_EPIS_MI_FILESIZE,   mi.Filesize.mapOrElse(CCFileSize::getBytes, null));
			stmt.setNullableLng(DatabaseStructure.COL_EPIS_MI_CDATE,      mi.CDate.orElse(null));
			stmt.setNullableLng(DatabaseStructure.COL_EPIS_MI_MDATE,      mi.MDate.orElse(null));
			stmt.setNullableStr(DatabaseStructure.COL_EPIS_MI_AFORMAT,    mi.AudioFormat.orElse(null));
			stmt.setNullableStr(DatabaseStructure.COL_EPIS_MI_VFORMAT,    mi.VideoFormat.orElse(null));
			stmt.setNullableInt(DatabaseStructure.COL_EPIS_MI_WIDTH,      mi.Width.orElse(null));
			stmt.setNullableInt(DatabaseStructure.COL_EPIS_MI_HEIGHT,     mi.Height.orElse(null));
			stmt.setNullableFlt(DatabaseStructure.COL_EPIS_MI_FRAMERATE,  mi.Framerate.orElse(null));
			stmt.setNullableFlt(DatabaseStructure.COL_EPIS_MI_DURATION,   mi.Duration.orElse(null));
			stmt.setNullableSht(DatabaseStructure.COL_EPIS_MI_BITDEPTH,   mi.Bitdepth.orElse(null));
			stmt.setNullableInt(DatabaseStructure.COL_EPIS_MI_BITRATE,    mi.Bitrate.orElse(null));
			stmt.setNullableInt(DatabaseStructure.COL_EPIS_MI_FRAMECOUNT, mi.Framecount.orElse(null));
			stmt.setNullableSht(DatabaseStructure.COL_EPIS_MI_ACHANNELS,  mi.AudioChannels.orElse(null));
			stmt.setNullableStr(DatabaseStructure.COL_EPIS_MI_VCODEC,     mi.VideoCodec.orElse(null));
			stmt.setNullableStr(DatabaseStructure.COL_EPIS_MI_ACODEC,     mi.AudioCodec.orElse(null));
			stmt.setNullableInt(DatabaseStructure.COL_EPIS_MI_SAMPLERATE, mi.AudioSamplerate.orElse(null));
			stmt.setNullableStr(DatabaseStructure.COL_EPIS_MI_CHECKSUM,   mi.Checksum.orElse(null));

			stmt.setInt(DatabaseStructure.COL_EPIS_LOCALID,       ep.getLocalID());

			stmt.execute();
			ep.resetDirty();

			return true;
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateEpisode", ep.Title.get(), ep.getLocalID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateMovieFromDatabase(CCMovie mov) {
		try {
			CCSQLStatement stmt = stmts.selectSingleMovieTabStatement;
			stmt.clearParameters();
			stmt.setInt(DatabaseStructure.COL_MOV_LOCALID, mov.getLocalID());
			CCSQLResultSet rs = stmt.executeQuery(this);
		
			if (rs.next()) { 
				updateMovieFromResultSet(rs, mov);
			}
			
			rs.close();

			mov.resetDirty();

			return true;
		} catch (SQLException | CCFormatException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateMovie", mov.Title.get(), mov.getLocalID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateSeriesFromDatabase(CCSeries ser) {
		try {
			CCSQLStatement stmt = stmts.selectSingleSeriesTabStatement;
			stmt.clearParameters();
			stmt.setInt(DatabaseStructure.COL_SER_LOCALID, ser.getLocalID());
			CCSQLResultSet rs = stmt.executeQuery(this);
		
			if (rs.next()) { 
				updateSeriesFromResultSet(rs, ser);
			}
			
			rs.close();

			ser.resetDirty();

			return true;
		} catch (SQLException | CCFormatException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateSeries", ser.Title.get(), ser.getLocalID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateSeasonFromDatabase(CCSeason sea) {
		try {
			CCSQLStatement stmt = stmts.selectSingleSeasonTabStatement;
			stmt.clearParameters();
			stmt.setInt(DatabaseStructure.COL_SEAS_LOCALID, sea.getLocalID());
			CCSQLResultSet rs = stmt.executeQuery(this);
		
			if (rs.next()) { 
				updateSeasonFromResultSet(rs, sea);
			}
			
			rs.close();

			sea.resetDirty();

			return true;
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateSeason", sea.Title.get(), sea.getLocalID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateEpisodeFromDatabase(CCEpisode epi) {
		try {
			CCSQLStatement stmt = stmts.selectSingleEpisodeTabStatement;
			stmt.clearParameters();
			stmt.setInt(DatabaseStructure.COL_EPIS_LOCALID, epi.getLocalID());
			CCSQLResultSet rs = stmt.executeQuery(this);
		
			if (rs.next()) { 
				updateEpisodeFromResultSet(rs, epi);
			}
			
			rs.close();

			epi.resetDirty();

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
				{
					CCSQLStatement stmt1 = stmts.selectAllMoviesTabStatement;
					stmt1.clearParameters();

					CCSQLResultSet rs1 = stmt1.executeQuery(this);

					while (rs1.next()) ml.directlyInsert(createMovieFromDatabase(rs1, ml));

					rs1.close();
				}
				{
					CCSQLStatement stmt2 = stmts.selectAllMoviesTabStatement;
					stmt2.clearParameters();

					CCSQLResultSet rs2 = stmt2.executeQuery(this);

					while (rs2.next()) ml.directlyInsert(createSeriesFromDatabase(rs2, ml, true));

					rs2.close();
				}
			}
			else
			{
				HashMap<Integer, CCSeries> seriesMap = new HashMap<>();

				// MOVIES
				{
					CCSQLStatement stmt = stmts.selectAllMoviesTabStatement;
					stmt.clearParameters();

					CCSQLResultSet rs = stmt.executeQuery(this);

					List<CCDatabaseElement> temp = new ArrayList<>();
					while (rs.next()) {
						CCDatabaseElement de = createMovieFromDatabase(rs, ml);
						temp.add(de);
					}
					ml.directlyInsert(temp);

					rs.close();
				}

				// SERIES
				{
					CCSQLStatement stmt = stmts.selectAllSeriesTabStatement;
					stmt.clearParameters();

					CCSQLResultSet rs = stmt.executeQuery(this);

					List<CCDatabaseElement> temp = new ArrayList<>();
					while (rs.next()) {
						CCDatabaseElement de = createSeriesFromDatabase(rs, ml, false);
						temp.add(de);
						if (de.getClass() == CCSeries.class) seriesMap.put(de.getLocalID(), (CCSeries) de);
					}
					ml.directlyInsert(temp);

					rs.close();
				}

				HashMap<Integer, CCSeason> seasonMap = new HashMap<>();

				// SEASONS
				{
					CCSQLStatement stmt = stmts.selectAllSeasonTabStatement;
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
					CCSQLStatement stmt = stmts.selectAllEpisodeTabStatement;
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

				ml.sortByIDAfterInitialLoad();
			}
		} catch (SQLException | CCFormatException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}

	public void fillGroups(CCMovieList ml) {
		try
		{
			// GROUPS

			CCSQLStatement stmt = stmts.selectGroupsStatement;
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
				CCSQLStatement stmt = stmts.selectCoversFullStatement;
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

					coverCache.addInternal(new CCCoverData(id, fn, ww, hh, cs, new CCFileSize(fs), pv, pt, ts));
				}
				rs.close();
			}
			else
			{
				CCSQLStatement stmt = stmts.selectCoversFastStatement;
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

					coverCache.addInternal(new CCCoverData(id, fn, ww, hh, cs, new CCFileSize(fs), pt, ts));
				}
				rs.close();
			}
		} catch (SQLException | SQLWrapperException | CCFormatException e) {
			CCLog.addError(e);
		}
	}

	private void fillSeries(CCSeries ser) {
		try {
			CCSQLStatement stmt = stmts.selectSeasonTabStatement;
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
			CCSQLStatement stmt = stmts.selectEpisodeTabStatement;
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

	public FSPath getDBPath() {
		return databaseDirectory.append(databaseName);
	}

	public String getDBName() {
		return databaseName;
	}

	public void removeFromMovies(int localID) {
		try {
			CCSQLStatement stmt = stmts.deleteMovieTabStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_MOV_LOCALID, localID);

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}

	public void removeFromSeries(int localID) {
		try {
			CCSQLStatement stmt = stmts.deleteSeriesTabStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_SER_LOCALID, localID);

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}
	
	public void removeFromSeasons(int seasonID) {
		try {
			CCSQLStatement stmt = stmts.deleteSeasonTabStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_SEAS_LOCALID, seasonID);

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}
	
	public void removeFromEpisodes(int localID) {
		try {
			CCSQLStatement stmt = stmts.deleteEpisodeTabStatement;
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
			
			CCSQLStatement stmt = stmts.readInfoKeyStatement;
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
			CCSQLStatement stmt = stmts.writeInfoKeyStatement;
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
			CCSQLStatement stmt = stmts.removeGroupStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_GRPS_NAME, name);

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}

	public void addGroup(String name, int order, Color color, boolean doSerialize, String parent, boolean visible) {
		try {
			CCSQLStatement stmt = stmts.insertGroupStatement;
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
			CCSQLStatement stmt = stmts.updateGroupStatement;
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
				CCSQLStatement stmt = stmts.insertCoversStatement;
				stmt.clearParameters();

				stmt.setInt(DatabaseStructure.COL_CVRS_ID,          cce.ID);
				stmt.setStr(DatabaseStructure.COL_CVRS_FILENAME,    cce.Filename);
				stmt.setInt(DatabaseStructure.COL_CVRS_WIDTH,       cce.Width);
				stmt.setInt(DatabaseStructure.COL_CVRS_HEIGHT,      cce.Height);
				stmt.setStr(DatabaseStructure.COL_CVRS_HASH_FILE,   cce.Checksum);
				stmt.setLng(DatabaseStructure.COL_CVRS_FILESIZE,    cce.Filesize.getBytes());
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
			CCSQLStatement stmt = stmts.removeCoversStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_CVRS_ID, cce.ID);

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}

	public void clearGroups() {
		try {
			stmts.removeAllGroupsStatement.execute();
		} catch (SQLException e) {
			CCLog.addError(e);
		}
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
			return stmts.countHistory.executeQueryInt(this);
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
					CCSQLStatement stmt = stmts.queryHistoryStatementFilteredLimited;
					stmt.clearParameters();
					stmt.setStr(COL_HISTORY_ID, idfilter);
					stmt.setStr(COL_HISTORY_DATE, start.toUTC(TimeZone.getDefault()).toStringSQL());
					rs = stmt.executeQuery(this);
				} else {
					CCSQLStatement stmt = stmts.queryHistoryStatementFiltered;
					stmt.clearParameters();
					stmt.setStr(COL_HISTORY_ID, idfilter);
					rs = stmt.executeQuery(this);
				}
			} else {
				if (start != null) {
					CCSQLStatement stmt = stmts.queryHistoryStatementLimited;
					stmt.clearParameters();
					stmt.setStr(COL_HISTORY_DATE, start.toUTC(TimeZone.getDefault()).toStringSQL());
					rs = stmt.executeQuery(this);
				} else {
					CCSQLStatement stmt = stmts.queryHistoryStatement;
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

	public static boolean validateDatabaseName(String name) {
		if (name == null) return false;

		if (name.length() == 0) return false;

		for (int i = 0; i < name.length(); i++) {
			char chr = name.charAt(i);

			boolean isDigit = (chr >= '0' && chr <= '9');
			boolean isUpper = (chr >= 'A' && chr <= 'Z');
			boolean isLower = (chr >= 'a' && chr <= 'z');
			boolean isSpecial = (chr == '_' || chr == '-');

			if (!(isDigit || isUpper || isLower || isSpecial)) return false;
		}

		return true;
	}
}
