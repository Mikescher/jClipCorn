package de.jClipCorn.table.sorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;

public class TableFSKComparator implements Comparator<CCFSK>{
	@Override
	public int compare(CCFSK o1, CCFSK o2) {
		return CCFSK.compare(o1, o2);
	}
}
