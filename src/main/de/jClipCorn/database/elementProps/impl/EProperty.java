package de.jClipCorn.database.elementValues;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.DatabaseUpdateException;

@SuppressWarnings("HardCodedStringLiteral")
public abstract class EProperty<TType> implements IEProperty {

	private TType _value;

	private final IPropertyParent _parent;

	public final EPropertyType ValueType;
	public final String Name;
	public final TType DefaultValue;

	public EProperty(String name, TType defValue, IPropertyParent p, EPropertyType t) {
		_value          = defValue;
		_parent         = p;

		DefaultValue = defValue;
		Name         = name;
		ValueType    = t;

		if (DefaultValue == null) CCLog.addUndefinied(Str.format("Default value for [{0}] cannot be NULL", Name));
	}

	protected TType validateValue(TType v) {
		if (v == null) { CCLog.addUndefinied("Prevented setting ["+Name+"] to NULL"); return DefaultValue; } //$NON-NLS-1$

		return v;
	}

	public void set(TType v) {
		v = validateValue(v);

		_value = v;

		_parent.getCache().bust();
		_parent.updateDB();
	}

	public void setWithException(TType v) throws DatabaseUpdateException {
		v = validateValue(v);

		_value = v;

		_parent.getCache().bust();
		_parent.updateDBWithException();
	}

	public TType get() {
		return _value;
	}

	@Override
	public void resetToDefault() {
		set(DefaultValue);
	}

	@Override
	public EPropertyType getValueType() {
		return ValueType;
	}

	@Override
	public String getName() {
		return Name;
	}

	@Override
	public String toString() {
		return "() -> " + _value;
	}

	public abstract String serializeValueToString();
	public abstract Object serializeValueToDatabaseValue();
	public abstract void deserializeValueFromString(String v) throws Exception;
	public abstract void deserializeValueFromDatabaseValue(Object v) throws Exception;
}