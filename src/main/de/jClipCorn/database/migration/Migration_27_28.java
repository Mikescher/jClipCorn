package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.filesystem.FSPath;

import java.util.ArrayList;
import java.util.List;

public class Migration_27_28 extends DBMigration {

	public Migration_27_28(GenericDatabase db, CCProperties ccprops, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, ccprops, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "27";
	}

	@Override
	public String getToVersion() {
		return "28";
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return false;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[MIGRATION] Upgrading database from version 27 to 28");

		db.executeSQLThrow("ALTER TABLE MOVIES ADD COLUMN \"CHECKSUM_CRC32\" VARCHAR DEFAULT NULL");
		db.executeSQLThrow("ALTER TABLE MOVIES ADD COLUMN \"CHECKSUM_MD5\" VARCHAR DEFAULT NULL");
		db.executeSQLThrow("ALTER TABLE MOVIES ADD COLUMN \"CHECKSUM_SHA256\" VARCHAR DEFAULT NULL");
		db.executeSQLThrow("ALTER TABLE MOVIES ADD COLUMN \"CHECKSUM_SHA512\" VARCHAR DEFAULT NULL");

		db.executeSQLThrow("ALTER TABLE EPISODES ADD COLUMN \"CHECKSUM_CRC32\" VARCHAR DEFAULT NULL");
		db.executeSQLThrow("ALTER TABLE EPISODES ADD COLUMN \"CHECKSUM_MD5\" VARCHAR DEFAULT NULL");
		db.executeSQLThrow("ALTER TABLE EPISODES ADD COLUMN \"CHECKSUM_SHA256\" VARCHAR DEFAULT NULL");
		db.executeSQLThrow("ALTER TABLE EPISODES ADD COLUMN \"CHECKSUM_SHA512\" VARCHAR DEFAULT NULL");

		return new ArrayList<>();
	}

}
