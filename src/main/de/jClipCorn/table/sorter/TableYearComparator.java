package de.jClipCorn.table.sorter;

import java.util.Comparator;

import de.jClipCorn.util.datetime.YearRange;

public class TableYearComparator implements Comparator<YearRange>{
	@Override
	public int compare(YearRange o1, YearRange o2) {
		return YearRange.compare(o1, o2);
	}
}
