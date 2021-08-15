package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.EOptPackFormatException;

@SuppressWarnings("HardCodedStringLiteral")
public abstract class EOptProperty<TTypeInner> extends EProperty<Opt<TTypeInner>> {
	public EOptProperty(String name, Opt<TTypeInner> defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	@Override
	public String serializeToString() {
		return get().mapOrElse(v -> "[" + serializeInnerToString(v) + "]", "(empty)");
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get().mapOrElse(this::serializeInnerToDatabaseValue, null);
	}

	@Override
	public void deserializeFromString(String v) throws CCFormatException {
		if (Str.equals(v, "(empty)")) { set(Opt.empty()); return; }

		if (v.startsWith("[") && v.endsWith("]")) { set(Opt.of(deserializeInnerFromString(v.substring(1, v.length()-1)))); return; }

		throw new EOptPackFormatException(v, this.getClass());
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) throws CCFormatException {
		if (v == null) set(Opt.empty());
		else set(Opt.of(deserializeInnerFromDatabaseValue(v)));
	}

	@Override
	public boolean valueEquals(Opt<TTypeInner> a, Opt<TTypeInner> b) {
		return a.isEqual(b, this::valueInnerEquals);
	}

	// -------------

	protected abstract String serializeInnerToString(TTypeInner v);
	protected abstract Object serializeInnerToDatabaseValue(TTypeInner v);
	protected abstract TTypeInner deserializeInnerFromString(String v);
	protected abstract TTypeInner deserializeInnerFromDatabaseValue(Object v);
	protected abstract boolean valueInnerEquals(TTypeInner a, TTypeInner b);
}
