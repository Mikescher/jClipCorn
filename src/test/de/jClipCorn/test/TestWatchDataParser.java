package de.jClipCorn.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.helper.SimpleFileUtils;
import de.jClipCorn.util.parser.watchdata.WatchDataChangeSet;
import de.jClipCorn.util.parser.watchdata.WatchDataParser;

@SuppressWarnings("nls")
public class TestWatchDataParser extends ClipCornBaseTest {

	@Test
	public void testWatchDataParse() throws Exception {
		CCMovieList ml = createExampleDB();
		
		List<String> err = new ArrayList<>();
		List<WatchDataChangeSet> r = WatchDataParser.parse(ml, SimpleFileUtils.readTextResource("/example_watchdata.txt", getClass()), err);
		
		assertEquals(0, err.size());
		
		assertEquals(16, r.size());
		
		for (WatchDataChangeSet wdcs : r) wdcs.execute();
		
		assertEquals(true, ml.getSeries("Steins;Gate").getSeasonByArrayIndex(0).getEpisodeByNumber(1).isViewed());
		assertTrue(ml.getSeries("Steins;Gate").getSeasonByArrayIndex(0).getEpisodeByNumber(1).getViewedHistory().contains(CCDateTime.parse("1.1.12", "d.M.y")));

		assertEquals(true, ml.getSeries("Steins;Gate").getSeasonByArrayIndex(0).getEpisodeByNumber(2).isViewed());
		assertTrue(ml.getSeries("Steins;Gate").getSeasonByArrayIndex(0).getEpisodeByNumber(2).getViewedHistory().contains(CCDateTime.parse("2.12", "d.M")));

		assertEquals(true, ml.getSeries("Soul Eater").getSeasonByArrayIndex(0).getEpisodeByNumber(6).isViewed());
		assertEquals(false, ml.getSeries("Soul Eater").getSeasonByArrayIndex(0).getEpisodeByNumber(7).isViewed());
		assertEquals(true, ml.getSeries("Soul Eater").getSeasonByArrayIndex(1).getEpisodeByNumber(17).isViewed());

		assertEquals(true, ml.getMovie("Hypercube").isViewed());

		assertEquals(true, ml.getMovie("Super 8").isViewed());
		assertTrue(ml.getMovie("Super 8").getViewedHistory().contains(CCDateTime.create(11, 12, 2012, 19, 4, 0)));

		assertEquals(true, ml.getMovie("Death Proof: Todsicher").isViewed());
		assertTrue(ml.getMovie("Death Proof: Todsicher").getViewedHistory().contains(CCDateTime.create(11, 12, 2012, 19, 4, 0)));
		assertEquals(CCUserScore.RATING_IV, ml.getMovie("Death Proof: Todsicher").getScore());

		assertEquals(true, ml.getMovie("Der Bomber").isViewed());
		assertEquals(CCUserScore.RATING_V, ml.getMovie("Der Bomber").getScore());
	}
}
