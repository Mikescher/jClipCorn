package de.jClipCorn.util.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.ICCDatedElement;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.util.SortableTuple;
import de.jClipCorn.util.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDatespan;
import de.jClipCorn.util.stream.CCStream;

/*
 * We need to return an unboxed Integer in many methods due to an OpenJDK compiler bug
 * https://bugs.openjdk.java.net/browse/JDK-8056038
 */
public class StatisticsHelper {
	public final static int[] CHART_COLORS = {0x4D4D4D, 0x5DA5DA, 0xFAA43A, 0x60BD68, 0xF17CB0, 0xB2912F, 0xB276B2, 0x000080, 0xF15854};
	
	public static Integer getViewedCount(CCStream<ICCPlayableElement> it) {
		return it.filter(e -> e.isViewed()).count();
	}
	
	public static Integer getUnviewedCount(CCStream<ICCPlayableElement> it) {
		return it.filter(e -> !e.isViewed()).count();
	}

	public static Integer getMovieDuration(CCMovieList ml) {
		return ml.iteratorMovies().sum(m -> m.getLength(), (a, b) -> a + b, 0);
	}

	public static Integer getSeriesDuration(CCMovieList ml) {
		return ml.iteratorEpisodes().sum(m -> m.getLength(), (a, b) -> a + b, 0);
	}

	public static Integer getTotalDuration(CCMovieList ml) {
		return ml.iteratorPlayables().sum(m -> m.getLength(), (a, b) -> a + b, 0);
	}

	public static CCFileSize getMovieSize(CCMovieList ml) {
		return ml.iteratorMovies().sum(m -> m.getFilesize(), CCFileSize::add, CCFileSize.ZERO);
	}

	public static CCFileSize getSeriesSize(CCMovieList ml) {
		return ml.iteratorSeries().sum(m -> m.getFilesize(), CCFileSize::add, CCFileSize.ZERO);
	}

	public static CCFileSize getTotalSize(CCMovieList ml) {
		return ml.iteratorElements().sum(m -> m.getFilesize(), CCFileSize::add, CCFileSize.ZERO);
	}

	public static CCFileSize getAvgMovieSize(CCMovieList ml) {
		int mc = ml.getEpisodeCount();
		
		if (mc == 0)
			return new CCFileSize(0);
		
		return CCFileSize.div(getMovieSize(ml), mc);
	}

	public static CCFileSize getAvgSeriesSize(CCMovieList ml) {
		int ec = ml.getEpisodeCount();
		
		if (ec == 0)
			return new CCFileSize(0);
		
		return CCFileSize.div(getSeriesSize(ml), ec);
	}

	public static double getAvgImDbRating(CCMovieList ml) {
		int c = 0;

		for (CCDatabaseElement el : ml.iteratorElements()) {
			c += el.getOnlinescore().asInt();
		}

		return ((int) ((c / (ml.getElementCount() * 1d)) * 5)) / 10d;
	}

	public static Integer getViewedMovieDuration(CCMovieList ml) {
		return ml.iteratorMovies().filter(m -> m.isViewed()).sum(m -> m.getLength(), (a, b) -> a + b, 0);
	}

	public static Integer getViewedSeriesDuration(CCMovieList ml) {
		return ml.iteratorEpisodes().filter(m -> m.isViewed()).sum(m -> m.getLength(), (a, b) -> a + b, 0);
	}

	public static Integer getViewedTotalDuration(CCMovieList ml) {
		return ml.iteratorPlayables().filter(m -> m.isViewed()).sum(m -> m.getLength(), (a, b) -> a + b, 0);
	}
	
	public static CCDate getFirstAddDate(CCStream<ICCPlayableElement> it) {
		return it.minOrDefault(m -> m.getAddDate(), CCDate::compare, CCDate.getUnspecified());
	}
	
	public static CCDate getLastAddDate(CCStream<ICCPlayableElement> it) {
		return it.maxOrDefault(m -> m.getAddDate(), CCDate::compare, CCDate.getUnspecified());
	}
	
	public static List<Integer> getCountForAllDates(CCDate startDate, int count, CCStream<ICCPlayableElement> source) {
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
	
	public static Integer getMinimumLength(CCStream<ICCPlayableElement> it) {
		return it.minOrDefault(m -> m.getLength(), Integer::compare, -1);
	}
	
	public static Integer getMaximumLength(CCStream<ICCPlayableElement> it) {
		return it.maxOrDefault(m -> m.getLength(), Integer::compare, -1);
	}
	
	public static double[][] getCountForAllLengths(int minLength, int count, CCStream<ICCPlayableElement> it) {
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
	
	public static int[] getCountForAllFormats(CCStream<ICCPlayableElement> it) {
		int[] result = new int[CCFileFormat.values().length];
		
		for (int i = 0; i < CCFileFormat.values().length; i++) {
			result[i] = 0;
		}
		
		for (ICCPlayableElement m : it) {
			result[m.getFormat().asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getElementCountForAllProvider(CCStream<CCDatabaseElement> it) {
		int[] result = new int[CCOnlineRefType.values().length];
		
		for (int i = 0; i < CCOnlineRefType.values().length; i++) {
			result[i] = 0;
		}
		
		for (CCDatabaseElement m : it) {
			result[m.getOnlineReference().type.asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getCountForAllQualities(CCStream<ICCPlayableElement> it) {
		int[] result = new int[CCQuality.values().length];
		
		for (int i = 0; i < CCQuality.values().length; i++) {
			result[i] = 0;
		}
		
		for (ICCPlayableElement m : it) {
			result[m.getQuality().asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getCountForAllOnlinescores(CCStream<CCDatabaseElement> it) {
		int[] result = new int[CCOnlineScore.values().length];
		
		for (int i = 0; i < CCOnlineScore.values().length; i++) {
			result[i] = 0;
		}
		
		for (CCDatabaseElement m : it) {
			result[m.getOnlinescore().asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getCountForAllScores(CCStream<CCDatabaseElement> it) {
		int[] result = new int[CCUserScore.values().length - 1];
		
		for (int i = 0; i < (CCUserScore.values().length - 1); i++) {
			result[i] = 0;
		}
		
		for (CCDatabaseElement m : it) {
			if (m.getScore() != CCUserScore.RATING_NO) {
				result[m.getScore().asInt()]++;
			}
		}
		
		return result;
	}
	
	public static Integer getMinimumYear(CCStream<ICCDatedElement> it) {
		return it.minOrDefault(e -> e.getYear(), Integer::compare, 0);
	}
	
	public static Integer getMaximumYear(CCStream<ICCDatedElement> it) {
		return it.maxOrDefault(e -> e.getYear(), Integer::compare, 0);
	}
	
	public static int[] getCountForAllYears(int minYear, int count, CCStream<ICCDatedElement> it) {
		int[] result = new int[count];
		
		for (int i = 0; i < count; i++) {
			result[i] = 0;
		}
		
		for (ICCDatedElement m : it) {
			result[m.getYear() - minYear]++;
		}
		
		return result;
	}
	
	public static int[] getCountForAllGenres(CCStream<CCDatabaseElement> it) {
		int[] result = new int[CCGenre.values().length];
		
		for (int i = 0; i < CCGenre.values().length; i++) {
			result[i] = 0;
		}
		
		for (CCDatabaseElement m : it) {
			for (int gi = 0; gi < m.getGenreCount(); gi++) {
				result[m.getGenre(gi).asInt()]++;
			}
		}
		
		return result;
	}
	
	public static int[] getCountForAllFSKs(CCStream<CCDatabaseElement> it) {
		int[] result = new int[CCFSK.values().length];
		
		for (int i = 0; i < CCFSK.values().length; i++) {
			result[i] = 0;
		}

		for (CCDatabaseElement m : it) {
			result[m.getFSK().asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getCountForAllLanguages(CCStream<CCDatabaseElement> it) {
		int[] result = new int[CCDBLanguage.values().length];
		
		for (int i = 0; i < CCDBLanguage.values().length; i++) {
			result[i] = 0;
		}

		for (CCDatabaseElement m : it) {
			result[m.getLanguage().asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getCountForAllTags(CCStream<ICCPlayableElement> it) {
		int[] result = new int[CCTagList.ACTIVETAGS];
		
		for (int i = 0; i < CCTagList.ACTIVETAGS; i++) {
			final int tag = i;
			result[tag] = it.filter(p -> p.getTag(tag)).count();
		}
		
		return result;
	}
	
	private static int[] getMinuteCountForAllDates(CCDate startDate, int count, CCStream<ICCPlayableElement> it) {
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
	
	private static long[] getByteCountForAllDates(CCDate startDate, int count, CCStream<ICCPlayableElement> it) {
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
	
	public static int[] getCumulativeMinuteCountForAllDates(CCDate startDate, int count, CCStream<ICCPlayableElement> it) {
		int[] ls = getMinuteCountForAllDates(startDate, count, it);
		int[] ns = new int[count];
		
		int curr = 0;
		
		for (int i = 0; i < count; i++) {
			curr += ls[i];
			ns[i] = curr;
		}
		
		return ns;
	}
	
	public static long[] getCumulativeByteCountForAllDates(CCDate startDate, int count, CCStream<ICCPlayableElement> it) {
		long[] ls = getByteCountForAllDates(startDate, count, it);
		long[] ns = new long[count];
		
		long curr = 0;
		
		for (int i = 0; i < count; i++) {
			curr += ls[i];
			ns[i] = curr;
		}
		
		return ns;
	}
	
	public static CCDate getFirstWatchedDate(CCStream<ICCPlayableElement> it) {
		return it.map(m -> m.getViewedHistory().getLastDateOrInvalid()).filter(p -> !p.isMinimum()).minOrDefault(CCDate::compare, CCDate.getUnspecified());
	}
	
	public static CCDate getLastWatchedDate(CCStream<ICCPlayableElement> it) {
		return it.map(m -> m.getViewedHistory().getLastDateOrInvalid()).filter(p -> !p.isMinimum()).maxOrDefault(CCDate::compare, CCDate.getUnspecified());
	}
	
	public static List<Integer> getViewedForAllDates(CCDate startDate, int count, CCStream<ICCPlayableElement> it) {
		int initialcount = 0;
		List<Integer> ls = new ArrayList<>();
		
		if (count <= 0) return ls;
		
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
	
	public static int[][] getCumulativeFormatCountForAllDates(CCDate startDate, int count, CCStream<ICCPlayableElement> it) {
		List<CCFileFormat> formats = Arrays.asList(CCFileFormat.values());
		
		int[][] ls = new int[count][formats.size()];
		
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < formats.size(); j++) {
				ls[i][j] = 0;
			}
		}
		
		for (ICCPlayableElement m : it) {
			int pos = startDate.getDayDifferenceTo(m.getAddDate());

			ls[pos][formats.indexOf(m.getFormat())] += 1;
		}
		
		return ls;
	}
	
	public static int[][] getCumulativeQualityCountForAllDates(CCDate startDate, int count, CCStream<ICCPlayableElement> it) {
		List<CCQuality> qualities = Arrays.asList(CCQuality.values());
		
		int[][] ls = new int[count][qualities.size()];
		
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < qualities.size(); j++) {
				ls[i][j] = 0;
			}
		}
		
		for (ICCPlayableElement m : it) {
			int pos = startDate.getDayDifferenceTo(m.getAddDate());

			ls[pos][qualities.indexOf(m.getQuality())] += 1;
		}
		
		return ls;
	}

	public static List<CCDatespan> getDatespanFromSeries(CCSeries series, int gravity) {
		List<CCDatespan> span = new ArrayList<>();
		
		List<SortableTuple<CCDate, Integer>> dates = series
				.iteratorEpisodes()
				.flatten(e -> e.getViewedHistory().iterator().map(h -> Tuple.Create(e, h)))
				.filter(t -> !t.Item2.isUnspecifiedDateTime())
				.map(t -> new SortableTuple<>(t.Item2.date, t.Item1.getSeason().getSeasonNumber() * 10000 + t.Item1.getEpisodeNumber()))
				.enumerate();
		
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
	
	public static int[] getMultipleWatchCount(CCStream<ICCPlayableElement> it) {
		Map<Integer, Integer> map = new HashMap<>();
		map.put(0, 0);
		int max_vc = 0;
		
		for (ICCPlayableElement m : it) {
			int vc = m.getViewedHistory().count();
			
			Integer sum = map.get(vc);
			if (sum == null) sum = 0;
			map.put(vc, sum + 1);
			
			max_vc = Math.max(max_vc, vc);
		}
		
		int[] result = new int[max_vc + 1];
		for (int vc = 0; vc <= max_vc; vc++) {
			Integer sum = map.get(vc);
			if (sum == null) sum = 0;
			result[vc] = sum;
		}
		return result;
	}
}
