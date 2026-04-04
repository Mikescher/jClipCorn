package de.jClipCorn.database.migration;

import de.jClipCorn.features.log.CCLog;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static de.jClipCorn.database.driver.DatabaseStructure.*;

@SuppressWarnings("nls")
public class HistoryDatabaseMigrator {

	public static final String HISTORYDB_VERSION = "1";

	// When adding migrations, add them here in order
	// private static final List<...> MIGRATION_LIST = List.of();

	private final Connection connection;
	private final boolean readonly;

	public HistoryDatabaseMigrator(Connection connection, boolean readonly) {
		this.connection = connection;
		this.readonly = readonly;
	}

	private String getDBVersion() throws SQLException {
		try (Statement s = connection.createStatement()) {
			ResultSet rs = s.executeQuery(String.format("SELECT %s FROM %s WHERE %s = '%s'",
					COL_INFO_VALUE.Name,
					TAB_INFO.Name,
					COL_INFO_KEY.Name,
					INFOKEY_DBVERSION.Key));
			if (rs.next()) {
				String v = rs.getString(1);
				rs.close();
				return v;
			}
			rs.close();
			throw new SQLException("VERSION_DB not found in INFO table");
		}
	}

	public void tryUpgrade() {
		try {
			String version = getDBVersion();

			if (version.equals(HISTORYDB_VERSION)) return;

			CCLog.addInformation("History DB: Migrate from " + version + " to " + HISTORYDB_VERSION);

			if (readonly) {
				CCLog.addInformation("History DB: Migration skipped due to readonly mode");
				return;
			}

			// Currently no migrations exist (we start at version 1)
			// When migrations are added, loop through them here similar to DatabaseMigrator.tryUpgrade()

			if (!getDBVersion().equals(HISTORYDB_VERSION)) {
				throw new Exception("History DB version mismatch after migration");
			}

			CCLog.addInformation("History DB: Migration completed successfully");

		} catch (Exception e) {
			CCLog.addError("History DB: Migration failed", e);
		}
	}
}
