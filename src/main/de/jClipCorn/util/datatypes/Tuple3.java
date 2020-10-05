package de.jClipCorn.util.datatypes;

import java.util.Objects;

public class Tuple3<T1, T2, T3> implements ITuple {
	public final T1 Item1;
	public final T2 Item2;
	public final T3 Item3;

	public Tuple3(T1 i1, T2 i2, T3 i3) {
		this.Item1 = i1;
		this.Item2 = i2;
		this.Item3 = i3;
	}

	public static <T1, T2, T3> Tuple3<T1, T2, T3> Create(T1 i1, T2 i2, T3 i3) {
		return new Tuple3<>(i1, i2, i3);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		
		if (o == this) return true;

		if (! (o instanceof Tuple3<?, ?, ?>)) return false;
		
		Tuple3<?, ?, ?> other = (Tuple3<?, ?, ?>) o;
		
		if (other.Item1 == null && Item1 != null) return false;
		if (other.Item1 == null && Item1 == null) return false;
		if (! other.Item1.equals(Item1)) return false;

		if (other.Item2 == null && Item2 != null) return false;
		if (other.Item2 == null && Item2 == null) return false;
		if (! other.Item2.equals(Item2)) return false;

		if (other.Item3 == null && Item3 != null) return false;
		if (other.Item3 == null && Item3 == null) return false;
		if (! other.Item3.equals(Item3)) return false;
		
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Item1, Item2, Item3);
	}

	@Override
	@SuppressWarnings("nls")
	public String toString() {
		String s1 = "NULL";
		if (Item1 != null) s1 = Item1.toString();

		String s2 = "NULL";
		if (Item2 != null) s2 = Item2.toString();

		String s3 = "NULL";
		if (Item3 != null) s3 = Item3.toString();
		
		
		return "<" + s1 + ", " + s2 + ", " + s3 + ">";
	}

}