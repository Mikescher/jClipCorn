package de.jClipCorn.gui.frames.statisticsFrame;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.frames.statisticsFrame.charts.*;
import de.jClipCorn.util.lambda.Func1to1;

public final class ClipCornStatistics {
	private ClipCornStatistics() { throw new InstantiationError(); }
	
	public final static List<Func1to1<CCMovieList, StatisticsGroup>> STATISTICS = new ArrayList<>();
	
	static {
		
		// ======== SIMPLE PROPERTIES =========
		
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsAddDateChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsLengthChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsFormatChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsQualityChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsOnlinescoreChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsScoreChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsViewedChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsYearChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsGenreChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsFSKChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsLanguageChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsTagChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsProviderMainChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsProviderAnyChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsGroupChart(ml, s)));

		// ======== SPECIAL PROPERTIES =========

		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsWatchCountChart(ml, s)));

		// ======== PROPERTIES PLOTTED OVER TIME =========

		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsLengthOverTimeChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsViewedOverTimeChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsSizeOverTimeChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsFormatOverTimeChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsQualityOverTimeChart(ml, s)));

		// ======== SERIES-ONLY CHARTS =========

		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsSeriesViewedChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsSeriesTotalViewedChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsSeriesTimeline(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsSeriesTimelineCombined(ml, s)));

		
	}
}
