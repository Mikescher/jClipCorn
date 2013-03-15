package de.jClipCorn.gui.guiComponents.tableSorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CombinedMovieQuality;

public class TableQualityComparator implements Comparator<CombinedMovieQuality>{
	@Override
	public int compare(CombinedMovieQuality o1, CombinedMovieQuality o2) {
		return CombinedMovieQuality.compare(o1, o2);
	}
}