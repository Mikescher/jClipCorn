package de.jClipCorn.util.comparator;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.CCSeason;

public class CCSeasonComparator implements Comparator<CCSeason> {
	
	@Override
	public int compare(CCSeason o1, CCSeason o2) {
		return o1.getTitle().compareTo(o2.getTitle());
	}

}
