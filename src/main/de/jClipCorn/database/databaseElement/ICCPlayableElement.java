package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.util.datetime.CCDate;

public interface ICCPlayableElement {

	String getTitle();
	boolean isViewed();
	CCQuality getQuality();
	int getLength();
	CCFileFormat getFormat();
	CCFileSize getFilesize();
	CCTagList getTags();
	boolean getTag(CCSingleTag t);
	boolean getTag(int c);
	CCDateTimeList getViewedHistory();
	CCDate getAddDate();

	void play(boolean updateViewedAndHistory);
	
}
