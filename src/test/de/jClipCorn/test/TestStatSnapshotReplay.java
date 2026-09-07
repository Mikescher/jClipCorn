package de.jClipCorn.test;

import de.jClipCorn.features.statistics.snapshots.*;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.lambda.Func1to0;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Drives {@link StatSnapshotRebuilder} against a hand-fed change log, with no database involved.
 *
 * The first test is the regression test for the bug this whole feature exists for: a movie that was
 * re-encoded has to show its *old* size on the days before the re-encode, not today's.
 */
@SuppressWarnings("nls")
public class TestStatSnapshotReplay extends ClipCornBaseTest {

	private static final long GB = 1024L * 1024 * 1024;

	/** Midday, so converting the UTC timestamp to local time can never move it to another day. */
	private static String at(String day) { return day + " 12:00:00.000"; }

	private static class FakeEnv implements IStatSnapshotEnv {
		final Map<StatClass, Map<String, StatSnapshotRawRow>> Current = new EnumMap<>(StatClass.class);
		final List<String[]>                                  History = new ArrayList<>();
		List<CCStatSnapshot>                                  Written = new ArrayList<>();

		FakeEnv() { for (StatClass c : StatClass.values()) Current.put(c, new HashMap<>()); }

		void put(StatClass cls, String id, String... columnValuePairs) {
			StatSnapshotRawRow row = new StatSnapshotRawRow(cls);
			for (int i = 0; i < columnValuePairs.length; i += 2) row.set(columnValuePairs[i], columnValuePairs[i + 1]);
			Current.get(cls).put(id, row);
		}

		void log(String day, String table, String id, String action, String field, String oldValue, String newValue) {
			History.add(new String[]{ table, id, at(day), action, field, oldValue, newValue });
		}

		@Override public Map<StatClass, Map<String, StatSnapshotRawRow>> readCurrentState() { return Current; }

		@Override public void streamHistory(Func1to0<String[]> consumer) {
			List<String[]> sorted = new ArrayList<>(History);
			sorted.sort((a, b) -> b[2].compareTo(a[2])); // newest first, as the real scan delivers them
			for (String[] r : sorted) consumer.invoke(r);
		}

		@Override public String earliestHistoryTimestamp() {
			String min = null;
			for (String[] r : History) if (min == null || r[2].compareTo(min) < 0) min = r[2];
			return min;
		}

		@Override public void writeSnapshots(List<CCStatSnapshot> rows) { Written = rows; }
	}

	/** The value the sparse table reports for a day: the newest row at or before it. */
	private static CCStatSnapshot on(List<CCStatSnapshot> rows, String day) {
		CCDate d = CCDate.parseOrDefault(day, "yyyy-MM-dd", null);
		assertNotNull(d);

		CCStatSnapshot res = null;
		for (CCStatSnapshot r : rows) if (!r.Date.isGreaterThan(d)) res = r;
		return res;
	}

	@Test
	public void testReencodedMovieKeepsItsOldSizeInThePast() throws Exception {
		FakeEnv env = new FakeEnv();
		env.put(StatClass.MOVIE, "A", "FILESIZE", String.valueOf(1 * GB), "LENGTH", "120", "FORMAT", "0", "ADDDATE", "2020-03-01");
		env.log("2024-06-10", "MOVIES", "A", "UPDATE", "FILESIZE", String.valueOf(4 * GB), String.valueOf(1 * GB));

		List<CCStatSnapshot> rows = new StatSnapshotRebuilder(env).rebuild(null);

		assertEquals(1 * GB, on(rows, "2024-06-10").bytes(StatClass.MOVIE));
		assertEquals(4 * GB, on(rows, "2024-06-09").bytes(StatClass.MOVIE));
		assertEquals(4 * GB, on(rows, "2021-01-01").bytes(StatClass.MOVIE));

		// before it was added there was nothing
		assertEquals(0, on(rows, "2020-02-29").count(StatClass.MOVIE));
		assertEquals(1, on(rows, "2020-03-01").count(StatClass.MOVIE));
	}

	@Test
	public void testExactCutoffIsTheOldestArchivedChange() throws Exception {
		FakeEnv env = new FakeEnv();
		env.put(StatClass.MOVIE, "A", "FILESIZE", String.valueOf(1 * GB), "LENGTH", "120", "FORMAT", "0", "ADDDATE", "2020-03-01");
		env.log("2024-06-10", "MOVIES", "A", "UPDATE", "FILESIZE", String.valueOf(4 * GB), String.valueOf(1 * GB));

		List<CCStatSnapshot> rows = new StatSnapshotRebuilder(env).rebuild(null);

		for (CCStatSnapshot r : rows) {
			CCDate cutoff = CCDate.create(10, 6, 2024);
			assertEquals("row " + r.Date.toStringSQL(), !r.Date.isLessThan(cutoff), r.Exact);
		}
	}

	@Test
	public void testUndoOfAnInsertRemovesTheElement() throws Exception {
		FakeEnv env = new FakeEnv();
		env.put(StatClass.MOVIE, "A", "FILESIZE", String.valueOf(2 * GB), "LENGTH", "100", "FORMAT", "0", "ADDDATE", "2024-05-01");
		env.log("2024-05-01", "MOVIES", "A", "ADD", "FILESIZE", null, String.valueOf(2 * GB));
		env.log("2024-05-01", "MOVIES", "A", "ADD", "LENGTH",   null, "100");
		env.log("2024-05-01", "MOVIES", "A", "ADD", "ADDDATE",  null, "2024-05-01");

		List<CCStatSnapshot> rows = new StatSnapshotRebuilder(env).rebuild(null);

		assertEquals(1, on(rows, "2024-05-01").count(StatClass.MOVIE));
		assertEquals(0, on(rows, "2024-04-30").count(StatClass.MOVIE));
		assertEquals(0, on(rows, "2024-04-30").bytes(StatClass.MOVIE));
	}

	/** A deleted element is invisible today, but the days it existed on have to show it again. */
	@Test
	public void testUndoOfADeleteBringsTheElementBack() throws Exception {
		FakeEnv env = new FakeEnv();
		env.put(StatClass.MOVIE, "A", "FILESIZE", String.valueOf(1 * GB), "LENGTH", "120", "FORMAT", "0", "ADDDATE", "2020-03-01");

		env.log("2024-08-20", "MOVIES", "B", "DELETE", "FILESIZE", String.valueOf(3 * GB), null);
		env.log("2024-08-20", "MOVIES", "B", "DELETE", "LENGTH",   "90",                   null);
		env.log("2024-08-20", "MOVIES", "B", "DELETE", "FORMAT",   "0",                    null);
		env.log("2024-08-20", "MOVIES", "B", "DELETE", "ADDDATE",  "2022-01-05",           null);

		List<CCStatSnapshot> rows = new StatSnapshotRebuilder(env).rebuild(null);

		assertEquals(1,          on(rows, "2024-08-20").count(StatClass.MOVIE));
		assertEquals(1 * GB,     on(rows, "2024-08-20").bytes(StatClass.MOVIE));

		assertEquals(2,          on(rows, "2024-08-19").count(StatClass.MOVIE));
		assertEquals(4 * GB,     on(rows, "2024-08-19").bytes(StatClass.MOVIE));

		// and it disappears again before its own add-date
		assertEquals(1, on(rows, "2022-01-04").count(StatClass.MOVIE));
	}

	@Test
	public void testFormatHistogramFollowsTheChange() throws Exception {
		FakeEnv env = new FakeEnv();
		env.put(StatClass.MOVIE, "A", "FILESIZE", String.valueOf(1 * GB), "LENGTH", "120", "FORMAT", "0", "ADDDATE", "2020-03-01");
		env.log("2024-06-10", "MOVIES", "A", "UPDATE", "FORMAT", "1", "0");

		List<CCStatSnapshot> rows = new StatSnapshotRebuilder(env).rebuild(null);

		assertEquals(Integer.valueOf(1), on(rows, "2024-06-10").histogram(StatClass.MOVIE, "FORMAT").get("0"));
		assertEquals(Integer.valueOf(1), on(rows, "2024-06-09").histogram(StatClass.MOVIE, "FORMAT").get("1"));
		assertNull(on(rows, "2024-06-09").histogram(StatClass.MOVIE, "FORMAT").get("0"));
	}

	@Test
	public void testRebuildIsIdempotent() throws Exception {
		FakeEnv env = new FakeEnv();
		env.put(StatClass.MOVIE,   "A", "FILESIZE", String.valueOf(1 * GB), "LENGTH", "120", "FORMAT", "0", "ADDDATE", "2020-03-01");
		env.put(StatClass.EPISODE, "E", "FILESIZE", String.valueOf(1 * GB), "LENGTH", "20",  "FORMAT", "0", "ADDDATE", "2021-05-05", "SEASONID", "S");
		env.log("2024-06-10", "MOVIES", "A", "UPDATE", "FILESIZE", String.valueOf(4 * GB), String.valueOf(1 * GB));

		List<CCStatSnapshot> first  = new StatSnapshotRebuilder(env).rebuild(null);
		List<CCStatSnapshot> second = new StatSnapshotRebuilder(env).rebuild(null);

		assertEquals(first.size(), second.size());
		for (int i = 0; i < first.size(); i++) {
			assertTrue(first.get(i).Date.isEqual(second.get(i).Date));
			assertTrue(first.get(i).valuesEqual(second.get(i)));
		}
	}

	/** One corrupt row must not abort a rebuild - the migration depends on that. */
	@Test
	public void testMalformedValuesAreSurvivable() throws Exception {
		FakeEnv env = new FakeEnv();
		env.put(StatClass.MOVIE, "A", "FILESIZE", String.valueOf(1 * GB), "LENGTH", "120", "FORMAT", "0", "ADDDATE", "2020-03-01");
		env.log("2024-06-10", "MOVIES", "A", "UPDATE", "FILESIZE", "not-a-number", String.valueOf(1 * GB));
		env.log("2024-06-11", "MOVIES", "A", "UPDATE", "FORMAT",   "99",           "0");

		List<CCStatSnapshot> rows = new StatSnapshotRebuilder(env).rebuild(null);

		assertFalse(rows.isEmpty());
		assertEquals(0, on(rows, "2024-06-09").bytes(StatClass.MOVIE));
	}

	@Test
	public void testNoHistoryFallsBackToAnEstimate() throws Exception {
		FakeEnv env = new FakeEnv();
		env.put(StatClass.MOVIE, "A", "FILESIZE", String.valueOf(1 * GB), "LENGTH", "120", "FORMAT", "0", "ADDDATE", "2020-03-01");
		env.put(StatClass.MOVIE, "B", "FILESIZE", String.valueOf(2 * GB), "LENGTH", "90",  "FORMAT", "0", "ADDDATE", "2022-08-08");

		List<CCStatSnapshot> rows = new StatSnapshotRebuilder(env).rebuild(null);

		for (CCStatSnapshot r : rows) assertFalse(r.Exact);

		assertEquals(3 * GB, on(rows, "2022-08-08").bytes(StatClass.MOVIE));
		assertEquals(1 * GB, on(rows, "2022-08-07").bytes(StatClass.MOVIE));
		assertEquals(0,      on(rows, "2020-02-29").count(StatClass.MOVIE));
	}

	/** A season and its series leave the collection together with their last episode. */
	@Test
	public void testSeasonAndSeriesFollowTheirLastEpisode() throws Exception {
		FakeEnv env = new FakeEnv();
		env.put(StatClass.SERIES,  "SER", "FSK", "0");
		env.put(StatClass.SEASON,  "SEA", "SERIESID", "SER");
		env.put(StatClass.EPISODE, "E1",  "FILESIZE", String.valueOf(GB), "LENGTH", "20", "FORMAT", "0", "ADDDATE", "2021-05-05", "SEASONID", "SEA");
		env.put(StatClass.EPISODE, "E2",  "FILESIZE", String.valueOf(GB), "LENGTH", "20", "FORMAT", "0", "ADDDATE", "2021-06-06", "SEASONID", "SEA");

		List<CCStatSnapshot> rows = new StatSnapshotRebuilder(env).rebuild(null);

		assertEquals(2, on(rows, "2021-06-06").count(StatClass.EPISODE));
		assertEquals(1, on(rows, "2021-06-06").count(StatClass.SEASON));
		assertEquals(1, on(rows, "2021-06-06").count(StatClass.SERIES));

		assertEquals(1, on(rows, "2021-06-05").count(StatClass.EPISODE));
		assertEquals(1, on(rows, "2021-06-05").count(StatClass.SEASON));

		assertEquals(0, on(rows, "2021-05-04").count(StatClass.EPISODE));
		assertEquals(0, on(rows, "2021-05-04").count(StatClass.SEASON));
		assertEquals(0, on(rows, "2021-05-04").count(StatClass.SERIES));
	}
}
