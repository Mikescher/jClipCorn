package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.filesystem.FSPath;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Splits the table settings that applied to both the MainFrame table and the PreviewSeriesFrame table into one
 * setting per frame, both starting out with the old value:
 *  - PROP_TABLE_MAX_SUBTITLE_COUNT  -> PROP_MAINFRAME_MAX_SUBTITLE_COUNT + PROP_SERIESFRAME_MAX_SUBTITLE_COUNT (old key removed)
 *  - PROP_MAINFRAME_SHOW_VIEWCOUNT  -> stays, copied to PROP_SERIESFRAME_SHOW_VIEWCOUNT
 * A setting that was never stored is not created, so it keeps falling back to its default.
 */
public class UserDataMigration_02_03 extends UserDataMigration {

	public UserDataMigration_02_03(GenericDatabase db, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "2"; //$NON-NLS-1$
	}

	@Override
	public String getToVersion() {
		return "3"; //$NON-NLS-1$
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return false; // only rows of the untracked PROPERTIES table change
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[UPGRADE userdata v2 -> v3] Split PROP_TABLE_MAX_SUBTITLE_COUNT and PROP_MAINFRAME_SHOW_VIEWCOUNT into per-frame settings");

		copyProperty("PROP_TABLE_MAX_SUBTITLE_COUNT", "PROP_MAINFRAME_MAX_SUBTITLE_COUNT");
		copyProperty("PROP_TABLE_MAX_SUBTITLE_COUNT", "PROP_SERIESFRAME_MAX_SUBTITLE_COUNT");
		db.executeSQLThrow("DELETE FROM userdata.PROPERTIES WHERE PKEY='PROP_TABLE_MAX_SUBTITLE_COUNT'");

		copyProperty("PROP_MAINFRAME_SHOW_VIEWCOUNT", "PROP_SERIESFRAME_SHOW_VIEWCOUNT");

		return new ArrayList<>();
	}

	@SuppressWarnings("nls")
	private void copyProperty(String source, String target) throws SQLException {
		db.executeSQLThrow(
			"INSERT INTO userdata.PROPERTIES ([PKEY], [PVALUE], [LAST_CHANGED]) " +
			"SELECT '" + target + "', [PVALUE], [LAST_CHANGED] FROM userdata.PROPERTIES WHERE [PKEY]='" + source + "' " +
			"AND NOT EXISTS (SELECT 1 FROM userdata.PROPERTIES WHERE [PKEY]='" + target + "')");
	}
}
