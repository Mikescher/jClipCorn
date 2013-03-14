package de.jClipCorn.util.sql;

import java.util.ArrayList;

import de.jClipCorn.util.DoubleString;

public class SQLSelectHelper extends SQLHelper {

	private String target = "*"; //$NON-NLS-1$
	private SQLOrder order = SQLOrder.ASC;
	private String orderBy = null;
	private ArrayList<DoubleString> wheres = new ArrayList<>();

	public SQLSelectHelper(String tabname) {
		super(tabname);
	}

	public void addWhere(String field, String val) {
		wheres.add(new DoubleString(field, val));
	}

	@SuppressWarnings("nls")
	@Override
	public String get() {
		String sql = String.format("SELECT %s FROM %s", target, tabname);
		if (!wheres.isEmpty()) {
			sql += " WHERE";
			for (int i = 0; i < wheres.size(); i++) {
				if (i > 0) {
					sql += " AND";
				}
				sql += " " + wheres.get(i).get1() + "=" + wheres.get(i).get2();
			}
		}

		if (orderBy != null) {
			sql += String.format(" ORDER BY %s %s", orderBy, order == SQLOrder.ASC ? "ASC" : "DESC"); //Note that you CAN compare enums with == , RTFM !!!
		}
		
		return sql;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setOrder(SQLOrder order) {
		this.order = order;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

}
