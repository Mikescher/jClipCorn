package de.jClipCorn.database.elementValues;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;

public class ELanguageListProp extends EProperty<CCDBLanguageList> {
	public ELanguageListProp(String name, CCDBLanguageList defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(long v) {
		set(CCDBLanguageList.fromBitmask(v));
	}

	@Override
	public String serializeValueToString() {
		return get().serializeToString();
	}

	@Override
	public Object serializeValueToDatabaseValue() {
		return get().serializeToLong();
	}

	@Override
	public void deserializeValueFromString(String v) throws Exception {
		set(CCDBLanguageList.parseFromString(v));
	}

	@Override
	public void deserializeValueFromDatabaseValue(Object v) throws Exception {
		set((Long)v);
	}
}
