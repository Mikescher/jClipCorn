package de.jClipCorn.database.databaseErrors;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCSeason;

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

	public boolean equalsCover(DatabaseCoverElement a) {
		return getCover().equals(a.getCover());
	}
}