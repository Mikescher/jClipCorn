package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.exceptions.CCFormatException;

public class EOptUUIDProp extends EOptProperty<CCUUID> {
	public EOptUUIDProp(String name, Opt<CCUUID> defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	@Override
	protected String serializeInnerToString(CCUUID v) {
		return v.toString();
	}

	@Override
	protected Object serializeInnerToDatabaseValue(CCUUID v) {
		return v.toString();
	}

	@Override
	protected CCUUID deserializeInnerFromString(String v) throws CCFormatException {
		return CCUUID.parse(v);
	}

	@Override
	protected CCUUID deserializeInnerFromDatabaseValue(Object v) throws CCFormatException {
		return CCUUID.parse((String) v);
	}

	@Override
	protected boolean valueInnerEquals(CCUUID a, CCUUID b) {
		return CCUUID.isEqual(a, b);
	}
}
