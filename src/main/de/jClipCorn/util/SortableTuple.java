package de.jClipCorn.util;

public class SortableTuple<X extends Comparable<? super X>, Y> extends Tuple<X, Y> implements Comparable<SortableTuple<X, Y>> {
	public SortableTuple(X i1, Y i2) {
		super(i1, i2);
	}

	@Override
	public int compareTo(SortableTuple<X, Y> o) {
		return Item1.compareTo(o.Item1);
	}
}