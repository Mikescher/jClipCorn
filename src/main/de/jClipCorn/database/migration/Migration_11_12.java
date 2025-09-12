package de.jClipCorn.database.migration;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.List;

public class Migration_11_12 extends DBMigration {

	public Migration_11_12(GenericDatabase db, CCProperties ccprops, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, ccprops, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "11";
	}

	@Override
	public String getToVersion() {
		return "12";
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[UPGRADE v11 -> v12] Extend language column size in elements table");
		CCLog.addInformation("[UPGRADE v11 -> v12] Convert language column in elements table");
		CCLog.addInformation("[UPGRADE v11 -> v12] Add language column to episodes");
		CCLog.addInformation("[UPGRADE v11 -> v12] Update language for episodes");
		CCLog.addInformation("[UPGRADE v11 -> v12] Update language for series");
		CCLog.addInformation("[UPGRADE v11 -> v12] Convert language column in episodes table");

		if (db.getDBType() != CCDatabaseDriver.SQLITE) db.executeSQLThrow("ALTER TABLE ELEMENTS ALTER COLUMN LANGUAGE BIGINT");

		db.executeSQLThrow("ALTER TABLE EPISODES ADD COLUMN LANGUAGE BIGINT");

		for (Object[] series : db.querySQL("SELECT LANGUAGE, SERIESID, TYPE FROM ELEMENTS WHERE TYPE=1", 3))
		{
			for (Object[] season : db.querySQL("SELECT SEASONID, SERIESID FROM SEASONS WHERE SERIESID="+series[1], 2))
			{
				db.executeSQLThrow("UPDATE EPISODES SET LANGUAGE="+series[0]+" WHERE SEASONID="+season[0]);
			}
		}

		for (CCDBLanguage lng : CCStreams.iterate(CCDBLanguage.values()).autosortByProperty(CCDBLanguage::asInt).reverse())
		{
			db.executeSQLThrow("UPDATE ELEMENTS SET LANGUAGE = "+lng.asBitMask()+" WHERE LANGUAGE = "+lng.asInt());
			db.executeSQLThrow("UPDATE EPISODES SET LANGUAGE = "+lng.asBitMask()+" WHERE LANGUAGE = "+lng.asInt());
		}

		db.executeSQLThrow("UPDATE ELEMENTS SET LANGUAGE=0 WHERE TYPE=1");

		return new ArrayList<>();
	}
}
