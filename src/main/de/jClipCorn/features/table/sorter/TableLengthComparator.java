package de.jClipCorn.features.table.sorter;

import java.util.Comparator;

public class TableLengthComparator implements Comparator<Integer>{
	@Override
	public int compare(Integer o1, Integer o2) {
		return Integer.compare(o1, o2);
	}
}
