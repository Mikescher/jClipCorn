package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.comparator.CCAnimeSeasonComparator;
import de.jClipCorn.util.filesystem.FSPath;
import org.json.JSONArray;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Moves the AnimeSeason and AnimeStudio fields from the SERIES table to the SEASONS table.
 *
 * Data is moved as follows (per series):
 *  - AnimeSeason: if there are as many anime-seasons as there are CCSeasons, they are moved 1:1 (in localid order);
 *                 otherwise all anime-seasons are moved to the first season (and a warning is logged).
 *  - AnimeStudio: all studios are copied to every season. If there is more than one studio a warning is logged.
 */
public class Migration_32_33 extends DBMigration {

	public Migration_32_33(GenericDatabase db, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "32"; //$NON-NLS-1$
	}

	@Override
	public String getToVersion() {
		return "33"; //$NON-NLS-1$
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return true;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[UPGRADE v32 -> v33] Move AnimeSeason and AnimeStudio from SERIES to SEASONS");

		// 1. Add the new columns to the SEASONS table
		db.executeSQLThrow("ALTER TABLE SEASONS ADD COLUMN ANIMESEASON VARCHAR(2048)");
		db.executeSQLThrow("ALTER TABLE SEASONS ADD COLUMN ANIMESTUDIO VARCHAR(2048)");
		db.executeSQLThrow("UPDATE SEASONS SET ANIMESEASON = '', ANIMESTUDIO = ''");

		// 2. Move the data from each series down to its seasons
		for (Object[] serRow : db.querySQL("SELECT LOCALID, ANIMESEASON, ANIMESTUDIO FROM SERIES", 3)) {
			int seriesId = (Integer) serRow[0];

			List<String> animeSeasons = parseJsonStringList((String) serRow[1]);
			List<String> animeStudios = parseJsonStringList((String) serRow[2]);

			if (animeSeasons.isEmpty() && animeStudios.isEmpty()) continue;

			// sort the anime-seasons chronologically - same order the UI uses (CCAnimeSeasonComparator)
			animeSeasons.sort(new CCAnimeSeasonComparator());

			// season-ids of this series, sorted by the same algorithm applied on-load
			// (CCSeasonComparator sorts CCSeasons by their title)
			List<Object[]> seaRows = new ArrayList<>(db.querySQL("SELECT LOCALID, NAME FROM SEASONS WHERE SERIESID = " + seriesId, 2));
			seaRows.sort(Comparator.comparing(r -> (String) r[1]));
			List<Integer> seasonIds = new ArrayList<>();
			for (Object[] seaRow : seaRows) {
				seasonIds.add((Integer) seaRow[0]);
			}

			if (seasonIds.isEmpty()) {
				CCLog.addWarning("[MIGRATION] Series #" + seriesId + " has anime-metadata but no seasons - dropping " +
						animeSeasons.size() + " anime-season(s) and " + animeStudios.size() + " studio(s)");
				continue;
			}

			// --- AnimeSeason ---
			if (!animeSeasons.isEmpty()) {
				if (animeSeasons.size() == seasonIds.size()) {
					// equal amount -> move 1:1
					for (int i = 0; i < seasonIds.size(); i++) {
						setSeasonColumn(seasonIds.get(i), "ANIMESEASON", List.of(animeSeasons.get(i)));
					}
				} else {
					// otherwise put all of them into all seasons
					for (int i = 0; i <seasonIds.size(); i++) {
						setSeasonColumn(seasonIds.get(i), "ANIMESEASON", animeSeasons);
					}
					CCLog.addWarning("[MIGRATION] Series #" + seriesId + " has " + animeSeasons.size() +
							" anime-season(s) but " + seasonIds.size() + " season(s) - assigned all anime-seasons to the all seasons");
				}
			}

			// --- AnimeStudio ---
			if (!animeStudios.isEmpty()) {
				// copy all studios to every season
				for (Integer seasId : seasonIds) {
					setSeasonColumn(seasId, "ANIMESTUDIO", animeStudios);
				}
				if (animeStudios.size() > 1) {
					CCLog.addWarning("[MIGRATION] Series #" + seriesId + " has " + animeStudios.size() +
							" studios - copied all of them to every season");
				}
			}
		}

		// 3. Remove the old columns from the SERIES table
		db.executeSQLThrow("ALTER TABLE SERIES DROP COLUMN ANIMESEASON");
		db.executeSQLThrow("ALTER TABLE SERIES DROP COLUMN ANIMESTUDIO");

		return new ArrayList<>();
	}

	@SuppressWarnings("nls")
	private void setSeasonColumn(int seasonId, String column, List<String> values) throws SQLException {
		String json = new JSONArray(values).toString().replace("'", "''");
		db.executeSQLThrow("UPDATE SEASONS SET " + column + " = '" + json + "' WHERE LOCALID = " + seasonId);
	}

	private List<String> parseJsonStringList(String value) {
		List<String> result = new ArrayList<>();
		if (value == null || value.trim().isEmpty()) return result;

		JSONArray arr = new JSONArray(value);
		for (int i = 0; i < arr.length(); i++) {
			String s = arr.getString(i);
			if (s != null && !s.trim().isEmpty()) result.add(s.trim());
		}
		return result;
	}
}
