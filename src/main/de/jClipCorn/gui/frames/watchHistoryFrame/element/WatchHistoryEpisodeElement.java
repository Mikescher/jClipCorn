package de.jClipCorn.gui.frames.watchHistoryFrame.element;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTags;
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
	public String getName() {
		return Episode.getTitle();
	}

	@Override
	public BufferedImage getCover() {
		return Episode.getSeason().getCover();
	}

	@Override
	public CCMovieQuality getQuality() {
		return Episode.getQuality();
	}

	@Override
	public CCMovieLanguage getLanguage() {
		return Episode.getSeries().getLanguage();
	}

	@Override
	public int getLength() {
		return Episode.getLength();
	}

	@Override
	public CCMovieTags getTags() {
		return Episode.getTags();
	}

	@Override
	public CCMovieFormat getFormat() {
		return Episode.getFormat();
	}

	@Override
	public CCMovieSize getSize() {
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
	@SuppressWarnings("nls")
	public String getFullName() {
		String p1 = Episode.getSeries().getTitle();
		String p2 = "S" + Episode.getSeason().getIndexForCreatedFolderStructure() + " " + Episode.getSeason().getTitle();
		String p3 = "E" + Episode.getEpisodeIndexInSeason() + " " + Episode.getTitle();
		
		return p1 + " - " + p2 + " - " + p3;
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
