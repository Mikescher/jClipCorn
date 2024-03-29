package de.jClipCorn.gui.frames.statisticsFrame;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.features.statistics.StatisticsTypeFilter;
import de.jClipCorn.properties.CCProperties;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.util.Map;
import java.util.Map.Entry;

public abstract class StatisticsPanel {

	protected abstract JComponent createComponent();
	
	public abstract boolean usesFilterableSeries();
	public abstract boolean usesFilterableYearRange();
	public abstract StatisticsTypeFilter[] supportedTypes();

	public abstract String createTitle();

	private JComponent cache;
	private int yearRangeCache = -1;
	private Map<CCSeries, Boolean> seriesFilterCache = null;
	protected final StatisticsTypeFilter source;

	protected final CCMovieList movielist;

	public StatisticsPanel(CCMovieList ml, StatisticsTypeFilter _source) {
		super();
		movielist = ml;
		source = _source;
	}

	public CCProperties ccprops() {
		return movielist.ccprops();
	}
	
	public void onResize(ComponentEvent e) {
		// Overwrite me
	}

	public void tryChangeFilter(Map<CCSeries, Boolean> map) {
		if (FilterEquals(seriesFilterCache, map)) {
			seriesFilterCache = map;
			return;
		}
		
		seriesFilterCache = map;
		onChangeFilter(map);
	}

	public void tryFilterYearRange(int year) {
		if (yearRangeCache == year) return;
		
		yearRangeCache = year;
		onFilterYearRange(year);
	}

	protected void onChangeFilter(Map<CCSeries, Boolean> map) {
		// Overwrite me
	}

	protected void onFilterYearRange(int year) {
		// Overwrite me
	}
	
	protected void invalidateComponent() {
		cache = null;
	}

	public JComponent getCachedComponent() {
		if (cache == null) cache = createComponent();
		
		return cache;
	}

	public boolean resetFrameOnFilter() {
		// Overwrite me
		return false;
	}

	public boolean resetFrameOnYearRange() {
		// Overwrite me
		return false;
	}

	private boolean FilterEquals(Map<CCSeries, Boolean> a, Map<CCSeries, Boolean> b) {
		if (a == null && b == null) return true;
		if (a == null && FilterAllTrue(b)) return true;
		if (b == null && FilterAllTrue(a)) return true;

		if (a == null || b == null) return false;
		
		if (a.size() != b.size()) return false;
		
		for (Entry<CCSeries, Boolean> entry : a.entrySet()) {
			Boolean va = entry.getValue();
			Boolean vb = b.get(entry.getKey());

			if (va == null) return false;
			if (vb == null) return false;
			
			if (va ^ vb) return false;
		}
		
		return true;
	}

	private boolean FilterAllTrue(Map<CCSeries, Boolean> f) {
		for (Entry<CCSeries, Boolean> entry : f.entrySet()) {
			if (!entry.getValue()) return false;
		}
		return true;
	}

	public void onShow() {
		// Overwrite me
	}
}
