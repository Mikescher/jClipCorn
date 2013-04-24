package de.jClipCorn.util.sql;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.util.DoubleString;

public class SQLInsertHelper extends SQLHelper {
	private List<DoubleString> fields = new ArrayList<>();

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

		StringBuilder cnames = new StringBuilder();
		StringBuilder cvals = new StringBuilder();
		for (int i = 0; i < fields.size(); i++) {
			if (i > 0) {
				cnames.append(", ");
				cvals.append(", ");
			}
			cnames.append(fields.get(i).get1());
			cvals.append(fields.get(i).get2());
		}

		return String.format(rformat, tabname, cnames.toString(), cvals.toString());
	}
}
