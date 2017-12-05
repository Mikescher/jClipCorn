package de.jClipCorn.util.stream;

import de.jClipCorn.util.lambda.Func1to1;

public class FilterStream<TType> extends CCSimpleStream<TType> {

	private CCStream<TType> source;
	private Func1to1<TType, Boolean> filter;
	
	public FilterStream(CCStream<TType> _source, Func1to1<TType, Boolean> _filter) {
		super();
		source = _source;
		filter = _filter;
	}

	@Override
	public TType nextOrNothing(boolean first) {
		while(source.hasNext()) {
			TType v = source.next();
			if (filter.invoke(v)) return v;
		}
		
		return finishStream();
	}

	@Override
	protected CCStream<TType> cloneFresh() {
		return new FilterStream<>(source.cloneFresh(), filter);
	}
}
