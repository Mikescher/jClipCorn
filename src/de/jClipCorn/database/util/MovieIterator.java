package de.jClipCorn.database.util;

import java.util.Iterator;
import java.util.List;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;

public class MovieIterator implements Iterator<CCMovie> {
	private boolean active = true;
	private int pos = -1;
	private final List<CCDatabaseElement> it;
	
	public MovieIterator(List<CCDatabaseElement> ownerIterator) {
		it = ownerIterator;
		skip();
	}

	@Override
	public boolean hasNext() {
		return active;
	}

	@Override
	public CCMovie next() {
		CCMovie m = (CCMovie)it.get(pos);
		
		skip();
		
		return m;
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
}
