package de.jClipCorn.gui.frames.watchHistoryFrame.element;

import java.awt.image.BufferedImage;
import java.text.MessageFormat;

import javax.swing.ImageIcon;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.frames.watchHistoryFrame.WatchHistoryFrame;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.datetime.CCDateTime;

public class WatchHistoryEpisodeElement extends WatchHistoryElement {
	public final CCDateTime Timestamp;
	public final CCEpisode Episode;
	
	public WatchHistoryEpisodeElement(CCDateTime _timestamp, CCEpisode _episode) {
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
		return MessageFormat.format("{0} E{1,number,###} - {2}", Episode.getSeries().getTitle(), Episode.getGlobalEpisodeNumber(), Episode.getTitle());
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
	public CCDBLanguageList getLanguage() {
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
		return Episode.getViewedHistory();
	}

	@Override
	public void open(WatchHistoryFrame owner) {
		PreviewSeriesFrame.show(owner, Episode);
	}
}
