package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.covertab.CCCoverData;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.database.elementProps.impl.EStringProp;
import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;

import java.awt.image.BufferedImage;

public interface ICCCoveredElement extends ICCDatabaseStructureElement {
	// Movie, Season, Series

	EStringProp title();

	CCUUID      getID();
	String      getQualifiedTitle();

	/** empty for an element without a cover */
	Opt<CCUUID> getCoverID();

	/** null both for an element without a cover and for a cover id with no COVERS row */
	CCCoverData getCoverInfo();

	BufferedImage getCover();
	Tuple<Integer, Integer> getCoverDimensions();

	CCOnlineReferenceList getOnlineReference();

	CCMovieList getMovieList();
}
