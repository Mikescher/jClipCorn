package de.jClipCorn.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.util.cciterator.ReverseIterator;
import de.jClipCorn.util.cciterator.SortedIterator;
import de.jClipCorn.util.comparator.CCMovieComparator;
import de.jClipCorn.util.datetime.CCDate;

public class TestIterators extends ClipCornBaseTest {

	@Test
	public void testDatabaseIterator() throws Exception {
		CCMovieList ml = createExampleDB();
		
		assertEquals(20, ml.iteratorElements().count());
		assertEquals(20, ml.iteratorElementsSorted().count());
		assertEquals(20, ml.iteratorElements().enumerate().size());
		assertEquals(20, ml.iteratorElementsSorted().enumerate().size());
	}

	@Test
	public void testMoviesIterator() throws Exception {
		CCMovieList ml = createExampleDB();
		
		assertEquals(17, ml.iteratorMovies().count());
		assertEquals(17, ml.iteratorMoviesSorted().count());
		assertEquals(17, ml.iteratorMovies().enumerate().size());
		assertEquals(17, ml.iteratorMoviesSorted().enumerate().size());
	}

	@Test
	public void testSeriesIterator() throws Exception {
		CCMovieList ml = createExampleDB();
		
		assertEquals(3, ml.iteratorSeries().count());
		assertEquals(3, ml.iteratorSeriesSorted().count());
		assertEquals(3, ml.iteratorSeries().enumerate().size());
		assertEquals(3, ml.iteratorSeriesSorted().enumerate().size());
	}

	@Test
	public void testEpisodeIterator() throws Exception {
		CCMovieList ml = createExampleDB();
		
		assertEquals(86, ml.iteratorEpisodes().count());
		assertEquals(86, ml.iteratorEpisodes().enumerate().size());
	}

	@Test
	public void testReverseIterator() throws Exception {
		CCMovieList ml = createExampleDB();

		assertEquals(ml.iteratorElements().count(), new ReverseIterator<>(ml.iteratorElements().enumerate()).count());
		assertEquals(ml.iteratorEpisodes().count(), new ReverseIterator<>(ml.iteratorEpisodes().enumerate()).count());
		assertEquals(ml.iteratorMovies().count(), new ReverseIterator<>(ml.iteratorMovies().enumerate()).count());
		assertEquals(ml.iteratorSeries().count(), new ReverseIterator<>(ml.iteratorSeries().enumerate()).count());
	}

	@Test
	public void testSortedIterator() throws Exception {
		CCMovieList ml = createExampleDB();

		assertEquals(12, new SortedIterator<>(ml.iteratorMovies().enumerate(), new CCMovieComparator()).enumerate().get(0).getLocalID());
		assertEquals(5, new SortedIterator<>(ml.iteratorMovies().enumerate(), new CCMovieComparator()).enumerate().get(1).getLocalID());
		assertEquals(16, new SortedIterator<>(ml.iteratorMovies().enumerate(), new CCMovieComparator()).enumerate().get(2).getLocalID());
		assertEquals(0, new SortedIterator<>(ml.iteratorMovies().enumerate(), new CCMovieComparator()).enumerate().get(3).getLocalID());
		assertEquals(9, new SortedIterator<>(ml.iteratorMovies().enumerate(), new CCMovieComparator()).enumerate().get(4).getLocalID());
	}

	@Test
	public void testCastIterator() throws Exception {
		CCMovieList ml = createExampleDB();
		
		assertEquals(ml.iteratorEpisodes().count(), ml.iteratorEpisodes().asCasted().count());
		assertEquals(ml.iteratorMovies().count(), ml.iteratorMovies().asCasted().count());
		assertEquals(ml.iteratorSeries().count(), ml.iteratorSeries().asCasted().count());
		assertEquals(ml.iteratorPlayables().count(), ml.iteratorPlayables().asCasted().count());
	}

	@Test
	public void testPlayablesIterator() throws Exception {
		CCMovieList ml = createExampleDB();
		
		assertEquals(103, ml.iteratorPlayables().count());
		assertEquals(103, ml.iteratorPlayables().enumerate().size());
	}

	@Test
	public void testDirectEpisodesIterator() throws Exception {
		CCMovieList ml = createExampleDB();
		
		assertEquals(25, ml.findDatabaseSeries(10).iteratorEpisodes().count());
		assertEquals(51, ml.findDatabaseSeries(11).iteratorEpisodes().count());
		assertEquals(10, ml.findDatabaseSeries(19).iteratorEpisodes().count());
	}

	@Test
	public void testSeasonsIterator() throws Exception {
		CCMovieList ml = createExampleDB();

		assertEquals(7, ml.iteratorSeasons().count());
	}

	@Test
	public void testFilterIterator() throws Exception {
		CCMovieList ml = createExampleDB();

		assertEquals(97, ml.iteratorPlayables().filter(p -> p.isViewed()).count());
		assertEquals(6, ml.iteratorPlayables().filter(p -> !p.isViewed()).count());

		assertEquals(ml.iteratorMovies().count(), ml.iteratorPlayables().filter(p -> p instanceof CCMovie).count());
		assertEquals(ml.iteratorEpisodes().count(), ml.iteratorPlayables().filter(p -> p instanceof CCEpisode).count());

		assertEquals(0, ml.iteratorElements().filter(p -> false).count());
		assertEquals(false, ml.iteratorElements().filter(p -> false).any());

		assertEquals(ml.getElementCount(), ml.iteratorElements().filter(p -> true).count());
		assertEquals(true, ml.iteratorElements().filter(p -> true).any());
	}

	@Test
	public void testDatedIterator() throws Exception {
		CCMovieList ml = createExampleDB();

		assertEquals(24, ml.iteratorDatedElements().count());
	}

	@Test
	public void testMapIterator() throws Exception {
		CCMovieList ml = createExampleDB();

		assertEquals(97, ml.iteratorPlayables().map(p -> p.isViewed()).filter(p -> p).count());
		assertEquals(6, ml.iteratorPlayables().map(p -> p.isViewed()).filter(p -> !p).count());

		assertEquals(CCDate.create(3, 8, 2010), ml.iteratorMovies().map(p -> p.getAddDate()).asSorted(CCDate::compare).firstOrNull());
		assertEquals(CCDate.create(2, 7, 2016), ml.iteratorMovies().map(p -> p.getAddDate()).asSorted(CCDate::compare).lastOrNull());

		assertEquals(CCDate.create(3, 8, 2010), ml.iteratorPlayables().map(p -> p.getAddDate()).asSorted(CCDate::compare).firstOrNull());
		assertEquals(CCDate.create(3, 10, 2016), ml.iteratorPlayables().map(p -> p.getAddDate()).asSorted(CCDate::compare).lastOrNull());

		assertEquals(ml.iteratorElements().minOrDefault(p -> p.getAddDate(), CCDate::compare, null), ml.iteratorElements().map(p -> p.getAddDate()).asSorted(CCDate::compare).firstOrNull());
		assertEquals(ml.iteratorElements().maxOrDefault(p -> p.getAddDate(), CCDate::compare, null), ml.iteratorElements().map(p -> p.getAddDate()).asSorted(CCDate::compare).lastOrNull());

		assertEquals(ml.iteratorPlayables().minOrDefault(p -> p.getAddDate(), CCDate::compare, null), ml.iteratorPlayables().map(p -> p.getAddDate()).asSorted(CCDate::compare).firstOrNull());
		assertEquals(ml.iteratorPlayables().maxOrDefault(p -> p.getAddDate(), CCDate::compare, null), ml.iteratorPlayables().map(p -> p.getAddDate()).asSorted(CCDate::compare).lastOrNull());
	}

	@Test
	public void testIterator() throws Exception {
		CCMovieList ml = createExampleDB();

		assertEquals(0, ml.iteratorMovies().map(p -> p.getLocalID()).asSorted(Integer::compare).firstOrNull().intValue());
		assertEquals(18, ml.iteratorMovies().map(p -> p.getLocalID()).asSorted(Integer::compare).lastOrNull().intValue());
		assertEquals(19, ml.iteratorElements().map(p -> p.getLocalID()).asSorted(Integer::compare).lastOrNull().intValue());
	}
}
