package de.jClipCorn.util.parser;

import java.awt.image.BufferedImage;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;

public interface ParseResultHandler {
	public String getFullTitle();
	
	public void setMovieFormat(CCMovieFormat cmf);
	public void setFilepath(int p, String t);
	public void setMovieName(String name);
	public void setZyklus(String mZyklusTitle);
	public void setZyklusNumber(int iRoman);
	public void setFilesize(long size);
	public void setMovieLanguage(CCMovieLanguage lang);
	public void setQuality(CCMovieQuality q);
	public void setYear(int y);
	public void setGenre(int gid, int movGenre);
	public void setFSK(int fsk);
	public void setLength(int l);
	public void setScore(int s);
	public void setCover(BufferedImage nci);
}
