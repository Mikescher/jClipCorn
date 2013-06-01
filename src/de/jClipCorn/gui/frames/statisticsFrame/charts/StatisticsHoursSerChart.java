package de.jClipCorn.gui.frames.statisticsFrame.charts;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.StatisticsHelper;

public class StatisticsHoursSerChart extends StatisticsChart {

	public StatisticsHoursSerChart(CCMovieList ml) {
		super(ml);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist) {
		DateAxis dateAxis = new DateAxis(""); //$NON-NLS-1$

	    DateFormat chartFormatter = new SimpleDateFormat("dd.MM.yyyy"); //$NON-NLS-1$
	    dateAxis.setDateFormatOverride(chartFormatter);

	    NumberAxis valueAxis = new NumberAxis(""); //$NON-NLS-1$

	    StandardXYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES, null, null);
	    
	    XYPlot plot = new XYPlot(getDataSet(movielist), dateAxis, valueAxis, renderer);
	    
		plot.getRenderer().setSeriesPaint(0, XYCHART_COLOR);
		
		plot.setBackgroundPaint(XYBACKGROUND_COLOR);
		plot.setDomainGridlinePaint(GRIDLINECOLOR);
		plot.setRangeGridlinePaint(GRIDLINECOLOR);
	    
	    JFreeChart chart = new JFreeChart(plot);
	    chart.removeLegend();
	    
	    chart.setBackgroundPaint(null);
	    plot.getDomainAxis().setTickLabelPaint(TEXT_FOREGROUND);
	    plot.getRangeAxis().setTickLabelPaint(TEXT_FOREGROUND);
	    
		plot.getRangeAxis().setLabel(LocaleBundle.getString("StatisticsFrame.chartAxis.Mins")); //$NON-NLS-1$
		plot.getDomainAxis().setLabel(LocaleBundle.getString("StatisticsFrame.chartAxis.Date")); //$NON-NLS-1$
		
		plot.getRangeAxis().setLabelPaint(TEXT_FOREGROUND);
		plot.getDomainAxis().setLabelPaint(TEXT_FOREGROUND);
	    
	    return chart;
	}
	
	private XYDataset getDataSet(CCMovieList movielist) {
		CCDate mindate = StatisticsHelper.getFirstSeriesAddDate(movielist);
		long minMilliecs = mindate.asMilliseconds();
		CCDate maxdate = StatisticsHelper.getLastSeriesAddDate(movielist);
		int daycount = mindate.getDayDifferenceTo(maxdate) + 1;
		
		DefaultXYDataset dataset = new DefaultXYDataset();
		
		int[] allpos = StatisticsHelper.getSeriesMinuteCountForAllDates(movielist, mindate, daycount);
		
		double[][] series = new double[2][allpos.length];
		
		for (int i = 0; i < allpos.length; i++) {
			series[0][i] = minMilliecs + i * CCDate.MILLISECONDS_PER_DAY;
			series[1][i] = allpos[i];
		}
		
        dataset.addSeries("Series0", series); //$NON-NLS-1$
        
        return dataset;
	}

	@Override
	protected String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.hoursSer"); //$NON-NLS-1$
	}

}
