package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
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
	private Class<?> _cls;
	private boolean _dirty = false;

	protected final IPropertyParent parent;

	private final List<Func3to0<EProperty<TType>, TType, TType>> _listener = new ArrayList<>();

	public final EPropertyType ValueType;
	public final String Name;
	public final TType DefaultValue;

	public EProperty(String name, TType defValue, IPropertyParent p, EPropertyType t) {
		if (defValue == null) CCLog.addUndefinied(Str.format("Default value for [{0}] cannot be NULL", name));

		_value       = defValue;
		_cls         = defValue.getClass();
		parent       = p;
		DefaultValue = defValue;
		Name         = name;
		ValueType    = t;
	}

	protected TType validateValue(TType v) {
		if (v == null) { CCLog.addUndefinied("Prevented setting ["+Name+"] to NULL"); return DefaultValue; } //$NON-NLS-1$

		return v;
	}

	/**
	 * Skips readonly check and does not call any external stuff (cache-busting, updateDB, listener, ...)
	 */
	public void setReadonlyPropToInitial(TType v) {
		set(v, false, false, false, false, true);
	}

	public void set(TType v) {
		set(v, true, true, true, true, true);
	}

	public void setWithoutListenerAndUpdateDB(TType v) {
		set(v, true, true, false, false, true);
	}

	protected void set(TType v, boolean verifyReadonly, boolean bustCache, boolean updateDB, boolean callListener, boolean setDirty) {
		if (verifyReadonly && isReadonly()) throw new Error("Cannot update field ["+getValueType().asString()+"]::["+Name+"] - its readonly");

		v = validateValue(v);

		var old = _value;

		v = onValueChanging(old, v);

		_value = v;

		if (setDirty && !valueEquals(old, v)) _dirty = true;

		if (callListener && !valueEquals(old, v)) {
			triggerListener(v, old);
		}

		if (bustCache) parent.getCache().bust();
		if (updateDB)  parent.updateDB();
	}

	public void setWithException(TType v) throws DatabaseUpdateException {
		if (isReadonly()) throw new Error("Cannot update field ["+getValueType().asString()+"]::["+Name+"] - its readonly");

		v = validateValue(v);

		var old = _value;

		_value = v;

		if (!valueEquals(old, v)) _dirty = true;

		if (!valueEquals(old, v)) {
			triggerListener(v, old);
		}


		parent.getCache().bust();
		parent.updateDBWithException();
	}

	private void triggerListener(TType v, TType old) {
		for (var l: _listener) l.invoke(this, old, v);

		switch (parent.getStructureType()) {
			case MOVIE:
				parent.getMovieList().triggerMoviePropChangedListener(this, (CCMovie)parent, old, v);
				break;
			case SERIES:
				parent.getMovieList().triggerSeriesPropChangedListener(this, (CCSeries)parent, old, v);
				break;
			case SEASON:
				parent.getMovieList().triggerSeasonPropChangedListener(this, (CCSeason)parent, old, v);
				break;
			case EPISODE:
				parent.getMovieList().triggerEpisodePropChangedListener(this, (CCEpisode)parent, old, v);
				break;
		}
	}

	/**
	 * Called before _value is set, before `parent.getCache().bust()`, `parent.updateDB()` are invoked and listener are called
	 * It is _neccesary_ that this method never returns an invalid value (somehing that validateValue would reject)
	 */
	protected TType onValueChanging(TType valOld, TType valNew){ return valNew; /* override me */ }

	public TType get() {
		return _value;
	}

	public boolean isDirty() {
		return _dirty;
	}

	public void resetDirty() {
		_dirty = false;
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

	public void addChangeListener(Func3to0<EProperty<TType>, TType, TType> lstnr) {
		_listener.add(lstnr);
	}

	public boolean isReadonly() {
		return getValueType().isReadonly();
	}

	public abstract String serializeToString();
	public abstract Object serializeToDatabaseValue();
	public abstract void deserializeFromString(String v) throws CCFormatException;
	public abstract void deserializeFromDatabaseValue(Object v) throws CCFormatException;

	public abstract boolean valueEquals(TType a, TType b);
}
