package de.jClipCorn.features.statistics.snapshots;

import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.InternationalDateTimeFormatHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Plain-JDBC access to main.STATSNAPSHOTS, shared by the migration (which has no prepared statements yet)
 * and by the live application.
 */
@SuppressWarnings("nls")
public final class StatSnapshotSQL {
	private StatSnapshotSQL() { throw new InstantiationError(); }

	public static final String COLUMNS =
			"[DATE],EXACT," +
			"MOV_COUNT,MOV_BYTES,MOV_MINUTES," +
			"SER_COUNT,SEA_COUNT," +
			"EPI_COUNT,EPI_BYTES,EPI_MINUTES," +
			"MOV_HISTOGRAMS,SER_HISTOGRAMS,SEA_HISTOGRAMS,EPI_HISTOGRAMS";

	public static final String INSERT_SQL = "INSERT INTO main.STATSNAPSHOTS (" + COLUMNS + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	public static final String SELECT_ALL_SQL = "SELECT " + COLUMNS + " FROM main.STATSNAPSHOTS ORDER BY [DATE] ASC";

	public static final String SELECT_LAST_SQL = "SELECT " + COLUMNS + " FROM main.STATSNAPSHOTS ORDER BY [DATE] DESC LIMIT 1";

	public static final String UPSERT_SQL =
			"INSERT INTO main.STATSNAPSHOTS (" + COLUMNS + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)" +
			" ON CONFLICT([DATE]) DO UPDATE SET" +
			" EXACT=excluded.EXACT," +
			" MOV_COUNT=excluded.MOV_COUNT,MOV_BYTES=excluded.MOV_BYTES,MOV_MINUTES=excluded.MOV_MINUTES," +
			" SER_COUNT=excluded.SER_COUNT,SEA_COUNT=excluded.SEA_COUNT," +
			" EPI_COUNT=excluded.EPI_COUNT,EPI_BYTES=excluded.EPI_BYTES,EPI_MINUTES=excluded.EPI_MINUTES," +
			" MOV_HISTOGRAMS=excluded.MOV_HISTOGRAMS,SER_HISTOGRAMS=excluded.SER_HISTOGRAMS," +
			" SEA_HISTOGRAMS=excluded.SEA_HISTOGRAMS,EPI_HISTOGRAMS=excluded.EPI_HISTOGRAMS";

	public static void bind(PreparedStatement ps, CCStatSnapshot s) throws SQLException {
		ps.setString(1,  s.Date.toStringSQL());
		ps.setInt   (2,  s.Exact ? 1 : 0);

		ps.setInt   (3,  s.count(StatClass.MOVIE));
		ps.setLong  (4,  s.bytes(StatClass.MOVIE));
		ps.setInt   (5,  s.minutes(StatClass.MOVIE));

		ps.setInt   (6,  s.count(StatClass.SERIES));
		ps.setInt   (7,  s.count(StatClass.SEASON));

		ps.setInt   (8,  s.count(StatClass.EPISODE));
		ps.setLong  (9,  s.bytes(StatClass.EPISODE));
		ps.setInt   (10, s.minutes(StatClass.EPISODE));

		ps.setString(11, s.histogramsToJson(StatClass.MOVIE));
		ps.setString(12, s.histogramsToJson(StatClass.SERIES));
		ps.setString(13, s.histogramsToJson(StatClass.SEASON));
		ps.setString(14, s.histogramsToJson(StatClass.EPISODE));
	}

	public static CCStatSnapshot read(ResultSet rs) throws SQLException {
		Map<StatClass, Integer> count   = new EnumMap<>(StatClass.class);
		Map<StatClass, Long>    bytes   = new EnumMap<>(StatClass.class);
		Map<StatClass, Integer> minutes = new EnumMap<>(StatClass.class);

		for (StatClass c : StatClass.values()) { count.put(c, 0); bytes.put(c, 0L); minutes.put(c, 0); }

		count  .put(StatClass.MOVIE,   rs.getInt(3));
		bytes  .put(StatClass.MOVIE,   rs.getLong(4));
		minutes.put(StatClass.MOVIE,   rs.getInt(5));
		count  .put(StatClass.SERIES,  rs.getInt(6));
		count  .put(StatClass.SEASON,  rs.getInt(7));
		count  .put(StatClass.EPISODE, rs.getInt(8));
		bytes  .put(StatClass.EPISODE, rs.getLong(9));
		minutes.put(StatClass.EPISODE, rs.getInt(10));

		Map<StatClass, Map<String, Map<String, Integer>>> hist = new EnumMap<>(StatClass.class);
		hist.put(StatClass.MOVIE,   CCStatSnapshot.histogramsFromJson(rs.getString(11)));
		hist.put(StatClass.SERIES,  CCStatSnapshot.histogramsFromJson(rs.getString(12)));
		hist.put(StatClass.SEASON,  CCStatSnapshot.histogramsFromJson(rs.getString(13)));
		hist.put(StatClass.EPISODE, CCStatSnapshot.histogramsFromJson(rs.getString(14)));

		CCDate date = parseSqlDate(rs.getString(1), CCDate.getMinimumDate());

		return new CCStatSnapshot(date, rs.getInt(2) != 0, count, bytes, minutes, hist);
	}

	/** DATE columns are written with CCDate.toStringSQL(), which is not the CCDate.deserialize() format. */
	public static CCDate parseSqlDate(String raw, CCDate fallback) {
		return CCDate.parseOrDefault(raw, InternationalDateTimeFormatHelper.DATE_SQL, fallback);
	}
}
