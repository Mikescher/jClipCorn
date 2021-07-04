package de.jClipCorn.features.statistics.charts;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.statistics.StatisticsChart;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsTypeFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.formatter.ByteFormat;
import de.jClipCorn.util.stream.CCStreams;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatisticsMediaInfoFilesizeChart extends StatisticsChart {

	public StatisticsMediaInfoFilesizeChart(CCMovieList ml, StatisticsTypeFilter _source) {
		super(ml, _source);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist, StatisticsTypeFilter source) {
		NumberAxis xAxis = new NumberAxis(LocaleBundle.getString("StatisticsFrame.chartAxis.Size_mb")); //$NON-NLS-1$
		xAxis.setNumberFormatOverride(new ByteFormat());

	    NumberAxis yAxis = new NumberAxis(""); //$NON-NLS-1$

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
		List<Long> values = source
				.iteratorMoviesOrEpisodes(movielist)
				.filter(e -> e.mediaInfo().get().isSet())
				.map(e -> (e.mediaInfo().get().getFilesize().getMegaBytes()))
				.enumerate();

		long min = CCStreams.iterate(values).autoMinOrDefault(-1L);
		long max = CCStreams.iterate(values).autoMaxOrDefault(-1L);

		if (min == -1 || max == -1) { DefaultXYDataset ds = new DefaultXYDataset(); ds.addSeries("Series0", new double[2][0]); return ds; } //$NON-NLS-1$

		Map<Long, Integer> valuemap = CCStreams.iterate(values)
				.groupBy(d->d)
				.toMap(Map.Entry::getKey, p -> p.getValue().size());

		DefaultXYDataset dataset = new DefaultXYDataset();

		long d = min;
		List<Tuple<Double, Integer>> posdata = new ArrayList<>();
		while (d <= max) {
			posdata.add(Tuple.Create(d * (1024d * 1024d), valuemap.getOrDefault(d, 0)));

			d++;
		}

		double[][] series = new double[2][posdata.size()];
		
		for (int i = 0; i < posdata.size(); i++) {
			series[0][i] = posdata.get(i).Item1;
			series[1][i] = posdata.get(i).Item2;
		}
		
        dataset.addSeries("Series0", series); //$NON-NLS-1$

        return dataset;
	}

	@Override
	public String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.mediainfo_filesize"); //$NON-NLS-1$
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
