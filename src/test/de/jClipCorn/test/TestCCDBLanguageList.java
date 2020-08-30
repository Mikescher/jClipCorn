package de.jClipCorn.test;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("nls")
public class TestCCDBLanguageList extends ClipCornBaseTest {

	@Test
	public void testCommon() {
		assertEquals(CCDBLanguageList.EMPTY, CCDBLanguageList.EMPTY);
	}

	@Test
	public void testSubset() {
		assertEquals(CCDBLanguageList.EMPTY.allSubsets().size(), 1);
		assertEquals(CCDBLanguageList.GERMAN.allSubsets().size(), 2);
		assertEquals(CCDBLanguageList.ENGLISH.allSubsets().size(), 2);
		assertEquals(CCDBLanguageList.create(CCDBLanguage.GERMAN, CCDBLanguage.ENGLISH).size(), 4);
		assertEquals(CCDBLanguageList.create(CCDBLanguage.GERMAN, CCDBLanguage.ENGLISH, CCDBLanguage.SPANISH).size(), 8);

		assertTrue(CCDBLanguageList.EMPTY.allSubsets().contains(CCDBLanguageList.EMPTY));

		assertTrue(CCDBLanguageList.GERMAN.allSubsets().contains(CCDBLanguageList.EMPTY));
		assertTrue(CCDBLanguageList.GERMAN.allSubsets().contains(CCDBLanguageList.GERMAN));

		assertTrue(CCDBLanguageList.create(CCDBLanguage.GERMAN, CCDBLanguage.ENGLISH).allSubsets().contains(CCDBLanguageList.EMPTY));
		assertTrue(CCDBLanguageList.create(CCDBLanguage.GERMAN, CCDBLanguage.ENGLISH).allSubsets().contains(CCDBLanguageList.GERMAN));
		assertTrue(CCDBLanguageList.create(CCDBLanguage.GERMAN, CCDBLanguage.ENGLISH).allSubsets().contains(CCDBLanguageList.ENGLISH));
		assertTrue(CCDBLanguageList.create(CCDBLanguage.GERMAN, CCDBLanguage.ENGLISH).allSubsets().contains(CCDBLanguageList.create(CCDBLanguage.GERMAN, CCDBLanguage.ENGLISH)));
	}
}
