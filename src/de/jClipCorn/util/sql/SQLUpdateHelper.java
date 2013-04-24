package de.jClipCorn.util.sql;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.util.DoubleString;

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
	
	@Override
	@SuppressWarnings("nls")
	public String get() {
		String rformat = "UPDATE %s SET %s WHERE %s=%s";
		
		StringBuilder cnames = new StringBuilder();
		for (int i = 0; i < fields.size(); i++) {
			if (i > 0) {
				cnames.append(", ");
			}
			cnames.append(fields.get(i).get1());
			cnames.append("=");
			cnames.append(fields.get(i).get2());
		}
		
		return String.format(rformat, tabname, cnames.toString(), whereClauses.get1(), whereClauses.get2());
	}
}
