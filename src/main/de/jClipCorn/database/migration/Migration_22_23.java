package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.filesystem.FSPath;

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
	protected boolean backupAndRestoreTrigger() {
		return true;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[UPGRADE v22 -> v23] Convert GROUP color from INTEGER to HEX VARCHAR");

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

		return new ArrayList<>();
	}
}
