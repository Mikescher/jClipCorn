package de.jClipCorn.util.stream;

import java.util.HashSet;
import java.util.Set;

import de.jClipCorn.util.lambda.Func1to1;

public class UniqueAttributeStream<TType, TAttrType> extends CCSimpleStream<TType> {
	private final CCStream<TType> source;
	
	private final Set<TAttrType> oldValues = new HashSet<>();
	private final Func1to1<TType, TAttrType> selector;
	
	public UniqueAttributeStream(CCStream<TType> _source, Func1to1<TType, TAttrType> _selector) {
		super();
		source = _source;
		selector = _selector;
	}

	@Override
	public TType nextOrNothing(boolean first) {
		while (source.hasNext()) {
			TType v = source.next();
			TAttrType av = selector.invoke(v);
			if (! oldValues.contains(av)) { oldValues.add(av); return v; }
		}
		
		return finishStream();
	}

	@Override
	protected CCStream<TType> cloneFresh() {
		return new UniqueAttributeStream<>(source.cloneFresh(), selector);
	}
}
