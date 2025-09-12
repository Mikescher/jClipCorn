package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.filesystem.FSPath;

import java.util.List;

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

	public abstract List<UpgradeAction> migrate() throws Exception;
}
