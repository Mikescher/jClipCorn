package de.jClipCorn.features.statistics.charts;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.features.statistics.StatisticsChart;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsTypeFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.stream.CCStreams;
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

import java.util.Map;

public class StatisticsMediaInfoResolutionChart extends StatisticsChart {
	public StatisticsMediaInfoResolutionChart(CCMovieList ml, StatisticsTypeFilter _source) {
		super(ml, _source);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist, StatisticsTypeFilter source) {
		JFreeChart chart = ChartFactory.createBarChart(Str.Empty, Str.Empty, Str.Empty, getDataSet(movielist, source), PlotOrientation.HORIZONTAL, false, false, false);
		chart.removeLegend();

		CategoryPlot plot = chart.getCategoryPlot();

		BarRenderer renderer = new BarRenderer();
		renderer.setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator());
		renderer.setSeriesItemLabelsVisible(0, true);
		renderer.setSeriesPositiveItemLabelPosition(0, new ItemLabelPosition(ItemLabelAnchor.OUTSIDE3, TextAnchor.CENTER_LEFT));
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

		plot.getDomainAxis().setMaximumCategoryLabelLines(2);

		return chart;
	}
	
	private DefaultCategoryDataset getDataSet(CCMovieList movielist, StatisticsTypeFilter source) {
		Map<CCMediaInfo, Integer> values = source
				.iteratorMoviesOrEpisodes(movielist)
				.filter(e -> e.mediaInfo().get().isSet())
				.groupBy(e -> e.mediaInfo().get().getWidth()+"x"+e.mediaInfo().get().getHeight()) //$NON-NLS-1$
				.autosortByProperty(e -> -e.getValue().size())
				.take(64)
				.toMap(e -> e.getValue().get(0).mediaInfo().get(), e -> e.getValue().size());

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (Map.Entry<CCMediaInfo, Integer> elem : CCStreams.iterate(values.entrySet()).autosortByProperty(e -> e.getKey().getWidth()*e.getKey().getHeight())) {
			dataset.addValue(elem.getValue(), "Series0", elem.getKey().getWidth()+"x"+ elem.getKey().getHeight()); //$NON-NLS-1$ //$NON-NLS-2$
		}

        return dataset;
	}

	@Override
	public String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.mediainfo_resolution"); //$NON-NLS-1$
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
		return LocaleBundle.getString("StatisticsFrame.this.toggleSeries"); //$NON-NLS-1$
	}
}
