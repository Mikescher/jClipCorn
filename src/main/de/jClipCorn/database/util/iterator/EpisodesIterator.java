package de.jClipCorn.database.util.iterator;

import java.util.List;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;

public class EpisodesIterator extends CCIterator<CCEpisode> {
	private boolean active = true;
	
	private int posCurr_list = -1;
	private int posCurr_season = -1;
	private int posCurr_episode = -1;
	
	private final List<CCDatabaseElement> it;
	
	public EpisodesIterator(List<CCDatabaseElement> ownerIterator) {
		it = ownerIterator;
		
		skipToNextSeries();
	}

	@Override
	public boolean hasNext() {
		return active;
	}

	@Override
	public CCEpisode next() {
		CCSeries ser = (CCSeries)it.get(posCurr_list);
		CCSeason sea = ser.getSeasonByArrayIndex(posCurr_season);
		CCEpisode ep = sea.getEpisodeByArrayIndex(posCurr_episode);
		
		skipToNextEpisode();
		
		return ep;
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
			skipToNextSeries();
		}
	}
	
	private void skipToNextSeries() {
		posCurr_season = 0;
		posCurr_episode = 0;
		
		for (;;) {
			posCurr_list++;
			
			if (posCurr_list >= it.size()) {active = false; return;}
			if (!(it.get(posCurr_list) instanceof CCSeries)) continue;
			if (((CCSeries)it.get(posCurr_list)).getSeasonCount() == 0) continue;
			if (((CCSeries)it.get(posCurr_list)).getEpisodeCount() == 0) continue;
			
			break;
		}
		
		CCSeries ser = (CCSeries) it.get(posCurr_list);
		
		while (ser.getSeasonByArrayIndex(posCurr_season).getEpisodeCount() == 0) posCurr_season++;
	}
}
