package de.jClipCorn.gui.frames.statisticsFrame;

import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.swing.JComponent;

import de.jClipCorn.database.databaseElement.CCSeries;

public class StatisticsGroup {
	private StatisticsPanel template;
	
	private final Function<StatisticsTypeFilter, StatisticsPanel> constructor;
	
	protected Map<StatisticsTypeFilter, StatisticsPanel> chartPanels;

	public StatisticsGroup(Function<StatisticsTypeFilter, StatisticsPanel> _ctr) {
		super();
		this.chartPanels = new HashMap<>();
		this.constructor = _ctr;
		this.template = _ctr.apply(StatisticsTypeFilter.MOVIES);
	}
	
	public JComponent getComponent(StatisticsTypeFilter source) {
		if (! chartPanels.containsKey(source)) {
			StatisticsPanel chartPanel = constructor.apply(source);
			
			chartPanels.put(source, chartPanel);
		}
		
		return chartPanels.get(source).getCachedComponent();
	}
	
	public String getTitle() {
		return template.createTitle();
	}
	
	public String getCaptionToggle2() {
		return template.createToggleTwoCaption();
	}
	
	@Override
	public String toString() {
		return template.createTitle();
	}
	
	public boolean usesFilterableSeries() {
		return template.usesFilterableSeries();
	}
	
	public boolean usesFilterableYearRange() {
		return template.usesFilterableYearRange();
	}

	public StatisticsTypeFilter supportedTypes() {
		return template.supportedTypes();
	}

	public boolean resetFrameOnFilter() {
		return template.resetFrameOnFilter();
	}
	
	public void onResize(ComponentEvent e) {
		for (Entry<StatisticsTypeFilter, StatisticsPanel> pnl : chartPanels.entrySet()) {
			pnl.getValue().onResize(e);
		}
	}

	public void onChangeFilter(Map<CCSeries, Boolean> map) {
		for (Entry<StatisticsTypeFilter, StatisticsPanel> pnl : chartPanels.entrySet()) {
			pnl.getValue().tryChangeFilter(map);
		}
	}

	public void onFilterYearRange(int year) {
		for (Entry<StatisticsTypeFilter, StatisticsPanel> pnl : chartPanels.entrySet()) {
			pnl.getValue().tryFilterYearRange(year);
		}
	}
}
