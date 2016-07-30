package de.jClipCorn.util.comparator;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.CCEpisode;

public class CCEpisodeComparator implements Comparator<CCEpisode> {
	
	@Override
	public int compare(CCEpisode o1, CCEpisode o2) {
		return o1.getTitle().compareTo(o2.getTitle());
	}

}