package de.jClipCorn.gui.frames.batchEditFrame;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.IEpisodeData;
import de.jClipCorn.util.datetime.CCDate;

public class BatchEditEpisodeData implements IEpisodeData
{
	private final CCEpisode _source;

	public int              episodeNumber;
	public String           title;
	public int              length;
	public CCTagList        tags;
	public CCFileFormat     format;
	public CCFileSize       filesize;
	public String           part;
	public CCDate           addDate;
	public CCDateTimeList   viewedHistory;
	public CCDBLanguageList language;
	public CCMediaInfo      mediaInfo;

	public BatchEditEpisodeData(CCEpisode e) {
		_source = e;

		episodeNumber = e.getEpisodeNumber();
		title         = e.getTitle();
		length        = e.getLength();
		tags          = e.getTags();
		format        = e.getFormat();
		filesize      = e.getFilesize();
		part          = e.getPart();
		addDate       = e.getAddDate();
		viewedHistory = e.ViewedHistory.get();
		language      = e.getLanguage();
		mediaInfo     = e.mediaInfo().get();
	}

	public CCEpisode getSource() {
		return _source;
	}

	public boolean isDirty_EpisodeNumber() { return !(_source.getEpisodeNumber() == episodeNumber); }
	public boolean isDirty_Title()         { return !_source.getTitle().equals(title); }
	public boolean isDirty_Length()        { return !(_source.getLength() == length); }
	public boolean isDirty_Tags()          { return !_source.getTags().isEqual(tags); }
	public boolean isDirty_Format()        { return !(_source.getFormat() == format); }
	public boolean isDirty_Filesize()      { return !(_source.getFilesize().getBytes() == filesize.getBytes()); }
	public boolean isDirty_Part()          { return !_source.getPart().equals(part); }
	public boolean isDirty_AddDate()       { return !_source.getAddDate().isEqual(addDate); }
	public boolean isDirty_ViewedHistory() { return !_source.ViewedHistory.get().isEqual(viewedHistory); }
	public boolean isDirty_Language()      { return !_source.getLanguage().isEqual(language); }
	public boolean isDirty_MediaInfo()     { return !_source.mediaInfo().get().isEqual(mediaInfo); }

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
		if (isDirty_MediaInfo()) return true;

		return false;
	}

	@Override public CCDate getAddDate() { return CCDate.getCurrentDate(); }
	@Override public CCDateTimeList getViewedHistory() { return CCDateTimeList.createEmpty(); }
	@Override public CCTagList getTags() { return CCTagList.EMPTY; }
	@Override public CCDBLanguageList getLanguage() { return language; }
	@Override public CCMediaInfo getMediaInfo() { return mediaInfo; }
	@Override public int getEpisodeNumber() { return episodeNumber; }
	@Override public String getTitle() { return title; }
	@Override public int getLength() { return length; }
	@Override public CCFileFormat getFormat() { return format; }
	@Override public CCFileSize getFilesize() { return filesize; }
	@Override public String getPart() { return part; }

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
			if (isDirty_MediaInfo())     _source.MediaInfo.set(mediaInfo);
		}
		_source.endUpdating();
	}
}
