package de.jClipCorn.gui.frames.batchEditFrame;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.IEpisodeData;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.ICCPropertySource;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.filesystem.CCPath;

public class BatchEditEpisodeData implements IEpisodeData, ICCPropertySource
{
	private final CCEpisode _source;

	public int              episodeNumber;
	public String           title;
	public int              length;
	public CCTagList        tags;
	public CCFileFormat     format;
	public CCFileSize       filesize;
	public CCPath           part;
	public CCDate           addDate;
	public CCDateTimeList   viewedHistory;
	public CCDBLanguageSet  language;
	public CCDBLanguageList subtitles;
	public CCMediaInfo mediaInfo;

	public BatchEditEpisodeData(CCEpisode e) {
		_source = e;

		episodeNumber = e.EpisodeNumber.get();
		title         = e.Title.get();
		length        = e.Length.get();
		tags          = e.Tags.get();
		format        = e.Format.get();
		filesize      = e.FileSize.get();
		part          = e.Part.get();
		addDate       = e.AddDate.get();
		viewedHistory = e.ViewedHistory.get();
		language      = e.Language.get();
		subtitles     = e.Subtitles.get();
		mediaInfo     = e.MediaInfo.get();
	}

	public CCEpisode getSource() {
		return _source;
	}

	public boolean isDirty_EpisodeNumber() { return !(_source.EpisodeNumber.get() == episodeNumber); }
	public boolean isDirty_Title()         { return !_source.Title.get().equals(title); }
	public boolean isDirty_Length()        { return !(_source.Length.get() == length); }
	public boolean isDirty_Tags()          { return !_source.Tags.get().isEqual(tags); }
	public boolean isDirty_Format()        { return !(_source.Format.get() == format); }
	public boolean isDirty_Filesize()      { return !(_source.FileSize.get().getBytes() == filesize.getBytes()); }
	public boolean isDirty_Part()          { return !_source.Part.get().equals(part); }
	public boolean isDirty_AddDate()       { return !_source.AddDate.get().isEqual(addDate); }
	public boolean isDirty_ViewedHistory() { return !_source.ViewedHistory.get().isEqual(viewedHistory); }
	public boolean isDirty_Language()      { return !_source.Language.get().isEqual(language); }
	public boolean isDirty_Subtitles()     { return !_source.Subtitles.get().isEqual(subtitles); }
	public boolean isDirty_MediaInfo()     { return !_source.MediaInfo.get().isEqual(mediaInfo); }

	public boolean isDirty()
	{
		if (isDirty_EpisodeNumber()) return true;
		if (isDirty_Title()) return true;
		if (isDirty_Length()) return true;
		if (isDirty_Tags()) return true;
		if (isDirty_Format()) return true;
		if (isDirty_Filesize()) return true;
		if (isDirty_Part()) return true;
		if (isDirty_AddDate()) return true;
		if (isDirty_ViewedHistory()) return true;
		if (isDirty_Language()) return true;
		if (isDirty_Subtitles()) return true;
		if (isDirty_MediaInfo()) return true;

		return false;
	}

	@Override public CCDate getAddDate() { return CCDate.getCurrentDate(); }
	@Override public CCDateTimeList getViewedHistory() { return CCDateTimeList.createEmpty(); }
	@Override public CCTagList getTags() { return CCTagList.EMPTY; }
	@Override public CCDBLanguageSet getLanguage() { return language; }
	@Override public CCDBLanguageList getSubtitles() { return subtitles; }
	@Override public CCMediaInfo getMediaInfo() { return mediaInfo; }
	@Override public int getEpisodeNumber() { return episodeNumber; }
	@Override public String getTitle() { return title; }
	@Override public int getLength() { return length; }
	@Override public CCFileFormat getFormat() { return format; }
	@Override public CCFileSize getFilesize() { return filesize; }
	@Override public CCPath getPart() { return part; }

	public void apply() {
		if (!isDirty()) return;

		_source.beginUpdating();
		{
			if (isDirty_EpisodeNumber()) _source.EpisodeNumber.set(episodeNumber);
			if (isDirty_Title())         _source.Title.set(title);
			if (isDirty_Length())        _source.Length.set(length);
			if (isDirty_Tags())          _source.Tags.set(tags);
			if (isDirty_Format())        _source.Format.set(format);
			if (isDirty_Filesize())      _source.FileSize.set(filesize);
			if (isDirty_Part())          _source.Part.set(part);
			if (isDirty_AddDate())       _source.AddDate.set(addDate);
			if (isDirty_ViewedHistory()) _source.ViewedHistory.set(viewedHistory);
			if (isDirty_Language())      _source.Language.set(language);
			if (isDirty_Subtitles())     _source.Subtitles.set(subtitles);
			if (isDirty_MediaInfo())     _source.MediaInfo.set(mediaInfo);
		}
		_source.endUpdating();
	}

	public CCProperties ccprops() {
		return getSource().getMovieList().ccprops();
	}
}
