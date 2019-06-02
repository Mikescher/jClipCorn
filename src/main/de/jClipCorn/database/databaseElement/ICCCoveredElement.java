package de.jClipCorn.database.databaseElement;

import java.awt.image.BufferedImage;

import de.jClipCorn.util.datatypes.Tuple;

public interface ICCCoveredElement {
	// Movie, Season, Series

	String getTitle();

	int getCoverID();

	BufferedImage getCover();
	Tuple<Integer, Integer> getCoverDimensions();
	
	String getCoverMD5();
	
}
