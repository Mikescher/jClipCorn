package de.jClipCorn.database.util.iterators;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.util.stream.CCSimpleStream;
import de.jClipCorn.util.stream.CCStream;

import java.util.List;

public class SeriesIterator extends CCSimpleStream<CCSeries> {
	private int pos = -1;
	private final List<CCDatabaseElement> it;
	
	public SeriesIterator(List<CCDatabaseElement> ownerIterator) {
		it = ownerIterator;
	}

	@Override
	public CCSeries nextOrNothing(boolean first) {
		while (pos + 1 < it.size()) {
			pos++;
			if (it.get(pos).isSeries()) return it.get(pos).asSeries();
		}
		return finishStream();
	}

	@Override
	protected CCStream<CCSeries> cloneFresh() {
		return new SeriesIterator(it);
	}
}
