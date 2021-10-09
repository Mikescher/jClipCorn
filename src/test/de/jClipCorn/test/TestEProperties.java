package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.stream.CCStreams;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class TestEProperties extends ClipCornBaseTest {

	@Test
	@SuppressWarnings("HardCodedStringLiteral")
	@Parameters({ "false", "true" })
	public void testDirtyBase(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);

		for (var e : ml.iteratorStructureElements()) {
			assertFalse(e.getQualifiedTitle() + " -> isDirty", e.isDirty());

			for (var p : e.getProperties()) {
				assertFalse(e.getQualifiedTitle() + " -> " + p.getName() + " -> isDirty", p.isDirty());
			}
		}

		assertEquals(0, CCLog.getChangeCount());
	}

	@Test
	@SuppressWarnings("HardCodedStringLiteral")
	@Parameters({ "false", "true" })
	public void testDirtyCCMovie(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);

		var m1 = ml.iteratorMovies().get(1);

		m1.Title.set("New1");
		assertFalse(m1.isDirty());
		assertFalse(m1.Title.isDirty());

		assertEquals(1, CCLog.getChangeCount());
		assertArrayEquals(new String[]{"Title"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

		m1.beginUpdating();
		{
			m1.OnlineScore.set(CCOnlineScore.create((short)1, (short)10));
			assertTrue(m1.isDirty());
			assertTrue(m1.OnlineScore.isDirty());
		}
		m1.endUpdating();

		assertEquals(2, CCLog.getChangeCount());
		assertArrayEquals(new String[]{"OnlineScore.Numerator"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

		m1.beginUpdating();
		{
			m1.Title.set("New2");
			assertTrue(m1.isDirty());
			assertTrue(m1.Title.isDirty());

			m1.Zyklus.Title.set("New3");
			assertTrue(m1.isDirty());
			assertTrue(m1.Zyklus.Title.isDirty());
		}
		m1.endUpdating();

		assertEquals(3, CCLog.getChangeCount());
		assertArrayEquals(new String[]{"Title", "Zyklus.Title"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

		assertFalse(m1.isDirty());
		assertFalse(m1.Title.isDirty());
	}

	@Test
	@SuppressWarnings("HardCodedStringLiteral")
	@Parameters({ "false", "true" })
	public void testDirtyCCSeries(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);

		var s1 = ml.iteratorSeries().get(1);

		s1.Title.set("New4");
		assertFalse(s1.isDirty());
		assertFalse(s1.Title.isDirty());

		assertEquals(1, CCLog.getChangeCount());
		assertArrayEquals(new String[]{"Title"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

		s1.beginUpdating();
		{
			s1.Title.set("New5");
			assertTrue(s1.isDirty());
			assertTrue(s1.Title.isDirty());
		}
		s1.endUpdating();

		assertEquals(2, CCLog.getChangeCount());
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

			s1.Tags.set(CCTagList.create(CCSingleTag.TAG_FILE_CORRUPTED));
			assertTrue(s1.isDirty());
			assertTrue(s1.Tags.isDirty());
			assertEquals(3, CCStreams.iterate(s1.getProperties()).count(p -> p.isDirty()));
		}
		s1.endUpdating();

		assertEquals(3, CCLog.getChangeCount());
		assertArrayEquals(new String[]{"FSK", "Score", "Tags"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

		assertFalse(s1.isDirty());
		assertFalse(s1.Title.isDirty());
	}

	@Test
	@SuppressWarnings("HardCodedStringLiteral")
	@Parameters({ "false", "true" })
	public void testDirtyCCSeason(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);

		var s1 = ml.iteratorSeasons().get(5);

		s1.Title.set("New6");
		assertFalse(s1.isDirty());
		assertFalse(s1.Title.isDirty());

		assertEquals(1, CCLog.getChangeCount());
		assertArrayEquals(new String[]{"Title"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

		s1.beginUpdating();
		{
			s1.Year.set(1980);
			assertTrue(s1.isDirty());
			assertTrue(s1.Year.isDirty());
		}
		s1.endUpdating();

		assertEquals(2, CCLog.getChangeCount());
		assertArrayEquals(new String[]{"Year"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

		s1.beginUpdating();
		{
			s1.Title.set("New7");
			assertTrue(s1.isDirty());
			assertTrue(s1.Title.isDirty());

			s1.Year.set(2222);
			assertTrue(s1.isDirty());
			assertTrue(s1.Year.isDirty());
		}
		s1.endUpdating();

		assertEquals(3, CCLog.getChangeCount());
		assertArrayEquals(new String[]{"Title", "Year"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

		assertFalse(s1.isDirty());
		assertFalse(s1.Title.isDirty());
	}

	@Test
	@SuppressWarnings("HardCodedStringLiteral")
	@Parameters({ "false", "true" })
	public void testDirtyCCEpisode(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);

		var e1 = ml.iteratorEpisodes().get(60);

		e1.Title.set("New8");
		assertFalse(e1.isDirty());
		assertFalse(e1.Title.isDirty());

		assertEquals(1, CCLog.getChangeCount());
		assertArrayEquals(new String[]{"Title"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

		e1.beginUpdating();
		{
			e1.EpisodeNumber.set(12);
			assertTrue(e1.isDirty());
			assertTrue(e1.EpisodeNumber.isDirty());
		}
		e1.endUpdating();

		assertEquals(2, CCLog.getChangeCount());
		assertArrayEquals(new String[]{"EpisodeNumber"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

		e1.beginUpdating();
		{
			e1.Title.set("New9");
			assertTrue(e1.isDirty());
			assertTrue(e1.Title.isDirty());

			e1.MediaInfo.AudioChannels.set(Opt.of((short)3));
			assertTrue(e1.isDirty());
			assertTrue(e1.MediaInfo.AudioChannels.isDirty());
		}
		e1.endUpdating();

		assertEquals(3, CCLog.getChangeCount());
		assertArrayEquals(new String[]{"Title", "MediaInfo.AudioChannels"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);

		assertFalse(e1.isDirty());
	}

	@Test
	@SuppressWarnings("HardCodedStringLiteral")
	@Parameters({ "false", "true" })
	public void testDirtyDeleteCCEpisode(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);

		var s1 = ml.iteratorSeasons().get(4);

		var e1 = s1.iteratorEpisodes().get(3);

		e1.delete();

		assertEquals(1, CCLog.getChangeCount());
		assertArrayEquals(new String[]{"@EPISODES"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);
	}

	@Test
	@SuppressWarnings("HardCodedStringLiteral")
	@Parameters({ "false", "true" })
	public void testDirtyDeleteCCSeason(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);

		assertEquals(0, CCLog.getChangeCount());

		var s1 = ml.iteratorSeries().get(1);

		var ss1 = s1.iteratorSeasons().get(0);

		ss1.delete();

		assertEquals(14, CCLog.getChangeCount());
		assertArrayEquals(new String[]{"@SEASONS"}, CCStreams.iterate(CCLog.getChangeElements()).lastOrNull().Properties);
	}

	@Test
	@SuppressWarnings("HardCodedStringLiteral")
	@Parameters({ "false", "true" })
	public void testDirtyUnchangedValue(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);

		var s1 = ml.iteratorMovies().get(8);

		s1.Title.set("VALUE_001");
		assertFalse(s1.isDirty());
		assertFalse(s1.Title.isDirty());

		s1.beginUpdating();

		s1.Title.set("VALUE_001");
		assertFalse(s1.isDirty());
		assertFalse(s1.Title.isDirty());

		s1.endUpdating();
	}
}
