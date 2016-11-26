package de.jClipCorn.util.stream;

import java.util.Iterator;

public class IterableStream<TType> extends CCStream<TType> {

	private final Iterable<TType> ib;
	private final Iterator<TType> it;
	
	public IterableStream(Iterable<TType> _ib) {
		super();
		ib = _ib;
		it = ib.iterator();
	}
	
	@Override
	public boolean hasNext() {
		return it.hasNext();
	}

	@Override
	public TType next() {
		return it.next();
	}

	@Override
	protected CCStream<TType> cloneFresh() {
		return new IterableStream<>(ib);
	}

}
