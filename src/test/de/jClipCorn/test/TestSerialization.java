package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.features.serialization.xmlimport.ImportOptions;
import de.jClipCorn.util.helper.SimpleFileUtils;
import org.junit.Test;

import java.io.File;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

@SuppressWarnings({"nls", "ResultOfMethodCallIgnored"})
public class TestSerialization extends ClipCornBaseTest {

	@Test
	public void testParseJSCCImport() throws Exception {
		CCMovieList ml = createEmptyDB();

		String data = SimpleFileUtils.readTextResource("/example_single_01.jsccexport", getClass());

		ExportHelper.importElements(ml, data, new ImportOptions(true, true, true, true, false), -1);

		assertEquals(1, ml.getElementCount());
		CCMovie mov = ml.iteratorMovies().firstOrNull();

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

		String data = SimpleFileUtils.readTextResource("/example_single_03.jsccexport", getClass());

		ExportHelper.importElements(ml, data, new ImportOptions(true, true, true, true, false), -1);

		assertEquals(1, ml.getElementCount());
		CCMovie mov = ml.iteratorMovies().firstOrNull();

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

		ExportHelper.importElements(ml, data, new ImportOptions(true, true, true, true, false), -1);
		CCMovie mov = ml.iteratorMovies().firstOrNull();
		assertEquals(1, ml.getElementCount());

		File filep = new File(SimpleFileUtils.getSystemTempFile("jsccexport"));
		ExportHelper.exportMovie(filep, mov, true, false);

		ml.remove(mov);
		assertEquals(0, ml.getElementCount());

		String dataExpected = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importElements(ml, dataExpected, new ImportOptions(true, true, true, true, false), -1);
		mov = ml.iteratorMovies().firstOrNull();
		assertEquals(1, ml.getElementCount());

		ExportHelper.exportMovie(filep, mov, true, false);

		ml.remove(ml.iteratorMovies().firstOrNull());
		assertEquals(0, ml.getElementCount());

		String dataActual = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importElements(ml, dataExpected, new ImportOptions(true, true, true, true, false), -1);
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

		ExportHelper.importElements(ml, data, new ImportOptions(true, true, true, true, false), -1);
		CCMovie mov = ml.iteratorMovies().firstOrNull();
		assertEquals(1, ml.getElementCount());

		File filep = new File(SimpleFileUtils.getSystemTempFile("jsccexport"));
		ExportHelper.exportMovie(filep, mov, true, false);

		ml.remove(mov);
		assertEquals(0, ml.getElementCount());

		String dataExpected = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importElements(ml, dataExpected, new ImportOptions(true, true, true, true, false), -1);
		mov = ml.iteratorMovies().firstOrNull();
		assertEquals(1, ml.getElementCount());

		ExportHelper.exportMovie(filep, mov, true, false);

		ml.remove(ml.iteratorMovies().firstOrNull());
		assertEquals(0, ml.getElementCount());

		String dataActual = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importElements(ml, dataExpected, new ImportOptions(true, true, true, true, false), -1);
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
		
		ExportHelper.importElements(ml, data, new ImportOptions(true, true, true, true, false), -1);
		CCSeries ser = ml.iteratorSeries().firstOrNull();
		assertEquals(1, ml.getElementCount());

		File filep = new File(SimpleFileUtils.getSystemTempFile("jsccexport"));
		ExportHelper.exportSeries(filep, ser, true, false);
		
		ml.remove(ser);
		assertEquals(0, ml.getElementCount());

		String dataExpected = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importElements(ml, dataExpected, new ImportOptions(true, true, true, true, false), -1);
		ser = ml.iteratorSeries().firstOrNull();
		assertEquals(1, ml.getElementCount());

		ExportHelper.exportSeries(filep, ser, true, false);
		
		ml.remove(ml.iteratorSeries().firstOrNull());
		assertEquals(0, ml.getElementCount());
		
		String dataActual = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importElements(ml, dataExpected, new ImportOptions(true, true, true, true, false), -1);
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

		ExportHelper.importElements(ml, data, new ImportOptions(true, true, true, true, false), -1);
		CCSeries ser = ml.iteratorSeries().firstOrNull();
		assertEquals(1, ml.getElementCount());

		File filep = new File(SimpleFileUtils.getSystemTempFile("jsccexport"));
		ExportHelper.exportSeries(filep, ser, true, false);

		ml.remove(ser);
		assertEquals(0, ml.getElementCount());

		String dataExpected = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importElements(ml, dataExpected, new ImportOptions(true, true, true, true, false), -1);
		ser = ml.iteratorSeries().firstOrNull();
		assertEquals(1, ml.getElementCount());

		ExportHelper.exportSeries(filep, ser, true, false);

		ml.remove(ml.iteratorSeries().firstOrNull());
		assertEquals(0, ml.getElementCount());

		String dataActual = SimpleFileUtils.readUTF8TextFile(filep);
		ExportHelper.importElements(ml, dataExpected, new ImportOptions(true, true, true, true, false), -1);
		ser = ml.iteratorSeries().firstOrNull();
		assertEquals(1, ml.getElementCount());

		ml.remove(ml.iteratorSeries().firstOrNull());
		assertEquals(0, ml.getElementCount());

		assertEquals(dataExpected, dataActual);

		filep.delete();
	}

	@Test
	public void testMultiRoundtrip1() throws Exception {
		CCMovieList ml = createExampleDB();

		for(CCDatabaseElement e : ml.iteratorElements()) {
			File tmpfile1 = new File(SimpleFileUtils.getSystemTempFile("jmccexport"));
			tmpfile1.deleteOnExit();
			ExportHelper.exportDBElements(tmpfile1, Collections.singletonList(e), true, false);
			String data1 = SimpleFileUtils.readUTF8TextFile(tmpfile1);
			tmpfile1.delete();

			CCMovieList ml2 = createEmptyDB();
			ExportHelper.importElements(ml2, data1, new ImportOptions(false, false, false, false, false), -1);

			File tmpfile2 = new File(SimpleFileUtils.getSystemTempFile("jmccexport"));
			tmpfile2.deleteOnExit();
			ExportHelper.exportDBElements(tmpfile2, Collections.singletonList(ml2.iteratorElements().singleOrNull()), true, false);
			String data2 = SimpleFileUtils.readUTF8TextFile(tmpfile2);
			tmpfile2.delete();

			assertEquals(data1, data2);
		}
	}

	@Test
	public void testMultiRoundtrip2() throws Exception {
		CCMovieList ml = createExampleDB();

		File tmpfile1 = new File(SimpleFileUtils.getSystemTempFile("jmccexport"));
		tmpfile1.deleteOnExit();
		ExportHelper.exportDBElements(tmpfile1, ml.iteratorElements().enumerate(), true, false);
		String data1 = SimpleFileUtils.readUTF8TextFile(tmpfile1);
		tmpfile1.delete();

		CCMovieList ml2 = createEmptyDB();
		ExportHelper.importElements(ml2, data1, new ImportOptions(false, false, false, false, false), -1);

		File tmpfile2 = new File(SimpleFileUtils.getSystemTempFile("jmccexport"));
		tmpfile2.deleteOnExit();
		ExportHelper.exportDBElements(tmpfile2, ml2.iteratorElements().enumerate(), true, false);
		String data2 = SimpleFileUtils.readUTF8TextFile(tmpfile2);
		tmpfile2.delete();

		assertEquals(data1, data2);
	}

}
