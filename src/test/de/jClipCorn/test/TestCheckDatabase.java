package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.databaseErrors.CCDatabaseValidator;
import de.jClipCorn.features.databaseErrors.DatabaseError;
import de.jClipCorn.features.databaseErrors.DatabaseErrorType;
import de.jClipCorn.features.databaseErrors.DatabaseValidatorOptions;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.listener.DoubleProgressCallbackListener;
import de.jClipCorn.util.stream.CCStreams;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
@RunWith(JUnitParamsRunner.class)
public class TestCheckDatabase extends ClipCornBaseTest {

	@Test
	@Parameters({ "false", "true" })
	public void testDatabaseValidator(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);

		List<DatabaseError> errs = new ArrayList<>();


		var opt = new DatabaseValidatorOptions();
		{
			opt.ValidateMovies = true;
			opt.ValidateSeries = true;
			opt.ValidateSeasons = true;
			opt.ValidateEpisodes = true;

			opt.ValidateCovers = true;
			opt.ValidateCoverFiles = false; // <=
			opt.ValidateVideoFiles = false; // <=
			opt.ValidateGroups = true;
			opt.ValidateOnlineReferences = true;

			opt.ValidateDuplicateFilesByPath = true;
			opt.ValidateDuplicateFilesByMediaInfo = true;
			opt.ValidateDatabaseConsistence = true;
			opt.ValidateSeriesStructure = false; // <=
			opt.FindEmptyDirectories = false;

			opt.IgnoreDuplicateIfos = true;
		}

		var validator = new CCDatabaseValidator(ml);
		validator.validate(errs, opt, DoubleProgressCallbackListener.EMPTY);

		DatabaseError e1 = CCStreams.iterate(errs).singleOrNull(p -> p.isTypeOf(DatabaseErrorType.ERROR_MEDIAINFO_UNSET) && "Der Bomber".equals(p.getElement1RawName()));
		DatabaseError e2 = CCStreams.iterate(errs).singleOrNull(p -> p.isTypeOf(DatabaseErrorType.ERROR_MEDIAINFO_UNSET) && "Forrest Gump".equals(p.getElement1RawName()));
		DatabaseError e3 = CCStreams.iterate(errs).singleOrNull(p -> p.isTypeOf(DatabaseErrorType.ERROR_MEDIAINFO_UNSET) && "Explosion Magic for This Formidable Enemy".equals(p.getElement1RawName()));
		DatabaseError e4 = CCStreams.iterate(errs).singleOrNull(p -> p.isTypeOf(DatabaseErrorType.ERROR_MEDIAINFO_UNSET) && "A Price for This Cursed Sword".equals(p.getElement1RawName()));
		DatabaseError e5 = CCStreams.iterate(errs).singleOrNull(p -> p.isTypeOf(DatabaseErrorType.ERROR_MEDIAINFO_UNSET) && "A Loving Hand for Our Party When We Can't Make It Through Winter".equals(p.getElement1RawName()));
		DatabaseError e6 = CCStreams.iterate(errs).singleOrNull(p -> p.isTypeOf(DatabaseErrorType.ERROR_MEDIAINFO_UNSET) && "God's Blessing on This Wonderful Shop".equals(p.getElement1RawName()));

		assertNotNull(e1);
		assertNotNull(e2);
		assertNotNull(e3);
		assertNotNull(e4);
		assertNotNull(e5);
		assertNotNull(e6);

		errs.remove(e1);
		errs.remove(e2);
		errs.remove(e3);
		errs.remove(e4);
		errs.remove(e5);
		errs.remove(e6);

		// Test database predates the checksum feature - all entries with files will have missing checksums
		errs.removeIf(p -> p.isTypeOf(DatabaseErrorType.ERROR_CHECKSUM_MISSING));

		assertEmptyErrors(errs);
	}

	@Test
	public void testCoverValidationWithMultipleCoverlessElements() throws Exception {
		CCMovieList ml = createExampleDB();

		var mov1 = movieByTitle(ml, "Der Bomber");
		var mov2 = movieByTitle(ml, "Forrest Gump");

		var cvr1 = mov1.getCoverInfo().Filename;
		var cvr2 = mov2.getCoverInfo().Filename;

		mov1.setCover(CCUUID.EMPTY);
		mov2.setCover(CCUUID.EMPTY);

		List<DatabaseError> errs = new ArrayList<>();

		var opt = new DatabaseValidatorOptions();
		opt.ValidateCovers = true;

		var validator = new CCDatabaseValidator(ml);
		validator.validate(errs, opt, DoubleProgressCallbackListener.EMPTY);

		assertNull(CCStreams.iterate(errs).firstOrNull(p -> p.isTypeOf(DatabaseErrorType.ERROR_DUPLICATE_COVERLINK)));
		assertNull(CCStreams.iterate(errs).firstOrNull(p -> p.isTypeOf(DatabaseErrorType.ERROR_DB_EXCEPTION)));

		// the cover pass ran to completion and still sees the two now unreferenced covers
		assertNotNull(CCStreams.iterate(errs).firstOrNull(p -> p.isTypeOf(DatabaseErrorType.ERROR_UNUSED_COVER_ENTRY) && cvr1.equals(p.getElement1RawName())));
		assertNotNull(CCStreams.iterate(errs).firstOrNull(p -> p.isTypeOf(DatabaseErrorType.ERROR_UNUSED_COVER_ENTRY) && cvr2.equals(p.getElement1RawName())));
	}

	@Test
	public void testCoverlessElementIsNotReportedAsBrokenCover() throws Exception {
		CCMovieList ml = createExampleDB();

		var mov = movieByTitle(ml, "Der Bomber");
		mov.setCover(CCUUID.EMPTY);

		List<DatabaseError> errs = new ArrayList<>();

		var opt = new DatabaseValidatorOptions();
		opt.ValidateMovies = true;
		opt.ValidateCoverFiles = true;

		var validator = new CCDatabaseValidator(ml);
		validator.validate(errs, opt, DoubleProgressCallbackListener.EMPTY);

		assertNull(CCStreams.iterate(errs).firstOrNull(p -> p.isTypeOf(DatabaseErrorType.ERROR_COVER_TOO_SMALL)));
		assertNull(CCStreams.iterate(errs).firstOrNull(p -> p.isTypeOf(DatabaseErrorType.ERROR_COVER_NOT_FOUND)));

		// it is still reported as having no cover at all
		assertNotNull(CCStreams.iterate(errs).firstOrNull(p -> p.isTypeOf(DatabaseErrorType.ERROR_NOCOVERSET) && "Der Bomber".equals(p.getElement1RawName())));
	}

	@Test
	public void testMalformedIDsInAllIDColumns() throws Exception {
		CCMovieList ml = createExampleDB();

		var db = (GenericDatabase) ml.getInternalDatabaseDirectly();
		db.executeSQLThrow("UPDATE COVERS   SET ID       = 'bad-covers-id'   WHERE ID = (SELECT ID FROM COVERS   LIMIT 1)");
		db.executeSQLThrow("UPDATE MOVIES   SET COVERID  = 'bad-movie-cvrid' WHERE ID = (SELECT ID FROM MOVIES   LIMIT 1)");
		db.executeSQLThrow("UPDATE SEASONS  SET SERIESID = 'bad-seriesid'    WHERE ID = (SELECT ID FROM SEASONS  LIMIT 1)");
		db.executeSQLThrow("UPDATE EPISODES SET SEASONID = 'bad-seasonid'    WHERE ID = (SELECT ID FROM EPISODES LIMIT 1)");

		List<DatabaseError> errs = new ArrayList<>();

		var opt = new DatabaseValidatorOptions();
		opt.ValidateDatabaseConsistence = true;

		var validator = new CCDatabaseValidator(ml);
		validator.validate(errs, opt, DoubleProgressCallbackListener.EMPTY);

		var malformed = CCStreams.iterate(errs).filter(p -> p.isTypeOf(DatabaseErrorType.ERROR_DB_MALFORMED_ID)).map(DatabaseError::getElement1RawName).toSet();

		assertTrue(malformed.contains("bad-covers-id"));
		assertTrue(malformed.contains("bad-movie-cvrid"));
		assertTrue(malformed.contains("bad-seriesid"));
		assertTrue(malformed.contains("bad-seasonid"));
	}

	@Test
	public void testDatabaseErrorElementsEquals() {
		CCMovieList ml = createEmptyDB();

		Object el1 = new Object();
		Object el2 = new Object();

		var double12 = DatabaseError.createDouble(ml, DatabaseErrorType.ERROR_DUPLICATE_FILE, el1, el2);
		var double21 = DatabaseError.createDouble(ml, DatabaseErrorType.ERROR_DUPLICATE_FILE, el2, el1);

		assertSame(el2, double12.getElement2());

		assertTrue(double12.elementsEquals(DatabaseError.createDouble(ml, DatabaseErrorType.ERROR_DUPLICATE_FILE, el1, el2)));
		assertFalse(double12.elementsEquals(double21));

		var single1 = DatabaseError.createSingle(ml, DatabaseErrorType.ERROR_TITLE_NOT_SET, el1);

		assertTrue(single1.elementsEquals(DatabaseError.createSingle(ml, DatabaseErrorType.ERROR_NOCOVERSET, el1)));
		assertFalse(single1.elementsEquals(DatabaseError.createSingle(ml, DatabaseErrorType.ERROR_NOCOVERSET, el2)));
	}

	@Test
	@Parameters({ "false", "true" })
	public void testDatabaseUserDataProblemMovies(boolean dbmode) throws Exception {
		CCMovieList mle = createEmptyDB();
		CCMovieList ml = createExampleDB(dbmode);

		for (CCMovie m : ml.iteratorMovies())
		{
			List<UserDataProblem> udp = new ArrayList<>();
			UserDataProblem.testMovieData(udp, mle, null, m);

			if ("Der Bomber".equals(m.getTitle())) {
				assertEquals(1, udp.size());
				assertEquals(UserDataProblem.PROBLEM_MEDIAINFO_UNSET, udp.get(0).getPID());
				udp.remove(0);
			} else if ("Forrest Gump".equals(m.getTitle())) {
				assertEquals(1, udp.size());
				assertEquals(UserDataProblem.PROBLEM_MEDIAINFO_UNSET, udp.get(0).getPID());
				udp.remove(0);
			}

			assertEmptyUDP(m.getTitle(), udp);
		}

		for (CCMovie m : ml.iteratorMovies())
		{
			List<UserDataProblem> udp = new ArrayList<>();
			UserDataProblem.testMovieData(udp, mle, m, m);

			if ("Der Bomber".equals(m.getTitle())) {
				assertEquals(1, udp.size());
				assertEquals(UserDataProblem.PROBLEM_MEDIAINFO_UNSET, udp.get(0).getPID());
				udp.remove(0);
			} else if ("Forrest Gump".equals(m.getTitle())) {
				assertEquals(1, udp.size());
				assertEquals(UserDataProblem.PROBLEM_MEDIAINFO_UNSET, udp.get(0).getPID());
				udp.remove(0);
			}

			assertEmptyUDP(m.getTitle(), udp);
		}
	}

	@Test
	@Parameters({ "false", "true" })
	public void testDatabaseUserDataProblemSeries(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);

		for (CCSeries s : ml.iteratorSeries()) {

			List<UserDataProblem> udp = new ArrayList<>();

			UserDataProblem.testSeriesData(udp, ml, null, s);
			assertEmptyUDP(s.getTitle(), udp);

			UserDataProblem.testSeriesData(udp, ml, s, s);
			assertEmptyUDP(s.getTitle(), udp);
		}
	}

	@Test
	@Parameters({ "false", "true" })
	public void testDatabaseUserDataProblemSeason(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);

		for (CCSeason s : ml.iteratorSeasons()) {

			List<UserDataProblem> udp = new ArrayList<>();

			UserDataProblem.testSeasonData(udp, ml, null, s);
			assertEmptyUDP(s.getTitle(), udp);

			UserDataProblem.testSeasonData(udp, ml, s, s);
			assertEmptyUDP(s.getTitle(), udp);
		}
	}

	@Test
	@Parameters({ "false", "true" })
	public void testDatabaseUserDataProblemEpisodes(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);

		for (CCEpisode e : ml.iteratorEpisodes()) {

			List<UserDataProblem> udp = new ArrayList<>();
			UserDataProblem.testEpisodeData(udp, ml, null, null, e);

			if ("Explosion Magic for This Formidable Enemy".equals(e.getTitle())) {
				assertEquals(1, udp.size());
				assertEquals(UserDataProblem.PROBLEM_MEDIAINFO_UNSET, udp.get(0).getPID());
				udp.remove(0);
			} else if ("A Price for This Cursed Sword".equals(e.getTitle())) {
				assertEquals(1, udp.size());
				assertEquals(UserDataProblem.PROBLEM_MEDIAINFO_UNSET, udp.get(0).getPID());
				udp.remove(0);
			} else if ("A Loving Hand for Our Party When We Can't Make It Through Winter".equals(e.getTitle())) {
				assertEquals(1, udp.size());
				assertEquals(UserDataProblem.PROBLEM_MEDIAINFO_UNSET, udp.get(0).getPID());
				udp.remove(0);
			} else if ("God's Blessing on This Wonderful Shop".equals(e.getTitle())) {
				assertEquals(1, udp.size());
				assertEquals(UserDataProblem.PROBLEM_MEDIAINFO_UNSET, udp.get(0).getPID());
				udp.remove(0);
			}

			assertEmptyUDP(e.getTitle(), udp);
		}
	}
}
