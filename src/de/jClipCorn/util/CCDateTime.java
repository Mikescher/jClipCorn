package de.jClipCorn.util;

import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.DateTimeFormatException;

@SuppressWarnings("nls")
public class CCDateTime implements Comparable<CCDateTime> {
	public final CCDate date;
	public final CCTime time;
	
	public CCDateTime(CCDate d, CCTime t) {
		date = d;
		time = t;
	}

	public static CCDateTime createFromSQL(String str) throws CCFormatException {
		if (str.length() != 18) throw new DateTimeFormatException(str);
		
		String[] parts = str.split(" ");

		if (parts.length != 2) throw new DateTimeFormatException(str);
		
		return new CCDateTime(CCDate.createFromSQL(parts[0]), CCTime.createFromSQL(parts[0]));
	}

	public static CCDateTime create(CCDate d) {
		return new CCDateTime(d, CCTime.MIDNIGHT);
	}
	
	public String getSQLStringRepresentation() {
		return date.getSQLStringRepresentation() + " " + time.getSQLStringRepresentation();
	}

	public static CCDateTime getCurrentDateTime() {
		return new CCDateTime(CCDate.getCurrentDate(), CCTime.getCurrentTime());
	}

	@Override
	public int compareTo(CCDateTime o) {
		int c = date.compareTo(o.date);
		if (c != 0) return c;
		
		return time.compareTo(o.time);
	}
}
