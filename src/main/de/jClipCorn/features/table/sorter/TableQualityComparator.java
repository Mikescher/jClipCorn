package de.jClipCorn.features.table.sorter;

import java.util.Comparator;

public class TableQualityComparator implements Comparator<CCQuality>{
	@Override
	public int compare(CCQuality o1, CCQuality o2) {
		return CCQuality.compare(o1, o2);
	}
}