package de.jClipCorn.util.datetime;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.DateTimeFormatException;
import de.jClipCorn.util.parser.StringSpecParser;
import de.jClipCorn.util.parser.StringSpecSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("nls")
public class CCDateTime implements Comparable<CCDateTime>, StringSpecSupplier {
	public static CCDateTime STATIC_SUPPLIER = CCDateTime.create(CCDate.create(1, 1, 2000), CCTime.getUnspecified());

	public static TimeZone UTC = TimeZone.getTimeZone("UTC");

	public final static String UNSPECIFIED_REPRESENTATION = "UNSPECIFIED";
	
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
		if (str.length() == 23) {
			String[] parts = str.split(" ");
			if (parts.length != 2) throw new DateTimeFormatException(str);

			return new CCDateTime(CCDate.createFromSQL(parts[0]), CCTime.createFromSQLWithMilliseconds(parts[1]));
		} else if (str.length() == 19) {
			String[] parts = str.split(" ");
			if (parts.length != 2) throw new DateTimeFormatException(str);

			return new CCDateTime(CCDate.createFromSQL(parts[0]), CCTime.createFromSQL(parts[1]));
		} else if (str.length() == 10) {
			return new CCDateTime(CCDate.createFromSQL(str), CCTime.getUnspecified());
		} else if (str.equals(UNSPECIFIED_REPRESENTATION)) {
			return new CCDateTime(CCDate.getUnspecified(), CCTime.getUnspecified());
		}

		throw new DateTimeFormatException(str);
	}

	public static CCDateTime createFromUTCSQL(String str, TimeZone tzone) throws CCFormatException {
		CCDateTime dt = createFromSQL(str);
		int minutes = (tzone.getOffset(dt.toFileTimestamp(UTC)) / (1000 * 60));
		return dt.getAddMinute(minutes);
	}

	public static CCDateTime create(CCDate d) {
		return new CCDateTime(d, CCTime.getUnspecified());
	}

	public static CCDateTime create(CCDate d, CCTime t) {
		return new CCDateTime(d, t);
	}

	public static CCDateTime createFromFileTimestamp(long millis, TimeZone tzone) { // ts = time in UTC milliseconds from the epoch
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(millis);
		c.setTimeZone(tzone);

		int dy = c.get(Calendar.YEAR);
		int dm = c.get(Calendar.MONTH) + 1;
		int dd = c.get(Calendar.DAY_OF_MONTH);
		int th = c.get(Calendar.HOUR_OF_DAY);
		int tm = c.get(Calendar.MINUTE);
		int ts = c.get(Calendar.SECOND);

		return create(dd, dm, dy, th, tm, ts);
	}

	// return seconds([b] - [a])
	public static int diffInSeconds(CCDateTime a, CCDateTime b) {

		if (a.isUnspecifiedDateTime() || !a.isValidDateTime() || b.isUnspecifiedDateTime() || !b.isValidDateTime()) return 0;

		int days = a.date.getDayDifferenceTo(b.date);
		int secs = a.time.getSecondDifferenceTo(b.time);

		return days * 86400 + secs;
	}

	public static CCDateTime getTodayStart() {
		return new CCDateTime(CCDate.getCurrentDate(), CCTime.getMidnight());
	}

	public static CCDateTime getWeekStart() {
		return new CCDateTime(CCDate.getWeekStart(), CCTime.getMidnight());
	}

	public static CCDateTime getMonthStart() {
		return new CCDateTime(CCDate.getMonthStart(), CCTime.getMidnight());
	}

	public static CCDateTime getYearStart() {
		return new CCDateTime(CCDate.getYearStart(), CCTime.getMidnight());
	}

	// return seconds([other] - [this])
	public int getSecondDifferenceTo(CCDateTime other) {
		return diffInSeconds(this, other);
	}

	public long toFileTimestamp(TimeZone tzone) { // = milliseconds since EPOCH
		Calendar c = Calendar.getInstance();

		c.set(Calendar.YEAR,         date.getYear());
		c.set(Calendar.MONTH,        date.getMonth() - 1);
		c.set(Calendar.DAY_OF_MONTH, date.getDay());
		c.set(Calendar.HOUR_OF_DAY,  time.getHours());
		c.set(Calendar.MINUTE,       time.getMinutes());
		c.set(Calendar.SECOND,       time.getSeconds());
		c.set(Calendar.MILLISECOND,  0);

		c.setTimeZone(tzone);

		return c.getTimeInMillis();
	}

	public static CCDateTime getCurrentDateTime() {
		return new CCDateTime(CCDate.getCurrentDate(), CCTime.getCurrentTime());
	}

	public String getSQLStringRepresentation() {
		if (isUnspecifiedDateTime())
			return UNSPECIFIED_REPRESENTATION;
		else if (time.isUnspecifiedTime())
			return date.toStringSQL();
		else
			return date.toStringSQL() + " " + time.toStringSQL();
	}

	public CCDateTime toUTC(TimeZone tzone) {
		return getSubMinute(tzone.getOffset(toFileTimestamp(UTC)) / (1000 * 60));
	}

	@Override
	public int compareTo(@NotNull CCDateTime o) {
		if (isUnspecifiedDateTime() && o.isUnspecifiedDateTime()) return 0;
		if (isUnspecifiedDateTime()) return -1;

		int c = date.compareTo(o.date);
		if (c != 0) return c;
		
		if (time.isUnspecifiedTime() || o.time.isUnspecifiedTime()) return 0;
		
		return time.compareTo(o.time);
	}

	public static int compare(CCDateTime o1, CCDateTime o2) {
		if (o1.equals(o2)) {
			return 0;
		} else if (o1.isGreaterThan(o2)) {
			return 1;
		} else {
			return -1;
		}
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

	public String toStringUINormal() {
		if (isUnspecifiedDateTime())
			return LocaleBundle.getString("CCDate.STRINGREP_UNSPEC");
		else if (time.isUnspecifiedTime())
			return InternationalDateTimeFormatHelper.fmtUIDateOnly(this);
		else
			return InternationalDateTimeFormatHelper.fmtUINormal(this);
	}

	public String toStringISO() {
		if (isUnspecifiedDateTime())
			return LocaleBundle.getString("CCDate.STRINGREP_UNSPEC");
		else if (time.isUnspecifiedTime())
			return date.getStringRepresentation("yyyy-MM-dd");
		else
			return getStringRepresentation("yyyy-MM-dd HH:mm:ss");
	}

	public String toStringFilesystem() {
		if (isUnspecifiedDateTime())
			return "Unspecified";
		else if (time.isUnspecifiedTime())
			return date.getStringRepresentation("yyyy-MM-dd");
		else
			return getStringRepresentation("yyyy-MM-dd_HH-mm-ss");
	}

	public String toStringSQL() {
		return getSQLStringRepresentation();
	}
	
	public String toStringUIShort() {
		if (isUnspecifiedDateTime())
			return LocaleBundle.getString("CCDate.STRINGREP_UNSPEC");
		else if (time.isUnspecifiedTime()) 
			return InternationalDateTimeFormatHelper.fmtUIDateOnly(this);
		else
			return InternationalDateTimeFormatHelper.fmtUIShort(this);
	}
	
	public String toStringUIDateOnly() {
		if (isUnspecifiedDateTime())
			return LocaleBundle.getString("CCDate.STRINGREP_UNSPEC");
		else
			return InternationalDateTimeFormatHelper.fmtUIDateOnly(this);
	}
	
	public String toStringInput() {
		return InternationalDateTimeFormatHelper.fmtInput(this);
	}
	
	public static CCDateTime parseInputOrNull(String rawData) {
		if (rawData.equalsIgnoreCase(CCDateTime.UNSPECIFIED_REPRESENTATION) || rawData.equalsIgnoreCase(LocaleBundle.getString("CCDate.STRINGREP_UNSPEC")))
			return CCDateTime.getUnspecified();
		
		for (String fmt : InternationalDateTimeFormatHelper.DESERIALIZE_DATETIME_INPUT) {
			CCDateTime d = CCDateTime.parseOrDefault(rawData, fmt, null);
			if (d != null) return d;
		}
		
		return null;
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

	public CCDateTime getAddDay(int v) {
		return getAdd(+v, 0, 0, 0, 0, 0);
	}

	public CCDateTime getSubDay(int v) {
		return getAdd(-v, 0, 0, 0, 0, 0);
	}

	public CCDateTime getAddMonth(int v) {
		return getAdd(0, +v, 0, 0, 0, 0);
	}

	public CCDateTime getSubMonth(int v) {
		return getAdd(0, -v, 0, 0, 0, 0);
	}

	public CCDateTime getAddYear(int v) {
		return getAdd(0, 0, +v, 0, 0, 0);
	}

	public CCDateTime getSubYear(int v) {
		return getAdd(0, 0, -v, 0, 0, 0);
	}

	public CCDateTime getAddHour(int v) {
		return getAdd(0, 0, 0, +v, 0, 0);
	}

	public CCDateTime getSubHour(int v) {
		return getAdd(0, 0, 0, -v, 0, 0);
	}

	public CCDateTime getAddMinute(int v) {
		return getAdd(0, 0, 0, 0, +v, 0);
	}

	public CCDateTime getSubMinute(int v) {
		return getAdd(0, 0, 0, 0, -v, 0);
	}

	public CCDateTime getAddSecond(int v) {
		return getAdd(0, 0, 0, 0, 0, +v);
	}

	public CCDateTime getSubSecond(int v) {
		return getAdd(0, 0, 0, 0, 0, -v);
	}

	public CCDateTime getAdd(int dd, int dm, int dy, int th, int tm, int ts) {

		if (isUnspecifiedDateTime()) return this;

		var r = CCChronos.addFull(
				this.date.getYear(), this.date.getMonth(), this.date.getDay(),
				this.time.getHours(), this.time.getMinutes(), this.time.getSeconds(),
				dy, dm, dd,
				th, tm, ts);

		return create(r.Day, r.Month, r.Year, r.Hour, r.Minute, r.Second);
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

			stringSpecifier.addAll(stringSpecifierDate);

			stringSpecifier.addAll(stringSpecifierTime);
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

		d.putAll(CCDate.STATIC_SUPPLIER.getSpecDefaults());

		d.putAll(CCTime.STATIC_SUPPLIER.getSpecDefaults());

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
	
	/**
	 * This method treats undefined datetimes as unequal to other undefined datetimes (SQL style)
	 * also a datetime with an undefinned time component is equal to all other datetimes with the same date component
	 */
	public boolean isEqual(CCDateTime other) {
		if (other == null) return false;
		
		if (this.isUnspecifiedDateTime() || other.isUnspecifiedDateTime()) return false;
		
		return compareTo(other) == 0;
	}

	/**
	 * In contrast to isEquals, this method tests if two objects contain really the same data (same date and same time)
	 */
	public boolean isExactEqual(CCDateTime other) {
		if (other == null) return false;
		
		return this.time.isEqual(other.time) && this.date.isEqual(other.date);
	}
	
	public boolean isGreaterEqualsThan(CCDateTime other) {
		return compareTo(other) >= 0;
	}
	
	public boolean isLessEqualsThan(CCDateTime other) {
		return compareTo(other) <= 0;
	}
	
	@Override
	public boolean equals(Object other) {
		return (other instanceof CCDateTime) && isEqual((CCDateTime) other);
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

	public static CCDateTime getUnspecified() {
		return new CCDateTime(CCDate.getUnspecified(), CCTime.getUnspecified());
	}

	public boolean isMinimum() {
		return date.isMinimum() && time.isMidnight();
	}

	public boolean isUnspecifiedDateTime() {
		return date.isUnspecifiedDate();
	}

	public boolean isUnspecifiedOrMinimum() {
		return isUnspecifiedDateTime() || isMinimum();
	}

	public CCDateTime getSpecifyTimeIfNeeded(CCTime fallbacktime) {
		if (time.isUnspecifiedTime()) return new CCDateTime(date, fallbacktime);
		return this;
	}
	
	@Override
	public String toString() {
		return "<CCDateTime>:" + getSQLStringRepresentation();
	}

	public int getHours() {
		return time.getHours();
	}

	public int getMinutes() {
		return time.getMinutes();
	}

	public int getSeconds() {
		return time.getSeconds();
	}

	public int getDay() {
		return date.getDay();
	}

	public int getMonth() {
		return date.getMonth();
	}

	public int getYear() {
		return date.getYear();
	}

	public int getWeekdayInt() {
		return date.getWeekdayInt();
	}

	public boolean isLeapYear() {
		return date.isLeapYear();
	}

	public CCDateTime getSetDay(int d) {
		return create(date.getSetDay(d), time);
	}

	public CCDateTime getSetMonth(int m) {
		return create(date.getSetMonth(m), time);
	}

	public CCDateTime getSetYear(int y) {
		return create(date.getSetYear(y), time);
	}
}
