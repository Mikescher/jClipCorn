package de.jClipCorn.util.stream;

import de.jClipCorn.util.datatypes.IndexEntry;

public class IndexStream<TType> extends CCSimpleStream<IndexEntry<TType>> {

	private final CCStream<TType> source;
	private int _counter = 0;

	public IndexStream(CCStream<TType> _source) {
		super();
		source = _source;
	}

	@Override
	public IndexEntry<TType> nextOrNothing(boolean first) {
		if (source.hasNext()) return new IndexEntry<>(_counter++, source.next());
		
		return finishStream();
	}

	@Override
	protected CCStream<IndexEntry<TType>> cloneFresh() {
		return new IndexStream<>(source.cloneFresh());
	}
}
