package de.jClipCorn.database.util.iterator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortedIterator<TType> extends CCIterator<TType> {
	private int posNext = 0;
	private final List<TType> source;
	
	public SortedIterator(List<TType> _source, Comparator<TType> _comparator) {
		source = _source;
		Collections.sort(source, _comparator);
	}
	
	@Override
	public boolean hasNext() {
		return posNext < source.size();
	}

	@Override
	public TType next() {
		posNext++;
		
		return source.get(posNext - 1);
	}

}
