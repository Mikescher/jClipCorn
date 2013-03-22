package de.jClipCorn.gui.frames.checkDatabaseFrame;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;

public class DatabaseCoverElement implements Comparable<DatabaseCoverElement>{
	private final String cover;
	private final Object element;
	
	public DatabaseCoverElement(String cvr, CCDatabaseElement el) {
		this.cover = cvr;
		this.element = el;
	}

	public DatabaseCoverElement(String cvr, CCSeason el) {
		this.cover = cvr;
		this.element = el;
	}

	@Override
	public int compareTo(DatabaseCoverElement a) {
		return getCover().compareTo(a.getCover());
	}

	public String getCover() {
		return cover;
	}

	public Object getElement() {
		return element;
	}
	
	public String getErrorIdent() {
		if (element instanceof CCSeason) {
			CCSeason m = (CCSeason) element;
			return "[" + m.getSeasonID() + "] (" + m.getSeries().getTitle() + ")(" + m.getTitle() + ") "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		} else if (element instanceof CCMovie || element instanceof CCSeries) {
			CCMovie m = (CCMovie) element;
			return "[" + m.getLocalID() + "] (" + m.getTitle() + ") "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else if (element instanceof CCEpisode) {
			CCEpisode m = (CCEpisode) element;
			return "[" + m.getEpisode() + "] (" + m.getSeries().getTitle() + ")(" + m.getSeason().getTitle() + ")(" + m.getTitle() + ") "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		}
		return "[???]"; //$NON-NLS-1$
	}

	public boolean equalsCover(DatabaseCoverElement a) {
		return getCover().equals(a.getCover());
	}
}
