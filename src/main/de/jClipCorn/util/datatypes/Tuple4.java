package de.jClipCorn.util.datatypes;

import java.util.Objects;

public class Tuple4<T1, T2, T3, T4> {
	public final T1 Item1;
	public final T2 Item2;
	public final T3 Item3;
	public final T4 Item4;

	public Tuple4(T1 i1, T2 i2, T3 i3, T4 i4) {
		this.Item1 = i1;
		this.Item2 = i2;
		this.Item3 = i3;
		this.Item4 = i4;
	}

	public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> Create(T1 i1, T2 i2, T3 i3, T4 i4) {
		return new Tuple4<>(i1, i2, i3, i4);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		
		if (o == this) return true;

		if (! (o instanceof Tuple4<?, ?, ?, ?>)) return false;
		
		Tuple4<?, ?, ?, ?> other = (Tuple4<?, ?, ?, ?>) o;
		
		if (other.Item1 == null && Item1 != null) return false;
		if (other.Item1 == null && Item1 == null) return false;
		if (! other.Item1.equals(Item1)) return false;

		if (other.Item2 == null && Item2 != null) return false;
		if (other.Item2 == null && Item2 == null) return false;
		if (! other.Item2.equals(Item2)) return false;

		if (other.Item3 == null && Item3 != null) return false;
		if (other.Item3 == null && Item3 == null) return false;
		if (! other.Item3.equals(Item3)) return false;

		if (other.Item4 == null && Item4 != null) return false;
		if (other.Item4 == null && Item4 == null) return false;
		if (! other.Item4.equals(Item4)) return false;
		
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Item1, Item2, Item3, Item4);
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

		String s4 = "NULL";
		if (Item4 != null) s4 = Item4.toString();
		
		
		return "<" + s1 + ", " + s2 + ", " + s3 + ", " + s4 + ">";
	}

}