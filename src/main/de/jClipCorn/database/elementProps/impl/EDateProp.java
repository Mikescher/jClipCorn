package de.jClipCorn.database.elementValues;

import de.jClipCorn.util.datetime.CCDate;

import java.sql.Date;

public class EDateProp extends EProperty<CCDate> {
	public EDateProp(String name, CCDate defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(Date v) {
		set(CCDate.create(v));
	}

	@Override
	public String serializeValueToString() {
		return get().toStringSQL();
	}

	@Override
	public Object serializeValueToDatabaseValue() {
		return get().toStringSQL();
	}

	@Override
	public void deserializeValueFromString(String v) throws Exception {
		set(CCDate.deserializeSQL(v));
	}

	@Override
	public void deserializeValueFromDatabaseValue(Object v) throws Exception {
		set(CCDate.deserializeSQL((String)v));
	}
}
