package de.jClipCorn.gui.frames.statisticsFrame.charts;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.helper.StatisticsHelper;

public class StatisticsMovieLengthChart extends StatisticsChart {
	public StatisticsMovieLengthChart(CCMovieList ml) {
		super(ml);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist) {
		NumberAxis xAxis = new NumberAxis(LocaleBundle.getString("StatisticsFrame.chartAxis.MovieLength")); //$NON-NLS-1$

	    NumberAxis yAxis = new NumberAxis(""); //$NON-NLS-1$
	    
	    yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	    
	    StandardXYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, null, null);
	    
	    XYPlot plot = new XYPlot(getDataSet(movielist), xAxis, yAxis, renderer);
	    
	    plot.getRenderer().setSeriesPaint(0, XYCHART_COLOR);
	    
	    plot.setBackgroundPaint(XYBACKGROUND_COLOR);
		plot.setDomainGridlinePaint(GRIDLINECOLOR);
		plot.setRangeGridlinePaint(GRIDLINECOLOR);
	    
	    JFreeChart chart = new JFreeChart(plot);
	    chart.removeLegend();
	    
	    chart.setBackgroundPaint(null);
	    plot.getDomainAxis().setTickLabelPaint(TEXT_FOREGROUND);
	    plot.getRangeAxis().setTickLabelPaint(TEXT_FOREGROUND);
	    
	    return chart;
	}
	
	private XYDataset getDataSet(CCMovieList movielist) {
		int minLen = StatisticsHelper.getMinimumMovieLength(movielist);
		int maxLen = StatisticsHelper.getMaximumMovieLength(movielist);
		
		int count = (maxLen - minLen) + 1;
		
		double[][] series = StatisticsHelper.getMovieCountForAllLengths(movielist, minLen, count);
		
		DefaultXYDataset dataset = new DefaultXYDataset();
		
        dataset.addSeries("Series0", series); //$NON-NLS-1$
        
        return dataset;
	}

	@Override
	protected String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.movielen"); //$NON-NLS-1$
	}

}
