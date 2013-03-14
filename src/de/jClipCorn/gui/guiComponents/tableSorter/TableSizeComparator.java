package de.jClipCorn.gui.guiComponents.tableSorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;

public class TableSizeComparator implements Comparator<CCMovieSize>{
	@Override
	public int compare(CCMovieSize o1, CCMovieSize o2) {
		return CCMovieSize.compare(o1, o2);
	}
}
