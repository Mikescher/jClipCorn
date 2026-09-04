package de.jClipCorn.database.driver;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.covertab.*;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.database.elementProps.impl.ETargetDatabase;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageSet;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroupList;
import de.jClipCorn.database.databaseElement.columnTypes.CCHexColor;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.database.databaseElement.columnTypes.CCPathList;
import de.jClipCorn.database.databaseElement.columnTypes.CCStringList;
import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.database.history.CCHistoryDatabase;
import de.jClipCorn.database.migration.DatabaseMigrator;
import de.jClipCorn.database.migration.UserDataDatabaseMigrator;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.datetime.CCTime;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.sqlwrapper.*;
import de.jClipCorn.util.stream.CCStreams;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static de.jClipCorn.database.driver.DatabaseStructure.*;

public class CCDatabase {

	private final FSPath databaseDirectory; // = most of the time the working directory
	private final String databaseName;      // = Main.DATABASE_NAME ("ClipCornDB")

	private final GenericDatabase db;
	public  final DatabaseMigrator upgrader;
	public  final UserDataDatabaseMigrator userDataUpgrader;
	private final Statements stmts;
	private final CCDatabaseHistory _history;
	private final CCHistoryDatabase _historyDb;
	private final CCDatabaseDriver _driver;

	private final boolean _readonly;

	// BEGIN/COMMIT are connection-global - two threads writing rows at the same time would abort or
	// roll back each other's transaction. Never call into the UI while this is held (EDT deadlock).
	private final ReentrantLock _rowWriteLock = new ReentrantLock();

	private boolean firstLaunch = false;

	private CCDatabase(CCDatabaseDriver driver, FSPath dbDir, String dbName, boolean readonly) {
		super();

		_readonly = readonly;

		databaseDirectory = dbDir;
		databaseName      = dbName;

		if (driver == null) driver = autoDetermineDriver(dbDir, dbName);

		_driver = driver;

		switch (driver) {
		case SQLITE:
			db = new SQLiteDatabase(readonly);
			break;
		case STUB:
			db = new StubDatabase();
			break;
		case INMEMORY:
			db = new MemoryDatabase();
			break;
		default:
			CCLog.addDefaultSwitchError(this, driver);
			db = null;
			break;
		}
		
		_history = new CCDatabaseHistory(this);
		_historyDb = (driver == CCDatabaseDriver.SQLITE)
				? CCHistoryDatabase.createFileBased(databaseDirectory, databaseName, readonly)
				: CCHistoryDatabase.createInMemory();

		upgrader         = new DatabaseMigrator(db, databaseDirectory, databaseName, readonly);
		userDataUpgrader = new UserDataDatabaseMigrator(db, databaseDirectory, databaseName, readonly);

		stmts = new Statements();
	}

	private static CCDatabaseDriver autoDetermineDriver(FSPath dbDir, String dbName) {
		var sqlite = dbDir.append(dbName, dbName + ".db"); //$NON-NLS-1$
		if (sqlite.exists()) return CCDatabaseDriver.SQLITE;

		CCLog.addWarning("Could not identify DB at path: " + dbDir + " | " + dbName); //$NON-NLS-1$
		return CCDatabaseDriver.SQLITE; // fallback
	}

	public ICoverCache createCoverCache(CCProperties ccprops) {
		if (Main.ARG_PREV_COVER_CACHE) {
			return new CCPrevCoverCache(this, ccprops);
		}

		if (Main.ARG_MEM_COVER_CACHE) {
			return new CCMemoryCoverCache(this, ccprops);
		}

		switch (_driver) {
			case SQLITE:
				return new CCDefaultCoverCache(this, ccprops);
			case STUB:
				return new CCStubCoverCache();
			case INMEMORY:
				return new CCMemoryCoverCache(this, ccprops);
			default:
				CCLog.addDefaultSwitchError(this, _driver);
				return null;
		}
	}

	public boolean isReadonly() {
		return _readonly;
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
			this.firstLaunch = true;

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

			if (!validateUserDataMainVersion()) return false;

			upgrader.tryUpgrade();

			ensureUserDataDatabase();

			if (!upgradeUserDataDatabase()) return false;

			stmts.initialize(this);

			if (!validateUserDataBinding()) return false;

			updateUserDataMainVersion();

			if (!_historyDb.tryconnect(this)) {
				CCLog.addError("Failed to connect history database"); //$NON-NLS-1$
				return false;
			}

			healHistoryTrigger();

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

			writeInformationToDB(DatabaseStructure.INFOKEY_DBVERSION,   Main.DBVERSION);
			writeInformationToDB(DatabaseStructure.INFOKEY_DATE,        CCDate.getCurrentDate().toStringSQL());
			writeInformationToDB(DatabaseStructure.INFOKEY_TIME,        CCTime.getCurrentTime().toStringSQL());
			writeInformationToDB(DatabaseStructure.INFOKEY_USERNAME,    ApplicationHelper.getCurrentUsername());
			writeInformationToDB(DatabaseStructure.INFOKEY_DUUID,       UUID.randomUUID().toString());

			try {
				writeInitialUserDataInfo();
			} catch (SQLException e) {
				db.setLastError(e);
				return false;
			}

			if (!_historyDb.tryconnect(this)) {
				CCLog.addError("Failed to create history database"); //$NON-NLS-1$
			}
		}

		return res;
	}
	
	public void disconnect(boolean cleanshutdown) {
		// the staging rows only live in the (shared) main db until they are drained - a sync would destroy them
		try {
			syncHistoryToHistoryDb();
		} catch (Exception e) {
			CCLog.addError("Could not sync history before disconnect", e); //$NON-NLS-1$
		}

		try {
			if (_historyDb.isConnected()) {
				_historyDb.disconnect();
			}
		} catch (Exception e) {
			CCLog.addError("Could not disconnect from history database", e); //$NON-NLS-1$
		}

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

	private CCMovie createMovieFromDatabase(CCSQLResultSet rs, CCMovieList ml) throws SQLException, CCFormatException, SQLWrapperException {
		CCMovie mov = new CCMovie(ml, CCUUID.parse(rs.getString(DatabaseStructure.COL_MOV_ID)));

		mov.beginUpdating();

		updateMovieFromResultSet(rs, mov);

		mov.abortUpdating();

		mov.resetDirty();
		mov.initNfoPaths();

		return mov;
	}

	private CCSeries createSeriesFromDatabase(CCSQLResultSet rs, CCMovieList ml, boolean fillSeries) throws SQLException, CCFormatException, SQLWrapperException {
		CCSeries ser = new CCSeries(ml, CCUUID.parse(rs.getString(DatabaseStructure.COL_SER_ID)));

		ser.beginUpdating();

		updateSeriesFromResultSet(rs, ser);

		ser.abortUpdating();

		ser.resetDirty();
		ser.initNfoPaths();

		if (fillSeries) fillSeries(ser);

		return ser;
	}

	private CCSeason createSeasonFromDatabase(CCSQLResultSet rs, CCSeries ser, boolean fillSeason) throws SQLException, CCFormatException, SQLWrapperException {
		CCSeason seas = new CCSeason(ser, CCUUID.parse(rs.getString(DatabaseStructure.COL_SEAS_ID)));

		seas.beginUpdating();

		updateSeasonFromResultSet(rs, seas);

		seas.abortUpdating();

		seas.resetDirty();
		seas.initNfoPaths(ser);

		if (fillSeason) fillSeason(seas);

		return seas;
	}

	private CCEpisode createEpisodeFromDatabase(CCSQLResultSet rs, CCSeason se) throws SQLException, CCFormatException, SQLWrapperException {
		CCEpisode ep = new CCEpisode(se, CCUUID.parse(rs.getString(DatabaseStructure.COL_EPIS_ID)));

		ep.beginUpdating();

		updateEpisodeFromResultSet(rs, ep);

		ep.abortUpdating();

		ep.resetDirty();
		ep.initNfoPaths();

		return ep;
	}

	/**
	 * The user-data row is optional - after a sync of the shared database most entities do not have
	 * one yet, and then every user property falls back to its default.
	 */
	private void updateEpisodeUserDataFromResultSet(CCSQLResultSet rs, CCEpisode ep) throws SQLException, CCFormatException, SQLWrapperException {
		if (rs.getNullableString(DatabaseStructure.COL_UD_EPIS_ID) == null) {
			ep.ViewedHistory.setOnly(ep.ViewedHistory.DefaultValue);
			ep.Tags.setOnly(ep.Tags.DefaultValue);
			ep.Score.setOnly(ep.Score.DefaultValue);
			ep.ScoreComment.setOnly(ep.ScoreComment.DefaultValue);
			return;
		}

		ep.ViewedHistory.setOnly(CCDateTimeList.fromJSONArray(rs.getString(DatabaseStructure.COL_EPIS_VIEWEDHISTORY)));
		ep.Tags.setOnly(rs.getString(DatabaseStructure.COL_EPIS_TAGS));
		ep.Score.setOnly(rs.getInt(DatabaseStructure.COL_EPIS_SCORE));
		ep.ScoreComment.setOnly(rs.getString(DatabaseStructure.COL_EPIS_SCORECOMMENT));
	}

	private void updateSeasonUserDataFromResultSet(CCSQLResultSet rs, CCSeason seas) throws SQLException, SQLWrapperException {
		if (rs.getNullableString(DatabaseStructure.COL_UD_SEAS_ID) == null) {
			seas.Score.setOnly(seas.Score.DefaultValue);
			seas.ScoreComment.setOnly(seas.ScoreComment.DefaultValue);
			return;
		}

		seas.Score.setOnly(rs.getInt(DatabaseStructure.COL_SEAS_SCORE));
		seas.ScoreComment.setOnly(rs.getString(DatabaseStructure.COL_SEAS_SCORECOMMENT));
	}

	private void updateSeriesUserDataFromResultSet(CCSQLResultSet rs, CCSeries ser) throws SQLException, CCFormatException, SQLWrapperException {
		if (rs.getNullableString(DatabaseStructure.COL_UD_SER_ID) == null) {
			ser.Tags.setOnly(ser.Tags.DefaultValue);
			ser.Score.setOnly(ser.Score.DefaultValue);
			ser.ScoreComment.setOnly(ser.ScoreComment.DefaultValue);
			return;
		}

		ser.Tags.setOnly(rs.getString(DatabaseStructure.COL_SER_TAGS));
		ser.Score.setOnly(rs.getInt(DatabaseStructure.COL_SER_SCORE));
		ser.ScoreComment.setOnly(rs.getString(DatabaseStructure.COL_SER_SCORECOMMENT));
	}

	private void updateMovieUserDataFromResultSet(CCSQLResultSet rs, CCMovie mov) throws SQLException, CCFormatException, SQLWrapperException {
		if (rs.getNullableString(DatabaseStructure.COL_UD_MOV_ID) == null) {
			mov.ViewedHistory.setOnly(mov.ViewedHistory.DefaultValue);
			mov.Tags.setOnly(mov.Tags.DefaultValue);
			mov.Score.setOnly(mov.Score.DefaultValue);
			mov.ScoreComment.setOnly(mov.ScoreComment.DefaultValue);
			return;
		}

		mov.ViewedHistory.setOnly(CCDateTimeList.fromJSONArray(rs.getString(DatabaseStructure.COL_MOV_VIEWEDHISTORY)));
		mov.Tags.setOnly(rs.getString(DatabaseStructure.COL_MOV_TAGS));
		mov.Score.setOnly(rs.getInt(DatabaseStructure.COL_MOV_SCORE));
		mov.ScoreComment.setOnly(rs.getString(DatabaseStructure.COL_MOV_SCORECOMMENT));
	}

	private void updateEpisodeFromResultSet(CCSQLResultSet rs, CCEpisode ep) throws SQLException, CCFormatException, SQLWrapperException {
		ep.EpisodeNumber.setOnly(rs.getInt(DatabaseStructure.COL_EPIS_EPISODE));
		ep.Title.setOnly(rs.getString(DatabaseStructure.COL_EPIS_NAME));
		ep.Length.setOnly(rs.getInt(DatabaseStructure.COL_EPIS_LENGTH));
		ep.Format.setOnly(rs.getInt(DatabaseStructure.COL_EPIS_FORMAT));
		ep.FileSize.setOnly(rs.getLong(DatabaseStructure.COL_EPIS_FILESIZE));
		ep.Part.setOnly(CCPath.create(rs.getString(DatabaseStructure.COL_EPIS_PART_1)));
		ep.AddDate.setOnly(rs.getDate(DatabaseStructure.COL_EPIS_ADDDATE));
		ep.Language.setOnly(CCDBLanguageSet.fromJSONArray(rs.getString(DatabaseStructure.COL_EPIS_LANGUAGE)));
		ep.Subtitles.setOnly(CCDBLanguageList.fromJSONArray(rs.getString(DatabaseStructure.COL_EPIS_SUBTITLES)));

		ep.MediaInfo.CDate.setOnly(Opt.ofNullable(rs.getNullableLong(DatabaseStructure.COL_EPIS_MI_CDATE)));
		ep.MediaInfo.MDate.setOnly(Opt.ofNullable(rs.getNullableLong(DatabaseStructure.COL_EPIS_MI_MDATE)));
		ep.MediaInfo.Checksum.setOnly(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_EPIS_MI_CHECKSUM)));
		ep.MediaInfo.Filesize.setOnly(Opt.ofNullable(rs.getNullableLong(DatabaseStructure.COL_EPIS_MI_FILESIZE)).map(CCFileSize::new));
		ep.MediaInfo.Duration.setOnly(Opt.ofNullable(rs.getNullableFloat(DatabaseStructure.COL_EPIS_MI_DURATION)));
		ep.MediaInfo.Bitrate.setOnly(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_EPIS_MI_BITRATE)));
		ep.MediaInfo.VideoFormat.setOnly(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_EPIS_MI_VFORMAT)));
		ep.MediaInfo.Width.setOnly(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_EPIS_MI_WIDTH)));
		ep.MediaInfo.Height.setOnly(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_EPIS_MI_HEIGHT)));
		ep.MediaInfo.Framerate.setOnly(Opt.ofNullable(rs.getNullableFloat(DatabaseStructure.COL_EPIS_MI_FRAMERATE)));
		ep.MediaInfo.Bitdepth.setOnly(Opt.ofNullable(rs.getNullableShort(DatabaseStructure.COL_EPIS_MI_BITDEPTH)));
		ep.MediaInfo.Framecount.setOnly(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_EPIS_MI_FRAMECOUNT)));
		ep.MediaInfo.VideoCodec.setOnly(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_EPIS_MI_VCODEC)));
		ep.MediaInfo.AudioFormat.setOnly(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_EPIS_MI_AFORMAT)));
		ep.MediaInfo.AudioChannels.setOnly(Opt.ofNullable(rs.getNullableShort(DatabaseStructure.COL_EPIS_MI_ACHANNELS)));
		ep.MediaInfo.AudioCodec.setOnly(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_EPIS_MI_ACODEC)));
		ep.MediaInfo.AudioSamplerate.setOnly(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_EPIS_MI_SAMPLERATE)));
		ep.MediaInfo.updateCache();

		ep.ChecksumCRC32.setOnly(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_EPIS_CHECKSUM_CRC32)));
		ep.ChecksumMD5.setOnly(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_EPIS_CHECKSUM_MD5)));
		ep.ChecksumSHA256.setOnly(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_EPIS_CHECKSUM_SHA256)));
		ep.ChecksumSHA512.setOnly(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_EPIS_CHECKSUM_SHA512)));

		updateEpisodeUserDataFromResultSet(rs, ep);
	}

	private void updateSeasonFromResultSet(CCSQLResultSet rs, CCSeason seas) throws SQLException, CCFormatException, SQLWrapperException {
		seas.Title.setOnly(rs.getString(DatabaseStructure.COL_SEAS_NAME));
		seas.Year.setOnly(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_SEAS_YEAR)));
		seas.OnlineReference.setOnly(CCOnlineReferenceList.fromJSONArray(rs.getString(DatabaseStructure.COL_SEAS_ONLINEREF)));
		seas.AnimeSeason.setOnly(CCStringList.deserialize(rs.getString(DatabaseStructure.COL_SEAS_ANIMESEASON)));
		seas.AnimeStudio.setOnly(CCStringList.deserialize(rs.getString(DatabaseStructure.COL_SEAS_ANIMESTUDIO)));

		seas.CoverID.setOnly(CCUUID.parse(rs.getString(DatabaseStructure.COL_SEAS_COVERID)));

		updateSeasonUserDataFromResultSet(rs, seas);
	}

	private void updateSeriesFromResultSet(CCSQLResultSet rs, CCSeries ser) throws SQLException, CCFormatException, SQLWrapperException {
		ser.Title.setOnly(rs.getString(DatabaseStructure.COL_SER_NAME));
		ser.Genres.setOnly(CCGenreList.fromJSONArray(rs.getString(DatabaseStructure.COL_SER_GENRE)));
		ser.OnlineScore.setOnly(rs.getShort(DatabaseStructure.COL_SER_ONLINESCORE_NUM), rs.getShort(DatabaseStructure.COL_SER_ONLINESCORE_DENOM));
		ser.FSK.setOnly(rs.getInt(DatabaseStructure.COL_SER_FSK));
		ser.OnlineReference.setOnly(CCOnlineReferenceList.fromJSONArray(rs.getString(DatabaseStructure.COL_SER_ONLINEREF)));

		ser.CoverID.setOnly(CCUUID.parse(rs.getString(DatabaseStructure.COL_SER_COVERID)));
		ser.Groups.setOnly(CCGroupList.fromJSONArrayWithoutAddingNewGroups(ser.getMovieList(), rs.getString(DatabaseStructure.COL_SER_GROUPS)));
		ser.SpecialVersion.setOnly(CCStringList.deserialize(rs.getString(DatabaseStructure.COL_SER_SPECIALVERSION)));

		updateSeriesUserDataFromResultSet(rs, ser);
	}

	private void updateMovieFromResultSet(CCSQLResultSet rs, CCMovie mov) throws SQLException, CCFormatException, SQLWrapperException {
		mov.Title.setOnly(rs.getString(DatabaseStructure.COL_MOV_NAME));
		mov.Zyklus.setOnly(rs.getString(DatabaseStructure.COL_MOV_ZYKLUS), rs.getInt(DatabaseStructure.COL_MOV_ZYKLUSNUMBER));
		mov.Language.setOnly(CCDBLanguageSet.fromJSONArray(rs.getString(DatabaseStructure.COL_MOV_LANGUAGE)));
		mov.Subtitles.setOnly(CCDBLanguageList.fromJSONArray(rs.getString(DatabaseStructure.COL_MOV_SUBTITLES)));
		mov.Genres.setOnly(CCGenreList.fromJSONArray(rs.getString(DatabaseStructure.COL_MOV_GENRE)));
		mov.Length.setOnly(rs.getInt(DatabaseStructure.COL_MOV_LENGTH));
		mov.AddDate.setOnly(rs.getDate(DatabaseStructure.COL_MOV_ADDDATE));
		mov.OnlineScore.setOnly(rs.getShort(DatabaseStructure.COL_MOV_ONLINESCORE_NUM), rs.getShort(DatabaseStructure.COL_MOV_ONLINESCORE_DENOM));
		mov.FSK.setOnly(rs.getInt(DatabaseStructure.COL_MOV_FSK));
		mov.Format.setOnly(rs.getInt(DatabaseStructure.COL_MOV_FORMAT));
		mov.Year.setOnly(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_MOV_MOVIEYEAR)));
		mov.OnlineReference.setOnly(CCOnlineReferenceList.fromJSONArray(rs.getString(DatabaseStructure.COL_MOV_ONLINEREF)));
		mov.FileSize.setOnly(rs.getLong(DatabaseStructure.COL_MOV_FILESIZE));

		mov.Parts.setOnly(CCPathList.createFromJSON(rs.getString(DatabaseStructure.COL_MOV_PARTS)));

		mov.MediaInfo.CDate.setOnly(Opt.ofNullable(rs.getNullableLong(DatabaseStructure.COL_MOV_MI_CDATE)));
		mov.MediaInfo.MDate.setOnly(Opt.ofNullable(rs.getNullableLong(DatabaseStructure.COL_MOV_MI_MDATE)));
		mov.MediaInfo.Checksum.setOnly(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_MOV_MI_CHECKSUM)));
		mov.MediaInfo.Filesize.setOnly(Opt.ofNullable(rs.getNullableLong(DatabaseStructure.COL_MOV_MI_FILESIZE)).map(CCFileSize::new));
		mov.MediaInfo.Duration.setOnly(Opt.ofNullable(rs.getNullableFloat(DatabaseStructure.COL_MOV_MI_DURATION)));
		mov.MediaInfo.Bitrate.setOnly(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_MOV_MI_BITRATE)));
		mov.MediaInfo.VideoFormat.setOnly(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_MOV_MI_VFORMAT)));
		mov.MediaInfo.Width.setOnly(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_MOV_MI_WIDTH)));
		mov.MediaInfo.Height.setOnly(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_MOV_MI_HEIGHT)));
		mov.MediaInfo.Framerate.setOnly(Opt.ofNullable(rs.getNullableFloat(DatabaseStructure.COL_MOV_MI_FRAMERATE)));
		mov.MediaInfo.Bitdepth.setOnly(Opt.ofNullable(rs.getNullableShort(DatabaseStructure.COL_MOV_MI_BITDEPTH)));
		mov.MediaInfo.Framecount.setOnly(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_MOV_MI_FRAMECOUNT)));
		mov.MediaInfo.VideoCodec.setOnly(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_MOV_MI_VCODEC)));
		mov.MediaInfo.AudioFormat.setOnly(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_MOV_MI_AFORMAT)));
		mov.MediaInfo.AudioChannels.setOnly(Opt.ofNullable(rs.getNullableShort(DatabaseStructure.COL_MOV_MI_ACHANNELS)));
		mov.MediaInfo.AudioCodec.setOnly(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_MOV_MI_ACODEC)));
		mov.MediaInfo.AudioSamplerate.setOnly(Opt.ofNullable(rs.getNullableInt(DatabaseStructure.COL_MOV_MI_SAMPLERATE)));
		mov.MediaInfo.updateCache();

		mov.CoverID.setOnly(CCUUID.parse(rs.getString(DatabaseStructure.COL_MOV_COVERID)));
		mov.Groups.setOnly(CCGroupList.fromJSONArrayWithoutAddingNewGroups(mov.getMovieList(), rs.getString(DatabaseStructure.COL_MOV_GROUPS)));
		mov.SpecialVersion.setOnly(CCStringList.deserialize(rs.getString(DatabaseStructure.COL_MOV_SPECIALVERSION)));
		mov.AnimeSeason.setOnly(CCStringList.deserialize(rs.getString(DatabaseStructure.COL_MOV_ANIMESEASON)));
		mov.AnimeStudio.setOnly(CCStringList.deserialize(rs.getString(DatabaseStructure.COL_MOV_ANIMESTUDIO)));

		mov.ChecksumCRC32.setOnly(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_MOV_CHECKSUM_CRC32)));
		mov.ChecksumMD5.setOnly(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_MOV_CHECKSUM_MD5)));
		mov.ChecksumSHA256.setOnly(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_MOV_CHECKSUM_SHA256)));
		mov.ChecksumSHA512.setOnly(Opt.ofNullable(rs.getNullableString(DatabaseStructure.COL_MOV_CHECKSUM_SHA512)));

		updateMovieUserDataFromResultSet(rs, mov);
	}

	@SuppressWarnings("nls")
	private boolean addEmptyMovieRow(CCUUID id) {
		try {
			CCSQLStatement stmt = stmts.addEmptyMovieTabStatement;
			stmt.clearParameters();

			for (var col : stmt.getPreparedFields()) {
				if (col == COL_MOV_ID)              { stmt.setStr(col, id.toString());           continue; }
				if (col == COL_MOV_COVERID)         { stmt.setStr(col, CCUUID.EMPTY.toString()); continue; }
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
	private boolean addEmptySeriesRow(CCUUID id) {
		try {
			CCSQLStatement stmt = stmts.addEmptySeriesTabStatement;
			stmt.clearParameters();

			for (var col : stmt.getPreparedFields()) {
				if (col == COL_SER_ID)              { stmt.setStr(col, id.toString());           continue; }
				if (col == COL_SER_COVERID)         { stmt.setStr(col, CCUUID.EMPTY.toString()); continue; }

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
	private boolean addEmptySeasonsRow(CCUUID seasid, CCUUID serid) {
		try {
			CCSQLStatement stmt = stmts.addEmptySeasonTabStatement;
			stmt.clearParameters();

			for (var col : stmt.getPreparedFields()) {
				if (col == COL_SEAS_ID)             { stmt.setStr(col, seasid.toString());       continue; }
				if (col == COL_SEAS_SERIESID)       { stmt.setStr(col, serid.toString());        continue; }
				if (col == COL_SEAS_COVERID)        { stmt.setStr(col, CCUUID.EMPTY.toString()); continue; }

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
	private boolean addEmptyEpisodeRow(CCUUID eid, CCUUID sid) {
		try {
			CCSQLStatement stmt = stmts.addEmptyEpisodeTabStatement;
			stmt.clearParameters();

			for (var col : stmt.getPreparedFields()) {
				if (col == COL_EPIS_ID)             { stmt.setStr(col, eid.toString()); continue; }
				if (col == COL_EPIS_SEASONID)       { stmt.setStr(col, sid.toString()); continue; }
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
		CCUUID nlid = CCUUID.generate();

		if (! addEmptyMovieRow(nlid)) {
			return null;
		}

		CCMovie result = new CCMovie(list, nlid);
		result.setDefaultValues(false);
		result.resetDirty();

		return result;
	}

	public CCSeries createNewEmptySeries(CCMovieList list) {
		CCUUID nlid = CCUUID.generate();

		if (! addEmptySeriesRow(nlid)) {
			return null;
		}

		CCSeries result = new CCSeries(list, nlid);
		result.setDefaultValues(false);
		result.resetDirty();

		return result;
	}

	public CCSeason createNewEmptySeason(CCSeries s) {
		CCUUID sid = CCUUID.generate();

		if (! addEmptySeasonsRow(sid, s.getID())) {
			return null;
		}

		CCSeason result = new CCSeason(s, sid);
		result.setDefaultValues(false);
		result.resetDirty();

		return result;
	}

	public CCEpisode createNewEmptyEpisode(CCSeason s) {
		CCUUID eid = CCUUID.generate();

		if (! addEmptyEpisodeRow(eid, s.getID())) {
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
			beginRowTransaction();

			CCSQLStatement stmt = stmts.updateMovieTabStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_MOV_ID,                mov.getID().toString());

			stmt.setStr(DatabaseStructure.COL_MOV_NAME,              mov.Title.get());
			stmt.setStr(DatabaseStructure.COL_MOV_ZYKLUS,            mov.Zyklus.get().getTitle());
			stmt.setInt(DatabaseStructure.COL_MOV_ZYKLUSNUMBER,      mov.Zyklus.get().getNumber());
			stmt.setStr(DatabaseStructure.COL_MOV_LANGUAGE,          mov.Language.get().asJSONArray());
			stmt.setStr(DatabaseStructure.COL_MOV_SUBTITLES,         mov.Subtitles.get().asJSONArray());
			stmt.setStr(DatabaseStructure.COL_MOV_GENRE,             mov.Genres.get().asJSONArray());
			stmt.setInt(DatabaseStructure.COL_MOV_LENGTH,            mov.Length.get());
			stmt.setStr(DatabaseStructure.COL_MOV_ADDDATE,           mov.AddDate.get().toStringSQL());
			stmt.setSht(DatabaseStructure.COL_MOV_ONLINESCORE_NUM,   mov.OnlineScore.Numerator.get());
			stmt.setSht(DatabaseStructure.COL_MOV_ONLINESCORE_DENOM, mov.OnlineScore.Denominator.get());
			stmt.setInt(DatabaseStructure.COL_MOV_FSK,               mov.FSK.get().asInt());
			stmt.setInt(DatabaseStructure.COL_MOV_FORMAT,            mov.Format.get().asInt());
			stmt.setNullableInt(DatabaseStructure.COL_MOV_MOVIEYEAR, mov.Year.get().orElse(null));
			stmt.setStr(DatabaseStructure.COL_MOV_ONLINEREF,         mov.OnlineReference.get().asJSONArray());
			stmt.setLng(DatabaseStructure.COL_MOV_FILESIZE,          mov.FileSize.get().getBytes());
			stmt.setStr(DatabaseStructure.COL_MOV_PARTS,             mov.Parts.get().asJSONArray());

			stmt.setStr(DatabaseStructure.COL_MOV_GROUPS,            mov.getGroups().asJSONArray());
			stmt.setStr(DatabaseStructure.COL_MOV_SPECIALVERSION,    mov.SpecialVersion.serializeToString());
			stmt.setStr(DatabaseStructure.COL_MOV_ANIMESEASON,       mov.AnimeSeason.serializeToString());
			stmt.setStr(DatabaseStructure.COL_MOV_ANIMESTUDIO,       mov.AnimeStudio.serializeToString());
			stmt.setStr(DatabaseStructure.COL_MOV_COVERID,           mov.getCoverID().toString());

			var mi = mov.MediaInfo.get();

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

			stmt.setNullableStr(DatabaseStructure.COL_MOV_CHECKSUM_CRC32,  mov.ChecksumCRC32.get().orElse(null));
			stmt.setNullableStr(DatabaseStructure.COL_MOV_CHECKSUM_MD5,    mov.ChecksumMD5.get().orElse(null));
			stmt.setNullableStr(DatabaseStructure.COL_MOV_CHECKSUM_SHA256, mov.ChecksumSHA256.get().orElse(null));
			stmt.setNullableStr(DatabaseStructure.COL_MOV_CHECKSUM_SHA512, mov.ChecksumSHA512.get().orElse(null));

			stmt.execute();

			writeMovieUserData(mov);

			commitRowTransaction();

			mov.resetDirty();

			return true;
		} catch (SQLException | SQLWrapperException e) {
			rollbackRowTransaction();
			var msg = LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateMovie", mov.Title.get(), mov.getID());
			CCLog.addError(msg, e);
			DialogHelper.showDispatchError(MainFrame.getInstance(), LocaleBundle.getString("Dialogs.GenericCaption.Error"), msg);
			return false;
		} finally {
			endRowTransaction();
		}
	}

	@SuppressWarnings("nls")
	public boolean updateSeriesInDatabase(CCSeries ser) {
		try {
			beginRowTransaction();

			CCSQLStatement stmt = stmts.updateSeriesTabStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_SER_NAME,              ser.Title.get());
			stmt.setStr(DatabaseStructure.COL_SER_GENRE,             ser.Genres.get().asJSONArray());
			stmt.setInt(DatabaseStructure.COL_SER_ONLINESCORE_NUM,   ser.OnlineScore.Numerator.get());
			stmt.setInt(DatabaseStructure.COL_SER_ONLINESCORE_DENOM, ser.OnlineScore.Denominator.get());
			stmt.setInt(DatabaseStructure.COL_SER_FSK,               ser.FSK.get().asInt());
			stmt.setStr(DatabaseStructure.COL_SER_ONLINEREF,         ser.OnlineReference.get().asJSONArray());

			stmt.setStr(DatabaseStructure.COL_SER_COVERID,           ser.getCoverID().toString());
			stmt.setStr(DatabaseStructure.COL_SER_GROUPS,            ser.getGroups().asJSONArray());
			stmt.setStr(DatabaseStructure.COL_SER_SPECIALVERSION,    ser.SpecialVersion.serializeToString());

			stmt.setStr(DatabaseStructure.COL_SER_ID,                ser.getID().toString());

			stmt.executeUpdate();

			writeSeriesUserData(ser);

			commitRowTransaction();

			ser.resetDirty();

			return true;
		} catch (SQLException | SQLWrapperException e) {
			rollbackRowTransaction();
			var msg = LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateSeries", ser.Title.get(), ser.getID());
			CCLog.addError(msg, e);
			DialogHelper.showDispatchError(MainFrame.getInstance(), LocaleBundle.getString("Dialogs.GenericCaption.Error"), msg);
			return false;
		} finally {
			endRowTransaction();
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateSeasonInDatabase(CCSeason sea) {
		try {
			beginRowTransaction();

			CCSQLStatement stmt = stmts.updateSeasonTabStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_SEAS_SERIESID,  sea.getSeries().getID().toString());

			stmt.setStr(DatabaseStructure.COL_SEAS_NAME,         sea.Title.get());
			stmt.setNullableInt(DatabaseStructure.COL_SEAS_YEAR,  sea.Year.get().orElse(null));
			stmt.setStr(DatabaseStructure.COL_SEAS_ONLINEREF,    sea.OnlineReference.get().asJSONArray());
			stmt.setStr(DatabaseStructure.COL_SEAS_ANIMESEASON,  sea.AnimeSeason.serializeToString());
			stmt.setStr(DatabaseStructure.COL_SEAS_ANIMESTUDIO,  sea.AnimeStudio.serializeToString());

			stmt.setStr(DatabaseStructure.COL_SEAS_COVERID,   sea.getCoverID().toString());

			stmt.setStr(DatabaseStructure.COL_SEAS_ID,        sea.getID().toString());

			stmt.executeUpdate();

			writeSeasonUserData(sea);

			commitRowTransaction();

			sea.resetDirty();

			return true;
		} catch (SQLException | SQLWrapperException e) {
			rollbackRowTransaction();
			var msg = LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateSeason", sea.Title.get(), sea.getID());
			CCLog.addError(msg, e);
			DialogHelper.showDispatchError(MainFrame.getInstance(), LocaleBundle.getString("Dialogs.GenericCaption.Error"), msg);
			return false;
		} finally {
			endRowTransaction();
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateEpisodeInDatabase(CCEpisode ep) {
		try {
			beginRowTransaction();

			CCSQLStatement stmt = stmts.updateEpisodeTabStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_EPIS_SEASONID,      ep.getSeason().getID().toString());

			stmt.setInt(DatabaseStructure.COL_EPIS_EPISODE,       ep.EpisodeNumber.get());
			stmt.setStr(DatabaseStructure.COL_EPIS_NAME,          ep.Title.get());
			stmt.setInt(DatabaseStructure.COL_EPIS_LENGTH,        ep.Length.get());
			stmt.setInt(DatabaseStructure.COL_EPIS_FORMAT,        ep.Format.get().asInt());
			stmt.setLng(DatabaseStructure.COL_EPIS_FILESIZE,      ep.FileSize.get().getBytes());
			stmt.setStr(DatabaseStructure.COL_EPIS_PART_1,        ep.Part.get().toString());
			stmt.setStr(DatabaseStructure.COL_EPIS_ADDDATE,       ep.AddDate.get().toStringSQL());
			stmt.setStr(DatabaseStructure.COL_EPIS_LANGUAGE,      ep.Language.get().asJSONArray());
			stmt.setStr(DatabaseStructure.COL_EPIS_SUBTITLES,     ep.Subtitles.get().asJSONArray());

			var mi = ep.MediaInfo.get();

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

			stmt.setNullableStr(DatabaseStructure.COL_EPIS_CHECKSUM_CRC32,  ep.ChecksumCRC32.get().orElse(null));
			stmt.setNullableStr(DatabaseStructure.COL_EPIS_CHECKSUM_MD5,    ep.ChecksumMD5.get().orElse(null));
			stmt.setNullableStr(DatabaseStructure.COL_EPIS_CHECKSUM_SHA256, ep.ChecksumSHA256.get().orElse(null));
			stmt.setNullableStr(DatabaseStructure.COL_EPIS_CHECKSUM_SHA512, ep.ChecksumSHA512.get().orElse(null));

			stmt.setStr(DatabaseStructure.COL_EPIS_ID,            ep.getID().toString());

			stmt.execute();

			writeEpisodeUserData(ep);

			commitRowTransaction();

			ep.resetDirty();

			return true;
		} catch (SQLException | SQLWrapperException e) {
			rollbackRowTransaction();
			var msg = LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateEpisode", ep.Title.get(), ep.getID());
			CCLog.addError(msg, e);
			DialogHelper.showDispatchError(MainFrame.getInstance(), LocaleBundle.getString("Dialogs.GenericCaption.Error"), msg);
			return false;
		} finally {
			endRowTransaction();
		}
	}
	
	/**
	 * The main row and the user-data row live in two files - they have to land together, which SQLite
	 * only guarantees while no attached database is in WAL mode (see {@code SQLiteDatabase.open}).
	 */
	@SuppressWarnings("nls")
	private void beginRowTransaction() throws SQLException {
		_rowWriteLock.lock();
		db.executeSQLThrow("BEGIN TRANSACTION");
	}

	@SuppressWarnings("nls")
	private void commitRowTransaction() throws SQLException {
		try {
			db.executeSQLThrow("COMMIT TRANSACTION");
		} finally {
			endRowTransaction();
		}
	}

	@SuppressWarnings("nls")
	private void rollbackRowTransaction() {
		try {
			db.executeSQLThrow("ROLLBACK TRANSACTION");
		} catch (SQLException e) {
			// there was no open transaction - the write failed before it started
		} finally {
			endRowTransaction();
		}
	}

	/** Releases the row-write lock if this thread still holds it - must stay tolerant of being called twice. */
	private void endRowTransaction() {
		if (_rowWriteLock.isHeldByCurrentThread()) _rowWriteLock.unlock();
	}

	private static boolean allUserPropertiesAreDefault(ICCDatabaseStructureElement el) {
		return CCStreams.iterate(el.getProperties()).filter(p -> p.getTargetDatabase() == ETargetDatabase.USERDATA).all(IEProperty::isDefault);
	}

	/**
	 * Keeps the user-data database sparse: a row only exists while at least one user property
	 * differs from its default.
	 */
	private void writeMovieUserData(CCMovie mov) throws SQLException, SQLWrapperException {
		if (allUserPropertiesAreDefault(mov)) {
			CCSQLStatement del = stmts.deleteMovieUserDataStatement;
			del.clearParameters();
			del.setStr(DatabaseStructure.COL_UD_MOV_ID, mov.getID().toString());
			del.executeUpdate();
			return;
		}

		CCSQLStatement stmt = stmts.upsertMovieUserDataStatement;
		stmt.clearParameters();

		stmt.setStr(DatabaseStructure.COL_UD_MOV_ID,          mov.getID().toString());
		stmt.setStr(DatabaseStructure.COL_MOV_VIEWEDHISTORY,  mov.ViewedHistory.get().asJSONArray());
		stmt.setStr(DatabaseStructure.COL_MOV_TAGS,           mov.Tags.get().asJSONArray());
		stmt.setInt(DatabaseStructure.COL_MOV_SCORE,          mov.Score.get().asInt());
		stmt.setStr(DatabaseStructure.COL_MOV_SCORECOMMENT,   mov.ScoreComment.get());

		stmt.executeUpdate();
	}

	private void writeSeriesUserData(CCSeries ser) throws SQLException, SQLWrapperException {
		if (allUserPropertiesAreDefault(ser)) {
			CCSQLStatement del = stmts.deleteSeriesUserDataStatement;
			del.clearParameters();
			del.setStr(DatabaseStructure.COL_UD_SER_ID, ser.getID().toString());
			del.executeUpdate();
			return;
		}

		CCSQLStatement stmt = stmts.upsertSeriesUserDataStatement;
		stmt.clearParameters();

		stmt.setStr(DatabaseStructure.COL_UD_SER_ID,        ser.getID().toString());
		stmt.setStr(DatabaseStructure.COL_SER_TAGS,         ser.Tags.get().asJSONArray());
		stmt.setInt(DatabaseStructure.COL_SER_SCORE,        ser.Score.get().asInt());
		stmt.setStr(DatabaseStructure.COL_SER_SCORECOMMENT, ser.ScoreComment.get());

		stmt.executeUpdate();
	}

	private void writeSeasonUserData(CCSeason sea) throws SQLException, SQLWrapperException {
		if (allUserPropertiesAreDefault(sea)) {
			CCSQLStatement del = stmts.deleteSeasonUserDataStatement;
			del.clearParameters();
			del.setStr(DatabaseStructure.COL_UD_SEAS_ID, sea.getID().toString());
			del.executeUpdate();
			return;
		}

		CCSQLStatement stmt = stmts.upsertSeasonUserDataStatement;
		stmt.clearParameters();

		stmt.setStr(DatabaseStructure.COL_UD_SEAS_ID,        sea.getID().toString());
		stmt.setInt(DatabaseStructure.COL_SEAS_SCORE,        sea.Score.get().asInt());
		stmt.setStr(DatabaseStructure.COL_SEAS_SCORECOMMENT, sea.ScoreComment.get());

		stmt.executeUpdate();
	}

	private void writeEpisodeUserData(CCEpisode ep) throws SQLException, SQLWrapperException {
		if (allUserPropertiesAreDefault(ep)) {
			CCSQLStatement del = stmts.deleteEpisodeUserDataStatement;
			del.clearParameters();
			del.setStr(DatabaseStructure.COL_UD_EPIS_ID, ep.getID().toString());
			del.executeUpdate();
			return;
		}

		CCSQLStatement stmt = stmts.upsertEpisodeUserDataStatement;
		stmt.clearParameters();

		stmt.setStr(DatabaseStructure.COL_UD_EPIS_ID,         ep.getID().toString());
		stmt.setStr(DatabaseStructure.COL_EPIS_VIEWEDHISTORY, ep.ViewedHistory.get().asJSONArray());
		stmt.setStr(DatabaseStructure.COL_EPIS_TAGS,          ep.Tags.get().asJSONArray());
		stmt.setInt(DatabaseStructure.COL_EPIS_SCORE,         ep.Score.get().asInt());
		stmt.setStr(DatabaseStructure.COL_EPIS_SCORECOMMENT,  ep.ScoreComment.get());

		stmt.executeUpdate();
	}

	@SuppressWarnings("nls")
	public boolean updateMovieFromDatabase(CCMovie mov) {
		try {
			CCSQLStatement stmt = stmts.selectSingleMovieTabStatement;
			stmt.clearParameters();
			stmt.setStr(DatabaseStructure.COL_MOV_ID, mov.getID().toString());
			CCSQLResultSet rs = stmt.executeQuery(this);
		
			if (rs.next()) { 
				updateMovieFromResultSet(rs, mov);
			}
			
			rs.close();

			mov.resetDirty();

			return true;
		} catch (SQLException | CCFormatException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateMovie", mov.Title.get(), mov.getID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateSeriesFromDatabase(CCSeries ser) {
		try {
			CCSQLStatement stmt = stmts.selectSingleSeriesTabStatement;
			stmt.clearParameters();
			stmt.setStr(DatabaseStructure.COL_SER_ID, ser.getID().toString());
			CCSQLResultSet rs = stmt.executeQuery(this);
		
			if (rs.next()) { 
				updateSeriesFromResultSet(rs, ser);
			}
			
			rs.close();

			ser.resetDirty();

			return true;
		} catch (SQLException | CCFormatException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateSeries", ser.Title.get(), ser.getID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateSeasonFromDatabase(CCSeason sea) {
		try {
			CCSQLStatement stmt = stmts.selectSingleSeasonTabStatement;
			stmt.clearParameters();
			stmt.setStr(DatabaseStructure.COL_SEAS_ID, sea.getID().toString());
			CCSQLResultSet rs = stmt.executeQuery(this);
		
			if (rs.next()) { 
				updateSeasonFromResultSet(rs, sea);
			}
			
			rs.close();

			sea.resetDirty();

			return true;
		} catch (SQLException | CCFormatException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateSeason", sea.Title.get(), sea.getID()), e);
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	public boolean updateEpisodeFromDatabase(CCEpisode epi) {
		try {
			CCSQLStatement stmt = stmts.selectSingleEpisodeTabStatement;
			stmt.clearParameters();
			stmt.setStr(DatabaseStructure.COL_EPIS_ID, epi.getID().toString());
			CCSQLResultSet rs = stmt.executeQuery(this);
		
			if (rs.next()) { 
				updateEpisodeFromResultSet(rs, epi);
			}
			
			rs.close();

			epi.resetDirty();

			return true;
		} catch (SQLException | CCFormatException | SQLWrapperException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateEpisode", epi.Title.get(), epi.getID()), e);
			return false;
		}
	}

	public void fillMovieList(CCMovieList ml) {
		try
		{
			if (ml.ccprops().PROP_LOADING_LIVEUPDATE.getValue())
			{
				{
					CCSQLStatement stmt1 = stmts.selectAllMoviesTabStatement;
					stmt1.clearParameters();

					CCSQLResultSet rs1 = stmt1.executeQuery(this);

					while (rs1.next()) ml.directlyInsert(createMovieFromDatabase(rs1, ml));

					rs1.close();
				}
				{
					CCSQLStatement stmt2 = stmts.selectAllSeriesTabStatement;
					stmt2.clearParameters();

					CCSQLResultSet rs2 = stmt2.executeQuery(this);

					while (rs2.next()) {
						CCSeries de = createSeriesFromDatabase(rs2, ml, true);
						ml.directlyInsert(de);
					}

					rs2.close();
				}
			}
			else
			{
				HashMap<CCUUID, CCSeries> seriesMap = new HashMap<>();

				// MOVIES
				{
					CCSQLStatement stmt = stmts.selectAllMoviesTabStatement;
					stmt.clearParameters();

					CCSQLResultSet rs = stmt.executeQuery(this);

					List<CCDatabaseElement> temp = new ArrayList<>();
					while (rs.next()) {
						CCMovie de = createMovieFromDatabase(rs, ml);
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
						CCSeries de = createSeriesFromDatabase(rs, ml, false);
						temp.add(de);
						if (de.getClass() == CCSeries.class) seriesMap.put(de.getID(), de);
					}
					ml.directlyInsert(temp);

					rs.close();
				}

				HashMap<CCUUID, CCSeason> seasonMap = new HashMap<>();

				// SEASONS
				{
					CCSQLStatement stmt = stmts.selectAllSeasonTabStatement;
					stmt.clearParameters();

					CCSeries lastSeries = null;

					CCSQLResultSet rs = stmt.executeQuery(this);
					while (rs.next()) {
						CCUUID sid = CCUUID.parse(rs.getString(DatabaseStructure.COL_SEAS_SERIESID));
						CCSeries ser = lastSeries;
						if (ser == null || !ser.getID().equals(sid)) ser = seriesMap.get(sid);
						lastSeries = ser;

						ser.beginUpdating();
						CCSeason season = createSeasonFromDatabase(rs, ser, false);
						ser.directlyInsertSeason(season);
						seasonMap.put(season.getID(), season);
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
						CCUUID sid = CCUUID.parse(rs.getString(DatabaseStructure.COL_EPIS_SEASONID));
						CCSeason sea = lastSeason;
						if (sea == null || !sea.getID().equals(sid)) sea = seasonMap.get(sid);
						lastSeason = sea;

						sea.beginUpdating();
						CCEpisode episode = createEpisodeFromDatabase(rs, sea);
						sea.directlyInsertEpisode(episode);
						sea.abortUpdating();
					}
					rs.close();
				}

				for (CCSeason s : seasonMap.values()) s.enforceOrder();
				for (CCSeries s : seriesMap.values()) s.enforceOrder();

				// NOTE: Series/Season NFO-paths depend on guessSeriesBasePath(), which iterates the (now attached) episodes, 
				// so they must be (re)computed *here*, after the episodes are added. 
				
				HashMap<CCUUID, FSPath> seriesBasePaths = new HashMap<>();
				for (CCSeries s : seriesMap.values()) {
					FSPath basePath = s.guessSeriesBasePath();
					seriesBasePaths.put(s.getID(), basePath);
					s.initNfoPaths(basePath);
				}

				for (CCSeason s : seasonMap.values()) s.initNfoPaths(s.getSeries(), seriesBasePaths.get(s.getSeries().getID()));

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
				String gc  = rs.getString(DatabaseStructure.COL_GRPS_COLOR);
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

	public void fillCoverCache(ICoverCache coverCache, boolean loadAll) {
		try
		{
			if (loadAll)
			{
				CCSQLStatement stmt = stmts.selectCoversFullStatement;
				stmt.clearParameters();

				CCSQLResultSet rs = stmt.executeQuery(this);

				while (rs.next()) {
					CCUUID id     = CCUUID.parse(rs.getString(DatabaseStructure.COL_CVRS_ID));
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
					CCUUID id     = CCUUID.parse(rs.getString(DatabaseStructure.COL_CVRS_ID));
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

	public byte[] getCoverPreviewOrNull(CCUUID cid) {
		try {
			CCSQLStatement stmt = stmts.selectSingleCoverStatement;
			stmt.clearParameters();
			stmt.setStr(DatabaseStructure.COL_CVRS_ID, cid.toString());

			CCSQLResultSet rs = stmt.executeQuery(this);

			byte[] pv = null;
			if (rs.next()) {
				pv = rs.getBlob(DatabaseStructure.COL_CVRS_PREVIEW);
			}

			rs.close();

			return pv;
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
			return null;
		}
	}

	private void fillSeries(CCSeries ser) {
		try {
			CCSQLStatement stmt = stmts.selectSeasonTabStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_SEAS_SERIESID, ser.getID().toString());
			
			CCSQLResultSet rs = stmt.executeQuery(this);

			ser.beginUpdating();

			while (rs.next()) {
				ser.directlyInsertSeason(createSeasonFromDatabase(rs, ser, true));
			}

			ser.abortUpdating();
			ser.enforceOrder();

			rs.close();

		} catch (SQLException | CCFormatException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}

	private void fillSeason(CCSeason se) {
		try {
			CCSQLStatement stmt = stmts.selectEpisodeTabStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_EPIS_SEASONID, se.getID().toString());
			
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

	public void removeFromMovies(CCUUID id) {
		try {
			CCSQLStatement stmt = stmts.deleteMovieTabStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_MOV_ID, id.toString());

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}

	public void removeFromSeries(CCUUID id) {
		try {
			CCSQLStatement stmt = stmts.deleteSeriesTabStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_SER_ID, id.toString());

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}
	
	public void removeFromSeasons(CCUUID seasonID) {
		try {
			CCSQLStatement stmt = stmts.deleteSeasonTabStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_SEAS_ID, seasonID.toString());

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}
	
	public void removeFromEpisodes(CCUUID id) {
		try {
			CCSQLStatement stmt = stmts.deleteEpisodeTabStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_EPIS_ID, id.toString());

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
		var duuid = UUID.randomUUID().toString();

		writeInformationToDB(DatabaseStructure.INFOKEY_DUUID, duuid);

		// the user-data and the history database are bound to the main DUUID - leaving the old binding
		// behind makes validateUserDataBinding() abort on the next start
		writeUserDataInformationToDB(DatabaseStructure.INFOKEY_MAINDB_DUUID, duuid);
		if (_historyDb.isConnected()) _historyDb.writeInfo(DatabaseStructure.INFOKEY_DUUID, duuid);
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

	public String readUserDataInformationFromDB(CCSQLKVKey key, String defaultValue) {
		try {
			String value;

			CCSQLStatement stmt = stmts.readUserDataInfoKeyStatement;
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

	public void writeUserDataInformationToDB(CCSQLKVKey key, String value) {
		try {
			CCSQLStatement stmt = stmts.writeUserDataInfoKeyStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_INFO_KEY, key.Key);
			stmt.setStr(DatabaseStructure.COL_INFO_VALUE, value);

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}

	/**
	 * Creates the per-user database from scratch when there is none yet - the case for someone who
	 * only received the shared {@code ClipCornDB.db} (and its covers) from a publisher.
	 */
	@SuppressWarnings("nls")
	private void ensureUserDataDatabase() throws Exception {
		if (db.querySingleIntSQL("SELECT COUNT(*) FROM userdata.sqlite_master WHERE type='table' AND name='INFO'", 0) > 0) return;

		CCLog.addWarning("No user-data database found - creating an empty one for main database " + readMainDUUIDDirect());

		for (var tab : DatabaseStructure.TABLES_USERDATA) {
			var sql = SQLBuilder.createSchema(tab).build(this::createPreparedStatement, new ArrayList<>());
			sql.execute();
			sql.tryClose();
		}

		writeInitialUserDataInfo();
	}

	/** Reads the main DUUID without the prepared statements, which are not built yet on this path. */
	@SuppressWarnings("nls")
	private String readMainDUUIDDirect() throws SQLException {
		var duuid = db.querySingleStringSQLThrow("SELECT IVALUE FROM main.INFO WHERE IKEY='" + DatabaseStructure.INFOKEY_DUUID.Key + "'", 0);
		if (duuid != null) return duuid;

		duuid = UUID.randomUUID().toString();
		db.executeSQLThrow("INSERT OR REPLACE INTO main.INFO ([IKEY],[IVALUE]) VALUES ('" + DatabaseStructure.INFOKEY_DUUID.Key + "','" + duuid + "')");
		return duuid;
	}

	@SuppressWarnings("nls")
	private void writeInitialUserDataInfo() throws SQLException {
		var values = new LinkedHashMap<CCSQLKVKey, String>();
		values.put(DatabaseStructure.INFOKEY_DBVERSION,      Main.USERDATA_DBVERSION);
		values.put(DatabaseStructure.INFOKEY_DATE,           CCDate.getCurrentDate().toStringSQL());
		values.put(DatabaseStructure.INFOKEY_TIME,           CCTime.getCurrentTime().toStringSQL());
		values.put(DatabaseStructure.INFOKEY_USERNAME,       ApplicationHelper.getCurrentUsername());
		values.put(DatabaseStructure.INFOKEY_DUUID,          UUID.randomUUID().toString());
		values.put(DatabaseStructure.INFOKEY_HISTORY,        "0");
		values.put(DatabaseStructure.INFOKEY_MAINDB_DUUID,   readMainDUUIDDirect());
		values.put(DatabaseStructure.INFOKEY_VERSION_MAINDB, Main.DBVERSION);

		try (PreparedStatement ps = db.createPreparedStatement("INSERT OR REPLACE INTO userdata.INFO ([IKEY], [IVALUE]) VALUES (?, ?)")) {
			for (var e : values.entrySet()) {
				ps.setString(1, e.getKey().Key);
				ps.setString(2, e.getValue());
				ps.executeUpdate();
			}
		}
	}

	/**
	 * The user-data database is bound to exactly one main database. Continuing with a mismatched
	 * pair would silently attach one user's ratings to another collection.
	 */
	@SuppressWarnings("nls")
	private boolean validateUserDataBinding() {
		String bound = readUserDataInformationFromDB(DatabaseStructure.INFOKEY_MAINDB_DUUID, null);
		if (bound == null) return true; // not yet written (fresh user-data db)

		String actual = getInformation_DUUID();
		if (Str.equals(bound, actual)) return true;

		CCLog.addFatalError(LocaleBundle.getFormattedString("LogMessage.UserDataDUUIDMismatch", bound, actual, actual));
		return false;
	}

	private boolean upgradeUserDataDatabase() {
		var referror = new RefParam<String>();
		if (userDataUpgrader.tryUpgrade(referror)) return true;

		CCLog.addFatalError(referror.Value);
		return false;
	}

	/**
	 * The user-data database records the main-database version it was last used with. A main database
	 * that is older than that record was replaced by a file from an outdated installation - migrating
	 * it again would replay migrations whose result the user database already contains.
	 */
	@SuppressWarnings("nls")
	private boolean validateUserDataMainVersion() throws SQLException {
		if (db.querySingleIntSQLThrow("SELECT COUNT(*) FROM userdata.sqlite_master WHERE type='table' AND name='INFO'", 0) == 0) return true;

		Integer recorded = parseDBVersion(db.querySingleStringSQLThrow("SELECT IVALUE FROM userdata.INFO WHERE IKEY='" + DatabaseStructure.INFOKEY_VERSION_MAINDB.Key + "'", 0));
		Integer actual   = parseDBVersion(db.querySingleStringSQLThrow("SELECT IVALUE FROM main.INFO WHERE IKEY='" + DatabaseStructure.INFOKEY_DBVERSION.Key + "'", 0));

		if (recorded == null || actual == null) return true;
		if (recorded <= actual) return true;

		CCLog.addFatalError(LocaleBundle.getFormattedString("LogMessage.UserDataMainVersionMismatch", recorded, actual));
		return false;
	}

	/** Keeps VERSION_MAINDB current - after a migration here, or after a sync brought in an already-migrated main database. */
	private void updateUserDataMainVersion() {
		if (_readonly) return;

		String actual = getInformation_DBVersion();
		if (Str.equals(actual, readUserDataInformationFromDB(DatabaseStructure.INFOKEY_VERSION_MAINDB, null))) return;

		writeUserDataInformationToDB(DatabaseStructure.INFOKEY_VERSION_MAINDB, actual);
	}

	private static Integer parseDBVersion(String value) {
		if (value == null) return null;
		try {
			return Integer.valueOf(value.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * The triggers on `main.*` travel with the shared database, so after a sync they reflect the
	 * publisher's history setting rather than this installation's.
	 */
	@SuppressWarnings("nls")
	private void healHistoryTrigger() {
		if (_readonly) return;

		try {
			var active = _history.isHistoryActive();

			var referror = new RefParam<String>();
			if (_history.testTrigger(active, referror)) return;

			if (active) {
				CCLog.addInformation("Recreating missing history trigger: " + referror.Value);
				_history.enableTrigger();
			} else {
				CCLog.addInformation("Removing unexpected history trigger: " + referror.Value);
				_history.disableTrigger();
			}
		} catch (Exception e) {
			CCLog.addError("Could not repair the history trigger", e);
		}
	}

	public Map<String, String> readAllProperties() {
		Map<String, String> result = new HashMap<>();

		try {
			CCSQLStatement stmt = stmts.readAllPropertiesStatement;
			stmt.clearParameters();

			CCSQLResultSet rs = stmt.executeQuery(this);
			while (rs.next()) {
				result.put(rs.getStringDirect(1), rs.getStringDirect(2));
			}
			rs.close();
		} catch (SQLException e) {
			// missing PROPERTIES table (old / external compare DB) ends up here -> empty map -> defaults
			CCLog.addError(e);
		}

		return result;
	}

	public void writeProperty(String key, String value) {
		if (_readonly) return;
		if (!db.isConnected()) return;

		try {
			CCSQLStatement stmt = stmts.writePropertyKeyStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_PROP_KEY,          key);
			stmt.setStr(DatabaseStructure.COL_PROP_VALUE,        value);
			stmt.setStr(DatabaseStructure.COL_PROP_LAST_CHANGED, CCDateTime.getCurrentDateTime().toStringSQL());

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

	public void addGroup(String name, int order, CCHexColor color, boolean doSerialize, String parent, boolean visible) {
		try {
			CCSQLStatement stmt = stmts.insertGroupStatement;
			stmt.clearParameters();

			stmt.setStr(DatabaseStructure.COL_GRPS_NAME,      name);
			stmt.setInt(DatabaseStructure.COL_GRPS_ORDER,     order);
			stmt.setStr(DatabaseStructure.COL_GRPS_COLOR,     color.getHex());
			stmt.setBoo(DatabaseStructure.COL_GRPS_SERIALIZE, doSerialize);
			stmt.setStr(DatabaseStructure.COL_GRPS_PARENT,    parent);
			stmt.setBoo(DatabaseStructure.COL_GRPS_VISIBLE,   visible);

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}

	public void updateGroup(String name, int order, CCHexColor color, boolean doSerialize, String parent, boolean visible) {
		try {
			CCSQLStatement stmt = stmts.updateGroupStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_GRPS_ORDER,     order);
			stmt.setStr(DatabaseStructure.COL_GRPS_COLOR,     color.getHex());
			stmt.setBoo(DatabaseStructure.COL_GRPS_SERIALIZE, doSerialize);
			stmt.setStr(DatabaseStructure.COL_GRPS_PARENT,    parent);
			stmt.setBoo(DatabaseStructure.COL_GRPS_VISIBLE,   visible);

			stmt.setStr(DatabaseStructure.COL_GRPS_NAME,      name);

			stmt.executeUpdate();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
	}

	public List<Tuple3<Integer, String, String>> getCustomFilterList() {
		List<Tuple3<Integer, String, String>> result = new ArrayList<>();
		try {
			CCSQLStatement stmt = stmts.selectFiltersStatement;
			stmt.clearParameters();

			CCSQLResultSet rs = stmt.executeQuery(this);

			while (rs.next()) {
				int id        = rs.getInt(DatabaseStructure.COL_FILT_ID);
				String name   = rs.getString(DatabaseStructure.COL_FILT_NAME);
				String def    = rs.getString(DatabaseStructure.COL_FILT_DEFINITION);

				result.add(Tuple3.Create(id, name, def));
			}

			rs.close();
		} catch (SQLException | SQLWrapperException e) {
			CCLog.addError(e);
		}
		return result;
	}

	public void clearCustomFilters() {
		try {
			CCSQLStatement stmt = stmts.removeAllFiltersStatement;
			stmt.clearParameters();

			stmt.executeUpdate();
		} catch (SQLException e) {
			CCLog.addError(e);
		}
	}

	public void insertCustomFilter(int id, int sort, String name, String definition) {
		try {
			CCSQLStatement stmt = stmts.insertFilterStatement;
			stmt.clearParameters();

			stmt.setInt(DatabaseStructure.COL_FILT_ID,         id);
			stmt.setInt(DatabaseStructure.COL_FILT_SORT,       sort);
			stmt.setStr(DatabaseStructure.COL_FILT_NAME,       name);
			stmt.setStr(DatabaseStructure.COL_FILT_DEFINITION, definition);

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

				stmt.setStr(DatabaseStructure.COL_CVRS_ID,          cce.ID.toString());
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

			stmt.setStr(DatabaseStructure.COL_CVRS_ID, cce.ID.toString());

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

	public boolean supportsDateType() {
		return db.supportsDateType();
	}

	public PublicDatabaseInterface getInternalDatabaseAccess() {
		return db;
	}

	public CCDatabaseHistory getHistory() {
		return _history;
	}

	public CCHistoryDatabase getHistoryDatabase() {
		return _historyDb;
	}

	public int getHistoryCount() {
		syncHistoryToHistoryDb();
		return _historyDb.getHistoryCount();
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

	public List<String[]> queryHistory(CCDateTime start, Opt<Integer> limit, String idfilter) {
		syncHistoryToHistoryDb();
		return _historyDb.queryHistory(start, limit, idfilter);
	}

	@SuppressWarnings("nls")
	public synchronized void syncHistoryToHistoryDb() {
		if (!_historyDb.isConnected()) return;
		if (!db.isConnected()) return;
		if (_readonly) return;

		try {
			int total = 0;
			for (var tab : new String[] { DatabaseStructure.TAB_HISTORY.qualifiedName(), DatabaseStructure.TAB_UD_HISTORY.qualifiedName() }) {
				total += drainHistoryTable(tab);
			}

			if (total == 0) CCLog.addInformation("Skipping sync of history - nothing to do");
			else            CCLog.addInformation("Synced " + total + " history rows to history DB");

		} catch (SQLException e) {
			CCLog.addError("Failed to sync history to history DB", e);
		}
	}

	@SuppressWarnings("nls")
	private int drainHistoryTable(String table) throws SQLException {
		// Step 1: Read all current rows with their rowids from the staging table
		List<Object[]> rows = db.querySQL(
				"SELECT rowid, [TABLE], [ID], [DATE], [ACTION], [FIELD], [OLD], [NEW] FROM " + table, 8);

		if (rows.isEmpty()) return 0;

		// Step 2: Insert into history DB (in a transaction)
		try {
			List<Object[]> insertrows = new ArrayList<>(rows.size());
			for (Object[] row : rows) {
				insertrows.add(new Object[] { row[1], row[2], row[3], row[4], row[5], row[6], row[7] });
			}
			_historyDb.insertHistoryRows(insertrows);
		} catch (Exception e) {
			CCLog.addError("Failed to insert history rows into history DB, rows remain in " + table, e);
			return 0;
		}

		// Step 3: Delete only the rows we copied, by rowid (batch in chunks of 500)
		List<Long> rowids = new ArrayList<>();
		for (Object[] row : rows) {
			Object rid = row[0];
			if (rid instanceof Long l)    rowids.add(l);
			else if (rid instanceof Integer i) rowids.add((long) i);
			else                          rowids.add(Long.parseLong(rid.toString()));
		}

		for (int i = 0; i < rowids.size(); i += 500) {
			int end = Math.min(i + 500, rowids.size());
			StringBuilder deleteSQL = new StringBuilder("DELETE FROM " + table + " WHERE rowid IN (");
			for (int j = i; j < end; j++) {
				if (j > i) deleteSQL.append(",");
				deleteSQL.append(rowids.get(j));
			}
			deleteSQL.append(")");
			db.executeSQLThrow(deleteSQL.toString());
		}

		return rows.size();
	}

	public boolean isFirstLaunch() {
		return this.firstLaunch;
	}
}
