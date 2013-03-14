package de.jClipCorn.gui.guiComponents.tableSorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;

public class TableZyklusComparator implements Comparator<CCMovieZyklus>{
	@Override
	public int compare(CCMovieZyklus o1, CCMovieZyklus o2) {
		return CCMovieZyklus.compare(o1, o2);
	}
}
