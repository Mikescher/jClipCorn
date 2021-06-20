package de.jClipCorn.database.elementProps;

import de.jClipCorn.database.elementProps.impl.EPropertyType;

public interface IEProperty {
	void resetToDefault();

	EPropertyType getValueType();
	String getName();

	String serializeValueToString();
	Object serializeValueToDatabaseValue();
	void deserializeValueFromString(String v) throws Exception;
	void deserializeValueFromDatabaseValue(Object v) throws Exception;
}
