package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.exceptions.CCFormatException;

import java.util.Objects;

public class EOptLongProp extends EOptProperty<Long> {
	public EOptLongProp(String name, Opt<Long> defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	@Override
	protected String serializeInnerToString(Long v) {
		return String.valueOf(v);
	}

	@Override
	protected Object serializeInnerToDatabaseValue(Long v) {
		return v;
	}

	@Override
	protected Long deserializeInnerFromString(String v) throws CCFormatException {
		return Long.parseLong(v);
	}

	@Override
	protected Long deserializeInnerFromDatabaseValue(Object v) throws CCFormatException {
		return (long)v;
	}

	@Override
	protected boolean valueInnerEquals(Long a, Long b) {
		return Objects.equals(a, b);
	}
}
