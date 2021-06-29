package de.jClipCorn.test;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.database.driver.DatabaseStructure;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.XMLFormatException;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.parser.TurbineParser;
import de.jClipCorn.util.sqlwrapper.CCSQLKVKey;
import de.jClipCorn.util.sqlwrapper.CCSQLTableDef;
import de.jClipCorn.util.stream.CCStreams;
import org.jdom2.JDOMException;
import org.junit.Test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.datetime.CCDate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
		movWrite.Title.set("Title");
		movWrite.AddDate.set(CCDate.getCurrentDate());
		movWrite.Format.set(CCFileFormat.MKV);
		movWrite.Zyklus.setNumber(4);
		movWrite.Zyklus.setTitle("Zyklus");
		movWrite.FileSize.set(1024);
		movWrite.FSK.set(CCFSK.RATING_III);
		movWrite.Genres.set(CCGenre.GENRE_002, 0);
		movWrite.Genres.set(CCGenre.GENRE_004, 1);
		movWrite.Genres.set(CCGenre.GENRE_006, 2);
		movWrite.Language.set(CCDBLanguageList.ENGLISH);
		movWrite.Length.set(120);
		movWrite.OnlineScore.set(CCOnlineScore.STARS_3_0);
		movWrite.Year.set(2012);
		movWrite.Score.set(CCUserScore.RATING_III);
		movWrite.OnlineReference.set("tmdb:movie/207703");
		movWrite.MediaInfo.set(new CCMediaInfo(1565454159, 1565454169, 1570732032, 5903.904, 2128398, "MPEG-4 Visual", 720, 304, 23.976, (short)8, 141552, "XVID", "AC-3", (short)6, "2000", 48000, Str.Empty));
		movWrite.Parts.set(0, "C:\\test.mov");
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
		assertEquals(CCUserScore.RATING_III, movRead.Score.get());
		assertEquals(CCFSK.RATING_III, movRead.getFSK());
		assertEquals(CCOnlineRefType.THEMOVIEDB, movRead.getOnlineReference().Main.type);
		assertEquals(CCOnlineScore.STARS_3_0, movRead.getOnlinescore());
		assertEquals("C:\\test.mov", movRead.Parts.get(0));

		assertEquals(1565454159, movRead.mediaInfo().get().getCDate());
		assertEquals(1565454169, movRead.mediaInfo().get().getMDate());
		assertEquals(1570732032, movRead.mediaInfo().get().getFilesize());
		assertEquals(5903.904, movRead.mediaInfo().get().getDuration(), 0.000001);
		assertEquals(2128398, movRead.mediaInfo().get().getBitrate());
		assertEquals("MPEG-4 Visual", movRead.mediaInfo().get().getVideoFormat());
		assertEquals(720, movRead.mediaInfo().get().getWidth());
		assertEquals(304, movRead.mediaInfo().get().getHeight());
		assertEquals(23.976, movRead.mediaInfo().get().getFramerate(), 0.000001);
		assertEquals(8, movRead.mediaInfo().get().getBitdepth());
		assertEquals(141552, movRead.mediaInfo().get().getFramecount());
		assertEquals("XVID", movRead.mediaInfo().get().getVideoCodec());
		assertEquals("AC-3", movRead.mediaInfo().get().getAudioFormat());
		assertEquals(6, movRead.mediaInfo().get().getAudioChannels());
		assertEquals("2000", movRead.mediaInfo().get().getAudioCodec());
		assertEquals(48000, movRead.mediaInfo().get().getAudioSamplerate());
	}

	@Test
	public void testAddSeries() {
		CCMovieList ml = createEmptyDB();

		assertEquals(0, ml.getElementCount());

		CCSeries serWrite = ml.createNewEmptySeries();
		{
			serWrite.Score.set(CCUserScore.RATING_V);
			serWrite.Groups.set(CCGroupList.create(CCGroup.create("G0"), CCGroup.create("G1")));
			serWrite.Title.set("MySeries");
			serWrite.Genres.set(CCGenreList.create(CCGenre.GENRE_006, CCGenre.GENRE_020, CCGenre.GENRE_006));
			serWrite.Tags.set(CCTagList.create(CCSingleTag.TAG_WATCH_LATER, CCSingleTag.TAG_BAD_QUALITY));
			serWrite.OnlineReference.set(CCOnlineReferenceList.create(CCSingleOnlineReference.createIMDB("1234"), CCSingleOnlineReference.createMyAnimeList(999)));
			serWrite.FSK.set(CCFSK.RATING_I);
			serWrite.OnlineScore.set(CCOnlineScore.STARS_2_0);
		}

		CCSeason seaWrite = serWrite.createNewEmptySeason();
		{
			seaWrite.Title.set("MySeason ~~~");
			seaWrite.Year.set(2020);
		}

		CCEpisode epiWrite = seaWrite.createNewEmptyEpisode();
		{
			epiWrite.EpisodeNumber.set(1);
			epiWrite.Title.set("This is my title: fight me");
			epiWrite.MediaInfo.set(new CCMediaInfo(
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
					48000,                                                           // int audiosamplerate
					""));                                                            // String checksum
			epiWrite.Length.set(98);
			epiWrite.Tags.set(CCTagList.create(CCSingleTag.TAG_MISSING_TIME));
			epiWrite.Format.set(CCFileFormat.MKV);
			epiWrite.FileSize.set(3570481288L);
			epiWrite.Part.set("/media/example.mkv");
			epiWrite.AddDate.set(CCDate.create(1, 1, 2000));
			epiWrite.ViewedHistory.set(CCDateTimeList.create(CCDateTime.create(10, 1, 2000, 6, 0, 0), CCDateTime.create(12, 1, 2000, 12, 0, 0), CCDateTime.create(22, 1, 2000, 18, 0, 0)));
			epiWrite.Language.set(CCDBLanguageList.create(CCDBLanguage.GERMAN, CCDBLanguage.RUSSIAN, CCDBLanguage.SPANISH));
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

		assertEquals(CCUserScore.RATING_V, serRead.Score.get());
		assertEquals(CCGroupList.create(CCGroup.create("G0"), CCGroup.create("G1")), serRead.getGroups());
		assertEquals("MySeries", serRead.getTitle());
		assertEquals(CCGenreList.create(CCGenre.GENRE_006, CCGenre.GENRE_020, CCGenre.GENRE_006), serRead.getGenres());
		assertEquals(CCTagList.create(CCSingleTag.TAG_WATCH_LATER, CCSingleTag.TAG_BAD_QUALITY), serRead.getTags());
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
		assertEquals(CCTagList.create(CCSingleTag.TAG_MISSING_TIME), epiRead.getTags());
		assertEquals(CCFileFormat.MKV, epiRead.getFormat());
		assertEquals(3570481288L, epiRead.getFilesize().getBytes());
		assertEquals("/media/example.mkv", epiRead.getPart());
		assertEquals(CCDate.create(1, 1, 2000), epiRead.getAddDate());
		assertEquals(CCDateTimeList.create(CCDateTime.create(10, 1, 2000, 6, 0, 0), CCDateTime.create(12, 1, 2000, 12, 0, 0), CCDateTime.create(22, 1, 2000, 18, 0, 0)), epiRead.ViewedHistory.get());
		assertEquals(CCDBLanguageList.create(CCDBLanguage.GERMAN, CCDBLanguage.RUSSIAN, CCDBLanguage.SPANISH), epiRead.getLanguage());
		assertEquals(1555300800000L, epiRead.mediaInfo().get().getCDate());
		assertEquals(1555309800000L, epiRead.mediaInfo().get().getMDate());
		assertEquals(3570481288L, epiRead.mediaInfo().get().getFilesize());
		assertEquals(5932.933, epiRead.mediaInfo().get().getDuration(), 0.000001);
		assertEquals(4814457, epiRead.mediaInfo().get().getBitrate());
		assertEquals("AVC", epiRead.mediaInfo().get().getVideoFormat());
		assertEquals(1920, epiRead.mediaInfo().get().getWidth());
		assertEquals(1080, epiRead.mediaInfo().get().getHeight());
		assertEquals(23.976, epiRead.mediaInfo().get().getFramerate(), 0.000001);
		assertEquals(8, epiRead.mediaInfo().get().getBitdepth());
		assertEquals(142248, epiRead.mediaInfo().get().getFramecount());
		assertEquals("V_MPEG4/ISO/AVC", epiRead.mediaInfo().get().getVideoCodec());
		assertEquals("AC-3", epiRead.mediaInfo().get().getAudioFormat());
		assertEquals(2, epiRead.mediaInfo().get().getAudioChannels());
		assertEquals("A_AC3", epiRead.mediaInfo().get().getAudioCodec());
		assertEquals(48000, epiRead.mediaInfo().get().getAudioSamplerate());
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
	public void testTurbine() throws IOException, XMLFormatException, JDOMException {

		TurbineParser tp = new TurbineParser(SimpleFileUtils.readTextResource("/" + CCDatabase.XML_NAME, getClass()));
		tp.parse();
		List<CCSQLTableDef> xml  = tp.convertToTableDefinition();
		List<CCSQLTableDef> code = new ArrayList<>(Arrays.asList(DatabaseStructure.TABLES));

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
		assertEquals(1, ml.getHistory().getCount());

		ml.getHistory().disableTrigger();

		assertTrue(ml.getHistory().testTrigger(false, new RefParam<>()));
		assertFalse(ml.getHistory().isHistoryActive());
		assertEquals(2, ml.getHistory().getCount());
	}

	@Test
	public void testInfoKeys() {
		CCMovieList ml = createEmptyDB();

		for (CCSQLKVKey ik : DatabaseStructure.INFOKEYS) {
			assertNotNull(ml.getDatabaseForUnitTests().readInformationFromDB(ik, null));
		}
	}
}
