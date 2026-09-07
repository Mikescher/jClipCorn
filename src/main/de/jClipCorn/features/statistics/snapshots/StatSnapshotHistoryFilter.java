package de.jClipCorn.features.statistics.snapshots;

import java.util.ArrayList;
import java.util.List;

/**
 * The subset of the change archive the snapshot replay cares about.
 *
 * Derived from the dimension registry, so the filter can never drift from the columns the snapshots are
 * actually built out of. The literals come from our own constants - there is nothing to escape against.
 */
@SuppressWarnings("nls")
public final class StatSnapshotHistoryFilter {
	private StatSnapshotHistoryFilter() { throw new InstantiationError(); }

	private static List<String> tables() {
		List<String> r = new ArrayList<>();
		for (StatClass c : StatClass.values()) r.add(c.TableName);
		return r;
	}

	private static List<String> fields() {
		return new ArrayList<>(StatSnapshotDimensions.allTrackedColumns());
	}

	public static String whereClause() {
		return "[TABLE] IN (" + quote(tables()) + ") AND [FIELD] IN (" + quote(fields()) + ")";
	}

	public static String selectSQL() {
		return "SELECT [TABLE],[ID],[DATE],[ACTION],[FIELD],[OLD],[NEW] FROM [HISTORY]" +
		       " WHERE " + whereClause() +
		       " ORDER BY [DATE] DESC, rowid DESC";
	}

	private static String quote(List<String> values) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < values.size(); i++) {
			if (i > 0) b.append(',');
			b.append('\'').append(values.get(i)).append('\'');
		}
		return b.toString();
	}
}
