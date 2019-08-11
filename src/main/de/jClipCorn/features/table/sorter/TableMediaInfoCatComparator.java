package de.jClipCorn.features.table.sorter;

import java.util.Comparator;

import de.jClipCorn.database.util.CCQualityCategory;

public class TableMediaInfoCatComparator implements Comparator<CCQualityCategory>{
	@Override
	public int compare(CCQualityCategory o1, CCQualityCategory o2) {
		return CCQualityCategory.compare(o1, o2);
	}
}