package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.exceptions.CCFormatException;

import java.util.Objects;

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
	public void deserializeFromString(String v) {
		set(Long.parseLong(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) {
		set((long)v);
	}

	@Override
	public boolean valueEquals(Long a, Long b) {
		return Objects.equals(a, b);
	}
}
