package de.jClipCorn.util;

public class DoubleString {
	private String s1;
	private String s2;
	
	public DoubleString(String s1, String s2) {
		this.s1 = s1;
		this.s2 = s2;
	}

	public String get1() {
		return s1;
	}
	
	public String get2() {
		return s2;
	}
	
	public void set1(String s1) {
		this.s1 = s1;
	}
	
	public void set2(String s2) {
		this.s2 = s2;
	}
}
