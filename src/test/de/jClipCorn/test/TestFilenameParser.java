package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.parser.FilenameParser;
import de.jClipCorn.util.parser.FilenameParserResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("nls")
public class TestFilenameParser extends ClipCornBaseTest {

	@Test
	public void testFilenameParser() {
		CCMovieList ml = createEmptyDB();
		
		{
			FilenameParserResult r = FilenameParser.parse(ml, FSPath.create(FilesystemUtils.combineWithFSPathSeparator("F:", "Filme", "Captain America I - The First Avenger [[MCU]] [GER].avi")));
			
			assertEquals(CCFileFormat.AVI, r.Format);
			assertEquals(1, r.Groups.count());
			assertEquals("MCU", r.Groups.get(0).Name);
			assertEquals(CCDBLanguageList.GERMAN, r.Language);
			assertEquals("The First Avenger", r.Title);
			assertEquals("Captain America", r.Zyklus.getTitle());
			assertEquals(1, r.Zyklus.getNumber());
			assertEquals(0, r.AdditionalFiles.size());
		}

		{
			FilenameParserResult r = FilenameParser.parse(ml, FSPath.create(FilesystemUtils.combineWithFSPathSeparator("F:", "Filme", "Inglourious Basterds [JAP].flv")));

			assertEquals(CCFileFormat.FLV, r.Format);
			assertEquals(0, r.Groups.count());
			assertEquals(CCDBLanguageList.JAPANESE, r.Language);
			assertEquals("Inglourious Basterds", r.Title);
			assertEquals(true, r.Zyklus.isEmpty());
			assertEquals(-1, r.Zyklus.getNumber());
			assertEquals(0, r.AdditionalFiles.size());
		}

		{
			FilenameParserResult r = FilenameParser.parse(ml, FSPath.create(FilesystemUtils.combineWithFSPathSeparator("F:", "Filme", "Inglourious Basterds [GER+FR].flv")));

			assertEquals(CCFileFormat.FLV, r.Format);
			assertEquals(0, r.Groups.count());
			assertEquals(CCDBLanguageList.create(CCDBLanguage.GERMAN, CCDBLanguage.FRENCH), r.Language);
			assertEquals("Inglourious Basterds", r.Title);
			assertEquals(true, r.Zyklus.isEmpty());
			assertEquals(-1, r.Zyklus.getNumber());
			assertEquals(0, r.AdditionalFiles.size());
		}
	}

}
