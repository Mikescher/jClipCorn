package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;

public class EStringProp extends EProperty<String> {
	public EStringProp(String name, String defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	@Override
	public String serializeValueToString() {
		return get();
	}

	@Override
	public Object serializeValueToDatabaseValue() {
		return get();
	}

	@Override
	public void deserializeValueFromString(String v) throws Exception {
		set(v);
	}

	@Override
	public void deserializeValueFromDatabaseValue(Object v) throws Exception {
		set((String)v);
	}
}
