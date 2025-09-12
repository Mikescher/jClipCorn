package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.filesystem.FSPath;

import java.util.ArrayList;
import java.util.List;

public class Migration_09_10 extends DBMigration {

	public Migration_09_10(GenericDatabase db, CCProperties ccprops, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, ccprops, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "1.8";
	}

	@Override
	public String getToVersion() {
		return "1.9";
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[UPGRADE 1.8 -> 1.9] Create Table Groups");

		db.executeSQLThrow("CREATE TABLE GROUPS(NAME VARCHAR(256) PRIMARY KEY,ORDERING INTEGER NOT NULL,COLOR INTEGER NOT NULL,SERIALIZE BIT NOT NULL)");

		//afterConnectActions.add(new UpgradeAction() {
		//	@Override
		//	public void onAfterConnect(CCMovieList ml, CCDatabase db) {
		//		ml.recalculateGroupCache(false);
		//	}
		//});

		return new ArrayList<>();
	}
}
