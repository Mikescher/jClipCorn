package de.jClipCorn.util.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieOnlineScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTags;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;
import de.jClipCorn.util.SortableTuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.datetime.CCDatespan;

public class StatisticsHelper {
	public static int[] CHART_COLORS = {0x4D4D4D, 0x5DA5DA, 0xFAA43A, 0x60BD68, 0xF17CB0, 0xB2912F, 0xB276B2, 0x000080, 0xF15854};
	
	public static int getViewedMovieCount(CCMovieList ml) {
		int c = 0;

		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			c += it.next().isViewed() ? 1 : 0;
		}

		return c;
	}

	public static int getViewedEpisodeCount(CCMovieList ml) {
		int c = 0;

		for (Iterator<CCSeries> it = ml.iteratorSeries(); it.hasNext();) {
			CCSeries series = it.next();
			for (int s = 0; s < series.getSeasonCount(); s++) {
				CCSeason season = series.getSeason(s);
				for (int e = 0; e < season.getEpisodeCount(); e++) {
					c += season.getEpisode(e).isViewed() ? 1 : 0;
				}
			}
		}

		return c;
	}

	public static int getMovieDuration(CCMovieList ml) {
		int c = 0;

		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			c += it.next().getLength();
		}

		return c;
	}

	public static int getSeriesDuration(CCMovieList ml) {
		int c = 0;

		for (Iterator<CCSeries> it = ml.iteratorSeries(); it.hasNext();) {
			c += it.next().getLength();
		}

		return c;
	}

	public static int getTotalDuration(CCMovieList ml) {
		return getMovieDuration(ml) + getSeriesDuration(ml);
	}

	public static CCMovieSize getMovieSize(CCMovieList ml) {
		CCMovieSize s = new CCMovieSize();

		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			s.add(it.next().getFilesize());
		}

		return s;
	}

	public static CCMovieSize getSeriesSize(CCMovieList ml) {
		CCMovieSize s = new CCMovieSize();

		for (Iterator<CCSeries> it = ml.iteratorSeries(); it.hasNext();) {
			s.add(it.next().getFilesize());
		}

		return s;
	}

	public static CCMovieSize getTotalSize(CCMovieList ml) {
		CCMovieSize s = new CCMovieSize();

		for (Iterator<CCDatabaseElement> it = ml.iterator(); it.hasNext();) {
			s.add(it.next().getFilesize());
		}

		return s;
	}

	public static CCMovieSize getAvgMovieSize(CCMovieList ml) {
		CCMovieSize s = new CCMovieSize();

		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			s.add(it.next().getFilesize());
		}

		int mc = ml.getEpisodeCount();
		
		if (mc == 0)
			return new CCMovieSize(0);
		
		s.div(mc);

		return s;
	}

	public static CCMovieSize getAvgSeriesSize(CCMovieList ml) {
		CCMovieSize s = new CCMovieSize();

		for (Iterator<CCSeries> it = ml.iteratorSeries(); it.hasNext();) {
			s.add(it.next().getFilesize());
		}

		int ec = ml.getEpisodeCount();
		
		if (ec == 0)
			return new CCMovieSize(0);
		
		s.div(ec);

		return s;
	}

	public static double getAvgImDbRating(CCMovieList ml) {
		int c = 0;

		for (Iterator<CCDatabaseElement> it = ml.iterator(); it.hasNext();) {
			c += it.next().getOnlinescore().asInt();
		}

		return ((int) ((c / (ml.getElementCount() * 1d)) * 5)) / 10d;
	}

	public static int getViewedMovieDuration(CCMovieList ml) {
		int c = 0;

		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
			if (m.isViewed()) {
				c += m.getLength();
			}
		}

		return c;
	}

	public static int getViewedSeriesDuration(CCMovieList ml) {
		int c = 0;

		for (Iterator<CCSeries> it = ml.iteratorSeries(); it.hasNext();) {
			CCSeries s = it.next();
			if (s.isViewed()) {
				c += s.getLength();
			}
		}

		return c;
	}

	public static int getViewedTotalDuration(CCMovieList ml) {
		return getViewedMovieDuration(ml) + getViewedSeriesDuration(ml);
	}
	
	public static CCDate getFirstMovieAddDate(CCMovieList ml) {
		CCDate date = CCDate.getMaximumDate();
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
			if (m.getAddDate().isLessThan(date)) {
				date = m.getAddDate();
			}
		}
		
		return date;
	}
	
	public static CCDate getLastMovieAddDate(CCMovieList ml) {
		CCDate date = CCDate.getMinimumDate();
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
			if (m.getAddDate().isGreaterThan(date)) {
				date = m.getAddDate();
			}
		}
		
		return date;
	}
	
	public static List<Integer> getMovieCountForAllDates(CCMovieList ml, CCDate startDate, int count) {
		List<Integer> ls = new ArrayList<>();
		
		for (int i = 0; i < count; i++) {
			ls.add(0);
		}
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
			
			int pos = startDate.getDayDifferenceTo(m.getAddDate());
			
			if (pos >= 0 && pos < count) {
				int prev = ls.get(pos) + 1;
				ls.set(pos, prev);
			}
		}
		
		return ls;
	}
	
	public static int getMinimumMovieLength(CCMovieList ml) {
		int len = Integer.MAX_VALUE;
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
		
			if (m.getLength() < len) {
				len = m.getLength();
			}
		}
		
		return len;
	}
	
	public static int getMaximumMovieLength(CCMovieList ml) {
		int len = 0;
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
		
			if (m.getLength() > len) {
				len = m.getLength();
			}
		}
		
		return len;
	}
	
	public static double[][] getMovieCountForAllLengths(CCMovieList ml, int minLength, int count) {
		double[][] result = new double[2][count+2];
		
		for (int i = 0; i < (count+2); i++) {
			result[0][i] = minLength + i;
			result[1][i] = 0;
		}
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
		
			result[1][m.getLength() - minLength + 1]++;
		}
		
		return result;
	}
	
	public static int[] getMovieCountForAllFormats(CCMovieList ml) {
		int[] result = new int[CCMovieFormat.values().length];
		
		for (int i = 0; i < CCMovieFormat.values().length; i++) {
			result[i] = 0;
		}
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
		
			result[m.getFormat().asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getElementCountForAllProvider(CCMovieList ml) {
		int[] result = new int[CCOnlineRefType.values().length];
		
		for (int i = 0; i < CCOnlineRefType.values().length; i++) {
			result[i] = 0;
		}
		
		for (Iterator<CCDatabaseElement> it = ml.iterator(); it.hasNext();) {
			CCDatabaseElement m = it.next();
		
			result[m.getOnlineReference().type.asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getMovieCountForAllQualities(CCMovieList ml) {
		int[] result = new int[CCMovieQuality.values().length];
		
		for (int i = 0; i < CCMovieQuality.values().length; i++) {
			result[i] = 0;
		}
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
		
			result[m.getQuality().asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getMovieCountForAllOnlinescores(CCMovieList ml) {
		int[] result = new int[CCMovieOnlineScore.values().length];
		
		for (int i = 0; i < CCMovieOnlineScore.values().length; i++) {
			result[i] = 0;
		}
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
		
			result[m.getOnlinescore().asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getMovieCountForAllScores(CCMovieList ml) {
		int[] result = new int[CCMovieScore.values().length - 1];
		
		for (int i = 0; i < (CCMovieScore.values().length - 1); i++) {
			result[i] = 0;
		}
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
		
			if (m.getScore() != CCMovieScore.RATING_NO) {
				result[m.getScore().asInt()]++;
			}
		}
		
		return result;
	}
	
	public static int getMovieViewedCount(CCMovieList ml) {
		int v = 0;
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
			if (m.isViewed()) {
				v++;
			}
		}
		return v;
	}
	
	public static int getMovieUnviewedCount(CCMovieList ml) {
		int v = 0;
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
			if (! m.isViewed()) {
				v++;
			}
		}
		return v;
	}
	
	public static int getMinimumMovieYear(CCMovieList ml) {
		int y = Integer.MAX_VALUE;
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
		
			if (m.getYear() < y) {
				y = m.getYear();
			}
		}
		
		return y;
	}
	
	public static int getMaximumMovieYear(CCMovieList ml) {
		int y = 0;
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
		
			if (m.getYear() > y) {
				y = m.getYear();
			}
		}
		
		return y;
	}
	
	public static int[] getMovieCountForAllYears(CCMovieList ml, int minYear, int count) {
		int[] result = new int[count];
		
		for (int i = 0; i < count; i++) {
			result[i] = 0;
		}
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
		
			result[m.getYear() - minYear]++;
		}
		
		return result;
	}
	
	public static int[] getMovieCountForAllGenres(CCMovieList ml) {
		int[] result = new int[CCMovieGenre.values().length];
		
		for (int i = 0; i < CCMovieGenre.values().length; i++) {
			result[i] = 0;
		}
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
			
			for (int gi = 0; gi < m.getGenreCount(); gi++) {
				result[m.getGenre(gi).asInt()]++;
			}
		}
		
		return result;
	}
	
	public static int[] getMovieCountForAllFSKs(CCMovieList ml) {
		int[] result = new int[CCMovieFSK.values().length];
		
		for (int i = 0; i < CCMovieFSK.values().length; i++) {
			result[i] = 0;
		}
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
			
			result[m.getFSK().asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getMovieCountForAllLanguages(CCMovieList ml) {
		int[] result = new int[CCMovieLanguage.values().length];
		
		for (int i = 0; i < CCMovieLanguage.values().length; i++) {
			result[i] = 0;
		}
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
			
			result[m.getLanguage().asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getMovieCountForAllTags(CCMovieList ml) {
		int[] result = new int[CCMovieTags.ACTIVETAGS];
		
		for (int i = 0; i < CCMovieTags.ACTIVETAGS; i++) {
			result[i] = 0;
		}
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
			
			for (int i = 0; i < CCMovieTags.ACTIVETAGS; i++) {
				 if (m.getTag(i)) {
					 result[i]++;
				 }
			}
		}
		
		return result;
	}
	
	public static int[] getAddedMinuteCountForAllDates(CCMovieList ml, CCDate startDate, int count) {
		int[] ls = new int[count];
		
		for (int i = 0; i < count; i++) {
			ls[i] = 0;
		}
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
			
			int pos = startDate.getDayDifferenceTo(m.getAddDate());
			
			ls[pos] += m.getLength();
		}
		
		return ls;
	}
	
	public static int[] getAddedSeriesCountForAllDates(CCMovieList ml, CCDate startDate, int count) {
		int[] ls = new int[count];
		
		for (int i = 0; i < count; i++) {
			ls[i] = 0;
		}
		
		for (Iterator<CCSeries> it = ml.iteratorSeries(); it.hasNext();) {
			CCSeries series = it.next();
			
			for (int sea = 0; sea < series.getSeasonCount(); sea++) {
				CCSeason season = series.getSeason(sea);
				
				for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
					CCEpisode episode = season.getEpisode(epi);
					
					int pos = startDate.getDayDifferenceTo(episode.getAddDate());
					
					ls[pos] += episode.getLength();
				}
			}
		}
		
		return ls;
	}
	
	public static long[] getAddedByteCountForAllDates(CCMovieList ml, CCDate startDate, int count) {
		long[] ls = new long[count];
		
		for (int i = 0; i < count; i++) {
			ls[i] = 0;
		}
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
			
			int pos = startDate.getDayDifferenceTo(m.getAddDate());
			
			ls[pos] += m.getFilesize().getBytes();
		}
		
		return ls;
	}
	
	public static int[] getMovieMinuteCountForAllDates(CCMovieList ml, CCDate startDate, int count) {
		int[] ls = getAddedMinuteCountForAllDates(ml, startDate, count);
		int[] ns = new int[count];
		
		int curr = 0;
		
		for (int i = 0; i < count; i++) {
			curr += ls[i];
			ns[i] = curr;
		}
		
		return ns;
	}
	
	public static int[] getSeriesMinuteCountForAllDates(CCMovieList ml, CCDate startDate, int count) {
		int[] ls = getAddedSeriesCountForAllDates(ml, startDate, count);
		int[] ns = new int[count];
		
		int curr = 0;
		
		for (int i = 0; i < count; i++) {
			curr += ls[i];
			ns[i] = curr;
		}
		
		return ns;
	}
	
	public static CCDate getFirstSeriesAddDate(CCMovieList ml) {
		CCDate date = CCDate.getMaximumDate();

		for (Iterator<CCSeries> it = ml.iteratorSeries(); it.hasNext();) {
			CCSeries series = it.next();
			
			for (int sea = 0; sea < series.getSeasonCount(); sea++) {
				CCSeason season = series.getSeason(sea);
				
				for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
					CCEpisode episode = season.getEpisode(epi);
					
					if (episode.getAddDate().isLessThan(date)) {
						date = episode.getAddDate();
					}
				}
			}
		}
		
		return date;
	}
	
	public static CCDate getLastSeriesAddDate(CCMovieList ml) {
		CCDate date = CCDate.getMinimumDate();
		
		for (Iterator<CCSeries> it = ml.iteratorSeries(); it.hasNext();) {
			CCSeries series = it.next();
			
			for (int sea = 0; sea < series.getSeasonCount(); sea++) {
				CCSeason season = series.getSeason(sea);
				
				for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
					CCEpisode episode = season.getEpisode(epi);
					
					if (episode.getAddDate().isGreaterThan(date)) {
						date = episode.getAddDate();
					}
				}
			}
		}
		
		return date;
	}
	
	public static long[] getMovieByteCountForAllDates(CCMovieList ml, CCDate startDate, int count) {
		long[] ls = getAddedByteCountForAllDates(ml, startDate, count);
		long[] ns = new long[count];
		
		long curr = 0;
		
		for (int i = 0; i < count; i++) {
			curr += ls[i];
			ns[i] = curr;
		}
		
		return ns;
	}
	
	public static CCDate getFirstEpisodeWatchedDate(CCMovieList ml) {
		CCDate date = CCDate.getMaximumDate();
		
		for (Iterator<CCSeries> it = ml.iteratorSeries(); it.hasNext();) {
			CCSeries s = it.next();
			
			for (int sc = 0; sc < s.getSeasonCount(); sc++) {
				for (int ec = 0; ec < s.getSeason(sc).getEpisodeCount(); ec++) {
					CCEpisode epis = s.getSeason(sc).getEpisode(ec);
					
					if (!epis.getViewedHistoryLast().isMinimum() && epis.getViewedHistoryLast().isLessThan(date)) {
						date = epis.getViewedHistoryLast();
					}
				}
			}
		}
		
		return date;
	}
	
	public static CCDate getLastEpisodeWatchedDate(CCMovieList ml) {
		CCDate date = CCDate.getMinimumDate();
		
		for (Iterator<CCSeries> it = ml.iteratorSeries(); it.hasNext();) {
			CCSeries s = it.next();
			
			for (int sc = 0; sc < s.getSeasonCount(); sc++) {
				for (int ec = 0; ec < s.getSeason(sc).getEpisodeCount(); ec++) {
					CCEpisode epis = s.getSeason(sc).getEpisode(ec);
					
					if (!epis.getViewedHistoryLast().isMinimum() && epis.getViewedHistoryLast().isGreaterThan(date)) {
						date = epis.getViewedHistoryLast();
					}
				}
			}
			
		}
		
		return date;
	}
	
	public static List<Integer> getEpisodesViewedForAllDates(CCMovieList ml, CCDate startDate, int count) {
		int initialcount = 0;
		List<Integer> ls = new ArrayList<>();
		
		if (count == 0) return ls;
		
		for (int i = 0; i < count; i++) {
			ls.add(0);
		}
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
			
			int pos = startDate.getDayDifferenceTo(m.getAddDate());
			
			if (pos >= 0 && pos < count) {
				int prev = ls.get(pos) + 1;
				ls.set(pos, prev);
			}
		}
		
		for (Iterator<CCSeries> it = ml.iteratorSeries(); it.hasNext();) {
			CCSeries s = it.next();
			
			for (int sc = 0; sc < s.getSeasonCount(); sc++) {
				for (int ec = 0; ec < s.getSeason(sc).getEpisodeCount(); ec++) {
					CCEpisode epis = s.getSeason(sc).getEpisode(ec);
					
					if (epis.isViewed()) {
						if (epis.getViewedHistoryLast().isMinimum()) {
							initialcount++;
						} else {
							int pos = startDate.getDayDifferenceTo(epis.getViewedHistoryLast());
							
							int prev = ls.get(pos) + 1;
							ls.set(pos, prev);
						}
					}
				}
			}
		}
		
		ls.set(0, ls.get(0) + initialcount);
		
		for (int i = 1; i < ls.size(); i++) {
			ls.set(i, ls.get(i-1) + ls.get(i));
		}
		
		return ls;
	}
	
	public static int failProofDiv(int a, int b) {
		return b == 0 ? 0 : a/b;
	}
	
	public static List<CCEpisode> getEpisodesWithExplicitLastViewedDate(CCSeries series) {
		List<CCEpisode> result = new ArrayList<>();
		
		for (int sc = 0; sc < series.getSeasonCount(); sc++) {
			CCSeason season = series.getSeason(sc);
			
			for (int ep = 0; ep < season.getEpisodeCount(); ep++) {
				CCEpisode episode = season.getEpisode(ep);
				
				if (episode.isViewed() && !episode.getViewedHistoryLast().isMinimum())
					result.add(episode);
			}
		}
		
		Collections.sort(result, new Comparator<CCEpisode>() {
			@Override
			public int compare(CCEpisode o1, CCEpisode o2) {
				return CCDate.compare(o1.getViewedHistoryLast(), o2.getViewedHistoryLast());
			}
		});
		
		return result;
	}
	
	public static int[][] getAddedSeriesFormatLengthForAllDates(CCMovieList ml, CCDate startDate, int count) {
		List<CCMovieFormat> formats = Arrays.asList(CCMovieFormat.values());
		
		int[][] ls = new int[count][formats.size()];
		
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < formats.size(); j++) {
				ls[i][j] = 0;
			}
		}
		
		for (Iterator<CCSeries> it = ml.iteratorSeries(); it.hasNext();) {
			CCSeries series = it.next();
			
			for (int sea = 0; sea < series.getSeasonCount(); sea++) {
				CCSeason season = series.getSeason(sea);
				
				for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
					CCEpisode episode = season.getEpisode(epi);
					
					int pos = startDate.getDayDifferenceTo(episode.getAddDate());
					
					ls[pos][formats.indexOf(episode.getFormat())] += episode.getLength();
				}
			}
		}
		
		return ls;
	}
	
	public static int[][] getAddedMoviesFormatLengthForAllDates(CCMovieList ml, CCDate startDate, int count) {
		List<CCMovieFormat> formats = Arrays.asList(CCMovieFormat.values());
		
		int[][] ls = new int[count][formats.size()];
		
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < formats.size(); j++) {
				ls[i][j] = 0;
			}
		}
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
			
			int pos = startDate.getDayDifferenceTo(m.getAddDate());

			ls[pos][formats.indexOf(m.getFormat())] += m.getLength();
		}
		
		return ls;
	}

	public static List<CCDatespan> getDatespanFromSeries(CCSeries series, int gravity) {
		List<CCDatespan> span = new ArrayList<>();
		List<SortableTuple<CCDate, Integer>> dates = new ArrayList<>();
		
		for (int sc = 0; sc < series.getSeasonCount(); sc++) {
			CCSeason season = series.getSeason(sc);
			
			for (int ep = 0; ep < season.getEpisodeCount(); ep++) {
				CCEpisode episode = season.getEpisode(ep);
				
				for (CCDateTime timestamp : episode.getViewedHistory()) {
					dates.add(new SortableTuple<>(timestamp.date, season.getSeasonID() * 10000 + episode.getEpisode()));
				}
			}
		}
		
		if (dates.size() == 0) return span;
		
		Collections.sort(dates);
		
		SortableTuple<CCDate, Integer> start = dates.get(0);
		dates.remove(0);
		
		SortableTuple<CCDate, Integer> end = start;
		
		while (dates.size() > 0) {
			SortableTuple<CCDate, Integer> curr = dates.get(0);
			dates.remove(0);
			
			if (end.Item1.getDayDifferenceTo(curr.Item1) > gravity || curr.Item2 < end.Item2) {
				span.add(new CCDatespan(start.Item1, end.Item1));
				start = curr;
				end = start;
			} else {
				end = curr;
			}
		}
		span.add(new CCDatespan(start.Item1, end.Item1));
		
		return span;
	}
	
	public static HashMap<CCSeries, List<CCDatespan>> getAllSeriesTimespans(CCMovieList ml, int gravity) {
		HashMap<CCSeries, List<CCDatespan>> r = new HashMap<>();

		for (Iterator<CCSeries> it = ml.iteratorSeries(); it.hasNext();) {
			CCSeries series = it.next();
			
			List<CCDatespan> span = getDatespanFromSeries(series, gravity);
			
			if (span.size() > 0) r.put(series, span);
		}

		return r;
	}

	public static <T, U> List<U> convertMapToOrderedKeyList(HashMap<U, T> m, Comparator<U> c) {
		List<U> r = new ArrayList<>();

		for (U key : m.keySet()) {
			r.add(key);
		}
		
		Collections.sort(r, c);
		
		return r;
	}

	public static CCDate getSeriesTimespansStart(HashMap<CCSeries, List<CCDatespan>> seriesMap) {
		CCDate start = CCDate.getCurrentDate();
		
		for (Entry<CCSeries, List<CCDatespan>> set : seriesMap.entrySet()) {
			for (CCDatespan span : set.getValue()) {
				start = CCDate.min(start, span.start);
			}
		}
		
		return start;
	}

	public static CCDate getSeriesTimespansEnd(HashMap<CCSeries, List<CCDatespan>> seriesMap) {
		CCDate end = CCDate.getCurrentDate();
		
		for (Entry<CCSeries, List<CCDatespan>> set : seriesMap.entrySet()) {
			for (CCDatespan span : set.getValue()) {
				end = CCDate.max(end, span.end);
			}
		}
		
		return end;
	}
}
