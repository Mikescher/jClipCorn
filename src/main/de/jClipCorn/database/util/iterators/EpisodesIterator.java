package de.jClipCorn.database.util.iterators;

import java.util.List;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.util.stream.CCSimpleStream;
import de.jClipCorn.util.stream.CCStream;

public class EpisodesIterator extends CCSimpleStream<CCEpisode> {
	private int posCurr_list = 0;
	private int posCurr_season = 0;
	private int posCurr_episode = -1;
	
	private final List<CCDatabaseElement> it;
	
	public EpisodesIterator(List<CCDatabaseElement> ownerIterator) {
		it = ownerIterator;
	}

	@Override
	public CCEpisode nextOrNothing(boolean first) {
		while(posCurr_list < it.size()) {
			if (it.get(posCurr_list) instanceof CCMovie) { posCurr_list++; posCurr_season = 0; posCurr_episode = -1; continue; }
			
			CCSeries t = (CCSeries) it.get(posCurr_list);
			if (posCurr_season >= t.getSeasonCount()) { posCurr_list++; posCurr_season = 0; posCurr_episode = -1; continue; }
			
			CCSeason s = t.getSeasonByArrayIndex(posCurr_season);
			if (posCurr_episode + 1 >= s.getEpisodeCount()) { posCurr_season++; posCurr_episode = -1; continue; }
			
			posCurr_episode++;
			
			return s.getEpisodeByArrayIndex(posCurr_episode);
		}
		
		return finishStream();
	}

	@Override
	protected CCStream<CCEpisode> cloneFresh() {
		return new EpisodesIterator(it);
	}
}
