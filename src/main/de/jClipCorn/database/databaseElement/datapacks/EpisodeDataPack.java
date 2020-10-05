package de.jClipCorn.database.databaseElement.datapacks;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.util.datetime.CCDate;

public class EpisodeDataPack implements IEpisodeData 
{
	private final int episodeNumber;
	private final String title;
	private final int length;
	private final CCFileFormat format;
	private final CCFileSize filesize;
	private final String part;
	private final CCDate addDate;
	private final CCDateTimeList viewedHistory;
	private final CCTagList tags;
	private final CCDBLanguageList language;
	private final CCMediaInfo mediaInfo;

	public EpisodeDataPack(int episodeNumber, String title, int length,
						   CCFileFormat format, CCFileSize filesize, String part,
						   CCDate addDate, CCDateTimeList viewedHistory,
						   CCTagList tags, CCDBLanguageList language, CCMediaInfo mediaInfo)
	{
		this.episodeNumber = episodeNumber;
		this.title = title;
		this.length = length;
		this.format = format;
		this.filesize = filesize;
		this.part = part;
		this.addDate = addDate;
		this.viewedHistory = viewedHistory;
		this.tags = tags;
		this.language = language;
		this.mediaInfo = mediaInfo;
	}

	@Override public int getEpisodeNumber() { return episodeNumber; }

	@Override public String getTitle() { return title; }

	@Override public int getLength() { return length; }

	@Override public CCFileFormat getFormat() { return format; }

	@Override public CCFileSize getFilesize() { return filesize; }

	@Override public String getPart() { return part; }

	@Override public CCDate getAddDate() { return addDate; }

	@Override public CCDateTimeList getViewedHistory() { return viewedHistory; }

	@Override public CCTagList getTags() { return tags; }

	@Override public CCDBLanguageList getLanguage() { return language; }

	@Override public CCMediaInfo getMediaInfo() { return mediaInfo; }
}
