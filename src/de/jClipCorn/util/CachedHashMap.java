package de.jClipCorn.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class CachedHashMap<K, V> extends LinkedHashMap<K, V> {
	private static final long serialVersionUID = -2538763626863890016L;

	private final int capacity;

	public CachedHashMap(int capacity) {
		super(capacity + 1, 1.1f, true);
		this.capacity = capacity;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > capacity;
	}
}
