package de.jClipCorn.util.enumextension;

import java.util.ArrayList;
import java.util.List;

public class EnumWrapper<T extends ContinoousEnum<T>> {

	private final T defValue;
	
	public EnumWrapper(T defaultValue) {
		defValue = defaultValue;
	}
	
	public T find(int val) {
		if (val >= 0 && val < defValue.evalues().length) {
			return defValue.evalues()[val]; // Geht nur wenn alle Zahlen nach der Reihe da sind
		}
		return null;
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
}
