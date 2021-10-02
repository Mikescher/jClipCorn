package de.jClipCorn.test;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageSet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("nls")
public class TestCCDBLanguageSet extends ClipCornBaseTest {

	@Test
	public void testCommon() {
		assertEquals(CCDBLanguageSet.EMPTY, CCDBLanguageSet.EMPTY);
	}

	@Test
	public void testSubset() {
		assertEquals(1, CCDBLanguageSet.EMPTY.allSubsets().size());
		assertEquals(2, CCDBLanguageSet.GERMAN.allSubsets().size());
		assertEquals(2, CCDBLanguageSet.ENGLISH.allSubsets().size());
		assertEquals(4, CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.ENGLISH).allSubsets().size());
		assertEquals(8, CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.ENGLISH, CCDBLanguage.SPANISH).allSubsets().size());

		assertTrue(CCDBLanguageSet.EMPTY.allSubsets().contains(CCDBLanguageSet.EMPTY));

		assertTrue(CCDBLanguageSet.GERMAN.allSubsets().contains(CCDBLanguageSet.EMPTY));
		assertTrue(CCDBLanguageSet.GERMAN.allSubsets().contains(CCDBLanguageSet.GERMAN));

		assertTrue(CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.ENGLISH).allSubsets().contains(CCDBLanguageSet.EMPTY));
		assertTrue(CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.ENGLISH).allSubsets().contains(CCDBLanguageSet.GERMAN));
		assertTrue(CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.ENGLISH).allSubsets().contains(CCDBLanguageSet.ENGLISH));
		assertTrue(CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.ENGLISH).allSubsets().contains(CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.ENGLISH)));
	}
}
