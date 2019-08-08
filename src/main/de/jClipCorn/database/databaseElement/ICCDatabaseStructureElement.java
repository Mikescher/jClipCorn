package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.util.datetime.CCDate;

public interface ICCDatabaseStructureElement {
	// Movies, Series, Seasons, Episodes

	String getTitle();
	CCFileFormat getFormat();
	CCDate getAddDate();
	CCTagList getTags();
	ExtendedViewedState getExtendedViewedState();
}
