package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.filesystem.FSPath;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Migration_25_26 extends DBMigration {

	public Migration_25_26(GenericDatabase db, CCProperties ccprops, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, ccprops, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "25";
	}

	@Override
	public String getToVersion() {
		return "26";
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return true;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[MIGRATION] Upgrading database from version 25 to 26");

		// Add new PARTS column to MOVIES table
		db.executeSQLThrow("ALTER TABLE MOVIES ADD COLUMN PARTS VARCHAR DEFAULT '[]'");

		// Migrate individual part columns to single JSON array column
		var movieRows = db.querySQL("SELECT LOCALID, PART1, PART2, PART3, PART4, PART5, PART6 FROM MOVIES", 7);
		for (Object[] row : movieRows) {
			int localId = (Integer) row[0];
			String[] parts = new String[6];
			for (int i = 0; i < 6; i++) {
				parts[i] = (String) row[i + 1];
			}

			// Convert individual parts to JSON array
			JSONArray partsArray = new JSONArray();
			for (String part : parts) {
				if (part != null && !part.trim().isEmpty()) {
					partsArray.put(part);
				}
			}

			String jsonParts = partsArray.toString().replace("'", "''");
			db.executeSQLThrow("UPDATE MOVIES SET PARTS = '" + jsonParts + "' WHERE LOCALID = " + localId);
		}

		// Drop the old individual part columns
		db.executeSQLThrow("ALTER TABLE MOVIES DROP COLUMN PART1");
		db.executeSQLThrow("ALTER TABLE MOVIES DROP COLUMN PART2");
		db.executeSQLThrow("ALTER TABLE MOVIES DROP COLUMN PART3");
		db.executeSQLThrow("ALTER TABLE MOVIES DROP COLUMN PART4");
		db.executeSQLThrow("ALTER TABLE MOVIES DROP COLUMN PART5");
		db.executeSQLThrow("ALTER TABLE MOVIES DROP COLUMN PART6");

		return new ArrayList<>();
	}

}