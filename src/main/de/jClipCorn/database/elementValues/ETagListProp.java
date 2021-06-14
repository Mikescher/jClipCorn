package de.jClipCorn.database.elementValues;

import de.jClipCorn.database.databaseElement.columnTypes.CCSingleTag;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
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
}
