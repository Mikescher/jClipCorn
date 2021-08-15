package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.exceptions.CCFormatException;

import java.util.Objects;

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
	public void deserializeFromString(String v) {
		set(Short.parseShort(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) {
		set((short)v);
	}

	@Override
	public boolean valueEquals(Short a, Short b) {
		return Objects.equals(a, b);
	}
}
