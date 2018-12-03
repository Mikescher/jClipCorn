package de.jClipCorn.util.datatypes;

import java.util.HashMap;

public class CollectionHelper {

	public static <T, V> HashMap<T, V> createHashMap(T k1, V v1) {
		HashMap<T, V> r = new HashMap<>();
		r.put(k1, v1);
		return r;
	}

	public static <T, V> HashMap<T, V> createHashMap(T k1, V v1, T k2, V v2) {
		HashMap<T, V> r = new HashMap<>();
		r.put(k1, v1);
		r.put(k2, v2);
		return r;
	}

	public static <T, V> HashMap<T, V> createHashMap(T k1, V v1, T k2, V v2, T k3, V v3) {
		HashMap<T, V> r = new HashMap<>();
		r.put(k1, v1);
		r.put(k2, v2);
		r.put(k3, v3);
		return r;
	}

}
