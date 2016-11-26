package de.jClipCorn.gui.frames.statisticsFrame.charts;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.xy.DefaultXYDataset;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsTypeFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.cciterator.CCIterator;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.helper.StatisticsHelper;

public class StatisticsFormatOverTimeChart extends StatisticsChart {

	private long domainTotalRangeMin;
	private long domainTotalRangeMax;
	private ValueAxis domainAxis;

	public StatisticsFormatOverTimeChart(CCMovieList ml, StatisticsTypeFilter _source) {
		super(ml, _source);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist, StatisticsTypeFilter source) {
		DateAxis dateAxis = new DateAxis(""); //$NON-NLS-1$

	    DateFormat chartFormatter = new SimpleDateFormat("dd.MM.yyyy"); //$NON-NLS-1$
	    dateAxis.setDateFormatOverride(chartFormatter);

	    NumberAxis valueAxis = new NumberAxis(""); //$NON-NLS-1$
	    
	    XYPlot plot = new XYPlot(new DefaultXYDataset(), dateAxis, valueAxis, new StandardXYItemRenderer(StandardXYItemRenderer.LINES, null, null));
	    
	    List<DefaultXYDataset> datasets = getDataSet(movielist, source);
	    for (int idx = 0; idx < datasets.size(); idx++) {
	    	Color cf = new Color(StatisticsHelper.CHART_COLORS[idx % StatisticsHelper.CHART_COLORS.length]);
	    	Color ca = new Color(cf.getRed(), cf.getGreen(), cf.getBlue(), 50);
	    	
	    	plot.setDataset( 2*idx+0, datasets.get(idx));
	    	plot.setRenderer(2*idx+0, new XYAreaRenderer(XYAreaRenderer.AREA, null, null));
	    	plot.getRenderer(2*idx+0).setSeriesPaint(0, ca);
	    	plot.getRenderer(2*idx+0).setSeriesVisibleInLegend(0, false);

	    	plot.setDataset( 2*idx+1, datasets.get(idx));
	    	plot.setRenderer(2*idx+1, new StandardXYItemRenderer(StandardXYItemRenderer.LINES, null, null));
	    	plot.getRenderer(2*idx+1).setSeriesPaint(0, cf);
	    	plot.getRenderer(2*idx+1).setSeriesVisibleInLegend(0, true);
		}
	    
		plot.setBackgroundPaint(XYBACKGROUND_COLOR);
		plot.setDomainGridlinePaint(GRIDLINECOLOR);
		plot.setRangeGridlinePaint(GRIDLINECOLOR);
	    
	    JFreeChart chart = new JFreeChart(plot);
	    
	    chart.setBackgroundPaint(null);
	    plot.getDomainAxis().setTickLabelPaint(TEXT_FOREGROUND);
	    plot.getRangeAxis().setTickLabelPaint(TEXT_FOREGROUND);
	    
		plot.getRangeAxis().setLabel(LocaleBundle.getString("StatisticsFrame.chartAxis.Perc")); //$NON-NLS-1$
		plot.getDomainAxis().setLabel(LocaleBundle.getString("StatisticsFrame.chartAxis.Date")); //$NON-NLS-1$
		
		plot.getRangeAxis().setLabelPaint(TEXT_FOREGROUND);
		plot.getDomainAxis().setLabelPaint(TEXT_FOREGROUND);
		
		plot.getRangeAxis().setAutoRange(false);
		plot.getRangeAxis().setRange(0, 100);
	    
	    domainAxis = plot.getDomainAxis();
	    
	    return chart;
	}
	
	private List<DefaultXYDataset> getDataSet(CCMovieList movielist, StatisticsTypeFilter source) {
		CCIterator<ICCPlayableElement> it = source.iteratorMoviesOrEpisodes(movielist);
		
		CCDate mindate = StatisticsHelper.getFirstAddDate(it);
		long minMilliecs = mindate.asMilliseconds();
		CCDate maxdate = StatisticsHelper.getLastAddDate(it);
		int daycount = mindate.getDayDifferenceTo(maxdate) + 1;

		List<CCMovieFormat> formats = Arrays.asList(CCMovieFormat.values());
		int[][] allLen = StatisticsHelper.getCumulativeFormatLengthForAllDates(mindate, daycount, it);
		
		List<DefaultXYDataset> result = new ArrayList<>();
		for (int ifmt = 0; ifmt < formats.size(); ifmt++) {
			double[][] series = new double[2][daycount];
	
			int lenSum = 0;
			int fmtSum = 0;
			for (int i = 0; i < daycount; i++) {
				for (int ifmt2 = 0; ifmt2 < formats.size(); ifmt2++) {
					lenSum += allLen[i][ifmt2];
				}

				fmtSum += allLen[i][ifmt];
				
				series[0][i] = minMilliecs + i * CCDate.MILLISECONDS_PER_DAY;
				series[1][i] = (100d * fmtSum) / lenSum;
			}

			DefaultXYDataset dataset = new DefaultXYDataset();
	        dataset.addSeries("" + formats.get(ifmt), series); //$NON-NLS-1$
			
			result.add(dataset);
		}

        domainTotalRangeMin = mindate.asMilliseconds();
        domainTotalRangeMax = maxdate.asMilliseconds();
		
        if (domainTotalRangeMin == domainTotalRangeMax) domainTotalRangeMax++;
        
		return result;
	}

	@Override
	protected String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.formatOverTime"); //$NON-NLS-1$
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
	protected StatisticsTypeFilter supportedTypes() {
		return StatisticsTypeFilter.BOTH;
	}

	@Override
	public String createToggleTwoCaption() {
		return LocaleBundle.getString("StatisticsFrame.this.toggleEpisodes"); //$NON-NLS-1$
	}
}
