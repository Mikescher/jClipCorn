package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.exceptions.CCFormatException;

import java.util.Objects;

public class EOptIntProp extends EOptProperty<Integer> {
	public EOptIntProp(String name, Opt<Integer> defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	@Override
	protected String serializeInnerToString(Integer v) {
		return String.valueOf(v);
	}

	@Override
	protected Object serializeInnerToDatabaseValue(Integer v) {
		return v;
	}

	@Override
	protected Integer deserializeInnerFromString(String v) throws CCFormatException {
		return Integer.parseInt(v);
	}

	@Override
	protected Integer deserializeInnerFromDatabaseValue(Object v) throws CCFormatException {
		return (int)v;
	}

	@Override
	protected boolean valueInnerEquals(Integer a, Integer b) {
		return Objects.equals(a, b);
	}
}
