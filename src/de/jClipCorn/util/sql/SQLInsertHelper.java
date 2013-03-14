package de.jClipCorn.util.sql;

import java.util.ArrayList;

import de.jClipCorn.util.DoubleString;

public class SQLInsertHelper extends SQLHelper {
	private ArrayList<DoubleString> fields = new ArrayList<>();

	public SQLInsertHelper(String tabName) {
		super(tabName);
	}

	public void addField(String field, String val) {
		fields.add(new DoubleString(field, val));
	}

	@Override
	@SuppressWarnings("nls")
	public String get() {
		String rformat = "INSERT INTO %s (%s) VALUES (%s)";

		String cnames = "";
		String cvals = "";
		for (int i = 0; i < fields.size(); i++) {
			if (i > 0) {
				cnames += ", ";
				cvals += ", ";
			}
			cnames += fields.get(i).get1();
			cvals += fields.get(i).get2();
		}

		return String.format(rformat, tabname, cnames, cvals);
	}
}
