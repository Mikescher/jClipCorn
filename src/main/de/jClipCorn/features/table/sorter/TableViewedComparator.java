package de.jClipCorn.features.table.sorter;

import java.util.Comparator;

import de.jClipCorn.database.util.ExtendedViewedState;

public class TableViewedComparator implements Comparator<ExtendedViewedState> {
	@Override
	public int compare(ExtendedViewedState o1, ExtendedViewedState o2) {
		int c = o1.getType().compareTo(o2.getType());
		if (c != 0) return c;

		return Integer.compare(o2.getViewCount(), o1.getViewCount()); // switched args b design - higher numbers first
	}
}
