package de.jClipCorn.gui.frames.statisticsFrame.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieOnlineScore;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.StatisticsHelper;

public class StatisticsOnlinescoreChart extends StatisticsChart {
	public StatisticsOnlinescoreChart(CCMovieList ml) {
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
		
		int[] values = StatisticsHelper.getMovieCountForAllOnlinescores(movielist);
		
		for (CCMovieOnlineScore oscore : CCMovieOnlineScore.values()) {
			dataset.addValue(values[oscore.asInt()], "Series0", "" + oscore.asInt()/2.0); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
        return dataset;
	}

	@Override
	protected String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.onlinescore"); //$NON-NLS-1$
	}
}
