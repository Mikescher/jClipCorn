package de.jClipCorn.database.elementProps;

import de.jClipCorn.database.elementProps.impl.EPropertyType;
import de.jClipCorn.util.exceptions.CCFormatException;

public interface IEProperty {
	void resetToDefault();

	EPropertyType getValueType();
	String getName();

	String serializeToString();
	Object serializeToDatabaseValue();
	void deserializeFromString(String v) throws CCFormatException;
	void deserializeFromDatabaseValue(Object v) throws CCFormatException;

	boolean isReadonly();

	boolean isDirty();
	void resetDirty();
}
