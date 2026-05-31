package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.exceptions.CCFormatException;

public class ELanguageListProp extends EProperty<CCDBLanguageList> {
	public ELanguageListProp(String name, CCDBLanguageList defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(String v) throws CCFormatException {
		set(CCDBLanguageList.parseFromString(v));
	}

	public void setOnly(String v) throws CCFormatException {
		setOnly(CCDBLanguageList.parseFromString(v));
	}

	@Override
	public String serializeToString() {
		return get().serializeToString();
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get().asJSONArray();
	}

	@Override
	public void deserializeFromString(String v) throws CCFormatException {
		set(CCDBLanguageList.parseFromString(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) {
		set(CCDBLanguageList.fromJSONArray((String)v));
	}

	@Override
	public boolean valueEquals(CCDBLanguageList a, CCDBLanguageList b) {
		if (a == null && b == null) return true;
		if (a == null || b == null) return false;
		return a.isEqual(b);
	}
}
