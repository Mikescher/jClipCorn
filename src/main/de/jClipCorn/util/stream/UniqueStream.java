package de.jClipCorn.util.stream;

import java.util.HashSet;
import java.util.Set;

public class UniqueStream<TType> extends CCSimpleStream<TType> {
	private final CCStream<TType> source;
	
	private final Set<TType> oldValues = new HashSet<>();
	
	public UniqueStream(CCStream<TType> _source) {
		super();
		source = _source;
	}

	@Override
	public TType nextOrNothing(boolean first) {
		while (source.hasNext()) {
			TType v = source.next();

			if (! oldValues.contains(v)) { oldValues.add(v); return v; }
		}
		
		return finishStream();
	}

	@Override
	protected CCStream<TType> cloneFresh() {
		return new UniqueStream<>(source.cloneFresh());
	}
}
