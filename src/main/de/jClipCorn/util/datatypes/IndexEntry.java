package de.jClipCorn.util.datatypes;

public class IndexEntry<T> {
	public final int Index;
	public final T   Value;

	public IndexEntry(int idx, T val) {
		this.Index = idx;
		this.Value = val;
	}

	@Override
	public int hashCode() {
		return Index * 31 + ((Value != null) ? Value.hashCode() : 0);
	}

	@Override
	@SuppressWarnings("nls")
	public String toString() {
		return "[" + Index + "] " + ((Value != null) ? Value.toString() : "NULL") + ">";
	}

}