package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.filesystem.FSPath;

import java.util.ArrayList;
import java.util.List;

public class Migration_10_11 extends DBMigration {

	public Migration_10_11() {
		this(null, null, null, null, true);
	}

	public Migration_10_11(GenericDatabase db, CCProperties ccprops, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, ccprops, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "1.9";
	}

	@Override
	public String getToVersion() {
		return "11";
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[UPGRADE 1.9 -> v11] Added ParentGroup identifier to groups");

		db.executeSQLThrow("ALTER TABLE GROUPS ADD COLUMN PARENTGROUP VARCHAR(256)");
		db.executeSQLThrow("UPDATE GROUPS SET PARENTGROUP = \"\"");

		db.executeSQLThrow("ALTER TABLE GROUPS ADD COLUMN VISIBLE BIT");
		db.executeSQLThrow("UPDATE GROUPS SET VISIBLE = 1");

		return new ArrayList<>();
	}
}
