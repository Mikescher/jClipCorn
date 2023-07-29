package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.covertab.CCCoverData;
import de.jClipCorn.database.elementProps.impl.EStringProp;
import de.jClipCorn.util.datatypes.Tuple;

import java.awt.image.BufferedImage;

public interface ICCCoveredElement extends ICCDatabaseStructureElement {
	// Movie, Season, Series

	EStringProp title();

	int         getLocalID();
	String      getQualifiedTitle();

	int getCoverID();
	CCCoverData getCoverInfo();

	BufferedImage getCover();
	Tuple<Integer, Integer> getCoverDimensions();

	CCMovieList getMovieList();
}
