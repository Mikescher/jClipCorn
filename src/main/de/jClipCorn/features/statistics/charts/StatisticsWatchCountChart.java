package de.jClipCorn.features.statistics.charts;

import de.jClipCorn.features.statistics.StatisticsChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.features.statistics.StatisticsHelper;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsTypeFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.stream.CCStream;

public class StatisticsWatchCountChart extends StatisticsChart {

	public StatisticsWatchCountChart(CCMovieList ml, StatisticsTypeFilter _source) {
		super(ml, _source);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist, StatisticsTypeFilter source) {
		JFreeChart chart = ChartFactory.createBarChart(
				"",  //$NON-NLS-1$
				"",  //$NON-NLS-1$
				"",  //$NON-NLS-1$
				getDataSet(movielist, source), 
				PlotOrientation.VERTICAL, 
				false, 
				false, 
				false
				);

		chart.removeLegend();
		
		CategoryPlot plot = chart.getCategoryPlot();
		
		BarRenderer renderer = new BarRenderer();
		renderer.setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator());
		renderer.setSeriesItemLabelsVisible(0, true);
		renderer.setSeriesPositiveItemLabelPosition(0, new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
		renderer.setSeriesItemLabelPaint(0, BARCHART_ITEM_COLOR);
		renderer.setSeriesPaint(0, BARCHART_COLOR);
		renderer.setBarPainter(new StandardBarPainter());
		plot.setRenderer(renderer);
		
		plot.setBackgroundPaint(XYBACKGROUND_COLOR);
		plot.setDomainGridlinePaint(GRIDLINECOLOR);
		plot.setRangeGridlinePaint(GRIDLINECOLOR);
		
		chart.setBackgroundPaint(null);
		plot.getDomainAxis().setTickLabelPaint(TEXT_FOREGROUND);
		plot.getRangeAxis().setTickLabelPaint(TEXT_FOREGROUND);

		plot.getDomainAxis().setLabel(LocaleBundle.getString("StatisticsFrame.chartAxis.WatchCount")); //$NON-NLS-1$
		
		return chart;
	}
	
	private DefaultCategoryDataset getDataSet(CCMovieList movielist, StatisticsTypeFilter source) {
		CCStream<ICCPlayableElement> it = source.iteratorMoviesOrEpisodes(movielist);

		int[] watchcounts = StatisticsHelper.getMultipleWatchCount(it);

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		for (int i = 0; i < watchcounts.length; i++) {
			dataset.addValue(watchcounts[i], "Series0", LocaleBundle.getFormattedString("StatisticsFrame.chartAxis.WatchCountFormat", i, watchcounts[i])); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return dataset;
	}

	@Override
	public String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.watchcount"); //$NON-NLS-1$
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
