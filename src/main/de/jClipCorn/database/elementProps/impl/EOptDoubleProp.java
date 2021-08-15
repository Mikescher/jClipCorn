package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.exceptions.CCFormatException;

import java.util.Objects;

public class EOptDoubleProp extends EOptProperty<Double> {
	public EOptDoubleProp(String name, Opt<Double> defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	@Override
	protected String serializeInnerToString(Double v) {
		return String.valueOf(v);
	}

	@Override
	protected Object serializeInnerToDatabaseValue(Double v) {
		return v;
	}

	@Override
	protected Double deserializeInnerFromString(String v) {
		return Double.parseDouble(v);
	}

	@Override
	protected Double deserializeInnerFromDatabaseValue(Object v) {
		return (double)v;
	}

	@Override
	protected boolean valueInnerEquals(Double a, Double b) {
		return Objects.equals(a, b);
	}
}
