package de.jClipCorn.features.online.metadata;

import de.jClipCorn.database.databaseElement.columnTypes.*;

import java.awt.image.BufferedImage;

public interface ParseResultHandler {
	String getFullTitle();
	String getTitleForParser();
	CCOnlineReferenceList getSearchReference();
	
	void setMovieFormat(CCFileFormat cmf);
	void setMovieName(String name);
	void setZyklus(String mZyklusTitle);
	void setZyklusNumber(int iRoman);
	void setFilesize(CCFileSize size);
	void setYear(int y);
	void setGenre(int gid, CCGenre movGenre);
	void setFSK(CCFSK fsk);
	void setLength(int l);
	void setScore(CCOnlineScore s);
	void setCover(BufferedImage nci);
	void setOnlineReference(CCOnlineReferenceList ref);
	
	void onFinishInserting();
}
