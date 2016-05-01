package de.jClipCorn.gui.frames.statisticsFrame;

import java.awt.event.ComponentEvent;
import java.util.Map;

import javax.swing.JComponent;

import de.jClipCorn.database.databaseElement.CCSeries;

public abstract class StatisticsPanel {
	private final String title;

	public StatisticsPanel() {
		this.title = createTitle();
	}

	public abstract JComponent getComponent();
	
	public abstract boolean usesFilterableSeries();
	public abstract boolean usesFilterableYearRange();

	protected abstract String createTitle();
	
	public String getTitle() {
		return title;
	}
	
	@Override
	public String toString() {
		return title;
	}
	
	public void onResize(ComponentEvent e) {
		// Overwrite me
	}

	public void onHideSeries(Map<CCSeries, Boolean> map) {
		// Overwrite me
	}

	public void onFilterYearRange(int year) {
		// Overwrite me
	}
}
