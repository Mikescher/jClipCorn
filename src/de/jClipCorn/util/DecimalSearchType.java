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
	
	public int asInt() {
		return id;
	}
}
