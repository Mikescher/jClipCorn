package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.exceptions.CCFormatException;

public class EFileSizeProp extends EProperty<CCFileSize> {
	public EFileSizeProp(String name, CCFileSize defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(long v) {
		set(new CCFileSize(v));
	}

	@Override
	public String serializeToString() {
		return String.valueOf(get().getBytes());
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get().getBytes();
	}

	@Override
	public void deserializeFromString(String v) throws CCFormatException {
		set(Long.parseLong(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) throws CCFormatException {
		set((Long)v);
	}

	@Override
	public boolean valueEquals(CCFileSize a, CCFileSize b) {
		return CCFileSize.isEqual(a, b);
	}
}
