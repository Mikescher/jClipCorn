package de.jClipCorn.database.util.iterator;

import java.util.List;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;

public class DatabaseIterator extends CCIterator<CCDatabaseElement> {
	private int posNext = 0;
	private final List<CCDatabaseElement> it;
	
	public DatabaseIterator(List<CCDatabaseElement> ownerIterator) {
		it = ownerIterator;
	}

	@Override
	public boolean hasNext() {
		return posNext < it.size();
	}

	@Override
	public CCDatabaseElement next() {
		posNext++;
		
		return it.get(posNext - 1);
	}
}
