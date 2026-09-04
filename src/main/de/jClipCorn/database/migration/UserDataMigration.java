package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.sqlwrapper.CCSQLTableDef;

import static de.jClipCorn.database.driver.DatabaseStructure.TAB_UD_INFO;

/**
 * A migration of {@code ClipCornUserData.db}, run by {@link UserDataDatabaseMigrator}.
 *
 * Identical to a main-database migration except that the version it bumps lives in
 * {@code userdata.INFO} - which also keeps the bump inside the migration transaction.
 */
public abstract class UserDataMigration extends DBMigration {

	protected UserDataMigration(GenericDatabase db, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, databaseDirectory, databaseName, readonly);
	}

	@Override
	protected CCSQLTableDef getInfoTable() {
		return TAB_UD_INFO;
	}
}
