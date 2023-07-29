package de.jClipCorn.database.databaseElement.datapacks;

import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;

import java.awt.image.BufferedImage;

public interface ISeasonData
{
	String getTitle();
	int getYear();
	BufferedImage getCover();
	CCUserScore getScore();
	String getScoreComment();
}
