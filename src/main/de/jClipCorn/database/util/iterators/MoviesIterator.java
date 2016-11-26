package de.jClipCorn.database.util.iterators;

import java.util.List;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.util.stream.CCSimpleStream;
import de.jClipCorn.util.stream.CCStream;

public class MoviesIterator extends CCSimpleStream<CCMovie> {
	private int pos = -1;
	private final List<CCDatabaseElement> it;
	
	public MoviesIterator(List<CCDatabaseElement> ownerIterator) {
		it = ownerIterator;
	}

	@Override
	public CCMovie nextOrNothing(boolean first) {
		while (pos + 1 < it.size()) {
			pos++;
			if (it.get(pos) instanceof CCMovie) return (CCMovie) it.get(pos);
		}
		return finishStream();
	}

	@Override
	protected CCStream<CCMovie> cloneFresh() {
		return new MoviesIterator(it);
	}
}
