package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageSet;
import de.jClipCorn.database.elementProps.IPropertyParent;

public class ELanguageSetProp extends EProperty<CCDBLanguageSet> {
	public ELanguageSetProp(String name, CCDBLanguageSet defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(long v) {
		set(CCDBLanguageSet.fromBitmask(v));
	}

	@Override
	public String serializeToString() {
		return get().serializeToString();
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get().serializeToLong();
	}

	@Override
	public void deserializeFromString(String v) {
		set(CCDBLanguageSet.parseFromString(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) {
		set((Long)v);
	}

	@Override
	public boolean valueEquals(CCDBLanguageSet a, CCDBLanguageSet b) {
		if (a == null && b == null) return true;
		if (a == null || b == null) return false;
		return a.isEqual(b);
	}
}
