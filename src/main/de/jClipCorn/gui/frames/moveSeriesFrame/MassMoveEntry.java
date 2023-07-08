package de.jClipCorn.gui.frames.moveSeriesFrame;

import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.util.filesystem.CCPath;

public class MassMoveEntry {
	public ICCPlayableElement entry;
	public CCPath PathOld;
	public CCPath PathNew;
	public boolean OldIsValid;
	public boolean NewIsValid;
}
