package de.jClipCorn.util.cciterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortedIterator<TType> extends CCIterator<TType> {
	private final List<TType> source;
	private final Comparator<TType> sourceComp;
	
	private int posNext = 0;
	private final List<TType> it;
	
	public SortedIterator(List<TType> _source, Comparator<TType> _comparator) {
		source = _source;
		sourceComp = _comparator;
		
		it = new ArrayList<>(source);
		Collections.sort(it, sourceComp);
	}
	
	@Override
	public boolean hasNext() {
		return posNext < it.size();
	}

	@Override
	public TType next() {
		posNext++;
		
		return it.get(posNext - 1);
	}

	@Override
	protected CCIterator<TType> cloneFresh() {
		return new SortedIterator<>(source, sourceComp);
	}
}
