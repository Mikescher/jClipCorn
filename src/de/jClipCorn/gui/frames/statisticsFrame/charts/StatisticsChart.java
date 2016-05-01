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
	
	protected JFreeChart chart;
	private JComponent chartPanel = null;
	
	public StatisticsChart(CCMovieList ml) {
		super();
		
		this.chart = createChart(ml);
	}

	public JFreeChart getChart() {
		return chart;
	}
	
	protected void updateChart(JFreeChart newchart) {
		this.chart = newchart;
	}
	
	@Override
	public JComponent getComponent() {
		if (chartPanel == null) {
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
			
			((ChartPanel)chartPanel).setChart(getChart());	
		}
		return chartPanel;
	}
	
	protected abstract JFreeChart createChart(CCMovieList movielist);
}
