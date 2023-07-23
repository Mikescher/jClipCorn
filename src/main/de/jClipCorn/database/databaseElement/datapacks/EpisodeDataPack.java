package de.jClipCorn.database.databaseElement.datapacks;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.filesystem.CCPath;

public class EpisodeDataPack implements IEpisodeData 
{
	private final int episodeNumber;
	private final String title;
	private final int length;
	private final CCFileFormat format;
	private final CCFileSize filesize;
	private final CCPath part;
	private final CCDate addDate;
	private final CCDateTimeList viewedHistory;
	private final CCTagList tags;
	private final CCDBLanguageSet language;
	private final CCDBLanguageList subtitles;
	private final CCMediaInfo mediaInfo;

	public EpisodeDataPack(int episodeNumber, String title, int length,
						   CCFileFormat format, CCFileSize filesize,
						   CCPath part,
						   CCDate addDate, CCDateTimeList viewedHistory,
						   CCTagList tags, CCDBLanguageSet language, CCDBLanguageList subtitles, CCMediaInfo mediaInfo)
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
		this.subtitles = subtitles;
		this.mediaInfo = mediaInfo;
	}

	@Override public int getEpisodeNumber() { return episodeNumber; }

	@Override public String getTitle() { return title; }

	@Override public int getLength() { return length; }

	@Override public CCFileFormat getFormat() { return format; }

	@Override public CCFileSize getFilesize() { return filesize; }

	@Override public CCPath getPart() { return part; }

	@Override public CCDate getAddDate() { return addDate; }

	@Override public CCDateTimeList getViewedHistory() { return viewedHistory; }

	@Override public CCTagList getTags() { return tags; }

	@Override public CCDBLanguageSet getLanguage() { return language; }

	@Override public CCDBLanguageList getSubtitles() { return subtitles; }

	@Override public CCMediaInfo getMediaInfo() { return mediaInfo; }
}
