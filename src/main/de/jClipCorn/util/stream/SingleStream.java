package de.jClipCorn.util.stream;

public class SingleStream<TType> extends CCSimpleStream<TType> {

	private TType element;
	
	public SingleStream(TType _element) {
		super();
		element = _element;
	}

	@Override
	public TType nextOrNothing(boolean first) {
		finishStreamAfterThis();
		return element;
	}

	@Override
	protected CCStream<TType> cloneFresh() {
		return new SingleStream<>(element);
	}
}
