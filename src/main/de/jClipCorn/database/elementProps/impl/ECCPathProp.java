package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.filesystem.CCPath;

public class ECCPathProp extends EProperty<CCPath> {
	public ECCPathProp(String name, CCPath defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	@Override
	public String serializeToString() {
		return get().toString();
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get();
	}

	@Override
	public void deserializeFromString(String v) throws CCFormatException {
		set(CCPath.create(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) throws CCFormatException {
		set(CCPath.create((String)v));
	}
}
