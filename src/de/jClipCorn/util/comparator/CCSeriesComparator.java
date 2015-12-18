package de.jClipCorn.util.comparator;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.CCSeries;

public class CCSeriesComparator implements Comparator<CCSeries> {
	
	@Override
	public int compare(CCSeries o1, CCSeries o2) {
		return o1.getTitle().compareTo(o2.getTitle());
	}

}