package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.CCFormatException;

public class EDateTimeListProp extends EProperty<CCDateTimeList> {
	public EDateTimeListProp(String name, CCDateTimeList defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(String v) throws CCFormatException {
		set(CCDateTimeList.parse(v));
	}

	public void add(CCDateTime v) {
		set(get().add(v));
	}

	@Override
	public String serializeToString() {
		return get().toSerializationString();
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get().toSerializationString();
	}

	@Override
	public void deserializeFromString(String v) throws CCFormatException {
		set(CCDateTimeList.parse(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) throws CCFormatException {
		set(CCDateTimeList.parse((String)v));
	}

	@Override
	public boolean valueEquals(CCDateTimeList a, CCDateTimeList b) {
		if (a == null && b == null) return true;
		if (a == null || b == null) return false;
		return a.isEqual(b);
	}
}
