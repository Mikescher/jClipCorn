package de.jClipCorn.features.statistics;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsPanel;
import de.jClipCorn.properties.CCProperties;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Arrays;

public abstract class StatisticsChart extends StatisticsPanel {
	protected final static boolean ROTATE_PIE = false;
	
	protected final static Color TEXT_FOREGROUND = UIManager.getColor("TextField.foreground"); //$NON-NLS-1$
	
	protected final static Color GRIDLINECOLOR = Color.WHITE;
	
	protected final static Color XYCHART_COLOR = Color.RED;
	protected final static Color XYBACKGROUND_COLOR = Color.LIGHT_GRAY;

	protected final static Color BARCHART_COLOR = Color.BLUE;
	protected final static Color BARCHART_ITEM_COLOR = Color.BLACK;
	protected final static Color BARACKGROUND_COLOR = Color.LIGHT_GRAY;
	
	protected final static Color HISTOGRAMMCHART_COLOR = Color.BLUE;
	protected final static Color HISTOGRAMMACKGROUND_COLOR = Color.LIGHT_GRAY;

	public StatisticsChart(CCMovieList ml, StatisticsTypeFilter _source) {
		super(ml, _source);
	}

	public CCProperties ccprops() {
		return movielist.ccprops();
	}

	private JFreeChart getChart() {
		if (!Arrays.asList(supportedTypes()).contains(source)) return null;
		
		return createChart(movielist, source);
	}
	
	@Override
	protected JComponent createComponent() {
		JFreeChart chart = getChart();
		if (chart == null) return null;
		
		ChartPanel chartPanel;
		
		if (movielist.ccprops().PROP_STATISTICS_INTERACTIVECHARTS.getValue()) {
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
		
		chartPanel.setChart(chart);

		chartPanel.setVisible(true);
		
		
		return chartPanel;
	}
	
	protected abstract JFreeChart createChart(CCMovieList movielist, StatisticsTypeFilter source);
}
