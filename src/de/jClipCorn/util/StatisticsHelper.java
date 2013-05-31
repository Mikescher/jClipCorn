package de.jClipCorn.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieOnlineScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;

public class StatisticsHelper {
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

		s.div(ml.getMovieCount());

		return s;
	}

	public static CCMovieSize getAvgSeriesSize(CCMovieList ml) {
		CCMovieSize s = new CCMovieSize();

		for (Iterator<CCSeries> it = ml.iteratorSeries(); it.hasNext();) {
			s.add(it.next().getFilesize());
		}

		s.div(ml.getEpisodeCount());

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
		CCDate date = CCDate.getNewMaximumDate();
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
			if (m.getAddDate().isLessThan(date)) {
				date.set(m.getAddDate());
			}
		}
		
		return date;
	}
	
	public static CCDate getLastMovieAddDate(CCMovieList ml) {
		CCDate date = CCDate.getNewMinimumDate();
		
		for (Iterator<CCMovie> it = ml.iteratorMovies(); it.hasNext();) {
			CCMovie m = it.next();
			if (m.getAddDate().isGreaterThan(date)) {
				date.set(m.getAddDate());
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
}
