package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.elementValues.*;
import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.properties.types.NamedPathVar;

import java.awt.*;
import java.util.List;


public interface ICCPlayableElement {
	// Episode, Movie

	EStringProp             title();
	EMediaInfoProp          mediaInfo();
	EIntProp                length();
	ETagListProp            tags();
	EEnumProp<CCFileFormat> format();
	EFileSizeProp           fileSize();
	EDateProp               addDate();
	EDateTimeListProp       viewedHistory();
	ELanguageListProp       language();

	boolean isViewed();
	CCQualityCategory getMediaInfoCategory();
	List<String> getParts();

	void play(Component swingOwner, boolean updateViewedAndHistory);
	void play(Component swingOwner, boolean updateViewedAndHistory, NamedPathVar player);
	void updateViewedAndHistoryFromUI();

	CCGenreList getGenresFromSelfOrParent();
}
