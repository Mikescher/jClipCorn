package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.exceptions.CCFormatException;

import java.util.Objects;

public class EDoubleProp extends EProperty<Double> {
	public EDoubleProp(String name, Double defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	@Override
	public String serializeToString() {
		return Double.toString(get());
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get();
	}

	@Override
	public void deserializeFromString(String v) {
		set(Double.parseDouble(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) {
		set((double)v);
	}

	@Override
	public boolean valueEquals(Double a, Double b) {
		return Objects.equals(a, b);
	}
}
