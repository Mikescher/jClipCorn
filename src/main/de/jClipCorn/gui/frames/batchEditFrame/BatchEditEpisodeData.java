package de.jClipCorn.gui.frames.batchEditFrame;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.IEpisodeData;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.util.datetime.CCDate;

public class BatchEditEpisodeData implements IEpisodeData
{
	private final CCEpisode _source;

	private int episodeNumber;
	public boolean episodeNumberDirty = false;

	private String title;
	public boolean titleDirty = false;

	private boolean viewed;
	public boolean viewedDirty = false;

	private int length;
	public boolean lengthDirty = false;

	private CCTagList tags;
	public boolean tagsDirty = false;

	private CCFileFormat format;
	public boolean formatDirty = false;

	private CCFileSize filesize;
	public boolean filesizeDirty = false;

	private String part;
	public boolean partDirty = false;

	private CCDate addDate;
	public boolean addDateDirty = false;

	private CCDateTimeList viewedHistory;
	public boolean viewedHistoryDirty = false;

	private CCDBLanguageList language;
	public boolean languageDirty = false;

	public boolean isDirty = false;

	public BatchEditEpisodeData(CCEpisode e) {
		_source = e;

		episodeNumber = e.getEpisodeNumber();
		title         = e.getTitle();
		viewed        = e.isViewed();
		length        = e.getLength();
		tags          = e.getTags();
		format        = e.getFormat();
		filesize      = e.getFilesize();
		part          = e.getPart();
		addDate       = e.getAddDate();
		viewedHistory = e.getViewedHistory();
		language      = e.getLanguage();
	}

	@Override
	public int getLocalID() {
		return _source.getLocalID();
	}

	@Override
	public void setEpisodeNumber(int en) {
		if (episodeNumber == en) return;
		episodeNumber = en;
		episodeNumberDirty = isDirty = true;
	}

	@Override
	public int getEpisodeNumber() {
		return episodeNumber;
	}

	@Override
	public void setTitle(String t) {
		if (title.equals(t)) return;
		title = t;
		titleDirty = isDirty = true;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setViewed(boolean vwd) {
		if (viewed == vwd) return;
		viewed = vwd;
		viewedDirty = isDirty = true;
	}

	@Override
	public boolean isViewed() {
		return viewed;
	}

	@Override
	public void setLength(int len) {
		if (length == len) return;
		length = len;
		lengthDirty = isDirty = true;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public void setFormat(CCFileFormat fmt) {
		if (format == fmt) return;
		format = fmt;
		formatDirty = isDirty = true;
	}

	@Override
	public CCFileFormat getFormat() {
		return format;
	}

	@Override
	public void setFilesize(CCFileSize fsz) {
		if (filesize.equals(fsz)) return;
		filesize = fsz;
		filesizeDirty = isDirty = true;
	}

	@Override
	public CCFileSize getFilesize() {
		return filesize;
	}

	@Override
	public void setPart(String p) {
		if (part.equals(p)) return;
		part = p;
		partDirty = isDirty = true;
	}

	@Override
	public String getPart() {
		return part;
	}

	@Override
	public void setAddDate(CCDate d) {
		if (addDate.isEqual(d)) return;
		addDate = d;
		addDateDirty = isDirty = true;
	}

	@Override
	public CCDate getAddDate() {
		return addDate;
	}

	@Override
	public void setViewedHistory(CCDateTimeList dlst) {
		if (viewedHistory.isEqual(dlst)) return;
		viewedHistory = dlst;
		viewedHistoryDirty = isDirty = true;
	}

	@Override
	public CCDateTimeList getViewedHistory() {
		return viewedHistory;
	}

	@Override
	public void setTags(CCTagList tgs) {
		if (tags.isEqual(tgs)) return;
		tags = tgs;
		tagsDirty = isDirty = true;
	}

	@Override
	public CCTagList getTags() {
		return tags;
	}

	@Override
	public void setLanguage(CCDBLanguageList lng) {
		if (language.isEqual(lng)) return;
		language = lng;
		languageDirty = isDirty = true;
	}

	@Override
	public CCDBLanguageList getLanguage() {
		return language;
	}

	public CCEpisode getSource() {
		return _source;
	}

	public void apply() {
		if (!isDirty) return;

		_source.beginUpdating();
		{
			if (episodeNumberDirty) _source.setEpisodeNumber(episodeNumber);
			if (titleDirty)         _source.setTitle(title);
			if (viewedDirty)        _source.setViewed(viewed);
			if (lengthDirty)        _source.setLength(length);
			if (tagsDirty)          _source.setTags(tags);
			if (formatDirty)        _source.setFormat(format);
			if (filesizeDirty)      _source.setFilesize(filesize);
			if (partDirty)          _source.setPart(part);
			if (addDateDirty)       _source.setAddDate(addDate);
			if (viewedHistoryDirty) _source.setViewedHistory(viewedHistory);
			if (languageDirty)      _source.setLanguage(language);
		}
		_source.endUpdating();
	}
}
