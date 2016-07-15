package de.jClipCorn.util.datetime;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.DateTimeFormatException;
import de.jClipCorn.util.parser.StringSpecParser;
import de.jClipCorn.util.parser.StringSpecSupplier;

@SuppressWarnings("nls")
public class CCDateTime implements Comparable<CCDateTime>, StringSpecSupplier {
	public final static String STRINGREP_SIMPLE 	= "DD.MM.YYYY HH:mm:ss"; //$NON-NLS-1$
	public final static String STRINGREP_SIMPLESHORT = "DD.MM.YY HH:mm"; //$NON-NLS-1$
	public final static String STRINGREP_LOCAL 		= "DD.N.YYYY HH:mm:ss"; //$NON-NLS-1$
	
	public final CCDate date;
	public final CCTime time;

	private final static HashSet<Character> stringSpecifierDate = CCDate.STATIC_SUPPLIER.getAllStringSpecifier();
	private final static HashSet<Character> stringSpecifierTime = CCTime.STATIC_SUPPLIER.getAllStringSpecifier();
	private final static HashSet<Character> stringSpecifier = getStringSpecifier();
	
	private CCDateTime(CCDate d, CCTime t) {
		date = d;
		time = t;
	}

	public static CCDateTime createFromSQL(String str) throws CCFormatException {
		if (str.length() != 19) throw new DateTimeFormatException(str);
		
		String[] parts = str.split(" ");

		if (parts.length != 2) throw new DateTimeFormatException(str);
		
		return new CCDateTime(CCDate.createFromSQL(parts[0]), CCTime.createFromSQL(parts[1]));
	}

	public static CCDateTime create(CCDate d) {
		return new CCDateTime(d, CCTime.getMidnight());
	}

	public static CCDateTime create(CCDate d, CCTime t) {
		return new CCDateTime(d, t);
	}

	public static CCDateTime getCurrentDateTime() {
		return new CCDateTime(CCDate.getCurrentDate(), CCTime.getCurrentTime());
	}
	
	public String getSQLStringRepresentation() {
		return date.getSQLStringRepresentation() + " " + time.getSQLStringRepresentation();
	}

	@Override
	public int compareTo(CCDateTime o) {
		int c = date.compareTo(o.date);
		if (c != 0) return c;
		
		return time.compareTo(o.time);
	}

	public static CCDateTime create(int dd, int dm, int dy, int th, int tm, int ts) {
		return new CCDateTime(CCDate.create(dd, dm, dy), CCTime.create(th, tm, ts));
	}

	public CCDate getDate() {
		return date;
	}

	public CCTime getTime() {
		return time;
	}
	
	public String getStringRepresentation(String fmt) {
		return StringSpecParser.build(fmt, this);
	}
	
	public String getSimpleStringRepresentation() {
		return getStringRepresentation(STRINGREP_SIMPLE);
	}
	
	public String getSimpleShortStringRepresentation() {
		return getStringRepresentation(STRINGREP_SIMPLE);
	}
	
	public static boolean testparse(String rawData, String fmt) {
		return parse(rawData, fmt) != null;
	}
	
	public static CCDate parse(String rawData, String fmt) {
		return (CCDate)StringSpecParser.parse(rawData, fmt, CCDate.STATIC_SUPPLIER);
	}
	
	public String getLocalStringRepresentation() {
		return getStringRepresentation(STRINGREP_LOCAL);
	}

	public CCDateTime getAddDay(int v) {
		return CCDateTime.create(getDate().getAddDay(v), getTime());
	}

	public CCDateTime getSubDay(int v) {
		return CCDateTime.create(getDate().getSubDay(v), getTime());
	}

	public CCDateTime getAddMonth(int v) {
		return CCDateTime.create(getDate().getAddMonth(v), getTime());
	}

	public CCDateTime getSubMonth(int v) {
		return CCDateTime.create(getDate().getSubMonth(v), getTime());
	}

	public CCDateTime getAddYear(int v) {
		return CCDateTime.create(getDate().getAddYear(v), getTime());
	}

	public CCDateTime getSubYear(int v) {
		return CCDateTime.create(getDate().getSubYear(v), getTime());
	}

	public CCDateTime getAddHour(int v) {
		return CCDateTime.create(getDate(), getTime().getAddHour(v));
	}

	public CCDateTime getSubHour(int v) {
		return CCDateTime.create(getDate(), getTime().getSubHour(v));
	}

	public CCDateTime getAddMinute(int v) {
		return CCDateTime.create(getDate(), getTime().getAddMinute(v));
	}

	public CCDateTime getSubMinute(int v) {
		return CCDateTime.create(getDate(), getTime().getSubMinute(v));
	}

	public CCDateTime getAddSecond(int v) {
		return CCDateTime.create(getDate(), getTime().getAddSecond(v));
	}

	public CCDateTime getSubSecond(int v) {
		return CCDateTime.create(getDate(), getTime().getSubSecond(v));
	}

	@Override
	public String resolveStringSpecifier(char c, int count) {
		if (stringSpecifierDate.contains(c))
			return date.resolveStringSpecifier(c, count);

		if (stringSpecifierTime.contains(c))
			return time.resolveStringSpecifier(c, count);
		
		return null;
	}

	@Override
	public HashSet<Character> getAllStringSpecifier() {
		return stringSpecifier;
	}

	private static HashSet<Character> getStringSpecifier() {
		HashSet<Character> r = new HashSet<>();
		
		for (Character chr : stringSpecifierDate)
			r.add(chr);
		
		for (Character chr : stringSpecifierTime)
			r.add(chr);
				
		return r;
	}

	@Override
	public Object createFromParsedData(Map<Character, Integer> values) {
		CCDate d = (CCDate) CCDate.STATIC_SUPPLIER.createFromParsedData(values);
		CCTime t = (CCTime) CCTime.STATIC_SUPPLIER.createFromParsedData(values);
		
		if (d == null || t == null) return null;
		
		return create(d, t);
	}

	@Override
	public Map<Character, Integer> getSpecDefaults() {
		Map<Character, Integer> d = new Hashtable<>();
		
		for (Entry<Character, Integer> entry : CCDate.STATIC_SUPPLIER.getSpecDefaults().entrySet())
			d.put(entry.getKey(), entry.getValue());
		
		for (Entry<Character, Integer> entry : CCTime.STATIC_SUPPLIER.getSpecDefaults().entrySet())
			d.put(entry.getKey(), entry.getValue());

		return d;
	}
}
