package de.jClipCorn.util;

public enum StringMatchType {
	SM_STARTSWITH(0),
	SM_INCLUDES(1),
	SM_ENDSWITH(2);
	
	private final int id;
	
	StringMatchType(int id) {
		this.id = id;
	}
	
	public int asInt() {
		return id;
	}
}
