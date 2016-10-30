package de.jClipCorn.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.util.iterator.ReverseIterator;
import de.jClipCorn.database.util.iterator.SortedIterator;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.comparator.CCMovieComparator;

public class TestIterators {

	@Test
	public void testDatabaseIterator() throws Exception {
		CCLog.setUnitTestMode();
		
		CCMovieList ml = ClipCornTestHelper.createExampleDB();
		
		assertEquals(18, ml.iterator().count());
		assertEquals(18, ml.iteratorSorted().count());
		assertEquals(18, ml.iterator().enumerate().size());
		assertEquals(18, ml.iteratorSorted().enumerate().size());
	}

	@Test
	public void testMoviesIterator() throws Exception {
		CCLog.setUnitTestMode();
		
		CCMovieList ml = ClipCornTestHelper.createExampleDB();
		
		assertEquals(16, ml.iteratorMovies().count());
		assertEquals(16, ml.iteratorMoviesSorted().count());
		assertEquals(16, ml.iteratorMovies().enumerate().size());
		assertEquals(16, ml.iteratorMoviesSorted().enumerate().size());
	}

	@Test
	public void testSeriesIterator() throws Exception {
		CCLog.setUnitTestMode();
		
		CCMovieList ml = ClipCornTestHelper.createExampleDB();
		
		assertEquals(2, ml.iteratorSeries().count());
		assertEquals(2, ml.iteratorSeriesSorted().count());
		assertEquals(2, ml.iteratorSeries().enumerate().size());
		assertEquals(2, ml.iteratorSeriesSorted().enumerate().size());
	}

	@Test
	public void testEpisodeIterator() throws Exception {
		CCLog.setUnitTestMode();
		
		CCMovieList ml = ClipCornTestHelper.createExampleDB();
		
		assertEquals(76, ml.iteratorEpisodes().count());
		assertEquals(76, ml.iteratorEpisodes().enumerate().size());
	}

	@Test
	public void testReverseIterator() throws Exception {
		CCLog.setUnitTestMode();
		
		CCMovieList ml = ClipCornTestHelper.createExampleDB();

		assertEquals(ml.iterator().count(), new ReverseIterator<>(ml.iterator().enumerate()).count());
		assertEquals(ml.iteratorEpisodes().count(), new ReverseIterator<>(ml.iteratorEpisodes().enumerate()).count());
		assertEquals(ml.iteratorMovies().count(), new ReverseIterator<>(ml.iteratorMovies().enumerate()).count());
		assertEquals(ml.iteratorSeries().count(), new ReverseIterator<>(ml.iteratorSeries().enumerate()).count());
	}

	@Test
	public void testSortedIterator() throws Exception {
		CCLog.setUnitTestMode();
		
		CCMovieList ml = ClipCornTestHelper.createExampleDB();

		assertEquals(12, new SortedIterator<>(ml.iteratorMovies().enumerate(), new CCMovieComparator()).enumerate().get(0).getLocalID());
		assertEquals(5, new SortedIterator<>(ml.iteratorMovies().enumerate(), new CCMovieComparator()).enumerate().get(1).getLocalID());
		assertEquals(16, new SortedIterator<>(ml.iteratorMovies().enumerate(), new CCMovieComparator()).enumerate().get(2).getLocalID());
		assertEquals(0, new SortedIterator<>(ml.iteratorMovies().enumerate(), new CCMovieComparator()).enumerate().get(3).getLocalID());
		assertEquals(9, new SortedIterator<>(ml.iteratorMovies().enumerate(), new CCMovieComparator()).enumerate().get(4).getLocalID());
	}
}
