package de.jClipCorn.util.comparator;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.CCMovie;

public class CCMovieComparator implements Comparator<CCMovie> {
	
	@Override
	public int compare(CCMovie o1, CCMovie o2) {
		return o1.getTitle().compareTo(o2.getTitle());
	}

}