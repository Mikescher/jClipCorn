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
	public String serializeToString() {
		return get().toSerializationString();
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get().toSerializationString();
	}

	@Override
	public void deserializeFromString(String v) throws CCFormatException {
		set(CCOnlineReferenceList.parse(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) throws CCFormatException {
		set(CCOnlineReferenceList.parse((String)v));
	}
}
