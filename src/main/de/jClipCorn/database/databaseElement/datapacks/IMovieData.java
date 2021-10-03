package de.jClipCorn.database.databaseElement.datapacks;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.metadata.PartialMediaInfo;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.filesystem.CCPath;

import java.util.List;

public interface IMovieData extends IDatabaseElementData
{
	CCMovieZyklus getZyklus();
	CCMediaInfo getMediaInfo();
	PartialMediaInfo getPartialMediaInfo();
	int getLength();
	CCDate getAddDate();
	CCFileFormat getFormat();
	int getYear();
	CCFileSize getFilesize();
	List<CCPath> getParts();
	CCDateTimeList getViewedHistory();
	CCDBLanguageSet getLanguage();
	CCDBLanguageList getSubtitles();
}
