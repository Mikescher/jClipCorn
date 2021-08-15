package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.databaseElement.columnTypes.CCSingleTag;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.DatabaseUpdateException;

public class ETagListProp extends EProperty<CCTagList> {
	public ETagListProp(String name, CCTagList defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(short v) {
		set(CCTagList.fromShort(v));
	}

	public void switchTag(int v) {
		set(get().getSwitchTag(v));
	}

	public void switchTag(CCSingleTag v) {
		set(get().getSwitchTag(v));
	}

	public void set(CCSingleTag t, boolean v) {
		set(get().getSetTag(t, v));
	}

	public void setWithException(CCSingleTag t, boolean v) throws DatabaseUpdateException {
		setWithException(get().getSetTag(t, v));
	}

	public void set(int t, boolean v) {
		set(get().getSetTag(t, v));
	}

	public boolean get(int t) {
		return get().getTag(t);
	}

	public boolean get(CCSingleTag t) {
		return get().getTag(t);
	}

	@Override
	public String serializeToString() {
		return get().serialize();
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get().asShort();
	}

	@Override
	public void deserializeFromString(String v) throws CCFormatException {
		set(CCTagList.deserialize(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) {
		set(CCTagList.fromShort((short)v));
	}

	@Override
	public boolean valueEquals(CCTagList a, CCTagList b) {
		if (a == null && b == null) return true;
		if (a == null || b == null) return false;
		return a.isEqual(b);
	}
}
