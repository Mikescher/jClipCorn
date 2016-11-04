package de.jClipCorn.gui.guiComponents.tableSorter;

import java.util.Comparator;

import de.jClipCorn.database.util.ExtendedViewedState;

public class TableViewedComparator implements Comparator<ExtendedViewedState> {
	@Override
	public int compare(ExtendedViewedState o1, ExtendedViewedState o2) {
		return o1.Type.compareTo(o2.Type);
	}
}
