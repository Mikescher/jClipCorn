package de.jClipCorn.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieOnlineScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;
import de.jClipCorn.database.util.ExportHelper;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.helper.SimpleFileUtils;

@SuppressWarnings("nls")
public class TestDatabase {

	@Test
	public void testAddMovie() throws Exception {
		CCMovieList ml = ClipCornTestHelper.createEmptyDB();
		
		assertEquals(0, ml.getElementCount());
		
		CCMovie movWrite = ml.createNewEmptyMovie();

		movWrite.beginUpdating();
		movWrite.setTitle("Title");
		movWrite.setAddDate(CCDate.getCurrentDate());
		movWrite.setFormat(CCMovieFormat.MKV);
		movWrite.setZyklusID(4);
		movWrite.setZyklusTitle("Zyklus");
		movWrite.setFilesize(1024);
		movWrite.setFsk(CCMovieFSK.RATING_III);
		movWrite.setGenre(CCMovieGenre.GENRE_002, 0);
		movWrite.setGenre(CCMovieGenre.GENRE_004, 1);
		movWrite.setGenre(CCMovieGenre.GENRE_006, 2);
		movWrite.setLanguage(CCMovieLanguage.ENGLISH);
		movWrite.setLength(120);
		movWrite.setOnlinescore(CCMovieOnlineScore.STARS_3_0);
		movWrite.setViewed(true);
		movWrite.setYear(2012);
		movWrite.setScore(CCMovieScore.RATING_III);
		movWrite.setOnlineReference("tmdb:movie/207703");
		movWrite.setQuality(CCMovieQuality.BLURAY);
		movWrite.setPart(0, "C:\test.mov");
		movWrite.endUpdating();
		
		assertEquals(1, ml.getElementCount());

		
		CCMovie movRead = ml.iteratorMovies().next();

		assertEquals("Title", movWrite.getTitle());
		assertEquals(CCMovieTyp.MOVIE, movWrite.getType());
		assertEquals(movRead.getAddDate(), movWrite.getAddDate());
		assertEquals(CCMovieFormat.MKV, movWrite.getFormat());
		assertEquals("Zyklus IV", movWrite.getZyklus().getFormatted());
		assertEquals("Zyklus IV - Title", movWrite.getCompleteTitle());
		assertEquals(1024, movWrite.getFilesize().getBytes());
		assertEquals(CCMovieLanguage.ENGLISH, movWrite.getLanguage());
		assertEquals(120, movWrite.getLength());
		assertEquals(2012, movWrite.getYear());
		assertEquals(CCMovieScore.RATING_III, movWrite.getScore());
		assertEquals(CCMovieFSK.RATING_III, movWrite.getFSK());
		assertEquals(CCOnlineRefType.THEMOVIEDB, movWrite.getOnlineReference().type);
		assertEquals(CCMovieOnlineScore.STARS_3_0, movWrite.getOnlinescore());
		assertEquals("C:\test.mov", movWrite.getPart(0));
	}

	@Test
	public void testParseJSCCImport() throws Exception {
		CCMovieList ml = ClipCornTestHelper.createEmptyDB();
		
		String data = SimpleFileUtils.readTextResource("/example_single_01.jsccexport", getClass());
		
		ExportHelper.importSingleElement(ml, data, true, true, true, true);

		assertEquals(1, ml.getElementCount());
		CCMovie mov = ml.iteratorMovies().next();
		
		assertEquals("Älter. Härter. Besser.", mov.getTitle());
		assertEquals(CCMovieTyp.MOVIE, mov.getType());
		assertEquals(CCMovieFormat.AVI, mov.getFormat());
		assertEquals("R.E.D. I - Älter. Härter. Besser.", mov.getCompleteTitle());
		assertEquals(714502144, mov.getFilesize().getBytes());
		assertEquals(CCMovieLanguage.GERMAN, mov.getLanguage());
		assertEquals(111, mov.getLength());
		assertEquals(2010, mov.getYear());
		assertEquals(CCMovieScore.RATING_NO, mov.getScore());
		assertEquals(CCMovieFSK.RATING_III, mov.getFSK());
		assertEquals(CCOnlineRefType.THEMOVIEDB, mov.getOnlineReference().type);
		assertEquals("movie/39514", mov.getOnlineReference().id);
		assertEquals(CCMovieOnlineScore.STARS_3_5, mov.getOnlinescore());
		assertEquals("<?self>R.E.D. I - Älter. Härter. Besser..avi", mov.getPart(0));
	}

	@Test
	public void testJSCCRoundtrip() throws Exception {
		CCMovieList ml = ClipCornTestHelper.createEmptyDB();
		
		String data = SimpleFileUtils.readTextResource("/example_single_01.jsccexport", getClass());
		
		ExportHelper.importSingleElement(ml, data, true, true, true, true);

		assertEquals(1, ml.getElementCount());
		CCMovie mov = ml.iteratorMovies().next();
		
		ml.remove(mov);
		assertEquals(0, ml.getElementCount());
		
		File filep = new File(SimpleFileUtils.getTempFile("xml"));
		
		ExportHelper.exportMovie(filep, ml, mov, true);
		String dataExpected = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importSingleElement(ml, dataExpected, true, true, true, true);
		ml.remove(ml.iteratorMovies().next());
		
		ExportHelper.exportMovie(filep, ml, mov, true);
		String dataActual = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importSingleElement(ml, dataExpected, true, true, true, true);
		ml.remove(ml.iteratorMovies().next());

		assertEquals(dataExpected, dataActual);
		
		filep.delete();
	}

	@Test
	public void testImport() throws Exception {
		CCMovieList ml = ClipCornTestHelper.createExampleDB();
		
		assertTrue(ml.hasElements());
		
		assertEquals(18, ml.getElementCount());
		assertEquals(76, ml.getEpisodeCount());
		assertEquals(2, ml.getGroupList().size());
		assertEquals(16, ml.getMovieCount());
		assertEquals(2, ml.getSeriesCount());
	}
}
