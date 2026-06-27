package de.jClipCorn.database.databaseElement.datapacks;

import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.util.datatypes.Opt;

import java.awt.image.BufferedImage;

public interface ISeasonData
{
	String getTitle();
	Opt<Integer> getYear();
	BufferedImage getCover();
	CCUserScore getScore();
	String getScoreComment();
}
