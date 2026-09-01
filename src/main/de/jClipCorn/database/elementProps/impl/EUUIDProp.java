package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.exceptions.CCFormatException;

public class EUUIDProp extends EProperty<CCUUID> {
	public EUUIDProp(String name, CCUUID defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	@Override
	public String serializeToString() {
		return get().toString();
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get().toString();
	}

	@Override
	public void deserializeFromString(String v) throws CCFormatException {
		set(CCUUID.parse(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) throws CCFormatException {
		set(CCUUID.parse((String) v));
	}

	@Override
	public boolean valueEquals(CCUUID a, CCUUID b) {
		return CCUUID.isEqual(a, b);
	}
}
