package de.jClipCorn.util.datatypes;

import java.util.Objects;

public class Tuple1<T1> {
	public final T1 Item1;

	public Tuple1(T1 i1) {
		this.Item1 = i1;
	}

	public static <T1> Tuple1<T1> Create(T1 i1) {
		return new Tuple1<>(i1);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		
		if (o == this) return true;

		if (! (o instanceof Tuple1<?>)) return false;
		
		Tuple1<?> other = (Tuple1<?>) o;
		
		if (other.Item1 == null && Item1 != null) return false;
		if (other.Item1 == null && Item1 == null) return false;
		if (! other.Item1.equals(Item1)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Item1);
	}

	@Override
	@SuppressWarnings("nls")
	public String toString() {
		String s1 = "NULL";
		if (Item1 != null) s1 = Item1.toString();

		return "<" + s1 + ">";
	}

}