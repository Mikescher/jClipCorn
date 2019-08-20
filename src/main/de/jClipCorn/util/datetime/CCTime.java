package de.jClipCorn.util.datetime;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.TimeFormatException;
import de.jClipCorn.util.parser.StringSpecParser;
import de.jClipCorn.util.parser.StringSpecSupplier;

public class CCTime implements Comparable<CCTime>, StringSpecSupplier {
	public static CCTime STATIC_SUPPLIER = new CCTime(12, 0, 0);
	
	private static final CCTime MIDNIGHT    = new CCTime(0, 0, 0); // { 'H', 'm', 's', 'h', 't' }
	private static final CCTime UNSPECIFIED = new CCTime(99, 99, 99);

	public final static String SERIALIZE_FORMAT = InternationalDateTimeFormatHelper.SERIALIZE_TIME;
	
	private final int hour;
	private final int min;
	private final int sec;
	
	private static HashSet<Character> stringSpecifier = null;
	
	public CCTime() {
		Calendar cal = Calendar.getInstance();
		
		if (cal.get(Calendar.AM_PM) == Calendar.PM) {
			hour = cal.get(Calendar.HOUR) + 12;
		} else {
			hour = cal.get(Calendar.HOUR);
		}
		
		min = cal.get(Calendar.MINUTE);
		
		sec = cal.get(Calendar.SECOND);
	}
	
	public CCTime(int h, int m, int s) {
		hour = h;
		min = m;
		sec = s;
	}
	
	public int getHours() {
		return hour;
	}
	
	public int getAMPMHours() {
		if (hour > 12) {
			return hour-12;
		} else {
			return hour;
		}
	}
	
	public int getMinutes() {
		return min;
	}
	
	public int getSeconds() {
		return sec;
	}

	public int getTotalSeconds() {
		return hour*(60*60) + min*(60) + sec;
	}

	public CCTime getSetHour(int h) {
		if (getHours() == h) return this;
		
		while (h < 0) h+= 24;
		return create(h%24, getMinutes(), getSeconds());
	}
	
	public CCTime getSetMinute(int m) {
		if (getMinutes() == m) return this;
		
		while (m < 0) m+= 60;
		return create(getHours(), m%60, getSeconds());
	}
	
	public CCTime getSetSecond(int s) {
		if (getSeconds() == s) return this;
		
		while (s < 0) s+= 60;
		return create(getHours(), getMinutes(), s % 60);
	}
	
	public String getStringRepresentation(String fmt) {
		return StringSpecParser.build(fmt, this);
	}
	
	public static CCTime parseInputOrNull(String rawData) {
		for (String fmt : InternationalDateTimeFormatHelper.DESERIALIZE_TIME_INPUT) {
			CCTime d = CCTime.parseOrDefault(rawData, fmt, null);
			if (d != null) return d;
		}
		
		return null;
	}
	
	public static boolean testparse(String rawData, String fmt) {
		return StringSpecParser.testparse(rawData, fmt, CCTime.STATIC_SUPPLIER);
	}

	public static CCTime parse(String rawData, String fmt) throws CCFormatException {
		return (CCTime)StringSpecParser.parse(rawData, fmt, CCTime.STATIC_SUPPLIER);
	}
	
	public static CCTime parseOrDefault(String rawData, String fmt, CCTime defaultValue){
		return (CCTime)StringSpecParser.parseOrDefault(rawData, fmt, CCTime.STATIC_SUPPLIER, defaultValue);
	}
	
	public static CCTime createFromSQL(String sqlRep) throws TimeFormatException {
		if (sqlRep.length() != 8) throw new TimeFormatException(sqlRep);
		
		int h = (sqlRep.charAt(0)-'0') * 10 +   // h
				(sqlRep.charAt(1)-'0') * 1;     // h
		                                        // :
		int m = (sqlRep.charAt(3)-'0') * 10 +   // M
				(sqlRep.charAt(4)-'0') * 1;     // M
                                                // :
		int s = (sqlRep.charAt(6)-'0') * 10 +   // s
				(sqlRep.charAt(7)-'0') * 1;     // s

		if (sqlRep.charAt(2) != ':') throw new TimeFormatException(sqlRep);
		if (sqlRep.charAt(5) != ':') throw new TimeFormatException(sqlRep);
		
		return new CCTime(h, m, s);
	}

	public static CCTime createFromSQLWithMilliseconds(String sqlRep) throws TimeFormatException {
		if (sqlRep.length() != 12) throw new TimeFormatException(sqlRep);

		int h = (sqlRep.charAt(0)-'0') * 10 +   // h
				(sqlRep.charAt(1)-'0') * 1;     // h
		// :
		int m = (sqlRep.charAt(3)-'0') * 10 +   // M
				(sqlRep.charAt(4)-'0') * 1;     // M
		// :
		int s = (sqlRep.charAt(6)-'0') * 10 +   // s
				(sqlRep.charAt(7)-'0') * 1;     // s

		if (sqlRep.charAt(2) != ':') throw new TimeFormatException(sqlRep);
		if (sqlRep.charAt(5) != ':') throw new TimeFormatException(sqlRep);
		if (sqlRep.charAt(8) != '.') throw new TimeFormatException(sqlRep);

		return new CCTime(h, m, s);
	}
	
	public static CCTime create(int h, int m, int s) {
		return new CCTime(h, m, s);
	}

	public static CCTime getCurrentTime() {
		return new CCTime();
	}
	
	public String toStringSQL() { 
		return getStringRepresentation(InternationalDateTimeFormatHelper.TIME_SQL);
	}
	
	public String toStringSerialize() { 
		return getStringRepresentation(SERIALIZE_FORMAT);
	}

	// with seconds
	public String toStringUINormal() { 
		return InternationalDateTimeFormatHelper.fmtUINormal(this);
	}

	// without sesonds
	public String toStringUIShort() { 
		return InternationalDateTimeFormatHelper.fmtUIShort(this);
	}
	
	public String toStringInput() { 
		return InternationalDateTimeFormatHelper.fmtInput(this);
	}
	
	public String getAMPMString() {
		if (isAM()) {
			return "AM"; //$NON-NLS-1$
		} else {
			return "PM"; //$NON-NLS-1$
		}
	}

	public boolean isAM() {
		return hour <= 12;
	}

	@Override
	public int compareTo(CCTime o) {
		return compare(this, o);
	}

	public static int compare(CCTime o1, CCTime o2) {
		return o1.compare(o2);
	}
	
	public int compare(CCTime other) {
		if (equals(other)) {
			return 0;
		} else if (isGreaterThan(other)) {
			return 1;
		} else {
			return -1;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return (other instanceof CCTime) && isEqual((CCTime)other);
	}

	public boolean isEqual(CCTime other) {
		if (other == null) return false;
		
		if (this.isUnspecifiedTime() && other.isUnspecifiedTime()) return true;
		
		return getSeconds() == other.getSeconds() &&
				getMinutes() == other.getMinutes() &&
				getHours() == other.getHours();
	}
	
	@Override
	public int hashCode() {
		return (((hour << 6) + min) << 6) + sec;
	}
	
	public boolean isGreaterThan(CCTime other) {
		if (getHours() < other.getHours()) {
			return false;
		} else if (getHours() > other.getHours()) {
			return true;
		} else {
			if (getMinutes() < other.getMinutes()) {
				return false;
			} else if (getMinutes() > other.getMinutes()) {
				return true;
			} else {
				if (getSeconds() < other.getSeconds()) {
					return false;
				} else if (getSeconds() > other.getSeconds()) {
					return true;
				} else {
					return false; // THEY ARE F*** EQUAL
				}
			}
		}
	}
	
	public CCTime getAdd(int h, int m, int s) {
		if (h < 0 || m < 0 || s < 0) {
			return null;
		}
		
		int newhours = getHours();
		int newminutes = getMinutes();
		int newseconds = getSeconds();
		
		newhours += h;
		newhours %= 24;
		
		newminutes += m;
		
		while (newminutes >= 60) {
			newminutes -= 60;
			newhours++;
			newhours %= 24;
		}

		newseconds += s;
		
		while (newseconds >= 60) {
			newseconds -= 60;
			newminutes++;
			
			while (newminutes >= 60) {
				newminutes -= 60;
				newhours++;
				newhours %= 24;
			}
		}	
		
		return create(newhours, newminutes, newseconds);
	}
	
	public CCTime getSub(int h, int m, int s) {
		if (h < 0 || m < 0 || s < 0) {
			return null;
		}
		
		int newhours = getHours();
		int newminutes = getMinutes();
		int newseconds = getSeconds();
		
		newhours -= h;
		
		while (newhours < 0) newhours += 24;
		
		newminutes -= m;
		
		while (newminutes < 0) {
			newminutes += 60;
			newhours--;
			while (newhours < 0) newhours += 24;
		}
		
		newseconds -= s;
		
		while (newseconds < 0) {
			newminutes--;
			newseconds += 60;
			
			while (newminutes <= 0) {
				newminutes += 60;
				newhours--;
				while (newhours < 0) newhours += 24;
			}
		}

		return create(newhours, newminutes, newseconds);
	}
	
	public CCTime getAddHour(int h) {
		if (h < 0) {
			return getSubHour(-h);
		}
		
		return getAdd(h, 0, 0);
	}
	
	public CCTime getSubHour(int h) {
		if (h < 0) {
			return getAddHour(-h);
		}
		
		return getSub(h, 0, 0);
	}
	
	public CCTime getAddMinute(int m) {
		if (m < 0) {
			return getSubMinute(-m);
		}
		
		return getAdd(0, m, 0);
	}
	
	public CCTime getSubMinute(int m) {
		if (m < 0) {
			return getAddMinute(-m);
		}
		
		return getSub(0, m, 0);
	}
	
	public CCTime getAddSecond(int s) {
		if (s < 0) {
			return getSubSecond(-s);
		}
		
		return getAdd(0, 0, s);
	}
	
	public CCTime getSubSecond(int s) {
		if (s < 0) {
			return getAddSecond(-s);
		}
		
		return getSub(0, 0, s);
	}

	public int getMinuteOfDay() {
		return getHours()*60 + getMinutes();
	}

	@SuppressWarnings("nls")
	@Override
	public String resolveStringSpecifier(char c, int count) {
		if (c == 'H')
			return String.format("%0" + count + "d", getHours());
		
		if (c == 'm')
			return String.format("%0" + count + "d", getMinutes());
		
		if (c == 's')
			return String.format("%0" + count + "d", getSeconds());
		
		if (c == 'h')
			return String.format("%0" + count + "d", getAMPMHours());
		
		if (c == 't')
			return getAMPMString();
		
		return null;
	}

	@Override
	public HashSet<Character> getAllStringSpecifier() {
		if (stringSpecifier == null) stringSpecifier = new HashSet<>(Arrays.asList('H', 'm', 's', 'h', 't'));
		
		return stringSpecifier;
	}

	@Override
	public Object createFromParsedData(Map<Character, Integer> values) {
		int th = values.get('H');
		int tm = values.get('m');
		int ts = values.get('s');
		
		if (th == UNSPECIFIED.hour && tm == UNSPECIFIED.min && ts == UNSPECIFIED.sec) return UNSPECIFIED;

		if (th == UNSPECIFIED.hour) th = 0;
		if (tm == UNSPECIFIED.hour) tm = 0;
		if (ts == UNSPECIFIED.hour) ts = 0;
				
		return create(th, tm, ts);
	}

	@Override
	public Map<Character, Integer> getSpecDefaults() {
		Map<Character, Integer> d = new Hashtable<>();
		d.put('H', UNSPECIFIED.hour);
		d.put('m', UNSPECIFIED.min);
		d.put('s', UNSPECIFIED.sec);
		return d;
	}

	public static CCTime getMidnight() {
		return MIDNIGHT;
	}

	public static CCTime getUnspecified() {
		return UNSPECIFIED;
	}

	public boolean isMidnight() {
		return hour == 0 && min == 0 && sec == 0;
	}

	public boolean isUnspecifiedTime() {
		return hour == 99 && min == 99 && sec == 99;
	}

	public boolean isValidTime() {
		if (isUnspecifiedTime()) return true;
		
		if (hour < 0 || hour >= 24) return false;
		if (min  < 0 || min  >= 60) return false;
		if (sec  < 0 || sec  >= 60) return false;
		
		return true;
	}
	
	@Override
	public String toString() {
		return "<CCTime>:" + toStringSQL(); //$NON-NLS-1$
	}

	// return seconds([other] - [this])
	public int getSecondDifferenceTo(CCTime other) {
		if (this.isUnspecifiedTime() || !this.isValidTime() || other.isUnspecifiedTime() || !other.isValidTime()) return 0;

		return other.getTotalSeconds() - getTotalSeconds();
	}
}