package de.jClipCorn.gui.frames.statisticsFrame.charts;

import java.text.DecimalFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.StatisticsHelper;

public class StatisticsYearChart extends StatisticsChart {

	public StatisticsYearChart(CCMovieList ml) {
		super(ml);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist) {
		JFreeChart chart = ChartFactory.createHistogram(
				"",  //$NON-NLS-1$
				"",  //$NON-NLS-1$
				"",  //$NON-NLS-1$
				getDataSet(movielist), 
				PlotOrientation.VERTICAL, 
				false, 
				false, 
				false
				);
		
		chart.removeLegend();
		
		XYPlot plot = chart.getXYPlot();
		
		XYBarRenderer bar_renderer = (XYBarRenderer) plot.getRenderer();
		bar_renderer.setBaseFillPaint(HISTOGRAMMCHART_COLOR);
		bar_renderer.setMargin( 0.2 );
		bar_renderer.setBarAlignmentFactor( 0 );
		bar_renderer.setSeriesPaint(0, HISTOGRAMMCHART_COLOR);
		bar_renderer.setShadowVisible(true);
		bar_renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{1}: {2}", new DecimalFormat("0"), new DecimalFormat("0"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		bar_renderer.setBarPainter(new StandardXYBarPainter());
		
		plot.setBackgroundPaint(HISTOGRAMMACKGROUND_COLOR);
		plot.setDomainGridlinePaint(GRIDLINECOLOR);
		plot.setRangeGridlinePaint(GRIDLINECOLOR);
		
		chart.setBackgroundPaint(null);
		plot.getDomainAxis().setTickLabelPaint(TEXT_FOREGROUND);
		if (plot.getDomainAxis() instanceof NumberAxis) {
			((NumberAxis)plot.getDomainAxis()).setNumberFormatOverride(new DecimalFormat("0")); //$NON-NLS-1$
		}
		plot.getRangeAxis().setTickLabelPaint(TEXT_FOREGROUND);
	    
	    return chart;
	}
	
	private IntervalXYDataset getDataSet(CCMovieList movielist) {
		int minYear = StatisticsHelper.getMinimumMovieYear(movielist);
		int maxYear = StatisticsHelper.getMaximumMovieYear(movielist);
		int count = (maxYear - minYear) + 1;
		
		int[] yearcounts = StatisticsHelper.getMovieCountForAllYears(movielist, minYear, count);
		
		XYSeries xyseries = new XYSeries("Series0"); //$NON-NLS-1$
		
		for (int i = 0; i < count; i++) {
			xyseries.add(minYear + i, yearcounts[i]);
		}
		
		return new XYSeriesCollection(xyseries);
	}

	@Override
	protected String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.year"); //$NON-NLS-1$
	}

}