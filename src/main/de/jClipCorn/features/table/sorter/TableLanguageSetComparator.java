package de.jClipCorn.features.table.sorter;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageSet;

import java.util.Comparator;

public class TableLanguageSetComparator implements Comparator<CCDBLanguageSet>{
	@Override
	public int compare(CCDBLanguageSet o1, CCDBLanguageSet o2) {
		return CCDBLanguageSet.compare(o1, o2);
	}
}