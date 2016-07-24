package de.jClipCorn.gui.frames.statisticsFrame.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.helper.StatisticsHelper;

public class StatisticsProviderChart extends StatisticsChart {
	public StatisticsProviderChart(CCMovieList ml) {
		super(ml);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist) {
		JFreeChart chart = ChartFactory.createBarChart(
	            "",      //$NON-NLS-1$
	            "",      //$NON-NLS-1$
	            "",      //$NON-NLS-1$
	            getDataSet(movielist),               
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
	    
	    return chart;
	}
	
	private DefaultCategoryDataset getDataSet(CCMovieList movielist) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		int[] values = StatisticsHelper.getElementCountForAllProvider(movielist);
		
		for (int i = 0; i < values.length; i++) {
			dataset.addValue(values[i], "Provider0", CCOnlineRefType.find(i).asString()); //$NON-NLS-1$
		}
		
        return dataset;
	}

	@Override
	protected String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.provider"); //$NON-NLS-1$
	}

	@Override
	public boolean usesFilterableSeries() {
		return false;
	}
	
	@Override
	public boolean usesFilterableYearRange() {
		return false;
	}
}