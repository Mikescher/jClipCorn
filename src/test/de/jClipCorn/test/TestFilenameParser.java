package de.jClipCorn.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.parser.FilenameParser;
import de.jClipCorn.util.parser.FilenameParserResult;

@SuppressWarnings("nls")
public class TestFilenameParser extends ClipCornBaseTest {

	@Test
	public void testFilenameParser() throws Exception {
		CCMovieList ml = createEmptyDB();
		
		{
			FilenameParserResult r = FilenameParser.parse(ml, PathFormatter.combine("F:", "Filme", "Captain America I - The First Avenger [[MCU]] [GER].avi"));
			
			assertEquals(CCFileFormat.AVI, r.Format);
			assertEquals(1, r.Groups.count());
			assertEquals("MCU", r.Groups.get(0).Name);
			assertEquals(CCDBLanguage.GERMAN, r.Language);
			assertEquals("The First Avenger", r.Title);
			assertEquals("Captain America", r.Zyklus.getTitle());
			assertEquals(1, r.Zyklus.getNumber());
			assertEquals(0, r.AdditionalFiles.size());
		}
		
		{
			FilenameParserResult r = FilenameParser.parse(ml, PathFormatter.combine("F:", "Filme", "Inglourious Basterds [JAP].flv"));
			
			assertEquals(CCFileFormat.FLV, r.Format);
			assertEquals(0, r.Groups.count());
			assertEquals(CCDBLanguage.JAPANESE, r.Language);
			assertEquals("Inglourious Basterds", r.Title);
			assertEquals(true, r.Zyklus.isEmpty());
			assertEquals(-1, r.Zyklus.getNumber());
			assertEquals(0, r.AdditionalFiles.size());
		}
	}

}