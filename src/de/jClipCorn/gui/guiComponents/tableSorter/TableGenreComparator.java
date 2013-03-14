package de.jClipCorn.gui.guiComponents.tableSorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenreList;

public class TableGenreComparator implements Comparator<CCMovieGenreList>{
	@Override
	public int compare(CCMovieGenreList o1, CCMovieGenreList o2) {
		return CCMovieGenreList.compare(o1, o2);
	}
}
