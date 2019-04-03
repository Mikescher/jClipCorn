package de.jClipCorn.features.online.metadata;

import java.awt.image.BufferedImage;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;

public interface ParseResultHandler {
	public String getFullTitle();
	public CCOnlineReferenceList getSearchReference();
	
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
	public void setOnlineReference(CCOnlineReferenceList ref);
	
	public void onFinishInserting();
}
