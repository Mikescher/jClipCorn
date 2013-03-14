package de.jClipCorn.gui.guiComponents.tableSorter;

import java.util.Comparator;

import de.jClipCorn.util.CCDate;

public class TableDateComparator implements Comparator<CCDate>{
	@Override
	public int compare(CCDate o1, CCDate o2) {
		return CCDate.compare(o1, o2);
	}
}
