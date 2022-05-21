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
	// ([A-Za-z].*)[ ]+\[S([0-9]+)\]
	private final static String REGEX_SERIES_HEADER = "([A-Za-z].*)[ ]+\\[S([0-9]+)\\]"; //$NON-NLS-1$
	
	//[^\[\]\{\}\n\r\t ][^\[\]\{\}\n\r]*
	private final static String REGEX_MOVIE_HEADER = "[^\\[\\]\\{\\}\\n\\r\\t ][^\\[\\]\\{\\}\\n\\r]*"; //$NON-NLS-1$

	//([^\[\]\{\}\n\r\t ][^\[\]\{\}\n\r]*)([\t ]+\{\{(\+\+\+|\+\+|\+|0|\-|\-\-|\-\-\-)\}\})?([\t ]+([0-9\.\-]+|(\[[0-9\.: \-]+\])))?
	private final static String REGEX_MOVIE_HEADER_EXT11 = "([^\\[\\]\\{\\}\\n\\r\\t ][^\\[\\]\\{\\}\\n\\r]*)([\\t ]+\\{\\{(\\+\\+\\+|\\+\\+|\\+|0|\\-|\\-\\-|\\-\\-\\-)\\}\\})?([\\t ]+([0-9\\.\\-]+|(\\[[0-9\\.: \\-]+\\])))?"; //$NON-NLS-1$

	//([^\[\]\{\}\n\r\t ][^\[\]\{\}\n\r]*)([\t ]+([0-9\.\-]+|(\[[0-9\.: \-]+\])))?([\t ]+\{\{(\+\+\+|\+\+|\+|0|\-|\-\-|\-\-\-)\}\})?
	private final static String REGEX_MOVIE_HEADER_EXT12 = "([^\\[\\]\\{\\}\\n\\r\\t ][^\\[\\]\\{\\}\\n\\r]*)([\\t ]+([0-9\\.\\-]+|(\\[[0-9\\.: \\-]+\\])))?([\\t ]+\\{\\{(\\+\\+\\+|\\+\\+|\\+|0|\\-|\\-\\-|\\-\\-\\-)\\}\\})?"; //$NON-NLS-1$

	//([0-9\.\-]+|(\[[0-9\.: \-]+\]))[\t ]*(?:\:[\t ]*|[\t ]+)([^\[\]\{\}\n\r\t ][^\[\]\{\}\n\r]*)([\t ]+\{\{(\+\+\+|\+\+|\+|0|\-|\-\-|\-\-\-)\}\})?
	private final static String REGEX_MOVIE_HEADER_EXT2 = "([0-9\\.\\-]+|(\\[[0-9\\.: \\-]+\\]))[\\t ]*(?:\\:[\\t ]*|[\\t ]+)([^\\[\\]\\{\\}\\n\\r\\t ][^\\[\\]\\{\\}\\n\\r]*)([\\t ]+\\{\\{(\\+\\+\\+|\\+\\+|\\+|0|\\-|\\-\\-|\\-\\-\\-)\\}\\})?"; //$NON-NLS-1$

	//[\t ]+([0-9\.\-]+|(\[[0-9\.: \-]+\]))[\t ]*(?:\:[\t ]*|[\t ]+)(((?:S?\-?[0-9]+)?(?:E?\-?[0-9]+))(\,[\t ]*((?:S?\-?[0-9]+)?(?:E?\-?[0-9]+)))*)
	private final static String REGEX_SERIES_CONTENT_1 = "[\\t ]+([0-9\\.\\-]+|(\\[[0-9\\.: \\-]+\\]))[\\t ]*(?:\\:[\\t ]*|[\\t ]+)(((?:S?\\-?[0-9]+)?(?:E?\\-?[0-9]+))(\\,[\\t ]*((?:S?\\-?[0-9]+)?(?:E?\\-?[0-9]+)))*)"; //$NON-NLS-1$

	//[\t ]+(((?:S?\-?[0-9]+)?(?:E?\-?[0-9]+)))[\t ]*(?:\:[\t ]*|[\t ]+)([0-9\.\-]+|(\[[0-9\.: \-]+\]))
	private final static String REGEX_SERIES_CONTENT_2 = "[\\t ]+(((?:S?\\-?[0-9]+)?(?:E?\\-?[0-9]+)))[\\t ]*(?:\\:[\\t ]*|[\\t ]+)([0-9\\.\\-]+|(\\[[0-9\\.: \\-]+\\]))"; //$NON-NLS-1$

	private final static String REGEX_CONTENT_SINGLE_1 = "E([0-9]+)"; //$NON-NLS-1$
	private final static String REGEX_CONTENT_SINGLE_2 = "([0-9]+)"; //$NON-NLS-1$
	private final static String REGEX_CONTENT_NEGATIVE_1 = "E\\-([0-9]+)"; //$NON-NLS-1$
	private final static String REGEX_CONTENT_NEGATIVE_2 = "\\-([0-9]+)"; //$NON-NLS-1$

	private final static String REGEX_CONTENT_ES_SINGLE_1 = "S([0-9]+)E([0-9]+)"; //$NON-NLS-1$
	private final static String REGEX_CONTENT_ES_NEGATIVE_1 = "S([0-9]+)E\\-([0-9]+)"; //$NON-NLS-1$

	private final static Pattern PATTERN_SERIES_HEADER = Pattern.compile(REGEX_SERIES_HEADER);
	private final static Pattern PATTERN_SERIES_CONTENT_1 = Pattern.compile(REGEX_SERIES_CONTENT_1);
	private final static Pattern PATTERN_SERIES_CONTENT_2 = Pattern.compile(REGEX_SERIES_CONTENT_2);
	private final static Pattern PATTERN_MOVIE_HEADER = Pattern.compile(REGEX_MOVIE_HEADER);
	private final static Pattern PATTERN_MOVIE_HEADER_EXT11 = Pattern.compile(REGEX_MOVIE_HEADER_EXT11);
	private final static Pattern PATTERN_MOVIE_HEADER_EXT12 = Pattern.compile(REGEX_MOVIE_HEADER_EXT12);
	private final static Pattern PATTERN_MOVIE_HEADER_EXT2 = Pattern.compile(REGEX_MOVIE_HEADER_EXT2);
	private final static Pattern PATTERN_CONTENT_SINGLE_1 = Pattern.compile(REGEX_CONTENT_SINGLE_1);
	private final static Pattern PATTERN_CONTENT_SINGLE_2 = Pattern.compile(REGEX_CONTENT_SINGLE_2);
	private final static Pattern PATTERN_CONTENT_NEGATIVE_1 = Pattern.compile(REGEX_CONTENT_NEGATIVE_1);
	private final static Pattern PATTERN_CONTENT_NEGATIVE_2 = Pattern.compile(REGEX_CONTENT_NEGATIVE_2);
	private final static Pattern PATTERN_CONTENT_ES_SINGLE_1 = Pattern.compile(REGEX_CONTENT_ES_SINGLE_1);
	private final static Pattern PATTERN_CONTENT_ES_NEGATIVE_1 = Pattern.compile(REGEX_CONTENT_ES_NEGATIVE_1);
	
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
				String title = matcher.group(1).trim();
				String seasonNumber = matcher.group(2).trim();
				
				Tuple<CCSeries, CCSeason> s = parseSeriesHeaderLine(movielist, currLine, line, title, seasonNumber, errors);
				
				if (s == null) continue;
				
				currSeason = s;
			} else if ((matcher = PATTERN_SERIES_CONTENT_1.matcher(line)).matches()) {
				String date = matcher.group(1).trim();
				String episodeList = matcher.group(3).trim();

				List<EpisodeWatchDataChangedSet> result = parseSeriesContentLine(movielist, currLine, currSeason, line, date, episodeList, errors);
				if (result != null) set.addAll(result);
			} else if ((matcher = PATTERN_SERIES_CONTENT_2.matcher(line)).matches()) {
				String date = matcher.group(3).trim();
				String episodeList = matcher.group(1).trim();

				List<EpisodeWatchDataChangedSet> result = parseSeriesContentLine(movielist, currLine, currSeason, line, date, episodeList, errors);
				if (result != null) set.addAll(result);
			} else if ((matcher = PATTERN_MOVIE_HEADER.matcher(line)).matches()) {
				String title = matcher.group().trim();
				
				Tuple<MovieWatchDataChangedSet, Tuple<CCSeries, CCSeason>> result = parseMovieHeader(movielist, currLine, line, title, errors);
				if (result == null) continue;
				
				if (result.Item1 != null) set.add(result.Item1);
				currSeason = result.Item2;
			} else if ((matcher = PATTERN_MOVIE_HEADER_EXT11.matcher(line)).matches()) {
				String title = matcher.group(1).trim();
				String score = matcher.group(2);
				String date = matcher.group(4);

				ExtendedMovieWatchDataChangedSet result = parseMovieExtHeader(movielist, currLine, line, title, score, date, errors);
				if (result == null) continue;
				set.add(result);
				currSeason = null;
			} else if ((matcher = PATTERN_MOVIE_HEADER_EXT12.matcher(line)).matches()) {
				String title = matcher.group(1).trim();
				String score = matcher.group(5);
				String date = matcher.group(2);

				ExtendedMovieWatchDataChangedSet result = parseMovieExtHeader(movielist, currLine, line, title, score, date, errors);
				if (result == null) continue;
				set.add(result);
				currSeason = null;
			} else if ((matcher = PATTERN_MOVIE_HEADER_EXT2.matcher(line)).matches()) {
				String title = matcher.group(3).trim();
				String score = matcher.group(4);
				String date = matcher.group(1).trim();
				
				ExtendedMovieWatchDataChangedSet result = parseMovieExtHeader(movielist, currLine, line, title, score, date, errors);
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
		CCSeries s = null;
		
		for (CCSeries curr : movielist.iteratorSeries()) {
			if (curr.getTitle().equalsIgnoreCase(title)) {
				s = curr;
				break;
			}
		}
		
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
	private static List<EpisodeWatchDataChangedSet> parseSeriesContentLine(CCMovieList movielist, int currLine, Tuple<CCSeries, CCSeason> currSeason, String line, String date, String episodeList, List<String> errors) {
		if (date.startsWith("[") && date.endsWith("]")) date = date.substring(1, date.length()-1);
		
		if (currSeason == null) {
			errors.add(String.format("Line[%d] \"%s\" : Missing series header token before this Line", currLine, line.trim()));
			return new ArrayList<>();
		}
		
		CCDateTime d = CCDateTime.parseOrDefault(date, "d.M.y", null);
		
		if (d == null) d = CCDateTime.parseOrDefault(date, "d.M", null);
		if (d == null) d = CCDateTime.parseOrDefault(date, "d.M.y H:m", null);
		if (d == null) d = CCDateTime.parseOrDefault(date, "d.M.y H:m:s", null);

		if (d == null) d = CCDateTime.parseOrDefault(date, "y-M-d", null);
		if (d == null) d = CCDateTime.parseOrDefault(date, "y-M-d H:m", null);
		if (d == null) d = CCDateTime.parseOrDefault(date, "y-M-d H:m:s", null);

		if (d == null || ! d.isValidDateTime()) {
			errors.add(String.format("Line[%d] \"%s\" : Date \"%s\" is no valid Date", currLine, line.trim(), date));
			return new ArrayList<>();
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
					range_min = Integer.parseInt(content_matcher.group(1));
					range_max = range_min;
				} else if ((content_matcher = PATTERN_CONTENT_SINGLE_2.matcher(e)).matches()) {
					range_viewed = true;
					explicit_season = -1;
					range_min = Integer.parseInt(content_matcher.group(1));
					range_max = range_min;
				} else if ((content_matcher = PATTERN_CONTENT_NEGATIVE_1.matcher(e)).matches()) {
					range_viewed = false;
					explicit_season = -1;
					range_min = Integer.parseInt(content_matcher.group(1));
					range_max = range_min;
				} else if ((content_matcher = PATTERN_CONTENT_NEGATIVE_2.matcher(e)).matches()) {
					range_viewed = false;
					explicit_season = -1;
					range_min = Integer.parseInt(content_matcher.group(1));
					range_max = range_min;
				} else if ((content_matcher = PATTERN_CONTENT_ES_SINGLE_1.matcher(e)).matches()) {
					range_viewed = true;
					explicit_season = Integer.parseInt(content_matcher.group(1));
					range_min = Integer.parseInt(content_matcher.group(2));
					range_max = range_min;
				} else if ((content_matcher = PATTERN_CONTENT_ES_NEGATIVE_1.matcher(e)).matches()) {
					range_viewed = false;
					explicit_season = Integer.parseInt(content_matcher.group(1));
					range_min = Integer.parseInt(content_matcher.group(2));
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
				
				result.add(new EpisodeWatchDataChangedSet(d, epis, range_viewed));
			}
		}
		
		return result;
	}

	@SuppressWarnings("nls")
	private static Tuple<MovieWatchDataChangedSet, Tuple<CCSeries, CCSeason>> parseMovieHeader(CCMovieList movielist, int currLine, String line, String title, List<String> errors) {
		CCSeries s = null;
		CCMovie m = null;
		
		for (CCSeries curr : movielist.iteratorSeries()) {
			if (curr.getTitle().equalsIgnoreCase(title)) {
				s = curr;
				break;
			}
		}
		
		for (CCMovie curr : movielist.iteratorMovies()) {
			if (curr.getTitle().equalsIgnoreCase(title) || curr.getCompleteTitle().equalsIgnoreCase(title) || curr.Zyklus.get().getFormatted().equalsIgnoreCase(title) || curr.Zyklus.get().getDecimalFormatted().equalsIgnoreCase(title)) {
				
				if (m != null) {
					errors.add(String.format("Line[%d] \"%s\" : Movie \"%s\" has more then 1 results in database", currLine, line.trim(), title));
					continue;
				}
				
				m = curr;
			}
		}
		
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
		
		if (m == null) {
			errors.add(String.format("Line[%d] \"%s\" : Movie \"%s\" not found", currLine, line.trim(), title));
			return null;
		}
		
		CCUserScore rscore = null;
		
		if (score != null && !score.trim().isEmpty()) {
			score = score.trim();
			score = score.substring(2, score.length() - 2);
			
			if (score.equals("+++")) rscore = CCUserScore.RATING_V;
			if (score.equals("++")) rscore = CCUserScore.RATING_IV;
			if (score.equals("+")) rscore = CCUserScore.RATING_III;
			if (score.equals("0")) rscore = CCUserScore.RATING_MID;
			if (score.equals("-")) rscore = CCUserScore.RATING_II;
			if (score.equals("--")) rscore = CCUserScore.RATING_I;
			if (score.equals("---")) rscore = CCUserScore.RATING_0;
			
			if (rscore == null) {
				errors.add(String.format("Line[%d] \"%s\" : Score \"%s\" has an invalid value", currLine, line.trim(), score));
				return null;
			}
		}
		
		CCDateTime rdate = null;
		
		if (date != null && !date.trim().isEmpty()) {
			date = date.trim();
			
			if (date.startsWith("[") && date.endsWith("]")) date = date.substring(1, date.length()-1);
			
			rdate = CCDateTime.parseOrDefault(date, "d.M.y", null);

			if (rdate == null) rdate = CCDateTime.parseOrDefault(date, "d.M", null);
			if (rdate == null) rdate = CCDateTime.parseOrDefault(date, "d.M.y H:m", null);
			if (rdate == null) rdate = CCDateTime.parseOrDefault(date, "d.M.y H:m:s", null);

			if (rdate == null) rdate = CCDateTime.parseOrDefault(date, "y-M-d", null);
			if (rdate == null) rdate = CCDateTime.parseOrDefault(date, "y-M-d H:m", null);
			if (rdate == null) rdate = CCDateTime.parseOrDefault(date, "y-M-d H:m:s", null);

			if (rdate == null || ! rdate.isValidDateTime()) {
				errors.add(String.format("Line[%d] \"%s\" : Date \"%s\" is no valid Date", currLine, line.trim(), date));
				return null;
			}
		}
		
		return new ExtendedMovieWatchDataChangedSet(rdate, rscore, m, true);
	}
}
