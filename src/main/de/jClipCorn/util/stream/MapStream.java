package de.jClipCorn.util.stream;

import java.util.function.Function;

public class MapStream<TSourceType, TTargetType> extends CCStream<TTargetType> {

	private final CCStream<TSourceType> source;
	private final Function<TSourceType, TTargetType> selector;
	
	public MapStream(CCStream<TSourceType> _source, Function<TSourceType, TTargetType> _selector) {
		source = _source;
		selector = _selector;
	}
	
	@Override
	public boolean hasNext() {
		return source.hasNext();
	}

	@Override
	public TTargetType next() {
		return selector.apply(source.next());
	}

	@Override
	protected CCStream<TTargetType> cloneFresh() {
		return new MapStream<>(source.cloneFresh(), selector);
	}
}
