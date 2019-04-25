package de.jClipCorn.features.statistics.charts;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.features.statistics.StatisticsChart;
import de.jClipCorn.features.statistics.StatisticsHelper;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsTypeFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.stream.CCStream;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class StatisticsWatchTimeChart extends StatisticsChart {
	public StatisticsWatchTimeChart(CCMovieList ml, StatisticsTypeFilter _source) {
		super(ml, _source);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist, StatisticsTypeFilter source) {
		DateAxis xAxis = new DateAxis(LocaleBundle.getString("StatisticsFrame.chartAxis.Time"), TimeZone.getTimeZone("GMT"), Locale.getDefault()); //$NON-NLS-1$ //$NON-NLS-2$
		xAxis.setRange(new Date(0), new Date(60*60*24*1000 - 1));

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
		CCStream<ICCPlayableElement> it = source.iteratorMoviesOrEpisodes(movielist);

		int[] mins = StatisticsHelper.getPlayedMinutes(it);

		TimeSeries series = new TimeSeries("Series0"); //$NON-NLS-1$

		for (int i=0; i < 60*24; i++) series.add(new FixedMillisecond(i * 60 * 1000), mins[i]);

        return new TimeSeriesCollection(series);
	}

	@Override
	public String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.watchtime"); //$NON-NLS-1$
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
	public StatisticsTypeFilter supportedTypes() {
		return StatisticsTypeFilter.BOTH;
	}

	@Override
	public String createToggleTwoCaption() {
		return LocaleBundle.getString("StatisticsFrame.this.toggleEpisodes"); //$NON-NLS-1$
	}
}
