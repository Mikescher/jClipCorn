package de.jClipCorn.database.databaseElement.datapacks;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.filesystem.CCPath;

import java.awt.image.BufferedImage;
import java.util.List;

public class MovieDataPack implements IMovieData
{
	private final CCMovieZyklus zyklus;
	private final CCMediaInfo mediaInfo;
	private final int length;
	private final CCDate addDate;
	private final CCFileFormat format;
	private final int year;
	private final CCFileSize filesize;
	private final List<CCPath> parts;
	private final CCDateTimeList viewedHistory;
	private final CCDBLanguageSet language;
	private final CCDBLanguageList subtitles;
	private final String title;
	private final CCGenreList genres;
	private final CCOnlineScore onlinescore;
	private final CCFSK fsk;
	private final CCUserScore score;
	private final CCOnlineReferenceList onlineReference;
	private final CCGroupList groups;
	private final CCTagList tags;
	private final BufferedImage cover;

	public MovieDataPack(CCMovieZyklus zyklus, CCMediaInfo mediaInfo, int length, CCDate addDate,
						 CCFileFormat format, int year, CCFileSize filesize, List<CCPath> parts,
						 CCDateTimeList viewedHistory, CCDBLanguageSet language, CCDBLanguageList subtitles, String title,
						 CCGenreList genres, CCOnlineScore onlinescore, CCFSK fsk, CCUserScore score,
						 CCOnlineReferenceList onlineReference, CCGroupList groups, CCTagList tags, BufferedImage cover)
	{
		this.zyklus = zyklus;
		this.mediaInfo = mediaInfo;
		this.length = length;
		this.addDate = addDate;
		this.format = format;
		this.year = year;
		this.filesize = filesize;
		this.parts = parts;
		this.viewedHistory = viewedHistory;
		this.language = language;
		this.subtitles = subtitles;
		this.title = title;
		this.genres = genres;
		this.onlinescore = onlinescore;
		this.fsk = fsk;
		this.score = score;
		this.onlineReference = onlineReference;
		this.groups = groups;
		this.tags = tags;
		this.cover = cover;
	}

	@Override public CCMovieZyklus getZyklus() { return zyklus; }

	@Override public CCMediaInfo getMediaInfo() { return mediaInfo; }

	@Override public int getLength() { return length; }

	@Override public CCDate getAddDate() { return addDate; }

	@Override public CCFileFormat getFormat() { return format; }

	@Override public int getYear() { return year; }

	@Override public CCFileSize getFilesize() { return filesize; }

	@Override public List<CCPath> getParts() { return parts; }

	@Override public CCDateTimeList getViewedHistory() { return viewedHistory; }

	@Override public CCDBLanguageSet getLanguage() { return language; }

	@Override public CCDBLanguageList getSubtitles() { return subtitles; }

	@Override public String getTitle() { return title; }

	@Override public CCGenreList getGenres() { return genres; }

	@Override public CCOnlineScore getOnlinescore() { return onlinescore; }

	@Override public CCFSK getFSK() { return fsk; }

	@Override public CCUserScore getScore() { return score; }

	@Override public CCOnlineReferenceList getOnlineReference() { return onlineReference; }

	@Override public CCGroupList getGroups() { return groups; }

	@Override public CCTagList getTags() { return tags; }

	@Override public BufferedImage getCover() { return cover; }
}
