package de.jClipCorn.table.sorter;

import java.util.Comparator;

import de.jClipCorn.util.datetime.CCDate;

public class TableDateComparator implements Comparator<CCDate>{
	@Override
	public int compare(CCDate o1, CCDate o2) {
		return CCDate.compare(o1, o2);
	}
}
