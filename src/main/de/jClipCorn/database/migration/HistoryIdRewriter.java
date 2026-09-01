package de.jClipCorn.database.migration;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.datatypes.CCUUID;

import java.sql.*;
import java.util.*;

/**
 * Rewrites the integer ids in a HISTORY table to the UUIDs of the v34-&gt;v35 migration.
 *
 * Used for both HISTORY tables: the staging table inside the main database and the archive in
 * ClipCornHistory.db. Because {@link CCUUID#deriveMigrationUUID} is a pure function, rows that
 * reference long-deleted entities are mapped correctly too and nothing has to be kept afterwards.
 */
@SuppressWarnings("nls")
public class HistoryIdRewriter {

	private static final String MAP_TABLE = "MIGRATION_HIST_ID_MAP";

	private static final List<String> ENTITY_TABLES = List.of("MOVIES", "SERIES", "SEASONS", "EPISODES", "COVERS");

	/** FIELD -> table whose id the OLD/NEW values of that field contain */
	private static final Map<String, String> REFERENCE_FIELDS = Map.of(
			"SERIESID", "SERIES",
			"SEASONID", "SEASONS",
			"COVERID",  "COVERS");

	public static void rewrite(Connection conn, String table) throws SQLException {
		// these were written on every single entity creation and are meaningless without the counters
		int dropped = executeUpdate(conn, "DELETE FROM [" + table + "] WHERE [TABLE]='INFO' AND [ID] IN ('LAST_ID','LAST_COVERID')");

		execute(conn, "CREATE TABLE " + MAP_TABLE + " (TAB TEXT NOT NULL, OLDID INTEGER NOT NULL, NEWID TEXT NOT NULL, PRIMARY KEY(TAB, OLDID))");
		try {
			Map<String, Set<Integer>> ids = new LinkedHashMap<>();
			for (String t : ENTITY_TABLES) ids.put(t, new TreeSet<>());

			collect(conn, ids, "SELECT DISTINCT [TABLE], [ID] FROM [" + table + "] WHERE [TABLE] IN " + inList(ENTITY_TABLES));

			for (var f : REFERENCE_FIELDS.entrySet()) {
				collectInto(conn, ids.get(f.getValue()), "SELECT DISTINCT [OLD] FROM [" + table + "] WHERE [FIELD]='" + f.getKey() + "'");
				collectInto(conn, ids.get(f.getValue()), "SELECT DISTINCT [NEW] FROM [" + table + "] WHERE [FIELD]='" + f.getKey() + "'");
			}

			try (PreparedStatement ps = conn.prepareStatement("INSERT OR IGNORE INTO " + MAP_TABLE + " (TAB, OLDID, NEWID) VALUES (?, ?, ?)")) {
				for (var e : ids.entrySet()) {
					for (int id : e.getValue()) {
						ps.setString(1, e.getKey());
						ps.setInt(2, id);
						ps.setString(3, CCUUID.deriveMigrationUUID(e.getKey(), id).toString());
						ps.executeUpdate();
					}
				}
			}

			int rows = executeUpdate(conn,
					"UPDATE [" + table + "] SET [ID] = " + lookup("[TABLE]", "[ID]") +
					" WHERE [TABLE] IN " + inList(ENTITY_TABLES) + " AND " + isPlainInteger("[ID]"));

			for (var f : REFERENCE_FIELDS.entrySet()) {
				for (String col : List.of("[OLD]", "[NEW]")) {
					rows += executeUpdate(conn,
							"UPDATE [" + table + "] SET " + col + " = " +
							"CASE WHEN " + col + " = '-1' THEN '" + CCUUID.EMPTY + "' ELSE " + lookup("'" + f.getValue() + "'", col) + " END" +
							" WHERE [FIELD]='" + f.getKey() + "' AND (" + col + " = '-1' OR " + isPlainInteger(col) + ")");
				}
			}

			CCLog.addInformation("[UPGRADE v34 -> v35] Rewrote " + rows + " history values in " + table + " (" + dropped + " counter rows dropped)");
		} finally {
			execute(conn, "DROP TABLE " + MAP_TABLE);
		}
	}

	/**
	 * Restricts a rewrite to values that are still integers. Keeps the pass idempotent (a re-run after
	 * a crash skips the rows it already converted) and keeps {@code CAST(&lt;uuid&gt; AS INTEGER)}, which is
	 * 0, from mapping an already-converted row onto the id-0 UUID.
	 */
	private static String isPlainInteger(String expr) {
		return expr + " NOT GLOB '*[^0-9]*'";
	}

	private static String lookup(String tabExpr, String idExpr) {
		return "COALESCE((SELECT NEWID FROM " + MAP_TABLE + " WHERE TAB=" + tabExpr + " AND OLDID=CAST(" + idExpr + " AS INTEGER)), " + idExpr + ")";
	}

	private static String inList(List<String> values) {
		StringBuilder b = new StringBuilder("(");
		for (int i = 0; i < values.size(); i++) {
			if (i > 0) b.append(",");
			b.append("'").append(values.get(i)).append("'");
		}
		return b.append(")").toString();
	}

	private static void collect(Connection conn, Map<String, Set<Integer>> target, String sql) throws SQLException {
		try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sql)) {
			while (rs.next()) {
				Set<Integer> set = target.get(rs.getString(1));
				if (set == null) continue;
				addIfNumeric(set, rs.getString(2));
			}
		}
	}

	private static void collectInto(Connection conn, Set<Integer> target, String sql) throws SQLException {
		try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sql)) {
			while (rs.next()) addIfNumeric(target, rs.getString(1));
		}
	}

	private static void addIfNumeric(Set<Integer> target, String value) {
		if (value == null) return;
		try {
			int v = Integer.parseInt(value.trim());
			if (v >= 0) target.add(v);
		} catch (NumberFormatException e) {
			// non-numeric ids (INFO keys, group names, already-migrated uuids) stay untouched
		}
	}

	private static void execute(Connection conn, String sql) throws SQLException {
		try (Statement s = conn.createStatement()) { s.execute(sql); }
	}

	private static int executeUpdate(Connection conn, String sql) throws SQLException {
		try (Statement s = conn.createStatement()) { return s.executeUpdate(sql); }
	}
}
