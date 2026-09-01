package de.jClipCorn.database.elementProps;

import de.jClipCorn.database.elementProps.impl.EPropertyType;
import de.jClipCorn.database.elementProps.impl.ETargetDatabase;
import de.jClipCorn.util.exceptions.CCFormatException;

public interface IEProperty {
	void resetToDefault();

	EPropertyType getValueType();
	ETargetDatabase getTargetDatabase();
	String getName();

	String serializeToString();
	Object serializeToDatabaseValue();
	void deserializeFromString(String v) throws CCFormatException;
	void deserializeFromDatabaseValue(Object v) throws CCFormatException;

	boolean isReadonly();

	boolean isDefault();

	boolean isDirty();
	void resetDirty();
}
