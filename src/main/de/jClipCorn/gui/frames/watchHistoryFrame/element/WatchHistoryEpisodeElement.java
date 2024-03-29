package de.jClipCorn.gui.frames.watchHistoryFrame.element;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.frames.watchHistoryFrame.WatchHistoryFrame;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDateTime;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;

public class WatchHistoryEpisodeElement extends WatchHistoryElement {
	public final int Counter;
	public final CCDateTime Timestamp;
	public final CCEpisode Episode;
	
	public WatchHistoryEpisodeElement(int _c, CCDateTime _timestamp, CCEpisode _episode) {
		Counter = _c;
		Timestamp = _timestamp;
		Episode = _episode;
	}

	@Override
	public CCDateTime getTimestamp() {
		return Timestamp;
	}

	@Override
	@SuppressWarnings("nls")
	public String getName() {
		return Str.format("{0} E{1,number,###} - {2}", Episode.getSeries().getTitle(), Episode.getGlobalEpisodeNumber(), Episode.getTitle());
	}

	@Override
	public BufferedImage getCover() {
		return Episode.getSeason().getCover();
	}

	@Override
	public CCQualityCategory getMediaInfoCategory() {
		return Episode.getMediaInfoCategory();
	}

	@Override
	public CCDBLanguageSet getLanguage() {
		return Episode.getLanguage();
	}

	@Override
	public int getLength() {
		return Episode.getLength();
	}

	@Override
	public CCTagList getTags() {
		return Episode.getTags();
	}

	@Override
	public CCFileFormat getFormat() {
		return Episode.getFormat();
	}

	@Override
	public CCFileSize getSize() {
		return Episode.getFilesize();
	}

	@Override
	public int getCounter() {
		return Counter;
	}

	@Override
	public boolean isMovie() {
		return false;
	}

	@Override
	public boolean isEpisode() {
		return true;
	}

	@Override
	public ImageIcon getNameIcon() {
		return Resources.ICN_HISTORY_SERIES.get();
	}

	@Override
	public String getFullNamePart1() {
		return Episode.getSeries().getTitle();
	}

	@Override
	@SuppressWarnings("nls")
	public String getFullNamePart2() {
		return MessageFormat.format("{0} - E{1}", Episode.getSeason().getTitle(), Episode.getEpisodeNumber());
	}

	@Override
	public String getFullNamePart3() {
		return Episode.getTitle();
	}

	@Override
	public CCDateTimeList getHistory() {
		return Episode.ViewedHistory.get();
	}

	@Override
	public void open(WatchHistoryFrame owner) {
		PreviewSeriesFrame.show(owner, Episode, false);
	}
}
