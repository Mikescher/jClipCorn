package de.jClipCorn.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDatespan;
import de.jClipCorn.util.helper.StatisticsHelper;

public class TestStatistics {

	@Test
	public void testStatisticsHelperSimple() throws Exception {
		CCLog.setUnitTestMode();
		
		CCMovieList ml = ClipCornTestHelper.createExampleDB();

		assertEquals(11, StatisticsHelper.getViewedMovieCount(ml));
		assertEquals(6, StatisticsHelper.getUnviewedMovieCount(ml));
		assertEquals(86, StatisticsHelper.getViewedEpisodeCount(ml));
		
		assertEquals(2165, StatisticsHelper.getMovieDuration(ml));
		assertEquals(2003, StatisticsHelper.getSeriesDuration(ml));
		assertEquals(4168, StatisticsHelper.getTotalDuration(ml));
		

		assertEquals(82481336554L, StatisticsHelper.getMovieSize(ml).getBytes());
		assertEquals(36278101444L, StatisticsHelper.getSeriesSize(ml).getBytes());
		assertEquals(118759437998L, StatisticsHelper.getTotalSize(ml).getBytes());
		assertEquals(959085308L, StatisticsHelper.getAvgMovieSize(ml).getBytes());
		assertEquals(421838388L, StatisticsHelper.getAvgSeriesSize(ml).getBytes());
		assertEquals(3.8, StatisticsHelper.getAvgImDbRating(ml), 0.1);
		
		assertEquals(1545, StatisticsHelper.getViewedMovieDuration(ml));
		assertEquals(2003, StatisticsHelper.getViewedSeriesDuration(ml));
		assertEquals(3548, StatisticsHelper.getViewedTotalDuration(ml));

		assertEquals(CCDate.create(3, 8, 2010), StatisticsHelper.getFirstMovieAddDate(ml));
		assertEquals(CCDate.create(2, 7, 2016), StatisticsHelper.getLastMovieAddDate(ml));
		assertEquals(CCDate.create(13, 12, 2015), StatisticsHelper.getFirstSeriesAddDate(ml));
		assertEquals(CCDate.create(3, 10, 2016), StatisticsHelper.getLastSeriesAddDate(ml));

		assertEquals(CCDate.create(6, 5, 2016), StatisticsHelper.getFirstEpisodeWatchedDate(ml));
		assertEquals(CCDate.create(3, 10, 2016), StatisticsHelper.getLastEpisodeWatchedDate(ml));

		assertEquals(1980, StatisticsHelper.getMinimumMovieYear(ml));
		assertEquals(2012, StatisticsHelper.getMaximumMovieYear(ml));
		
		assertEquals(90, StatisticsHelper.getMinimumMovieLength(ml));
		assertEquals(201, StatisticsHelper.getMaximumMovieLength(ml));

	}

	@Test
	public void testStatisticsHelperGetMovieCountNoException() throws Exception {
		CCLog.setUnitTestMode();
		
		CCMovieList ml = ClipCornTestHelper.createExampleDB();

		StatisticsHelper.getMovieCountForAllDates(ml, CCDate.getMinimumDate(), 128);
		StatisticsHelper.getMovieCountForAllLengths(ml, 0, 500);
		StatisticsHelper.getMovieCountForAllFormats(ml);
		StatisticsHelper.getMovieCountForAllFSKs(ml);
		StatisticsHelper.getMovieCountForAllGenres(ml);
		StatisticsHelper.getMovieCountForAllOnlinescores(ml);
		StatisticsHelper.getMovieCountForAllQualities(ml);
		StatisticsHelper.getMovieCountForAllTags(ml);
		StatisticsHelper.getMovieCountForAllScores(ml);
		StatisticsHelper.getMovieCountForAllYears(ml, 1900, 512);
		StatisticsHelper.getMovieCountForAllLanguages(ml);
		
		StatisticsHelper.getElementCountForAllProvider(ml);
	}

	@Test
	public void testStatisticsHelperGetAddedForAllDatesNoException() throws Exception {
		CCLog.setUnitTestMode();
		
		CCMovieList ml = ClipCornTestHelper.createExampleDB();

		CCDate mf = StatisticsHelper.getFirstMovieAddDate(ml);
		int mdc = StatisticsHelper.getFirstMovieAddDate(ml).getDayDifferenceTo(StatisticsHelper.getLastMovieAddDate(ml)) + 1;
		
		StatisticsHelper.getAddedByteCountForAllDates(ml, mf, mdc);
		StatisticsHelper.getAddedMinuteCountForAllDates(ml, mf, mdc);
		StatisticsHelper.getAddedMoviesFormatLengthForAllDates(ml, mf, mdc);
		
		CCDate sf = StatisticsHelper.getFirstSeriesAddDate(ml);
		int sdc = StatisticsHelper.getFirstSeriesAddDate(ml).getDayDifferenceTo(StatisticsHelper.getLastSeriesAddDate(ml)) + 1;
		
		StatisticsHelper.getAddedSeriesCountForAllDates(ml, sf, sdc);
		StatisticsHelper.getAddedSeriesFormatLengthForAllDates(ml, sf, sdc);
	}

	@Test
	public void testStatisticsHelperNoException() throws Exception {
		CCLog.setUnitTestMode();
		
		CCMovieList ml = ClipCornTestHelper.createExampleDB();

		CCDate mindate = StatisticsHelper.getFirstEpisodeWatchedDate(ml).getSubDay(1);
		CCDate maxdate = StatisticsHelper.getLastEpisodeWatchedDate(ml);
		int daycount = mindate.getDayDifferenceTo(maxdate) + 1;
		
		StatisticsHelper.getEpisodesViewedForAllDates(ml, mindate, daycount);
		StatisticsHelper.getAllSeriesTimespans(ml, 0);
		StatisticsHelper.getAllSeriesTimespans(ml, 7);
		StatisticsHelper.getAllSeriesTimespans(ml, 56);
	}

	@Test
	public void testStatisticsHelperGetDatespanFromSeries() throws Exception {
		CCLog.setUnitTestMode();
		
		CCMovieList ml = ClipCornTestHelper.createExampleDB();

		assertEquals(8, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(10), 0).size());
		assertEquals(3, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(10), 7).size());
		assertEquals(2, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(10), 56).size());

		assertEquals(8, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(11), 0).size());
		assertEquals(1, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(11), 7).size());
		assertEquals(1, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(11), 56).size());

		assertEquals(1, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(19), 0).size());
		assertEquals(1, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(19), 7).size());
		assertEquals(1, StatisticsHelper.getDatespanFromSeries(ml.findDatabaseSeries(19), 56).size());
	}

	@Test
	public void testStatisticsHelperGetSeriesTimespansStartEnd() throws Exception {
		CCLog.setUnitTestMode();
		
		CCMovieList ml = ClipCornTestHelper.createExampleDB();

		HashMap<CCSeries, List<CCDatespan>> map_0 = StatisticsHelper.getAllSeriesTimespans(ml, 0);
		HashMap<CCSeries, List<CCDatespan>> map_1 = StatisticsHelper.getAllSeriesTimespans(ml, 7);
		HashMap<CCSeries, List<CCDatespan>> map_2 = StatisticsHelper.getAllSeriesTimespans(ml, 56);

		assertEquals(CCDate.create(6, 5, 2016), StatisticsHelper.getSeriesTimespansStart(map_0));
		assertEquals(CCDate.create(6, 5, 2016), StatisticsHelper.getSeriesTimespansStart(map_1));
		assertEquals(CCDate.create(6, 5, 2016), StatisticsHelper.getSeriesTimespansStart(map_2));

		assertEquals(CCDate.create(03, 10, 2016), StatisticsHelper.getSeriesTimespansEnd(map_0));
		assertEquals(CCDate.create(03, 10, 2016), StatisticsHelper.getSeriesTimespansEnd(map_1));
		assertEquals(CCDate.create(03, 10, 2016), StatisticsHelper.getSeriesTimespansEnd(map_2));

		assertEquals(8, map_0.get(ml.findDatabaseSeries(10)).size());
		assertEquals(8, map_0.get(ml.findDatabaseSeries(11)).size());
		assertEquals(1, map_0.get(ml.findDatabaseSeries(19)).size());

		assertEquals(3, map_1.get(ml.findDatabaseSeries(10)).size());
		assertEquals(1, map_1.get(ml.findDatabaseSeries(11)).size());
		assertEquals(1, map_1.get(ml.findDatabaseSeries(19)).size());

		assertEquals(2, map_2.get(ml.findDatabaseSeries(10)).size());
		assertEquals(1, map_2.get(ml.findDatabaseSeries(11)).size());
		assertEquals(1, map_2.get(ml.findDatabaseSeries(19)).size());
	}

	@Test
	public void testStatisticsHelperGetEpisodesWithExplicitLastViewedDate() throws Exception {
		CCLog.setUnitTestMode();
		
		CCMovieList ml = ClipCornTestHelper.createExampleDB();

		assertEquals(25, StatisticsHelper.getEpisodesWithExplicitLastViewedDate(ml.findDatabaseSeries(10)).size());
		assertEquals(51, StatisticsHelper.getEpisodesWithExplicitLastViewedDate(ml.findDatabaseSeries(11)).size());
		assertEquals(10, StatisticsHelper.getEpisodesWithExplicitLastViewedDate(ml.findDatabaseSeries(19)).size());
	}
}
