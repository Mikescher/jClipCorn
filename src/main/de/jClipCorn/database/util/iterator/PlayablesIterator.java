package de.jClipCorn.database.util.iterator;

import java.util.List;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.util.cciterator.CCIterator;

public class PlayablesIterator extends CCIterator<ICCPlayableElement> {
	private boolean active = true;
	
	private int posCurr_list = -1;
	private int posCurr_season = -1;
	private int posCurr_episode = -1;
	
	private final List<CCDatabaseElement> it;
	
	public PlayablesIterator(List<CCDatabaseElement> ownerIterator) {
		it = ownerIterator;
		
		skipToNextElement();
	}

	@Override
	public boolean hasNext() {
		return active;
	}

	@Override
	public ICCPlayableElement next() {
	
		CCDatabaseElement curr = it.get(posCurr_list);
		
		if (curr instanceof CCMovie) {
			skipToNextElement();
			return (CCMovie)curr;
		} else {
			CCSeries ser = (CCSeries)it.get(posCurr_list);
			CCSeason sea = ser.getSeasonByArrayIndex(posCurr_season);
			CCEpisode ep = sea.getEpisodeByArrayIndex(posCurr_episode);
			
			skipToNextEpisode();
			
			return ep;
		}
		
	}

	private void skipToNextEpisode() {
		CCSeries ser = (CCSeries)it.get(posCurr_list);
		CCSeason sea = ser.getSeasonByArrayIndex(posCurr_season);
		
		posCurr_episode++;
		
		if (sea.getEpisodeCount() <= posCurr_episode) {
			skipToNextSeason();
		}
	}

	private void skipToNextSeason() {
		CCSeries ser = (CCSeries)it.get(posCurr_list);
		
		posCurr_episode = 0;
		
		do {
			posCurr_season++;
		} while (posCurr_season < ser.getSeasonCount() && ser.getSeasonByArrayIndex(posCurr_season).getEpisodeCount() == 0);
		
		if (ser.getSeasonCount() <= posCurr_season) {
			skipToNextElement();
		}
	}
	
	private void skipToNextElement() {
		posCurr_season = 0;
		posCurr_episode = 0;
		
		for (;;) {
			posCurr_list++;
			
			if (posCurr_list >= it.size()) {active = false; return;}
			
			if (it.get(posCurr_list) instanceof CCMovie) return;
			
			if (((CCSeries)it.get(posCurr_list)).getSeasonCount() == 0) continue;
			if (((CCSeries)it.get(posCurr_list)).getEpisodeCount() == 0) continue;
			
			break;
		}
		
		CCSeries ser = (CCSeries) it.get(posCurr_list);
		
		while (ser.getSeasonByArrayIndex(posCurr_season).getEpisodeCount() == 0) posCurr_season++;
	}

	@Override
	protected CCIterator<ICCPlayableElement> cloneFresh() {
		return new PlayablesIterator(it);
	}
}
