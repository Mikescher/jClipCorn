package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.caches.ICalculationCache;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.database.elementValues.EStringProp;
import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.util.datetime.CCDate;

public interface ICCDatabaseStructureElement {
	// Movies, Series, Seasons, Episodes

	EStringProp             title();

	CCTagList getTags();

	CCFileFormat getFormat();
	CCDate getAddDate();

	ExtendedViewedState getExtendedViewedState();
	String getQualifiedTitle();
	int getLocalID();
	CCMovieList getMovieList();

	ICalculationCache getCache();
}
