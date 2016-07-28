package de.jClipCorn.util.enumextension;

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
}
