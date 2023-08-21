package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.ICCDatedElement;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.features.statistics.StatisticsHelper;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDatespan;
import de.jClipCorn.util.stream.CCStream;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestStatistics extends ClipCornBaseTest {

	@Test
	public void testStatisticsHelperSimple() throws Exception {
		CCMovieList ml = createExampleDB();

		assertEquals((Integer)11, StatisticsHelper.getViewedCount(ml.iteratorMovies().cast()));
		assertEquals((Integer)86, StatisticsHelper.getViewedCount(ml.iteratorEpisodes().cast()));
		assertEquals((Integer)97, StatisticsHelper.getViewedCount(ml.iteratorPlayables().cast()));
		
		assertEquals((Integer)6, StatisticsHelper.getUnviewedCount(ml.iteratorMovies().cast()));
		assertEquals((Integer)0, StatisticsHelper.getUnviewedCount(ml.iteratorEpisodes().cast()));
		assertEquals((Integer)6, StatisticsHelper.getUnviewedCount(ml.iteratorPlayables().cast()));
		
		assertEquals((Integer)2314, StatisticsHelper.getMovieDuration(ml));
		assertEquals((Integer)2003, StatisticsHelper.getSeriesDuration(ml));
		assertEquals((Integer)4317, StatisticsHelper.getTotalDuration(ml));
		

		assertEquals(82473488109L, StatisticsHelper.getMovieSize(ml).getBytes());
		assertEquals(36278101444L, StatisticsHelper.getSeriesSize(ml).getBytes());
		assertEquals(118751589553L, StatisticsHelper.getTotalSize(ml).getBytes());
		assertEquals(958994047L, StatisticsHelper.getAvgMovieSize(ml).getBytes());
		assertEquals(421838388L, StatisticsHelper.getAvgSeriesSize(ml).getBytes());
		assertEquals(3.8, StatisticsHelper.getAvgImDbRating(ml), 0.1);
		
		assertEquals((Integer)1713, StatisticsHelper.getViewedMovieDuration(ml));
		assertEquals((Integer)2003, StatisticsHelper.getViewedSeriesDuration(ml));
		assertEquals((Integer)3716, StatisticsHelper.getViewedTotalDuration(ml));

		assertEquals(CCDate.create(3, 8, 2010), StatisticsHelper.getFirstAddDate(ml.iteratorMovies().cast()));
		assertEquals(CCDate.create(2, 7, 2016), StatisticsHelper.getLastAddDate(ml.iteratorMovies().cast()));
		assertEquals(CCDate.create(13, 12, 2015), StatisticsHelper.getFirstAddDate(ml.iteratorEpisodes().cast()));
		assertEquals(CCDate.create(3, 10, 2016), StatisticsHelper.getLastAddDate(ml.iteratorEpisodes().cast()));

		assertEquals(CCDate.create(6, 5, 2016), StatisticsHelper.getFirstWatchedDate(ml.iteratorEpisodes().cast()));
		assertEquals(CCDate.create(3, 10, 2016), StatisticsHelper.getLastWatchedDate(ml.iteratorEpisodes().cast()));

		assertEquals((Integer)1980, StatisticsHelper.getMinimumYear(ml.iteratorMovies().cast()));
		assertEquals((Integer)2012, StatisticsHelper.getMaximumYear(ml.iteratorMovies().cast()));
		
		assertEquals((Integer)80, StatisticsHelper.getMinimumLength(ml.iteratorMovies().cast()));
		assertEquals((Integer)263, StatisticsHelper.getMaximumLength(ml.iteratorMovies().cast()));

	}

	@Test
	public void testStatisticsHelperGetMovieCountNoException() throws Exception {
		CCMovieList ml = createExampleDB();
		
		StatisticsHelper.getCountForAllDates(CCDate.getMinimumDate(), 128, ml.iteratorPlayables().cast());
		StatisticsHelper.getCountForAllDates(CCDate.getMinimumDate(), 128, ml.iteratorMovies().cast());
		StatisticsHelper.getCountForAllDates(CCDate.getMinimumDate(), 128, ml.iteratorEpisodes().cast());

		CCStream<ICCPlayableElement> it1 = ml.iteratorMovies().cast();
		CCStream<CCDatabaseElement>  it2 = ml.iteratorMovies().cast();
		CCStream<ICCDatedElement>    it3 = ml.iteratorMovies().cast();
		
		StatisticsHelper.getCountForAllLengths(0, 500, it1);
		StatisticsHelper.getCountForAllFormats(it1);
		StatisticsHelper.getCountForAllFSKs(it2);
		StatisticsHelper.getCountForAllGenres(it2);
		StatisticsHelper.getCountForAllOnlinescores(it2);
		StatisticsHelper.getCountForAllTags(it1);
		StatisticsHelper.getCountForAllScores(it2.cast());
		StatisticsHelper.getCountForAllYears(1900, 512, it3);
		StatisticsHelper.getCountForAllLanguages(it1);

		StatisticsHelper.getElementCountForAllProviderMain(it2);
		StatisticsHelper.getElementCountForAllProviderAny(it2);
	}

	@Test
	public void testStatisticsHelperGetAddedForAllDatesNoException() throws Exception {
		CCMovieList ml = createExampleDB();

		{
			CCDate mf = StatisticsHelper.getFirstAddDate(ml.iteratorMovies().cast());
			int mdc = StatisticsHelper.getFirstAddDate(ml.iteratorMovies().cast()).getDayDifferenceTo(StatisticsHelper.getLastAddDate(ml.iteratorMovies().cast())) + 1;
			
			StatisticsHelper.getCumulativeByteCountForAllDates(mf, mdc, ml.iteratorMovies().cast());
			StatisticsHelper.getCumulativeMinuteCountForAllDates(mf, mdc, ml.iteratorMovies().cast());
			StatisticsHelper.getCumulativeFormatCountForAllDates(mf, mdc, ml.iteratorMovies().cast());
		}

		{
			CCDate sf = StatisticsHelper.getFirstAddDate(ml.iteratorEpisodes().cast());
			int sdc = StatisticsHelper.getFirstAddDate(ml.iteratorEpisodes().cast()).getDayDifferenceTo(StatisticsHelper.getLastAddDate(ml.iteratorEpisodes().cast())) + 1;
	
			StatisticsHelper.getCumulativeMinuteCountForAllDates(sf, sdc, ml.iteratorEpisodes().cast());
			StatisticsHelper.getCumulativeMinuteCountForAllDates(sf, sdc, ml.iteratorEpisodes().cast());
			StatisticsHelper.getCumulativeFormatCountForAllDates(sf, sdc, ml.iteratorEpisodes().cast());
		}
		
		{
			CCDate pf = StatisticsHelper.getFirstAddDate(ml.iteratorPlayables().cast());
			int pdc = StatisticsHelper.getFirstAddDate(ml.iteratorPlayables().cast()).getDayDifferenceTo(StatisticsHelper.getLastAddDate(ml.iteratorPlayables().cast())) + 1;
			
			StatisticsHelper.getCumulativeMinuteCountForAllDates(pf, pdc, ml.iteratorPlayables().cast());
			StatisticsHelper.getCumulativeMinuteCountForAllDates(pf, pdc, ml.iteratorPlayables().cast());
			StatisticsHelper.getCumulativeFormatCountForAllDates(pf, pdc, ml.iteratorPlayables().cast());
		}
	}

	@Test
	public void testStatisticsHelperNoException() throws Exception {
		CCMovieList ml = createExampleDB();

		CCDate mindate = StatisticsHelper.getFirstWatchedDate(ml.iteratorEpisodes().cast()).getSubDay(1);
		CCDate maxdate = StatisticsHelper.getLastWatchedDate(ml.iteratorEpisodes().cast());
		int daycount = mindate.getDayDifferenceTo(maxdate) + 1;
		
		StatisticsHelper.getViewedForAllDates(mindate, daycount, ml.iteratorEpisodes().cast());
		StatisticsHelper.getAllSeriesTimespans(ml, 0, StatisticsHelper.OrderMode.ENFORCED);
		StatisticsHelper.getAllSeriesTimespans(ml, 0, StatisticsHelper.OrderMode.IGNORED);
		StatisticsHelper.getAllSeriesTimespans(ml, 7, StatisticsHelper.OrderMode.ENFORCED);
		StatisticsHelper.getAllSeriesTimespans(ml, 56, StatisticsHelper.OrderMode.ENFORCED);
	}

	@Test
	public void testStatisticsHelperGetDatespanFromSeries() throws Exception {
		CCMovieList ml = createExampleDB();

		assertEquals(8, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(11), 0, StatisticsHelper.OrderMode.ENFORCED).size());
		assertEquals(8, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(11), 0, StatisticsHelper.OrderMode.IGNORED).size());
		assertEquals(3, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(11), 7, StatisticsHelper.OrderMode.ENFORCED).size());
		assertEquals(2, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(11), 56, StatisticsHelper.OrderMode.ENFORCED).size());

		assertEquals(8, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(39), 0, StatisticsHelper.OrderMode.ENFORCED).size());
		assertEquals(8, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(39), 0, StatisticsHelper.OrderMode.IGNORED).size());
		assertEquals(1, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(39), 7, StatisticsHelper.OrderMode.ENFORCED).size());
		assertEquals(1, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(39), 56, StatisticsHelper.OrderMode.ENFORCED).size());

		assertEquals(1, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(102), 0, StatisticsHelper.OrderMode.ENFORCED).size());
		assertEquals(1, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(102), 0, StatisticsHelper.OrderMode.IGNORED).size());
		assertEquals(1, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(102), 7, StatisticsHelper.OrderMode.ENFORCED).size());
		assertEquals(1, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(102), 56, StatisticsHelper.OrderMode.ENFORCED).size());
	}

	@Test
	public void testStatisticsHelperGetSeriesTimespansStartEnd() throws Exception {
		CCMovieList ml = createExampleDB();

		HashMap<CCSeries, List<CCDatespan>> map_0 = StatisticsHelper.getAllSeriesTimespans(ml, 0, StatisticsHelper.OrderMode.ENFORCED);
		HashMap<CCSeries, List<CCDatespan>> map_1 = StatisticsHelper.getAllSeriesTimespans(ml, 7, StatisticsHelper.OrderMode.ENFORCED);
		HashMap<CCSeries, List<CCDatespan>> map_2 = StatisticsHelper.getAllSeriesTimespans(ml, 56, StatisticsHelper.OrderMode.ENFORCED);

		assertEquals(CCDate.create(6, 5, 2016), StatisticsHelper.getSeriesTimespansStart(map_0));
		assertEquals(CCDate.create(6, 5, 2016), StatisticsHelper.getSeriesTimespansStart(map_1));
		assertEquals(CCDate.create(6, 5, 2016), StatisticsHelper.getSeriesTimespansStart(map_2));

		assertEquals(CCDate.create(4, 10, 2016), StatisticsHelper.getSeriesTimespansEnd(map_0));
		assertEquals(CCDate.create(4, 10, 2016), StatisticsHelper.getSeriesTimespansEnd(map_1));
		assertEquals(CCDate.create(4, 10, 2016), StatisticsHelper.getSeriesTimespansEnd(map_2));

		assertEquals(8, map_0.get(ml.findDatabaseSeries( 11)).size());
		assertEquals(8, map_0.get(ml.findDatabaseSeries( 39)).size());
		assertEquals(1, map_0.get(ml.findDatabaseSeries(102)).size());

		assertEquals(3, map_1.get(ml.findDatabaseSeries( 11)).size());
		assertEquals(1, map_1.get(ml.findDatabaseSeries( 39)).size());
		assertEquals(1, map_1.get(ml.findDatabaseSeries(102)).size());

		assertEquals(2, map_2.get(ml.findDatabaseSeries( 11)).size());
		assertEquals(1, map_2.get(ml.findDatabaseSeries( 39)).size());
		assertEquals(1, map_2.get(ml.findDatabaseSeries(102)).size());
	}

	@Test
	public void testStatisticsHelperGetEpisodesWithExplicitLastViewedDate() throws Exception {
		CCMovieList ml = createExampleDB();

		assertEquals(25, StatisticsHelper.getEpisodesWithExplicitLastViewedDate(ml.findDatabaseSeries( 11)).size());
		assertEquals(51, StatisticsHelper.getEpisodesWithExplicitLastViewedDate(ml.findDatabaseSeries( 39)).size());
		assertEquals(10, StatisticsHelper.getEpisodesWithExplicitLastViewedDate(ml.findDatabaseSeries(102)).size());
	}
}
