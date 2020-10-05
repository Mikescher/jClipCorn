package de.jClipCorn.database.databaseElement.datapacks;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.util.datetime.CCDate;

public interface IEpisodeData
{
	int getEpisodeNumber();
	String getTitle();
	int getLength();
	CCFileFormat getFormat();
	CCFileSize getFilesize();
	String getPart();
	CCDate getAddDate();
	CCDateTimeList getViewedHistory();
	CCTagList getTags();
	CCDBLanguageList getLanguage();
	CCMediaInfo getMediaInfo();
}
