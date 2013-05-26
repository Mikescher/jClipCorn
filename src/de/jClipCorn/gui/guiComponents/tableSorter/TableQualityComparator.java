package de.jClipCorn.gui.guiComponents.tableSorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;

public class TableQualityComparator implements Comparator<CCMovieQuality>{
	@Override
	public int compare(CCMovieQuality o1, CCMovieQuality o2) {
		return CCMovieQuality.compare(o1, o2);
	}
}