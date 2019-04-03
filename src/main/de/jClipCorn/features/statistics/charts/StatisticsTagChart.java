package de.jClipCorn.features.statistics.charts;

import de.jClipCorn.features.statistics.StatisticsChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.features.statistics.StatisticsHelper;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsTypeFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.stream.CCStream;

public class StatisticsTagChart extends StatisticsChart {
	public StatisticsTagChart(CCMovieList ml, StatisticsTypeFilter _source) {
		super(ml, _source);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist, StatisticsTypeFilter source) {
		JFreeChart chart = ChartFactory.createBarChart(
	            "",      //$NON-NLS-1$
	            "",      //$NON-NLS-1$
	            "",      //$NON-NLS-1$
	            getDataSet(movielist, source),               
	            PlotOrientation.VERTICAL, 
	            false,                  
	            false,
	            false
	    );
		
		chart.removeLegend();
		
		CategoryPlot plot = chart.getCategoryPlot();
		
		BarRenderer renderer = new BarRenderer();
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
		
		plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 15.0));
	    
	    return chart;
	}
	
	private DefaultCategoryDataset getDataSet(CCMovieList movielist, StatisticsTypeFilter source) {
		CCStream<ICCPlayableElement> it = source.iteratorMoviesOrEpisodes(movielist);
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		int[] values = StatisticsHelper.getCountForAllTags(it);
		
		for (int i = 0; i < CCTagList.ACTIVETAGS; i++) {
			dataset.addValue(values[i], "Series0", CCTagList.getName(i)); //$NON-NLS-1$
		}
		
        return dataset;
	}

	@Override
	public String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.tags"); //$NON-NLS-1$
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
