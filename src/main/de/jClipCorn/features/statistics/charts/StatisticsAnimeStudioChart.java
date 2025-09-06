package de.jClipCorn.features.statistics.charts;

import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.features.statistics.PieRotator;
import de.jClipCorn.features.statistics.StatisticsChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.util.Rotation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.statistics.StatisticsHelper;
import de.jClipCorn.features.statistics.StatisticsTypeFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.stream.CCStream;

import java.util.HashMap;
import java.util.Map;

public class StatisticsAnimeStudioChart extends StatisticsChart {
	public StatisticsAnimeStudioChart(CCMovieList ml, StatisticsTypeFilter _source) {
		super(ml, _source);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist, StatisticsTypeFilter source) {
		JFreeChart chart = ChartFactory.createPieChart3D(
	            "",      //$NON-NLS-1$
	            getDataSet(movielist, source),               
	            false,                  
	            true, // tooltips
	            false
	    );

		chart.removeLegend();

		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		plot.setStartAngle(290);
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setForegroundAlpha(0.5f);

		plot.setBackgroundPaint(XYBACKGROUND_COLOR);

		chart.setBackgroundPaint(null);

		if (ROTATE_PIE) {
			PieRotator rotator = new PieRotator(plot);
			rotator.start();
		}

	    return chart;
	}
	
	private PieDataset getDataSet(CCMovieList movielist, StatisticsTypeFilter source) {
		CCStream<CCSeries> it = movielist.iteratorSeries();
		
		DefaultPieDataset dataset = new DefaultPieDataset();
		
		HashMap<String, Integer> values = StatisticsHelper.getCountForAllAnimeStudios(it);
		
		for (Map.Entry<String, Integer> entry : values.entrySet()) {
			dataset.setValue(entry.getKey() + " [" + entry.getValue() + "]", entry.getValue()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
        return dataset;
	}

	@Override
	public String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.animestudio"); //$NON-NLS-1$
	}

	@Override
	public boolean usesFilterableSeries() {
		return false;
	}
	
	@Override
	public boolean usesFilterableYearRange() {
		return false;
	}

	@Override
	public StatisticsTypeFilter[] supportedTypes() {
		return new StatisticsTypeFilter[]{StatisticsTypeFilter.STF_SERIES};
	}
}