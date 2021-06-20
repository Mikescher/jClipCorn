package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;

public class EDoubleProp extends EProperty<Double> {
	public EDoubleProp(String name, Double defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	@Override
	public String serializeValueToString() {
		return Double.toString(get());
	}

	@Override
	public Object serializeValueToDatabaseValue() {
		return get();
	}

	@Override
	public void deserializeValueFromString(String v) throws Exception {
		set(Double.parseDouble(v));
	}

	@Override
	public void deserializeValueFromDatabaseValue(Object v) throws Exception {
		set((double)v);
	}
}
