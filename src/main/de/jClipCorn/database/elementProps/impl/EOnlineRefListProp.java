package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.exceptions.CCFormatException;

public class EOnlineRefListProp extends EProperty<CCOnlineReferenceList> {
	public EOnlineRefListProp(String name, CCOnlineReferenceList defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(String v) throws CCFormatException {
		set(CCOnlineReferenceList.parse(v));
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
		set(CCOnlineReferenceList.parse(v));
	}

	@Override
	public void deserializeValueFromDatabaseValue(Object v) throws Exception {
		set(CCOnlineReferenceList.parse((String)v));
	}
}
