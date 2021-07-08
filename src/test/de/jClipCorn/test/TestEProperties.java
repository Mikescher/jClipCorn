package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.stream.CCStreams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

public class TestEProperties extends ClipCornBaseTest {

	@Test
	@SuppressWarnings("HardCodedStringLiteral")
	public void testDirty() throws Exception {
		CCMovieList ml = createExampleDB();

		for (var e : ml.iteratorStructureElements())
		{
			assertFalse(e.getQualifiedTitle()+" -> isDirty", e.isDirty());

			for (var p : e.getProperties())
			{
				assertFalse(e.getQualifiedTitle() + " -> " + p.getName() + " -> isDirty", p.isDirty());
			}
		}

		assertEquals(0, CCLog.getChangeCount());

		// CCMovie
		{
			var m1 = ml.iteratorMovies().get(1);

			m1.Title.set("New");
			assertFalse(m1.isDirty());
			assertFalse(m1.Title.isDirty());

			assertEquals(1, CCLog.getChangeCount());
			assertArrayEquals(new String[]{"Title"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

			m1.beginUpdating();
			{
				m1.OnlineScore.set(CCOnlineScore.STARS_0_5);
				assertTrue(m1.isDirty());
				assertTrue(m1.OnlineScore.isDirty());
			}
			m1.endUpdating();

			assertEquals(2, CCLog.getChangeCount());
			assertArrayEquals(new String[]{"OnlineScore"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

			m1.beginUpdating();
			{
				m1.Title.set("New");
				assertTrue(m1.isDirty());
				assertTrue(m1.Title.isDirty());

				m1.Zyklus.Title.set("New2");
				assertTrue(m1.isDirty());
				assertTrue(m1.Zyklus.Title.isDirty());
			}
			m1.endUpdating();

			assertEquals(3, CCLog.getChangeCount());
			assertArrayEquals(new String[]{"Title", "Zyklus.Title"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

			assertFalse(m1.isDirty());
			assertFalse(m1.Title.isDirty());
		}

		// CCSeries
		{
			var s1 = ml.iteratorSeries().get(1);

			s1.Title.set("New");
			assertFalse(s1.isDirty());
			assertFalse(s1.Title.isDirty());

			assertEquals(4, CCLog.getChangeCount());
			assertArrayEquals(new String[]{"Title"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

			s1.beginUpdating();
			{
				s1.Title.set("New");
				assertTrue(s1.isDirty());
				assertTrue(s1.Title.isDirty());
			}
			s1.endUpdating();

			assertEquals(5, CCLog.getChangeCount());
			assertArrayEquals(new String[]{"Title"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

			s1.beginUpdating();
			{
				s1.FSK.set(CCFSK.RATING_II);
				assertTrue(s1.isDirty());
				assertTrue(s1.FSK.isDirty());
				assertEquals(1, CCStreams.iterate(s1.getProperties()).count(p -> p.isDirty()));

				s1.Score.set(CCUserScore.RATING_I);
				assertTrue(s1.isDirty());
				assertTrue(s1.Score.isDirty());
				assertEquals(2, CCStreams.iterate(s1.getProperties()).count(p -> p.isDirty()));

				s1.Tags.set(CCTagList.EMPTY);
				assertTrue(s1.isDirty());
				assertTrue(s1.Tags.isDirty());
				assertEquals(3, CCStreams.iterate(s1.getProperties()).count(p -> p.isDirty()));
			}
			s1.endUpdating();

			assertEquals(6, CCLog.getChangeCount());
			assertArrayEquals(new String[]{"FSK", "Score", "Tags"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

			assertFalse(s1.isDirty());
			assertFalse(s1.Title.isDirty());
		}

		// CCSeason
		{
			var s1 = ml.iteratorSeasons().get(5);

			s1.Title.set("New");
			assertFalse(s1.isDirty());
			assertFalse(s1.Title.isDirty());

			assertEquals(7, CCLog.getChangeCount());
			assertArrayEquals(new String[]{"Title"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

			s1.beginUpdating();
			{
				s1.Year.set(1980);
				assertTrue(s1.isDirty());
				assertTrue(s1.Year.isDirty());
			}
			s1.endUpdating();

			assertEquals(8, CCLog.getChangeCount());
			assertArrayEquals(new String[]{"Year"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

			s1.beginUpdating();
			{
				s1.Title.set("New");
				assertTrue(s1.isDirty());
				assertTrue(s1.Title.isDirty());

				s1.Year.set(2222);
				assertTrue(s1.isDirty());
				assertTrue(s1.Year.isDirty());
			}
			s1.endUpdating();

			assertEquals(9, CCLog.getChangeCount());
			assertArrayEquals(new String[]{"Title", "Year"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

			assertFalse(s1.isDirty());
			assertFalse(s1.Title.isDirty());
		}

		// CCEpisode
		{
			var e1 = ml.iteratorEpisodes().get(60);

			e1.Title.set("New");
			assertFalse(e1.isDirty());
			assertFalse(e1.Title.isDirty());

			assertEquals(10, CCLog.getChangeCount());
			assertArrayEquals(new String[]{"Title"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

			e1.beginUpdating();
			{
				e1.EpisodeNumber.set(12);
				assertTrue(e1.isDirty());
				assertTrue(e1.EpisodeNumber.isDirty());
			}
			e1.endUpdating();

			assertEquals(11, CCLog.getChangeCount());
			assertArrayEquals(new String[]{"EpisodeNumber"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

			e1.beginUpdating();
			{
				e1.Title.set("New");
				assertTrue(e1.isDirty());
				assertTrue(e1.Title.isDirty());

				e1.MediaInfo.AudioChannels.set((short)3);
				assertTrue(e1.isDirty());
				assertTrue(e1.MediaInfo.AudioChannels.isDirty());
			}
			e1.endUpdating();

			assertEquals(12, CCLog.getChangeCount());
			assertArrayEquals(new String[]{"Title", "MediaInfo.AudioChannels"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

			assertFalse(e1.isDirty());
		}

		{
			var s1 = ml.iteratorSeasons().get(4);

			var e1 = s1.iteratorEpisodes().get(3);

			e1.delete();

			assertEquals(13, CCLog.getChangeCount());
			assertArrayEquals(new String[]{"@EPISODES"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);
		}

		{
			var s1 = ml.iteratorSeries().get(1);

			var ss1 = s1.iteratorSeasons().get(0);

			ss1.delete();

			assertEquals(27, CCLog.getChangeCount());
			assertArrayEquals(new String[]{"@SEASONS"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);
		}

	}
}
