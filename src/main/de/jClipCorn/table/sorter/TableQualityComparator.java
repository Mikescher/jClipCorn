package de.jClipCorn.table.sorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;

public class TableQualityComparator implements Comparator<CCQuality>{
	@Override
	public int compare(CCQuality o1, CCQuality o2) {
		return CCQuality.compare(o1, o2);
	}
}