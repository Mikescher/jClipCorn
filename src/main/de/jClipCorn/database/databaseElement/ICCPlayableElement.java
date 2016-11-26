package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.util.datetime.CCDate;

public interface ICCPlayableElement {

	public String getTitle();
	public boolean isViewed();
	public CCQuality getQuality();
	public int getLength();
	public CCFileFormat getFormat();
	public CCFileSize getFilesize();
	public CCTagList getTags();
	public boolean getTag(int c);
	public CCDateTimeList getViewedHistory();
	public CCDate getAddDate();

	public void play(boolean updateViewedAndHistory);
	
}
