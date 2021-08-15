package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.features.databaseErrors.CCDatabaseValidator;
import de.jClipCorn.features.databaseErrors.DatabaseError;
import de.jClipCorn.features.databaseErrors.DatabaseErrorType;
import de.jClipCorn.features.databaseErrors.DatabaseValidatorOptions;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
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
		DatabaseValidatorOptions opt = new DatabaseValidatorOptions(
				true,  // movies
				true,  // series
				true,  // seasons
				true,  // episodes
				true,  // covers
				false, // cover files
				false, // video files
				true,  // groups
				true,  // online-refs
				true,  // internal db
				true,  // Additional
				false, // Validate Series Structure
				true); // Ignore IFO duplicates

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

		assertEmptyErrors(errs);
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

			assertArrayEquals(m.getTitle(), new Object[0], udp.toArray());
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

			assertArrayEquals(m.getTitle(), new Object[0], udp.toArray());
		}
	}

	@Test
	@Parameters({ "false", "true" })
	public void testDatabaseUserDataProblemSeries(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);

		for (CCSeries s : ml.iteratorSeries()) {

			List<UserDataProblem> udp = new ArrayList<>();

			UserDataProblem.testSeriesData(udp, ml, null, s);
			assertArrayEquals(s.getTitle(), new Object[0], udp.toArray());

			UserDataProblem.testSeriesData(udp, ml, s, s);
			assertArrayEquals(s.getTitle(), new Object[0], udp.toArray());
		}
	}

	@Test
	@Parameters({ "false", "true" })
	public void testDatabaseUserDataProblemSeason(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);

		for (CCSeason s : ml.iteratorSeasons()) {

			List<UserDataProblem> udp = new ArrayList<>();

			UserDataProblem.testSeasonData(udp, ml, null, s);
			assertArrayEquals(s.getTitle(), new Object[0], udp.toArray());

			UserDataProblem.testSeasonData(udp, ml, s, s);
			assertArrayEquals(s.getTitle(), new Object[0], udp.toArray());
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

			assertArrayEquals(e.getTitle(), new Object[0], udp.toArray());
		}
	}
}
