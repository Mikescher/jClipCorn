package de.jClipCorn.util.cciterator;

import java.util.function.Function;

public class MapIterator<TSourceType, TTargetType> extends CCIterator<TTargetType> {

	private final CCIterator<TSourceType> source;
	private final Function<TSourceType, TTargetType> selector;
	
	public MapIterator(CCIterator<TSourceType> _source, Function<TSourceType, TTargetType> _selector) {
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
	protected CCIterator<TTargetType> cloneFresh() {
		return new MapIterator<>(source.cloneFresh(), selector);
	}
}
