package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.CCFormatException;

import java.sql.Date;

public class EDateProp extends EProperty<CCDate> {
	public EDateProp(String name, CCDate defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(Date v) {
		set(CCDate.create(v));
	}

	@Override
	public String serializeToString() {
		return get().toStringSQL();
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get().toStringSQL();
	}

	@Override
	public void deserializeFromString(String v) throws CCFormatException {
		set(CCDate.deserializeSQL(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) throws CCFormatException {
		set(CCDate.deserializeSQL((String)v));
	}
}
