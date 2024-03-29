package de.jClipCorn.features.statistics.charts;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.features.statistics.StatisticsChart;
import de.jClipCorn.features.statistics.StatisticsHelper;
import de.jClipCorn.features.statistics.StatisticsTypeFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datetime.CCDate;
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
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;

public class StatisticsSeriesViewedChart extends StatisticsChart {

	private long domainTotalRangeMin;
	private long domainTotalRangeMax;
	private ValueAxis domainAxis;
	
	private static class TupleSeriesEpList {
		public CCSeries series;
		public List<CCEpisode> episodes;
	}

	private NumberAxis valueAxis;
	private XYPlot plot;
	
	private HashMap<CCSeries, Integer> indexMap;
	private HashMap<Integer, XYDataset> datasetList;
	
	private JFreeChart chart;
	
	public StatisticsSeriesViewedChart(CCMovieList ml, StatisticsTypeFilter _source) {
		super(ml, _source);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist, StatisticsTypeFilter source) {
		if (chart != null) return chart;
		
		indexMap = new HashMap<>();
		datasetList = new HashMap<>();
		
		DateAxis dateAxis = new DateAxis(""); //$NON-NLS-1$

	    DateFormat chartFormatter = new SimpleDateFormat("dd.MM.yyyy"); //$NON-NLS-1$
	    dateAxis.setDateFormatOverride(chartFormatter);

	    valueAxis = new NumberAxis(""); //$NON-NLS-1$
	    valueAxis.setRange(0, 35);
	    
	    plot = new XYPlot(new DefaultXYDataset(), dateAxis, valueAxis, new StandardXYItemRenderer(StandardXYItemRenderer.LINES, null, null));
	    
	    List<TupleSeriesEpList> serieslist = getIncludedSeries(movielist);
	    CCDate startdate = GetStartDate(serieslist);
	    CCDate enddate = GetEndDate(serieslist);
	    
	    int idx = 0;
	    for (TupleSeriesEpList series : serieslist) {
	    	indexMap.put(series.series, idx);
	    	
	    	Color cf = new Color(StatisticsHelper.CHART_COLORS[idx % StatisticsHelper.CHART_COLORS.length]);
	    	Color ca = new Color(cf.getRed(), cf.getGreen(), cf.getBlue(), 178);
	    	
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
	    	plot.getRenderer(3*idx + 1).setSeriesVisibleInLegend(0, false);

	    	datasetList.put(3*idx + 2, startset);
	    	plot.setDataset( 3*idx + 2, startset);
	    	plot.setRenderer(3*idx + 2, new StandardXYItemRenderer(StandardXYItemRenderer.LINES, null, null));
	    	plot.getRenderer(3*idx + 2).setSeriesPaint(0, cf);
	    	plot.getRenderer(3*idx + 2).setSeriesVisibleInLegend(0, false);
	    	plot.getRenderer(3*idx + 2).setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator("{0}")); //$NON-NLS-1$
	    	plot.getRenderer(3*idx + 2).setDefaultItemLabelPaint(cf);
	    	plot.getRenderer(3*idx + 2).setDefaultItemLabelsVisible(true);
	    	plot.getRenderer(3*idx + 2).setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_LEFT, TextAnchor.BOTTOM_LEFT, -45));
	    	plot.getRenderer(3*idx + 2).setSeriesItemLabelsVisible(0, true);
	    	
	    	idx++;
		}

        domainTotalRangeMin = startdate.asMilliseconds();
        domainTotalRangeMax = enddate.asMilliseconds();
	    
        if (domainTotalRangeMin == domainTotalRangeMax) domainTotalRangeMax++;
        
		plot.setBackgroundPaint(XYBACKGROUND_COLOR);
		plot.setDomainGridlinePaint(GRIDLINECOLOR);
		plot.setRangeGridlinePaint(GRIDLINECOLOR);
	    
	    chart = new JFreeChart(plot);
	    //chart.removeLegend();
	    
	    chart.setBackgroundPaint(null);
	    plot.getDomainAxis().setTickLabelPaint(TEXT_FOREGROUND);
	    plot.getRangeAxis().setTickLabelPaint(TEXT_FOREGROUND);
		plot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	    
	    domainAxis = plot.getDomainAxis();
	    
	    return chart;
	}
	
	@Override
	public void onResize(ComponentEvent e) {
		if (valueAxis == null || chart == null) return;
		
		valueAxis.setRange(0, e.getComponent().getHeight() / 25f);
		chart.fireChartChanged();
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
		
		int ymax = 7;
		for (CCEpisode episode : series.episodes) {
			int idx = mindate.getDayDifferenceTo(episode.getViewedHistoryFirst());
			posy.set(idx, posy.get(idx) + 1);
			ymax = Math.max(ymax, posy.get(idx));
		}

		if (single) {
			CCEpisode episode = series.episodes.get(0);
			
			int idx = mindate.getDayDifferenceTo(episode.getViewedHistoryFirst());
			int value = ymax + 1;
			
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
		
		return max;
	}

	@Override
	public String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.seriesViewed"); //$NON-NLS-1$
	}

	@Override
	public boolean usesFilterableSeries() {
		return true;
	}

	@Override
	protected void onChangeFilter(Map<CCSeries, Boolean> map) {
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
	public StatisticsTypeFilter[] supportedTypes() {
		return new StatisticsTypeFilter[]{StatisticsTypeFilter.STF_MOVIES, StatisticsTypeFilter.STF_EPISODES, StatisticsTypeFilter.STF_MOVIES_AND_EPISODES};
	}
}
