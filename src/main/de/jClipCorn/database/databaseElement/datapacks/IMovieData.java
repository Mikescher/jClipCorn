package de.jClipCorn.database.databaseElement.datapacks;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.util.datetime.CCDate;

import java.util.List;

public interface IMovieData extends IDatabaseElementData
{
	CCMovieZyklus getZyklus();
	CCMediaInfo getMediaInfo();
	int getLength();
	CCDate getAddDate();
	CCFileFormat getFormat();
	int getYear();
	CCFileSize getFilesize();
	List<String> getParts();
	CCDateTimeList getViewedHistory();
	CCDBLanguageList getLanguage();
}
