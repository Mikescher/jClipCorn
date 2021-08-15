package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.exceptions.CCFormatException;

public class EOptStringProp extends EOptProperty<String> {
	public EOptStringProp(String name, Opt<String> defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	@Override
	protected String serializeInnerToString(String v) {
		return v;
	}

	@Override
	protected Object serializeInnerToDatabaseValue(String v) {
		return v;
	}

	@Override
	protected String deserializeInnerFromString(String v) {
		return v;
	}

	@Override
	protected String deserializeInnerFromDatabaseValue(Object v) {
		return (String)v;
	}

	@Override
	protected boolean valueInnerEquals(String a, String b) {
		return Str.equals(a, b);
	}
}
