package de.jClipCorn.util.stream;

import de.jClipCorn.util.lambda.Func1to1;

public class CondTakeStream<TType> extends CCSimpleStream<TType> {

	private final CCStream<TType> source;
	private final Func1to1<TType, Boolean> predicate;

	public CondTakeStream(CCStream<TType> _source, Func1to1<TType, Boolean> _predicate) {
		super();
		source    = _source;
		predicate = _predicate;
	}

	@Override
	public TType nextOrNothing(boolean first) {

		TType n = source.next();

		if (!predicate.invoke(n)) return finishStream(); else return n;
	}

	@Override
	protected CCStream<TType> cloneFresh() {
		return new CondTakeStream<>(source.cloneFresh(), predicate);
	}
}
