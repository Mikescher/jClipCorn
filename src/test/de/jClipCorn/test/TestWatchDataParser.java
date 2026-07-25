package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.parser.watchdata.WatchDataChangeSet;
import de.jClipCorn.util.parser.watchdata.WatchDataParser;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("nls")
@RunWith(JUnitParamsRunner.class)
public class TestWatchDataParser extends ClipCornBaseTest {

	@Test
	@Parameters({ "false", "true" })
	public void testWatchDataParse(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);

		List<String> err = new ArrayList<>();
		List<WatchDataChangeSet> r = WatchDataParser.parse(ml, SimpleFileUtils.readTextResource("/example_watchdata.txt", getClass()), err);

		assertEquals(0, err.size());

		assertEquals(17, r.size());

		for (WatchDataChangeSet wdcs : r) wdcs.execute();

		assertEquals(true, ml.getSeries("Steins;Gate").getSeasonByArrayIndex(0).getEpisodeByNumber(1).isViewed());
		assertTrue(ml.getSeries("Steins;Gate").getSeasonByArrayIndex(0).getEpisodeByNumber(1).ViewedHistory.get().contains(CCDateTime.parse("1.1.12", "d.M.y")));

		assertEquals(true, ml.getSeries("Steins;Gate").getSeasonByArrayIndex(0).getEpisodeByNumber(2).isViewed());
		assertTrue(ml.getSeries("Steins;Gate").getSeasonByArrayIndex(0).getEpisodeByNumber(2).ViewedHistory.get().contains(CCDateTime.parse("2.12", "d.M")));

		assertEquals(true, ml.getSeries("Soul Eater").getSeasonByArrayIndex(0).getEpisodeByNumber(6).isViewed());
		assertEquals(false, ml.getSeries("Soul Eater").getSeasonByArrayIndex(0).getEpisodeByNumber(7).isViewed());
		assertEquals(true, ml.getSeries("Soul Eater").getSeasonByArrayIndex(1).getEpisodeByNumber(17).isViewed());
		assertEquals(true, ml.getSeries("Soul Eater").getSeasonByArrayIndex(1).getEpisodeByNumber(18).isViewed());

		assertEquals(true, ml.getMovie("Hypercube").isViewed());

		assertEquals(true, ml.getMovie("Super 8").isViewed());
		assertTrue(ml.getMovie("Super 8").ViewedHistory.get().contains(CCDateTime.create(11, 12, 2012, 19, 4, 0)));

		assertEquals(true, ml.getMovie("Death Proof: Todsicher").isViewed());
		assertTrue(ml.getMovie("Death Proof: Todsicher").ViewedHistory.get().contains(CCDateTime.create(11, 12, 2012, 19, 4, 0)));
		assertEquals(CCUserScore.RATING_IV, ml.getMovie("Death Proof: Todsicher").Score.get());

		assertEquals(true, ml.getMovie("Der Bomber").isViewed());
		assertEquals(CCUserScore.RATING_V, ml.getMovie("Der Bomber").Score.get());
	}

	// The in-app example (shown by the "Show Example" button) must stay syntactically valid: its placeholder
	// SerienName/MovieName entries won't resolve (-> "not found" errors are fine), but no line may ever be
	// unrecognizable to the parser. Guards against advertising a syntax the parser rejects.
	@Test
	public void testExampleFileIsSyntacticallyValid() throws Exception {
		CCMovieList ml = createExampleDB(false);

		List<String> err = new ArrayList<>();
		WatchDataParser.parse(ml, SimpleFileUtils.readTextResource("/watchdata_example.txt", getClass()), err);

		for (String e : err) {
			assertFalse("example contains an unparseable line: " + e, e.contains("Cannot parse the content of this line"));
		}
	}

	// ================================================================================================================
	// Broad, per-case regression coverage of every supported WatchData syntax variant + the error paths.
	//
	// Each case: { name, input, expectedErrorCount, expectedChangeCount, verify }.
	// Cases are parsed against a fresh example-DB, the resulting change-sets are executed, and the verify-lambda
	// asserts the resulting DB state (viewed-flags / viewed-dates / scores) - all format-independent, so we do not
	// depend on the UI date-format (which needs a global CCProperties that is absent in unit-test mode).
	// ================================================================================================================

	@Test
	@Parameters(method = "watchDataCases")
	@TestCaseName("[{index}] {0}")
	public void testWatchDataCase(String name, String input, int expectedErrors, int expectedChanges, Consumer<CCMovieList> verify) throws Exception {
		CCMovieList ml = createExampleDB(false);

		List<String> err = new ArrayList<>();
		List<WatchDataChangeSet> r = WatchDataParser.parse(ml, input, err);

		assertEquals("wrong error count, errors=" + err, expectedErrors, err.size());
		assertEquals("wrong change count", expectedChanges, r.size());

		for (WatchDataChangeSet cs : r) cs.execute();

		if (verify != null) verify.accept(ml);
	}

	@SuppressWarnings("unused")
	private Object[] watchDataCases() {
		return new Object[] {
			// ---- series header + date-first content ----
			c("series-header-basic",     "Steins;Gate [S01]\n    1.1.12: E01",            0, 1, ml -> epOn(ml, "Steins;Gate", 0, 1, d("1.1.12", "d.M.y"))),
			c("series-date-no-year",     "Steins;Gate [S01]\n    2.12: E02",              0, 1, ml -> epOn(ml, "Steins;Gate", 0, 2, d("2.12", "d.M"))),
			c("series-comma-list",       "Steins;Gate [S01]\n    3.12.12: E04, E05, E06", 0, 3, ml -> { epOn(ml, "Steins;Gate", 0, 4, d("3.12.12", "d.M.y")); ep(ml, "Steins;Gate", 0, 5, true); ep(ml, "Steins;Gate", 0, 6, true); }),
			c("series-comma-trailing",   "Steins;Gate [S01]\n    3.12.12: E04, E05, E06,",0, 3, ml -> { ep(ml, "Steins;Gate", 0, 4, true); ep(ml, "Steins;Gate", 0, 5, true); ep(ml, "Steins;Gate", 0, 6, true); }),
			c("series-bracket-datetime", "Steins;Gate [S01]\n    [11.12.16 19:04]: E9",   0, 1, ml -> epOn(ml, "Steins;Gate", 0, 9, CCDateTime.create(11, 12, 2016, 19, 4, 0))),
			c("series-iso-seconds",      "Steins;Gate [S01]\n    [2016-12-11 19:04:33]: E10", 0, 1, ml -> epOn(ml, "Steins;Gate", 0, 10, CCDateTime.create(11, 12, 2016, 19, 4, 33))),
			c("series-leading-zero",     "Steins;Gate [S01]\n    3.12.12: E008",          0, 1, ml -> ep(ml, "Steins;Gate", 0, 8, true)),
			c("series-bare-numbers",     "Steins;Gate [S01]\n    1.1.12: 01, 2, 3",       0, 3, ml -> { ep(ml, "Steins;Gate", 0, 1, true); ep(ml, "Steins;Gate", 0, 2, true); ep(ml, "Steins;Gate", 0, 3, true); }),

			// ---- episode-first content form ----
			c("series-episode-first",    "Steins;Gate [S01]\n    E01: 1.1.12",            0, 1, ml -> epOn(ml, "Steins;Gate", 0, 1, d("1.1.12", "d.M.y"))),

			// ---- explicit season inside a season-less series header ----
			c("series-explicit-season",  "Soul Eater\n    4.3: S2E18",                    0, 1, ml -> ep(ml, "Soul Eater", 1, 18, true)),

			// ---- negatives (mark as NOT viewed) ----
			c("series-negative-unview",  "Soul Eater [S01]\n    3.3: E-07",               0, 1, ml -> ep(ml, "Soul Eater", 0, 7, false)),
			c("series-negative-season",  "Soul Eater\n    4.3: S2E-18",                   0, 1, ml -> ep(ml, "Soul Eater", 1, 18, false)),
			c("series-multi-line",       "Soul Eater [S01]\n    3.3: E06\n    4.3: E-07", 0, 2, ml -> { ep(ml, "Soul Eater", 0, 6, true); ep(ml, "Soul Eater", 0, 7, false); }),

			// ---- movies ----
			c("movie-bare",              "Hypercube",                                     0, 1, ml -> movieViewed(ml, "Hypercube", true)),
			c("movie-all-scores",        "Der Bomber {{---}}\nDer Bomber {{--}}\nDer Bomber {{-}}\nDer Bomber {{0}}\nDer Bomber {{+}}\nDer Bomber {{++}}\nDer Bomber {{+++}}", 0, 7, ml -> { movieViewed(ml, "Der Bomber", true); movieScore(ml, "Der Bomber", CCUserScore.RATING_V); }),
			c("movie-score-single",      "Der Bomber {{-}}",                              0, 1, ml -> movieScore(ml, "Der Bomber", CCUserScore.RATING_II)),
			c("movie-date",              "Super 8 [11.12.12 19:04]",                      0, 1, ml -> movieOn(ml, "Super 8", CCDateTime.create(11, 12, 2012, 19, 4, 0))),
			c("movie-score-then-date",   "Der Bomber {{++}} [11.12.12 19:04]",            0, 1, ml -> { movieOn(ml, "Der Bomber", CCDateTime.create(11, 12, 2012, 19, 4, 0)); movieScore(ml, "Der Bomber", CCUserScore.RATING_IV); }),
			c("movie-date-then-score",   "Der Bomber [11.12.12 19:04] {{++}}",            0, 1, ml -> { movieOn(ml, "Der Bomber", CCDateTime.create(11, 12, 2012, 19, 4, 0)); movieScore(ml, "Der Bomber", CCUserScore.RATING_IV); }),
			c("movie-datefirst",         "[11.12.12 19:04]: Death Proof: Todsicher",      0, 1, ml -> movieOn(ml, "Death Proof: Todsicher", CCDateTime.create(11, 12, 2012, 19, 4, 0))),
			c("movie-datefirst-nocolon", "[11.12.12 19:04] Hypercube",                    0, 1, ml -> movieOn(ml, "Hypercube", CCDateTime.create(11, 12, 2012, 19, 4, 0))),
			c("movie-datefirst-score",   "[11.12.12 19:04]: Der Bomber {{++}}",           0, 1, ml -> { movieOn(ml, "Der Bomber", CCDateTime.create(11, 12, 2012, 19, 4, 0)); movieScore(ml, "Der Bomber", CCUserScore.RATING_IV); }),

			// ---- single-line episode syntax ("[date]: Series [SxxExx]", e.g. Jellyfin export) ----
			c("singleline-basic",        "[11.12.12 19:04]: Soul Eater [S01E06]",         0, 1, ml -> epOn(ml, "Soul Eater", 0, 6, CCDateTime.create(11, 12, 2012, 19, 4, 0))),
			c("singleline-nocolon",      "[11.12.12 19:04] Soul Eater [S01E06]",          0, 1, ml -> epOn(ml, "Soul Eater", 0, 6, CCDateTime.create(11, 12, 2012, 19, 4, 0))),
			c("singleline-unview",       "[11.12.12 19:04]: Soul Eater [S01E-07]",        0, 1, ml -> ep(ml, "Soul Eater", 0, 7, false)),
			c("singleline-single-season","[11.12.12 19:04]: KonoSuba [E05]",              0, 1, ml -> epOn(ml, "KonoSuba", 0, 5, CCDateTime.create(11, 12, 2012, 19, 4, 0))),
			c("singleline-jellyfin-cmt", "[2026-07-22 21:59:09]: Soul Eater [S01E06]             // From Jellyfin [mike]", 0, 1, ml -> epOn(ml, "Soul Eater", 0, 6, CCDateTime.create(22, 7, 2026, 21, 59, 9))),
			c("singleline-jellyfin-blk", "[2026-07-22 21:59:09]: Soul Eater [S01E06]\n[2026-07-23 21:29:00]: Soul Eater [S02E17]", 0, 2, ml -> { epOn(ml, "Soul Eater", 0, 6, CCDateTime.create(22, 7, 2026, 21, 59, 9)); epOn(ml, "Soul Eater", 1, 17, CCDateTime.create(23, 7, 2026, 21, 29, 0)); }),
			c("singleline-err-noseason", "[11.12.12]: Soul Eater [E06]",                  1, 0, null),
			c("singleline-err-unknown",  "[11.12.12]: NoSuchShow [S01E01]",               1, 0, null),

			// ---- episode user-rating {{score}} (single-line + multi-line) ----
			c("epscore-singleline",      "[11.12.12 19:04]: Soul Eater [S01E06] {{++}}",  0, 1, ml -> { epOn(ml, "Soul Eater", 0, 6, CCDateTime.create(11, 12, 2012, 19, 4, 0)); epScore(ml, "Soul Eater", 0, 6, CCUserScore.RATING_IV); }),
			c("epscore-singleline-uv",   "[11.12.12 19:04]: Soul Eater [S01E-07] {{-}}",  0, 1, ml -> { ep(ml, "Soul Eater", 0, 7, false); epScore(ml, "Soul Eater", 0, 7, CCUserScore.RATING_II); }),
			c("epscore-multiline-list",  "Soul Eater [S01]\n    3.3: E06, E07 {{+++}}",   0, 2, ml -> { epScore(ml, "Soul Eater", 0, 6, CCUserScore.RATING_V); epScore(ml, "Soul Eater", 0, 7, CCUserScore.RATING_V); }),
			c("epscore-multiline-first", "Soul Eater [S01]\n    E06: 3.3 {{+}}",          0, 1, ml -> { ep(ml, "Soul Eater", 0, 6, true); epScore(ml, "Soul Eater", 0, 6, CCUserScore.RATING_III); }),

			// ---- comments + whitespace ----
			c("comment-stripped",        "Hypercube    // watched it",                    0, 1, ml -> movieViewed(ml, "Hypercube", true)),
			c("blank-lines",             "\n\n   \n",                                     0, 0, null),

			// ---- error paths ----
			c("err-unknown-series",      "NoSuchSeries [S01]\n    1.1.12: E01",           2, 0, null),
			c("err-unknown-movie",       "NoSuchMovie",                                   1, 0, null),
			c("err-invalid-date",        "Steins;Gate [S01]\n    99.99.99: E01",          1, 0, null),
			c("err-content-before-hdr",  "    1.1.12: E01",                               1, 0, null),
			c("err-episode-not-found",   "Steins;Gate [S01]\n    1.1.12: E9999",          1, 0, null),
			c("err-junk-line",           "{{+}}",                                         1, 0, null),
		};
	}

	private static Object[] c(String name, String input, int errors, int changes, Consumer<CCMovieList> verify) {
		return new Object[] { name, input, errors, changes, verify };
	}

	// ---- format-independent state assertions ----

	private static CCDateTime d(String raw, String fmt) {
		return CCDateTime.parseOrDefault(raw, fmt, null);
	}

	private static CCEpisode episode(CCMovieList ml, String series, int seasonIdx, int epNum) {
		return ml.getSeries(series).getSeasonByArrayIndex(seasonIdx).getEpisodeByNumber(epNum);
	}

	private static void ep(CCMovieList ml, String series, int seasonIdx, int epNum, boolean viewed) {
		assertEquals(series + " S#" + seasonIdx + " E" + epNum + " viewed", viewed, episode(ml, series, seasonIdx, epNum).isViewed());
	}

	private static void epOn(CCMovieList ml, String series, int seasonIdx, int epNum, CCDateTime date) {
		CCEpisode e = episode(ml, series, seasonIdx, epNum);
		assertTrue(series + " S#" + seasonIdx + " E" + epNum + " viewed", e.isViewed());
		assertTrue(series + " S#" + seasonIdx + " E" + epNum + " history contains " + date.toStringISO(), e.ViewedHistory.get().contains(date));
	}

	private static void movieViewed(CCMovieList ml, String title, boolean viewed) {
		assertEquals(title + " viewed", viewed, ml.getMovie(title).isViewed());
	}

	private static void movieOn(CCMovieList ml, String title, CCDateTime date) {
		CCMovie m = ml.getMovie(title);
		assertTrue(title + " viewed", m.isViewed());
		assertTrue(title + " history contains " + date.toStringISO(), m.ViewedHistory.get().contains(date));
	}

	private static void movieScore(CCMovieList ml, String title, CCUserScore score) {
		assertEquals(title + " score", score, ml.getMovie(title).Score.get());
	}

	private static void epScore(CCMovieList ml, String series, int seasonIdx, int epNum, CCUserScore score) {
		assertEquals(series + " S#" + seasonIdx + " E" + epNum + " score", score, episode(ml, series, seasonIdx, epNum).Score.get());
	}
}
