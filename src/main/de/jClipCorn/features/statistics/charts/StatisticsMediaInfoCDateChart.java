package de.jClipCorn.features.statistics.charts;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.statistics.StatisticsChart;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsTypeFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.stream.CCStreams;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class StatisticsMediaInfoCDateChart extends StatisticsChart {

	private long domainTotalRangeMin;
	private long domainTotalRangeMax;
	private ValueAxis domainAxis;

	public StatisticsMediaInfoCDateChart(CCMovieList ml, StatisticsTypeFilter _source) {
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
	    
	    domainAxis = plot.getDomainAxis();
	    
	    return chart;
	}
	
	private XYDataset getDataSet(CCMovieList movielist, StatisticsTypeFilter source) {
		CCDate now = CCDate.getCurrentDate();
		List<CCDate> dates = source
				.iteratorMoviesOrEpisodes(movielist)
				.filter(e -> e.getMediaInfo().isSet())
				.map(e -> CCDateTime.createFromFileTimestamp(e.getMediaInfo().getCDate(), TimeZone.getDefault()).date.getSetDay(1))
				.filter(d -> d.isLessEqualsThan(now))
				.enumerate();
		
		CCDate mindate = CCStreams.iterate(dates).minOrDefault(CCDate::compare, CCDate.getUnspecified());
		CCDate maxdate = CCStreams.iterate(dates).maxOrDefault(CCDate::compare, CCDate.getUnspecified());

		if (mindate.isUnspecifiedDate() || maxdate.isUnspecifiedDate()) { DefaultXYDataset ds = new DefaultXYDataset(); ds.addSeries("Series0", new double[2][0]); return ds; } //$NON-NLS-1$

		Map<CCDate, Integer> hdata = CCStreams.iterate(dates)
				.groupBy(d->d)
				.toMap(Map.Entry::getKey, p -> p.getValue().size());

		DefaultXYDataset dataset = new DefaultXYDataset();

		CCDate d = mindate;
		List<Tuple<CCDate, Integer>> posdata = new ArrayList<>();
		while (!d.isUnspecifiedDate() && d.isLessEqualsThan(maxdate)) {
			posdata.add(Tuple.Create(d, hdata.getOrDefault(d, 0)));

			d = d.getAddMonth(1);
		}

		double[][] series = new double[2][posdata.size()];
		
		for (int i = 0; i < posdata.size(); i++) {
			series[0][i] = posdata.get(i).Item1.asMilliseconds();
			series[1][i] = posdata.get(i).Item2;
		}
		
        dataset.addSeries("Series0", series); //$NON-NLS-1$

        domainTotalRangeMin = (long)series[0][0];
        domainTotalRangeMax = (long)series[0][posdata.size() - 1];
        
        if (domainTotalRangeMin == domainTotalRangeMax) domainTotalRangeMax++;
        
        return dataset;
	}

	@Override
	public String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.mediainfo_cdate"); //$NON-NLS-1$
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
