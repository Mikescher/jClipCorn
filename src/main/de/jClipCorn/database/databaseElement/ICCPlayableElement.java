package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTags;
import de.jClipCorn.util.datetime.CCDate;

public interface ICCPlayableElement {

	public String getTitle();
	public boolean isViewed();
	public CCMovieQuality getQuality();
	public int getLength();
	public CCMovieFormat getFormat();
	public CCMovieSize getFilesize();
	public CCMovieTags getTags();
	public boolean getTag(int c);
	public CCDateTimeList getViewedHistory();
	public CCDate getAddDate();

	public void play(boolean updateViewedAndHistory);
	
}
