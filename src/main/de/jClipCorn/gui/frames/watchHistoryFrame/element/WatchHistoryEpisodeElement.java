package de.jClipCorn.gui.frames.watchHistoryFrame.element;

import java.awt.image.BufferedImage;
import java.text.MessageFormat;

import javax.swing.ImageIcon;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.frames.watchHistoryFrame.WatchHistoryFrame;
import de.jClipCorn.gui.resources.CachedResourceLoader;
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
	public CCQuality getQuality() {
		return Episode.getQuality();
	}

	@Override
	public CCDBLanguage getLanguage() {
		return Episode.getSeries().getLanguage();
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
		return CachedResourceLoader.getIcon(Resources.ICN_HISTORY_SERIES);
	}

	@Override
	public String getFullNamePart1() {	
		return Episode.getSeries().getTitle();
	}

	@Override
	public String getFullNamePart2() {	
		return Episode.getSeason().getTitle();
	}

	@Override
	@SuppressWarnings("nls")
	public String getFullNamePart3() {	
		return MessageFormat.format("E{0,number,##} - {1}", Episode.getEpisodeIndexInSeason(), Episode.getTitle());
	}

	@Override
	public CCDateTimeList getHistory() {
		return Episode.getViewedHistory();
	}

	@Override
	public void open(WatchHistoryFrame owner) {
		PreviewSeriesFrame psf = new PreviewSeriesFrame(owner, Episode);
		psf.setVisible(true);
	}
}