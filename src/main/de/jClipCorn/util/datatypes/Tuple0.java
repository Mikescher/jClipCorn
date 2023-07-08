package de.jClipCorn.util.datatypes;

import java.util.Objects;

public class Tuple0 implements ITuple {

	public static final Tuple0 Inst = new Tuple0();

	private Tuple0() {}

	@Override
	public boolean equals(Object o) {
		if (o == null) return false;

		if (o == this) return true;

		if (! (o instanceof Tuple0)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash();
	}

	@Override
	@SuppressWarnings("nls")
	public String toString() {
		return "<->";
	}

}