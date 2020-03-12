package de.jClipCorn.gui.frames.vlcRobot;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;

import javax.swing.*;

public class VLCPlaylistEntry {

	public ICCPlayableElement element;
	public boolean autoPlay;

	public Icon getIcon() {

		if (autoPlay) return Resources.ICN_MENUBAR_VLCROBOT.get16x16();

		if (element instanceof CCMovie) return Resources.ICN_TABLE_MOVIE.get();
		if (element instanceof CCEpisode) return Resources.ICN_TABLE_MOVIE.get();

		return null;
	}

	public String getText() {
		return element.getTitle();
	}

	public String getLengthText() {
		return TimeIntervallFormatter.formatPointed(element.getLength());
	}
}
