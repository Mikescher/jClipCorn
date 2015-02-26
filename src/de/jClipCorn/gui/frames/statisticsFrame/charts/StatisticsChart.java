package de.jClipCorn.gui.frames.statisticsFrame.charts;

import java.awt.Color;
import java.awt.event.ComponentEvent;

import javax.swing.UIManager;

import org.jfree.chart.JFreeChart;

import de.jClipCorn.database.CCMovieList;

public abstract class StatisticsChart {
	protected final static boolean ROTATE_PIE = false;
	
	protected final static Color TEXT_FOREGROUND = UIManager.getColor("TextField.foreground"); //$NON-NLS-1$
	
	protected final static Color GRIDLINECOLOR = Color.WHITE;
	
	protected final static Color XYCHART_COLOR = Color.RED;
	protected final static Color XYBACKGROUND_COLOR = Color.LIGHT_GRAY;
	
	protected final static Color BARCHART_COLOR = Color.BLUE;
	protected final static Color BARACKGROUND_COLOR = Color.LIGHT_GRAY;
	
	protected final static Color HISTOGRAMMCHART_COLOR = Color.BLUE;
	protected final static Color HISTOGRAMMACKGROUND_COLOR = Color.LIGHT_GRAY;
	
	protected final JFreeChart chart;
	private final String title;
	
	public StatisticsChart(CCMovieList ml) {
		this.chart = createChart(ml);
		this.title = createTitle();
	}

	public JFreeChart getChart() {
		return chart;
	}
	
	public String getTitle() {
		return title;
	}
	
	@Override
	public String toString() {
		return title;
	}
	
	protected abstract JFreeChart createChart(CCMovieList movielist);
	protected abstract String createTitle();
	
	public void onResize(ComponentEvent e) {
		// Overwrite me
	}
}
