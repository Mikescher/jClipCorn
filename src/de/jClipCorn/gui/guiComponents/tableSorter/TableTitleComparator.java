package de.jClipCorn.gui.guiComponents.tableSorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;

public class TableTitleComparator implements Comparator<CCDatabaseElement> {
	@Override
	public int compare(CCDatabaseElement arg0, CCDatabaseElement arg1) {
		return arg0.getTitle().compareToIgnoreCase(arg1.getTitle());
	}
}
