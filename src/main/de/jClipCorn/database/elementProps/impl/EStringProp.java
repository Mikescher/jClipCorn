package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.exceptions.CCFormatException;

public class EStringProp extends EProperty<String> {
	public EStringProp(String name, String defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	@Override
	public String serializeToString() {
		return get();
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get();
	}

	@Override
	public void deserializeFromString(String v) throws CCFormatException {
		set(v);
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) throws CCFormatException {
		set((String)v);
	}
}
