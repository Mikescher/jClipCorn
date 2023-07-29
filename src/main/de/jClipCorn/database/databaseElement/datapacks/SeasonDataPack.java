package de.jClipCorn.database.databaseElement.datapacks;

import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;

import java.awt.image.BufferedImage;

public class SeasonDataPack implements ISeasonData
{
	private final String title;
	private final int year;
	private final BufferedImage cover;
	private final CCUserScore score;
	private final String scoreComment;

	public SeasonDataPack(String title, int year, BufferedImage cover, CCUserScore score, String scoreComment)
	{
		this.title        = title;
		this.year         = year;
		this.cover        = cover;
		this.score        = score;
		this.scoreComment = scoreComment;
	}

	@Override public String getTitle() { return title; }

	@Override public int getYear() { return year; }

	@Override public BufferedImage getCover() { return cover; }

	@Override public CCUserScore getScore() { return score; }

	@Override public String getScoreComment() { return scoreComment; }
}
