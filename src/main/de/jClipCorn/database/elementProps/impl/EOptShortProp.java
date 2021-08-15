package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.exceptions.CCFormatException;

import java.util.Objects;

public class EOptShortProp extends EOptProperty<Short> {
	public EOptShortProp(String name, Opt<Short> defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	@Override
	protected String serializeInnerToString(Short v) {
		return String.valueOf((short)v);
	}

	@Override
	protected Object serializeInnerToDatabaseValue(Short v) {
		return v;
	}

	@Override
	protected Short deserializeInnerFromString(String v) {
		return Short.parseShort(v);
	}

	@Override
	protected Short deserializeInnerFromDatabaseValue(Object v) {
		return (short)v;
	}

	@Override
	protected boolean valueInnerEquals(Short a, Short b) {
		return Objects.equals(a, b);
	}
}
