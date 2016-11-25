package de.jClipCorn.database.util.iterator;

import java.util.List;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.ICCDatedElement;
import de.jClipCorn.util.cciterator.CCIterator;

public class DatedElementsIterator extends CCIterator<ICCDatedElement> {
	private boolean active = true;
	
	private int posCurr_list = -1;
	private int posCurr_season = -1;
	
	private final List<CCDatabaseElement> it;
	
	public DatedElementsIterator(List<CCDatabaseElement> ownerIterator) {
		it = ownerIterator;
		
		skipToNextElement();
	}

	@Override
	public boolean hasNext() {
		return active;
	}

	@Override
	public ICCDatedElement next() {
		CCDatabaseElement curr = it.get(posCurr_list);
		
		if (curr instanceof CCMovie) {
			skipToNextElement();
			return (CCMovie)curr;
		} else {
			CCSeries ser = (CCSeries)it.get(posCurr_list);
			CCSeason sea = ser.getSeasonByArrayIndex(posCurr_season);
			
			skipToNextSeason();
			
			return sea;
		}
	}

	private void skipToNextSeason() {
		CCSeries ser = (CCSeries)it.get(posCurr_list);
		
		do {
			posCurr_season++;
		} while (posCurr_season < ser.getSeasonCount() && ser.getSeasonByArrayIndex(posCurr_season).getEpisodeCount() == 0);
		
		if (ser.getSeasonCount() <= posCurr_season) {
			skipToNextElement();
		}
	}
	
	private void skipToNextElement() {
		posCurr_season = 0;
		
		for (;;) {
			posCurr_list++;
			
			if (posCurr_list >= it.size()) {active = false; return;}
			
			if (it.get(posCurr_list) instanceof CCMovie) return;
			
			if (((CCSeries)it.get(posCurr_list)).getSeasonCount() == 0) continue;
			
			break;
		}
	}

	@Override
	protected CCIterator<ICCDatedElement> cloneFresh() {
		return new DatedElementsIterator(it);
	}
}
