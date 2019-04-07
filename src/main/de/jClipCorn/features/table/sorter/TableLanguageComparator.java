package de.jClipCorn.features.table.sorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;

public class TableLanguageComparator implements Comparator<CCDBLanguageList>{
	@Override
	public int compare(CCDBLanguageList o1, CCDBLanguageList o2) {
		return CCDBLanguageList.compare(o1, o2);
	}
}