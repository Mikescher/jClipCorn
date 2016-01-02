package de.jClipCorn.gui.frames.showIncompleteFilmSeriesFrame;

import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;

public class MissingZyklusElement {
	public final CCMovieZyklus zyklus;
	public final CCMovie target;
	
	public MissingZyklusElement(CCMovieZyklus z, CCMovie m) {
		zyklus = z;
		target = m;
	}

	@Override
	public String toString() {
		return zyklus.getFormatted();
	}
}
