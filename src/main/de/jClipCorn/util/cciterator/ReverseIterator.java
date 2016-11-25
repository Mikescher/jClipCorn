package de.jClipCorn.util.cciterator;

import java.util.Iterator;
import java.util.List;

public class ReverseIterator<T> extends CCIterator<T> {

	private int posCurr = -1;
	private final List<T> it;

	public ReverseIterator(List<T> original) {
		it = original;
		posCurr = it.size();
	}

	@Override
	public Iterator<T> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return posCurr > 0;
	}

	@Override
	public T next() {
		posCurr--;
		return it.get(posCurr);
	}

	@Override
	protected CCIterator<T> cloneFresh() {
		return new ReverseIterator<>(it);
	}
}
