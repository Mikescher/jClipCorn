package de.jClipCorn.util.enumextension;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.EnumFormatException;
import de.jClipCorn.util.exceptions.EnumValueNotFoundException;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.util.*;

public class EnumWrapper<T extends ContinoousEnum<T>> {

	private final T defValue;
	private final Func1to1<T, Object> displaySorter;
	private final Func1to1<T, Boolean> displayFilter;
	private final Func1to1<T, String> displayRenderer;

	public EnumWrapper(T defaultValue) {
		defValue = defaultValue;
		displaySorter   = ContinoousEnum::asInt;
		displayFilter   = (v->true);
		displayRenderer = ContinoousEnum::asString;
	}

	public EnumWrapper(T defaultValue, Func1to1<T, Object> cbxSelector) {
		defValue = defaultValue;
		displaySorter   = cbxSelector;
		displayFilter   = (v->true);
		displayRenderer = ContinoousEnum::asString;
	}

	public EnumWrapper(T defaultValue, Func1to1<T, Object> cbxSelector, Func1to1<T, Boolean> cbxFilter) {
		defValue = defaultValue;
		displaySorter   = cbxSelector;
		displayFilter   = cbxFilter;
		displayRenderer = ContinoousEnum::asString;
	}

	public EnumWrapper(T defaultValue, Func1to1<T, Object> cbxSelector, Func1to1<T, Boolean> cbxFilter, Func1to1<T, String> cbxRenderer) {
		defValue = defaultValue;
		displaySorter   = cbxSelector;
		displayFilter   = cbxFilter;
		displayRenderer = cbxRenderer;
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

	public String asDisplayString(T value) {
		if (value == null) return ""; //$NON-NLS-1$
		return displayRenderer.invoke(value);
	}

	public T firstValue() {
		return defValue.evalues()[0];
	}

	public List<T> allValues() {
		return new ArrayList<>(Arrays.asList(defValue.evalues()));
	}

	public T randomValue(Random r) {
		T[] a = defValue.evalues();
		if (a.length == 0) return null;
		return a[r.nextInt(a.length)];
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public DefaultComboBoxModel<T> getComboBoxModel() {
		T[] a = defValue.evalues();

		Vector<T> b = CCStreams
				.iterate(a)
				.filter(displayFilter)
				.autosortByProperty(x -> (Comparable)displaySorter.invoke(x))
				.toVector();

		return new DefaultComboBoxModel<>(b);
	}

	public DefaultListCellRenderer getComboBoxRenderer() {
		return new ContinoousEnumComboBoxRenderer<>(this);
	}
}
