package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.filesystem.FSPath;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Makes the cover references nullable and replaces the nil-UUID sentinel with NULL:
 *  - MOVIES.COVERID  (was TEXT NOT NULL -> TEXT)
 *  - SERIES.COVERID  (was TEXT NOT NULL -> TEXT)
 *  - SEASONS.COVERID (was TEXT NOT NULL -> TEXT)
 *
 * The nil UUID passes CCUUID.isValid(), so as a value in a reference column it is indistinguishable
 * from a real - dangling - cover id without knowing the sentinel. NULL says "no cover" in a way every
 * reader already understands.
 *
 * SQLite cannot drop a NOT NULL constraint in-place, so each column is rebuilt
 * (rename old -> add new nullable -> copy values -> drop old), mapping the sentinel to NULL on the way.
 * The physical column moves to the end of the table, but the schema is compared by column-name,
 * so this is irrelevant (see CCSQLTableDef.isEqual).
 */
public class Migration_36_37 extends DBMigration {

	public Migration_36_37(GenericDatabase db, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "36"; //$NON-NLS-1$
	}

	@Override
	public String getToVersion() {
		return "37"; //$NON-NLS-1$
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return true;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[UPGRADE v36 -> v37] Make MOVIES.COVERID, SERIES.COVERID and SEASONS.COVERID nullable");

		makeCoverIdNullable("main.MOVIES");
		makeCoverIdNullable("main.SERIES");
		makeCoverIdNullable("main.SEASONS");

		return new ArrayList<>();
	}

	@SuppressWarnings("nls")
	private void makeCoverIdNullable(String table) throws SQLException {
		db.executeSQLThrow("ALTER TABLE " + table + " RENAME COLUMN COVERID TO COVERID_OLD");
		db.executeSQLThrow("ALTER TABLE " + table + " ADD COLUMN COVERID TEXT");
		db.executeSQLThrow("UPDATE " + table + " SET COVERID = CASE WHEN COVERID_OLD = '" + CCUUID.EMPTY + "' THEN NULL ELSE COVERID_OLD END");
		db.executeSQLThrow("ALTER TABLE " + table + " DROP COLUMN COVERID_OLD");
	}
}
