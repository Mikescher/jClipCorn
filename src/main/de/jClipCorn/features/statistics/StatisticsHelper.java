package de.jClipCorn.features.statistics;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.util.SortableTuple;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.datetime.CCDatespan;
import de.jClipCorn.util.stream.CCStream;

import java.util.*;
import java.util.Map.Entry;

/*
 * We need to return an unboxed Integer in many methods due to an OpenJDK compiler bug
 * https://bugs.openjdk.java.net/browse/JDK-8056038
 */
public class StatisticsHelper {
	public final static int[] CHART_COLORS = {0x4D4D4D, 0x5DA5DA, 0xFAA43A, 0x60BD68, 0xF17CB0, 0xB2912F, 0xB276B2, 0x000080, 0xF15854};

	public enum OrderMode {
		IGNORED,  // Episode order does not matter
		ENFORCED, // Episode order must be strictly increasing
		STRICT,   // Episode order must be strictly increasing without gaps
	}

	public static Integer getViewedCount(CCStream<ICCPlayableElement> it) {
		return it.filter(ICCPlayableElement::isViewed).count();
	}
	
	public static Integer getUnviewedCount(CCStream<ICCPlayableElement> it) {
		return it.filter(e -> !e.isViewed()).count();
	}

	public static Integer getMovieDuration(CCMovieList ml) {
		return ml.iteratorMovies().sum(CCMovie::getLength, Integer::sum, 0);
	}

	public static Integer getSeriesDuration(CCMovieList ml) {
		return ml.iteratorEpisodes().sum(CCEpisode::getLength, Integer::sum, 0);
	}

	public static Integer getTotalDuration(CCMovieList ml) {
		return ml.iteratorPlayables().sum(e -> e.length().get(), Integer::sum, 0);
	}

	public static CCFileSize getMovieSize(CCMovieList ml) {
		return ml.iteratorMovies().sum(CCMovie::getFilesize, CCFileSize::add, CCFileSize.ZERO);
	}

	public static CCFileSize getSeriesSize(CCMovieList ml) {
		return ml.iteratorSeries().sum(CCSeries::getFilesize, CCFileSize::add, CCFileSize.ZERO);
	}

	public static CCFileSize getTotalSize(CCMovieList ml) {
		return ml.iteratorElements().sum(CCDatabaseElement::getFilesize, CCFileSize::add, CCFileSize.ZERO);
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
		return ml.iteratorMovies().filter(CCMovie::isViewed).sum(CCMovie::getLength, Integer::sum, 0);
	}

	public static Integer getViewedSeriesDuration(CCMovieList ml) {
		return ml.iteratorEpisodes().filter(CCEpisode::isViewed).sum(CCEpisode::getLength, Integer::sum, 0);
	}

	public static Integer getViewedTotalDuration(CCMovieList ml) {
		return ml.iteratorPlayables().filter(ICCPlayableElement::isViewed).sum(e -> e.length().get(), Integer::sum, 0);
	}
	
	public static CCDate getFirstAddDate(CCStream<ICCPlayableElement> it) {
		return it.minOrDefault(e -> e.addDate().get(), CCDate::compare, CCDate.getUnspecified());
	}
	
	public static CCDate getLastAddDate(CCStream<ICCPlayableElement> it) {
		return it.maxOrDefault(e -> e.addDate().get(), CCDate::compare, CCDate.getUnspecified());
	}
	
	public static List<Integer> getCountForAllDates(CCDate startDate, int count, CCStream<ICCPlayableElement> source) {
		List<Integer> ls = new ArrayList<>();
		
		for (int i = 0; i < count; i++) {
			ls.add(0);
		}
		
		for (ICCPlayableElement m : source) {
			int pos = startDate.getDayDifferenceTo(m.addDate().get());
			
			if (pos >= 0 && pos < count) {
				int prev = ls.get(pos) + 1;
				ls.set(pos, prev);
			}
		}
		
		return ls;
	}
	
	public static Integer getMinimumLength(CCStream<ICCPlayableElement> it) {
		return it.minOrDefault(e -> e.length().get(), Integer::compare, -1);
	}
	
	public static Integer getMaximumLength(CCStream<ICCPlayableElement> it) {
		return it.maxOrDefault(e -> e.length().get(), Integer::compare, -1);
	}
	
	public static double[][] getCountForAllLengths(int minLength, int count, CCStream<ICCPlayableElement> it) {
		double[][] result = new double[2][count+2];
		
		for (int i = 0; i < (count+2); i++) {
			result[0][i] = minLength + i;
			result[1][i] = 0;
		}
		
		for (ICCPlayableElement m : it) {
			result[1][m.length().get() - minLength + 1] += 1;
		}
		
		return result;
	}
	
	public static int[] getCountForAllFormats(CCStream<ICCPlayableElement> it) {
		int[] result = new int[CCFileFormat.values().length];
		
		for (int i = 0; i < CCFileFormat.values().length; i++) {
			result[i] = 0;
		}
		
		for (ICCPlayableElement m : it) {
			result[m.format().get().asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getElementCountForAllProviderAny(CCStream<CCDatabaseElement> it) {
		int[] result = new int[CCOnlineRefType.values().length];
		
		for (int i = 0; i < CCOnlineRefType.values().length; i++) {
			result[i] = 0;
		}
		
		for (CCDatabaseElement m : it) {
			for (CCSingleOnlineReference r : m.getOnlineReference()) result[r.type.asInt()]++;
		}
		
		return result;
	}
	
	public static int[] getElementCountForAllProviderMain(CCStream<CCDatabaseElement> it) {
		int[] result = new int[CCOnlineRefType.values().length];
		
		for (int i = 0; i < CCOnlineRefType.values().length; i++) {
			result[i] = 0;
		}
		
		for (CCDatabaseElement m : it) {
			result[m.getOnlineReference().Main.type.asInt()]++;
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
		int[] result = new int[CCUserScore.values().length];
		
		for (int i = 0; i < (CCUserScore.values().length); i++) {
			result[i] = 0;
		}
		
		for (CCDatabaseElement m : it) {
			result[m.Score.get().asInt()]++;
		}
		
		return result;
	}
	
	public static Integer getMinimumYear(CCStream<ICCDatedElement> it) {
		return it.minOrDefault(e -> e.year().get(), Integer::compare, 0);
	}
	
	public static Integer getMaximumYear(CCStream<ICCDatedElement> it) {
		return it.maxOrDefault(e -> e.year().get(), Integer::compare, 0);
	}
	
	public static int[] getCountForAllYears(int minYear, int count, CCStream<ICCDatedElement> it) {
		int[] result = new int[count];
		
		for (int i = 0; i < count; i++) {
			result[i] = 0;
		}
		
		for (ICCDatedElement m : it) {
			result[m.year().get() - minYear]++;
		}
		
		return result;
	}
	
	public static int[] getCountForAllGenres(CCStream<CCDatabaseElement> it) {
		int[] result = new int[CCGenre.values().length];
		
		for (int i = 0; i < CCGenre.values().length; i++) {
			result[i] = 0;
		}
		
		for (CCDatabaseElement m : it) {
			for (int gi = 0; gi < m.Genres.get().getGenreCount(); gi++) {
				result[m.Genres.get(gi).asInt()]++;
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

	public static int[] getCountForAllLanguages(CCStream<ICCPlayableElement> it) {
		int[] result = new int[CCDBLanguage.values().length];

		for (int i = 0; i < CCDBLanguage.values().length; i++) result[i] = 0;

		for (ICCPlayableElement m : it) {
			for(CCDBLanguage lng: m.language().get()) result[lng.asInt()]++;
		}

		return result;
	}

	public static HashMap<CCDBLanguageSet, Integer> getCountForAllLanguageLists(CCStream<ICCPlayableElement> it) {
		HashMap<CCDBLanguageSet, Integer> result = new HashMap<>();

		for (ICCPlayableElement m : it)
		{
			Integer vn = result.getOrDefault(m.language().get(), null);
			if (vn == null) result.put(m.language().get(), 1);
			else result.put(m.language().get(), vn + 1);
		}

		return result;
	}
	
	public static int[] getCountForAllTags(CCStream<ICCPlayableElement> it) {
		int[] result = new int[CCTagList.ACTIVETAGS];
		
		for (int i = 0; i < CCTagList.ACTIVETAGS; i++) {
			final int tag = i;
			result[tag] = it.filter(p -> p.tags().get(tag)).count();
		}
		
		return result;
	}
	
	private static int[] getMinuteCountForAllDates(CCDate startDate, int count, CCStream<ICCPlayableElement> it) {
		int[] ls = new int[count];
		
		for (int i = 0; i < count; i++) {
			ls[i] = 0;
		}
		
		for (ICCPlayableElement m : it) {
			int pos = startDate.getDayDifferenceTo(m.addDate().get());
			
			ls[pos] += m.length().get();
		}
		
		return ls;
	}
	
	private static long[] getByteCountForAllDates(CCDate startDate, int count, CCStream<ICCPlayableElement> it) {
		long[] ls = new long[count];
		
		for (int i = 0; i < count; i++) {
			ls[i] = 0;
		}

		for (ICCPlayableElement m : it) {
			int pos = startDate.getDayDifferenceTo(m.addDate().get());
			
			ls[pos] += m.fileSize().get().getBytes();
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
		return it.map(m -> m.viewedHistory().get().getLastDateOrInvalid()).filter(p -> !p.isMinimum()).minOrDefault(CCDate::compare, CCDate.getUnspecified());
	}
	
	public static CCDate getLastWatchedDate(CCStream<ICCPlayableElement> it) {
		return it.map(m -> m.viewedHistory().get().getLastDateOrInvalid()).filter(p -> !p.isMinimum()).maxOrDefault(CCDate::compare, CCDate.getUnspecified());
	}
	
	public static List<Integer> getViewedForAllDates(CCDate startDate, int count, CCStream<ICCPlayableElement> it) {
		int initialcount = 0;
		List<Integer> ls = new ArrayList<>();
		
		if (count <= 0) return ls;
		
		for (int i = 0; i < count; i++) {
			ls.add(0);
		}
		
		for (ICCPlayableElement elem : it.filter(ICCPlayableElement::isViewed)) {
			if (elem.viewedHistory().get().getLastDateOrInvalid().isMinimum()) {
				initialcount++;
			} else {
				int pos = startDate.getDayDifferenceTo(elem.viewedHistory().get().getLastDateOrInvalid());
				
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
		
		result.sort((o1, o2) -> CCDate.compare(o1.getViewedHistoryLast(), o2.getViewedHistoryLast()));
		
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
			int pos = startDate.getDayDifferenceTo(m.addDate().get());

			ls[pos][formats.indexOf(m.format().get())] += 1;
		}
		
		return ls;
	}
	
	public static List<CCDatespan> getDatespanFromSeries(CCSeries series, int gravity, OrderMode omode) {
		List<CCDatespan> span = new ArrayList<>();
		
		List<SortableTuple<CCDate, Integer>> dates = series
				.iteratorEpisodes()
				.flatten(e -> e.ViewedHistory.get().ccstream().map(h -> Tuple.Create(e, h)))
				.filter(t -> !t.Item2.isUnspecifiedDateTime())
				.map(t -> new SortableTuple<>(t.Item2.date, t.Item1.getGlobalEpisodeNumber()))
				.enumerate();
		
		if (dates.size() == 0) return span;
		
		Collections.sort(dates);
		
		SortableTuple<CCDate, Integer> start = dates.get(0);
		dates.remove(0);
		
		SortableTuple<CCDate, Integer> end = start;
		
		while (dates.size() > 0) {
			SortableTuple<CCDate, Integer> curr = dates.get(0);
			dates.remove(0);

			switch (omode)
			{
				case IGNORED:
					if (end.Item1.getDayDifferenceTo(curr.Item1) > gravity) {
						span.add(new CCDatespan(start.Item1, end.Item1.getAddDay(1)));
						start = curr;
						end = start;
					} else {
						end = curr;
					}
					break;
				case ENFORCED:
					if (end.Item1.getDayDifferenceTo(curr.Item1) > gravity) {
						span.add(new CCDatespan(start.Item1, end.Item1.getAddDay(1)));
						start = curr;
						end = start;
					} else if (curr.Item2 < end.Item2) {
						span.add(new CCDatespan(start.Item1, end.Item1.getAddDay(1)));
						start = curr;
						end = start;
					}  else {
						end = curr;
					}
					break;
				case STRICT:
					if (end.Item1.getDayDifferenceTo(curr.Item1) > gravity) {
						span.add(new CCDatespan(start.Item1, end.Item1.getAddDay(1)));
						start = curr;
						end = start;
					} else if (curr.Item2 - 1 != end.Item2) {
						span.add(new CCDatespan(start.Item1, end.Item1.getAddDay(1)));
						start = curr;
						end = start;
					}  else {
						end = curr;
					}
					break;
			}

		}
		span.add(new CCDatespan(start.Item1, end.Item1.getAddDay(1)));
		
		return span;
	}
	
	public static HashMap<CCSeries, List<CCDatespan>> getAllSeriesTimespans(CCMovieList ml, int gravity, OrderMode omode) {
		HashMap<CCSeries, List<CCDatespan>> r = new HashMap<>();

		for (CCSeries series : ml.iteratorSeries()) {
			List<CCDatespan> span = getDatespanFromSeries(series, gravity, omode);
			
			if (span.size() > 0) r.put(series, span);
		}

		return r;
	}

	public static <T, U> List<U> convertMapToOrderedKeyList(HashMap<U, T> m, Comparator<U> c) {
		List<U> r = new ArrayList<>(m.keySet());
		r.sort(c);
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
			int vc = m.viewedHistory().get().count();
			
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

	public static int[] getPlayedMinutes(CCStream<ICCPlayableElement> it) {
		int[] result = new int[60*24];

		for (ICCPlayableElement e : it)
		{
			for (CCDateTime v : e.viewedHistory().get().ccstream())
			{
				if (v.time.isUnspecifiedTime()) continue;
				if (!v.isValidDateTime()) continue;
				if (v.isUnspecifiedOrMinimum()) continue;

				int t = v.time.getMinuteOfDay();
				for (int i=0; i < e.length().get(); i++) result[(t+i)%(60*24)]++;
			}
		}

		return result;
	}

}
