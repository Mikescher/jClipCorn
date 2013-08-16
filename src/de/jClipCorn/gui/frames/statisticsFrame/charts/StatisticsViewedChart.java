package de.jClipCorn.gui.frames.statisticsFrame.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.helper.StatisticsHelper;

public class StatisticsViewedChart extends StatisticsChart {
	public StatisticsViewedChart(CCMovieList ml) {
		super(ml);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist) {
		JFreeChart chart = ChartFactory.createPieChart3D(
	            "",      //$NON-NLS-1$
	            getDataSet(movielist),               
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
	
	private PieDataset getDataSet(CCMovieList movielist) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue(LocaleBundle.getString("StatisticsFrame.chartAxis.PieViewed"), StatisticsHelper.getMovieViewedCount(movielist)); //$NON-NLS-1$
		dataset.setValue(LocaleBundle.getString("StatisticsFrame.chartAxis.PieUnviewed"), StatisticsHelper.getMovieUnviewedCount(movielist)); //$NON-NLS-1$
		
        return dataset;
	}

	@Override
	protected String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.viewed"); //$NON-NLS-1$
	}
}
