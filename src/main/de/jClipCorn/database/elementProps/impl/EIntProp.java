package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.exceptions.CCFormatException;
import org.jetbrains.annotations.NonNls;

import java.util.Objects;

public class EIntProp extends EProperty<Integer> {
	@NonNls
	public EIntProp(String name, Integer defValue, IPropertyParent p, EPropertyType t) {
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
		set(Integer.parseInt(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) throws CCFormatException {
		set((int)v);
	}

	@Override
	public boolean valueEquals(Integer a, Integer b) {
		return Objects.equals(a, b);
	}
}
