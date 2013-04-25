package de.jClipCorn.util.parser;

public enum ImDBLanguage {
	GERMAN(0), 
	ENGLISH(1);

	private int id;

	private ImDBLanguage(int val) {
		id = val;
	}

	public static ImDBLanguage find(int val) {
		if (val >= 0 && val < ImDBLanguage.values().length) {
			return ImDBLanguage.values()[val]; // Geht nur wenn alle Zahlen nach der Reihe da sind
		}
		return null;
	}
	
	public int asInt() {
		return id;
	}
}
