package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.filesystem.FSPath;
import org.json.JSONArray;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Migration_23_24 extends DBMigration {

	public Migration_23_24(GenericDatabase db, CCProperties ccprops, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, ccprops, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "23";
	}

	@Override
	public String getToVersion() {
		return "24";
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return true;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[UPGRADE v23 -> v24] Migrate groups to individual fields (SpecialVersion, AnimeSeason, AnimeStudio)");

		// First, add the new columns to the database
		CCLog.addInformation("[MIGRATION] Adding new columns to MOVIES and SERIES tables");

		// Add columns to MOVIES table
		db.executeSQLThrow("ALTER TABLE MOVIES ADD COLUMN SPECIALVERSION VARCHAR(2048)");
		db.executeSQLThrow("ALTER TABLE MOVIES ADD COLUMN ANIMESEASON VARCHAR(2048)");
		db.executeSQLThrow("ALTER TABLE MOVIES ADD COLUMN ANIMESTUDIO VARCHAR(2048)");

		// Add columns to SERIES table
		db.executeSQLThrow("ALTER TABLE SERIES ADD COLUMN SPECIALVERSION VARCHAR(2048)");
		db.executeSQLThrow("ALTER TABLE SERIES ADD COLUMN ANIMESEASON VARCHAR(2048)");
		db.executeSQLThrow("ALTER TABLE SERIES ADD COLUMN ANIMESTUDIO VARCHAR(2048)");

		// Initialize the new columns with empty strings
		db.executeSQLThrow("UPDATE MOVIES SET SPECIALVERSION = '', ANIMESEASON = '', ANIMESTUDIO = ''");
		db.executeSQLThrow("UPDATE SERIES SET SPECIALVERSION = '', ANIMESEASON = '', ANIMESTUDIO = ''");

		// Get all children of the parent groups we need to migrate
		List<String> animeSeasonGroups = new ArrayList<>();
		List<String> animeStudioGroups = new ArrayList<>();
		List<String> specialVersionGroups = new ArrayList<>();

		// Find all child groups under "Anime Seasons"
		for (Object[] row : db.querySQL("SELECT NAME FROM GROUPS WHERE PARENTGROUP = 'Anime Seasons'", 1)) {
			animeSeasonGroups.add((String) row[0]);
		}

		// Find all child groups under "Anime Studio"
		for (Object[] row : db.querySQL("SELECT NAME FROM GROUPS WHERE PARENTGROUP = 'Animation Studios'", 1)) {
			animeStudioGroups.add((String) row[0]);
		}

		// Find all child groups under "Animation Studios" -> these go to SpecialVersion
		for (Object[] row : db.querySQL("SELECT NAME FROM GROUPS WHERE PARENTGROUP = 'Special Version'", 1)) {
			specialVersionGroups.add((String) row[0]);
		}

		CCLog.addInformation("[MIGRATION] Found " + animeSeasonGroups.size() + " anime season groups");
		CCLog.addInformation("[MIGRATION] Found " + animeStudioGroups.size() + " anime studio groups");
		CCLog.addInformation("[MIGRATION] Found " + specialVersionGroups.size() + " special version groups");

		// Migrate MOVIES table
		migrateElementTable_23_24("MOVIES", animeSeasonGroups, animeStudioGroups, specialVersionGroups);

		// Migrate SERIES table
		migrateElementTable_23_24("SERIES", animeSeasonGroups, animeStudioGroups, specialVersionGroups);

		// Clean up - remove the migrated child groups and parent groups
		for (String groupName : animeSeasonGroups) {
			db.executeSQLThrow("DELETE FROM GROUPS WHERE NAME = '" + groupName.replace("'", "''") + "'");
		}
		for (String groupName : animeStudioGroups) {
			db.executeSQLThrow("DELETE FROM GROUPS WHERE NAME = '" + groupName.replace("'", "''") + "'");
		}
		for (String groupName : specialVersionGroups) {
			db.executeSQLThrow("DELETE FROM GROUPS WHERE NAME = '" + groupName.replace("'", "''") + "'");
		}

		// Remove parent groups if they're empty now
		db.executeSQLThrow("DELETE FROM GROUPS WHERE NAME = 'Anime Seasons' AND NOT EXISTS (SELECT 1 FROM GROUPS WHERE PARENTGROUP = 'Anime Seasons')");
		db.executeSQLThrow("DELETE FROM GROUPS WHERE NAME = 'Animation Studios' AND NOT EXISTS (SELECT 1 FROM GROUPS WHERE PARENTGROUP = 'Animation Studios')");
		db.executeSQLThrow("DELETE FROM GROUPS WHERE NAME = 'Special Version' AND NOT EXISTS (SELECT 1 FROM GROUPS WHERE PARENTGROUP = 'Special Version')");

		return new ArrayList<>();
	}

	private void migrateElementTable_23_24(String tableName, List<String> animeSeasonGroups, List<String> animeStudioGroups, List<String> specialVersionGroups) throws SQLException
	{
		CCLog.addInformation("[MIGRATION] Migrating table " + tableName);

		for (Object[] row : db.querySQL("SELECT LOCALID, GROUPS FROM " + tableName + " WHERE GROUPS IS NOT NULL AND GROUPS != ''", 2))
		{
			int localId = (Integer) row[0];
			String groupsStr = (String) row[1];
			if (groupsStr == null || groupsStr.trim().isEmpty()) continue;

			// Parse current groups (semicolon separated)
			String[] currentGroups = groupsStr.split(";");
			List<String> remainingGroups = new ArrayList<>();
			List<String> foundAnimeSeasons = new ArrayList<>();
			List<String> foundAnimeStudios = new ArrayList<>();
			List<String> foundSpecialVersions = new ArrayList<>();

			// Process each group
			for (String group : currentGroups) {
				String trimmedGroup = group.trim();
				if (trimmedGroup.isEmpty()) continue;

				if (animeSeasonGroups.contains(trimmedGroup)) {
					foundAnimeSeasons.add(trimmedGroup);
				} else if (animeStudioGroups.contains(trimmedGroup)) {
					foundAnimeStudios.add(trimmedGroup);
				} else if (specialVersionGroups.contains(trimmedGroup)) {
					foundSpecialVersions.add(trimmedGroup);
				} else {
					remainingGroups.add(trimmedGroup);
				}
			}

			// Build update query
			StringBuilder updateQuery = new StringBuilder("UPDATE " + tableName + " SET ");
			boolean needComma = false;

			if (!foundSpecialVersions.isEmpty()) {
				updateQuery.append("SPECIALVERSION = '").append((new JSONArray(foundSpecialVersions).toString()).replace("'", "''")).append("'");
				needComma = true;
			}

			if (!foundAnimeSeasons.isEmpty()) {
				if (needComma) updateQuery.append(", ");
				updateQuery.append("ANIMESEASON = '").append((new JSONArray(foundAnimeSeasons).toString()).replace("'", "''")).append("'");
				needComma = true;
			}

			if (!foundAnimeStudios.isEmpty()) {
				if (needComma) updateQuery.append(", ");
				updateQuery.append("ANIMESTUDIO = '").append((new JSONArray(foundAnimeStudios.stream().map(p -> p.replace("Studio ", "")).toArray()).toString()).replace("'", "''")).append("'");
				needComma = true;
			}

			// Update remaining groups
			if (needComma) updateQuery.append(", ");
			updateQuery.append("GROUPS = '").append(String.join(";", remainingGroups).replace("'", "''")).append("'");

			updateQuery.append(" WHERE LOCALID = ").append(localId);

			// Execute the update if we found any groups to migrate
			if (!foundSpecialVersions.isEmpty() || !foundAnimeSeasons.isEmpty() || !foundAnimeStudios.isEmpty()) {
				db.executeSQLThrow(updateQuery.toString());
				CCLog.addInformation("[MIGRATION] Updated " + tableName + " ID=" + localId +
						" SpecialVersion=" + foundSpecialVersions.size() +
						" AnimeSeasons=" + foundAnimeSeasons.size() +
						" AnimeStudios=" + foundAnimeStudios.size());
			}
		}
	}

}
