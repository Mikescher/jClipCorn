package de.jClipCorn.util.stream;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortedStream<TType> extends CCSimpleStream<TType> {
	private final CCStream<TType> source;
	private final Comparator<TType> sourceComp;
	
	private int posNext = -1;
	private List<TType> it;
	
	public SortedStream(CCStream<TType> _source, Comparator<TType> _comparator) {
		source = _source;
		sourceComp = _comparator;
	}

	@Override
	public TType nextOrNothing(boolean first) {
		if (first) {
			it = source.enumerate();
			Collections.sort(it, sourceComp);
		}
		
		posNext++;
		if (posNext >= it.size()) return finishStream();
		
		return it.get(posNext);
	}

	@Override
	protected CCStream<TType> cloneFresh() {
		return new SortedStream<>(source, sourceComp);
	}
}
