package de.jClipCorn.util.sql;

import java.util.ArrayList;

import de.jClipCorn.util.DoubleString;

public class SQLUpdateHelper extends SQLHelper {
	private DoubleString whereClauses;
	
	private ArrayList<DoubleString> fields = new ArrayList<>();
	
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
		
		String cnames = "";
		for (int i = 0; i < fields.size(); i++) {
			if (i > 0) {
				cnames += ", ";
			}
			cnames += fields.get(i).get1() + "=" + fields.get(i).get2();
		}
		
		return String.format(rformat, tabname, cnames, whereClauses.get1(), whereClauses.get2());
	}
}
