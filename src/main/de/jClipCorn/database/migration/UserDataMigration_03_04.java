package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.filesystem.FSPath;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.List;

/**
 * Gives every viewed-history entry a comment: VIEWED_HISTORY (userdata MOVIES, EPISODES) goes from
 *   ["UNSPECIFIED","2016-11-27 16:48:30"]  ->  [{"d":"UNSPECIFIED","c":""},{"d":"2016-11-27 16:48:30","c":""}]
 * The datetime strings and the order are kept verbatim. The conversion is self-contained (no model classes),
 * so this migration stays frozen when CCDateTimeList changes.
 *
 * A value that is not a valid JSON array of strings/objects is left as it is - the loader reports it.
 */
public class UserDataMigration_03_04 extends UserDataMigration {

	public UserDataMigration_03_04(GenericDatabase db, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "3"; //$NON-NLS-1$
	}

	@Override
	public String getToVersion() {
		return "4"; //$NON-NLS-1$
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return true; // the history triggers would otherwise log every rewritten row as a change
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[UPGRADE userdata v3 -> v4] Add comments to the viewed history entries");

		convertHistory("userdata.MOVIES");
		convertHistory("userdata.EPISODES");

		return new ArrayList<>();
	}

	@SuppressWarnings("nls")
	private void convertHistory(String table) throws Exception {
		var rows = db.querySQL("SELECT [ID], [VIEWED_HISTORY] FROM " + table, 2);

		try (var stmt = db.createPreparedStatement("UPDATE " + table + " SET [VIEWED_HISTORY] = ? WHERE [ID] = ?")) {
			for (Object[] row : rows) {
				String id = (String) row[0];
				String oldVal = (String) row[1];

				String newVal = convert(oldVal);
				if (newVal == null) {
					CCLog.addWarning("[UPGRADE userdata v3 -> v4] Could not convert VIEWED_HISTORY of " + table + " '" + id + "': " + oldVal);
					continue;
				}
				if (newVal.equals(oldVal)) continue;

				stmt.setString(1, newVal);
				stmt.setString(2, id);
				stmt.executeUpdate();
			}
		}
	}

	/** @return null if the value cannot be converted */
	@SuppressWarnings("nls")
	static String convert(String old) {
		if (old == null) return null;

		try {
			JSONArray arr = new JSONArray(old);

			JSONStringer w = new JSONStringer();
			w.array();
			for (int i = 0; i < arr.length(); i++) {
				Object v = arr.get(i);
				if (v instanceof String str) {
					w.object().key("d").value(str).key("c").value("").endObject();
				} else if (v instanceof JSONObject obj && obj.opt("d") instanceof String d) {
					Object c = obj.opt("c");
					if (c != null && !(c instanceof String)) return null;
					w.object().key("d").value(d).key("c").value(c == null ? "" : c).endObject();
				} else {
					return null;
				}
			}
			w.endArray();
			return w.toString();
		} catch (JSONException e) {
			return null;
		}
	}
}
