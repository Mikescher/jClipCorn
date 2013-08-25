package de.jClipCorn.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.database.util.Statements;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.CCDate;

public class CCDatabase extends DerbyDatabase {
	public final static String TAB_MAIN = "MOVIES"; //$NON-NLS-1$
	public final static String TAB_SEASONS = "SEASONS"; //$NON-NLS-1$
	public final static String TAB_EPISODES = "EPISODES"; //$NON-NLS-1$

	private final static String XML_NAME = "database/ClipCornSchema.xml"; //$NON-NLS-1$

	public final static String TAB_MAIN_COLUMN_LOCALID = "LOCALID"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_NAME = "NAME"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_VIEWED = "VIEWED"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_ZYKLUS = "ZYKLUS"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_ZYKLUSNUMBER = "ZYKLUSNUMBER"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_QUALITY = "QUALITY"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_LANGUAGE = "LANGUAGE"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_GENRE = "GENRE"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_LENGTH = "LENGTH"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_ADDDATE = "ADDDATE"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_ONLINESCORE = "ONLINESCORE"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_FSK = "FSK"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_FORMAT = "FORMAT"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_MOVIEYEAR = "MOVIEYEAR"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_FILESIZE = "FILESIZE"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_TAGS = "TAGS"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_PART_1 = "PART1"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_PART_2 = "PART2"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_PART_3 = "PART3"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_PART_4 = "PART4"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_PART_5 = "PART5"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_PART_6 = "PART6"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_SCORE = "SCORE"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_COVER = "COVERNAME"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_TYPE = "TYPE"; //$NON-NLS-1$
	public final static String TAB_MAIN_COLUMN_SERIES_ID = "SERIESID"; //$NON-NLS-1$

	public final static String TAB_SEASONS_COLUMN_SEASONID = "SEASONID"; //$NON-NLS-1$
	public final static String TAB_SEASONS_COLUMN_SERIESID = "SERIESID"; //$NON-NLS-1$
	public final static String TAB_SEASONS_COLUMN_NAME = "NAME"; //$NON-NLS-1$
	public final static String TAB_SEASONS_COLUMN_YEAR = "SEASONYEAR"; //$NON-NLS-1$
	public final static String TAB_SEASONS_COLUMN_COVERNAME = "COVERNAME"; //$NON-NLS-1$

	public final static String TAB_EPISODES_COLUMN_LOCALID = "LOCALID"; //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_SEASONID = "SEASONID"; //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_EPISODE = "EPISODE"; //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_NAME = "NAME"; //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_VIEWED = "VIEWED"; //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_QUALITY = "QUALITY"; //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_LENGTH = "LENGTH"; //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_FORMAT = "FORMAT"; //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_FILESIZE = "FILESIZE"; //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_PART_1 = "PART1"; //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_TAGS = "TAGS"; //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_LASTVIEWED = "LASTVIEWED"; //$NON-NLS-1$
	public final static String TAB_EPISODES_COLUMN_ADDDATE = "ADDDATE"; //$NON-NLS-1$

	private String databasePath;

	public CCDatabase() {
		super();
	}

	public boolean tryconnect(String path) {
		if (!connect(path)) {
			if (!create(path)) {
				return false;
			} else {
				CCLog.addWarning(LocaleBundle.getString("LogMessage.FailedDBConnect")); //$NON-NLS-1$
				return true;
			}
		} else {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.DBConnect", path)); //$NON-NLS-1$
			return true;
		}
	}

	private boolean connect(String dbpath) {
		try {
			establishDBConnection(dbpath);
			databasePath = dbpath;
			Statements.intialize(this);
			return true;
		} catch (SQLException e) {
			lastError = e;
			return false;
		}
	}
	
	public void disconnect(boolean cleanshutdown) {
		try {
			if (isConnected()) {
				shutdownStatements();
				closeDBConnection(getDBPath(), cleanshutdown);
			}
		} catch (SQLException e) {
			CCLog.addError(LocaleBundle.getString("LogMessage.CouldNotDisconnectFromDB"), e); //$NON-NLS-1$
		}
	}
	
	public void reconnect() {
		if (! connect(getDBPath())) {
			CCLog.addFatalError(LocaleBundle.getString("LogMessage.CouldNotReconnectToDB"), lastError); //$NON-NLS-1$
		}
	}

	private boolean create(String dbpath) {
		boolean res = createNewDatabasefromResourceXML('/' + XML_NAME, dbpath);
		if (res) {
			databasePath = dbpath;
			Statements.intialize(this);
		}
		return res;
	}

	/**
	 * @return the ammount of Movies and Series in the Database
	 */
	public int getDBElementCount() {
		return getRowCount(TAB_MAIN);
	}

	private CCDatabaseElement createDatabaseElementFromDatabase(ResultSet rs, CCMovieList ml) throws SQLException {
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

	private CCEpisode createEpisodeFromDatabase(ResultSet rs, CCSeason se) throws SQLException {
		CCEpisode ep = new CCEpisode(se, rs.getInt(TAB_EPISODES_COLUMN_LOCALID));

		ep.beginUpdating();

		updateEpisodeFromResultSet(rs, ep);

		ep.abortUpdating();

		return ep;
	}

	private void updateEpisodeFromResultSet(ResultSet rs, CCEpisode ep) throws SQLException {
		ep.setEpisodeNumber(rs.getInt(TAB_EPISODES_COLUMN_EPISODE));
		ep.setTitle(rs.getString(TAB_EPISODES_COLUMN_NAME));
		ep.setViewed(rs.getBoolean(TAB_EPISODES_COLUMN_VIEWED));
		ep.setQuality(rs.getInt(TAB_EPISODES_COLUMN_QUALITY));
		ep.setLength(rs.getInt(TAB_EPISODES_COLUMN_LENGTH));
		ep.setFormat(rs.getInt(TAB_EPISODES_COLUMN_FORMAT));
		ep.setFilesize(rs.getLong(TAB_EPISODES_COLUMN_FILESIZE));
		ep.setPart(rs.getString(TAB_EPISODES_COLUMN_PART_1));
		ep.setAddDate(rs.getDate(TAB_EPISODES_COLUMN_ADDDATE));
		ep.setLastViewed(rs.getDate(TAB_EPISODES_COLUMN_LASTVIEWED));
		ep.setTags(rs.getShort(TAB_EPISODES_COLUMN_TAGS));
	}
	
	private void updateSeasonFromResultSet(ResultSet rs, CCSeason seas) throws SQLException {
		seas.setTitle(rs.getString(TAB_SEASONS_COLUMN_NAME));
		seas.setYear(rs.getInt(TAB_SEASONS_COLUMN_YEAR));
		seas.setCover(rs.getString(TAB_SEASONS_COLUMN_COVERNAME));
	}
	
	private void updateSeriesFromResultSet(ResultSet rs, CCSeries ser) throws SQLException {
		ser.setTitle(rs.getString(TAB_MAIN_COLUMN_NAME));
		ser.setLanguage(rs.getInt(TAB_MAIN_COLUMN_LANGUAGE));
		ser.setGenres(rs.getLong(TAB_MAIN_COLUMN_GENRE));
		ser.setOnlinescore(rs.getInt(TAB_MAIN_COLUMN_ONLINESCORE));
		ser.setFsk(rs.getInt(TAB_MAIN_COLUMN_FSK));
		ser.setScore(rs.getInt(TAB_MAIN_COLUMN_SCORE));
		ser.setCover(rs.getString(TAB_MAIN_COLUMN_COVER));
	}

	private void updateMovieFromResultSet(ResultSet rs, CCMovie mov) throws SQLException {
		mov.setTitle(rs.getString(TAB_MAIN_COLUMN_NAME));
		mov.setViewed(rs.getBoolean(TAB_MAIN_COLUMN_VIEWED));
		mov.setZyklusTitle(rs.getString(TAB_MAIN_COLUMN_ZYKLUS));
		mov.setZyklusID(rs.getInt(TAB_MAIN_COLUMN_ZYKLUSNUMBER));
		mov.setQuality(rs.getInt(TAB_MAIN_COLUMN_QUALITY));
		mov.setLanguage(rs.getInt(TAB_MAIN_COLUMN_LANGUAGE));
		mov.setGenres(rs.getLong(TAB_MAIN_COLUMN_GENRE));
		mov.setLength(rs.getInt(TAB_MAIN_COLUMN_LENGTH));
		mov.setAddDate(rs.getDate(TAB_MAIN_COLUMN_ADDDATE));
		mov.setOnlinescore(rs.getInt(TAB_MAIN_COLUMN_ONLINESCORE));
		mov.setFsk(rs.getInt(TAB_MAIN_COLUMN_FSK));
		mov.setFormat(rs.getInt(TAB_MAIN_COLUMN_FORMAT));
		mov.setYear(rs.getInt(TAB_MAIN_COLUMN_MOVIEYEAR));
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

			s.setInt(1, id);
			s.setString(2, "");
			s.setInt(3, 0);
			s.setString(4, "");
			s.setInt(5, 0);
			s.setInt(6, 0);
			s.setInt(7, 0);
			s.setInt(8, 0);
			s.setInt(9, 0);
			s.setString(10, CCDate.YEAR_MIN + "-01-01");
			s.setInt(11, 0);
			s.setInt(12, 0);
			s.setInt(13, 0);
			s.setInt(14, 0);
			s.setInt(15, 0);
			s.setInt(16, 0);
			s.setString(17, "");
			s.setString(18, "");
			s.setString(19, "");
			s.setString(20, "");
			s.setString(21, "");
			s.setString(22, "");
			s.setInt(23, 0);
			s.setString(24, "");
			s.setInt(25, CCMovieTyp.MOVIE.asInt());
			s.setInt(26, -1);

			s.executeUpdate();

			return true;
		} catch (SQLException e) {
			lastError = e;
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
			lastError = e;
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.NoNewSeasonRow", seasid, serid), e); //$NON-NLS-1$
			return false;
		}
	}
	
	@SuppressWarnings("nls")
	private boolean addEmptyEpisodeRow(int eid, int sid) {
		try {
			PreparedStatement s = Statements.addEmptyEpisodeTabStatement;
			s.clearParameters();

			s.setInt(1, eid);
			s.setInt(2, sid);
			s.setInt(3, 0);
			s.setString(4, "");
			s.setInt(5, 0);
			s.setInt(6, 0);
			
			s.setInt(7, 0);
			s.setInt(8, 0);
			s.setInt(9, 0);
			s.setString(10, "");
			s.setInt(11, 0);
			s.setString(12, CCDate.YEAR_MIN + "-01-01");
			s.setString(13, CCDate.YEAR_MIN + "-01-01");

			s.executeUpdate();

			return true;
		} catch (SQLException e) {
			lastError = e;
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

			s.setString(1, mov.getTitle());
			s.setBoolean(2, mov.isViewed());
			s.setString(3, mov.getZyklus().getTitle());
			s.setInt(4, mov.getZyklus().getNumber());
			s.setInt(5, mov.getQuality().asInt());
			s.setInt(6, mov.getLanguage().asInt());
			s.setLong(7, mov.getGenres().getAllGenres());
			s.setInt(8, mov.getLength());
			s.setString(9, mov.getAddDate().getSQLStringRepresentation());
			s.setInt(10, mov.getOnlinescore().asInt());
			s.setInt(11, mov.getFSK().asInt());
			s.setInt(12, mov.getFormat().asInt());
			s.setInt(13, mov.getYear());
			s.setLong(14, mov.getFilesize().getBytes());
			s.setShort(15, mov.getTags().asShort());
			s.setString(16, mov.getPart(0));
			s.setString(17, mov.getPart(1));
			s.setString(18, mov.getPart(2));
			s.setString(19, mov.getPart(3));
			s.setString(20, mov.getPart(4));
			s.setString(21, mov.getPart(5));
			s.setInt(22, mov.getScore().asInt());
			s.setString(23, mov.getCoverName());
			s.setInt(24, mov.getType().asInt());
			s.setInt(25, mov.getSeriesID());

			s.setInt(26, mov.getLocalID());

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

			s.setString(1, ser.getTitle());
			s.setInt(2, ser.getLanguage().asInt());
			s.setLong(3, ser.getGenres().getAllGenres());
			s.setInt(4, ser.getOnlinescore().asInt());
			s.setInt(5, ser.getFSK().asInt());
			s.setInt(6, ser.getScore().asInt());
			s.setString(7, ser.getCoverName());
			s.setInt(8, ser.getType().asInt());
			s.setInt(9, ser.getSeriesID());

			s.setInt(10, ser.getLocalID());

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

			s.setInt(1, ep.getSeason().getSeasonID());
			s.setInt(2, ep.getEpisode());
			s.setString(3, ep.getTitle());
			s.setBoolean(4, ep.isViewed());
			s.setInt(5, ep.getQuality().asInt());
			s.setInt(6, ep.getLength());
			s.setInt(7, ep.getFormat().asInt());
			s.setLong(8, ep.getFilesize().getBytes());
			s.setString(9, ep.getPart());
			s.setShort(10, ep.getTags().asShort());
			s.setString(11, ep.getLastViewed().getSQLStringRepresentation());
			s.setString(12, ep.getAddDate().getSQLStringRepresentation());
			
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
		} catch (SQLException e) {
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
		} catch (SQLException e) {
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
		} catch (SQLException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateEpisode", epi.getTitle(), epi.getLocalID()), e);
			return false;
		}
	}

	public void fillMovieList(CCMovieList ml) {
		try {
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
			
		} catch (SQLException e) {
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
			
		} catch (SQLException e) {
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

	private void shutdownStatements() {
		Statements.shutdown();
	}
}
