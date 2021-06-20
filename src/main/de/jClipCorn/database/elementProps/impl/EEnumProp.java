package de.jClipCorn.database.elementValues;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.IEnumWrapper;

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
	public String serializeValueToString() {
		return String.valueOf(get().asInt());
	}

	@Override
	public Object serializeValueToDatabaseValue() {
		return get().asInt();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void deserializeValueFromString(String v) throws Exception {
		set((TType)_wrapper.findOrException(Integer.parseInt(v)));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void deserializeValueFromDatabaseValue(Object v) throws Exception {
		set((TType)_wrapper.findOrException((int)v));
	}
}