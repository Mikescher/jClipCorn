package de.jClipCorn.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import de.jClipCorn.features.serialization.xmlimport.ImportOptions;
import org.junit.Test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBElementTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.helper.SimpleFileUtils;

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

		
		CCMovie movRead = ml.iteratorMovies().next();

		assertEquals("Title", movWrite.getTitle());
		assertEquals(CCDBElementTyp.MOVIE, movWrite.getType());
		assertEquals(movRead.getAddDate(), movWrite.getAddDate());
		assertEquals(CCFileFormat.MKV, movWrite.getFormat());
		assertEquals("Zyklus IV", movWrite.getZyklus().getFormatted());
		assertEquals("Zyklus IV - Title", movWrite.getCompleteTitle());
		assertEquals(1024, movWrite.getFilesize().getBytes());
		assertEquals(CCDBLanguageList.ENGLISH, movWrite.getLanguage());
		assertEquals(120, movWrite.getLength());
		assertEquals(2012, movWrite.getYear());
		assertEquals(CCUserScore.RATING_III, movWrite.getScore());
		assertEquals(CCFSK.RATING_III, movWrite.getFSK());
		assertEquals(CCOnlineRefType.THEMOVIEDB, movWrite.getOnlineReference().Main.type);
		assertEquals(CCOnlineScore.STARS_3_0, movWrite.getOnlinescore());
		assertEquals("C:\\test.mov", movWrite.getPart(0));

		assertEquals(1565454159, movWrite.getMediaInfo().getCDate());
		assertEquals(1565454169, movWrite.getMediaInfo().getMDate());
		assertEquals(1570732032, movWrite.getMediaInfo().getFilesize());
		assertEquals(5903.904, movWrite.getMediaInfo().getDuration(), 0.000001);
		assertEquals(2128398, movWrite.getMediaInfo().getBitrate());
		assertEquals("MPEG-4 Visual", movWrite.getMediaInfo().getVideoFormat());
		assertEquals(720, movWrite.getMediaInfo().getWidth());
		assertEquals(304, movWrite.getMediaInfo().getHeight());
		assertEquals(23.976, movWrite.getMediaInfo().getFramerate(), 0.000001);
		assertEquals(8, movWrite.getMediaInfo().getBitdepth());
		assertEquals(141552, movWrite.getMediaInfo().getFramecount());
		assertEquals("XVID", movWrite.getMediaInfo().getVideoCodec());
		assertEquals("AC-3", movWrite.getMediaInfo().getAudioFormat());
		assertEquals(6, movWrite.getMediaInfo().getAudioChannels());
		assertEquals("2000", movWrite.getMediaInfo().getAudioCodec());
		assertEquals(48000, movWrite.getMediaInfo().getAudioSamplerate());
	}

	@Test
	public void testAddSeries() throws Exception {
		//TODO (Ser + Seas + Epi)
	}

	@Test
	public void testParseJSCCImport() throws Exception {
		CCMovieList ml = createEmptyDB();

		String data = SimpleFileUtils.readTextResource("/example_single_01.jsccexport", getClass());

		ExportHelper.importSingleElement(ml, data, new ImportOptions(true, true, true, true, false));

		assertEquals(1, ml.getElementCount());
		CCMovie mov = ml.iteratorMovies().next();

		assertEquals("Älter. Härter. Besser.", mov.getTitle());
		assertEquals(CCDBElementTyp.MOVIE, mov.getType());
		assertEquals(CCFileFormat.AVI, mov.getFormat());
		assertEquals("R.E.D. I - Älter. Härter. Besser.", mov.getCompleteTitle());
		assertEquals(714502144, mov.getFilesize().getBytes());
		assertEquals(CCDBLanguageList.GERMAN, mov.getLanguage());
		assertEquals(111, mov.getLength());
		assertEquals(2010, mov.getYear());
		assertEquals(CCUserScore.RATING_NO, mov.getScore());
		assertEquals(CCFSK.RATING_III, mov.getFSK());
		assertEquals(CCOnlineRefType.THEMOVIEDB, mov.getOnlineReference().Main.type);
		assertEquals("movie/39514", mov.getOnlineReference().Main.id);
		assertEquals(CCOnlineScore.STARS_3_5, mov.getOnlinescore());
		assertEquals("<?self>R.E.D. I - Älter. Härter. Besser..avi", mov.getPart(0));
	}

	@Test
	public void testParseJSCCImport2() throws Exception {
		CCMovieList ml = createEmptyDB();

		String data = SimpleFileUtils.readTextResource("/example_single_04.jsccexport", getClass());

		ExportHelper.importSingleElement(ml, data, new ImportOptions(true, true, true, true, false));

		assertEquals(1, ml.getElementCount());
		CCMovie mov = ml.iteratorMovies().next();

		assertEquals("Älter. Härter. Besser.", mov.getTitle());
		assertEquals(CCDBElementTyp.MOVIE, mov.getType());
		assertEquals(CCFileFormat.AVI, mov.getFormat());
		assertEquals("R.E.D. I - Älter. Härter. Besser.", mov.getCompleteTitle());
		assertEquals(714502144, mov.getFilesize().getBytes());
		assertEquals(CCDBLanguageList.GERMAN, mov.getLanguage());
		assertEquals(111, mov.getLength());
		assertEquals(2010, mov.getYear());
		assertEquals(CCUserScore.RATING_NO, mov.getScore());
		assertEquals(CCFSK.RATING_III, mov.getFSK());
		assertEquals(CCOnlineRefType.THEMOVIEDB, mov.getOnlineReference().Main.type);
		assertEquals("movie/39514", mov.getOnlineReference().Main.id);
		assertEquals(CCOnlineScore.STARS_3_5, mov.getOnlinescore());
		assertEquals("<?self>R.E.D. I - Älter. Härter. Besser..avi", mov.getPart(0));
	}

	@Test
	public void testJSCCRoundtrip_Movie() throws Exception {
		CCMovieList ml = createEmptyDB();

		String data = SimpleFileUtils.readTextResource("/example_single_01.jsccexport", getClass());

		ExportHelper.importSingleElement(ml, data, new ImportOptions(true, true, true, true, false));
		CCMovie mov = ml.iteratorMovies().firstOrNull();
		assertEquals(1, ml.getElementCount());

		File filep = new File(SimpleFileUtils.getSystemTempFile("xml"));
		ExportHelper.exportMovie(filep, ml, mov, true);

		ml.remove(mov);
		assertEquals(0, ml.getElementCount());

		String dataExpected = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importSingleElement(ml, dataExpected, new ImportOptions(true, true, true, true, false));
		mov = ml.iteratorMovies().firstOrNull();
		assertEquals(1, ml.getElementCount());

		ExportHelper.exportMovie(filep, ml, mov, true);

		ml.remove(ml.iteratorMovies().firstOrNull());
		assertEquals(0, ml.getElementCount());

		String dataActual = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importSingleElement(ml, dataExpected, new ImportOptions(true, true, true, true, false));
		mov = ml.iteratorMovies().firstOrNull();
		assertEquals(1, ml.getElementCount());

		ml.remove(ml.iteratorMovies().firstOrNull());
		assertEquals(0, ml.getElementCount());

		assertEquals(dataExpected, dataActual);

		filep.delete();
	}

	@Test
	public void testJSCCRoundtrip_Movie2() throws Exception {
		CCMovieList ml = createEmptyDB();

		String data = SimpleFileUtils.readTextResource("/example_single_03.jsccexport", getClass());

		ExportHelper.importSingleElement(ml, data, new ImportOptions(true, true, true, true, false));
		CCMovie mov = ml.iteratorMovies().firstOrNull();
		assertEquals(1, ml.getElementCount());

		File filep = new File(SimpleFileUtils.getSystemTempFile("xml"));
		ExportHelper.exportMovie(filep, ml, mov, true);

		ml.remove(mov);
		assertEquals(0, ml.getElementCount());

		String dataExpected = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importSingleElement(ml, dataExpected, new ImportOptions(true, true, true, true, false));
		mov = ml.iteratorMovies().firstOrNull();
		assertEquals(1, ml.getElementCount());

		ExportHelper.exportMovie(filep, ml, mov, true);

		ml.remove(ml.iteratorMovies().firstOrNull());
		assertEquals(0, ml.getElementCount());

		String dataActual = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importSingleElement(ml, dataExpected, new ImportOptions(true, true, true, true, false));
		mov = ml.iteratorMovies().firstOrNull();
		assertEquals(1, ml.getElementCount());

		ml.remove(ml.iteratorMovies().firstOrNull());
		assertEquals(0, ml.getElementCount());

		assertEquals(dataExpected, dataActual);

		filep.delete();
	}

	@Test
	public void testJSCCRoundtrip_Series() throws Exception {
		CCMovieList ml = createEmptyDB();
		
		String data = SimpleFileUtils.readTextResource("/example_single_02.jsccexport", getClass());
		
		ExportHelper.importSingleElement(ml, data, new ImportOptions(true, true, true, true, false));
		CCSeries ser = ml.iteratorSeries().firstOrNull();
		assertEquals(1, ml.getElementCount());

		File filep = new File(SimpleFileUtils.getSystemTempFile("xml"));
		ExportHelper.exportSeries(filep, ml, ser, true);
		
		ml.remove(ser);
		assertEquals(0, ml.getElementCount());

		String dataExpected = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importSingleElement(ml, dataExpected, new ImportOptions(true, true, true, true, false));
		ser = ml.iteratorSeries().firstOrNull();
		assertEquals(1, ml.getElementCount());

		ExportHelper.exportSeries(filep, ml, ser, true);
		
		ml.remove(ml.iteratorSeries().firstOrNull());
		assertEquals(0, ml.getElementCount());
		
		String dataActual = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importSingleElement(ml, dataExpected, new ImportOptions(true, true, true, true, false));
		ser = ml.iteratorSeries().firstOrNull();
		assertEquals(1, ml.getElementCount());
		
		ml.remove(ml.iteratorSeries().firstOrNull());
		assertEquals(0, ml.getElementCount());

		assertEquals(dataExpected, dataActual);
		
		filep.delete();
	}

	@Test
	public void testJSCCRoundtrip_Series2() throws Exception {
		CCMovieList ml = createEmptyDB();

		String data = SimpleFileUtils.readTextResource("/example_single_04.jsccexport", getClass());

		ExportHelper.importSingleElement(ml, data, new ImportOptions(true, true, true, true, false));
		CCSeries ser = ml.iteratorSeries().firstOrNull();
		assertEquals(1, ml.getElementCount());

		File filep = new File(SimpleFileUtils.getSystemTempFile("xml"));
		ExportHelper.exportSeries(filep, ml, ser, true);

		ml.remove(ser);
		assertEquals(0, ml.getElementCount());

		String dataExpected = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importSingleElement(ml, dataExpected, new ImportOptions(true, true, true, true, false));
		ser = ml.iteratorSeries().firstOrNull();
		assertEquals(1, ml.getElementCount());

		ExportHelper.exportSeries(filep, ml, ser, true);

		ml.remove(ml.iteratorSeries().firstOrNull());
		assertEquals(0, ml.getElementCount());

		String dataActual = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importSingleElement(ml, dataExpected, new ImportOptions(true, true, true, true, false));
		ser = ml.iteratorSeries().firstOrNull();
		assertEquals(1, ml.getElementCount());

		ml.remove(ml.iteratorSeries().firstOrNull());
		assertEquals(0, ml.getElementCount());

		assertEquals(dataExpected, dataActual);

		filep.delete();
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
}
