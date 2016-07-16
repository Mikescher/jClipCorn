package de.jClipCorn.util.sql;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.util.DoubleString;

public class SQLSelectHelper extends SQLHelper {

	private String target = "*"; //$NON-NLS-1$
	private SQLOrder order = SQLOrder.ASC;
	private String orderBy = null;
	private List<DoubleString> wheres = new ArrayList<>();

	public SQLSelectHelper(String tabname) {
		super(tabname);
	}

	public void addWhere(String field, String val) {
		wheres.add(new DoubleString(field, val));
	}

	public void addPreparedWhere(String field) {
		addWhere(field, "?"); //$NON-NLS-1$
	}

	@SuppressWarnings("nls")
	@Override
	public String get() {
		StringBuilder sqlbuilder = new StringBuilder();
		sqlbuilder.append(String.format("SELECT %s FROM %s", sqlEscape(target), sqlEscape(tabname)));
		if (!wheres.isEmpty()) {
			sqlbuilder.append(" WHERE");
			for (int i = 0; i < wheres.size(); i++) {
				if (i > 0) {
					sqlbuilder.append(" AND");
				}
				sqlbuilder.append(" ");
				sqlbuilder.append(sqlEscape(wheres.get(i).get1()));
				sqlbuilder.append("=");
				sqlbuilder.append(sqlEscape(wheres.get(i).get2()));
			}
		}

		if (orderBy != null) {
			sqlbuilder.append(String.format(" ORDER BY %s %s", orderBy, order == SQLOrder.ASC ? "ASC" : "DESC")); //Note that you CAN compare enums with == , RTFM !!!
		}
		
		return sqlbuilder.toString();
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
