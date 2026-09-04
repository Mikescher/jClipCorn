package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.sqlwrapper.CCSQLTableDef;
import de.jClipCorn.util.stream.CCStreams;

import java.util.List;

import static de.jClipCorn.database.driver.DatabaseStructure.*;

public abstract class DBMigration {
	protected final GenericDatabase db;
	protected final FSPath databaseDirectory;
	protected final String databaseName;
	protected final boolean readonly;

	protected DBMigration(GenericDatabase db, FSPath databaseDirectory, String databaseName, boolean readonly) {
		this.db = db;
		this.databaseDirectory = databaseDirectory;
		this.databaseName = databaseName;
		this.readonly = readonly;
	}

	public abstract String getFromVersion();
	public abstract String getToVersion();

	/** The INFO table holding the version this migration bumps - {@code userdata.INFO} for a {@link UserDataMigration}. */
	protected CCSQLTableDef getInfoTable() {
		return TAB_INFO; // override me
	}

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
		List<UpgradeAction> actions;
		try {
			actions = run();
		} catch (Exception e) {
			if (dotransaction) { try { db.executeSQLThrow("ROLLBACK TRANSACTION"); } catch (Exception e2) { /* nothing was open */ } }
			throw e;
		}
		// ======================================================

		db.executeSQLThrow(String.format("UPDATE %s SET %s='%s' WHERE %s='%s'",  //$NON-NLS-1$
				getInfoTable().qualifiedName(),
				COL_INFO_VALUE.Name,
				getToVersion(),
				COL_INFO_KEY.Name,
				INFOKEY_DBVERSION.Key));

		if (disableForeignKeys) db.executeSQLThrow("PRAGMA foreign_keys = ON;");
		if (dotransaction) db.executeSQLThrow("COMMIT TRANSACTION");

		if (vacuum) db.executeSQLThrow("VACUUM " + getInfoTable().Schema);

		return actions;
	}
}
