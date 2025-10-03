package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.filesystem.FSPath;

import java.util.ArrayList;
import java.util.List;

public class Migration_26_27 extends DBMigration {

	public Migration_26_27(GenericDatabase db, CCProperties ccprops, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, ccprops, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "26";
	}

	@Override
	public String getToVersion() {
		return "27";
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return false;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[MIGRATION] Upgrading database from version 26 to 27");

		var maxid  = db.querySingleIntSQL("SELECT MAX(ID) FROM COVERS", 0);
		var lastid = db.querySingleIntSQL("SELECT IVALUE FROM INFO WHERE IKEY = 'LAST_COVERID'", 0);

		// Add new PARTS column to MOVIES table
		db.executeSQLThrow("UPDATE INFO SET IVALUE = '" + Math.max(maxid, lastid) + "' WHERE IKEY = 'LAST_COVERID'");

		return new ArrayList<>();
	}

}