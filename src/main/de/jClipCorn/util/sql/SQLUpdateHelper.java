package de.jClipCorn.util.sql;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.util.datatypes.DoubleString;

public class SQLUpdateHelper extends SQLHelper {
	private DoubleString whereClauses;
	
	private List<DoubleString> fields = new ArrayList<>();
	
	public SQLUpdateHelper(String tabName, DoubleString whereClauses) {
		super(tabName);
		this.whereClauses = whereClauses;
	}

	public void addField(String field, String val) {
		fields.add(new DoubleString(field, val));
	}

	public void addPreparedField(String field) {
		addField(field, "?"); //$NON-NLS-1$
	}
	
	@Override
	@SuppressWarnings("nls")
	public String get() {
		String rformat = "UPDATE %s SET %s WHERE %s=%s";
		
		StringBuilder cnames = new StringBuilder();
		for (int i = 0; i < fields.size(); i++) {
			if (i > 0) {
				cnames.append(", ");
			}
			cnames.append(sqlEscape(fields.get(i).get1()));
			cnames.append("=");
			cnames.append(sqlEscape(fields.get(i).get2()));
		}
		
		return String.format(rformat, tabname, cnames.toString(), sqlEscape(whereClauses.get1()), sqlEscape(whereClauses.get2()));
	}
}
