package de.jClipCorn.features.statistics.charts;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import de.jClipCorn.features.statistics.StatisticsChart;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.features.statistics.StatisticsHelper;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsTypeFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.stream.CCStream;

public class StatisticsLengthOverTimeChart extends StatisticsChart {

	private long domainTotalRangeMin;
	private long domainTotalRangeMax;
	private ValueAxis domainAxis;

	public StatisticsLengthOverTimeChart(CCMovieList ml, StatisticsTypeFilter _source) {
		super(ml, _source);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist, StatisticsTypeFilter source) {
		DateAxis dateAxis = new DateAxis(""); //$NON-NLS-1$

	    DateFormat chartFormatter = new SimpleDateFormat("dd.MM.yyyy"); //$NON-NLS-1$
	    dateAxis.setDateFormatOverride(chartFormatter);

	    NumberAxis valueAxis = new NumberAxis(""); //$NON-NLS-1$

	    StandardXYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, null, null);
	    
	    XYPlot plot = new XYPlot(getDataSet(movielist, source), dateAxis, valueAxis, renderer);
	    
		plot.getRenderer().setSeriesPaint(0, XYCHART_COLOR);
		
		plot.setBackgroundPaint(XYBACKGROUND_COLOR);
		plot.setDomainGridlinePaint(GRIDLINECOLOR);
		plot.setRangeGridlinePaint(GRIDLINECOLOR);
	    
	    JFreeChart chart = new JFreeChart(plot);
	    chart.removeLegend();
	    
	    chart.setBackgroundPaint(null);
	    plot.getDomainAxis().setTickLabelPaint(TEXT_FOREGROUND);
	    plot.getRangeAxis().setTickLabelPaint(TEXT_FOREGROUND);
	    
		plot.getRangeAxis().setLabel(LocaleBundle.getString("StatisticsFrame.chartAxis.Mins")); //$NON-NLS-1$
		plot.getDomainAxis().setLabel(LocaleBundle.getString("StatisticsFrame.chartAxis.Date")); //$NON-NLS-1$
		
		plot.getRangeAxis().setLabelPaint(TEXT_FOREGROUND);
		plot.getDomainAxis().setLabelPaint(TEXT_FOREGROUND);
	    
	    domainAxis = plot.getDomainAxis();
	    
	    return chart;
	}
	
	private XYDataset getDataSet(CCMovieList movielist, StatisticsTypeFilter source) {
		CCStream<ICCPlayableElement> it = source.iteratorMoviesOrEpisodes(movielist);
		
		CCDate mindate = StatisticsHelper.getFirstAddDate(movielist.iteratorPlayables());
		long minMilliecs = mindate.asMilliseconds();
		CCDate maxdate = StatisticsHelper.getLastAddDate(movielist.iteratorPlayables());
		int daycount = mindate.getDayDifferenceTo(maxdate) + 1;
		
		DefaultXYDataset dataset = new DefaultXYDataset();
		
		int[] allpos = StatisticsHelper.getCumulativeMinuteCountForAllDates(mindate, daycount, it);
		
		double[][] series = new double[2][allpos.length];
		
		for (int i = 0; i < allpos.length; i++) {
			series[0][i] = minMilliecs + i * CCDate.MILLISECONDS_PER_DAY;
			series[1][i] = allpos[i];
		}
		
        dataset.addSeries("Series0", series); //$NON-NLS-1$

        domainTotalRangeMin = (long)series[0][0];
        domainTotalRangeMax = (long)series[0][allpos.length - 1];
        
        if (domainTotalRangeMin == domainTotalRangeMax) domainTotalRangeMax++;
        
        return dataset;
	}

	@Override
	public String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.hoursOverTime"); //$NON-NLS-1$
	}

	@Override
	public boolean usesFilterableSeries() {
		return false;
	}

	@Override
	public boolean usesFilterableYearRange() {
		return true;
	}
	
	@Override
	protected void onFilterYearRange(int year) {
		if (year == -1) {
			domainAxis.setRange(domainTotalRangeMin, domainTotalRangeMax);
		} else {
			domainAxis.setRange(CCDate.create(1, 1, year).asMilliseconds(), CCDate.create(1, 1, year+1).asMilliseconds());
		}
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
