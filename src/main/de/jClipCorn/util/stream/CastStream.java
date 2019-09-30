package de.jClipCorn.util.stream;

public class CastStream<TSourceType, TTargetType> extends CCStream<TTargetType> {
	private final CCStream<TSourceType> source;

	public CastStream(CCStream<TSourceType> _source) {
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
	protected CCStream<TTargetType> cloneFresh() {
		return new CastStream<>(source.cloneFresh());
	}
}
