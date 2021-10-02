package de.jClipCorn.features.table.sorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageSet;

public class TableLanguageComparator implements Comparator<CCDBLanguageSet>{
	@Override
	public int compare(CCDBLanguageSet o1, CCDBLanguageSet o2) {
		return CCDBLanguageSet.compare(o1, o2);
	}
}