package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.filesystem.FSPath;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Migration_24_25 extends DBMigration {

	public Migration_24_25(GenericDatabase db, CCProperties ccprops, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, ccprops, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "24";
	}

	@Override
	public String getToVersion() {
		return "25";
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return true;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[MIGRATION] Upgrading database from version 24 to 25");

		// Migrate tags from bitfield (SMALLINT) to JSON array (VARCHAR) for MOVIES
		db.executeSQLThrow("ALTER TABLE MOVIES ADD COLUMN TAGS_NEW VARCHAR");

		// Read and convert all movie tags
		var movieRows = db.querySQL("SELECT LOCALID, TAGS FROM MOVIES", 2);
		for (Object[] row : movieRows) {
			int localId = (Integer) row[0];
			short tagsValue = ((Number) row[1]).shortValue();

			// Convert bitfield to JSON array
			JSONArray tagArray = new JSONArray();
			for (int i = 0; i < 16; i++) {
				if ((tagsValue & (1 << i)) != 0) {
					tagArray.put(i);
				}
			}

			db.executeSQLThrow("UPDATE MOVIES SET TAGS_NEW = '" + tagArray.toString().replace("'", "''") + "' WHERE LOCALID = " + localId);
		}

		db.executeSQLThrow("ALTER TABLE MOVIES DROP COLUMN TAGS");
		db.executeSQLThrow("ALTER TABLE MOVIES RENAME COLUMN TAGS_NEW TO TAGS");

		// Migrate tags from bitfield (SMALLINT) to JSON array (VARCHAR) for SERIES
		db.executeSQLThrow("ALTER TABLE SERIES ADD COLUMN TAGS_NEW VARCHAR");

		// Read and convert all series tags
		var seriesRows = db.querySQL("SELECT LOCALID, TAGS FROM SERIES", 2);
		for (Object[] row : seriesRows) {
			int localId = (Integer) row[0];
			short tagsValue = ((Number) row[1]).shortValue();

			// Convert bitfield to JSON array
			JSONArray tagArray = new JSONArray();
			for (int i = 0; i < 16; i++) {
				if ((tagsValue & (1 << i)) != 0) {
					tagArray.put(i);
				}
			}

			db.executeSQLThrow("UPDATE SERIES SET TAGS_NEW = '" + tagArray.toString().replace("'", "''") + "' WHERE LOCALID = " + localId);
		}

		db.executeSQLThrow("ALTER TABLE SERIES DROP COLUMN TAGS");
		db.executeSQLThrow("ALTER TABLE SERIES RENAME COLUMN TAGS_NEW TO TAGS");

		// Migrate tags from bitfield (SMALLINT) to JSON array (VARCHAR) for EPISODES
		db.executeSQLThrow("ALTER TABLE EPISODES ADD COLUMN TAGS_NEW VARCHAR");

		// Read and convert all episode tags
		var episodeRows = db.querySQL("SELECT LOCALID, TAGS FROM EPISODES", 2);
		for (Object[] row : episodeRows) {
			int localId = (Integer) row[0];
			short tagsValue = ((Number) row[1]).shortValue();

			// Convert bitfield to JSON array
			JSONArray tagArray = new JSONArray();
			for (int i = 0; i < 16; i++) {
				if ((tagsValue & (1 << i)) != 0) {
					tagArray.put(i);
				}
			}

			db.executeSQLThrow("UPDATE EPISODES SET TAGS_NEW = '" + tagArray.toString().replace("'", "''") + "' WHERE LOCALID = " + localId);
		}

		db.executeSQLThrow("ALTER TABLE EPISODES DROP COLUMN TAGS");
		db.executeSQLThrow("ALTER TABLE EPISODES RENAME COLUMN TAGS_NEW TO TAGS");

		return new ArrayList<>();
	}

}
