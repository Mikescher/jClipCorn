package de.jClipCorn.util.enumextension;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.EnumFormatException;
import de.jClipCorn.util.exceptions.EnumValueNotFoundException;

public class EnumWrapper<T extends ContinoousEnum<T>> {

	private final T defValue;
	
	public EnumWrapper(T defaultValue) {
		defValue = defaultValue;
	}
	
	public T findOrNull(int val) {
		if (val >= 0 && val < defValue.evalues().length) {
			return defValue.evalues()[val]; // Geht nur wenn alle Zahlen nach der Reihe da sind
		}
		return null;
	}

	public T findOrException(int val) throws EnumFormatException {
		if (val >= 0 && val < defValue.evalues().length) {
			return defValue.evalues()[val]; // Geht nur wenn alle Zahlen nach der Reihe da sind
		}
		throw new EnumFormatException(val, defValue.getClass());
	}

	public T findOrFatalError(int val) {
		if (val >= 0 && val < defValue.evalues().length) {
			return defValue.evalues()[val]; // Geht nur wenn alle Zahlen nach der Reihe da sind
		}
		CCLog.addFatalError("Value '" + val + "' is not a valid element of enum " + defValue.getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		throw new Error("Value '" + val + "' is not a valid element of enum " + defValue.getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public T findOrDefault(int val, T def) {
		if (val >= 0 && val < defValue.evalues().length) {
			return defValue.evalues()[val]; // Geht nur wenn alle Zahlen nach der Reihe da sind
		}
		return def;
	}

	public T findByTextOrException(String strval) throws EnumValueNotFoundException {
		for (T val : defValue.evalues()) if (Str.equals(val.asString(), strval)) return val;
		throw new EnumValueNotFoundException(strval, defValue.getClass());
	}

	public String[] getList() {
		return defValue.getList();
	}

	public String asString(T value) {
		if (value == null) return ""; //$NON-NLS-1$
		return value.asString();
	}

	public T firstValue() {
		return defValue.evalues()[0];
	}

	public List<T> allValues() {
		List<T> lst = new ArrayList<>();
		
		for (T val : defValue.evalues()) lst.add(val);
		
		return lst;
	}

	public T randomValue(Random r) {
		T[] a = defValue.evalues();
		if (a.length == 0) return null;
		return a[r.nextInt(a.length)];
	}
}
