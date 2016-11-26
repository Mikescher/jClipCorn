package de.jClipCorn.gui.guiComponents.tableSorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;

public class TableSizeComparator implements Comparator<CCFileSize>{
	@Override
	public int compare(CCFileSize o1, CCFileSize o2) {
		return CCFileSize.compare(o1, o2);
	}
}
