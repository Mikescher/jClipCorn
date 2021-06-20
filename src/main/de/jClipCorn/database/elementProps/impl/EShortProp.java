package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.exceptions.CCFormatException;

public class EShortProp extends EProperty<Short> {
	public EShortProp(String name, Short defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	@Override
	public String serializeToString() {
		return String.valueOf(get());
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get();
	}

	@Override
	public void deserializeFromString(String v) throws CCFormatException {
		set(Short.parseShort(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) throws CCFormatException {
		set((short)v);
	}
}
