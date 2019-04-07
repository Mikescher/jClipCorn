package de.jClipCorn.gui.frames.watchHistoryFrame.element;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.gui.frames.watchHistoryFrame.WatchHistoryFrame;
import de.jClipCorn.util.datetime.CCDateTime;

public abstract class WatchHistoryElement implements Comparable<WatchHistoryElement> {
	
	public abstract CCDateTime getTimestamp();
	public abstract String getName();
	public abstract BufferedImage getCover();
	public abstract CCQuality getQuality();
	public abstract CCDBLanguageList getLanguage();
	public abstract int getLength();
	public abstract CCTagList getTags();
	public abstract CCFileFormat getFormat();
	public abstract CCFileSize getSize();

	public abstract ImageIcon getNameIcon();
	public abstract String getFullNamePart1();
	public abstract String getFullNamePart2();
	public abstract String getFullNamePart3();
	public abstract CCDateTimeList getHistory();

	public abstract boolean isMovie();
	public abstract boolean isEpisode();

	public abstract void open(WatchHistoryFrame owner);
	
	@Override
	public int compareTo(WatchHistoryElement o) {
		return getTimestamp().compareTo(o.getTimestamp());
	}
}
