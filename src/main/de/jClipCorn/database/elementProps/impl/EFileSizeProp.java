package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.elementProps.IPropertyParent;

public class EFileSizeProp extends EProperty<CCFileSize> {
	public EFileSizeProp(String name, CCFileSize defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(long v) {
		set(new CCFileSize(v));
	}

	@Override
	public String serializeValueToString() {
		return String.valueOf(get().getBytes());
	}

	@Override
	public Object serializeValueToDatabaseValue() {
		return get().getBytes();
	}

	@Override
	public void deserializeValueFromString(String v) throws Exception {
		set(Long.parseLong(v));
	}

	@Override
	public void deserializeValueFromDatabaseValue(Object v) throws Exception {
		set((Long)v);
	}
}
