package de.jClipCorn.features.statistics.snapshots;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.datetime.CCDate;

import java.util.*;

/**
 * Carries a known state backwards in time using nothing but the elements' add-dates: on the day before an
 * element was added it simply was not there yet.
 *
 * This is what the old charts did for the whole series, and it is wrong in exactly two ways - it cannot
 * see values that changed and it cannot see elements that were deleted. It is used for the era the change
 * archive does not reach (seeded with the state reconstructed at that cutoff, so at least the values are
 * the ones of that era) and as the fallback for a database whose snapshots were never built. Rows it
 * produces are flagged as estimates.
 */
public final class StatSnapshotProjector {
	private StatSnapshotProjector() { throw new InstantiationError(); }

	/**
	 * Appends a snapshot, or - when it carries the same values as the previous one - moves that one
	 * further back instead. The table is read with carry-forward, so of two equal rows the *earlier* one
	 * is the one worth keeping. Callers append newest-first.
	 *
	 * A repeat of the same day also replaces: only the exact-era seed and the first estimated row can
	 * collide like that, and the estimate is the honest one of the two.
	 */
	public static void emit(List<CCStatSnapshot> out, CCStatSnapshot s) {
		if (!out.isEmpty()) {
			CCStatSnapshot last = out.get(out.size() - 1);
			if (last.Date.isEqual(s.Date) || last.valuesEqual(s)) { out.set(out.size() - 1, s); return; }
		}

		out.add(s);
	}

	/**
	 * Walks {@code acc} back from {@code startDay}, emitting one row per distinct add-date. That bounds
	 * this era by the number of days anything was ever added on, not by the number of days it spans.
	 *
	 * @return how many elements had an add-date that could not be used
	 */
	public static int project(StatSnapshotAccumulator acc, List<CCStatSnapshot> out, CCDate startDay) {
		int unusableDates = 0;

		NavigableMap<CCDate, List<StatRef>> byAddDate = new TreeMap<>(CCDate::compare);

		for (StatClass cls : StatClass.values()) {
			if (!cls.isPlayable()) continue; // only movies and episodes carry an add-date

			for (Map.Entry<String, StatSnapshotRawRow> e : acc.rows(cls).entrySet()) {
				CCDate ad = StatSnapshotSQL.parseSqlDate(e.getValue().get(StatSnapshotDimensions.COL_ADDDATE), null);

				// without a usable add-date there is no day on which the element appeared - leave it in,
				// i.e. treat it as having always been there
				if (ad == null || ad.isUnspecifiedDate() || !ad.isValidDate() || ad.isMinimum()) { unusableDates++; continue; }

				byAddDate.computeIfAbsent(ad, k -> new ArrayList<>()).add(new StatRef(cls, e.getKey()));
			}
		}

		CCDate day = startDay;
		emit(out, acc.snapshot(day, false));

		if (byAddDate.isEmpty()) return unusableDates;

		// series and seasons have no add-date of their own - they leave the collection with their last child
		Map<String, Integer> seasonEpisodes = countByParent(acc, StatClass.EPISODE, StatSnapshotDimensions.COL_SEASONID);
		Map<String, Integer> seriesSeasons  = countByParent(acc, StatClass.SEASON,  StatSnapshotDimensions.COL_SERIESID);

		for (CCDate addDate : byAddDate.descendingKeySet()) {
			if (addDate.isGreaterThan(day)) continue; // newer than the seed state, already accounted for

			// the state still holds everything added on this day, so this is its end-of-day state; when it
			// equals the row above it, emit() simply carries that one back to here
			emit(out, acc.snapshot(addDate, false));

			for (StatRef ref : byAddDate.get(addDate)) removeWithParents(acc, ref, seasonEpisodes, seriesSeasons);

			day = addDate.getSubDay(1);
			emit(out, acc.snapshot(day, false));
		}

		return unusableDates;
	}

	/** The whole series estimated from the current state - the fallback for a database without snapshots. */
	public static List<CCStatSnapshot> projectFromCurrentState(CCMovieList movielist) {
		List<CCStatSnapshot> out = new ArrayList<>();

		try {
			StatSnapshotAccumulator acc = new StatSnapshotAccumulator();
			for (Map.Entry<StatClass, Map<String, StatSnapshotRawRow>> e : StatSnapshotStateReader.readCurrentState(movielist.getInternalDatabaseDirectly()).entrySet()) {
				for (Map.Entry<String, StatSnapshotRawRow> r : e.getValue().entrySet()) acc.addRow(e.getKey(), r.getKey(), r.getValue().copy());
			}

			project(acc, out, CCDate.getCurrentDate());
		} catch (Exception e) {
			CCLog.addWarning("[SNAPSHOT] Cannot estimate the statistics time-series", e); //$NON-NLS-1$
			return new ArrayList<>();
		}

		Collections.reverse(out);

		return out;
	}

	private static void removeWithParents(StatSnapshotAccumulator acc, StatRef ref, Map<String, Integer> seasonEpisodes, Map<String, Integer> seriesSeasons) {
		StatSnapshotRawRow row = acc.get(ref.Class, ref.Id);
		if (row == null) return;

		acc.removeRow(ref.Class, ref.Id);

		if (ref.Class != StatClass.EPISODE) return;

		String seasonId = row.get(StatSnapshotDimensions.COL_SEASONID);
		if (seasonId == null) return;
		if (seasonEpisodes.merge(seasonId, -1, Integer::sum) > 0) return;

		StatSnapshotRawRow season = acc.get(StatClass.SEASON, seasonId);
		if (season == null) return;

		acc.removeRow(StatClass.SEASON, seasonId);

		String seriesId = season.get(StatSnapshotDimensions.COL_SERIESID);
		if (seriesId == null) return;
		if (seriesSeasons.merge(seriesId, -1, Integer::sum) > 0) return;

		acc.removeRow(StatClass.SERIES, seriesId);
	}

	private static Map<String, Integer> countByParent(StatSnapshotAccumulator acc, StatClass cls, String parentColumn) {
		Map<String, Integer> res = new HashMap<>();

		for (StatSnapshotRawRow r : acc.rows(cls).values()) {
			String p = r.get(parentColumn);
			if (p != null) res.merge(p, 1, Integer::sum);
		}

		return res;
	}

	private static class StatRef {
		final StatClass Class;
		final String    Id;

		StatRef(StatClass cls, String id) { Class = cls; Id = id; }
	}
}
