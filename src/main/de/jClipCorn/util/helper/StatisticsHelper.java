package de.jClipCorn.util.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.ICCDatedElement;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
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
import de.jClipCorn.util.cciterator.CCIterator;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.datetime.CCDatespan;

public class StatisticsHelper {
	public static int[] CHART_COLORS = {0x4D4D4D, 0x5DA5DA, 0xFAA43A, 0x60BD68, 0xF17CB0, 0xB2912F, 0xB276B2, 0x000080, 0xF15854};
	
	public static int getViewedCount(CCIterator<ICCPlayableElement> it) {
		return it.filter(e -> e.isViewed()).count();
	}
	
	public static int getUnviewedCount(CCIterator<ICCPlayableElement> it) {
		return it.filter(e -> !e.isViewed()).count();
	}

	public static int getMovieDuration(CCMovieList ml) {
		int c = 0;

		for (CCMovie mov : ml.iteratorMovies()) {
			c += mov.getLength();
		}

		return c;
	}

	public static int getSeriesDuration(CCMovieList ml) {
		int c = 0;

		for (CCSeries series : ml.iteratorSeries()) {
			c += series.getLength();
		}

		return c;
	}

	public static int getTotalDuration(CCMovieList ml) {
		return getMovieDuration(ml) + getSeriesDuration(ml);
	}

	public static CCMovieSize getMovieSize(CCMovieList ml) {
		CCMovieSize s = new CCMovieSize();

		for (CCMovie mov : ml.iteratorMovies()) {
			s.add(mov.getFilesize());
		}

		return s;
	}

	public static CCMovieSize getSeriesSize(CCMovieList ml) {
		CCMovieSize s = new CCMovieSize();

		for (CCSeries series : ml.iteratorSeries()) {
			s.add(series.getFilesize());
		}

		return s;
	}

	public static CCMovieSize getTotalSize(CCMovieList ml) {
		CCMovieSize s = new CCMovieSize();

		for (CCDatabaseElement el : ml.iteratorElements()) {
			s.add(el.getFilesize());
		}

		return s;
	}

	public static CCMovieSize getAvgMovieSize(CCMovieList ml) {
		CCMovieSize s = new CCMovieSize();

		for (CCMovie mov : ml.iteratorMovies()) {
			s.add(mov.getFilesize());
		}

		int mc = ml.getEpisodeCount();
		
		if (mc == 0)
			return new CCMovieSize(0);
		
		s.div(mc);

		return s;
	}

	public static CCMovieSize getAvgSeriesSize(CCMovieList ml) {
		CCMovieSize s = new CCMovieSize();

		for (CCSeries series : ml.iteratorSeries()) {
			s.add(series.getFilesize());
		}

		int ec = ml.getEpisodeCount();
		
		if (ec == 0)
			return new CCMovieSize(0);
		
		s.div(ec);

		return s;
	}

	public static double getAvgImDbRating(CCMovieList ml) {
		int c = 0;

		for (CCDatabaseElement el : ml.iteratorElements()) {
			c += el.getOnlinescore().asInt();
		}

		return ((int) ((c / (ml.getElementCount() * 1d)) * 5)) / 10d;
	}

	public static int getViewedMovieDuration(CCMovieList ml) {
		int c = 0;

		for (CCMovie m : ml.iteratorMovies()) {
			if (m.isViewed()) {
				c += m.getLength();
			}
		}

		return c;
	}

	public static int getViewedSeriesDuration(CCMovieList ml) {
		int c = 0;

		for (CCSeries s : ml.iteratorSeries()) {
			if (s.isViewed()) {
				c += s.getLength();
			}
		}

		return c;
	}

	public static int getViewedTotalDuration(CCMovieList ml) {
		return getViewedMovieDuration(ml) + getViewedSeriesDuration(ml);
	}
	
	public static CCDate getFirstAddDate(CCIterator<ICCPlayableElement> it) {
		return it.minOrDefault(m -> m.getAddDate(), CCDate::compare, CCDate.getUnspecified());
	}
	
	public static CCDate getLastAddDate(CCIterator<ICCPlayableElement> it) {
		return it.maxOrDefault(m -> m.getAddDate(), CCDate::compare, CCDate.getUnspecified());
	}
	
	public static List<Integer> getCountForAllDates(CCDate startDate, int count, CCIterator<ICCPlayableElement> source) {
		List<Integer> ls = new ArrayList<>();
		
		for (int i = 0; i < count; i++) {
			ls.add(0);
		}
		
		for (ICCPlayableElement m : source) {
			int pos = startDate.getDayDifferenceTo(m.getAddDate());
			
			if (pos >= 0 && pos < count) {
				int prev = ls.get(pos) + 1;
				ls.set(pos, prev);
			}
		}
		
		return ls;
	}
	
	public static int getMinimumLength(CCIterator<ICCPlayableElement> it) {
		return it.minOrDefault(m -> m.getLength(), Integer::compare, -1);
	}
	
	public static int getMaximumLength(CCIterator<ICCPlayableElement> it) {
		return it.maxOrDefault(m -> m.getLength(), Integer::compare, -1);
	}
	
	public static double[][] getCountForAllLengths(int minLength, int count, CCIterator<ICCPlayableElement> it) {
		double[][] result = new double[2][count+2];
		
		for (int i = 0; i < (count+2); i++) {
			result[0][i] = minLength + i;
			result[1][i] = 0;
		}
		
		for (ICCPlayableElement m : it) {
			result[1][m.getLength() - minLength + 1] += 1;
		}
		
		return result;
	}
	
	public static int[] getCountForAllFormats(CCIterator<ICCPlayableElement> it) {
		int[] result = new int[CCMovieFormat.values().length];
		
		for (int i = 0; i < CCMovieFormat.values().length; i++) {
			result[i] = 0;
		}
		
		for (ICCPlayableElement m : it) {
			result[m.getFormat().asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getElementCountForAllProvider(CCIterator<CCDatabaseElement> it) {
		int[] result = new int[CCOnlineRefType.values().length];
		
		for (int i = 0; i < CCOnlineRefType.values().length; i++) {
			result[i] = 0;
		}
		
		for (CCDatabaseElement m : it) {
			result[m.getOnlineReference().type.asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getCountForAllQualities(CCIterator<ICCPlayableElement> it) {
		int[] result = new int[CCMovieQuality.values().length];
		
		for (int i = 0; i < CCMovieQuality.values().length; i++) {
			result[i] = 0;
		}
		
		for (ICCPlayableElement m : it) {
			result[m.getQuality().asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getCountForAllOnlinescores(CCIterator<CCDatabaseElement> it) {
		int[] result = new int[CCMovieOnlineScore.values().length];
		
		for (int i = 0; i < CCMovieOnlineScore.values().length; i++) {
			result[i] = 0;
		}
		
		for (CCDatabaseElement m : it) {
			result[m.getOnlinescore().asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getCountForAllScores(CCIterator<CCDatabaseElement> it) {
		int[] result = new int[CCMovieScore.values().length - 1];
		
		for (int i = 0; i < (CCMovieScore.values().length - 1); i++) {
			result[i] = 0;
		}
		
		for (CCDatabaseElement m : it) {
			if (m.getScore() != CCMovieScore.RATING_NO) {
				result[m.getScore().asInt()]++;
			}
		}
		
		return result;
	}
	
	public static int getMinimumYear(CCIterator<ICCDatedElement> it) {
		return it.minOrDefault(e -> e.getYear(), Integer::compare, 0);
	}
	
	public static int getMaximumYear(CCIterator<ICCDatedElement> it) {
		return it.maxOrDefault(e -> e.getYear(), Integer::compare, 0);
	}
	
	public static int[] getCountForAllYears(int minYear, int count, CCIterator<ICCDatedElement> it) {
		int[] result = new int[count];
		
		for (int i = 0; i < count; i++) {
			result[i] = 0;
		}
		
		for (ICCDatedElement m : it) {
			result[m.getYear() - minYear]++;
		}
		
		return result;
	}
	
	public static int[] getCountForAllGenres(CCIterator<CCDatabaseElement> it) {
		int[] result = new int[CCMovieGenre.values().length];
		
		for (int i = 0; i < CCMovieGenre.values().length; i++) {
			result[i] = 0;
		}
		
		for (CCDatabaseElement m : it) {
			for (int gi = 0; gi < m.getGenreCount(); gi++) {
				result[m.getGenre(gi).asInt()]++;
			}
		}
		
		return result;
	}
	
	public static int[] getCountForAllFSKs(CCIterator<CCDatabaseElement> it) {
		int[] result = new int[CCMovieFSK.values().length];
		
		for (int i = 0; i < CCMovieFSK.values().length; i++) {
			result[i] = 0;
		}

		for (CCDatabaseElement m : it) {
			result[m.getFSK().asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getCountForAllLanguages(CCIterator<CCDatabaseElement> it) {
		int[] result = new int[CCMovieLanguage.values().length];
		
		for (int i = 0; i < CCMovieLanguage.values().length; i++) {
			result[i] = 0;
		}

		for (CCDatabaseElement m : it) {
			result[m.getLanguage().asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getCountForAllTags(CCIterator<ICCPlayableElement> it) {
		int[] result = new int[CCMovieTags.ACTIVETAGS];
		
		for (int i = 0; i < CCMovieTags.ACTIVETAGS; i++) {
			final int tag = i;
			result[tag] = it.filter(p -> p.getTag(tag)).count();
		}
		
		return result;
	}
	
	public static int[] getMinuteCountForAllDates(CCDate startDate, int count, CCIterator<ICCPlayableElement> it) {
		int[] ls = new int[count];
		
		for (int i = 0; i < count; i++) {
			ls[i] = 0;
		}
		
		for (ICCPlayableElement m : it) {
			int pos = startDate.getDayDifferenceTo(m.getAddDate());
			
			ls[pos] += m.getLength();
		}
		
		return ls;
	}
	
	public static long[] getByteCountForAllDates(CCDate startDate, int count, CCIterator<ICCPlayableElement> it) {
		long[] ls = new long[count];
		
		for (int i = 0; i < count; i++) {
			ls[i] = 0;
		}

		for (ICCPlayableElement m : it) {
			int pos = startDate.getDayDifferenceTo(m.getAddDate());
			
			ls[pos] += m.getFilesize().getBytes();
		}
		
		return ls;
	}
	
	public static int[] getCumulativeMinuteCountForAllDates(CCDate startDate, int count, CCIterator<ICCPlayableElement> it) {
		int[] ls = getMinuteCountForAllDates(startDate, count, it);
		int[] ns = new int[count];
		
		int curr = 0;
		
		for (int i = 0; i < count; i++) {
			curr += ls[i];
			ns[i] = curr;
		}
		
		return ns;
	}
	
	public static long[] getCumulativeByteCountForAllDates(CCDate startDate, int count, CCIterator<ICCPlayableElement> it) {
		long[] ls = getByteCountForAllDates(startDate, count, it);
		long[] ns = new long[count];
		
		long curr = 0;
		
		for (int i = 0; i < count; i++) {
			curr += ls[i];
			ns[i] = curr;
		}
		
		return ns;
	}
	
	public static CCDate getFirstWatchedDate(CCIterator<ICCPlayableElement> it) {
		return it.map(m -> m.getViewedHistory().getLastDateOrInvalid()).filter(p -> !p.isMinimum()).minOrDefault(CCDate::compare, CCDate.getUnspecified());
	}
	
	public static CCDate getLastWatchedDate(CCIterator<ICCPlayableElement> it) {
		return it.map(m -> m.getViewedHistory().getLastDateOrInvalid()).filter(p -> !p.isMinimum()).maxOrDefault(CCDate::compare, CCDate.getUnspecified());
	}
	
	public static List<Integer> getViewedForAllDates(CCDate startDate, int count, CCIterator<ICCPlayableElement> it) {
		int initialcount = 0;
		List<Integer> ls = new ArrayList<>();
		
		if (count == 0) return ls;
		
		for (int i = 0; i < count; i++) {
			ls.add(0);
		}
		
		for (ICCPlayableElement elem : it.filter(el -> el.isViewed())) {
			if (elem.getViewedHistory().getLastDateOrInvalid().isMinimum()) {
				initialcount++;
			} else {
				int pos = startDate.getDayDifferenceTo(elem.getViewedHistory().getLastDateOrInvalid());
				
				int prev = ls.get(pos) + 1;
				ls.set(pos, prev);
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
			CCSeason season = series.getSeasonByArrayIndex(sc);
			
			for (int ep = 0; ep < season.getEpisodeCount(); ep++) {
				CCEpisode episode = season.getEpisodeByArrayIndex(ep);
				
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
	
	public static int[][] getAddedFormatLengthForAllDates(CCDate startDate, int count, CCIterator<ICCPlayableElement> it) {
		List<CCMovieFormat> formats = Arrays.asList(CCMovieFormat.values());
		
		int[][] ls = new int[count][formats.size()];
		
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < formats.size(); j++) {
				ls[i][j] = 0;
			}
		}
		
		for (ICCPlayableElement m : it) {
			int pos = startDate.getDayDifferenceTo(m.getAddDate());

			ls[pos][formats.indexOf(m.getFormat())] += m.getLength();
		}
		
		return ls;
	}

	public static List<CCDatespan> getDatespanFromSeries(CCSeries series, int gravity) {
		List<CCDatespan> span = new ArrayList<>();
		List<SortableTuple<CCDate, Integer>> dates = new ArrayList<>();
		
		for (int sc = 0; sc < series.getSeasonCount(); sc++) {
			CCSeason season = series.getSeasonByArrayIndex(sc);
			
			for (int ep = 0; ep < season.getEpisodeCount(); ep++) {
				CCEpisode episode = season.getEpisodeByArrayIndex(ep);
				
				for (CCDateTime timestamp : episode.getViewedHistory()) {
					if (!timestamp.isUnspecifiedDateTime()) dates.add(new SortableTuple<>(timestamp.date, season.getSeasonNumber() * 10000 + episode.getEpisodeNumber()));
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
			
			if (end.Item1.getDayDifferenceTo(curr.Item1) > gravity) {
				span.add(new CCDatespan(start.Item1, end.Item1));
				start = curr;
				end = start;
			} else if (curr.Item2 < end.Item2) {
				span.add(new CCDatespan(start.Item1, end.Item1));
				start = curr;
				end = start;
			}  else {
				end = curr;
			}
		}
		span.add(new CCDatespan(start.Item1, end.Item1));
		
		return span;
	}
	
	public static HashMap<CCSeries, List<CCDatespan>> getAllSeriesTimespans(CCMovieList ml, int gravity) {
		HashMap<CCSeries, List<CCDatespan>> r = new HashMap<>();

		for (CCSeries series : ml.iteratorSeries()) {
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
		CCDate start = CCDate.getMaximumDate();
		
		for (Entry<CCSeries, List<CCDatespan>> set : seriesMap.entrySet()) {
			for (CCDatespan span : set.getValue()) {
				start = CCDate.min(start, span.start);
			}
		}
		
		return start;
	}

	public static CCDate getSeriesTimespansEnd(HashMap<CCSeries, List<CCDatespan>> seriesMap) {
		CCDate end = CCDate.getMinimumDate();
		
		for (Entry<CCSeries, List<CCDatespan>> set : seriesMap.entrySet()) {
			for (CCDatespan span : set.getValue()) {
				end = CCDate.max(end, span.end);
			}
		}
		
		return end;
	}
}
