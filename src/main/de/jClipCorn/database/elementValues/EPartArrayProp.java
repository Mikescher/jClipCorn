package de.jClipCorn.database.elementValues;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.DatabaseUpdateException;

import java.util.Arrays;

public class EPartArrayProp extends EProperty<String[]> {

	private final int _size;

	public EPartArrayProp(String name, int len, IPropertyParent p, EPropertyType t) {
		super(name, arr(len), p, t);
		_size = len;
	}

	private static String[] arr(int len) {
		var a = new String[len];
		for (int i = 0; i < len; i++) a[i] = Str.Empty;
		return a;
	}

	public String get(int idx) {
		return get()[idx];
	}

	public void set(int idx, String str) {
		var arr = Arrays.copyOf(get(), _size);
		arr[idx] = str;
		set(arr);
	}

	public void reset(int idx) {
		set(idx, Str.Empty);
	}

	@Override
	protected String[] validateValue(String[] v) {
		if (v != null && v.length != _size) { CCLog.addUndefinied(Str.format("Prevented setting [{0}] to wrong-sized ({1}) array", Name, v.length)); return DefaultValue; } //$NON-NLS-1$

		return super.validateValue(v);
	}

	@Override
	public void setWithException(String[] v) throws DatabaseUpdateException {
		super.setWithException(v);
	}
}
