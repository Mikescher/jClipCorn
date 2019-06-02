package de.jClipCorn.features.databaseErrors;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCSeason;

public class DatabaseCoverElement implements Comparable<DatabaseCoverElement>{
	private final int coverid;
	private final Object element;
	
	public DatabaseCoverElement(int cvr, CCDatabaseElement el) {
		this.coverid = cvr;
		this.element = el;
	}

	public DatabaseCoverElement(int cvr, CCSeason el) {
		this.coverid = cvr;
		this.element = el;
	}

	@Override
	public int compareTo(DatabaseCoverElement a) {
		return Integer.compare(coverid, a.coverid);
	}

	public int getCoverID() {
		return coverid;
	}

	public Object getElement() {
		return element;
	}

	public boolean equalsCover(DatabaseCoverElement a) {
		return coverid == a.coverid;
	}
}