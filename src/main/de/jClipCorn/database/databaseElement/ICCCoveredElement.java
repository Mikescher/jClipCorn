package de.jClipCorn.database.databaseElement;

import java.awt.image.BufferedImage;

import de.jClipCorn.database.covertab.CoverCacheElement;
import de.jClipCorn.util.datatypes.Tuple;

public interface ICCCoveredElement {
	// Movie, Season, Series

	String getTitle();

	int getCoverID();
	CoverCacheElement getCoverInfo();

	BufferedImage getCover();
	Tuple<Integer, Integer> getCoverDimensions();
}
