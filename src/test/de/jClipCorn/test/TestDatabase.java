package de.jClipCorn.test;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.database.driver.DatabaseStructure;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.XMLFormatException;
import de.jClipCorn.util.helper.SimpleFileUtils;
import de.jClipCorn.util.parser.TurbineParser;
import de.jClipCorn.util.sqlwrapper.CCSQLKVKey;
import de.jClipCorn.util.sqlwrapper.CCSQLTableDef;
import de.jClipCorn.util.stream.CCStreams;
import org.junit.Test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.datetime.CCDate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public class TestDatabase extends ClipCornBaseTest {

	@Test
	public void testAddMovie() throws Exception {
		CCMovieList ml = createEmptyDB();
		
		assertEquals(0, ml.getElementCount());
		
		CCMovie movWrite = ml.createNewEmptyMovie();

		movWrite.beginUpdating();
		movWrite.setTitle("Title");
		movWrite.setAddDate(CCDate.getCurrentDate());
		movWrite.setFormat(CCFileFormat.MKV);
		movWrite.setZyklusID(4);
		movWrite.setZyklusTitle("Zyklus");
		movWrite.setFilesize(1024);
		movWrite.setFsk(CCFSK.RATING_III);
		movWrite.setGenre(CCGenre.GENRE_002, 0);
		movWrite.setGenre(CCGenre.GENRE_004, 1);
		movWrite.setGenre(CCGenre.GENRE_006, 2);
		movWrite.setLanguage(CCDBLanguageList.ENGLISH);
		movWrite.setLength(120);
		movWrite.setOnlinescore(CCOnlineScore.STARS_3_0);
		movWrite.setViewed(true);
		movWrite.setYear(2012);
		movWrite.setScore(CCUserScore.RATING_III);
		movWrite.setOnlineReference("tmdb:movie/207703");
		movWrite.setMediaInfo(new CCMediaInfo(1565454159, 1565454169, 1570732032, 5903.904, 2128398, "MPEG-4 Visual", 720, 304, 23.976, (short)8, 141552, "XVID", "AC-3", (short)6, "2000", 48000));
		movWrite.setPart(0, "C:\\test.mov");
		movWrite.endUpdating();
		
		assertEquals(1, ml.getElementCount());

		ml.forceReconnectAndReloadForTests();

		assertEquals(1, ml.getElementCount());

		CCMovie movRead = ml.iteratorMovies().firstOrNull();

		assertEquals("Title", movRead.getTitle());
		assertEquals(CCDBElementTyp.MOVIE, movRead.getType());
		assertEquals(movWrite.getAddDate(), movRead.getAddDate());
		assertEquals(CCFileFormat.MKV, movRead.getFormat());
		assertEquals("Zyklus IV", movRead.getZyklus().getFormatted());
		assertEquals("Zyklus IV - Title", movRead.getCompleteTitle());
		assertEquals(1024, movRead.getFilesize().getBytes());
		assertEquals(CCDBLanguageList.ENGLISH, movRead.getLanguage());
		assertEquals(120, movRead.getLength());
		assertEquals(2012, movRead.getYear());
		assertEquals(CCUserScore.RATING_III, movRead.getScore());
		assertEquals(CCFSK.RATING_III, movRead.getFSK());
		assertEquals(CCOnlineRefType.THEMOVIEDB, movRead.getOnlineReference().Main.type);
		assertEquals(CCOnlineScore.STARS_3_0, movRead.getOnlinescore());
		assertEquals("C:\\test.mov", movRead.getPart(0));

		assertEquals(1565454159, movRead.getMediaInfo().getCDate());
		assertEquals(1565454169, movRead.getMediaInfo().getMDate());
		assertEquals(1570732032, movRead.getMediaInfo().getFilesize());
		assertEquals(5903.904, movRead.getMediaInfo().getDuration(), 0.000001);
		assertEquals(2128398, movRead.getMediaInfo().getBitrate());
		assertEquals("MPEG-4 Visual", movRead.getMediaInfo().getVideoFormat());
		assertEquals(720, movRead.getMediaInfo().getWidth());
		assertEquals(304, movRead.getMediaInfo().getHeight());
		assertEquals(23.976, movRead.getMediaInfo().getFramerate(), 0.000001);
		assertEquals(8, movRead.getMediaInfo().getBitdepth());
		assertEquals(141552, movRead.getMediaInfo().getFramecount());
		assertEquals("XVID", movRead.getMediaInfo().getVideoCodec());
		assertEquals("AC-3", movRead.getMediaInfo().getAudioFormat());
		assertEquals(6, movRead.getMediaInfo().getAudioChannels());
		assertEquals("2000", movRead.getMediaInfo().getAudioCodec());
		assertEquals(48000, movRead.getMediaInfo().getAudioSamplerate());
	}

	@Test
	public void testAddSeries() {
		CCMovieList ml = createEmptyDB();

		assertEquals(0, ml.getElementCount());

		CCSeries serWrite = ml.createNewEmptySeries();
		{
			serWrite.setScore(CCUserScore.RATING_V);
			serWrite.setGroups(CCGroupList.create(CCGroup.create("G0"), CCGroup.create("G1")));
			serWrite.setTitle("MySeries");
			serWrite.setGenres(CCGenreList.create(CCGenre.GENRE_006, CCGenre.GENRE_020, CCGenre.GENRE_006));
			serWrite.setTags(CCTagList.create(CCTagList.TAG_WATCH_LATER, CCTagList.TAG_BAD_QUALITY));
			serWrite.setOnlineReference(CCOnlineReferenceList.create(CCSingleOnlineReference.createIMDB("1234"), CCSingleOnlineReference.createMyAnimeList(999)));
			serWrite.setFsk(CCFSK.RATING_I);
			serWrite.setOnlinescore(CCOnlineScore.STARS_2_0);
		}

		CCSeason seaWrite = serWrite.createNewEmptySeason();
		{
			seaWrite.setTitle("MySeason ~~~");
			seaWrite.setYear(2020);
		}

		CCEpisode epiWrite = seaWrite.createNewEmptyEpisode();
		{
			epiWrite.setEpisodeNumber(1);
			epiWrite.setTitle("This is my title: fight me");
			epiWrite.setViewed(true);
			epiWrite.setMediaInfo(new CCMediaInfo(
					CCDateTime.create(15, 4, 2019, 6,  0, 0).toFileTimestamp(GMT_2), // long cdate
					CCDateTime.create(15, 4, 2019, 8, 30, 0).toFileTimestamp(GMT_2), // long mdate
					3570481288L,                                                     // long filesize
					5932.933,                                                        // double duration
					4814457,                                                         // int bitrate
					"AVC",                                                           // String videoformat
					1920,                                                            // int width
					1080,                                                            // int height
					23.976,                                                          // double framerate
					(short)8,                                                        // short bitdepth
					142248,                                                          // int framecount
					"V_MPEG4/ISO/AVC",                                               // String videocodec
					"AC-3",                                                          // String audioformat
					(short)2,                                                        // short audiochannels
					"A_AC3",                                                         // String audiocodec
					48000));                                                         // int audiosamplerate
			epiWrite.setLength(98);
			epiWrite.setTags(CCTagList.create(CCTagList.TAG_MISSING_TIME));
			epiWrite.setFormat(CCFileFormat.MKV);
			epiWrite.setFilesize(3570481288L);
			epiWrite.setPart("/media/example.mkv");
			epiWrite.setAddDate(CCDate.create(1, 1, 2000));
			epiWrite.setViewedHistory(CCDateTimeList.create(CCDateTime.create(10, 1, 2000, 6, 0, 0), CCDateTime.create(12, 1, 2000, 12, 0, 0), CCDateTime.create(22, 1, 2000, 18, 0, 0)));
			epiWrite.setLanguage(CCDBLanguageList.create(CCDBLanguage.GERMAN, CCDBLanguage.RUSSIAN, CCDBLanguage.SPANISH));
		}

		assertEquals(1, ml.getElementCount());
		assertEquals(0, ml.iteratorMovies().count());
		assertEquals(1, ml.iteratorSeries().count());
		assertEquals(1, ml.iteratorSeasons().count());
		assertEquals(1, ml.iteratorEpisodes().count());

		ml.forceReconnectAndReloadForTests();

		assertEquals(1, ml.getElementCount());
		assertEquals(0, ml.iteratorMovies().count());
		assertEquals(1, ml.iteratorSeries().count());
		assertEquals(1, ml.iteratorSeasons().count());
		assertEquals(1, ml.iteratorEpisodes().count());

		CCSeries serRead = ml.iteratorSeries().firstOrNull();

		assertEquals(CCUserScore.RATING_V, serRead.getScore());
		assertEquals(CCGroupList.create(CCGroup.create("G0"), CCGroup.create("G1")), serRead.getGroups());
		assertEquals("MySeries", serRead.getTitle());
		assertEquals(CCGenreList.create(CCGenre.GENRE_006, CCGenre.GENRE_020, CCGenre.GENRE_006), serRead.getGenres());
		assertEquals(CCTagList.create(CCTagList.TAG_WATCH_LATER, CCTagList.TAG_BAD_QUALITY), serRead.getTags());
		assertEquals(CCOnlineReferenceList.create(CCSingleOnlineReference.createIMDB("1234"), CCSingleOnlineReference.createMyAnimeList(999)), serRead.getOnlineReference());
		assertEquals(CCFSK.RATING_I, serRead.getFSK());
		assertEquals(CCOnlineScore.STARS_2_0, serRead.getOnlinescore());

		CCSeason seaRead = serRead.iteratorSeasons().firstOrNull();

		assertEquals("MySeason ~~~", seaRead.getTitle());
		assertEquals(2020, seaRead.getYear());

		CCEpisode epiRead = seaRead.iteratorEpisodes().firstOrNull();

		assertEquals(1, epiRead.getEpisodeNumber());
		assertEquals("This is my title: fight me", epiRead.getTitle());
		assertTrue(epiRead.isViewed());
		assertEquals(98, epiRead.getLength());
		assertEquals(CCTagList.create(CCTagList.TAG_MISSING_TIME), epiRead.getTags());
		assertEquals(CCFileFormat.MKV, epiRead.getFormat());
		assertEquals(3570481288L, epiRead.getFilesize().getBytes());
		assertEquals("/media/example.mkv", epiRead.getPart());
		assertEquals(CCDate.create(1, 1, 2000), epiRead.getAddDate());
		assertEquals(CCDateTimeList.create(CCDateTime.create(10, 1, 2000, 6, 0, 0), CCDateTime.create(12, 1, 2000, 12, 0, 0), CCDateTime.create(22, 1, 2000, 18, 0, 0)), epiRead.getViewedHistory());
		assertEquals(CCDBLanguageList.create(CCDBLanguage.GERMAN, CCDBLanguage.RUSSIAN, CCDBLanguage.SPANISH), epiRead.getLanguage());
		assertEquals(1555300800000L, epiRead.getMediaInfo().getCDate());
		assertEquals(1555309800000L, epiRead.getMediaInfo().getMDate());
		assertEquals(3570481288L, epiRead.getMediaInfo().getFilesize());
		assertEquals(5932.933, epiRead.getMediaInfo().getDuration(), 0.000001);
		assertEquals(4814457, epiRead.getMediaInfo().getBitrate());
		assertEquals("AVC", epiRead.getMediaInfo().getVideoFormat());
		assertEquals(1920, epiRead.getMediaInfo().getWidth());
		assertEquals(1080, epiRead.getMediaInfo().getHeight());
		assertEquals(23.976, epiRead.getMediaInfo().getFramerate(), 0.000001);
		assertEquals(8, epiRead.getMediaInfo().getBitdepth());
		assertEquals(142248, epiRead.getMediaInfo().getFramecount());
		assertEquals("V_MPEG4/ISO/AVC", epiRead.getMediaInfo().getVideoCodec());
		assertEquals("AC-3", epiRead.getMediaInfo().getAudioFormat());
		assertEquals(2, epiRead.getMediaInfo().getAudioChannels());
		assertEquals("A_AC3", epiRead.getMediaInfo().getAudioCodec());
		assertEquals(48000, epiRead.getMediaInfo().getAudioSamplerate());
	}

	@Test
	public void testImport() throws Exception {
		CCMovieList ml = createExampleDB();
		
		assertTrue(ml.hasElements());
		
		assertEquals(20, ml.getElementCount());
		assertEquals(86, ml.getEpisodeCount());
		assertEquals(2, ml.getGroupList().size());
		assertEquals(17, ml.getMovieCount());
		assertEquals(3, ml.getSeriesCount());
	}

	@Test
	public void testTurbine() throws IOException, XMLFormatException {
		List<CCSQLTableDef> code = Arrays.asList(DatabaseStructure.TABLES);
		List<CCSQLTableDef> xml  = new TurbineParser(SimpleFileUtils.readUTF8TextFile("/" + CCDatabase.XML_NAME)).convertToTableDefinition();

		assertEquals(code.size(), xml.size());

		for (CCSQLTableDef d1 : xml) {
			CCSQLTableDef other = CCStreams.iterate(code).singleOrNull(d2 -> d2.isEqual(d1));
			assertNotNull(other);
			code.remove(other);
		}

		assertEquals(0, code.size());
	}

	@Test
	public void testTrigger() throws SQLException {
		CCMovieList ml = createEmptyDB();

		assertTrue(ml.getHistory().testTrigger(false, new RefParam<>()));
		assertFalse(ml.getHistory().isHistoryActive());
		assertEquals(0, ml.getHistory().getCount());

		ml.getHistory().enableTrigger();

		assertTrue(ml.getHistory().testTrigger(true, new RefParam<>()));
		assertTrue(ml.getHistory().isHistoryActive());
		assertEquals(0, ml.getHistory().getCount());

		ml.getHistory().disableTrigger();

		assertTrue(ml.getHistory().testTrigger(false, new RefParam<>()));
		assertFalse(ml.getHistory().isHistoryActive());
		assertEquals(0, ml.getHistory().getCount());
	}

	@Test
	public void testInfoKeys() {
		CCMovieList ml = createEmptyDB();

		for (CCSQLKVKey ik : DatabaseStructure.INFOKEYS) {
			assertNotNull(ml.getDatabaseForUnitTests().readInformationFromDB(ik, null));
		}
	}
}
