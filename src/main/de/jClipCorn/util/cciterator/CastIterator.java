package de.jClipCorn.util.cciterator;

public class CastIterator<TSourceType, TTargetType> extends CCIterator<TTargetType> {
	
	private final CCIterator<TSourceType> source;
	
	public CastIterator(CCIterator<TSourceType> _source) {
		source = _source;
	}
	
	@Override
	public boolean hasNext() {
		return source.hasNext();
	}

	@SuppressWarnings("unchecked")
	@Override
	public TTargetType next() {
		return (TTargetType)source.next();
	}

	@Override
	protected CCIterator<TTargetType> cloneFresh() {
		return new CastIterator<>(source.cloneFresh());
	}
}
