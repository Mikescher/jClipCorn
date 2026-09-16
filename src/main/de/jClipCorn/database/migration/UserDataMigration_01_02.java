package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.filesystem.FSPath;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

/**
 * Replaces the ten fixed CCPath syntax variable settings (PROP_PATHSYNTAX_VAR1..VAR10) with the single
 * list setting PROP_PATHSYNTAX_VARIABLES.
 *
 * Old value: {@code base64(host);base64(key);base64(value)} (or without the host part), one per key.
 * New value: {@code [{"host":..,"key":..,"value":..}, ...]} in VAR1..VAR10 order, empty variables dropped.
 * Both formats are spelled out here instead of reusing the property classes, which only know the new one.
 */
public class UserDataMigration_01_02 extends UserDataMigration {

	private static final int LEGACY_VAR_COUNT = 10;

	public UserDataMigration_01_02(GenericDatabase db, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "1"; //$NON-NLS-1$
	}

	@Override
	public String getToVersion() {
		return "2"; //$NON-NLS-1$
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return false; // only rows of the untracked PROPERTIES table change
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[UPGRADE userdata v1 -> v2] Merge PROP_PATHSYNTAX_VAR1..10 into PROP_PATHSYNTAX_VARIABLES");

		var jarr = new JSONArray();
		var found = false;

		for (int i = 1; i <= LEGACY_VAR_COUNT; i++) {
			var key = "PROP_PATHSYNTAX_VAR" + i;
			var raw = db.querySingleStringSQLThrow("SELECT PVALUE FROM userdata.PROPERTIES WHERE PKEY='" + key + "'", 0);
			if (raw == null) continue;
			found = true;

			var jobj = parseLegacy(key, raw);
			if (jobj != null) jarr.put(jobj);
		}

		if (found && db.querySingleIntSQLThrow("SELECT COUNT(*) FROM userdata.PROPERTIES WHERE PKEY='PROP_PATHSYNTAX_VARIABLES'", 0) == 0) {
			try (PreparedStatement ps = db.createPreparedStatement("INSERT INTO userdata.PROPERTIES ([PKEY], [PVALUE], [LAST_CHANGED]) VALUES (?, ?, ?)")) {
				ps.setString(1, "PROP_PATHSYNTAX_VARIABLES");
				ps.setString(2, jarr.toString());
				ps.setString(3, CCDateTime.getCurrentDateTime().toStringSQL());
				ps.executeUpdate();
			}
		}

		db.executeSQLThrow("DELETE FROM userdata.PROPERTIES WHERE PKEY GLOB 'PROP_PATHSYNTAX_VAR[0-9]*'");

		return new ArrayList<>();
	}

	/** @return null for an empty or unreadable variable */
	@SuppressWarnings("nls")
	private static JSONObject parseLegacy(String propKey, String raw) {
		String[] sval = raw.split(";");

		String host, key, value;
		if (sval.length == 0) {
			return null;
		} else if (sval.length == 2) {
			host  = Str.Empty;
			key   = Str.fromBase64(sval[0]);
			value = Str.fromBase64(sval[1]);
		} else if (sval.length == 3) {
			host  = Str.fromBase64(sval[0]);
			key   = Str.fromBase64(sval[1]);
			value = Str.fromBase64(sval[2]);
		} else {
			CCLog.addWarning("[UPGRADE userdata v1 -> v2] Dropping unreadable value of " + propKey + ": '" + raw + "'");
			return null;
		}

		if (Str.isNullOrWhitespace(host) && Str.isNullOrWhitespace(key) && Str.isNullOrWhitespace(value)) return null;

		var jobj = new JSONObject();
		jobj.put("host", host);
		jobj.put("key", key);
		jobj.put("value", value);
		return jobj;
	}
}
