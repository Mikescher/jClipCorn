package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FilesystemUtils;

import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.util.*;

/**
 * Introduces the PROPERTIES table (application settings now live in the database).
 *
 * The settings previously stored in the {@code jClipcorn.properties} file are imported
 * once into the new table, and the legacy file is deleted afterwards.
 */
public class Migration_30_31 extends DBMigration {

	// Bootstrap settings that are no longer configurable (hardcoded constants) - not carried into the DB.
	@SuppressWarnings("nls")
	private static final List<String> REMOVED_KEYS = Arrays.asList(
			"PROP_DATABASE_NAME", "PROP_DATABASE_DIR", "PROP_LOG_PATH", "PROP_DATABASE_DRIVER");

	public Migration_30_31(GenericDatabase db, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "30"; //$NON-NLS-1$
	}

	@Override
	public String getToVersion() {
		return "31"; //$NON-NLS-1$
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return false;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[MIGRATION] Upgrading database from version 30 to 31");

		db.executeSQLThrow("CREATE TABLE \"PROPERTIES\" ( \"PKEY\" TEXT PRIMARY KEY, \"PVALUE\" TEXT NOT NULL, \"LAST_CHANGED\" TEXT NOT NULL )");

		importLegacyPropertiesFile();

		return new ArrayList<>();
	}

	@SuppressWarnings("nls")
	private void importLegacyPropertiesFile() throws Exception {
		FSPath propFile = FilesystemUtils.getRealSelfDirectory().append("jClipcorn.properties");

		if (!propFile.exists()) {
			CCLog.addInformation("[MIGRATION] No legacy properties file found - skipping settings import");
			return;
		}

		Properties legacy = new Properties();
		try (FileInputStream stream = new FileInputStream(propFile.toFile())) {
			legacy.load(stream);
		}

		String now = CCDateTime.getCurrentDateTime().toStringSQL();

		int idx = 0;
		try (PreparedStatement ps = db.createPreparedStatement("INSERT OR REPLACE INTO \"PROPERTIES\" (\"PKEY\", \"PVALUE\", \"LAST_CHANGED\") VALUES (?, ?, ?)")) {
			for (Map.Entry<Object, Object> e : legacy.entrySet()) {
				String key = String.valueOf(e.getKey());
				if (REMOVED_KEYS.contains(key)) continue;

				ps.setString(1, key);
				ps.setString(2, String.valueOf(e.getValue()));
				ps.setString(3, now);
				ps.executeUpdate();

				idx++;
			}
		}

		CCLog.addInformation("[MIGRATION] Imported " + idx + " settings from legacy properties file into the database");

		propFile.deleteWithException();
	}

}
