package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.IEnumWrapper;
import de.jClipCorn.util.exceptions.CCFormatException;

public class EEnumProp<TType extends ContinoousEnum<TType>> extends EProperty<TType> {

	private final IEnumWrapper _wrapper;

	public EEnumProp(String name, TType defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
		_wrapper = defValue.wrapper();
	}

	@SuppressWarnings("unchecked")
	public void setSafe(int v) {
		TType ev = (TType)_wrapper.findOrNull(v);

		if (ev == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErroneousDatabaseValues", v)); //$NON-NLS-1$
			set(DefaultValue);
		} else {
			set(ev);
		}
	}

	@SuppressWarnings("unchecked")
	public void set(int v) {
		TType ev = (TType)_wrapper.findOrNull(v);

		set(ev);
	}

	@Override
	public String serializeToString() {
		return String.valueOf(get().asInt());
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get().asInt();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void deserializeFromString(String v) throws CCFormatException {
		set((TType)_wrapper.findOrException(Integer.parseInt(v)));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void deserializeFromDatabaseValue(Object v) throws CCFormatException {
		set((TType)_wrapper.findOrException((int)v));
	}

	@Override
	public boolean valueEquals(TType a, TType b) {
		return a == b;
	}
}
