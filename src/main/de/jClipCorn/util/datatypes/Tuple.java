package de.jClipCorn.util.datatypes;

public class Tuple<X, Y> implements ITuple {
	public final X Item1;
	public final Y Item2;

	public Tuple(X i1, Y i2) {
		this.Item1 = i1;
		this.Item2 = i2;
	}

	public static <X, Y> Tuple<X, Y> Create(X i1, Y i2) {
		return new Tuple<>(i1, i2);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		
		if (o == this) return true;

		if (! (o instanceof Tuple<?, ?>)) return false;
		
		Tuple<?, ?> other = (Tuple<?, ?>) o;
		
		if (other.Item1 == null && Item1 != null) return false;
		if (other.Item1 == null && Item1 == null) return false;
		if (! other.Item1.equals(Item1)) return false;

		if (other.Item2 == null && Item2 != null) return false;
		if (other.Item2 == null && Item2 == null) return false;
		if (! other.Item2.equals(Item2)) return false;
		
		return true;
	}

	@Override
	public int hashCode() {
		int hc1 = 0;
		if (Item1 != null) hc1 = Item1.hashCode();
		
		int hc2 = 0;
		if (Item2 != null) hc2 = Item2.hashCode();
	
		return 31 * hc1 + hc2;
	}

	@Override
	@SuppressWarnings("nls")
	public String toString() {
		String s1 = "NULL";
		if (Item1 != null) s1 = Item1.toString();
		
		String s2 = "NULL";
		if (Item2 != null) s2 = Item2.toString();
		
		
		return "<" + s1 + ", " + s2 + ">";
	}

}