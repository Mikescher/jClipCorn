package de.jClipCorn.features.statistics.snapshots;

import de.jClipCorn.util.datetime.CCDate;

import java.util.*;

/**
 * The mutable working state of a rebuild: every tracked element plus the running totals over them.
 *
 * Totals are maintained incrementally - a rebuild emits a snapshot for every day the collection changed
 * on, and recomputing 30k+ elements per day would be several orders of magnitude too slow.
 */
public class StatSnapshotAccumulator {

	private final Map<StatClass, Map<String, StatSnapshotRawRow>> _rows      = new EnumMap<>(StatClass.class);
	private final Map<StatClass, Integer>                         _count     = new EnumMap<>(StatClass.class);
	private final Map<StatClass, Long>                            _bytes     = new EnumMap<>(StatClass.class);
	private final Map<StatClass, Integer>                         _minutes   = new EnumMap<>(StatClass.class);
	private final Map<StatClass, Map<String, Map<String, Integer>>> _hist    = new EnumMap<>(StatClass.class);

	public StatSnapshotAccumulator() {
		for (StatClass c : StatClass.values()) {
			_rows.put(c, new HashMap<>());
			_count.put(c, 0);
			_bytes.put(c, 0L);
			_minutes.put(c, 0);
			_hist.put(c, new HashMap<>());
		}
	}

	public StatSnapshotRawRow get(StatClass cls, String id) {
		return _rows.get(cls).get(id);
	}

	public String getValue(StatClass cls, String id, String column) {
		StatSnapshotRawRow r = _rows.get(cls).get(id);
		return (r == null) ? null : r.get(column);
	}

	public boolean contains(StatClass cls, String id) {
		return _rows.get(cls).containsKey(id);
	}

	public Map<String, StatSnapshotRawRow> rows(StatClass cls) {
		return _rows.get(cls);
	}

	public void addRow(StatClass cls, String id, StatSnapshotRawRow row) {
		if (_rows.get(cls).put(id, row) != null) return; // already there - a double-add is a no-op

		_count.merge(cls, 1, Integer::sum);
		applyScalars(cls, row, +1);
		applyBuckets(cls, row, StatSnapshotDimensions.forClass(cls), +1);
	}

	public void removeRow(StatClass cls, String id) {
		StatSnapshotRawRow row = _rows.get(cls).remove(id);
		if (row == null) return;

		_count.merge(cls, -1, Integer::sum);
		applyScalars(cls, row, -1);
		applyBuckets(cls, row, StatSnapshotDimensions.forClass(cls), -1);
	}

	/**
	 * Sets one column and updates only what depends on it. A dimension computed from several columns is
	 * removed with its old buckets before the write and re-added with the new ones afterwards.
	 */
	public void setColumn(StatClass cls, String id, String column, String value) {
		StatSnapshotRawRow row = _rows.get(cls).get(id);
		if (row == null) return;

		List<StatSnapshotDimension> affected = StatSnapshotDimensions.forColumn(cls, column);

		applyBuckets(cls, row, affected, -1);
		if (isScalar(cls, column)) applyScalars(cls, row, -1);

		row.set(column, value);

		if (isScalar(cls, column)) applyScalars(cls, row, +1);
		applyBuckets(cls, row, affected, +1);
	}

	public CCStatSnapshot snapshot(CCDate date, boolean exact) {
		Map<StatClass, Map<String, Map<String, Integer>>> hist = new EnumMap<>(StatClass.class);

		for (StatClass c : StatClass.values()) {
			Map<String, Map<String, Integer>> byDim = new HashMap<>();
			for (Map.Entry<String, Map<String, Integer>> e : _hist.get(c).entrySet()) {
				Map<String, Integer> buckets = new HashMap<>();
				for (Map.Entry<String, Integer> b : e.getValue().entrySet()) if (b.getValue() != 0) buckets.put(b.getKey(), b.getValue());
				if (!buckets.isEmpty()) byDim.put(e.getKey(), buckets);
			}
			hist.put(c, byDim);
		}

		return new CCStatSnapshot(date, exact,
				new EnumMap<>(_count), new EnumMap<>(_bytes), new EnumMap<>(_minutes), hist);
	}

	private boolean isScalar(StatClass cls, String column) {
		if (!cls.isPlayable()) return false;
		return column.equals(StatSnapshotDimensions.COL_FILESIZE) || column.equals(StatSnapshotDimensions.COL_LENGTH);
	}

	private void applyScalars(StatClass cls, StatSnapshotRawRow row, int sign) {
		if (!cls.isPlayable()) return;

		_bytes.merge(cls, sign * row.getLong(StatSnapshotDimensions.COL_FILESIZE, 0L), Long::sum);
		_minutes.merge(cls, sign * row.getInt(StatSnapshotDimensions.COL_LENGTH, 0), Integer::sum);
	}

	private void applyBuckets(StatClass cls, StatSnapshotRawRow row, List<StatSnapshotDimension> dims, int sign) {
		Map<String, Map<String, Integer>> byDim = _hist.get(cls);

		for (StatSnapshotDimension d : dims) {
			Map<String, Integer> buckets = byDim.computeIfAbsent(d.Key, k -> new HashMap<>());
			for (String b : d.buckets(row)) buckets.merge(b, sign, Integer::sum);
		}
	}
}
