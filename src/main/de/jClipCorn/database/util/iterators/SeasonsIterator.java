package de.jClipCorn.database.util.iterators;

import java.util.List;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.util.stream.CCSimpleStream;
import de.jClipCorn.util.stream.CCStream;

public class SeasonsIterator extends CCSimpleStream<CCSeason> {
	private int pos = -1;
	private CCSeries currSeries;
	private int posSeason = -1;
	
	private final List<CCDatabaseElement> it;
	
	public SeasonsIterator(List<CCDatabaseElement> ownerIterator) {
		it = ownerIterator;
	}

	@Override
	public CCSeason nextOrNothing(boolean first) {
		for(;;) {
			if (currSeries != null && posSeason + 1 < currSeries.getSeasonCount()) {
				posSeason++;
				return currSeries.getSeasonByArrayIndex(posSeason);
			}
			
			pos++;
			currSeries = null;
			
			if (pos >= it.size()) return finishStream();
			
			if (it.get(pos) instanceof CCSeries) {
				currSeries = (CCSeries) it.get(pos);
				posSeason = -1;
			}
		}
	}

	@Override
	protected CCStream<CCSeason> cloneFresh() {
		return new SeasonsIterator(it);
	}
}
