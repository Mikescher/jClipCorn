package de.jClipCorn.util.stream;

import de.jClipCorn.util.lambda.Func1to1;

public class CondSkipStream<TType> extends CCSimpleStream<TType> {

	private final CCStream<TType> source;
	private final Func1to1<TType, Boolean> predicate;

	public CondSkipStream(CCStream<TType> _source, Func1to1<TType, Boolean> _predicate) {
		super();
		source    = _source;
		predicate = _predicate;
	}

	@Override
	public TType nextOrNothing(boolean first) {

		if (!source.hasNext()) return finishStream();

		if (first)
		{
			while(source.hasNext())
			{
				TType v = source.next();
				if (predicate.invoke(v)) continue;
				return v;
			}
			return finishStream();
		}
		else
		{
			return source.next();
		}
	}

	@Override
	protected CCStream<TType> cloneFresh() {
		return new CondSkipStream<>(source.cloneFresh(), predicate);
	}
}
