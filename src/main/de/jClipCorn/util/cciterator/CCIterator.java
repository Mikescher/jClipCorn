package de.jClipCorn.util.cciterator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 
 * Friday night, 23:10, Germany
 * ... and I really feel like reimplementing the java8 stream() api
 * 
 */
public abstract class CCIterator<TType> implements Iterator<TType>, Iterable<TType> {
	@Override
	public Iterator<TType> iterator() {
		return cloneFresh();
	}
	
	protected abstract CCIterator<TType> cloneFresh();
	
	public CCIterator<TType> asSorted(Comparator<TType> _comparator) {
		return new SortedIterator<>(enumerate(), _comparator);
	}
	
	public <TCastType> CCIterator<TCastType> asCasted() {
		return new CastIterator<>(this);
	}
	
	public List<TType> enumerate() {
		List<TType> result = new ArrayList<>();
		
		for (TType v : this) result.add(v);
		
		return result;
	}

	public int count() {
		int c = 0;
		
		for (Iterator<TType> it = this.iterator(); it.hasNext(); c++) {
			it.next();
		}
		
		return c;
	}

	public boolean any() {
		return cloneFresh().hasNext();
	}
	
	public Stream<TType> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}
	
	public TType maxOrDefault(Comparator<? super TType> comp, TType defValue) {
		return maxOrDefault(p -> p, comp, defValue);
	}
	
	public TType minOrDefault(Comparator<? super TType> comp, TType defValue) {
		return minOrDefault(p -> p, comp, defValue);
	}
	
	public <TAttrType> TAttrType maxOrDefault(Function<TType, TAttrType> selector, Comparator<? super TAttrType> comp, TAttrType defValue) {
		TAttrType current = defValue;
		boolean first = true;
		
		for (TType m : this) {
			TAttrType mattr = selector.apply(m);
			if (first) {
				current = mattr;
			} else {
				if (comp.compare(mattr, current) > 0) current = mattr;
			}
			first = false;
		}
		
		if (first) 
			return defValue;
		else
			return current;
	}
	
	public <TAttrType> TAttrType minOrDefault(Function<TType, TAttrType> selector, Comparator<? super TAttrType> comp, TAttrType defValue) {
		TAttrType current = defValue;
		boolean first = true;
		
		for (TType m : this) {
			TAttrType mattr = selector.apply(m);
			if (first) {
				current = mattr;
			} else {
				if (comp.compare(mattr, current) < 0) current = mattr;
			}
			first = false;
		}
		
		if (first) 
			return defValue;
		else
			return current;
	}

	public <TAttrType> TAttrType findMostCommon(Function<TType, TAttrType> selector, TAttrType defValue) {
		Map<TAttrType, Integer> cache = new HashMap<>();
		
		for (TType m : this) {
			TAttrType mattr = selector.apply(m);

			Integer v = cache.get(mattr);
			if (v == null) v = 0;
			
			cache.put(mattr, v + 1);
		}
		
		int maxCount = -999;
		TAttrType maxValue = defValue;
		for (Entry<TAttrType, Integer> entry : cache.entrySet()) {
			if (entry.getValue() > maxCount) {
				maxValue = entry.getKey();
				maxCount = entry.getValue();
			}
		}
		
		return maxValue;
	}

	public <TAttrType> TAttrType findLeastCommon(Function<TType, TAttrType> selector, TAttrType defValue) {
		Map<TAttrType, Integer> cache = new HashMap<>();
		
		for (TType m : this) {
			TAttrType mattr = selector.apply(m);

			Integer v = cache.get(mattr);
			if (v == null) v = 0;
			
			cache.put(mattr, v + 1);
		}
		
		int maxCount = Integer.MAX_VALUE;
		TAttrType maxValue = defValue;
		for (Entry<TAttrType, Integer> entry : cache.entrySet()) {
			if (entry.getValue() < maxCount) {
				maxValue = entry.getKey();
				maxCount = entry.getValue();
			}
		}
		
		return maxValue;
	}

	public CCIterator<TType> filter(Function<TType, Boolean> filter) {
		return new FilterIterator<>(this, filter);
	}

	public <TAttrType> CCIterator<TAttrType> map(Function<TType, TAttrType> selector) {
		return new MapIterator<>(this, selector);
	}
}
