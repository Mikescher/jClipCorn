package de.jClipCorn.util.sql;

import de.jClipCorn.util.datatypes.DoubleString;

public class SQLDeleteHelper extends SQLHelper {
	private DoubleString whereClause = null;

	public SQLDeleteHelper(String tabName, DoubleString whereClause) {
		super(tabName);
		
		this.whereClause = whereClause;
	}

	@Override
	@SuppressWarnings("nls")
	public String get() {
		return String.format("DELETE FROM %s WHERE %s=?", tabname, whereClause.get1(), whereClause.get2());
	}
}
