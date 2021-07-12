package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.exceptions.CCFormatException;

public class EOptFileSizeProp extends EOptProperty<CCFileSize> {
	public EOptFileSizeProp(String name, Opt<CCFileSize> defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	@Override
	protected String serializeInnerToString(CCFileSize v) {
		return String.valueOf(v.getBytes());
	}

	@Override
	protected Object serializeInnerToDatabaseValue(CCFileSize v) {
		return v.getBytes();
	}

	@Override
	protected CCFileSize deserializeInnerFromString(String v) throws CCFormatException {
		return new CCFileSize(Long.parseLong(v));
	}

	@Override
	protected CCFileSize deserializeInnerFromDatabaseValue(Object v) throws CCFormatException {
		return new CCFileSize((long)v);
	}

	@Override
	protected boolean valueInnerEquals(CCFileSize a, CCFileSize b) {
		return CCFileSize.isEqual(a, b);
	}
}
