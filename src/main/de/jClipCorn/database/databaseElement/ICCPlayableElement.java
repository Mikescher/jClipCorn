package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.util.datetime.CCDate;

import java.util.List;

public interface ICCPlayableElement {
	// Episode, Movie

	String getTitle();
	boolean isViewed();
	int getLength();
	CCMediaInfo getMediaInfo();
	CCFileFormat getFormat();
	CCFileSize getFilesize();
	CCTagList getTags();
	boolean getTag(CCSingleTag t);
	boolean getTag(int c);
	CCDateTimeList getViewedHistory();
	CCDate getAddDate();
	CCDBLanguageList getLanguage();
	List<String> getParts();

	void setLanguage(CCDBLanguageList lang);
	void setLength(int len);

	void play(boolean updateViewedAndHistory);
}
