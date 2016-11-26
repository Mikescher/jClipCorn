package de.jClipCorn.util.parser.onlineparser;

import java.awt.image.BufferedImage;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;

public interface ParseResultHandler {
	public String getFullTitle();
	public CCOnlineReference getSearchReference();
	
	public void setMovieFormat(CCFileFormat cmf);
	public void setFilepath(int p, String t);
	public void setMovieName(String name);
	public void setZyklus(String mZyklusTitle);
	public void setZyklusNumber(int iRoman);
	public void setFilesize(long size);
	public void setMovieLanguage(CCDBLanguage lang);
	public void setQuality(CCQuality q);
	public void setYear(int y);
	public void setGenre(int gid, int movGenre);
	public void setFSK(int fsk);
	public void setLength(int l);
	public void setScore(int s);
	public void setCover(BufferedImage nci);
	public void setOnlineReference(CCOnlineReference ref);
	
	public void onFinishInserting();
}
