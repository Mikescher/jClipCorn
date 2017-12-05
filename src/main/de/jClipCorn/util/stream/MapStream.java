package de.jClipCorn.util.stream;

import de.jClipCorn.util.lambda.Func1to1;

public class MapStream<TSourceType, TTargetType> extends CCStream<TTargetType> {

	private final CCStream<TSourceType> source;
	private final Func1to1<TSourceType, TTargetType> selector;
	
	public MapStream(CCStream<TSourceType> _source, Func1to1<TSourceType, TTargetType> _selector) {
		source = _source;
		selector = _selector;
	}
	
	@Override
	public boolean hasNext() {
		return source.hasNext();
	}

	@Override
	public TTargetType next() {
		return selector.invoke(source.next());
	}

	@Override
	protected CCStream<TTargetType> cloneFresh() {
		return new MapStream<>(source.cloneFresh(), selector);
	}
}
