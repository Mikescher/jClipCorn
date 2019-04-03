package de.jClipCorn.features.table.sorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;

public class TableTagsComparator implements Comparator<CCTagList> {
	@Override
	public int compare(CCTagList o1, CCTagList o2) {
		return CCTagList.compare(o1, o2);
	}
}
