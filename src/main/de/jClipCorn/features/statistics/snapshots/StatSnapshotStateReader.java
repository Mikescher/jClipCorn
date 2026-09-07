package de.jClipCorn.features.statistics.snapshots;

import de.jClipCorn.database.driver.PublicDatabaseInterface;
import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.features.log.CCLog;

import java.sql.SQLException;
import java.util.*;

/**
 * Reads the current state of every tracked element straight out of the database.
 *
 * Going through SQL rather than a loaded CCMovieList serves both hosts with one implementation - the
 * migration has no movie list at all - and it keeps the values in exactly the representation the change
 * archive records, so the replay never has to translate between two of them.
 */
@SuppressWarnings("nls")
public final class StatSnapshotStateReader {
	private StatSnapshotStateReader() { throw new InstantiationError(); }

	public static Map<StatClass, Map<String, StatSnapshotRawRow>> readCurrentState(PublicDatabaseInterface db) throws SQLException {
		boolean hasUserData = hasUserDataTables(db);

		Map<StatClass, Map<String, StatSnapshotRawRow>> res = new EnumMap<>(StatClass.class);
		for (StatClass cls : StatClass.values()) res.put(cls, readClass(db, cls, hasUserData));

		return res;
	}

	private static Map<String, StatSnapshotRawRow> readClass(PublicDatabaseInterface db, StatClass cls, boolean hasUserData) throws SQLException {
		List<String> mainCols = new ArrayList<>();
		List<String> userCols = new ArrayList<>();

		for (String c : StatSnapshotDimensions.TRACKED_COLUMNS.get(cls)) {
			if (isUserDataColumn(c)) userCols.add(c); else mainCols.add(c);
		}

		boolean joinUser = !userCols.isEmpty() && hasUserData;

		StringBuilder sql = new StringBuilder("SELECT m.ID");
		for (String c : mainCols) sql.append(", m.[").append(c).append(']');
		if (joinUser) for (String c : userCols) sql.append(", u.[").append(c).append(']');

		sql.append(" FROM main.").append(cls.TableName).append(" m");
		if (joinUser) sql.append(" LEFT JOIN userdata.").append(cls.TableName).append(" u ON u.ID = m.ID");

		int colCount = 1 + mainCols.size() + (joinUser ? userCols.size() : 0);

		Map<String, StatSnapshotRawRow> res = new HashMap<>();

		for (Object[] o : db.querySQL(sql.toString(), colCount)) {
			String id = str(o[0]);
			if (id == null) continue;

			StatSnapshotRawRow row = new StatSnapshotRawRow(cls);

			int i = 1;
			for (String c : mainCols) row.set(c, str(o[i++]));

			for (String c : userCols) {
				// a missing (sparse) user row stands for the defaults - the same substitution the history
				// triggers make when they log the creation or removal of one
				String v = joinUser ? str(o[i++]) : null;
				row.set(c, (v != null) ? v : CCDatabaseHistory.sparseDefault(c));
			}

			res.put(id, row);
		}

		return res;
	}

	/** ClipCornUserData.db is recreated after the migration chain, so during a migration it can be absent. */
	private static boolean hasUserDataTables(PublicDatabaseInterface db) {
		try {
			return !db.querySQL("SELECT name FROM userdata.sqlite_master WHERE type='table' AND name='MOVIES'", 1).isEmpty();
		} catch (SQLException e) {
			CCLog.addWarning("[SNAPSHOT] Cannot inspect the user database, its columns fall back to their defaults", e);
			return false;
		}
	}

	public static boolean isUserDataColumn(String column) {
		// the sparse defaults exist exactly for the userdata columns
		return CCDatabaseHistory.sparseDefault(column) != null;
	}

	static String str(Object o) {
		return (o == null) ? null : o.toString();
	}
}
