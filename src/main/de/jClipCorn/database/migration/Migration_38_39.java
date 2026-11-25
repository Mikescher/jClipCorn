package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.filesystem.FSPath;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds main.SERIES.SHOWSTATUS (CCTVShowStatus, 0 = UNKNOWN for every existing series).
 *
 * The history triggers list every column of a tracked table, so they have to be regenerated to
 * pick up the new column - hence backupAndRestoreTrigger() although nothing is dropped.
 */
public class Migration_38_39 extends DBMigration {

	public Migration_38_39(GenericDatabase db, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "38"; //$NON-NLS-1$
	}

	@Override
	public String getToVersion() {
		return "39"; //$NON-NLS-1$
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return true;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[UPGRADE v38 -> v39] Add column SERIES.SHOWSTATUS");

		db.executeSQLThrow("ALTER TABLE main.SERIES ADD COLUMN SHOWSTATUS TINYINT NOT NULL DEFAULT 0");

		return new ArrayList<>();
	}
}
