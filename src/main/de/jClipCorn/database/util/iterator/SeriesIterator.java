package de.jClipCorn.database.util.iterator;

import java.util.List;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCSeries;

public class SeriesIterator extends CCIterator<CCSeries> {
	private boolean active = true;
	private int pos = -1;
	private final List<CCDatabaseElement> it;
	
	public SeriesIterator(List<CCDatabaseElement> ownerIterator) {
		it = ownerIterator;
		skip();
	}

	@Override
	public boolean hasNext() {
		return active;
	}

	@Override
	public CCSeries next() {
		CCSeries ser = (CCSeries)it.get(pos);
		
		skip();
		
		return ser;
	}
	
	private void skip() {
		do {
			pos++;
		} while ((active = pos < it.size()) && !(it.get(pos) instanceof CCSeries));
	}

	@Override
	public void remove() {
		it.remove(pos);
		pos--;
		skip();
	}
}
