package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.JSONListFormatException;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public class TestJSONListColumns extends ClipCornBaseTest {

	@Test
	public void testDateTimeList() throws CCFormatException {
		assertTrue(CCDateTimeList.fromJSONArray("[]").isEmpty());
		assertTrue(CCDateTimeList.fromJSONArray("").isEmpty());

		String json = "[{\"d\":\"UNSPECIFIED\",\"c\":\"\"},{\"d\":\"2016-09-07\",\"c\":\"Kino\"},{\"d\":\"2016-09-08 12:13:14\",\"c\":\"\"}]";
		assertEquals(json, CCDateTimeList.fromJSONArray(json).asJSONArray());
		assertEquals("Kino", CCDateTimeList.fromJSONArray(json).entries().get(1).comment);

		String legacy = "[\"UNSPECIFIED\",\"2016-09-07\",\"2016-09-08 12:13:14\"]";
		assertEquals(json.replace("Kino", ""), CCDateTimeList.fromJSONArray(legacy).asJSONArray());

		String mixed = "[\"2016-09-08 12:13:14\",{\"d\":\"2016-09-07\"},{\"c\":\"x\",\"d\":\"UNSPECIFIED\"}]";
		assertEquals("[{\"d\":\"UNSPECIFIED\",\"c\":\"x\"},{\"d\":\"2016-09-07\",\"c\":\"\"},{\"d\":\"2016-09-08 12:13:14\",\"c\":\"\"}]", CCDateTimeList.fromJSONArray(mixed).asJSONArray());

		var special = CCDateTimeList.createEmpty().add(CCDateTime.createFromSQL("2016-09-07"), "a\"b\\c</d\nä");
		assertEquals("[{\"d\":\"2016-09-07\",\"c\":\"a\\\"b\\\\c<\\/d\\nä\"}]", special.asJSONArray());
		assertEquals(special, CCDateTimeList.fromJSONArray(special.asJSONArray()));

		assertNotEquals(CCDateTimeList.fromJSONArray(json), CCDateTimeList.fromJSONArray(legacy));

		assertThrows(JSONListFormatException.class, () -> CCDateTimeList.fromJSONArray("[\"2016-09-07\",\"invalid\"]"));
		assertThrows(JSONListFormatException.class, () -> CCDateTimeList.fromJSONArray("[\"2016-09-07\""));
		assertThrows(JSONListFormatException.class, () -> CCDateTimeList.fromJSONArray("[\"2016-09-07\",42]"));
		assertThrows(JSONListFormatException.class, () -> CCDateTimeList.fromJSONArray("[{\"c\":\"x\"}]"));
		assertThrows(JSONListFormatException.class, () -> CCDateTimeList.fromJSONArray("[{\"d\":\"2016-09-07\",\"c\":5}]"));
	}

	@Test
	public void testTagList() throws CCFormatException {
		String json = CCTagList.create(CCSingleTag.find(0), CCSingleTag.find(1)).asJSONArray();
		assertEquals(json, CCTagList.fromJSONArray(json).asJSONArray());

		assertThrows(JSONListFormatException.class, () -> CCTagList.fromJSONArray("[0,9999]"));
		assertThrows(JSONListFormatException.class, () -> CCTagList.fromJSONArray("[0,\"x\"]"));
		assertThrows(JSONListFormatException.class, () -> CCTagList.fromJSONArray("[0"));
	}

	@Test
	public void testGenreList() throws CCFormatException {
		String json = CCGenreList.create(CCGenre.GENRE_001, CCGenre.GENRE_005).asJSONArray();
		assertEquals(json, CCGenreList.fromJSONArray(json).asJSONArray());

		assertThrows(JSONListFormatException.class, () -> CCGenreList.fromJSONArray("[1,9999]"));
		assertThrows(JSONListFormatException.class, () -> CCGenreList.fromJSONArray("[1,\"x\"]"));
		assertThrows(JSONListFormatException.class, () -> CCGenreList.fromJSONArray("[1"));
	}

	@Test
	public void testLanguageSetAndList() throws CCFormatException {
		String jsonSet = CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.ENGLISH).asJSONArray();
		assertEquals(jsonSet, CCDBLanguageSet.fromJSONArray(jsonSet).asJSONArray());

		String jsonList = CCDBLanguageList.create(CCDBLanguage.ENGLISH, CCDBLanguage.GERMAN).asJSONArray();
		assertEquals(jsonList, CCDBLanguageList.fromJSONArray(jsonList).asJSONArray());

		assertThrows(JSONListFormatException.class, () -> CCDBLanguageSet.fromJSONArray("[0,9999]"));
		assertThrows(JSONListFormatException.class, () -> CCDBLanguageSet.fromJSONArray("[0"));
		assertThrows(JSONListFormatException.class, () -> CCDBLanguageList.fromJSONArray("[0,9999]"));
		assertThrows(JSONListFormatException.class, () -> CCDBLanguageList.fromJSONArray("[0"));
	}

	@Test
	public void testOnlineReferenceList() throws CCFormatException {
		String json = CCOnlineReferenceList.create(CCSingleOnlineReference.createIMDB("tt0111161"), CCSingleOnlineReference.createAniList(1)).asJSONArray();
		assertEquals(json, CCOnlineReferenceList.fromJSONArray(json).asJSONArray());

		assertThrows(JSONListFormatException.class, () -> CCOnlineReferenceList.fromJSONArray("[{\"t\":\"invalid\",\"id\":\"1\"}]"));
		assertThrows(JSONListFormatException.class, () -> CCOnlineReferenceList.fromJSONArray("[{\"t\":\"imdb\"}]"));
		assertThrows(JSONListFormatException.class, () -> CCOnlineReferenceList.fromJSONArray("[1]"));
	}

	@Test
	public void testGroupList() throws CCFormatException {
		CCMovieList ml = createEmptyDB();

		String json = CCGroupList.create(CCGroup.create("Group A"), CCGroup.create("Group_B")).asJSONArray();
		assertEquals(json, CCGroupList.fromJSONArrayWithoutAddingNewGroups(ml, json).asJSONArray());

		assertThrows(CCFormatException.class, () -> CCGroupList.fromJSONArrayWithoutAddingNewGroups(ml, "[\"Group A\",\"in;valid\"]"));
		assertThrows(CCFormatException.class, () -> CCGroupList.fromJSONArrayWithoutAddingNewGroups(ml, "[\"Group A\",1]"));
		assertThrows(CCFormatException.class, () -> CCGroupList.fromJSONArrayWithoutAddingNewGroups(ml, "[\"Group A\""));
	}

	@Test
	public void testInvalidColumnAbortsLoadWithoutTouchingTheDatabase() throws Exception {
		CCMovieList ml = createEmptyDB();
		ml.createNewMovie(m -> {
			m.Title.set("Title");
			m.ViewedHistory.set(CCDateTimeList.create(CCDateTime.createFromSQL("2020-01-01")));
		});

		String corrupt = "[\"2020-01-01\",\"invalid\"]";

		var db = (GenericDatabase) ml.getInternalDatabaseDirectly();
		db.executeSQLThrow("UPDATE userdata.MOVIES SET [VIEWED_HISTORY]='" + corrupt + "'");

		var reloaded = CCMovieList.createInstanceMovieList(ml.ccprops(), ml.getDatabaseForUnitTests());
		assertThrows(JSONListFormatException.class, () -> ml.getDatabaseForUnitTests().fillMovieList(reloaded));

		assertEquals(corrupt, db.querySingleStringSQLThrow("SELECT [VIEWED_HISTORY] FROM userdata.MOVIES", 0));
	}
}
