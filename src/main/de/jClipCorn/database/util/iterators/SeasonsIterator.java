package de.jClipCorn.database.util.iterators;

import java.util.List;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.util.stream.CCStream;

public class SeasonsIterator extends CCStream<CCSeason> {
	private boolean active = true;
	
	private int posCurr_list = -1;
	private int posCurr_season = -1;
	
	private final List<CCDatabaseElement> it;
	
	public SeasonsIterator(List<CCDatabaseElement> ownerIterator) {
		it = ownerIterator;
		
		skipToNextSeries();
	}

	@Override
	public boolean hasNext() {
		return active;
	}

	@Override
	public CCSeason next() {
		CCSeries ser = (CCSeries)it.get(posCurr_list);
		CCSeason sea = ser.getSeasonByArrayIndex(posCurr_season);
		
		skipToNextSeason();
		
		return sea;
	}
	
	private void skipToNextSeason() {
		CCSeries ser = (CCSeries)it.get(posCurr_list);
		
		do {
			posCurr_season++;
		} while (posCurr_season < ser.getSeasonCount() && ser.getSeasonByArrayIndex(posCurr_season).getEpisodeCount() == 0);
		
		if (ser.getSeasonCount() <= posCurr_season) {
			skipToNextSeries();
		}
	}
	
	private void skipToNextSeries() {
		posCurr_season = 0;
		
		for (;;) {
			posCurr_list++;
			
			if (posCurr_list >= it.size()) {active = false; return;}
			if (!(it.get(posCurr_list) instanceof CCSeries)) continue;
			if (((CCSeries)it.get(posCurr_list)).getSeasonCount() == 0) continue;
			
			break;
		}
	}

	@Override
	protected CCStream<CCSeason> cloneFresh() {
		return new SeasonsIterator(it);
	}
}
