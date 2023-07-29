package de.jClipCorn.database.databaseElement.datapacks;

import de.jClipCorn.database.databaseElement.columnTypes.*;

import java.awt.image.BufferedImage;

public class SeriesDataPack implements ISeriesData
{
	private final String title;
	private final CCGenreList genres;
	private final CCOnlineScore onlinescore;
	private final CCFSK fsk;
	private final CCUserScore score;
	private final String scoreComment;
	private final CCOnlineReferenceList onlineReference;
	private final CCGroupList groups;
	private final CCTagList tags;
	private final BufferedImage cover;

	public SeriesDataPack(String title, CCGenreList genres, CCOnlineScore onlinescore,
						  CCFSK fsk, CCUserScore score, String scoreComment, CCOnlineReferenceList onlineReference,
						  CCGroupList groups, CCTagList tags, BufferedImage cover)
	{
		this.title           = title;
		this.genres          = genres;
		this.onlinescore     = onlinescore;
		this.fsk             = fsk;
		this.score           = score;
		this.scoreComment    = scoreComment;
		this.onlineReference = onlineReference;
		this.groups          = groups;
		this.tags            = tags;
		this.cover           = cover;
	}

	@Override public String getTitle() { return title; }

	@Override public CCGenreList getGenres() { return genres; }

	@Override public CCOnlineScore getOnlinescore() { return onlinescore; }

	@Override public CCFSK getFSK() { return fsk; }

	@Override public CCUserScore getScore() { return score; }

	@Override public String getScoreComment() { return scoreComment; }

	@Override public CCOnlineReferenceList getOnlineReference() { return onlineReference; }

	@Override public CCGroupList getGroups() { return groups; }

	@Override public CCTagList getTags() { return tags; }

	@Override public BufferedImage getCover() { return cover; }
}
