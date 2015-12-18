package de.jClipCorn.util.comparator;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;

public class CCDatabaseElementComparator implements Comparator<CCDatabaseElement> {
	
	@Override
	public int compare(CCDatabaseElement o1, CCDatabaseElement o2) {
		return o1.getTitle().compareTo(o2.getTitle());
	}

}