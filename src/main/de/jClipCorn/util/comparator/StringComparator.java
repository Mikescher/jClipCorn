package de.jClipCorn.util.comparator;

import java.util.Comparator;

public class StringComparator implements Comparator<String> {
	
	@Override
	public int compare(String o1, String o2) {
	    if (o1 == null) return -1;
	    if (o2 == null) return 1;
	    if (o1.equals(o2)) return 0;
	    return o1.compareTo(o2);
	}

}