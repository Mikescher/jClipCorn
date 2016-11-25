package de.jClipCorn.gui.frames.statisticsFrame.charts;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;
import javax.swing.UIManager;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.frames.statisticsFrame.FixedChartPanel;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsPanel;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsTypeFilter;
import de.jClipCorn.properties.CCProperties;

public abstract class StatisticsChart extends StatisticsPanel {
	protected final static boolean ROTATE_PIE = false;
	
	protected final static Color TEXT_FOREGROUND = UIManager.getColor("TextField.foreground"); //$NON-NLS-1$
	
	protected final static Color GRIDLINECOLOR = Color.WHITE;
	
	protected final static Color XYCHART_COLOR = Color.RED;
	protected final static Color XYBACKGROUND_COLOR = Color.LIGHT_GRAY;
	
	protected final static Color BARCHART_COLOR = Color.BLUE;
	protected final static Color BARACKGROUND_COLOR = Color.LIGHT_GRAY;
	
	protected final static Color HISTOGRAMMCHART_COLOR = Color.BLUE;
	protected final static Color HISTOGRAMMACKGROUND_COLOR = Color.LIGHT_GRAY;
	
	private final CCMovieList movielist;
	
	public StatisticsChart(CCMovieList ml, StatisticsTypeFilter _source) {
		super(_source);
		movielist = ml;
	}

	private JFreeChart getChart() {
		if (! supportedTypes().contains(source)) return null;

		if (source == StatisticsTypeFilter.MOVIES && ! movielist.containsMovies()) return null;
		if (source == StatisticsTypeFilter.SERIES && ! movielist.containsMovies()) return null;
		if (source == StatisticsTypeFilter.BOTH && movielist.getElementCount() == 0) return null;
		
		return createChart(movielist, source);
	}
	
	@Override
	protected JComponent createComponent() {
		JFreeChart chart = getChart();
		if (chart == null) return null;
		
		JComponent chartPanel;
		
		if (CCProperties.getInstance().PROP_STATISTICS_INTERACTIVECHARTS.getValue()) {
			chartPanel = new ChartPanel(new JFreeChart(new XYPlot()));
		} else {
			chartPanel = new FixedChartPanel(new JFreeChart(new XYPlot()));
		}
		
		chartPanel.setVisible(true);
		
		chartPanel.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {/**/}
			
			@Override
			public void componentResized(ComponentEvent e) {
				StatisticsChart.this.onResize(e);
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {/**/}
			
			@Override
			public void componentHidden(ComponentEvent e) {/**/}
		});
		
		((ChartPanel)chartPanel).setChart(chart);	
		
		return chartPanel;
	}
	
	protected abstract JFreeChart createChart(CCMovieList movielist, StatisticsTypeFilter source);
}
