package de.jClipCorn.features.statistics;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.statistics.charts.*;
import de.jClipCorn.util.lambda.Func1to1;

public final class ClipCornStatistics {
	private ClipCornStatistics() { throw new InstantiationError(); }
	
	public final static List<Func1to1<CCMovieList, StatisticsGroup>> STATISTICS = new ArrayList<>();
	
	static {
		
		// ======== SIMPLE PROPERTIES =========
		
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsAddDateChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsLengthChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsFormatChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsOnlinescoreChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsScoreChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsViewedChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsYearChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsGenreChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsFSKChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsLanguageChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsLanguageListChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsTagChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsProviderMainChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsProviderAnyChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsGroupChart(ml, s)));

		// ======== SPECIAL PROPERTIES =========

		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsWatchCountChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsWatchTimeChart(ml, s)));

		// ======== PROPERTIES PLOTTED OVER TIME =========

		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsLengthOverTimeChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsViewedOverTimeChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsSizeOverTimeChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsFormatOverTimeChart(ml, s)));

		// ======== SERIES-ONLY CHARTS =========

		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsSeriesViewedChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsSeriesTotalViewedChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsSeriesTimeline(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsSeriesTimelineCombined(ml, s)));

		// ======== MEDIAINFO =========

		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsMediaInfoSetChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsMediaInfoCDateChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsMediaInfoMDateChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsMediaInfoFilesizeChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsMediaInfoDurationChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsMediaInfoBitrateChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsMediaInfoVideoFormatChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsMediaInfoVideoCodecChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsMediaInfoResolutionChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsMediaInfoFramerateChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsMediaInfoBitDepthChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsMediaInfoFramecountChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsMediaInfoAudioFormatChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsMediaInfoAudioCodecChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsMediaInfoAudioChannelsChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsMediaInfoSamplerateChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsMediaInfoQualityCategoryChart(ml, s)));
		STATISTICS.add(ml -> new StatisticsGroup(s -> new StatisticsMediaInfoQualityResolutionTypeChart(ml, s)));
	}
}
