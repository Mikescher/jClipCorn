package de.jClipCorn.database.util.iterators;

import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.util.stream.CCSimpleStream;
import de.jClipCorn.util.stream.CCStream;

import java.util.List;

public class StructureElementsIterator extends CCSimpleStream<ICCDatabaseStructureElement> {
	private int posCurr_list    = 0;
	private int posCurr_season  = -1;
	private int posCurr_episode = -1;

	private final List<CCDatabaseElement> it;

	public StructureElementsIterator(List<CCDatabaseElement> ownerIterator) {
		it = ownerIterator;
	}

	@Override
	public ICCDatabaseStructureElement nextOrNothing(boolean first) {
		
		while(true) {
			if (posCurr_list >= it.size()) return finishStream();

			if (it.get(posCurr_list) instanceof CCMovie) { posCurr_season = -1; posCurr_episode = -1; posCurr_list++; return it.get(posCurr_list-1); }

			CCSeries ser = it.get(posCurr_list).asSeries();

			if (posCurr_season == -1) { posCurr_season++; return ser; }

			if (posCurr_season >= ser.getSeasonCount()) { posCurr_list++; posCurr_season = -1; posCurr_episode = -1; continue; }
			
			CCSeason sea = ser.getSeasonByArrayIndex(posCurr_season);

			if (posCurr_episode == -1) { posCurr_episode++; return sea; }

			if (posCurr_episode >= sea.getEpisodeCount()) { posCurr_season++; posCurr_episode = -1; continue; }

			CCEpisode epi = sea.getEpisodeByArrayIndex(posCurr_episode);

			{ posCurr_episode++; return epi; }
		}
	}

	@Override
	protected CCStream<ICCDatabaseStructureElement> cloneFresh() {
		return new StructureElementsIterator(it);
	}
}
