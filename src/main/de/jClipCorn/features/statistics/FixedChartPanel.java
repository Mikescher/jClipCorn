package de.jClipCorn.features.statistics;

import java.awt.event.MouseEvent;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class FixedChartPanel extends ChartPanel {
	private static final long serialVersionUID = -2436812783706475087L;

	public FixedChartPanel(JFreeChart chart) {
		super(chart);
		
		setPopupMenu(null);
		setDomainZoomable(false);
		setRangeZoomable(false);
		setMouseZoomable(false);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		//--
	}
}
