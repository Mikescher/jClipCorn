package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.covertab.CCCoverData;
import de.jClipCorn.database.elementValues.EStringProp;
import de.jClipCorn.util.datatypes.Tuple;

import java.awt.image.BufferedImage;

public interface ICCCoveredElement {
	// Movie, Season, Series

	EStringProp title();

	int getCoverID();
	CCCoverData getCoverInfo();

	BufferedImage getCover();
	Tuple<Integer, Integer> getCoverDimensions();
}
