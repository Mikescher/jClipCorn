package de.jClipCorn.gui.guiComponents.tableSorter;

import java.util.Comparator;

public class TableViewedComparator implements Comparator<Boolean> {
	@Override
	public int compare(Boolean o1, Boolean o2) {
		return ((o1 ^ o2) ? ((o1) ? (1) : (-1)) : (0)); // Code-Level: Genius
	}
}
