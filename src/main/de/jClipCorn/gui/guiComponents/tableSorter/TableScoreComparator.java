package de.jClipCorn.gui.guiComponents.tableSorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieScore;

public class TableScoreComparator implements Comparator<CCMovieScore> {
	@Override
	public int compare(CCMovieScore first, CCMovieScore last) {
		return CCMovieScore.compare(first, last);
	}
}
