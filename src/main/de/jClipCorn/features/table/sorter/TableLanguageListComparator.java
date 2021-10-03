package de.jClipCorn.features.table.sorter;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;

import java.util.Comparator;

public class TableLanguageListComparator implements Comparator<CCDBLanguageList>{
	@Override
	public int compare(CCDBLanguageList o1, CCDBLanguageList o2) {
		return CCDBLanguageList.compare(o1, o2);
	}
}