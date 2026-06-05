package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.FSPath;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds a per-season ONLINEREF column and relocates description-scoped online-references from the
 * series onto their matching season.
 *
 * Previously a reference that conceptually belonged to a single season was stored on the series with
 * the season title kept in its "d" (description) field. From now on seasons own their references
 * directly (SEASONS.ONLINEREF, same JSON format as SERIES/MOVIES: [{"t":"imdb","id":"tt123"}]).
 *
 * For every series:
 *   - refs whose "d" (trimmed, case-insensitively) equals a season's NAME are moved onto that season,
 *     the "d" field is dropped, and within a season MAL ("myal") becomes the Main ref (first entry),
 *     otherwise the first matched entry stays Main.
 *   - all other refs (incl. those whose "d" matches no season) stay on the series unchanged.
 *
 * The conversion is implemented self-contained (raw SQL + org.json, no model classes) so it stays
 * frozen even if the column-type classes change - mirroring Migration_29_30.
 */
public class Migration_31_32 extends DBMigration {

	private static final String MAL_IDENTIFIER = "myal"; //$NON-NLS-1$

	public Migration_31_32(GenericDatabase db, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "31"; //$NON-NLS-1$
	}

	@Override
	public String getToVersion() {
		return "32"; //$NON-NLS-1$
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return true;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[MIGRATION] Upgrading database from version 31 to 32");

		db.executeSQLThrow("ALTER TABLE SEASONS ADD COLUMN ONLINEREF VARCHAR NOT NULL DEFAULT '[]'");

		var seriesRows = db.querySQL("SELECT LOCALID, ONLINEREF FROM SERIES", 2);
		for (Object[] srow : seriesRows) {
			int seriesId = (Integer) srow[0];
			String seriesRefJson = (srow[1] == null) ? "" : (String) srow[1];

			JSONArray seriesRefs = parseArray(seriesRefJson);
			if (seriesRefs.length() == 0) continue;

			// season-title (normalized) -> seasonId
			var seasonRows = db.querySQL("SELECT LOCALID, NAME FROM SEASONS WHERE SERIESID = " + seriesId, 2);
			if (seasonRows.isEmpty()) continue;

			var seasonIdByName = new java.util.HashMap<String, Integer>();
			for (Object[] arow : seasonRows) {
				int seasonId = (Integer) arow[0];
				String name = (arow[1] == null) ? "" : (String) arow[1];
				seasonIdByName.putIfAbsent(normalize(name), seasonId);
			}

			JSONArray remainingSeriesRefs = new JSONArray();
			var movedBySeason = new java.util.LinkedHashMap<Integer, JSONArray>(); // seasonId -> refs

			for (int i = 0; i < seriesRefs.length(); i++) {
				JSONObject ref = seriesRefs.getJSONObject(i);
				String desc = ref.optString("d", "");
				Integer targetSeason = Str.isNullOrWhitespace(desc) ? null : seasonIdByName.get(normalize(desc));

				if (targetSeason == null) {
					if (!Str.isNullOrWhitespace(desc)) {
						CCLog.addWarning("[MIGRATION] Online-reference " + ref.optString("t", "") + ":" + ref.optString("id", "")
								+ " on series #" + seriesId + " has a description ('" + desc + "') that does not match any season - keeping it on the series as-is");
					}
					remainingSeriesRefs.put(ref);
				} else {
					JSONObject moved = new JSONObject();
					moved.put("t", ref.getString("t"));
					moved.put("id", ref.getString("id"));
					// "d" is intentionally dropped - the season now owns the ref
					movedBySeason.computeIfAbsent(targetSeason, k -> new JSONArray()).put(moved);
				}
			}

			if (movedBySeason.isEmpty()) continue; // nothing to relocate for this series

			for (var en : movedBySeason.entrySet()) {
				JSONArray ordered = malFirst(en.getValue());
				updateRefColumn("SEASONS", en.getKey(), ordered);
			}
			updateRefColumn("SERIES", seriesId, remainingSeriesRefs);
		}

		return new ArrayList<>();
	}

	@SuppressWarnings("nls")
	private void updateRefColumn(String table, int localId, JSONArray refs) throws Exception {
		String json = refs.toString().replace("'", "''");
		db.executeSQLThrow("UPDATE " + table + " SET ONLINEREF = '" + json + "' WHERE LOCALID = " + localId);
	}

	@SuppressWarnings("nls")
	private static JSONArray parseArray(String json) {
		if (Str.isNullOrWhitespace(json)) return new JSONArray();
		try {
			return new JSONArray(json);
		} catch (Exception e) {
			return new JSONArray();
		}
	}

	/** Moves the first MyAnimeList reference to the front (it becomes the season's Main ref). */
	@SuppressWarnings("nls")
	private static JSONArray malFirst(JSONArray refs) {
		int malIdx = -1;
		for (int i = 0; i < refs.length(); i++) {
			if (MAL_IDENTIFIER.equals(refs.getJSONObject(i).optString("t", ""))) { malIdx = i; break; }
		}
		if (malIdx <= 0) return refs; // none, or already first

		JSONArray result = new JSONArray();
		result.put(refs.getJSONObject(malIdx));
		for (int i = 0; i < refs.length(); i++) if (i != malIdx) result.put(refs.getJSONObject(i));
		return result;
	}

	private static String normalize(String s) {
		return (s == null) ? "" : s.trim().toLowerCase(); //$NON-NLS-1$
	}
}
