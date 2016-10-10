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
	public static CCDateTime STATIC_SUPPLIER = CCDateTime.create(CCDate.create(1, 1, 2000), CCTime.getUnspecified());
	
	public final static String STRINGREP_SIMPLE 	     = "dd.MM.yyyy HH:mm:ss"; //$NON-NLS-1$
	public final static String STRINGREP_SIMPLESHORT     = "dd.MM.yyyy HH:mm";    //$NON-NLS-1$
	public final static String STRINGREP_SIMPLEDATE      = "dd.MM.yyyy";          //$NON-NLS-1$
	public final static String STRINGREP_SIMPLESHORTDATE = "dd.MM.yyyy";          //$NON-NLS-1$
	public final static String STRINGREP_LOCAL 		     = "dd.N.yyyy HH:mm:ss";  //$NON-NLS-1$
	
	public final CCDate date;
	public final CCTime time;

	private static final CCTime TIME_MIN = new CCTime(0, 0, 0);
	private static final CCTime TIME_MAX = new CCTime(23, 59, 59);

	private static HashSet<Character> stringSpecifierDate = null;
	private static HashSet<Character> stringSpecifierTime = null;
	private static HashSet<Character> stringSpecifier = null; // { 'y', 'M', 'd' } && { 'H', 'm', 's', 'h', 't' }
	
	private CCDateTime(CCDate d, CCTime t) {
		date = d;
		time = t;
	}

	public static CCDateTime createFromSQL(String str) throws CCFormatException {
		if (str.length() == 19) {
			String[] parts = str.split(" ");

			if (parts.length != 2) throw new DateTimeFormatException(str);
			
			return new CCDateTime(CCDate.createFromSQL(parts[0]), CCTime.createFromSQL(parts[1]));
		} else if (str.length() == 10) {
			return new CCDateTime(CCDate.createFromSQL(str), CCTime.getUnspecified());
		}
		
		throw new DateTimeFormatException(str);
	}

	public static CCDateTime create(CCDate d) {
		return new CCDateTime(d, CCTime.getUnspecified());
	}

	public static CCDateTime create(CCDate d, CCTime t) {
		return new CCDateTime(d, t);
	}

	public static CCDateTime getCurrentDateTime() {
		return new CCDateTime(CCDate.getCurrentDate(), CCTime.getCurrentTime());
	}
	
	public String getSQLStringRepresentation() {
		if (time.isUnspecifiedTime()) 
			return date.getSQLStringRepresentation();
		else
			return date.getSQLStringRepresentation() + " " + time.getSQLStringRepresentation();
	}

	@Override
	public int compareTo(CCDateTime o) {
		int c = date.compareTo(o.date);
		if (c != 0) return c;
		
		if (time.isUnspecifiedTime() || o.time.isUnspecifiedTime()) return 0;
		
		return time.compareTo(o.time);
	}

	public static CCDateTime create(int dd, int dm, int dy, int th, int tm, int ts) {
		return new CCDateTime(CCDate.create(dd, dm, dy), CCTime.create(th, tm, ts));
	}

	public static CCDateTime createDateOnly(int dd, int dm, int dy) {
		return new CCDateTime(CCDate.create(dd, dm, dy), CCTime.getUnspecified());
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
		if (time.isUnspecifiedTime()) 
			return getStringRepresentation(STRINGREP_SIMPLEDATE);
		else
			return getStringRepresentation(STRINGREP_SIMPLE);
	}
	
	public String getSimpleShortStringRepresentation() {
		if (time.isUnspecifiedTime()) 
			return getStringRepresentation(STRINGREP_SIMPLESHORTDATE);
		else
			return getStringRepresentation(STRINGREP_SIMPLESHORT);
	}
	
	public static boolean testparse(String rawData, String fmt) {
		return StringSpecParser.testparse(rawData, fmt, CCDateTime.STATIC_SUPPLIER);
	}
	
	public static CCDateTime parse(String rawData, String fmt) throws CCFormatException {
		return (CCDateTime)StringSpecParser.parse(rawData, fmt, CCDateTime.STATIC_SUPPLIER);
	}
	
	public static CCDateTime parseOrDefault(String rawData, String fmt, CCDateTime defaultValue){
		return (CCDateTime)StringSpecParser.parseOrDefault(rawData, fmt, CCDateTime.STATIC_SUPPLIER, defaultValue);
	}
	
	public String getLocalStringRepresentation() {
		if (time.isUnspecifiedTime()) 
			return getStringRepresentation(STRINGREP_SIMPLEDATE);
		else
			return getStringRepresentation(STRINGREP_LOCAL);
	}

	public String getSimpleDateStringRepresentation() {
		return getStringRepresentation(STRINGREP_SIMPLEDATE);
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
		if (stringSpecifierDate == null)
			stringSpecifierDate = CCDate.STATIC_SUPPLIER.getAllStringSpecifier();

		if (stringSpecifierTime == null)
			stringSpecifierTime = CCTime.STATIC_SUPPLIER.getAllStringSpecifier();
			
		if (stringSpecifierDate.contains(c))
			return date.resolveStringSpecifier(c, count);

		if (stringSpecifierTime.contains(c))
			return time.resolveStringSpecifier(c, count);
		
		return null;
	}

	@Override
	public HashSet<Character> getAllStringSpecifier() {
		if (stringSpecifierDate == null)
			stringSpecifierDate = CCDate.STATIC_SUPPLIER.getAllStringSpecifier();

		if (stringSpecifierTime == null)
			stringSpecifierTime = CCTime.STATIC_SUPPLIER.getAllStringSpecifier();
		
		if (stringSpecifier == null) {
			stringSpecifier = new HashSet<>();
			
			for (Character chr : stringSpecifierDate)
				stringSpecifier.add(chr);
			
			for (Character chr : stringSpecifierTime)
				stringSpecifier.add(chr);
		}
		
		return stringSpecifier;
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

	public boolean isLessThan(CCDate date) {
		return isLessThan(create(date, TIME_MIN));
	}

	public boolean isGreaterThan(CCDate date) {
		return isGreaterThan(create(date, TIME_MAX));
	}

	public boolean isLessEqualsThan(CCDate date) {
		return isLessEqualsThan(create(date, TIME_MIN));
	}

	public boolean isGreaterEqualsThan(CCDate date) {
		return isGreaterEqualsThan(create(date, TIME_MAX));
	}
	
	public boolean isGreaterThan(CCDateTime other) {
		return compareTo(other) > 0;
	}
	
	public boolean isLessThan(CCDateTime other) {
		return compareTo(other) < 0;
	}
	
	public boolean isEquals(CCDateTime other) {
		return compareTo(other) == 0;
	}
	
	public boolean isGreaterEqualsThan(CCDateTime other) {
		return compareTo(other) >= 0;
	}
	
	public boolean isLessEqualsThan(CCDateTime other) {
		return compareTo(other) <= 0;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && (other instanceof CCDateTime) && isEquals((CCDateTime)other);
	}

	public boolean isMidnight() {
		return time.isMidnight();
	}

	public boolean isValidDateTime() {
		return date.isValidDate() && time.isValidTime();
	}

	@Override
	public int hashCode() {
		return date.hashCode() ^ 13 * time.hashCode();
	}

	public static CCDateTime getMinimumDateTime() {
		return new CCDateTime(CCDate.getMinimumDate(), CCTime.getMidnight());
	}

	public boolean isMinimum() {
		return date.isMinimum() && time.isMidnight();
	}

	public CCDateTime getSpecifyTimeIfNeeded(CCTime fallbacktime) {
		if (time.isUnspecifiedTime()) return new CCDateTime(date, fallbacktime);
		return this;
	}
}
