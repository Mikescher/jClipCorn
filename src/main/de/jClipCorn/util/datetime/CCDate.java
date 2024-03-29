package de.jClipCorn.util.datetime;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.DateFormatException;
import de.jClipCorn.util.parser.StringSpecParser;
import de.jClipCorn.util.parser.StringSpecSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class CCDate implements Comparable<CCDate>, StringSpecSupplier {
	public static CCDate STATIC_SUPPLIER = new CCDate(6, 8, 1991);
	
	public final static long MILLISECONDS_PER_DAY = 86400000;
	
	public static final int YEAR_MIN = 1900; 
	public static final int YEAR_MAX = 9999; 

	public static final int YEAR_UNSPECIFIED  = 99;
	public static final int MONTH_UNSPECIFIED = 99;
	public static final int DAY_UNSPECIFIED   = 99;
	
	private static final CCDate DATE_MIN = new CCDate(1, 1, YEAR_MIN);
	private static final CCDate DATE_MAX = new CCDate(31, 12, YEAR_MAX);
	public  static final String MIN_SQL = DATE_MIN.toStringSQL();
	private static final CCDate UNSPECIFIED = new CCDate(DAY_UNSPECIFIED, MONTH_UNSPECIFIED, YEAR_UNSPECIFIED);
	
	private static HashSet<Character> stringSpecifier = null; // { 'y', 'M', 'd' }

	private static final int[] MONTHVALUES = {0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4};

	private static final String[] WEEKDAYS = {
			LocaleBundle.getString("CCDate.Month0"), //$NON-NLS-1$
			LocaleBundle.getString("CCDate.Day1"), //$NON-NLS-1$
			LocaleBundle.getString("CCDate.Day2"), //$NON-NLS-1$
			LocaleBundle.getString("CCDate.Day3"), //$NON-NLS-1$
			LocaleBundle.getString("CCDate.Day4"), //$NON-NLS-1$
			LocaleBundle.getString("CCDate.Day5"), //$NON-NLS-1$
			LocaleBundle.getString("CCDate.Day6"), //$NON-NLS-1$
			LocaleBundle.getString("CCDate.Day7") //$NON-NLS-1$
	};

	private final int day;   // 1..31
	private final int month; // 1..12
	private final int year;  // 1900..9999
	
	private CCDate(int d, int m, int y) {
		day   = d;
		month = m;
		year  = y;
	}
	
	public static CCDate getCurrentDate() {
		Calendar c = Calendar.getInstance();
		return new CCDate(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));
	}
	
	public static CCDate create(CCDate d) {
		return new CCDate(d.getDay(), d.getMonth(), d.getYear());
	}
	
	public static CCDate create(int d, int m, int y) {
		return new CCDate(d, m, y);
	}
	
	public static CCDate create(Date d) {
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		return new CCDate(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
	}
	
	public static CCDate createFromSQL(String sqlRep) throws DateFormatException {
		// [yyyy-MM-dd]  or  [yyyy-MM-dd HH:mm]  or  [yyyy-MM-dd HH:mm:ss]  
		if (sqlRep.length() != 10 && sqlRep.length() != 16 && sqlRep.length() != 19) throw new DateFormatException(sqlRep);
		
		int y = (sqlRep.charAt(0)-'0') * 1000 + // Y
				(sqlRep.charAt(1)-'0') * 100 +  // Y
				(sqlRep.charAt(2)-'0') * 10 +   // Y
				(sqlRep.charAt(3)-'0') * 1;     // Y
		                                        // -
		int m = (sqlRep.charAt(5)-'0') * 10 +   // M
				(sqlRep.charAt(6)-'0') * 1;     // M
                                                // -
		int d = (sqlRep.charAt(8)-'0') * 10 +   // D
				(sqlRep.charAt(9)-'0') * 1;     // D

		if (sqlRep.charAt(4) != '-') throw new DateFormatException(sqlRep);
		if (sqlRep.charAt(7) != '-') throw new DateFormatException(sqlRep);
		if (sqlRep.length() > 10 && sqlRep.charAt(10) != ' ') throw new DateFormatException(sqlRep);
		if (sqlRep.length() > 10 && sqlRep.charAt(13) != ':') throw new DateFormatException(sqlRep);
		if (sqlRep.length() > 16 && sqlRep.charAt(16) != ':') throw new DateFormatException(sqlRep);
		
		return new CCDate(d, m, y);
	}

	public static CCDate createRandom(Random r) {
		return createRandom(r, YEAR_MIN, YEAR_MAX);
	}

	public static CCDate createRandom(Random r, int minyear, int maxyear) {
		int y = r.nextInt(maxyear-minyear)+minyear;
		int m = r.nextInt(12)+1;
		int d = r.nextInt(CCChronos.getDaysOfMonth(m, y)) + 1;
		return create(d, m, y);
	}
	
	public static CCDate getMinimumDate() {
		return DATE_MIN;
	}
	
	public static CCDate getMaximumDate() { //Not really Maximum - just extreme high
		return DATE_MAX;
	}

	public static CCDate getUnspecified() {
		return UNSPECIFIED;
	}

	public static CCDate getWeekStart() {
		CCDate d = getCurrentDate();
		return d.getSubDay(d.getWeekdayInt()-1);
	}

	public static CCDate getMonthStart() {
		Calendar c = Calendar.getInstance();
		return new CCDate(1, c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));
	}

	public static CCDate getYearStart() {
		Calendar c = Calendar.getInstance();
		return new CCDate(1, 1, c.get(Calendar.YEAR));
	}

	public String getMonthName() {
		return CCChronos.getMonthName(month);
	}

	public int getDaysOfMonth() {
		return CCChronos.getDaysOfMonth(month, year);
	}

	public boolean isLeapYear() {
		return CCChronos.isLeapYear(year);
	}
	
	public int getWeekdayInt() {
	    int wkd = (((year - ((month<3)?1:0)) + (year - ((month<3)?1:0))/4 - (year - ((month<3)?1:0))/100 + (year - ((month<3)?1:0))/400 + MONTHVALUES[month - 1] + day) % 7);
	    
	    return (wkd < 1) ? (wkd+7) : (wkd);
	}
	
	public String getWeekday() {
		return WEEKDAYS[getWeekdayInt()];
	}
	
	public CCWeekday getWeekdayEnum() {
		return CCWeekday.getWrapper().findOrNull(getWeekdayInt());
	}
	
	public String getStringRepresentation(String fmt) {
		return StringSpecParser.build(fmt, this);
	}
	
	@SuppressWarnings("nls")
	@Override
	public String resolveStringSpecifier(char c, int count) {
		if (c == 'y')
		{
			if (count == 1) return Integer.toString(year);
			if (count == 2) return String.format("%02d", (year % 100));

			return String.format("%0" + count + "d", year);
		}
		
		if (c == 'M') {
			if (count == 4) return getMonthName();

			return String.format("%0" + count + "d", month);
		}
		
		if (c == 'd') {
			if (count == 4) return getWeekday();

			return String.format("%0" + count + "d", day);
		}
		
		return null;
	}

	@Override
	public Map<Character, Integer> getSpecDefaults() {
		Map<Character, Integer> d = new Hashtable<>();
		d.put('y', -1);
		d.put('M', 1);
		d.put('d', 1);
		return d;
	}

	@Override
	public Object createFromParsedData(Map<Character, Integer> values) {
		int td = values.get('d');
		int tm = values.get('M');
		int ty = values.get('y');
		
		if (ty > 0 && ty < 100) { // Jahr with only 2 Letters e.g. '94
			int currYear = getCurrentDate().getYear();

			int currBase = ((currYear / 100) * 100);

			int distanceA = Math.abs(currYear - ((currBase - 100) + ty));
			int distanceB = Math.abs(currYear - ((currBase) + ty));
			int distanceC = Math.abs(currYear - ((currBase + 100) + ty));
			
			if (distanceC < distanceB && distanceC < distanceA) {
				ty = (currBase + 100) + ty;
			} else if (distanceB < distanceC && distanceB < distanceA) {
				ty = currBase + ty;
			} else {
				ty = (currBase - 100) + ty;
			}
		} else if (ty == -1) { // No Year set
			int cy = getCurrentDate().getYear();
			
			int distanceA = Math.abs(getCurrentDate().getDayDifferenceTo(create(td, tm, cy-1)));
			int distanceB = Math.abs(getCurrentDate().getDayDifferenceTo(create(td, tm, cy)));
			int distanceC = Math.abs(getCurrentDate().getDayDifferenceTo(create(td, tm, cy+1)));
			
			if (distanceC < distanceB && distanceC < distanceA) {
				ty = cy+1;
			} else if (distanceB < distanceC && distanceB < distanceA) {
				ty = cy;
			} else {
				ty = cy+1;
			}
		}
		
		return CCDate.create(td, tm, ty);
	}

	@Override
	public HashSet<Character> getAllStringSpecifier() {
		if (stringSpecifier == null) stringSpecifier = new HashSet<>(Arrays.asList('y', 'M', 'd'));
		
		return stringSpecifier;
	}
	
	public String toStringSQL() { 
		return getStringRepresentation(InternationalDateTimeFormatHelper.DATE_SQL);
	}

	public String toStringSerialize() { 
		return getStringRepresentation(InternationalDateTimeFormatHelper.SERIALIZE_DATE);
	}
	
	public String toStringInput() { 
		return InternationalDateTimeFormatHelper.fmtInput(this);
	}
	
	public String toStringUIShort() { 
		return InternationalDateTimeFormatHelper.fmtUIShort(this);
	}
	
	public String toStringUINormal() { 
		return InternationalDateTimeFormatHelper.fmtUINormal(this);
	}
	
	public String toStringUIVerbose() { 
		return InternationalDateTimeFormatHelper.fmtUIVerbose(this);
	}

	public static CCDate deserialize(String rawData) throws CCFormatException {
		return parse(rawData, InternationalDateTimeFormatHelper.SERIALIZE_DATE);
	}

	public static CCDate deserializeSQL(String rawData) throws CCFormatException {
		return parse(rawData, InternationalDateTimeFormatHelper.DATE_SQL);
	}

	public static CCDate deserializeOrDefault(String rawData, CCDate defaultValue) {
		return parseOrDefault(rawData, InternationalDateTimeFormatHelper.SERIALIZE_DATE, defaultValue);
	}
	
	public static CCDate parseInputOrNull(String rawData) {
		for (String fmt : InternationalDateTimeFormatHelper.DESERIALIZE_DATE_INPUT) {
			CCDate d = CCDate.parseOrDefault(rawData, fmt, null);
			if (d != null) return d;
		}
		
		return null;
	}
	
	public static boolean testparse(String rawData, String fmt) {
		return StringSpecParser.testparse(rawData, fmt, CCDate.STATIC_SUPPLIER);
	}
	
	public static CCDate parse(String rawData, String fmt) throws CCFormatException {
		return (CCDate)StringSpecParser.parse(rawData, fmt, CCDate.STATIC_SUPPLIER);
	}
	
	public static CCDate parseOrDefault(String rawData, String fmt, CCDate defaultValue) {
		return (CCDate)StringSpecParser.parseOrDefault(rawData, fmt, CCDate.STATIC_SUPPLIER, defaultValue);
	}
	
	public CCDate getAdd(int d, int m, int y) {
		if (isUnspecifiedDate()) return this;
		
		var r = CCChronos.addUpperHalf(this.year, this.month, this.day, y, m, d);

		return create(r.Day, r.Month, r.Year);
	}
	
	public CCDate getSub(int d, int m, int y) {
		return getAdd(-d, -m, -y);
	}
	
	public CCDate getAddDay(int d) {
		return getAdd(d, 0, 0);
	}
	
	public CCDate getSubDay(int d) {
		return getAdd(-d, 0, 0);
	}
	
	public CCDate getAddMonth(int m) {
		return getAdd(0, m, 0);
	}
	
	public CCDate getSubMonth(int m) {
		return getAdd(0, -m, 0);
	}
	
	public CCDate getAddYear(int y) {
		return getAdd(0, 0, y);
	}
	
	public CCDate getSubYear(int y) {
		return getSub(0, 0, y);
	}
	
	public CCDate getSetDay(int d) {
		return create(d, getMonth(), getYear());
	}
	
	public CCDate getSetMonth(int m) {
		return create(getDay(), m, getYear());
	}
	
	public CCDate getSetYear(int y) {
		return create(getDay(), getMonth(), y);
	}
	
	public int getDay() {
		return day;
	}
	
	public int getMonth() {
		return month;
	}
	
	public int getYear() {
		return year;
	}

	// return days([other] - [this])
	public int getDayDifferenceTo(CCDate other) {
		if (isEqual(other)) return 0;

		if (this.isUnspecifiedDate() || !this.isValidDate() || other.isUnspecifiedDate() || !other.isValidDate()) return 0;

		return CCChronos.subtractUpperHalf(other.year, other.month, other.day, this.year, this.month, this.day);
	}

	@Override
	public String toString() {
		return "<CCDate>:" + toStringSQL(); //$NON-NLS-1$
	}
	
	@Override
	public boolean equals(Object other) {
		return (other instanceof CCDate) && isEqual((CCDate)other);
	}

	public boolean isEqual(CCDate other) {
		if (other == null) return false;
		
		if (this.isUnspecifiedDate() && other.isUnspecifiedDate()) return true;
		
		return getDay() == other.getDay() &&
				getMonth() == other.getMonth() &&
				getYear() == other.getYear();
	}

	public static boolean equals(CCDate a, CCDate b) {
		if (a != null) return a.isEqual(b);
		return b == null;
	}

	@Override
	public int hashCode() {
		return (((year << 4) + month) << 5) + day; // Yep - thats right
	}
	
	public boolean isGreaterThan(CCDate other) {
		if (getYear() < other.getYear()) {
			return false;
		} else if (getYear() > other.getYear()) {
			return true;
		} else {
			if (getMonth() < other.getMonth()) {
				return false;
			} else if (getMonth() > other.getMonth()) {
				return true;
			} else {
				if (getDay() < other.getDay()) {
					return false;
				} else if (getDay() > other.getDay()) {
					return true;
				} else {
					return false; // THEY ARE F*** EQUAL
				}
			}
		}
	}
	
	public boolean isLessThan(CCDate other) {
		if (getYear() < other.getYear()) {
			return true;
		} else if (getYear() > other.getYear()) {
			return false;
		} else {
			if (getMonth() < other.getMonth()) {
				return true;
			} else if (getMonth() > other.getMonth()) {
				return false;
			} else {
				if (getDay() < other.getDay()) {
					return true;
				} else if (getDay() > other.getDay()) {
					return false;
				} else {
					return false; // THEY ARE F*** EQUAL
				}
			}
		}
	}
	
	public boolean isGreaterEqualsThan(CCDate other) {
		return isGreaterThan(other) || isEqual(other);
	}
	
	public boolean isLessEqualsThan(CCDate other) {
		return isLessThan(other) || isEqual(other);
	}

	public static int compare(CCDate o1, CCDate o2) {
		if (o1.equals(o2)) {
			return 0;
		} else if (o1.isGreaterThan(o2)) {
			return 1;
		} else {
			return -1;
		}
	}

	public boolean isMinimum() {
		return 1 == getDay() && 1 == getMonth() && YEAR_MIN == getYear();
	}

	public boolean isUnspecifiedDate() {
		return 99 == getDay() && 99 == getMonth() && 99 == getYear();
	}
	
	public static CCDate getMinDate(List<CCDate> datelist) {
		CCDate min = getMaximumDate();
		
		for (int i = 0; i < datelist.size(); i++) {
			if (datelist.get(i).isLessThan(min)) {
				min = datelist.get(i);
			}
		}
		
		return min;
	}
	
	public static CCDate getAverageDate(List<CCDate> datelist) {
		if (datelist.isEmpty()) return getMinimumDate();
		
		CCDate min = getMinDate(datelist);
		int sum = 0;
		
		for (int i = 0; i < datelist.size(); i++) {
			sum += min.getDayDifferenceTo(datelist.get(i));
		}
		
		sum /= datelist.size();
		
		CCDate result = create(min);
		result = result.getAddDay(sum);
		
		return result;
	}
	
	public static CCDate min(CCDate a, CCDate b) {
		if (a.isGreaterThan(b)) {
			return b;
		} else {
			return a;
		}
	}
	
	public static CCDate max(CCDate a, CCDate b) {
		if (a.isGreaterThan(b)) {
			return a;
		} else {
			return b;
		}
	}
	
	public long asMilliseconds() {
		return new CCDate(1, 1, 1970).getDayDifferenceTo(this) * MILLISECONDS_PER_DAY;
	}
	
	/// This checks _not_ for unset dates (1.1.1900)
	/// This checks only for really invalid dates (eg 40.13.1800)
	public boolean isValidDate() {
		if (isUnspecifiedDate()) return true;
		
		if (! (year >= YEAR_MIN)) {
			return false;
		}
		
		if (! (month > 0 && month <= 12)) {
			return false;
		}
		
		if (! (day > 0 && day <= getDaysOfMonth())) {
			return false;
		}
		
		return true;
	}

	@Override
	public int compareTo(@NotNull CCDate o) {
		return compare(this, o);
	}

	public boolean isFirstOfMonth()
	{
		return !isMinimum() && !isUnspecifiedDate() && isValidDate() && getDay()==1;
	}
}
