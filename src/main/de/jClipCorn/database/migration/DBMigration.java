package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.stream.CCStreams;

import java.util.List;

import static de.jClipCorn.database.driver.DatabaseStructure.*;
import static de.jClipCorn.database.driver.DatabaseStructure.INFOKEY_DBVERSION;

public abstract class DBMigration {
	protected final GenericDatabase db;
	protected final CCProperties ccprops;
	protected final FSPath databaseDirectory;
	protected final String databaseName;
	protected final boolean readonly;

	protected DBMigration(GenericDatabase db, CCProperties ccprops, FSPath databaseDirectory, String databaseName, boolean readonly) {
		this.db = db;
		this.ccprops = ccprops;
		this.databaseDirectory = databaseDirectory;
		this.databaseName = databaseName;
		this.readonly = readonly;
	}

	public abstract String getFromVersion();
	public abstract String getToVersion();

	protected boolean backupAndRestoreTrigger() {
		return false; // override me
	}

	protected boolean runInTransaction() {
		return true; // override me
	}

	protected boolean disableForeignKeysDuringMigration() {
		return true; // override me
	}

	protected boolean vacuumAfterRun() {
		return true; // override me
	}

	protected abstract List<UpgradeAction> run() throws Exception;

	@SuppressWarnings("nls")
	public List<UpgradeAction> migrate() throws Exception{

		var backupTrigger = this.backupAndRestoreTrigger();
		var dotransaction = this.runInTransaction();
		var disableForeignKeys = this.disableForeignKeysDuringMigration();
		var vacuum = this.vacuumAfterRun();

		if (dotransaction) db.executeSQLThrow("BEGIN TRANSACTION");
		if (disableForeignKeys) db.executeSQLThrow("PRAGMA foreign_keys = OFF;");

		if (backupTrigger) {
			for (String trigger : CCStreams.iterate(db.listTrigger()).filter(t -> t.startsWith("JCCTRIGGER_")))
			{
				db.executeSQLThrow("DROP TRIGGER ["+trigger+"]");
			}
		}

		// ======================================================
		var actions = run();
		// ======================================================

		if (backupTrigger) {
			if (db.querySingleStringSQL("SELECT IVALUE FROM INFO WHERE IKEY = 'HISTORY_ENABLED'", 0).equals("1"))
			{
				for (var trigger : CCDatabaseHistory.createTriggerStatements()) db.executeSQLThrow(trigger.Item2);
			}
		}

		db.executeSQLThrow(String.format("UPDATE %s SET %s='%s' WHERE %s='%s'",  //$NON-NLS-1$
				TAB_INFO.Name,
				COL_INFO_VALUE.Name,
				getToVersion(),
				COL_INFO_KEY.Name,
				INFOKEY_DBVERSION.Key));

		if (disableForeignKeys) db.executeSQLThrow("PRAGMA foreign_keys = ON;");
		if (dotransaction) db.executeSQLThrow("COMMIT TRANSACTION");

		if (vacuum) db.executeSQLThrow("VACUUM");

		return actions;
	}
}
