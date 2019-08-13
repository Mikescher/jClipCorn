package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.util.datetime.CCDate;

public interface IEpisodeData {
	int getLocalID();

	void setEpisodeNumber(int en);
	int getEpisodeNumber();

	void setTitle(String t);
	String getTitle();

	void setViewed(boolean viewed);
	boolean isViewed();

	void setLength(int length);
	int getLength();

	void setFormat(CCFileFormat format);
	CCFileFormat getFormat();

	void setFilesize(CCFileSize filesize);
	CCFileSize getFilesize();

	void setPart(String path);
	String getPart();

	void setAddDate(CCDate date);
	CCDate getAddDate();

	void setViewedHistory(CCDateTimeList datelist);
	CCDateTimeList getViewedHistory();

	void setTags(CCTagList stat);
	CCTagList getTags();

	void setLanguage(CCDBLanguageList language);
	CCDBLanguageList getLanguage();

	void setMediaInfo(CCMediaInfo minfo);
	public CCMediaInfo getMediaInfo();
}
