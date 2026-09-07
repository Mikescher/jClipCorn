package de.jClipCorn.features.statistics.snapshots;

import de.jClipCorn.util.lambda.Func1to1;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * One histogram of a snapshot - a name, the element kinds it applies to, the raw columns it is computed
 * from, and how to turn those columns into zero or more bucket keys.
 *
 * An element contributes one count to every bucket the extractor returns, so a multi-valued dimension
 * (languages, genres, tags, ...) makes the histogram's total exceed the element count. That is intended.
 */
public class StatSnapshotDimension {

	public final String           Key;
	public final Set<StatClass>   Classes;
	public final Set<String>      Columns;

	private final Func1to1<StatSnapshotRawRow, List<String>> _buckets;

	public StatSnapshotDimension(String key, Set<StatClass> classes, Set<String> columns, Func1to1<StatSnapshotRawRow, List<String>> buckets) {
		Key      = key;
		Classes  = classes;
		Columns  = columns;
		_buckets = buckets;
	}

	public List<String> buckets(StatSnapshotRawRow row) {
		List<String> r = _buckets.invoke(row);
		return (r == null) ? Collections.emptyList() : r;
	}

	static List<String> one(String value) {
		return (value == null) ? Collections.emptyList() : Collections.singletonList(value);
	}

	static Set<String> cols(String... names) {
		return Set.copyOf(Arrays.asList(names));
	}

	static Set<StatClass> classes(StatClass... c) {
		return Set.copyOf(Arrays.asList(c));
	}
}
