package de.jClipCorn.gui.frames.watchHistoryFrame.element;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTags;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.watchHistoryFrame.WatchHistoryFrame;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.datetime.CCDateTime;

public class WatchHistoryMovieElement extends WatchHistoryElement{
	public final CCDateTime Timestamp;
	public final CCMovie Movie;
	
	public WatchHistoryMovieElement(CCDateTime _timestamp, CCMovie _movie) {
		Timestamp = _timestamp;
		Movie = _movie;
	}

	@Override
	public CCDateTime getTimestamp() {
		return Timestamp;
	}

	@Override
	public String getName() {
		return Movie.getCompleteTitle();
	}

	@Override
	public BufferedImage getCover() {
		return Movie.getCover();
	}

	@Override
	public CCMovieQuality getQuality() {
		return Movie.getQuality();
	}

	@Override
	public CCMovieLanguage getLanguage() {
		return Movie.getLanguage();
	}

	@Override
	public int getLength() {
		return Movie.getLength();
	}

	@Override
	public CCMovieTags getTags() {
		return Movie.getTags();
	}

	@Override
	public CCMovieFormat getFormat() {
		return Movie.getFormat();
	}

	@Override
	public CCMovieSize getSize() {
		return Movie.getFilesize();
	}

	@Override
	public boolean isMovie() {
		return true;
	}

	@Override
	public boolean isEpisode() {
		return false;
	}

	@Override
	public ImageIcon getNameIcon() {
		return CachedResourceLoader.getIcon(Resources.ICN_HISTORY_MOVIES);
	}

	@Override
	public String getFullNamePart1() {
		return Movie.getZyklus().getFormatted();
	}

	@Override
	public String getFullNamePart2() {
		return Movie.getTitle();
	}

	@Override
	public String getFullNamePart3() {
		return ""; //$NON-NLS-1$
	}

	@Override
	public CCDateTimeList getHistory() {
		return Movie.getViewedHistory();
	}

	@Override
	public void open(WatchHistoryFrame owner) {
		PreviewMovieFrame pmf = new PreviewMovieFrame(owner, Movie);
		pmf.setVisible(true);
	}
}
