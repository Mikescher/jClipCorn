package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;

public class ELongProp extends EProperty<Long> {
	public ELongProp(String name, Long defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	@Override
	public String serializeValueToString() {
		return String.valueOf(get());
	}

	@Override
	public Object serializeValueToDatabaseValue() {
		return get();
	}

	@Override
	public void deserializeValueFromString(String v) throws Exception {
		set(Long.parseLong(v));
	}

	@Override
	public void deserializeValueFromDatabaseValue(Object v) throws Exception {
		set((long)v);
	}
}
