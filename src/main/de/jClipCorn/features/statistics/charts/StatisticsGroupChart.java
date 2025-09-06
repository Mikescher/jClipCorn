package de.jClipCorn.features.statistics.charts;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.features.statistics.StatisticsChart;
import de.jClipCorn.features.statistics.StatisticsTypeFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.stream.CCStream;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StatisticsGroupChart extends StatisticsChart {
	public StatisticsGroupChart(CCMovieList ml, StatisticsTypeFilter _source) {
		super(ml, _source);
	}

	private static class CustomBarRenderer extends BarRenderer {
		private static final long serialVersionUID = 7129766928866125281L;
		
		public final CCGroup[] groups;

		private CustomBarRenderer(CCGroup[] g) {
			groups = g;
		}

		@Override
		public Paint getItemPaint(final int row, final int column) {
			if (groups == null || column < 0 || column >= groups.length) return super.getItemPaint(row, column);
			return groups[column].HexColor.toColorWithAlpha(CCGroup.COLOR_TAG_ALPHA);
		}
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist, StatisticsTypeFilter source) {

		Tuple<DefaultCategoryDataset, CCGroup[]> data = getDataSet(movielist, source);
		if (data == null) return null;

		JFreeChart chart = ChartFactory.createBarChart(
	            "",      //$NON-NLS-1$
	            "",      //$NON-NLS-1$
	            "",      //$NON-NLS-1$
				data.Item1,
	            PlotOrientation.VERTICAL, 
	            false,                  
	            false,
	            false
	    );
		
		chart.removeLegend();
		
		CategoryPlot plot = chart.getCategoryPlot();
		
		BarRenderer renderer = new CustomBarRenderer(data.Item2);
		renderer.setSeriesPaint(0, BARCHART_COLOR);
		renderer.setBarPainter(new StandardBarPainter());
		
		plot.setRenderer(renderer);
		
		plot.setBackgroundPaint(XYBACKGROUND_COLOR);
		plot.setDomainGridlinePaint(GRIDLINECOLOR);
		plot.setRangeGridlinePaint(GRIDLINECOLOR);
		
		chart.setBackgroundPaint(null);
		plot.getDomainAxis().setTickLabelPaint(TEXT_FOREGROUND);
		plot.getRangeAxis().setTickLabelPaint(TEXT_FOREGROUND);
		plot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4.5));
	    
	    return chart;
	}
	
	private Tuple<DefaultCategoryDataset, CCGroup[]> getDataSet(CCMovieList movielist, StatisticsTypeFilter source) {
		CCStream<CCDatabaseElement> it = source.iterator(movielist).cast();
		if (it == null) return null;

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		Map<CCGroup, Integer> groups = new HashMap<>();
		for (CCDatabaseElement e : it) {
			for (CCGroup g : e.getGroups()) {
				if (groups.containsKey(g)) groups.put(g, groups.get(g) + 1);
				else groups.put(g, 1);

				CCGroup parent = movielist.getGroupOrNull(g.Parent);
				while (parent != null) {

					if (groups.containsKey(parent)) groups.put(parent, groups.get(parent) + 1);
					else groups.put(parent, 1);

					parent = movielist.getGroupOrNull(parent.Parent);
				}

			}
		}

		ArrayList<CCGroup> allgroups = new ArrayList<>();

		for (Map.Entry<CCGroup, Integer> elem : groups.entrySet()) {
			dataset.addValue(elem.getValue(), "Series0", elem.getKey().Name); //$NON-NLS-1$
			allgroups.add(elem.getKey());
		}

        return Tuple.Create(dataset, allgroups.toArray(new CCGroup[0]));
	}

	@Override
	public String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.groups"); //$NON-NLS-1$
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
		return new StatisticsTypeFilter[]{StatisticsTypeFilter.STF_MOVIES, StatisticsTypeFilter.STF_SERIES, StatisticsTypeFilter.STF_MOVIES_AND_SERIES};
	}
}
