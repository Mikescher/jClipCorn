package de.jClipCorn.database.util.iterators;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.ICCDatedElement;
import de.jClipCorn.util.stream.CCSimpleStream;
import de.jClipCorn.util.stream.CCStream;

import java.util.List;

public class DatedElementsIterator extends CCSimpleStream<ICCDatedElement> {
	private int pos = -1;
	private CCSeries currSeries;
	private int posSeason = -1;
	
	private final List<CCDatabaseElement> it;
	
	public DatedElementsIterator(List<CCDatabaseElement> ownerIterator) {
		it = ownerIterator;
	}

	@Override
	public ICCDatedElement nextOrNothing(boolean first) {
		for(;;) {
			if (currSeries != null && posSeason + 1 < currSeries.getSeasonCount()) {
				posSeason++;
				return currSeries.getSeasonByArrayIndex(posSeason);
			}
			
			pos++;
			currSeries = null;
			
			if (pos >= it.size()) return finishStream();
			
			if (it.get(pos) instanceof CCMovie) {
				return (ICCDatedElement) it.get(pos);
			} else {
				currSeries = it.get(pos).asSeries();
				posSeason = -1;
			}
		}
	}

	@Override
	protected CCStream<ICCDatedElement> cloneFresh() {
		return new DatedElementsIterator(it);
	}
}
