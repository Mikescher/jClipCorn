package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.DatabaseUpdateException;
import de.jClipCorn.util.lambda.Func3to0;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("HardCodedStringLiteral")
public abstract class EProperty<TType> implements IEProperty {

	private TType _value;

	protected final IPropertyParent parent;

	private final List<Func3to0<EProperty<TType>, TType, TType>> _listener = new ArrayList<>();

	public final EPropertyType ValueType;
	public final String Name;
	public final TType DefaultValue;

	public EProperty(String name, TType defValue, IPropertyParent p, EPropertyType t) {
		_value       = defValue;
		parent       = p;
		DefaultValue = defValue;
		Name         = name;
		ValueType    = t;

		if (DefaultValue == null) CCLog.addUndefinied(Str.format("Default value for [{0}] cannot be NULL", Name));
	}

	protected TType validateValue(TType v) {
		if (v == null) { CCLog.addUndefinied("Prevented setting ["+Name+"] to NULL"); return DefaultValue; } //$NON-NLS-1$

		return v;
	}

	/**
	 * Skips readonly check and does not call any external stuff (cache-busting, updateDB, listener, ...)
	 */
	public void setReadonlyPropToInitial(TType v) {
		set(v, false, false, false, false);
	}

	public void set(TType v) {
		set(v, true, true, true, true);
	}

	protected void set(TType v, boolean verifyReadonly, boolean bustCache, boolean updateDB, boolean callListener) {
		if (verifyReadonly && getValueType().isReadonly()) throw new Error("Cannot update field ["+getValueType().asString()+"]::["+Name+"] - its readonly");

		v = validateValue(v);

		var old = _value;

		v = onValueChanging(old, v);

		_value = v;

		if (bustCache) parent.getCache().bust();
		if (updateDB)  parent.updateDB();

		if (callListener) for (var l: _listener) l.invoke(this, old, v);
	}

	/**
	 * Called before _value is set, before `parent.getCache().bust()`, `parent.updateDB()` are invoked and listener are called
	 * It is _neccesary_ that this method never returns an invalid value (somehing that validateValue would reject)
	 */
	protected TType onValueChanging(TType valOld, TType valNew){ return valNew; /* override me */ }

	public void setWithException(TType v) throws DatabaseUpdateException {
		v = validateValue(v);

		_value = v;

		parent.getCache().bust();
		parent.updateDBWithException();
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

	public void addChangeListener(Func3to0<EProperty<TType>, TType, TType> lstnr)
	{
		_listener.add(lstnr);
	}

	public abstract String serializeToString();
	public abstract Object serializeToDatabaseValue();
	public abstract void deserializeFromString(String v) throws CCFormatException;
	public abstract void deserializeFromDatabaseValue(Object v) throws CCFormatException;
}
