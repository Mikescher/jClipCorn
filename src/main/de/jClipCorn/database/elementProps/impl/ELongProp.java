package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.exceptions.CCFormatException;

public class ELongProp extends EProperty<Long> {
	public ELongProp(String name, Long defValue, IPropertyParent p, EPropertyType t) {
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
		set(Long.parseLong(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) throws CCFormatException {
		set((long)v);
	}
}
