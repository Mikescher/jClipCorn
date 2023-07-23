package de.jClipCorn.util.comparator;

import java.util.Comparator;

public class StringComparator implements Comparator<String> {

	private final int prefix;

	public StringComparator() { prefix = 1; }

	public StringComparator(boolean desc) { prefix = (desc) ? -1 : +1; }

	@Override
	public int compare(String o1, String o2) {
	    if (o1 == null)    return prefix * -1;
	    if (o2 == null)    return prefix * +1;
	    if (o1.equals(o2)) return prefix * 0;
	    return prefix * o1.compareTo(o2);
	}

}