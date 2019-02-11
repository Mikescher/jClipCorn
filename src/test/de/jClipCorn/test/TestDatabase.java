package de.jClipCorn.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBElementTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;
import de.jClipCorn.database.util.ExportHelper;
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
		movWrite.setLanguage(CCDBLanguage.ENGLISH);
		movWrite.setLength(120);
		movWrite.setOnlinescore(CCOnlineScore.STARS_3_0);
		movWrite.setViewed(true);
		movWrite.setYear(2012);
		movWrite.setScore(CCUserScore.RATING_III);
		movWrite.setOnlineReference("tmdb:movie/207703");
		movWrite.setQuality(CCQuality.BLURAY);
		movWrite.setPart(0, "C:\test.mov");
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
		assertEquals(CCDBLanguage.ENGLISH, movWrite.getLanguage());
		assertEquals(120, movWrite.getLength());
		assertEquals(2012, movWrite.getYear());
		assertEquals(CCUserScore.RATING_III, movWrite.getScore());
		assertEquals(CCFSK.RATING_III, movWrite.getFSK());
		assertEquals(CCOnlineRefType.THEMOVIEDB, movWrite.getOnlineReference().Main.type);
		assertEquals(CCOnlineScore.STARS_3_0, movWrite.getOnlinescore());
		assertEquals("C:\test.mov", movWrite.getPart(0));
	}

	@Test
	public void testParseJSCCImport() throws Exception {
		CCMovieList ml = createEmptyDB();
		
		String data = SimpleFileUtils.readTextResource("/example_single_01.jsccexport", getClass());
		
		ExportHelper.importSingleElement(ml, data, true, true, true, true);

		assertEquals(1, ml.getElementCount());
		CCMovie mov = ml.iteratorMovies().next();
		
		assertEquals("Älter. Härter. Besser.", mov.getTitle());
		assertEquals(CCDBElementTyp.MOVIE, mov.getType());
		assertEquals(CCFileFormat.AVI, mov.getFormat());
		assertEquals("R.E.D. I - Älter. Härter. Besser.", mov.getCompleteTitle());
		assertEquals(714502144, mov.getFilesize().getBytes());
		assertEquals(CCDBLanguage.GERMAN, mov.getLanguage());
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
		
		ExportHelper.importSingleElement(ml, data, true, true, true, true);
		CCMovie mov = ml.iteratorMovies().firstOrNull();
		assertEquals(1, ml.getElementCount());

		File filep = new File(SimpleFileUtils.getTempFilename("xml"));
		ExportHelper.exportMovie(filep, ml, mov, true);
		
		ml.remove(mov);
		assertEquals(0, ml.getElementCount());

		String dataExpected = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importSingleElement(ml, dataExpected, true, true, true, true);
		mov = ml.iteratorMovies().firstOrNull();
		assertEquals(1, ml.getElementCount());

		ExportHelper.exportMovie(filep, ml, mov, true);
		
		ml.remove(ml.iteratorMovies().firstOrNull());
		assertEquals(0, ml.getElementCount());
		
		String dataActual = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importSingleElement(ml, dataExpected, true, true, true, true);
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
		
		ExportHelper.importSingleElement(ml, data, true, true, true, true);
		CCSeries ser = ml.iteratorSeries().firstOrNull();
		assertEquals(1, ml.getElementCount());

		File filep = new File(SimpleFileUtils.getTempFilename("xml"));
		ExportHelper.exportSeries(filep, ml, ser, true);
		
		ml.remove(ser);
		assertEquals(0, ml.getElementCount());

		String dataExpected = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importSingleElement(ml, dataExpected, true, true, true, true);
		ser = ml.iteratorSeries().firstOrNull();
		assertEquals(1, ml.getElementCount());

		ExportHelper.exportSeries(filep, ml, ser, true);
		
		ml.remove(ml.iteratorSeries().firstOrNull());
		assertEquals(0, ml.getElementCount());
		
		String dataActual = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importSingleElement(ml, dataExpected, true, true, true, true);
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
