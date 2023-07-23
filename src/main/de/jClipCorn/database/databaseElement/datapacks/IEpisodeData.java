package de.jClipCorn.database.databaseElement.datapacks;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.filesystem.CCPath;

public interface IEpisodeData
{
	int getEpisodeNumber();
	String getTitle();
	int getLength();
	CCFileFormat getFormat();
	CCFileSize getFilesize();
	CCPath getPart();
	CCDate getAddDate();
	CCDateTimeList getViewedHistory();
	CCTagList getTags();
	CCDBLanguageSet getLanguage();
	CCDBLanguageList getSubtitles();
	CCMediaInfo getMediaInfo();
}
