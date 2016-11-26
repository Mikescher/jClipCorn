package de.jClipCorn.util.stream;

public class EmptyStream<TType> extends CCSimpleStream<TType> {

	public EmptyStream() {
		super();
	}

	@Override
	public TType nextOrNothing(boolean first) {
		return finishStream();
	}

	@Override
	protected CCStream<TType> cloneFresh() {
		return new EmptyStream<>();
	}
}
