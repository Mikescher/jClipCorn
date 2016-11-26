package de.jClipCorn.util.stream;

import java.util.function.Function;

public class FilterStream<TType> extends CCSimpleStream<TType> {

	private CCStream<TType> source;
	private Function<TType, Boolean> filter;
	
	public FilterStream(CCStream<TType> _source, Function<TType, Boolean> _filter) {
		super();
		source = _source;
		filter = _filter;
	}

	@Override
	public TType nextOrNothing(boolean first) {
		while(source.hasNext()) {
			TType v = source.next();
			if (filter.apply(v)) return v;
		}
		
		return finishStream();
	}

	@Override
	protected CCStream<TType> cloneFresh() {
		return new FilterStream<>(source.cloneFresh(), filter);
	}
}
