package de.jClipCorn.database.elementValues;

public interface IEProperty {
	void resetToDefault();

	EPropertyType getValueType();
	String getName();

	String serializeValueToString();
	Object serializeValueToDatabaseValue();
	void deserializeValueFromString(String v) throws Exception;
	void deserializeValueFromDatabaseValue(Object v) throws Exception;
}
