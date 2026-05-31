package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.lambda.Func1to1;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Migrates the following columns from their custom serialization format to plain JSON (DB storage only):
 *   - VIEWED_HISTORY (MOVIES, EPISODES)  : comma-separated SQL datetimes  -> ["UNSPECIFIED","2026-04-12", ...]
 *   - SUBTITLES      (MOVIES, EPISODES)  : ";"-separated language ids      -> [5,8,2,8]
 *   - GENRE          (MOVIES, SERIES)    : packed 8x8-bit BIGINT           -> [123,5,15]   (BIGINT -> VARCHAR)
 *   - ONLINEREF      (MOVIES, SERIES)    : "type:id[:base64desc]" list     -> [{"t":"imdb","id":"tt123","d":"..."}]
 *   - GROUPS         (MOVIES, SERIES)    : ";"-separated group names       -> ["DC","DCU Animated"]
 *   - LANGUAGE       (MOVIES, EPISODES)  : bitmask BIGINT                  -> [0,1,5]       (BIGINT -> VARCHAR)
 *
 * The conversion logic is implemented self-contained (inline parsing, no model classes) so this migration
 * stays frozen even if the column-type classes change in the future - mirroring Migration_24_25 / Migration_25_26.
 */
public class Migration_29_30 extends DBMigration {

	public Migration_29_30(GenericDatabase db, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "29"; //$NON-NLS-1$
	}

	@Override
	public String getToVersion() {
		return "30"; //$NON-NLS-1$
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return true;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[MIGRATION] Upgrading database from version 29 to 30");

		// VARCHAR columns: convert the stored string format to JSON in-place
		convertStringColumn("MOVIES",   "VIEWED_HISTORY", Migration_29_30::viewedHistoryToJson);
		convertStringColumn("EPISODES", "VIEWED_HISTORY", Migration_29_30::viewedHistoryToJson);

		convertStringColumn("MOVIES",   "SUBTITLES",      Migration_29_30::intListToJson);
		convertStringColumn("EPISODES", "SUBTITLES",      Migration_29_30::intListToJson);

		convertStringColumn("MOVIES",   "ONLINEREF",      Migration_29_30::onlineRefToJson);
		convertStringColumn("SERIES",   "ONLINEREF",      Migration_29_30::onlineRefToJson);

		convertStringColumn("MOVIES",   "GROUPS",         Migration_29_30::groupsToJson);
		convertStringColumn("SERIES",   "GROUPS",         Migration_29_30::groupsToJson);

		// BIGINT columns: convert the long value to JSON in a new VARCHAR column, then swap
		convertLongColumn("MOVIES",   "GENRE",    Migration_29_30::genreLongToJson);
		convertLongColumn("SERIES",   "GENRE",    Migration_29_30::genreLongToJson);

		convertLongColumn("MOVIES",   "LANGUAGE", Migration_29_30::languageLongToJson);
		convertLongColumn("EPISODES", "LANGUAGE", Migration_29_30::languageLongToJson);

		return new ArrayList<>();
	}

	@SuppressWarnings("nls")
	private void convertStringColumn(String table, String column, Func1to1<String, String> conv) throws Exception {
		var rows = db.querySQL("SELECT LOCALID, " + column + " FROM " + table, 2);
		for (Object[] row : rows) {
			int localId = (Integer) row[0];
			String oldVal = (row[1] == null) ? "" : (String) row[1];

			String json = conv.invoke(oldVal).replace("'", "''");
			db.executeSQLThrow("UPDATE " + table + " SET " + column + " = '" + json + "' WHERE LOCALID = " + localId);
		}
	}

	@SuppressWarnings("nls")
	private void convertLongColumn(String table, String column, Func1to1<Long, String> conv) throws Exception {
		db.executeSQLThrow("ALTER TABLE " + table + " ADD COLUMN " + column + "_NEW VARCHAR DEFAULT '[]'");

		var rows = db.querySQL("SELECT LOCALID, " + column + " FROM " + table, 2);
		for (Object[] row : rows) {
			int localId = (Integer) row[0];
			long oldVal = ((Number) row[1]).longValue();

			String json = conv.invoke(oldVal).replace("'", "''");
			db.executeSQLThrow("UPDATE " + table + " SET " + column + "_NEW = '" + json + "' WHERE LOCALID = " + localId);
		}

		db.executeSQLThrow("ALTER TABLE " + table + " DROP COLUMN " + column);
		db.executeSQLThrow("ALTER TABLE " + table + " RENAME COLUMN " + column + "_NEW TO " + column);
	}

	@SuppressWarnings("nls")
	private static String viewedHistoryToJson(String old) {
		JSONArray arr = new JSONArray();
		if (!old.isEmpty()) {
			for (String s : old.split(",")) if (!s.isEmpty()) arr.put(s);
		}
		return arr.toString();
	}

	@SuppressWarnings("nls")
	private static String intListToJson(String old) {
		JSONArray arr = new JSONArray();
		if (!old.isEmpty()) {
			for (String s : old.split(";")) {
				s = s.trim();
				if (!s.isEmpty()) arr.put(Integer.parseInt(s));
			}
		}
		return arr.toString();
	}

	@SuppressWarnings("nls")
	private static String groupsToJson(String old) {
		JSONArray arr = new JSONArray();
		if (!old.isEmpty()) {
			for (String s : old.split(";")) if (!s.isEmpty()) arr.put(s);
		}
		return arr.toString();
	}

	@SuppressWarnings("nls")
	private static String onlineRefToJson(String old) {
		JSONArray arr = new JSONArray();
		if (old.isEmpty()) return arr.toString();

		for (String entry : old.split(";")) {
			if (entry.isEmpty()) continue;

			int idx1 = entry.indexOf(':');
			int idx2 = entry.lastIndexOf(':');
			if (idx1 < 0) continue;

			String type;
			String id;
			String desc;
			if (idx1 == idx2) {
				type = entry.substring(0, idx1);
				id   = entry.substring(idx1 + 1);
				desc = "";
			} else {
				type = entry.substring(0, idx1);
				id   = entry.substring(idx1 + 1, idx2);
				desc = Str.fromBase64(entry.substring(idx2 + 1));
			}

			if (type.isEmpty() || id.trim().isEmpty()) continue;

			JSONObject o = new JSONObject();
			o.put("t", type);
			o.put("id", id);
			if (!Str.isNullOrWhitespace(desc)) o.put("d", desc);
			arr.put(o);
		}

		return arr.toString();
	}

	private static String genreLongToJson(long v) {
		JSONArray arr = new JSONArray();
		for (int i = 0; i < 8; i++) {
			int g = (int) ((v >> (i * 8)) & 0xFFL);
			if (g == 0) break; // NO_GENRE (0) marks the end of the packed list
			arr.put(g);
		}
		return arr.toString();
	}

	private static String languageLongToJson(long v) {
		JSONArray arr = new JSONArray();
		for (int i = 0; i < 64; i++) {
			if ((v & (1L << i)) != 0) arr.put(i);
		}
		return arr.toString();
	}
}
