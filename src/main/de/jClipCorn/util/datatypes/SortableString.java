package de.jClipCorn.util.datatypes;

public class SortableString implements Comparable<SortableString> {
	public final String Content;
	public final int Order;

	public SortableString(String c, int o) {
		Content = c;
		Order = o;
	}

	@Override
	public String toString() {
		return Content;
	}

	@Override
	public int compareTo(SortableString o) {
		return Integer.compare(Order, o.Order);
	}

	@Override
	public int hashCode() {
		return Content.hashCode() ^ 101*Order;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SortableString))
			return false;
		if (obj == this)
			return true;

		return ((SortableString)obj).Content.equals(Content) && ((SortableString)obj).Order == Order;
	}
}
