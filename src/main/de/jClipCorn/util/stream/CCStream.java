package de.jClipCorn.util.stream;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * 
 * Friday night, 23:10, Germany
 * ... and I really feel like reimplementing the java8 stream() api
 * 
 */
public abstract class CCStream<TType> implements Iterator<TType>, Iterable<TType> {
	@Override
	public Iterator<TType> iterator() {
		return cloneFresh();
	}
	
	protected abstract CCStream<TType> cloneFresh();
	
	public CCStream<TType> sort(Comparator<TType> _comparator) {
		return new SortedStream<>(this, _comparator);
	}
	
	public <TCastType> CCStream<TCastType> cast() {
		return new CastStream<>(this);
	}
	
	public List<TType> enumerate() {
		List<TType> result = new ArrayList<>();
		
		for (TType v : this) result.add(v);
		
		return result;
	}

	// enumerate this iterator, the stream is dead after this operation (!)
	public List<TType> enumerateThisInternal() {
		List<TType> result = new ArrayList<>();
		while (hasNext()) result.add(next());
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

	public CCStream<TType> filter(Function<TType, Boolean> filter) {
		return new FilterStream<>(this, filter);
	}

	public <TAttrType> CCStream<TAttrType> map(Function<TType, TAttrType> selector) {
		return new MapStream<>(this, selector);
	}

	public TType firstOrNull() {
		CCStream<TType> it = cloneFresh();
		if (it.hasNext()) return it.next();
		return null;
	}

	public TType lastOrNull() {
		CCStream<TType> it = cloneFresh();
		TType last = null;
		while (it.hasNext()) last = it.next();
		return last;
	}
	
	public TType sum(BinaryOperator<TType> op, TType startValue) {
		TType v = startValue;
		for (TType t : this) v = op.apply(v, t);
		return v;
	}
	
	public <TAttrType> TAttrType sum(Function<TType, TAttrType> selector, BinaryOperator<TAttrType> op, TAttrType startValue) {
		TAttrType v = startValue;
		for (TType t : this) v = op.apply(v, selector.apply(t));
		return v;
	}

	public CCStream<TType> unique() {
		return new UniqueStream<>(this);
	}

	public <TAttrType> CCStream<TType> unique(Function<TType, TAttrType> selector) {
		return new UniqueAttributeStream<>(this, selector);
	}

	public <TAttrType> CCStream<TAttrType> flatten(Function<TType, Iterator<TAttrType>> selector) {
		return new FlattenedStream<>(this, selector);
	}
	
	public String stringjoin(Function<TType, String> selector) {
		StringBuilder buildr = new StringBuilder();

		for (TType t : this) buildr.append(selector.apply(t));
		
		return buildr.toString();
	}
	
	public String stringjoin(Function<TType, String> selector, String seperator) {
		StringBuilder buildr = new StringBuilder();

		boolean first = true;
		for (TType t : this) {
			if (! first) buildr.append(seperator);
			buildr.append(selector.apply(t));
			first = false;
		}
		
		return buildr.toString();
	}
	
	public CCStream<TType> reverse() {
		return new ReverseStream<>(this);
	}

	public CCStream<TType> prepend(TType t) {
		return new ConcatStream<>(CCStreams.single(t), this);
	}

	public CCStream<TType> prepend(CCStream<TType> t) {
		return new ConcatStream<>(t, this);
	}

	public CCStream<TType> append(TType t) {
		return new ConcatStream<>(this, CCStreams.single(t));
	}

	public CCStream<TType> append(CCStream<TType> t) {
		return new ConcatStream<>(this, t);
	}

	public TType get(int idx) {
		TType value = null;
		for (int i = 0; i <= idx; i++) value = next();
		return value;
	}
}
