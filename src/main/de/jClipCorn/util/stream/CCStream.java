package de.jClipCorn.util.stream;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ObjectUtils;

import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.lambda.Func2to1;

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CCStream<TType> autosort() {
		return new SortedStream<>(this, (a,b) -> ObjectUtils.compare((Comparable)a, (Comparable)b));
	}

	public <TAttrType extends Comparable<? super TAttrType>> CCStream<TType> autosortByProperty(Func1to1<TType, TAttrType> _selector) {
		return new SortedStream<>(this, (a, b) -> ObjectUtils.compare(_selector.invoke(a), _selector.invoke(b)));
	}

	public <TAttrType> CCStream<TType> sortByProperty(Func1to1<TType, TAttrType> _selector, Comparator<TAttrType> _comparator) {
		return new SortedStream<>(this, new Comparator<TType>() {
			@Override
			public int compare(TType o1, TType o2) {
				return _comparator.compare(_selector.invoke(o1), _selector.invoke(o2));
			}
		});
	}
	
	public <TCastType> CCStream<TCastType> cast() {
		return new CastStream<>(this);
	}
	
	public <TCastType> CCStream<TCastType> cast(Class<TCastType> type) {
		return new CastStream<>(this);
	}
	
	public List<TType> enumerate() {
		List<TType> result = new ArrayList<>();
		
		for (TType v : this) result.add(v);
		
		return result;
	}
	
	public TType[] toArray(TType[] a) {
		return enumerate().toArray(a);
	}

	// enumerate this iterator, the stream is dead after this operation (!)
	public List<TType> enumerateThisInternal() {
		List<TType> result = new ArrayList<>();
		while (hasNext()) result.add(next());
		return result;
	}

	public int count(Func1to1<TType, Boolean> condition) {
		return this.filter(condition).count();
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

	public boolean any(Func1to1<TType, Boolean> condition) {
		for (TType m : this) {
			if (condition.invoke(m)) return true;
		}
		
		return false;
	}

	public boolean all(Func1to1<TType, Boolean> condition) {
		for (TType m : this) {
			if (!condition.invoke(m)) return false;
		}
		
		return true;
	}
	
	public TType maxOrDefault(Comparator<? super TType> comp, TType defValue) {
		return maxOrDefault(p -> p, comp, defValue);
	}
	
	public TType minOrDefault(Comparator<? super TType> comp, TType defValue) {
		return minOrDefault(p -> p, comp, defValue);
	}
	
	public <TAttrType> TAttrType maxOrDefault(Func1to1<TType, TAttrType> selector, Comparator<? super TAttrType> comp, TAttrType defValue) {
		TAttrType current = defValue;
		boolean first = true;
		
		for (TType m : this) {
			TAttrType mattr = selector.invoke(m);
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
	
	public <TAttrType> TAttrType minOrDefault(Func1to1<TType, TAttrType> selector, Comparator<? super TAttrType> comp, TAttrType defValue) {
		TAttrType current = defValue;
		boolean first = true;
		
		for (TType m : this) {
			TAttrType mattr = selector.invoke(m);
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

	public <TAttrType> TAttrType findMostCommon(Func1to1<TType, TAttrType> selector, TAttrType defValue) {
		Map<TAttrType, Integer> cache = new HashMap<>();
		
		for (TType m : this) {
			TAttrType mattr = selector.invoke(m);

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

	public <TAttrType> CCStream<Map.Entry<TAttrType, List<TType>>> groupBy(Func1to1<TType, TAttrType> selector) {
		Map<TAttrType, List<TType>> result = new HashMap<>();

		for (TType m : this) {
			TAttrType mattr = selector.invoke(m);

			List<TType> v = result.computeIfAbsent(mattr, k -> new ArrayList<>());
			v.add(m);
		}

		return CCStreams.iterate(result);
	}

	public <TAttrType> TAttrType findLeastCommon(Func1to1<TType, TAttrType> selector, TAttrType defValue) {
		Map<TAttrType, Integer> cache = new HashMap<>();
		
		for (TType m : this) {
			TAttrType mattr = selector.invoke(m);

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

	public CCStream<TType> filter(Func1to1<TType, Boolean> filter) {
		return new FilterStream<>(this, filter);
	}

	public <TAttrType> CCStream<TAttrType> map(Func1to1<TType, TAttrType> selector) {
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
	
	public int sumInt(Func1to1<TType, Integer> op) {
		int v = 0;
		for (TType t : this) v += op.invoke(t);
		return v;
	}
	
	public float sumFloat(Func1to1<TType, Float> op) {
		float v = 0;
		for (TType t : this) v += op.invoke(t);
		return v;
	}
	
	public double sumDouble(Func1to1<TType, Double> op) {
		double v = 0;
		for (TType t : this) v += op.invoke(t);
		return v;
	}
	
	public TType sum(Func2to1<TType, TType, TType> op, TType startValue) {
		TType v = startValue;
		for (TType t : this) v = op.invoke(v, t);
		return v;
	}
	
	public <TAttrType> TAttrType sum(Func1to1<TType, TAttrType> selector, Func2to1<TAttrType, TAttrType, TAttrType> op, TAttrType startValue) {
		TAttrType v = startValue;
		for (TType t : this) v = op.invoke(v, selector.invoke(t));
		return v;
	}

	public CCStream<TType> unique() {
		return new UniqueStream<>(this);
	}

	public <TAttrType> CCStream<TType> unique(Func1to1<TType, TAttrType> selector) {
		return new UniqueAttributeStream<>(this, selector);
	}

	public <TAttrType> CCStream<TAttrType> flatten(Func1to1<TType, Iterator<TAttrType>> selector) {
		return new FlattenedStream<>(this, selector);
	}
	
	public String stringjoin(Func1to1<TType, String> selector) {
		StringBuilder buildr = new StringBuilder();

		for (TType t : this) buildr.append(selector.invoke(t));
		
		return buildr.toString();
	}
	
	public String stringjoin(Func1to1<TType, String> selector, String seperator) {
		StringBuilder buildr = new StringBuilder();

		boolean first = true;
		for (TType t : this) {
			if (! first) buildr.append(seperator);
			buildr.append(selector.invoke(t));
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

	public CCStream<TType> append(Iterable<TType> t) {
		return new ConcatStream<>(this, CCStreams.iterate(t));
	}

	public CCStream<TType> append(TType[] t) {
		return new ConcatStream<>(this, CCStreams.iterate(t));
	}

	public TType get(int idx) {
		TType value = null;
		for (int i = 0; i <= idx; i++) value = next();
		return value;
	}

	public int findIndex(Func1to1<TType, Boolean> filter) {
		int i = 0;
		for (TType t : this) {
			if (filter.invoke(t)) return i;
			i++;
		}
		return -1;
	}

	public boolean contains(TType other) {
		for (TType t : this) {
			if (other == null ? t == null : other.equals(t)) return true;
		}
		return false;
	}
}
