package de.jClipCorn.util;

public enum DecimalSearchType {
	LESSER(0),
	GREATER(1),
	IN_RANGE(2),
	EXACT(3);
	
	private final int id;
	
	DecimalSearchType(int id) {
		this.id = id;
	}
	
	public static DecimalSearchType find(int val) {
		if (val >= 0 && val < DecimalSearchType.values().length) {
			return DecimalSearchType.values()[val]; // Geht nur wenn alle Zahlen nach der Reihe da sind
		}
		return null;
	}
	
	public int asInt() {
		return id;
	}
}
