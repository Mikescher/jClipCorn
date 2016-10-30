package de.jClipCorn.gui.frames.statisticsFrame.charts;

import java.awt.Color;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.TextAnchor;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.helper.StatisticsHelper;

public class StatisticsSeriesTotalViewedChart extends StatisticsChart {

	private long domainTotalRangeMin;
	private long domainTotalRangeMax;
	private ValueAxis domainAxis;
	
	private class TupleSeriesEpList {
		public CCSeries series;
		public List<CCEpisode> episodes;
	}

	private NumberAxis valueAxis;
	private XYPlot plot;
	
	private HashMap<CCSeries, Integer> indexMap;
	private HashMap<Integer, XYDataset> datasetList;
	
	public StatisticsSeriesTotalViewedChart(CCMovieList ml) {
		super(ml);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist) {
		indexMap = new HashMap<>();
		datasetList = new HashMap<>();
		
		DateAxis dateAxis = new DateAxis(""); //$NON-NLS-1$

	    DateFormat chartFormatter = new SimpleDateFormat("dd.MM.yyyy"); //$NON-NLS-1$
	    dateAxis.setDateFormatOverride(chartFormatter);

	    valueAxis = new NumberAxis(LocaleBundle.getString("StatisticsFrame.chartAxis.Hours")); //$NON-NLS-1$
	    valueAxis.setNumberFormatOverride(new NumberFormat(){
			private static final long serialVersionUID = 8613030189889703487L;
			@Override
		    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) { return new StringBuffer(String.valueOf((int)(number/60))); }
		    @Override
		    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {  return new StringBuffer(String.valueOf((int)(number/60))); }
		    @Override
		    public Number parse(String source, ParsePosition parsePosition) { return null; }
	    });
	    
	    plot = new XYPlot(new DefaultXYDataset(), dateAxis, valueAxis, new StandardXYItemRenderer(StandardXYItemRenderer.LINES, null, null));
	    
	    List<TupleSeriesEpList> serieslist = getIncludedSeries(movielist);
	    CCDate startdate = GetStartDate(serieslist);
	    CCDate enddate = GetEndDate(serieslist);
	    
	    int idx = 0;
	    for (TupleSeriesEpList series : serieslist) {
	    	indexMap.put(series.series, idx);
	    	
	    	Color cf = new Color(StatisticsHelper.CHART_COLORS[idx % StatisticsHelper.CHART_COLORS.length]);
	    	Color ca = new Color(cf.getRed(), cf.getGreen(), cf.getBlue(), 50);
	    	
	    	XYDataset dataset = getDataSet(series, startdate, enddate, false);
	    	XYDataset startset = getDataSet(series, startdate, enddate, true);
	    	
	    	datasetList.put(3*idx, dataset);
	    	plot.setDataset(3*idx, dataset);
	    	plot.setRenderer(3*idx, new XYAreaRenderer(XYAreaRenderer.AREA, null, null));
	    	plot.getRenderer(3*idx).setSeriesPaint(0, ca);
	    	plot.getRenderer(3*idx).setSeriesVisibleInLegend(0, false);

	    	datasetList.put(3*idx + 1, dataset);
	    	plot.setDataset(3*idx + 1, dataset);
	    	plot.setRenderer(3*idx + 1, new StandardXYItemRenderer(StandardXYItemRenderer.LINES, null, null));
	    	plot.getRenderer(3*idx + 1).setSeriesPaint(0, cf);
	    	plot.getRenderer(3*idx + 1).setSeriesVisibleInLegend(0, true);

	    	datasetList.put(3*idx + 2, startset);
	    	plot.setDataset( 3*idx + 2, startset);
	    	plot.setRenderer(3*idx + 2, new StandardXYItemRenderer(StandardXYItemRenderer.LINES, null, null));
	    	plot.getRenderer(3*idx + 2).setSeriesPaint(0, cf);
	    	plot.getRenderer(3*idx + 2).setSeriesVisibleInLegend(0, false);
	    	plot.getRenderer(3*idx + 2).setBaseItemLabelGenerator(new StandardXYItemLabelGenerator("{0}")); //$NON-NLS-1$
	    	plot.getRenderer(3*idx + 2).setBaseItemLabelPaint(cf);
	    	plot.getRenderer(3*idx + 2).setBaseItemLabelsVisible(true);
	    	plot.getRenderer(3*idx + 2).setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_RIGHT, TextAnchor.BOTTOM_RIGHT, 0.262f));
	    	plot.getRenderer(3*idx + 2).setSeriesItemLabelsVisible(0, true);
	    	
	    	idx++;
		}

        domainTotalRangeMin = startdate.asMilliseconds();
        domainTotalRangeMax = enddate.asMilliseconds();
	    
        if (domainTotalRangeMin == domainTotalRangeMax) domainTotalRangeMax++;
        
		plot.setBackgroundPaint(XYBACKGROUND_COLOR);
		plot.setDomainGridlinePaint(GRIDLINECOLOR);
		plot.setRangeGridlinePaint(GRIDLINECOLOR);
	    
	    JFreeChart chart = new JFreeChart(plot);
	    
	    chart.setBackgroundPaint(null);
	    plot.getDomainAxis().setTickLabelPaint(TEXT_FOREGROUND);
	    plot.getRangeAxis().setTickLabelPaint(TEXT_FOREGROUND);
	    
	    domainAxis = plot.getDomainAxis();
		
	    return chart;
	}
	
	private XYDataset getDataSet(TupleSeriesEpList series, CCDate mindate, CCDate maxdate, boolean single) {
		mindate = mindate.getSubDay(1);
		
		long minMilliecs = mindate.asMilliseconds();
		int daycount = mindate.getDayDifferenceTo(maxdate) + 1;
		
		DefaultXYDataset dataset = new DefaultXYDataset();
		
		List<Integer> posx = new Vector<>();
		List<Integer> posy = new Vector<>();
		
		for (int i = 0; i < daycount; i++) {
			posx.add(i);
			posy.add(0);
		}
		
		int firstIdx = mindate.getDayDifferenceTo(series.episodes.get(0).getViewedHistoryFirst());
		int lastIdx = 0;
		int minutesum = 0;
		for (CCEpisode episode : series.episodes) {
			int idx = mindate.getDayDifferenceTo(episode.getViewedHistoryFirst());
			lastIdx = idx;
			minutesum += episode.getLength();
			
			posy.set(idx, minutesum);
		}
		
		int valueYSum = 0;
		for (int idx = firstIdx; idx <= lastIdx; idx++) {
			valueYSum = Math.max(valueYSum, posy.get(idx));
			
			posy.set(idx, valueYSum);
		}

		if (single) {
			CCEpisode episode = series.episodes.get(series.episodes.size() - 1);
			
			int idx = mindate.getDayDifferenceTo(episode.getViewedHistoryFirst());
			int value = posy.get(idx) + 1;
			
			posx.clear();
			posx.add(idx);
			posy.clear();
			posy.add(value);
		}
		
		double[][] dataseries = new double[2][posx.size()];
		
		for (int i = 0; i < posx.size(); i++) {
			dataseries[0][i] = minMilliecs + posx.get(i) * CCDate.MILLISECONDS_PER_DAY;
			dataseries[1][i] = posy.get(i);
		}
		
        dataset.addSeries(series.series.getTitle() , dataseries);
        
        return dataset;
	}
	
	private List<TupleSeriesEpList> getIncludedSeries(CCMovieList movielist) {
		List<TupleSeriesEpList> result = new ArrayList<>();
		
		for (CCSeries series : movielist.iteratorSeries()) {
			List<CCEpisode> expeplist = StatisticsHelper.getEpisodesWithExplicitLastViewedDate(series);
			
			if (! expeplist.isEmpty()) {
				TupleSeriesEpList tsel = new TupleSeriesEpList();
				tsel.series = series;
				tsel.episodes = expeplist;
				result.add(tsel);
			}
		}
		
		Collections.sort(result, new Comparator<TupleSeriesEpList>() {
			@Override
			public int compare(TupleSeriesEpList o1, TupleSeriesEpList o2) {
				return CCDate.compare(o1.episodes.get(0).getViewedHistoryFirst(), o2.episodes.get(0).getViewedHistoryFirst());
			}
		});
		
		return result;
	}
	
	private CCDate GetStartDate(List<TupleSeriesEpList> serieslist) {
		CCDate min = CCDate.getMaximumDate();
		
		for (TupleSeriesEpList series : serieslist) {
			for (CCEpisode episode : series.episodes) {
				min = CCDate.min(min, episode.getViewedHistoryFirst());
			}
		}
		
		return min;
	}
	
	private CCDate GetEndDate(List<TupleSeriesEpList> serieslist) {
		CCDate max = CCDate.getMinimumDate();
		
		for (TupleSeriesEpList series : serieslist) {
			for (CCEpisode episode : series.episodes) {
				max = CCDate.max(max, episode.getViewedHistoryFirst());
			}
		}
		
		return max.getAddDay(1);
	}

	@Override
	protected String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.seriesTotalViewed"); //$NON-NLS-1$
	}

	@Override
	public boolean usesFilterableSeries() {
		return true;
	}

	@Override
	public void onHideSeries(Map<CCSeries, Boolean> map) {
		for (Entry<CCSeries, Boolean> entry : map.entrySet()) {
			if (indexMap.containsKey(entry.getKey())) {
				int idx = indexMap.get(entry.getKey());

				if (entry.getValue()) {
					plot.setDataset(3*idx+0, datasetList.get(3*idx+0));
					plot.setDataset(3*idx+1, datasetList.get(3*idx+1));
					plot.setDataset(3*idx+2, datasetList.get(3*idx+2));
				} else {
					plot.setDataset(3*idx+0, null);
					plot.setDataset(3*idx+1, null);
					plot.setDataset(3*idx+2, null);
				}
			}
		}
		
		JFreeChart chart = new JFreeChart(plot);
		chart.setBackgroundPaint(null);
		
		updateChart(chart);
	}

	@Override
	public boolean usesFilterableYearRange() {
		return true;
	}
	
	@Override
	public void onFilterYearRange(int year) {
		if (year == -1) {
			domainAxis.setRange(domainTotalRangeMin, domainTotalRangeMax);
		} else {
			domainAxis.setRange(CCDate.create(1, 1, year).asMilliseconds(), CCDate.create(1, 1, year+1).asMilliseconds());
		}
	}
}
