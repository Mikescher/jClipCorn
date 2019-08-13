package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.util.datetime.CCDate;

import java.util.List;

public interface ICCPlayableElement {
	// Episode, Movie

	String getTitle();
	boolean isViewed();
	int getLength();
	CCMediaInfo getMediaInfo();
	CCQualityCategory getMediaInfoCategory();
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
	void setMediaInfo(CCMediaInfo minfo);

	void play(boolean updateViewedAndHistory);

	CCGenreList getGenresFromSelfOrParent();
}
