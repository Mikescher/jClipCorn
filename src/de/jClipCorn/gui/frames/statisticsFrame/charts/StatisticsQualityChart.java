package de.jClipCorn.gui.frames.statisticsFrame.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.StatisticsHelper;

public class StatisticsQualityChart extends StatisticsChart {
	public StatisticsQualityChart(CCMovieList ml) {
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
	    
	    return chart;
	}
	
	private DefaultCategoryDataset getDataSet(CCMovieList movielist) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		int[] values = StatisticsHelper.getMovieCountForAllQualities(movielist);
		
		for (CCMovieQuality format : CCMovieQuality.values()) {
			dataset.addValue(values[format.asInt()], "Series0", format.asString()); //$NON-NLS-1$
		}
		
        return dataset;
	}

	@Override
	protected String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.quality"); //$NON-NLS-1$
	}
}
