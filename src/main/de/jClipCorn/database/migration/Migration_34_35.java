package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.filesystem.FSPath;

import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Replaces the integer LOCALID of MOVIES/SERIES/SEASONS/EPISODES and the integer COVERS.ID
 * with a UUIDv7, and renames the cover files to {@code <uuid>.<ext>}.
 *
 * The new ids are derived deterministically from (table, old id) via
 * {@link CCUUID#deriveMigrationUUID}, so two installations migrating the same source database
 * end up with byte-identical ids without coordinating.
 *
 * SQLite cannot change a primary key in place, so each table is rebuilt. The new column layout is
 * derived from {@code PRAGMA table_info} rather than hardcoded, because earlier migrations moved
 * some columns to the end of their table.
 */
@SuppressWarnings("nls")
public class Migration_34_35 extends DBMigration {

	private static final String MAP_TABLE = "MIGRATION_ID_MAP";

	private static final String NO_COVER = CCUUID.EMPTY.toString();

	public Migration_34_35(GenericDatabase db, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "34";
	}

	@Override
	public String getToVersion() {
		return "35";
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return true;
	}

	@Override
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[UPGRADE v34 -> v35] Replace integer ids with UUIDv7");

		createIdMap();

		rebuildTable("MOVIES",   "LOCALID", Map.of("COVERID", "COVERS"), "");
		rebuildTable("SERIES",   "LOCALID", Map.of("COVERID", "COVERS"), "");
		rebuildTable("SEASONS",  "LOCALID", Map.of("COVERID", "COVERS", "SERIESID", "SERIES"),
				",FOREIGN KEY(SERIESID) REFERENCES SERIES(ID)");
		rebuildTable("EPISODES", "LOCALID", Map.of("SEASONID", "SEASONS"),
				",FOREIGN KEY(SEASONID) REFERENCES SEASONS(ID)");
		rebuildTable("COVERS",   "ID",      Map.of(), "");

		HistoryIdRewriter.rewrite(db.getConnection(), "HISTORY");

		db.executeSQLThrow("DROP TABLE " + MAP_TABLE);

		db.executeSQLThrow("DELETE FROM INFO WHERE IKEY IN ('LAST_ID','LAST_COVERID')");

		renameCoverFiles();

		return new ArrayList<>();
	}

	/**
	 * (table, old-int-id) -> uuid for every id that is stored anywhere, including ids that are only
	 * referenced (a dangling COVERID stays dangling instead of silently becoming "no cover").
	 */
	private void createIdMap() throws SQLException {
		db.executeSQLThrow("CREATE TABLE " + MAP_TABLE + " (TAB TEXT NOT NULL, OLDID INTEGER NOT NULL, NEWID TEXT NOT NULL, PRIMARY KEY(TAB, OLDID))");

		Map<String, List<String>> sources = new LinkedHashMap<>();
		sources.put("MOVIES",   List.of("SELECT LOCALID FROM MOVIES"));
		sources.put("SERIES",   List.of("SELECT LOCALID FROM SERIES", "SELECT SERIESID FROM SEASONS"));
		sources.put("SEASONS",  List.of("SELECT LOCALID FROM SEASONS", "SELECT SEASONID FROM EPISODES"));
		sources.put("EPISODES", List.of("SELECT LOCALID FROM EPISODES"));
		sources.put("COVERS",   List.of("SELECT ID FROM COVERS",
				"SELECT COVERID FROM MOVIES WHERE COVERID <> -1",
				"SELECT COVERID FROM SERIES WHERE COVERID <> -1",
				"SELECT COVERID FROM SEASONS WHERE COVERID <> -1"));

		for (var src : sources.entrySet()) {
			Set<Integer> ids = new TreeSet<>();
			for (String sql : src.getValue()) ids.addAll(db.querySQL(sql, 1, o -> ((Number) o[0]).intValue()));

			try (PreparedStatement ps = db.createPreparedStatement("INSERT OR IGNORE INTO " + MAP_TABLE + " (TAB, OLDID, NEWID) VALUES (?, ?, ?)")) {
				for (int id : ids) {
					ps.setString(1, src.getKey());
					ps.setInt(2, id);
					ps.setString(3, CCUUID.deriveMigrationUUID(src.getKey(), id).toString());
					ps.executeUpdate();
				}
			}

			CCLog.addInformation("[UPGRADE v34 -> v35] Derived " + ids.size() + " UUIDs for " + src.getKey());
		}
	}

	/**
	 * @param idColumn  the current (integer) primary key column, becomes {@code ID}
	 * @param refs      column -> table whose id it references
	 * @param fkeys     literal FOREIGN KEY clauses for the new table (leading comma included)
	 */
	private void rebuildTable(String table, String idColumn, Map<String, String> refs, String fkeys) throws SQLException {
		List<Object[]> cols = db.querySQL("PRAGMA table_info(" + table + ")", 6);

		StringBuilder defs = new StringBuilder();
		StringBuilder names = new StringBuilder();
		StringBuilder values = new StringBuilder();

		for (Object[] col : cols) {
			String name = (String) col[1];
			String type = (String) col[2];
			boolean notNull = ((Number) col[3]).intValue() != 0;
			String deflt = (col[4] == null) ? null : String.valueOf(col[4]);
			boolean isPrimary = name.equals(idColumn);
			boolean isRef = refs.containsKey(name);

			String newName = isPrimary ? "ID" : name;

			if (defs.length() > 0) { defs.append(","); names.append(","); values.append(","); }

			// a TEXT PRIMARY KEY does not imply NOT NULL in SQLite
			if (isPrimary)   defs.append("[").append(newName).append("] TEXT NOT NULL PRIMARY KEY");
			else if (isRef)  defs.append("[").append(newName).append("] TEXT NOT NULL");
			else             defs.append("[").append(newName).append("] ").append(type).append(notNull ? " NOT NULL" : "").append(deflt == null ? "" : (" DEFAULT " + deflt));

			names.append("[").append(newName).append("]");

			if (isPrimary)  values.append(lookup(table, name));
			else if (isRef) values.append(lookupNullable(refs.get(name), name));
			else            values.append("[").append(name).append("]");
		}

		db.executeSQLThrow("CREATE TABLE " + table + "_NEW (" + defs + fkeys + ")");
		db.executeSQLThrow("INSERT INTO " + table + "_NEW (" + names + ") SELECT " + values + " FROM " + table);
		db.executeSQLThrow("DROP TABLE " + table);
		db.executeSQLThrow("ALTER TABLE " + table + "_NEW RENAME TO " + table);
	}

	private String lookup(String mapTable, String column) {
		return "(SELECT NEWID FROM " + MAP_TABLE + " WHERE TAB='" + mapTable + "' AND OLDID=[" + column + "])";
	}

	/** {@code -1} was the "no reference" sentinel and becomes the nil UUID. */
	private String lookupNullable(String mapTable, String column) {
		return "CASE WHEN [" + column + "] = -1 THEN '" + NO_COVER + "' ELSE " + lookup(mapTable, column) + " END";
	}

	/**
	 * Renames every cover file to {@code <uuid>.<ext>}, keeping the extension of the current
	 * filename. The automatic migration backup does not include the cover directory, so this runs
	 * idempotently: an already-renamed file is treated as done.
	 */
	private void renameCoverFiles() throws Exception {
		FSPath coverDir = databaseDirectory.append(databaseName, "cover");
		if (!coverDir.directoryExists()) {
			CCLog.addInformation("[UPGRADE v34 -> v35] No cover directory - skipping cover rename");
			return;
		}

		List<Object[]> covers = db.querySQL("SELECT ID, FILENAME FROM COVERS", 2);

		Map<String, Integer> usages = new HashMap<>();
		for (Object[] row : covers) usages.merge((String) row[1], 1, Integer::sum);

		List<Object[]> renames = new ArrayList<>();
		int done = 0;
		int missing = 0;

		for (Object[] row : covers) {
			String uuid = (String) row[0];
			String oldName = (String) row[1];

			String ext = extensionOf(oldName);
			String newName = ext.isEmpty() ? uuid : (uuid + "." + ext);

			if (Str.equals(oldName, newName)) { done++; continue; }

			FSPath src = coverDir.append(oldName);
			FSPath dst = coverDir.append(newName);

			if (dst.fileExists()) {
				done++;
			} else if (src.fileExists()) {
				// two rows can reference the same file - moving it would leave the second one dangling
				if (usages.get(oldName) > 1) Files.copy(src.toPath(), dst.toPath());
				else                         src.renameToWithException(dst);
				done++;
			} else {
				missing++;
				CCLog.addWarning("[UPGRADE v34 -> v35] Cover file not found: " + src);
			}

			renames.add(new Object[] { newName, uuid });
		}

		try (PreparedStatement ps = db.createPreparedStatement("UPDATE COVERS SET FILENAME = ? WHERE ID = ?")) {
			for (Object[] r : renames) {
				ps.setString(1, (String) r[0]);
				ps.setString(2, (String) r[1]);
				ps.executeUpdate();
			}
		}

		CCLog.addInformation("[UPGRADE v34 -> v35] Renamed " + renames.size() + " cover files (" + done + " ok, " + missing + " missing)");
	}

	private static String extensionOf(String filename) {
		int idx = filename.lastIndexOf('.');
		if (idx < 0) return Str.Empty;
		return filename.substring(idx + 1);
	}
}
