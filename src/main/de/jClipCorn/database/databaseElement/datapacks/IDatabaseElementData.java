package de.jClipCorn.database.databaseElement.datapacks;

import de.jClipCorn.database.databaseElement.columnTypes.*;

import java.awt.image.BufferedImage;

public interface IDatabaseElementData
{
	String getTitle();
	CCGenreList getGenres();
	CCOnlineScore getOnlinescore();
	CCFSK getFSK();
	CCUserScore getScore();
	String getScoreComment();
	CCOnlineReferenceList getOnlineReference();
	CCGroupList getGroups();
	CCTagList getTags();
	BufferedImage getCover();
}
