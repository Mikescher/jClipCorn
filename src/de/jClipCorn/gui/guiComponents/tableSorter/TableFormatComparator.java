package de.jClipCorn.gui.guiComponents.tableSorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;

public class TableFormatComparator implements Comparator<CCMovieFormat>{
	@Override
	public int compare(CCMovieFormat o1, CCMovieFormat o2) {
		return CCMovieFormat.compare(o1, o2);
	}
}
