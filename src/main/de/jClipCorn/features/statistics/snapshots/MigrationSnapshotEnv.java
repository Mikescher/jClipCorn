package de.jClipCorn.features.statistics.snapshots;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.lambda.Func1to0;

import java.sql.*;
import java.util.*;

/**
 * The rebuild environment for a schema migration - no CCMovieList, no prepared statements, and a change
 * archive that is not connected yet.
 *
 * {@code CCHistoryDatabase.tryconnect} only runs after the whole migration chain, and ATTACH is illegal
 * inside the migration's transaction, so the archive is read through a separate short-lived connection.
 * That connection deliberately does not take the FileLockManager lock - the real one is taken later in
 * the same startup.
 */
@SuppressWarnings("nls")
public class MigrationSnapshotEnv implements IStatSnapshotEnv {

	private static final String HISTORY_DB_FILENAME = "ClipCornHistory.db";
	private static final String JDBC_PROTOCOL       = "jdbc:sqlite:";

	/** The archive layout the replay understands - v1 still holds pre-UUID integer ids. */
	private static final String SUPPORTED_HISTORY_VERSION = "2";

	private final GenericDatabase _db;
	private final FSPath          _databaseDirectory;
	private final String          _databaseName;

	public MigrationSnapshotEnv(GenericDatabase db, FSPath databaseDirectory, String databaseName) {
		_db                = db;
		_databaseDirectory = databaseDirectory;
		_databaseName      = databaseName;
	}

	@Override
	public Map<StatClass, Map<String, StatSnapshotRawRow>> readCurrentState() throws Exception {
		return StatSnapshotStateReader.readCurrentState(_db);
	}

	@Override
	public void streamHistory(Func1to0<String[]> consumer) throws Exception {
		List<String[]> staging = readStagingRows();

		FSPath hist = historyPath();
		if (!hist.exists()) { emitAll(staging, consumer); return; }

		try (Connection c = DriverManager.getConnection(JDBC_PROTOCOL + hist)) {

			String version = readHistoryVersion(c);
			if (!SUPPORTED_HISTORY_VERSION.equals(version)) {
				// the archive migrates to the current layout on connect, which only happens after the whole
				// main-database chain - a jump across many versions can therefore still find the old one here
				CCLog.addWarning("[SNAPSHOT] The change archive is at version " + version + " - the statistics " +
				                 "time-series is estimated from the add-dates instead of replayed");
				return;
			}

			ensureDateIndex(c);

			// the staging rows are newer than everything in the archive - the drain moves them in order -
			// so emitting them first keeps the whole stream newest-first
			emitAll(staging, consumer);

			try (Statement s = c.createStatement(); ResultSet rs = s.executeQuery(StatSnapshotHistoryFilter.selectSQL())) {
				while (rs.next()) consumer.invoke(readRow(rs));
			}
		}
	}

	@Override
	public String earliestHistoryTimestamp() throws Exception {
		String earliest = null;
		for (String[] r : readStagingRows()) if (earliest == null || r[2].compareTo(earliest) < 0) earliest = r[2];

		FSPath hist = historyPath();
		if (!hist.exists()) return earliest;

		try (Connection c = DriverManager.getConnection(JDBC_PROTOCOL + hist)) {
			if (!SUPPORTED_HISTORY_VERSION.equals(readHistoryVersion(c))) return null;

			try (Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT MIN([DATE]) FROM [HISTORY]")) {
				String v = rs.next() ? rs.getString(1) : null;
				if (v != null && (earliest == null || v.compareTo(earliest) < 0)) earliest = v;
			}
		}

		return earliest;
	}

	@Override
	public void writeSnapshots(List<CCStatSnapshot> rows) throws Exception {
		// already inside the migration's transaction
		_db.executeSQLThrow("DELETE FROM main.STATSNAPSHOTS");

		try (PreparedStatement ps = _db.createPreparedStatement(StatSnapshotSQL.INSERT_SQL)) {
			for (CCStatSnapshot s : rows) {
				StatSnapshotSQL.bind(ps, s);
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}

	// -----------------------------------------------------------------------------------------------

	/** Rows the periodic drain has not moved into the archive yet - they live on the main connection. */
	private List<String[]> readStagingRows() throws SQLException {
		List<String[]> res = new ArrayList<>();

		for (Object[] o : _db.querySQL(stagingSQL("main"), 7)) res.add(toRow(o));

		// ATTACHing a deleted ClipCornUserData.db creates an empty one, and ensureUserDataDatabase only
		// rebuilds it after the migration chain - so this table can legitimately not be there yet
		if (hasUserDataHistory()) {
			for (Object[] o : _db.querySQL(stagingSQL("userdata"), 7)) res.add(toRow(o));
		}

		res.sort((a, b) -> b[2].compareTo(a[2]));

		return res;
	}

	private static String stagingSQL(String schema) {
		return "SELECT [TABLE],[ID],[DATE],[ACTION],[FIELD],[OLD],[NEW] FROM " + schema + ".HISTORY" +
		       " WHERE " + StatSnapshotHistoryFilter.whereClause() +
		       " ORDER BY [DATE] DESC, rowid DESC";
	}

	private boolean hasUserDataHistory() throws SQLException {
		return !_db.querySQL("SELECT name FROM userdata.sqlite_master WHERE type='table' AND name='HISTORY'", 1).isEmpty();
	}

	private static void emitAll(List<String[]> rows, Func1to0<String[]> consumer) {
		for (String[] r : rows) consumer.invoke(r);
	}

	private static String[] readRow(ResultSet rs) throws SQLException {
		String[] r = new String[7];
		for (int i = 0; i < 7; i++) r[i] = rs.getString(i + 1);
		return r;
	}

	private static String[] toRow(Object[] o) {
		String[] r = new String[7];
		for (int i = 0; i < 7; i++) r[i] = StatSnapshotStateReader.str(o[i]);
		return r;
	}

	private FSPath historyPath() {
		return _databaseDirectory.append(_databaseName, HISTORY_DB_FILENAME);
	}

	private static String readHistoryVersion(Connection c) throws SQLException {
		try (Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT IVALUE FROM INFO WHERE IKEY = 'VERSION_DB'")) {
			return rs.next() ? rs.getString(1) : null;
		}
	}

	/** Also speeds up the DATE-ranged queries of the history frame, so it is worth leaving behind. */
	private static void ensureDateIndex(Connection c) throws SQLException {
		try (Statement s = c.createStatement()) {
			s.execute("CREATE INDEX IF NOT EXISTS IDX_HISTORY_DATE ON [HISTORY]([DATE])");
		}
	}
}
