package de.jClipCorn.gui.guiComponents.tableSorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;

public class TableLanguageComparator implements Comparator<CCDBLanguage>{
	@Override
	public int compare(CCDBLanguage o1, CCDBLanguage o2) {
		return CCDBLanguage.compare(o1, o2);
	}
}