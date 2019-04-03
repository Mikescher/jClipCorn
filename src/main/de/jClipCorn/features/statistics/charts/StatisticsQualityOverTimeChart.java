package de.jClipCorn.features.statistics.charts;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.jClipCorn.features.statistics.StatisticsChart;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.util.ShapeUtilities;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;
import de.jClipCorn.features.statistics.StatisticsHelper;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsTypeFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.stream.CCStream;

public class StatisticsQualityOverTimeChart extends StatisticsChart {

	private long domainTotalRangeMin;
	private long domainTotalRangeMax;
	private ValueAxis domainAxis;

	public StatisticsQualityOverTimeChart(CCMovieList ml, StatisticsTypeFilter _source) {
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
		LegendItemCollection coll = new LegendItemCollection();
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
	    	plot.getRenderer(2*idx+1).setSeriesShape(0, ShapeUtilities.createDiamond(5));

			coll.add(new LegendItem(datasets.get(idx).getSeriesKey(0).toString(), "", "", "", ShapeUtilities.createDiamond(5), cf)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		plot.setFixedLegendItems(coll);
		
		plot.setBackgroundPaint(XYBACKGROUND_COLOR);
		plot.setDomainGridlinePaint(GRIDLINECOLOR);
		plot.setRangeGridlinePaint(GRIDLINECOLOR);
	    
		
	    JFreeChart chart = new JFreeChart(plot);
	    
	    chart.setBackgroundPaint(null);
	    plot.getDomainAxis().setTickLabelPaint(TEXT_FOREGROUND);
	    plot.getRangeAxis().setTickLabelPaint(TEXT_FOREGROUND);
	    
		plot.getDomainAxis().setLabel(LocaleBundle.getString("StatisticsFrame.chartAxis.Date")); //$NON-NLS-1$
		
		plot.getRangeAxis().setLabelPaint(TEXT_FOREGROUND);
		plot.getDomainAxis().setLabelPaint(TEXT_FOREGROUND);
		
		plot.getRangeAxis().setAutoRange(true);

	    domainAxis = plot.getDomainAxis();
	    
	    return chart;
	}
	
	private List<DefaultXYDataset> getDataSet(CCMovieList movielist, StatisticsTypeFilter source) {
		CCStream<ICCPlayableElement> it = source.iteratorMoviesOrEpisodes(movielist);
		
		CCDate mindate = StatisticsHelper.getFirstAddDate(movielist.iteratorPlayables());
		long minMilliecs = mindate.asMilliseconds();
		CCDate maxdate = StatisticsHelper.getLastAddDate(movielist.iteratorPlayables());
		int daycount = mindate.getDayDifferenceTo(maxdate) + 1;

		List<CCQuality> qualities = Arrays.asList(CCQuality.values());
		int[][] allLen = StatisticsHelper.getCumulativeQualityCountForAllDates(mindate, daycount, it);
		
		List<DefaultXYDataset> result = new ArrayList<>();
		for (int ifmt = 0; ifmt < qualities.size(); ifmt++) {
			double[][] series = new double[2][daycount];
	
			int fmtSum = 0;
			for (int i = 0; i < daycount; i++) {
				fmtSum += allLen[i][ifmt];
				
				series[0][i] = minMilliecs + i * CCDate.MILLISECONDS_PER_DAY;
				series[1][i] = fmtSum;
			}

			DefaultXYDataset dataset = new DefaultXYDataset();
	        dataset.addSeries("" + qualities.get(ifmt).asString(), series); //$NON-NLS-1$
			
			result.add(dataset);
		}

        domainTotalRangeMin = mindate.asMilliseconds();
        domainTotalRangeMax = maxdate.asMilliseconds();
		
        if (domainTotalRangeMin == domainTotalRangeMax) domainTotalRangeMax++;
        
		return result;
	}

	@Override
	public String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.qualityOverTime"); //$NON-NLS-1$
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
