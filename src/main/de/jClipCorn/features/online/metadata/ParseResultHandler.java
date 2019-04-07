package de.jClipCorn.features.online.metadata;

import java.awt.image.BufferedImage;

import de.jClipCorn.database.databaseElement.columnTypes.*;

public interface ParseResultHandler {
	String getFullTitle();
	CCOnlineReferenceList getSearchReference();
	
	void setMovieFormat(CCFileFormat cmf);
	void setFilepath(int p, String t);
	void setMovieName(String name);
	void setZyklus(String mZyklusTitle);
	void setZyklusNumber(int iRoman);
	void setFilesize(long size);
	void setQuality(CCQuality q);
	void setYear(int y);
	void setGenre(int gid, int movGenre);
	void setFSK(int fsk);
	void setLength(int l);
	void setScore(int s);
	void setCover(BufferedImage nci);
	void setOnlineReference(CCOnlineReferenceList ref);
	
	void onFinishInserting();
}
