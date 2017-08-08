package de.jClipCorn.util.stream;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class UniqueAttributeStream<TType, TAttrType> extends CCSimpleStream<TType> {
	private final CCStream<TType> source;
	
	private final Set<TAttrType> oldValues = new HashSet<>();
	private final Function<TType, TAttrType> selector;
	
	public UniqueAttributeStream(CCStream<TType> _source, Function<TType, TAttrType> _selector) {
		super();
		source = _source;
		selector = _selector;
	}

	@Override
	public TType nextOrNothing(boolean first) {
		while (source.hasNext()) {
			TType v = source.next();
			TAttrType av = selector.apply(v);
			if (! oldValues.contains(av)) { oldValues.add(av); return v; }
		}
		
		return finishStream();
	}

	@Override
	protected CCStream<TType> cloneFresh() {
		return new UniqueAttributeStream<>(source.cloneFresh(), selector);
	}
}
