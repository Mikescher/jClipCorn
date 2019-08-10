package de.jClipCorn.gui.frames.watchHistoryFrame.element;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.watchHistoryFrame.WatchHistoryFrame;
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
	public CCMediaInfo getMediaInfo() {
		return Movie.getMediaInfo();
	}

	@Override
	public CCDBLanguageList getLanguage() {
		return Movie.getLanguage();
	}

	@Override
	public int getLength() {
		return Movie.getLength();
	}

	@Override
	public CCTagList getTags() {
		return Movie.getTags();
	}

	@Override
	public CCFileFormat getFormat() {
		return Movie.getFormat();
	}

	@Override
	public CCFileSize getSize() {
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
		return Resources.ICN_HISTORY_MOVIES.get();
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
		PreviewMovieFrame.show(owner, Movie);
	}
}
