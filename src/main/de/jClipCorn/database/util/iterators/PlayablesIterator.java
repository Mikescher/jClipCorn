package de.jClipCorn.database.util.iterators;

import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.util.stream.CCSimpleStream;
import de.jClipCorn.util.stream.CCStream;

import java.util.List;

public class PlayablesIterator extends CCSimpleStream<ICCPlayableElement> {
	private int posCurr_list = 0;
	private int posCurr_season = 0;
	private int posCurr_episode = -1;
	
	private final List<CCDatabaseElement> it;
	
	public PlayablesIterator(List<CCDatabaseElement> ownerIterator) {
		it = ownerIterator;
	}

	@Override
	public ICCPlayableElement nextOrNothing(boolean first) {
		if (posCurr_list >= it.size()) return finishStream();
		
		if (!first && it.get(posCurr_list) instanceof CCMovie) { posCurr_list++; posCurr_season = 0; posCurr_episode = -1; }
		
		while(posCurr_list < it.size()) {
			if (it.get(posCurr_list) instanceof CCMovie) { posCurr_season = 0; posCurr_episode = -1; return (ICCPlayableElement) it.get(posCurr_list); }
			
			CCSeries t = it.get(posCurr_list).asSeries();
			if (posCurr_season >= t.getSeasonCount()) { posCurr_list++; posCurr_season = 0; posCurr_episode = -1; continue; }
			
			CCSeason s = t.getSeasonByArrayIndex(posCurr_season);
			if (posCurr_episode + 1 >= s.getEpisodeCount()) { posCurr_season++; posCurr_episode = -1; continue; }
			
			posCurr_episode++;
			
			return s.getEpisodeByArrayIndex(posCurr_episode);
		}
		
		return finishStream();
	}

	@Override
	protected CCStream<ICCPlayableElement> cloneFresh() {
		return new PlayablesIterator(it);
	}
}
