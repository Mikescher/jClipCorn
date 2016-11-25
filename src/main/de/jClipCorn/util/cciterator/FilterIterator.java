package de.jClipCorn.util.cciterator;

import java.util.function.Function;

public class FilterIterator<TType> extends CCIterator<TType> {

	private CCIterator<TType> source;
	private TType current = null;
	private boolean alive = true;
	
	private Function<TType, Boolean> filter;
	
	public FilterIterator(CCIterator<TType> _source, Function<TType, Boolean> _filter) {
		source = _source;
		filter = _filter;
		
		skipFiltered();
	}
	
	@Override
	public boolean hasNext() {
		return alive;
	}

	@Override
	public TType next() {
		TType v = current;
		skipFiltered();
		return v;
	}

	private void skipFiltered() {
		while (source.hasNext()) {
			current = source.next();
			
			if (filter.apply(current)) return;
		}
		alive = false;
	}

	@Override
	protected CCIterator<TType> cloneFresh() {
		return new FilterIterator<>(source.cloneFresh(), filter);
	}
}
