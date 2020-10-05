package de.jClipCorn.database.databaseElement.datapacks;

import java.awt.image.BufferedImage;

public class SeasonDataPack implements ISeasonData
{
	private final String title;
	private final int year;
	private final BufferedImage cover;

	public SeasonDataPack(String title, int year, BufferedImage cover)
	{
		this.title = title;
		this.year  = year;
		this.cover = cover;
	}

	@Override public String getTitle() { return title; }

	@Override public int getYear() { return year; }

	@Override public BufferedImage getCover() { return cover; }
}
