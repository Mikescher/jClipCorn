package de.jClipCorn.util.sql;

public abstract class SQLHelper {
	protected final String tabname;
	
	public SQLHelper(String tabname) {
		this.tabname = tabname;
	}

	public abstract String get();
}
