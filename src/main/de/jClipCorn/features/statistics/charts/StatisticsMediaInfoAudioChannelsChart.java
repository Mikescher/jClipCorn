package de.jClipCorn.features.statistics.charts;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.features.statistics.StatisticsChart;
import de.jClipCorn.features.statistics.StatisticsTypeFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
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

public class StatisticsMediaInfoAudioChannelsChart extends StatisticsChart {
	public StatisticsMediaInfoAudioChannelsChart(CCMovieList ml, StatisticsTypeFilter _source) {
		super(ml, _source);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist, StatisticsTypeFilter source) {
		JFreeChart chart = ChartFactory.createBarChart(Str.Empty, Str.Empty, Str.Empty, getDataSet(movielist, source), PlotOrientation.VERTICAL, false, false, false);
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

		plot.getDomainAxis().setMaximumCategoryLabelLines(2);

		return chart;
	}
	
	private DefaultCategoryDataset getDataSet(CCMovieList movielist, StatisticsTypeFilter source) {
		Map<String, Integer> values = source
				.iterator(movielist)
				.<ICCPlayableElement>cast()
				.map(e -> e.mediaInfo().get().AudioChannels)
				.filter(Opt::isPresent)
				.map(Opt::get)
				.autosort()
				.groupBy(e -> e)
				.toMap(e -> Integer.toString(e.getKey()), e -> e.getValue().size()); 

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (Map.Entry<String, Integer> elem : values.entrySet()) {
			dataset.addValue(elem.getValue(), "Series0", elem.getKey()); //$NON-NLS-1$
		}

        return dataset;
	}

	@Override
	public String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.mediainfo_audiochannels"); //$NON-NLS-1$
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
	public StatisticsTypeFilter[] supportedTypes() {
		return new StatisticsTypeFilter[]{StatisticsTypeFilter.STF_MOVIES, StatisticsTypeFilter.STF_EPISODES, StatisticsTypeFilter.STF_MOVIES_AND_EPISODES};
	}
}
