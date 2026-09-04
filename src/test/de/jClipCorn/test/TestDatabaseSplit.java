package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.database.driver.DatabaseStructure;
import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.database.elementProps.impl.ETargetDatabase;
import de.jClipCorn.database.history.CCHistoryAction;
import de.jClipCorn.database.history.CCHistoryTable;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.stream.CCStreams;
import de.jClipCorn.util.sqlwrapper.CCSQLColDef;
import de.jClipCorn.util.sqlwrapper.CCSQLTableDef;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public class TestDatabaseSplit extends ClipCornBaseTest {

	private static Set<String> columnNames(CCSQLTableDef tab) {
		return tab.Columns.stream().map(c -> c.Name).collect(Collectors.toSet());
	}

	private static Set<String> userPropertyNames(ICCDatabaseStructureElement el) {
		return CCStreams.iterate(el.getProperties())
				.filter(p -> p.getTargetDatabase() == ETargetDatabase.USERDATA)
				.map(IEProperty::getName)
				.enumerate().stream().collect(Collectors.toSet());
	}

	@Test
	public void testTargetsPartitionTheEntityTables() {
		var pairs = List.of(
				new CCSQLTableDef[] { DatabaseStructure.TAB_MOVIES,   DatabaseStructure.TAB_UD_MOVIES   },
				new CCSQLTableDef[] { DatabaseStructure.TAB_SERIES,   DatabaseStructure.TAB_UD_SERIES   },
				new CCSQLTableDef[] { DatabaseStructure.TAB_SEASONS,  DatabaseStructure.TAB_UD_SEASONS  },
				new CCSQLTableDef[] { DatabaseStructure.TAB_EPISODES, DatabaseStructure.TAB_UD_EPISODES });

		for (var pair : pairs) {
			var main = pair[0];
			var user = pair[1];

			assertEquals(DatabaseStructure.SCHEMA_MAIN, main.Schema);
			assertEquals(DatabaseStructure.SCHEMA_USERDATA, user.Schema);
			assertEquals(main.Name, user.Name);

			// ID is the only column both files hold, and it is the primary key of both
			var shared = columnNames(main);
			shared.retainAll(columnNames(user));
			assertEquals(main.Name, Set.of("ID"), shared);

			assertEquals("ID", main.Primary.Name);
			assertEquals("ID", user.Primary.Name);
			assertNotSame("main and user-data ID must be distinct column definitions", main.Primary, user.Primary);
		}
	}

	@Test
	public void testEveryTableBelongsToExactlyOneSchema() {
		for (CCSQLTableDef tab : DatabaseStructure.TABLES_MAIN)     assertEquals(tab.Name, DatabaseStructure.SCHEMA_MAIN, tab.Schema);
		for (CCSQLTableDef tab : DatabaseStructure.TABLES_USERDATA) assertEquals(tab.Name, DatabaseStructure.SCHEMA_USERDATA, tab.Schema);

		for (CCSQLTableDef a : DatabaseStructure.TABLES_MAIN) {
			for (CCSQLTableDef b : DatabaseStructure.TABLES_USERDATA) assertNotSame(a, b);
		}
	}

	@Test
	public void testUserPropertiesMatchTheUserDataSchema() throws Exception {
		CCMovieList ml = createExampleDB();

		assertEquals(Set.of("ViewedHistory", "Tags", "Score", "ScoreComment"), userPropertyNames(ml.iteratorMovies().firstOrNull()));
		assertEquals(Set.of("Tags", "Score", "ScoreComment"),                  userPropertyNames(ml.iteratorSeries().firstOrNull()));
		assertEquals(Set.of("Score", "ScoreComment"),                          userPropertyNames(ml.iteratorSeasons().firstOrNull()));
		assertEquals(Set.of("ViewedHistory", "Tags", "Score", "ScoreComment"), userPropertyNames(ml.iteratorEpisodes().firstOrNull()));

		// AddDate and Groups are user metadata but are deliberately shared (D4/D5)
		for (IEProperty p : ml.iteratorMovies().firstOrNull().getProperties()) {
			if (p.getName().equals("AddDate") || p.getName().equals("Groups")) assertEquals(p.getName(), ETargetDatabase.MAIN, p.getTargetDatabase());
		}
	}

	/** the property-level target and the column-level split have to agree */
	@Test
	public void testUserPropertyNamesAreCoveredByTheUserDataColumns() throws Exception {
		CCMovieList ml = createExampleDB();

		assertEquals(userPropertyNames(ml.iteratorMovies().firstOrNull()).size(),   DatabaseStructure.TAB_UD_MOVIES.getNonPrimaryColumns().count());
		assertEquals(userPropertyNames(ml.iteratorSeries().firstOrNull()).size(),   DatabaseStructure.TAB_UD_SERIES.getNonPrimaryColumns().count());
		assertEquals(userPropertyNames(ml.iteratorSeasons().firstOrNull()).size(),  DatabaseStructure.TAB_UD_SEASONS.getNonPrimaryColumns().count());
		assertEquals(userPropertyNames(ml.iteratorEpisodes().firstOrNull()).size(), DatabaseStructure.TAB_UD_EPISODES.getNonPrimaryColumns().count());
	}

	@Test
	public void testMainTableHoldsNoUserColumns() {
		for (CCSQLTableDef tab : new CCSQLTableDef[] { DatabaseStructure.TAB_MOVIES, DatabaseStructure.TAB_SERIES, DatabaseStructure.TAB_SEASONS, DatabaseStructure.TAB_EPISODES }) {
			for (CCSQLColDef col : tab.Columns) {
				assertFalse(tab.Name + "." + col.Name, List.of("VIEWED_HISTORY", "TAGS", "SCORE", "SCORECOMMENT").contains(col.Name));
			}
		}
	}

	private static int userRowCount(CCMovieList ml, String table) throws Exception {
		return ml.getInternalDatabaseDirectly().querySingleIntSQLThrow("SELECT COUNT(*) FROM userdata." + table, 0);
	}

	@Test
	public void testUserDataRowsAreSparse() throws Exception {
		CCMovieList ml = createEmptyDB();

		CCMovie mov = ml.createNewMovie(m -> m.Title.set("Title"));

		// nothing user-side was touched -> no row in the user database
		assertEquals(0, userRowCount(ml, "MOVIES"));

		mov.Score.set(CCUserScore.RATING_IV);
		assertEquals(1, userRowCount(ml, "MOVIES"));

		mov.Score.set(CCUserScore.RATING_NO);
		assertEquals(0, userRowCount(ml, "MOVIES"));

		mov.ScoreComment.set("nice");
		assertEquals(1, userRowCount(ml, "MOVIES"));
	}

	@Test
	public void testUserDataRoundtrip() throws Exception {
		CCMovieList ml = createEmptyDB();

		CCMovie mov = ml.createNewMovie(m -> {
			m.Title.set("Title");
			m.Score.set(CCUserScore.RATING_V);
			m.ScoreComment.set("comment");
		});
		CCUUID id = mov.getID();

		ml.forceReconnectAndReloadForTests();

		CCMovie read = ml.iteratorMovies().firstOrNull();
		assertEquals(id, read.getID());
		assertEquals(CCUserScore.RATING_V, read.Score.get());
		assertEquals("comment", read.ScoreComment.get());
	}

	/** the common case after a sync: a main row without a matching user-data row */
	@Test
	public void testMissingUserRowFallsBackToDefaults() throws Exception {
		CCMovieList ml = createEmptyDB();

		ml.createNewMovie(m -> {
			m.Title.set("Rated");
			m.Score.set(CCUserScore.RATING_V);
			m.ScoreComment.set("comment");
		});
		ml.createNewMovie(m -> m.Title.set("Untouched"));

		assertEquals(1, userRowCount(ml, "MOVIES"));

		ml.forceReconnectAndReloadForTests();

		CCMovie rated = movieByTitle(ml, "Rated");
		assertEquals(CCUserScore.RATING_V, rated.Score.get());
		assertEquals("comment", rated.ScoreComment.get());

		CCMovie untouched = movieByTitle(ml, "Untouched");
		assertEquals(CCUserScore.RATING_NO, untouched.Score.get());
		assertEquals("", untouched.ScoreComment.get());
		assertEquals(CCDateTimeList.createEmpty().asJSONArray(), untouched.ViewedHistory.get().asJSONArray());
		assertEquals(CCTagList.EMPTY.asJSONArray(), untouched.Tags.get().asJSONArray());
	}

	@Test
	public void testHistoryTriggerExistInBothSchemas() throws Exception {
		CCMovieList ml = createEmptyDB();

		ml.getHistory().enableTrigger();

		var names = CCStreams.iterate(ml.getDatabaseForUnitTests().listTrigger()).map(p -> p.Item1).enumerate();

		assertTrue(names.contains("JCCTRIGGER_AUTOHISTORY_ADD_MAIN_MOVIES"));
		assertTrue(names.contains("JCCTRIGGER_AUTOHISTORY_ADD_USERDATA_MOVIES"));
		assertTrue(names.contains("JCCTRIGGER_AUTOHISTORY_UPD_USERDATA_MOVIES_SCORE"));
		assertTrue(names.contains("JCCTRIGGER_AUTOHISTORY_REM_MAIN_EPISODES"));

		// no table is tracked in the wrong file
		assertFalse(names.contains("JCCTRIGGER_AUTOHISTORY_ADD_MAIN_PROPERTIES"));
		assertFalse(names.contains("JCCTRIGGER_AUTOHISTORY_ADD_USERDATA_PROPERTIES"));
		assertFalse(names.contains("JCCTRIGGER_AUTOHISTORY_ADD_USERDATA_HISTORY"));
	}

	@Test
	public void testUserSideChangeIsRecordedInTheUserHistory() throws Exception {
		CCMovieList ml = createEmptyDB();

		CCMovie mov = ml.createNewMovie(m -> m.Title.set("Title"));

		ml.getHistory().enableTrigger();

		mov.Title.set("Changed");
		mov.Score.set(CCUserScore.RATING_IV);

		var db = ml.getInternalDatabaseDirectly();
		assertTrue(db.querySingleIntSQLThrow("SELECT COUNT(*) FROM main.HISTORY WHERE [FIELD]='NAME'", 0) > 0);
		assertTrue(db.querySingleIntSQLThrow("SELECT COUNT(*) FROM userdata.HISTORY WHERE [FIELD]='SCORE'", 0) > 0);

		assertEquals(0, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM main.HISTORY WHERE [FIELD]='SCORE'", 0));
		assertEquals(0, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM userdata.HISTORY WHERE [FIELD]='NAME'", 0));
	}

	/** someone who only received the shared file from a publisher gets a fresh user-data database */
	@Test
	public void testUserDataDatabaseIsRecreatedWhenMissing() throws Exception {
		var dir = createAutocleanedDir("dbsplit");

		{
			var ml = CCMovieList.connectAndLoadDirect(CCDatabaseDriver.SQLITE, dir, "ClipCornDB", false, true);
			ml.createNewMovie(m -> { m.Title.set("Title"); m.Score.set(CCUserScore.RATING_V); });
			ml.shutdown();
		}

		var userDb = dir.append("ClipCornDB", "ClipCornUserData.db");
		assertTrue(userDb.fileExists());
		userDb.deleteWithException();

		{
			var ml = CCMovieList.connectAndLoadDirect(CCDatabaseDriver.SQLITE, dir, "ClipCornDB", false, false);

			CCMovie read = ml.iteratorMovies().firstOrNull();
			assertEquals("Title", read.getTitle());
			assertEquals(CCUserScore.RATING_NO, read.Score.get());

			var db = ml.getDatabaseForUnitTests();
			assertEquals(db.getInformation_DUUID(), db.readUserDataInformationFromDB(DatabaseStructure.INFOKEY_MAINDB_DUUID, null));

			// the recreated database is usable again
			read.Score.set(CCUserScore.RATING_III);
			assertEquals(1, userRowCount(ml, "MOVIES"));

			ml.shutdown();
		}
	}

	/** the sparse row appears and vanishes with the user data, but the element itself was neither added nor removed */
	@Test
	public void testCreatingAndDroppingTheUserRowIsLoggedAsUpdate() throws Exception {
		CCMovieList ml = createEmptyDB();

		CCMovie mov = ml.createNewMovie(m -> m.Title.set("Title"));

		ml.getHistory().enableTrigger();

		mov.Score.set(CCUserScore.RATING_IV);

		var db = ml.getInternalDatabaseDirectly();
		assertEquals(0, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM userdata.HISTORY WHERE [TABLE]='MOVIES' AND [ACTION]<>'UPDATE'", 0));

		// the missing row stood for the defaults
		assertEquals(Integer.toString(CCUserScore.RATING_NO.asInt()), db.querySingleStringSQLThrow("SELECT [OLD] FROM userdata.HISTORY WHERE [FIELD]='SCORE'", 0));
		assertEquals(CCTagList.EMPTY.asJSONArray(),                   db.querySingleStringSQLThrow("SELECT [OLD] FROM userdata.HISTORY WHERE [FIELD]='TAGS'", 0));
		assertEquals(CCDateTimeList.createEmpty().asJSONArray(),       db.querySingleStringSQLThrow("SELECT [OLD] FROM userdata.HISTORY WHERE [FIELD]='VIEWED_HISTORY'", 0));

		mov.Score.set(CCUserScore.RATING_NO);

		assertEquals(0, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM userdata.HISTORY WHERE [TABLE]='MOVIES' AND [ACTION]<>'UPDATE'", 0));
		assertEquals(Integer.toString(CCUserScore.RATING_NO.asInt()), db.querySingleStringSQLThrow("SELECT [NEW] FROM userdata.HISTORY WHERE [FIELD]='SCORE' ORDER BY rowid DESC", 0));
	}

	/** the per-field rows of one user-side edit have to end up in a single entry */
	@Test
	public void testFirstUserEditIsOneUpdateEntry() throws Exception {
		CCMovieList ml = createEmptyDB();

		CCMovie mov = ml.createNewMovie(m -> m.Title.set("Title"));

		ml.getHistory().enableTrigger();

		mov.Score.set(CCUserScore.RATING_IV);

		var entries = CCStreams.iterate(ml.getHistory().query(ml, false, false, false, null, Opt.empty(), null).Item1)
				.filter(p -> p.Table == CCHistoryTable.MOVIES)
				.enumerate();

		assertEquals(1, entries.size());
		assertEquals(CCHistoryAction.UPDATE, entries.get(0).Action);
		assertSame(mov, entries.get(0).getSourceElement());

		// the three columns that stayed at their default are not a change
		assertEquals(1, entries.get(0).Changes.size());
		assertEquals("SCORE", entries.get(0).Changes.get(0).Field);
	}

	/** the first rating of a just-added element belongs to the add, it is not a separate edit */
	@Test
	public void testUserDataOfANewElementFoldsIntoTheAdd() throws Exception {
		CCMovieList ml = createEmptyDB();

		ml.getHistory().enableTrigger();

		CCMovie mov = ml.createNewMovie(m -> m.Title.set("Title"));
		Thread.sleep(20); // the merge is order-dependent, and the two writes would otherwise share a timestamp
		mov.Score.set(CCUserScore.RATING_IV);

		var scored = CCStreams.iterate(ml.getHistory().query(ml, false, false, false, null, Opt.empty(), null).Item1)
				.filter(p -> p.Table == CCHistoryTable.MOVIES && p.getNewValue("SCORE").isPresent())
				.enumerate();

		assertEquals(1, scored.size());
		assertEquals(CCHistoryAction.INSERT, scored.get(0).Action);
		assertSame(mov, scored.get(0).getSourceElement());
	}

	/** the user row is upserted, not deleted and re-inserted, or every edit would log a remove+add pair */
	@Test
	public void testRepeatedUserEditsStayUpdatesInTheHistory() throws Exception {
		CCMovieList ml = createEmptyDB();

		CCMovie mov = ml.createNewMovie(m -> { m.Title.set("Title"); m.Score.set(CCUserScore.RATING_IV); });

		ml.getHistory().enableTrigger();

		mov.Score.set(CCUserScore.RATING_V);
		mov.ScoreComment.set("comment");

		var db = ml.getInternalDatabaseDirectly();
		assertEquals(0, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM userdata.HISTORY WHERE [TABLE]='MOVIES' AND [ACTION] <> 'UPDATE'", 0));

		// only the two fields that actually changed
		assertEquals(2, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM userdata.HISTORY WHERE [TABLE]='MOVIES'", 0));
	}

	/** the row-write transaction is connection-global - a second writer must not abort the first one */
	@Test
	public void testConcurrentRowWritesDoNotCorruptEachOther() throws Exception {
		CCMovieList ml = createEmptyDB();

		final int threadCount   = 4;
		final int moviesPerThread = 4;

		var movies = new ArrayList<CCMovie>();
		for (int i = 0; i < threadCount * moviesPerThread; i++) {
			final int n = i;
			movies.add(ml.createNewMovie(m -> m.Title.set("Movie " + n)));
		}

		var failures = Collections.synchronizedList(new ArrayList<Throwable>());
		var threads  = new ArrayList<Thread>();

		for (int t = 0; t < threadCount; t++) {
			final int base = t * moviesPerThread;
			var thread = new Thread(() -> {
				try {
					for (int r = 0; r < 25; r++) {
						CCMovie mov = movies.get(base + (r % moviesPerThread));
						mov.Score.set(CCUserScore.RATING_III);
						mov.ScoreComment.set("run " + r);
						mov.Score.set(CCUserScore.RATING_NO);
						mov.ScoreComment.set("");
					}
					for (int i = 0; i < moviesPerThread; i++) movies.get(base + i).Score.set(CCUserScore.RATING_V);
				} catch (Throwable e) {
					failures.add(e);
				}
			});
			threads.add(thread);
			thread.start();
		}
		for (var thread : threads) thread.join();

		assertEquals(CCStreams.iterate(failures).stringjoin(Throwable::toString, "\n"), 0, failures.size());

		var db = ml.getInternalDatabaseDirectly();
		assertEquals(movies.size(), userRowCount(ml, "MOVIES"));
		assertEquals(0, db.querySingleIntSQLThrow("SELECT COUNT(*) FROM userdata.MOVIES WHERE SCORE <> " + CCUserScore.RATING_V.asInt(), 0));
		assertEquals(movies.size(), db.querySingleIntSQLThrow("SELECT COUNT(*) FROM main.MOVIES", 0));
	}

	/** regenerating the main DUUID has to rebind the user-data database, or the next start aborts */
	@Test
	public void testResetDUUIDRebindsTheUserDataDatabase() throws Exception {
		var dir = createAutocleanedDir("dbsplit_duuid");

		{
			var ml = CCMovieList.connectAndLoadDirect(CCDatabaseDriver.SQLITE, dir, "ClipCornDB", false, true);
			ml.createNewMovie(m -> { m.Title.set("Title"); m.Score.set(CCUserScore.RATING_V); });

			var db = ml.getDatabaseForUnitTests();
			var before = db.getInformation_DUUID();

			ml.resetLocalDUUID();

			assertNotEquals(before, db.getInformation_DUUID());
			assertEquals(db.getInformation_DUUID(), db.readUserDataInformationFromDB(DatabaseStructure.INFOKEY_MAINDB_DUUID, null));

			ml.shutdown();
		}

		{
			var ml = CCMovieList.connectAndLoadDirect(CCDatabaseDriver.SQLITE, dir, "ClipCornDB", false, false);
			assertEquals(CCUserScore.RATING_V, ml.iteratorMovies().firstOrNull().Score.get());
			ml.shutdown();
		}
	}

	@Test
	public void testInfoKeysAreSplit() {
		CCMovieList ml = createEmptyDB();
		var db = ml.getDatabaseForUnitTests();

		for (var k : DatabaseStructure.INFOKEYS)          assertNotNull(k.Key, db.readInformationFromDB(k, null));
		for (var k : DatabaseStructure.INFOKEYS_USERDATA) assertNotNull(k.Key, db.readUserDataInformationFromDB(k, null));

		// HISTORY_ENABLED is per user, so it must not be in the shared file
		assertNull(db.readInformationFromDB(DatabaseStructure.INFOKEY_HISTORY, null));

		assertEquals(db.getInformation_DUUID(), db.readUserDataInformationFromDB(DatabaseStructure.INFOKEY_MAINDB_DUUID, null));
	}
}
