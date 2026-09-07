package de.jClipCorn.features.statistics.snapshots;

import java.util.HashMap;
import java.util.Map;

/**
 * One element's tracked columns, held as the raw text the database (and therefore the history archive)
 * stores them as.
 *
 * The replay works on raw strings rather than domain objects because that is exactly what {@code OLD}/
 * {@code NEW} give us: undoing a change is a single map put, and the same code runs inside a migration
 * where no CCMovieList exists yet.
 */
public class StatSnapshotRawRow {

	public final StatClass Class;

	private final Map<String, String> _values;

	public StatSnapshotRawRow(StatClass cls) {
		Class   = cls;
		_values = new HashMap<>();
	}

	public StatSnapshotRawRow(StatClass cls, Map<String, String> values) {
		Class   = cls;
		_values = new HashMap<>(values);
	}

	/** The replay mutates its rows, so it must never work on the ones the environment handed it. */
	public StatSnapshotRawRow copy() {
		return new StatSnapshotRawRow(Class, _values);
	}

	public String get(String column) {
		return _values.get(column);
	}

	public void set(String column, String value) {
		_values.put(column, value);
	}

	public long getLong(String column, long fallback) {
		String v = _values.get(column);
		if (v == null) return fallback;

		try {
			return Long.parseLong(v.trim());
		} catch (NumberFormatException e) {
			return fallback;
		}
	}

	public int getInt(String column, int fallback) {
		return (int) getLong(column, fallback);
	}
}
