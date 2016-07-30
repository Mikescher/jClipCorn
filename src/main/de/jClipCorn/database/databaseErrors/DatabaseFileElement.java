package de.jClipCorn.database.databaseErrors;

import org.apache.commons.lang.StringUtils;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;

public class DatabaseFileElement implements Comparable<DatabaseFileElement> {
	private final String path;
	private final Object element;
	
	public DatabaseFileElement(String path, CCMovie m) {
		this.path = path;
		this.element = m;
	}

	public DatabaseFileElement(String path, CCEpisode e) {
		this.path = path;
		this.element = e;
	}
	
	@Override
	public int compareTo(DatabaseFileElement a) {
		return getPath().compareTo(a.getPath());
	}
	
	public String getPath() {
		return path;
	}

	public boolean equalsPath(DatabaseFileElement dfe) {
		return StringUtils.equalsIgnoreCase(getPath(), dfe.getPath());
	}

	public Object getElement() {
		return element;
	}
}
