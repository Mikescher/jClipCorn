package de.jClipCorn.features.statistics.charts;

import de.jClipCorn.features.statistics.StatisticsChart;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.features.statistics.StatisticsHelper;
import de.jClipCorn.features.statistics.StatisticsTypeFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.stream.CCStream;

public class StatisticsLengthChart extends StatisticsChart {
	public StatisticsLengthChart(CCMovieList ml, StatisticsTypeFilter _source) {
		super(ml, _source);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist, StatisticsTypeFilter source) {
		NumberAxis xAxis = new NumberAxis(LocaleBundle.getString("StatisticsFrame.chartAxis.Length")); //$NON-NLS-1$

	    NumberAxis yAxis = new NumberAxis(""); //$NON-NLS-1$
	    
	    yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	    
	    StandardXYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, null, null);
	    
	    XYPlot plot = new XYPlot(getDataSet(movielist, source), xAxis, yAxis, renderer);
	    
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
	
	private XYDataset getDataSet(CCMovieList movielist, StatisticsTypeFilter source) {
		CCStream<ICCPlayableElement> it = source.iterator(movielist).cast();
		
		int minLen = StatisticsHelper.getMinimumLength(it);
		int maxLen = StatisticsHelper.getMaximumLength(it);
		
		int count = (maxLen - minLen) + 1;
		
		double[][] series = StatisticsHelper.getCountForAllLengths(minLen, count, it);
		
		DefaultXYDataset dataset = new DefaultXYDataset();
		
        dataset.addSeries("Series0", series); //$NON-NLS-1$
        
        return dataset;
	}

	@Override
	public String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.length"); //$NON-NLS-1$
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
