package de.jClipCorn.features.statistics.snapshots;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.statistics.StatisticsTypeFilter;
import de.jClipCorn.util.datetime.CCDate;

import java.util.*;

/**
 * The chart-facing view of main.STATSNAPSHOTS: the sparse rows expanded to one entry per day, reduced to
 * the element kinds a chart is showing.
 */
public class StatSnapshotSeries {

	private final CCDate                 _first;
	private final List<CCStatSnapshot>   _days;   // dense, index 0 == _first
	private final List<StatClass>        _classes;
	private final CCDate                 _exactSince;

	private StatSnapshotSeries(CCDate first, List<CCStatSnapshot> days, List<StatClass> classes, CCDate exactSince) {
		_first      = first;
		_days       = days;
		_classes    = classes;
		_exactSince = exactSince;
	}

	public static StatSnapshotSeries load(CCMovieList movielist, StatisticsTypeFilter source) {
		List<CCStatSnapshot> rows = readRows(movielist);

		if (rows.isEmpty()) rows = StatSnapshotProjector.projectFromCurrentState(movielist);

		return build(rows, classesOf(source));
	}

	private static List<CCStatSnapshot> readRows(CCMovieList movielist) {
		try {
			return movielist.readStatSnapshots();
		} catch (Exception e) {
			CCLog.addWarning("[SNAPSHOT] Cannot read the statistics snapshots", e); //$NON-NLS-1$
			return new ArrayList<>();
		}
	}

	private static StatSnapshotSeries build(List<CCStatSnapshot> rows, List<StatClass> classes) {
		if (rows.isEmpty()) return new StatSnapshotSeries(CCDate.getCurrentDate(), new ArrayList<>(), classes, null);

		rows.sort((a, b) -> CCDate.compare(a.Date, b.Date));

		CCDate first = rows.get(0).Date;
		CCDate last  = CCDate.max(rows.get(rows.size() - 1).Date, CCDate.getCurrentDate());

		int count = first.getDayDifferenceTo(last) + 1;

		List<CCStatSnapshot> days = new ArrayList<>(count);

		int idx = 0;
		CCStatSnapshot current = rows.get(0);
		for (int i = 0; i < count; i++) {
			CCDate day = first.getAddDay(i);

			// sparse rows: a day without one keeps the values of the newest row before it
			while (idx + 1 < rows.size() && !rows.get(idx + 1).Date.isGreaterThan(day)) current = rows.get(++idx);

			days.add(current);
		}

		CCDate exactSince = null;
		for (CCStatSnapshot r : rows) if (r.Exact) { exactSince = r.Date; break; }

		return new StatSnapshotSeries(first, days, classes, exactSince);
	}

	private static List<StatClass> classesOf(StatisticsTypeFilter source) {
		switch (source) {
			case STF_MOVIES:              return List.of(StatClass.MOVIE);
			case STF_EPISODES:            return List.of(StatClass.EPISODE);
			case STF_SERIES:              return List.of(StatClass.SERIES);
			case STF_SEASONS:             return List.of(StatClass.SEASON);
			case STF_MOVIES_AND_SERIES:   return List.of(StatClass.MOVIE, StatClass.SERIES);
			case STF_MOVIES_AND_SEASONS:  return List.of(StatClass.MOVIE, StatClass.SEASON);
			case STF_MOVIES_AND_EPISODES: return List.of(StatClass.MOVIE, StatClass.EPISODE);
			default:                      return List.of(StatClass.values());
		}
	}

	public boolean isEmpty()   { return _days.isEmpty(); }
	public CCDate  firstDay()  { return _first; }
	public int     dayCount()  { return _days.size(); }

	/** The first day that was reconstructed from the change archive, or null when everything is an estimate. */
	public CCDate exactSince() { return _exactSince; }

	public long[] bytes() {
		long[] r = new long[_days.size()];
		for (int i = 0; i < r.length; i++) for (StatClass c : _classes) r[i] += _days.get(i).bytes(c);
		return r;
	}

	public int[] minutes() {
		int[] r = new int[_days.size()];
		for (int i = 0; i < r.length; i++) for (StatClass c : _classes) r[i] += _days.get(i).minutes(c);
		return r;
	}

	public int[] count() {
		int[] r = new int[_days.size()];
		for (int i = 0; i < r.length; i++) for (StatClass c : _classes) r[i] += _days.get(i).count(c);
		return r;
	}

	/** {@code [day][bucketIndex]} for the given buckets of one dimension, summed over the selected kinds. */
	public int[][] histogram(String dimension, List<String> buckets) {
		int[][] r = new int[_days.size()][buckets.size()];

		for (int i = 0; i < _days.size(); i++) {
			for (StatClass c : _classes) {
				Map<String, Integer> h = _days.get(i).histogram(c, dimension);
				if (h.isEmpty()) continue;

				for (int b = 0; b < buckets.size(); b++) r[i][b] += h.getOrDefault(buckets.get(b), 0);
			}
		}

		return r;
	}
}
