package de.jClipCorn.features.databaseErrors;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.ICCCoveredElement;
import de.jClipCorn.util.datatypes.CCUUID;

public class DatabaseCoverElement implements Comparable<DatabaseCoverElement>{
	private final CCUUID coverid;
	private final ICCCoveredElement element;
	
	public DatabaseCoverElement(CCUUID cvr, CCDatabaseElement el) {
		this.coverid = cvr;
		this.element = el;
	}

	public DatabaseCoverElement(CCUUID cvr, CCSeason el) {
		this.coverid = cvr;
		this.element = el;
	}

	@Override
	public int compareTo(DatabaseCoverElement a) {
		return coverid.compareTo(a.coverid);
	}

	public CCUUID getCoverID() {
		return coverid;
	}

	public ICCCoveredElement getElement() {
		return element;
	}

	public boolean equalsCover(DatabaseCoverElement a) {
		if (coverid.isEmpty()) return false; // two elements without a cover do not share one
		return coverid.equals(a.coverid);
	}
}