package de.jClipCorn.features.statistics;

import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;

import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsPanel;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsTypeFilter;
import de.jClipCorn.util.lambda.Func1to1;

public class StatisticsGroup {
	private final StatisticsPanel template;
	
	private final Func1to1<StatisticsTypeFilter, StatisticsPanel> constructor;
	
	protected Map<StatisticsTypeFilter, StatisticsPanel> chartPanels;

	public StatisticsGroup(Func1to1<StatisticsTypeFilter, StatisticsPanel> _ctr) {
		super();
		this.chartPanels = new HashMap<>();
		this.constructor = _ctr;
		this.template = _ctr.invoke(StatisticsTypeFilter.MOVIES);
	}
	
	public JComponent getComponent(StatisticsTypeFilter source) {
		if (! chartPanels.containsKey(source)) {
			StatisticsPanel chartPanel = constructor.invoke(source);
			
			chartPanels.put(source, chartPanel);
		}
		
		return chartPanels.get(source).getCachedComponent();
	}

	public StatisticsPanel getTemplate() {
		return template;
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

	public boolean resetFrameOnYearRange() {
		return template.resetFrameOnYearRange();
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

	public void onShow() {
		for (Entry<StatisticsTypeFilter, StatisticsPanel> pnl : chartPanels.entrySet()) {
			pnl.getValue().onShow();
		}
	}
}
