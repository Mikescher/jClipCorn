package de.jClipCorn.util.stream;

import java.util.Iterator;

import de.jClipCorn.util.lambda.Func1to1;

public class FlattenedStream<TSourceType, TType> extends CCSimpleStream<TType> {

	private CCStream<TSourceType> source;
	private Iterator<TType> currentIterator;
	
	private final Func1to1<TSourceType, Iterator<TType>> selector;
	
	public FlattenedStream(CCStream<TSourceType> _source, Func1to1<TSourceType, Iterator<TType>> _selector) {
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
			
			currentIterator = selector.invoke(source.next());
			
			if (currentIterator.hasNext()) return currentIterator.next();
		}
	}

	@Override
	protected CCStream<TType> cloneFresh() {
		return new FlattenedStream<>(source.cloneFresh(), selector);
	}
}
