package de.jClipCorn.gui.frames.batchEditFrame;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.util.datetime.CCDate;

public class BatchEditEpisodeData
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
		viewedHistory = e.getViewedHistory();
		language      = e.getLanguage();
		mediaInfo     = e.getMediaInfo();
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
	public boolean isDirty_ViewedHistory() { return !_source.getViewedHistory().isEqual(viewedHistory); }
	public boolean isDirty_Language()      { return !_source.getLanguage().isEqual(language); }
	public boolean isDirty_MediaInfo()     { return !_source.getMediaInfo().isEqual(mediaInfo); }

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

	public void apply() {
		if (!isDirty()) return;

		_source.beginUpdating();
		{
			if (isDirty_EpisodeNumber()) _source.setEpisodeNumber(episodeNumber);
			if (isDirty_Title())         _source.setTitle(title);
			if (isDirty_Length())        _source.setLength(length);
			if (isDirty_Tags())          _source.setTags(tags);
			if (isDirty_Format())        _source.setFormat(format);
			if (isDirty_Filesize())      _source.setFilesize(filesize);
			if (isDirty_Part())          _source.setPart(part);
			if (isDirty_AddDate())       _source.setAddDate(addDate);
			if (isDirty_ViewedHistory()) _source.setViewedHistory(viewedHistory);
			if (isDirty_Language())      _source.setLanguage(language);
			if (isDirty_MediaInfo())     _source.setMediaInfo(mediaInfo);
		}
		_source.endUpdating();
	}
}
