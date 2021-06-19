package de.jClipCorn.database.elementValues;

public class EIntProp extends EProperty<Integer> {
	public EIntProp(String name, Integer defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	@Override
	public String serializeValueToString() {
		return String.valueOf(get());
	}

	@Override
	public Object serializeValueToDatabaseValue() {
		return get();
	}

	@Override
	public void deserializeValueFromString(String v) throws Exception {
		set(Integer.parseInt(v));
	}

	@Override
	public void deserializeValueFromDatabaseValue(Object v) throws Exception {
		set((int)v);
	}
}
