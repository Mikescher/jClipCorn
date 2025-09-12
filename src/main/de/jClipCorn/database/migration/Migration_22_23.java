package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.stream.CCStreams;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Migration_22_23 extends DBMigration {

	public Migration_22_23(GenericDatabase db, CCProperties ccprops, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, ccprops, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "22";
	}

	@Override
	public String getToVersion() {
		return "23";
	}

	@Override
	@SuppressWarnings("nls")
	public List<UpgradeAction> migrate() throws Exception {
		CCLog.addInformation("[UPGRADE v22 -> v23] Convert GROUP color from INTEGER to HEX VARCHAR");

		db.executeSQLThrow("BEGIN TRANSACTION");
		db.executeSQLThrow("PRAGMA foreign_keys = OFF;");
		{
			for (String trigger : CCStreams.iterate(db.listTrigger()).filter(t -> t.startsWith("JCCTRIGGER_")))
			{
				db.executeSQLThrow("DROP TRIGGER ["+trigger+"]");
			}

			// Create new table with VARCHAR COLOR column
			db.executeSQLThrow("CREATE TABLE \"sqlb_temp_table_1\" ( \"NAME\" TEXT NOT NULL, \"ORDERING\" INTEGER NOT NULL, \"COLOR\" VARCHAR NOT NULL, \"SERIALIZE\" BOOLEAN NOT NULL, \"PARENTGROUP\" TEXT NOT NULL, \"VISIBLE\" BOOLEAN NOT NULL, PRIMARY KEY(\"NAME\") );");

			// Convert integer colors to hex format - use universal formula that works for all values
			db.executeSQLThrow("INSERT INTO \"main\".\"sqlb_temp_table_1\" (\"NAME\", \"ORDERING\", \"COLOR\", \"SERIALIZE\", \"PARENTGROUP\", \"VISIBLE\") " +
					"SELECT \"NAME\", \"ORDERING\", " +
					"printf('#%02X%02X%02X', " +
					"(\"COLOR\" & 16711680) >> 16, " +  // Red: (color & 0xFF0000) >> 16
					"(\"COLOR\" & 65280) >> 8, " +       // Green: (color & 0xFF00) >> 8
					"(\"COLOR\" & 255)) " +              // Blue: (color & 0xFF)
					"as COLOR, " +
					"\"SERIALIZE\", \"PARENTGROUP\", \"VISIBLE\" FROM \"main\".\"GROUPS\"");

			db.executeSQLThrow("DROP TABLE \"main\".\"GROUPS\"");
			db.executeSQLThrow("ALTER TABLE \"main\".\"sqlb_temp_table_1\" RENAME TO \"GROUPS\"");

			if (db.querySingleStringSQL("SELECT IVALUE FROM INFO WHERE IKEY = 'HISTORY_ENABLED'", 0).equals("1"))
			{
				for (var trigger : CCDatabaseHistory.createTriggerStatements()) db.executeSQLThrow(trigger.Item2);
			}
		}
		db.executeSQLThrow("PRAGMA foreign_keys = ON;");
		db.executeSQLThrow("COMMIT TRANSACTION");

		db.executeSQLThrow("VACUUM");

		return new ArrayList<>();
	}
}
