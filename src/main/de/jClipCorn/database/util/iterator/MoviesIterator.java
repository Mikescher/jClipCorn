package de.jClipCorn.database.util.iterator;

import java.util.List;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.util.cciterator.CCIterator;

public class MoviesIterator extends CCIterator<CCMovie> {
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
	protected CCIterator<CCMovie> cloneFresh() {
		return new MoviesIterator(it);
	}
}
