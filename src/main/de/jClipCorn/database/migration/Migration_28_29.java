package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.SimpleFileUtils;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class Migration_28_29 extends DBMigration {

	public Migration_28_29(GenericDatabase db, CCProperties ccprops, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, ccprops, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "28";
	}

	@Override
	public String getToVersion() {
		return "29";
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return false;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[MIGRATION] Upgrading database from version 28 to 29");

		db.executeSQLThrow("CREATE TABLE \"FILTERS\" ( \"ID\" INTEGER PRIMARY KEY, \"SORT\" INTEGER NOT NULL, \"NAME\" TEXT NOT NULL, \"DEFINITION\" TEXT NOT NULL )");

		migrateLegacyFilterFile();

		return new ArrayList<>();
	}

	@SuppressWarnings("nls")
	private void migrateLegacyFilterFile() throws Exception {
		FSPath flst = ccprops.PROP_MAINFRAME_FILTERLISTPATH.getValue().toFSPath(ccprops);

		if (!flst.exists()) {
			CCLog.addInformation("[MIGRATION] No legacy filter file found - skipping filter import");
			return;
		}

		String txt;
		try {
			txt = flst.readAsUTF8TextFile();
		} catch (IOException e) {
			CCLog.addError(e);
			return;
		}

		String[] lines = SimpleFileUtils.splitLines(txt);

		try (PreparedStatement ps = db.createPreparedStatement("INSERT INTO \"FILTERS\" (\"ID\", \"SORT\", \"NAME\", \"DEFINITION\") VALUES (?, ?, ?, ?)")) {
			int idx = 0;

			for (String raw : lines) {
				String line = raw.trim();
				if (line.isEmpty()) continue;

				String[] parts = line.split("\t");
				if (parts.length != 2) {
					CCLog.addError("[MIGRATION] Could not parse legacy filter list line " + (idx + 1));
					continue;
				}

				ps.setInt(1, idx + 1);
				ps.setInt(2, idx);
				ps.setString(3, parts[0]);
				ps.setString(4, parts[1]);
				ps.executeUpdate();

				idx++;
			}

			CCLog.addInformation("[MIGRATION] Imported " + idx + " custom filters from legacy filter file into the database");
		}

		flst.deleteWithException();
	}

}
