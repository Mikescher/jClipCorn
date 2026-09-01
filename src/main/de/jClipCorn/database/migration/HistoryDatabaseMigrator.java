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

	public static final String HISTORYDB_VERSION = "2";

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

	private void setDBVersion(String version) throws SQLException {
		try (Statement s = connection.createStatement()) {
			s.execute(String.format("UPDATE %s SET %s='%s' WHERE %s='%s'",
					TAB_INFO.Name,
					COL_INFO_VALUE.Name,
					version,
					COL_INFO_KEY.Name,
					INFOKEY_DBVERSION.Key));
		}
	}

	/** The archived history references the entity ids that Migration_34_35 replaced with UUIDs. */
	private void migrate_01_02() throws SQLException {
		try (Statement s = connection.createStatement()) { s.execute("BEGIN TRANSACTION"); }
		try {
			HistoryIdRewriter.rewrite(connection, TAB_HISTORY.Name);
			try (Statement s = connection.createStatement()) { s.execute("COMMIT TRANSACTION"); }
		} catch (SQLException e) {
			try (Statement s = connection.createStatement()) { s.execute("ROLLBACK TRANSACTION"); }
			throw e;
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

			while (!version.equals(HISTORYDB_VERSION)) {
				if (version.equals("1")) { migrate_01_02(); version = "2"; }
				else throw new Exception("no migration found to migrate history-db from version " + version);

				setDBVersion(version);
			}

			if (!getDBVersion().equals(HISTORYDB_VERSION)) {
				throw new Exception("History DB version mismatch after migration");
			}

			CCLog.addInformation("History DB: Migration completed successfully");

		} catch (Exception e) {
			CCLog.addError("History DB: Migration failed", e);
		}
	}
}
