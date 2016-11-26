package de.jClipCorn.database.util.iterators;

import java.util.List;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.util.stream.CCSimpleStream;
import de.jClipCorn.util.stream.CCStream;

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
			if (it.get(pos) instanceof CCSeries) return (CCSeries) it.get(pos);
		}
		return finishStream();
	}

	@Override
	protected CCStream<CCSeries> cloneFresh() {
		return new SeriesIterator(it);
	}
}
