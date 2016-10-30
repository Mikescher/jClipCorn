package de.jClipCorn.database.util.iterator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class CCIterator<TType> implements Iterator<TType>, Iterable<TType> {
	@Override
	public Iterator<TType> iterator() {
		return this;
	}
	
	public CCIterator<TType> asSorted(Comparator<TType> _comparator) {
		return new SortedIterator<>(enumerate(), _comparator);
	}
	
	public List<TType> enumerate() {
		List<TType> result = new ArrayList<>();
		
		for (TType v : this) result.add(v);
		
		return result;
	}

	public int count() {
		int c = 0;
		
		for (Iterator<TType> it = this.iterator(); it.hasNext(); c++) {
			it.next();
		}
		
		return c;
	}

	public boolean any() {
		return hasNext();
	}
}
