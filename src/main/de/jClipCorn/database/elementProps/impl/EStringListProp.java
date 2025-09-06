package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.databaseElement.columnTypes.CCStringList;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.exceptions.CCFormatException;

public class EStringListProp extends EProperty<CCStringList> {
	public EStringListProp(String name, CCStringList defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public boolean tryAddValue(String value) {
		CCStringList newList = get().getAdd(value);
		if (newList.equals(get())) return false;
		set(newList);
		return true;
	}

	public boolean tryRemoveValue(String value) {
		CCStringList newList = get().getRemove(value);
		if (newList.equals(get())) return false;
		set(newList);
		return true;
	}

	public boolean contains(String value) {
		return get().contains(value);
	}

	@Override
	public String serializeToString() {
		return get().serialize();
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get().serialize();
	}

	@Override
	public void deserializeFromString(String v) throws CCFormatException {
		set(CCStringList.deserialize(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) throws CCFormatException {
		set(CCStringList.deserialize((String)v));
	}

	@Override
	public boolean valueEquals(CCStringList a, CCStringList b) {
		if (a == null && b == null) return true;
		if (a == null || b == null) return false;
		return a.equals(b);
	}
}