package de.jClipCorn.util.stream;

import java.util.List;

public class ReverseStream<T> extends CCSimpleStream<T> {

	private final CCStream<T> source;
	
	private int posCurr = -1;
	private List<T> ls;

	public ReverseStream(CCStream<T> _source) {
		source = _source;
	}

	@Override
	public T nextOrNothing(boolean first) {
		if (first) {
			ls = source.enumerateThisInternal();
			posCurr = ls.size();
		}
		
		posCurr--;
		if (posCurr < 0) return finishStream();
		
		return ls.get(posCurr);
	}

	@Override
	protected CCStream<T> cloneFresh() {
		return new ReverseStream<>(source.cloneFresh());
	}
}
