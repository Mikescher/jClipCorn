package de.jClipCorn.features.statistics.charts;

import de.jClipCorn.features.statistics.PieRotator;
import de.jClipCorn.features.statistics.StatisticsChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.util.Rotation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.features.statistics.StatisticsHelper;
import de.jClipCorn.features.statistics.StatisticsTypeFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.stream.CCStream;

public class StatisticsViewedChart extends StatisticsChart {
	public StatisticsViewedChart(CCMovieList ml, StatisticsTypeFilter _source) {
		super(ml, _source);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist, StatisticsTypeFilter source) {
		JFreeChart chart = ChartFactory.createPieChart3D(
	            "",      //$NON-NLS-1$
	            getDataSet(movielist, source),               
	            false,                  
	            false,
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
		CCStream<ICCPlayableElement> it = source.iterator(movielist).cast();
		
		int vc = StatisticsHelper.getViewedCount(it);
		int nvc = StatisticsHelper.getUnviewedCount(it);
		
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue(LocaleBundle.getString("StatisticsFrame.chartAxis.PieViewed") + " [" + vc + "]", vc); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		dataset.setValue(LocaleBundle.getString("StatisticsFrame.chartAxis.PieUnviewed") + " [" + nvc + "]", nvc); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
        return dataset;
	}

	@Override
	public String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.viewed"); //$NON-NLS-1$
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
		return new StatisticsTypeFilter[]{StatisticsTypeFilter.STF_MOVIES, StatisticsTypeFilter.STF_EPISODES, StatisticsTypeFilter.STF_MOVIES_AND_EPISODES};
	}
}
