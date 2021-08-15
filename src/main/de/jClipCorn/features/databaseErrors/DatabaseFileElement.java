package de.jClipCorn.features.databaseErrors;

import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.util.filesystem.CCPath;
import org.apache.commons.lang.StringUtils;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;

public class DatabaseFileElement implements Comparable<DatabaseFileElement> {
	private final CCPath path;
	private final ICCPlayableElement element;
	
	public DatabaseFileElement(CCPath path, CCMovie m) {
		this.path = path;
		this.element = m;
	}

	public DatabaseFileElement(CCPath path, CCEpisode e) {
		this.path = path;
		this.element = e;
	}
	
	@Override
	public int compareTo(DatabaseFileElement a) {
		return getPath().toString().compareTo(a.getPath().toString());
	}
	
	public CCPath getPath() {
		return path;
	}

	public boolean equalsPath(DatabaseFileElement dfe) {
		return StringUtils.equalsIgnoreCase(getPath().toString(), dfe.getPath().toString());
	}

	public ICCPlayableElement getElement() {
		return element;
	}
}
