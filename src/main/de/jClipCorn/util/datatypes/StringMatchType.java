package de.jClipCorn.util.datatypes;

public enum StringMatchType {
	SM_STARTSWITH(0),
	SM_INCLUDES(1),
	SM_ENDSWITH(2);
	
	private final int id;
	
	StringMatchType(int id) {
		this.id = id;
	}
	
	public static StringMatchType find(int val) {
		if (val >= 0 && val < StringMatchType.values().length) {
			return StringMatchType.values()[val]; // Geht nur wenn alle Zahlen nach der Reihe da sind
		}
		return null;
	}
	
	public int asInt() {
		return id;
	}
}
