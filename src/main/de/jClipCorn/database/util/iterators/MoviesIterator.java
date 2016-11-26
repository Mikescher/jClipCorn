package de.jClipCorn.database.util.iterators;

import java.util.List;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.util.stream.CCStream;

public class MoviesIterator extends CCStream<CCMovie> {
	private boolean active = true;
	private int pos = -1;
	private final List<CCDatabaseElement> it;
	
	public MoviesIterator(List<CCDatabaseElement> ownerIterator) {
		it = ownerIterator;
		skip();
	}

	@Override
	public boolean hasNext() {
		return active;
	}

	@Override
	public CCMovie next() {
		CCMovie mov = (CCMovie)it.get(pos);
		
		skip();
		
		return mov;
	}
	
	private void skip() {
		do {
			pos++;
		} while ((active = pos < it.size()) && !(it.get(pos) instanceof CCMovie));
	}

	@Override
	public void remove() {
		it.remove(pos);
		pos--;
		skip();
	}

	@Override
	protected CCStream<CCMovie> cloneFresh() {
		return new MoviesIterator(it);
	}
}
