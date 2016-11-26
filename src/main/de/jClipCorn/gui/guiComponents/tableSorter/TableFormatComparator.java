package de.jClipCorn.gui.guiComponents.tableSorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;

public class TableFormatComparator implements Comparator<CCFileFormat>{
	@Override
	public int compare(CCFileFormat o1, CCFileFormat o2) {
		return CCFileFormat.compare(o1, o2);
	}
}
