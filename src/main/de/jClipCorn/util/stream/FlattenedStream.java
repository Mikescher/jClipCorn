package de.jClipCorn.util.stream;

import java.util.Iterator;
import java.util.function.Function;

public class FlattenedStream<TSourceType, TType> extends CCSimpleStream<TType> {

	private CCStream<TSourceType> source;
	private Iterator<TType> currentIterator;
	
	private final Function<TSourceType, Iterator<TType>> selector;
	
	public FlattenedStream(CCStream<TSourceType> _source, Function<TSourceType, Iterator<TType>> _selector) {
		super();
		source = _source;
		selector = _selector;
		currentIterator = null;
	}
	
	@Override
	public TType nextOrNothing(boolean first) {
		if (!first && currentIterator.hasNext()) {
			return currentIterator.next();
		}
		
		for (;;) {
			if (!source.hasNext()) return finishStream();
			
			currentIterator = selector.apply(source.next());
			
			if (currentIterator.hasNext()) return currentIterator.next();
		}
	}

	@Override
	protected CCStream<TType> cloneFresh() {
		return new FlattenedStream<>(source.cloneFresh(), selector);
	}
}
