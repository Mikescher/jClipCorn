package de.jClipCorn.gui.frames.statisticsFrame.charts;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

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
import de.jClipCorn.util.helper.StatisticsHelper;

public class StatisticsAddDateChart extends StatisticsChart {

	public StatisticsAddDateChart(CCMovieList ml) {
		super(ml);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist) {
		DateAxis dateAxis = new DateAxis(""); //$NON-NLS-1$

	    DateFormat chartFormatter = new SimpleDateFormat("dd.MM.yyyy"); //$NON-NLS-1$
	    dateAxis.setDateFormatOverride(chartFormatter);

	    NumberAxis valueAxis = new NumberAxis(""); //$NON-NLS-1$
	    
	    //StandardXYToolTipGenerator ttg = new StandardXYToolTipGenerator("{1}: {2}",new SimpleDateFormat("dd.MM.yyyy"), new DecimalFormat()); //$NON-NLS-1$ //$NON-NLS-2$

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
	    
	    return chart;
	}
	
	private XYDataset getDataSet(CCMovieList movielist) {
		CCDate mindate = StatisticsHelper.getFirstMovieAddDate(movielist);
		long minMilliecs = mindate.asMilliseconds();
		CCDate maxdate = StatisticsHelper.getLastMovieAddDate(movielist);
		int daycount = mindate.getDayDifferenceTo(maxdate) + 1;
		
		DefaultXYDataset dataset = new DefaultXYDataset();
		
		List<Integer> allpos = StatisticsHelper.getMovieCountForAllDates(movielist, mindate, daycount);
		
		List<Integer> posx = new Vector<>();
		List<Integer> posy = new Vector<>();
		
		for (int i = 0; i < daycount; i++) {
//			if (allpos.get(i) > 0) {
				posx.add(i);
				posy.add(allpos.get(i));
//			}
		}
		
		double[][] series = new double[2][posx.size()];
		
		for (int i = 0; i < posx.size(); i++) {
			series[0][i] = minMilliecs + posx.get(i) * CCDate.MILLISECONDS_PER_DAY;
			series[1][i] = posy.get(i);
		}
		
        dataset.addSeries("Series0", series); //$NON-NLS-1$
        
        return dataset;
	}

	@Override
	protected String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.adddate"); //$NON-NLS-1$
	}

	@Override
	public boolean usesFilterableSeries() {
		return false;
	}
}
