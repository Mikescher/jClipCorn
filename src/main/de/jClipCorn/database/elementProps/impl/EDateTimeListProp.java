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
	public String serializeValueToString() {
		return get().toSerializationString();
	}

	@Override
	public Object serializeValueToDatabaseValue() {
		return get().toSerializationString();
	}

	@Override
	public void deserializeValueFromString(String v) throws Exception {
		set(CCDateTimeList.parse(v));
	}

	@Override
	public void deserializeValueFromDatabaseValue(Object v) throws Exception {
		set(CCDateTimeList.parse((String)v));
	}
}
