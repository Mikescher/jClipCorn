package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.filesystem.CCPath;

public class ECCPathProp extends EProperty<CCPath> {
	public ECCPathProp(String name, CCPath defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(String v) {
		set(CCPath.create(v));
	}

	@Override
	public String serializeToString() {
		return get().toString();
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get();
	}

	@Override
	public void deserializeFromString(String v) {
		set(CCPath.create(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) {
		set(CCPath.create((String)v));
	}

	@Override
	public boolean valueEquals(CCPath a, CCPath b) {
		return CCPath.isEqual(a, b);
	}
}
