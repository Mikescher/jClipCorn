package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.features.statistics.StatisticsTypeFilter;
import de.jClipCorn.features.statistics.snapshots.*;
import de.jClipCorn.util.datetime.CCDate;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/** Storage round trip, the sparse-row reader, and the writer's decision of when to write at all. */
@SuppressWarnings("nls")
public class TestStatisticsSnapshots extends ClipCornBaseTest {

	private static CCStatSnapshot snapshot(CCDate date, boolean exact, int movieCount, long movieBytes, Map<String, Integer> formats) {
		Map<StatClass, Integer> count   = new EnumMap<>(StatClass.class);
		Map<StatClass, Long>    bytes   = new EnumMap<>(StatClass.class);
		Map<StatClass, Integer> minutes = new EnumMap<>(StatClass.class);

		for (StatClass c : StatClass.values()) { count.put(c, 0); bytes.put(c, 0L); minutes.put(c, 0); }

		count.put(StatClass.MOVIE, movieCount);
		bytes.put(StatClass.MOVIE, movieBytes);
		minutes.put(StatClass.MOVIE, movieCount * 100);

		Map<StatClass, Map<String, Map<String, Integer>>> hist = new EnumMap<>(StatClass.class);
		for (StatClass c : StatClass.values()) hist.put(c, new HashMap<>());
		if (formats != null) hist.get(StatClass.MOVIE).put("FORMAT", formats);

		return new CCStatSnapshot(date, exact, count, bytes, minutes, hist);
	}

	@Test
	public void testRoundTrip() throws Exception {
		CCMovieList ml = createEmptyDB();
		CCDatabase  db = ml.getDatabaseForUnitTests();

		CCStatSnapshot written = snapshot(CCDate.create(4, 5, 2024), true, 7, 12345678901L, Map.of("0", 5, "6", 2));
		db.writeStatSnapshot(written);

		List<CCStatSnapshot> read = db.readStatSnapshots();
		assertEquals(1, read.size());

		CCStatSnapshot r = read.get(0);
		assertTrue(written.Date.isEqual(r.Date));
		assertTrue(written.valuesEqual(r));
		assertEquals(Integer.valueOf(5), r.histogram(StatClass.MOVIE, "FORMAT").get("0"));
		assertEquals(Integer.valueOf(2), r.histogram(StatClass.MOVIE, "FORMAT").get("6"));
	}

	/** The primary key is the day, so writing it twice has to update, not add a second row. */
	@Test
	public void testSecondWriteOfADayUpdates() throws Exception {
		CCMovieList ml = createEmptyDB();
		CCDatabase  db = ml.getDatabaseForUnitTests();

		CCDate day = CCDate.create(4, 5, 2024);
		db.writeStatSnapshot(snapshot(day, true, 7, 100, null));
		db.writeStatSnapshot(snapshot(day, true, 9, 200, null));

		List<CCStatSnapshot> read = db.readStatSnapshots();
		assertEquals(1, read.size());
		assertEquals(9,   read.get(0).count(StatClass.MOVIE));
		assertEquals(200, read.get(0).bytes(StatClass.MOVIE));
	}

	@Test
	public void testReadIsOrderedAscending() throws Exception {
		CCMovieList ml = createEmptyDB();
		CCDatabase  db = ml.getDatabaseForUnitTests();

		db.writeStatSnapshot(snapshot(CCDate.create(9, 9, 2024), true, 3, 30, null));
		db.writeStatSnapshot(snapshot(CCDate.create(1, 1, 2020), true, 1, 10, null));
		db.writeStatSnapshot(snapshot(CCDate.create(5, 5, 2022), true, 2, 20, null));

		List<CCStatSnapshot> read = db.readStatSnapshots();
		assertEquals(3, read.size());
		for (int i = 1; i < read.size(); i++) assertTrue(read.get(i).Date.isGreaterThan(read.get(i-1).Date));

		assertTrue(CCDate.create(9, 9, 2024).isEqual(db.readLastStatSnapshot().Date));
	}

	/** Days without a row take the values of the newest row before them. */
	@Test
	public void testSparseRowsAreCarriedForward() throws Exception {
		CCMovieList ml = createEmptyDB();
		CCDatabase  db = ml.getDatabaseForUnitTests();

		CCDate d0 = CCDate.create(1, 3, 2024);

		db.writeStatSnapshot(snapshot(d0,              true, 1, 100, null));
		db.writeStatSnapshot(snapshot(d0.getAddDay(5), true, 4, 400, null));

		StatSnapshotSeries s = StatSnapshotSeries.load(ml, StatisticsTypeFilter.STF_MOVIES);

		assertTrue(s.firstDay().isEqual(d0));
		assertTrue(s.dayCount() >= 6);

		long[] bytes = s.bytes();
		for (int i = 0; i < 5; i++) assertEquals("day " + i, 100, bytes[i]);
		for (int i = 5; i < bytes.length; i++) assertEquals("day " + i, 400, bytes[i]);

		assertTrue(s.exactSince().isEqual(d0));
	}

	/**
	 * Before a rebuild has run there is nothing in the table, and the charts still have to render - they
	 * fall back to an in-memory estimate that is never persisted.
	 */
	@Test
	public void testEmptyTableFallsBackToAnEstimate() throws Exception {
		CCMovieList ml = createExampleDB();

		StatSnapshotSeries s = StatSnapshotSeries.load(ml, StatisticsTypeFilter.STF_MOVIES_AND_EPISODES);

		assertFalse(s.isEmpty());
		assertNull(s.exactSince());

		int[] count = s.count();
		assertEquals(0, count[0]);
		assertTrue(count[count.length - 1] > 0);

		// the fallback must not have written anything
		assertEquals(0, ml.getDatabaseForUnitTests().readStatSnapshots().size());
	}

	@Test
	public void testWriterSkipsAnUnchangedDay() throws Exception {
		CCMovieList ml = createExampleDB();
		CCDatabase  db = ml.getDatabaseForUnitTests();

		StatSnapshotWriter writer = new StatSnapshotWriter(ml, db);
		try {
			writer.updateToday();
			assertEquals(1, db.readStatSnapshots().size());

			writer.updateToday();
			assertEquals(1, db.readStatSnapshots().size());
		} finally {
			writer.shutdown();
		}
	}

	/** A changed collection gets a new row rather than an update of the older one. */
	@Test
	public void testWriterAddsARowWhenTheValuesChanged() throws Exception {
		CCMovieList ml = createExampleDB();
		CCDatabase  db = ml.getDatabaseForUnitTests();

		db.writeStatSnapshot(snapshot(CCDate.getCurrentDate().getSubDay(3), true, 1, 1, null));

		StatSnapshotWriter writer = new StatSnapshotWriter(ml, db);
		try {
			writer.updateToday();

			List<CCStatSnapshot> rows = db.readStatSnapshots();
			assertEquals(2, rows.size());
			assertTrue(rows.get(1).Date.isEqual(CCDate.getCurrentDate()));
		} finally {
			writer.shutdown();
		}
	}

	@Test
	public void testHistogramJsonRoundTrip() {
		CCStatSnapshot s = snapshot(CCDate.create(1, 1, 2024), true, 3, 300, Map.of("0", 2, "6", 1));

		Map<String, Map<String, Integer>> back = CCStatSnapshot.histogramsFromJson(s.histogramsToJson(StatClass.MOVIE));

		assertEquals(Integer.valueOf(2), back.get("FORMAT").get("0"));
		assertEquals(Integer.valueOf(1), back.get("FORMAT").get("6"));
	}

	@Test
	public void testHistogramJsonOfAFutureVersionIsIgnoredRatherThanFatal() {
		assertTrue(CCStatSnapshot.histogramsFromJson("{\"v\":99,\"FORMAT\":{\"0\":5}}").isEmpty());
		assertTrue(CCStatSnapshot.histogramsFromJson("not json at all").isEmpty());
		assertTrue(CCStatSnapshot.histogramsFromJson(null).isEmpty());
	}
}
