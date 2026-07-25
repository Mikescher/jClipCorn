package de.jClipCorn.util.parser.watchdata;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDateTime;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WatchDataParser {

	// ================================================================================================================
	// Shared regex building-blocks (each defines a NAMED capture-group so field extraction is index-independent).
	// The same group-name may recur across different patterns - every Pattern is compiled on its own, so as long as
	// a name is used at most once *within* a single pattern there is no collision.
	// ================================================================================================================

	// a date, either bare (de_de / ISO) or wrapped in [ ... ] - e.g. "11.12.12", "2016-12-11 19:04:33", "[11.12.16 19:04]"
	private final static String A_DATE = "(?<date>[0-9\\.\\-]+|\\[[0-9\\.: \\-]+\\])"; //$NON-NLS-1$

	// a movie/series title - may contain spaces and ':' but no brackets/braces/newlines
	private final static String A_TITLE = "(?<title>[^\\[\\]\\{\\}\\n\\r\\t ][^\\[\\]\\{\\}\\n\\r]*)"; //$NON-NLS-1$

	// a custom-score token inside {{ ... }} - e.g. "{{++}}"
	private final static String A_SCORE = "\\{\\{(?<score>\\+\\+\\+|\\+\\+|\\+|0|\\-|\\-\\-|\\-\\-\\-)\\}\\}"; //$NON-NLS-1$

	// separator between date and content: a colon (optional surrounding whitespace) OR plain whitespace
	private final static String SEP = "[\\t ]*(?:\\:[\\t ]*|[\\t ]+)"; //$NON-NLS-1$

	// a single episode-token: optional season, optional 'E', optional negative-marker - e.g. "E01", "12", "S2E18", "S1E-07"
	private final static String EP_TOKEN = "(?:S?\\-?[0-9]+)?(?:E?\\-?[0-9]+)"; //$NON-NLS-1$

	// a comma-separated list of episode-tokens
	private final static String A_EPISODES = "(?<episodes>" + EP_TOKEN + "(?:\\,[\\t ]*" + EP_TOKEN + ")*)"; //$NON-NLS-1$ //$NON-NLS-2$

	// ================================================================================================================
	// Line patterns (matched full-line, first hit wins - see parse())
	// ================================================================================================================

	// SerienName [S01]
	private final static Pattern PATTERN_SERIES_HEADER = Pattern.compile("(?<title>[A-Za-z].*)[ ]+\\[S(?<season>[0-9]+)\\]"); //$NON-NLS-1$

	// <indent> date : ep, ep, ep {{score}}?   (optional trailing comma, optional score applied to all episodes)
	private final static Pattern PATTERN_SERIES_CONTENT_1 = Pattern.compile("[\\t ]+" + A_DATE + SEP + A_EPISODES + "(?:[\\t ]*\\,)?(?:[\\t ]+" + A_SCORE + ")?"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	// <indent> ep : date {{score}}?
	private final static Pattern PATTERN_SERIES_CONTENT_2 = Pattern.compile("[\\t ]+(?<episodes>" + EP_TOKEN + ")" + SEP + A_DATE + "(?:[\\t ]+" + A_SCORE + ")?"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	// date : SerienName [SxxExx] {{score}}?   (single line, colon optional) - e.g. "[2026-07-22 21:59:09]: My Show [S01E07]"
	private final static Pattern PATTERN_SERIES_SINGLELINE = Pattern.compile(A_DATE + SEP + A_TITLE + "\\[(?<episodes>" + EP_TOKEN + ")\\](?:[\\t ]+" + A_SCORE + ")?"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	// MovieName
	private final static Pattern PATTERN_MOVIE_HEADER = Pattern.compile(A_TITLE);

	// MovieName {{score}}? date?
	private final static Pattern PATTERN_MOVIE_HEADER_EXT11 = Pattern.compile(A_TITLE + "(?:[\\t ]+" + A_SCORE + ")?(?:[\\t ]+" + A_DATE + ")?"); //$NON-NLS-1$ //$NON-NLS-2$

	// MovieName date? {{score}}?
	private final static Pattern PATTERN_MOVIE_HEADER_EXT12 = Pattern.compile(A_TITLE + "(?:[\\t ]+" + A_DATE + ")?(?:[\\t ]+" + A_SCORE + ")?"); //$NON-NLS-1$ //$NON-NLS-2$

	// date : MovieName {{score}}?
	private final static Pattern PATTERN_MOVIE_HEADER_EXT2 = Pattern.compile(A_DATE + SEP + A_TITLE + "(?:[\\t ]+" + A_SCORE + ")?"); //$NON-NLS-1$

	// ================================================================================================================
	// Single episode-token patterns (matched against one element of an episode-list)
	// ================================================================================================================

	private final static Pattern PATTERN_CONTENT_SINGLE_1      = Pattern.compile("E(?<num>[0-9]+)"); //$NON-NLS-1$
	private final static Pattern PATTERN_CONTENT_SINGLE_2      = Pattern.compile("(?<num>[0-9]+)"); //$NON-NLS-1$
	private final static Pattern PATTERN_CONTENT_NEGATIVE_1    = Pattern.compile("E\\-(?<num>[0-9]+)"); //$NON-NLS-1$
	private final static Pattern PATTERN_CONTENT_NEGATIVE_2    = Pattern.compile("\\-(?<num>[0-9]+)"); //$NON-NLS-1$
	private final static Pattern PATTERN_CONTENT_ES_SINGLE_1   = Pattern.compile("S(?<season>[0-9]+)E(?<num>[0-9]+)"); //$NON-NLS-1$
	private final static Pattern PATTERN_CONTENT_ES_NEGATIVE_1 = Pattern.compile("S(?<season>[0-9]+)E\\-(?<num>[0-9]+)"); //$NON-NLS-1$

	@SuppressWarnings("nls")
	public static List<WatchDataChangeSet> parse(CCMovieList movielist, String content, List<String> errors) {
		List<WatchDataChangeSet> set = new ArrayList<>();

		String[] lines = content.split("\\r?\\n");

		Tuple<CCSeries, CCSeason> currSeason = null;

		for (int currLine = 0; currLine < lines.length; currLine++) {
			String line = StringUtils.stripEnd(lines[currLine], null);

			if (line.contains("//")) {
				line = line.substring(0, line.indexOf("//"));
			}

			line = StringUtils.stripEnd(line, null);

			if (line.trim().isEmpty()) continue;

			Matcher matcher;

			if ((matcher = PATTERN_SERIES_HEADER.matcher(line)).matches()) {
				String title = matcher.group("title").trim();
				String seasonNumber = matcher.group("season").trim();

				Tuple<CCSeries, CCSeason> s = parseSeriesHeaderLine(movielist, currLine, line, title, seasonNumber, errors);

				if (s == null) continue;

				currSeason = s;
			} else if ((matcher = PATTERN_SERIES_CONTENT_1.matcher(line)).matches()) {
				String date = matcher.group("date").trim();
				String episodeList = matcher.group("episodes").trim();
				String score = matcher.group("score");

				List<EpisodeWatchDataChangedSet> result = parseSeriesContentLine(movielist, currLine, currSeason, line, date, episodeList, score, errors);
				if (result != null) set.addAll(result);
			} else if ((matcher = PATTERN_SERIES_CONTENT_2.matcher(line)).matches()) {
				String date = matcher.group("date").trim();
				String episodeList = matcher.group("episodes").trim();
				String score = matcher.group("score");

				List<EpisodeWatchDataChangedSet> result = parseSeriesContentLine(movielist, currLine, currSeason, line, date, episodeList, score, errors);
				if (result != null) set.addAll(result);
			} else if ((matcher = PATTERN_SERIES_SINGLELINE.matcher(line)).matches()) {
				String date = matcher.group("date").trim();
				String title = matcher.group("title").trim();
				String episodeList = matcher.group("episodes").trim();
				String score = matcher.group("score");

				List<EpisodeWatchDataChangedSet> result = parseSeriesSingleLine(movielist, currLine, line, title, date, episodeList, score, errors);
				if (result != null) set.addAll(result);
				currSeason = null;
			} else if ((matcher = PATTERN_MOVIE_HEADER.matcher(line)).matches()) {
				String title = matcher.group("title").trim();

				Tuple<MovieWatchDataChangedSet, Tuple<CCSeries, CCSeason>> result = parseMovieHeader(movielist, currLine, line, title, errors);
				if (result == null) continue;

				if (result.Item1 != null) set.add(result.Item1);
				currSeason = result.Item2;
			} else if ((matcher = PATTERN_MOVIE_HEADER_EXT11.matcher(line)).matches()) {
				ExtendedMovieWatchDataChangedSet result = parseMovieExtHeader(movielist, currLine, line, matcher.group("title").trim(), matcher.group("score"), matcher.group("date"), errors);
				if (result == null) continue;
				set.add(result);
				currSeason = null;
			} else if ((matcher = PATTERN_MOVIE_HEADER_EXT12.matcher(line)).matches()) {
				ExtendedMovieWatchDataChangedSet result = parseMovieExtHeader(movielist, currLine, line, matcher.group("title").trim(), matcher.group("score"), matcher.group("date"), errors);
				if (result == null) continue;
				set.add(result);
				currSeason = null;
			} else if ((matcher = PATTERN_MOVIE_HEADER_EXT2.matcher(line)).matches()) {
				ExtendedMovieWatchDataChangedSet result = parseMovieExtHeader(movielist, currLine, line, matcher.group("title").trim(), matcher.group("score"), matcher.group("date").trim(), errors);
				if (result == null) continue;
				set.add(result);
				currSeason = null;
			} else {
				errors.add(String.format("Line[%d] \"%s\" : Cannot parse the content of this line", currLine, line.trim()));
			}
		}

		return set;
	}

	@SuppressWarnings("nls")
	private static Tuple<CCSeries, CCSeason> parseSeriesHeaderLine(CCMovieList movielist, int currLine, String line, String title, String seasonNumber, List<String> errors) {
		CCSeries s = findSeriesByTitle(movielist, title);

		if (s == null) {
			errors.add(String.format("Line[%d] \"%s\" : Series \"%s\" not found", currLine, line.trim(), title));
			return null;
		}

		int sn = Integer.parseInt(seasonNumber) - 1;

		if (sn < 0 || sn >= s.getSeasonCount()) {
			errors.add(String.format("Line[%d] \"%s\" : Series \"%s\" has no Season with number %s (%d)", currLine, line.trim(), title, seasonNumber, sn));
			return null;
		}

		return Tuple.Create(s, s.getSeasonByArrayIndex(sn));
	}

	@SuppressWarnings("nls")
	private static List<EpisodeWatchDataChangedSet> parseSeriesSingleLine(CCMovieList movielist, int currLine, String line, String title, String date, String episodeList, String score, List<String> errors) {
		CCSeries s = findSeriesByTitle(movielist, title);

		if (s == null) {
			errors.add(String.format("Line[%d] \"%s\" : Series \"%s\" not found", currLine, line.trim(), title));
			return null;
		}

		// no season header on a single-line entry - only fall back to the first season if the series has exactly one
		CCSeason sea = (s.getSeasonCount() == 1) ? s.getSeasonByArrayIndex(0) : null;

		return parseSeriesContentLine(movielist, currLine, Tuple.Create(s, sea), line, date, episodeList, score, errors);
	}

	@SuppressWarnings("nls")
	private static List<EpisodeWatchDataChangedSet> parseSeriesContentLine(CCMovieList movielist, int currLine, Tuple<CCSeries, CCSeason> currSeason, String line, String date, String episodeList, String score, List<String> errors) {
		if (currSeason == null) {
			errors.add(String.format("Line[%d] \"%s\" : Missing series header token before this Line", currLine, line.trim()));
			return new ArrayList<>();
		}

		CCDateTime d = parseWatchDate(date);

		if (d == null) {
			errors.add(String.format("Line[%d] \"%s\" : Date \"%s\" is no valid Date", currLine, line.trim(), date));
			return new ArrayList<>();
		}

		CCUserScore rscore = null;

		if (score != null && !score.trim().isEmpty()) {
			rscore = parseScore(score.trim());

			if (rscore == null) {
				errors.add(String.format("Line[%d] \"%s\" : Score \"%s\" has an invalid value", currLine, line.trim(), score));
				return new ArrayList<>();
			}
		}

		String[] episodesarr = episodeList.split(",");

		List<EpisodeWatchDataChangedSet> result = new ArrayList<>();

		for (String ep : episodesarr) {
			String e = ep.trim();
			if (e.isEmpty()) continue;

			boolean range_viewed;

			int range_min;
			int range_max;
			int explicit_season;

			try {
				Matcher content_matcher;
				if ((content_matcher = PATTERN_CONTENT_SINGLE_1.matcher(e)).matches()) {
					range_viewed = true;
					explicit_season = -1;
					range_min = Integer.parseInt(content_matcher.group("num"));
					range_max = range_min;
				} else if ((content_matcher = PATTERN_CONTENT_SINGLE_2.matcher(e)).matches()) {
					range_viewed = true;
					explicit_season = -1;
					range_min = Integer.parseInt(content_matcher.group("num"));
					range_max = range_min;
				} else if ((content_matcher = PATTERN_CONTENT_NEGATIVE_1.matcher(e)).matches()) {
					range_viewed = false;
					explicit_season = -1;
					range_min = Integer.parseInt(content_matcher.group("num"));
					range_max = range_min;
				} else if ((content_matcher = PATTERN_CONTENT_NEGATIVE_2.matcher(e)).matches()) {
					range_viewed = false;
					explicit_season = -1;
					range_min = Integer.parseInt(content_matcher.group("num"));
					range_max = range_min;
				} else if ((content_matcher = PATTERN_CONTENT_ES_SINGLE_1.matcher(e)).matches()) {
					range_viewed = true;
					explicit_season = Integer.parseInt(content_matcher.group("season"));
					range_min = Integer.parseInt(content_matcher.group("num"));
					range_max = range_min;
				} else if ((content_matcher = PATTERN_CONTENT_ES_NEGATIVE_1.matcher(e)).matches()) {
					range_viewed = false;
					explicit_season = Integer.parseInt(content_matcher.group("season"));
					range_min = Integer.parseInt(content_matcher.group("num"));
					range_max = range_min;
				} else {
					errors.add(String.format("Line[%d] \"%s\" : The Episode \"%s\" could not be parsed (No RegEx)", currLine, line.trim(), e));
					continue;
				}
			} catch (NumberFormatException ex2) {
				errors.add(String.format("Line[%d] \"%s\" : The Episode \"%s\" could not be parsed (RangeFormat)", currLine, line.trim(), e));
				continue;
			}

			for (int en = range_min; en <= range_max; en++) {

				if (currSeason.Item2 == null && explicit_season <= 0) {
					errors.add(String.format("Line[%d] \"%s\" : The Episode \"%s\" needs to have a season-index (or the series header needs one)", currLine, line.trim(), en));
					continue;
				}

				CCSeason season;

				if (explicit_season > 0) {
					season = currSeason.Item1.getSeasonByArrayIndex(explicit_season - 1);
					if (season == null) {
						errors.add(String.format("Line[%d] \"%s\" : Series \"%s\" has no Season with number %d", currLine, line.trim(), currSeason.Item1.getSeries().Title.get(), (explicit_season-1)));
						return null;
					}
				} else /* if (currSeason.Item2 != null) */ {
					season = currSeason.Item2;
				}

				CCEpisode epis = season.getEpisodeByNumber(en);

				if (epis == null) {
					errors.add(String.format("Line[%d] \"%s\" : The Episode \"%s\" could not be found in the current Season", currLine, line.trim(), en));
					continue;
				}

				result.add(new EpisodeWatchDataChangedSet(d, epis, range_viewed, rscore));
			}
		}

		return result;
	}

	@SuppressWarnings("nls")
	private static Tuple<MovieWatchDataChangedSet, Tuple<CCSeries, CCSeason>> parseMovieHeader(CCMovieList movielist, int currLine, String line, String title, List<String> errors) {
		CCSeries s = findSeriesByTitle(movielist, title);
		CCMovie m = findMovieByTitle(movielist, currLine, line, title, errors);

		if (s == null && m == null) {
			errors.add(String.format("Line[%d] \"%s\" : Movie/Series \"%s\" not found", currLine, line.trim(), title));
			return null;
		}

		if (s != null && m != null) {
			errors.add(String.format("Line[%d] \"%s\" : Movie/Series \"%s\" is found more than one time", currLine, line.trim(), title));
			return null;
		}

		if (m != null) {
			return new Tuple<>(new MovieWatchDataChangedSet(m, true), null);
		} else if (s != null) {
			if (s.getSeasonCount() != 1) {
				return new Tuple<>(null, Tuple.Create(s, null));
			}

			return new Tuple<>(null, Tuple.Create(s, s.getSeasonByArrayIndex(0)));
		}

		return null;
	}

	@SuppressWarnings("nls")
	private static ExtendedMovieWatchDataChangedSet parseMovieExtHeader(CCMovieList movielist, int currLine, String line, String title, String score, String date, List<String> errors) {
		CCMovie m = findMovieByTitle(movielist, currLine, line, title, errors);

		if (m == null) {
			errors.add(String.format("Line[%d] \"%s\" : Movie \"%s\" not found", currLine, line.trim(), title));
			return null;
		}

		CCUserScore rscore = null;

		if (score != null && !score.trim().isEmpty()) {
			rscore = parseScore(score.trim());

			if (rscore == null) {
				errors.add(String.format("Line[%d] \"%s\" : Score \"%s\" has an invalid value", currLine, line.trim(), score));
				return null;
			}
		}

		CCDateTime rdate = null;

		if (date != null && !date.trim().isEmpty()) {
			rdate = parseWatchDate(date);

			if (rdate == null) {
				errors.add(String.format("Line[%d] \"%s\" : Date \"%s\" is no valid Date", currLine, line.trim(), date));
				return null;
			}
		}

		return new ExtendedMovieWatchDataChangedSet(rdate, rscore, m, true);
	}

	// ================================================================================================================
	// Helpers (shared lookups + value-parsers, extracted from the branch handlers above)
	// ================================================================================================================

	private static CCSeries findSeriesByTitle(CCMovieList movielist, String title) {
		for (CCSeries curr : movielist.iteratorSeries()) {
			if (curr.getTitle().equalsIgnoreCase(title)) return curr;
		}
		return null;
	}

	@SuppressWarnings("nls")
	private static CCMovie findMovieByTitle(CCMovieList movielist, int currLine, String line, String title, List<String> errors) {
		CCMovie m = null;

		for (CCMovie curr : movielist.iteratorMovies()) {
			if (curr.getTitle().equalsIgnoreCase(title) || curr.getCompleteTitle().equalsIgnoreCase(title) || curr.Zyklus.get().getFormatted().equalsIgnoreCase(title) || curr.Zyklus.get().getDecimalFormatted().equalsIgnoreCase(title)) {

				if (m != null) {
					errors.add(String.format("Line[%d] \"%s\" : Movie \"%s\" has more then 1 results in database", currLine, line.trim(), title));
					continue;
				}

				m = curr;
			}
		}

		return m;
	}

	@SuppressWarnings("nls")
	private static CCUserScore parseScore(String score) {
		switch (score) {
			case "+++": return CCUserScore.RATING_V;
			case "++":  return CCUserScore.RATING_IV;
			case "+":   return CCUserScore.RATING_III;
			case "0":   return CCUserScore.RATING_MID;
			case "-":   return CCUserScore.RATING_II;
			case "--":  return CCUserScore.RATING_I;
			case "---": return CCUserScore.RATING_0;
			default:    return null;
		}
	}

	@SuppressWarnings("nls")
	private static CCDateTime parseWatchDate(String date) {
		date = date.trim();

		if (date.startsWith("[") && date.endsWith("]")) date = date.substring(1, date.length() - 1);

		CCDateTime d = CCDateTime.parseOrDefault(date, "d.M.y", null);

		if (d == null) d = CCDateTime.parseOrDefault(date, "d.M", null);
		if (d == null) d = CCDateTime.parseOrDefault(date, "d.M.y H:m", null);
		if (d == null) d = CCDateTime.parseOrDefault(date, "d.M.y H:m:s", null);

		if (d == null) d = CCDateTime.parseOrDefault(date, "y-M-d", null);
		if (d == null) d = CCDateTime.parseOrDefault(date, "y-M-d H:m", null);
		if (d == null) d = CCDateTime.parseOrDefault(date, "y-M-d H:m:s", null);

		if (d == null || !d.isValidDateTime()) return null;

		return d;
	}
}
