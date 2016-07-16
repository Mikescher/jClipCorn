package de.jClipCorn.gui.guiComponents.tableSorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieOnlineScore;

public class TableOnlineScoreComparator implements Comparator<CCMovieOnlineScore>{
	@Override
	public int compare(CCMovieOnlineScore o1, CCMovieOnlineScore o2) {
		return CCMovieOnlineScore.compare(o1, o2);
	}
}
