package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.filesystem.FSPath;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Makes the year columns nullable:
 *  - MOVIES.MOVIEYEAR   (was SMALLINT NOT NULL -> SMALLINT)
 *  - SEASONS.SEASONYEAR (was SMALLINT NOT NULL -> SMALLINT)
 *
 * SQLite cannot drop a NOT NULL constraint in-place, so each column is rebuilt
 * (rename old -> add new nullable -> copy values -> drop old). Existing values are preserved.
 * The physical column moves to the end of the table, but the schema is compared by column-name,
 * so this is irrelevant (see CCSQLTableDef.isEqual).
 */
public class Migration_33_34 extends DBMigration {

	public Migration_33_34(GenericDatabase db, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "33"; //$NON-NLS-1$
	}

	@Override
	public String getToVersion() {
		return "34"; //$NON-NLS-1$
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return true;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[UPGRADE v33 -> v34] Make MOVIES.MOVIEYEAR and SEASONS.SEASONYEAR nullable");

		makeColumnNullable("MOVIES",  "MOVIEYEAR");
		makeColumnNullable("SEASONS", "SEASONYEAR");

		return new ArrayList<>();
	}

	@SuppressWarnings("nls")
	private void makeColumnNullable(String table, String column) throws SQLException {
		db.executeSQLThrow("ALTER TABLE " + table + " RENAME COLUMN " + column + " TO " + column + "_OLD");
		db.executeSQLThrow("ALTER TABLE " + table + " ADD COLUMN " + column + " SMALLINT");
		db.executeSQLThrow("UPDATE " + table + " SET " + column + " = " + column + "_OLD");
		db.executeSQLThrow("ALTER TABLE " + table + " DROP COLUMN " + column + "_OLD");
	}
}
