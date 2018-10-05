package de.jClipCorn.util.helper;

public class ObjectUtils {
	
	public static <T> boolean IsEqual(T v1, T v2) {
		return (v1 == null) ? (v2 == null) : v1.equals(v2);
	}
	
}
