package de.jClipCorn.util.datetime;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.exceptions.DateFormatException;
import de.jClipCorn.util.parser.StringSpecParser;
import de.jClipCorn.util.parser.StringSpecSupplier;

public final class CCDate implements Comparable<CCDate>, StringSpecSupplier {
	public static CCDate STATIC_SUPPLIER = new CCDate(6, 8, 1991);
	
	public final static String STRINGREP_SIMPLE 	= "dd.MM.yyyy"; //$NON-NLS-1$
	public final static String STRINGREP_SIMPLESHORT = "dd.MM.yy"; //$NON-NLS-1$
	public final static String STRINGREP_LOCAL 		= "dd.MMMM.yyyy"; //$NON-NLS-1$
	public final static String STRINGREP_EXTENDED 	= LocaleBundle.getString("CCDate.STRINGREP_EXTENDED"); //$NON-NLS-1$
	public final static String STRINGREP_SQL 		= "yyyy-MM-dd"; //$NON-NLS-1$
	
	public final static long MILLISECONDS_PER_DAY = 86400000;
	
	public static final int YEAR_MIN = 1900; 
	public static final int YEAR_MAX = 9999; 
	
	private static final CCDate DATE_MIN = new CCDate(1, 1, YEAR_MIN);
	private static final CCDate DATE_MAX = new CCDate(31, 12, YEAR_MAX);
	public  static final String MIN_SQL = DATE_MIN.getSQLStringRepresentation();
	
	private static HashSet<Character> stringSpecifier = null; // { 'y', 'M', 'd' }
	
	private static final String[] MONTHNAMES = {
		LocaleBundle.getString("CCDate.Month0"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Month1"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Month2"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Month3"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Month4"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Month5"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Month6"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Month7"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Month8"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Month9"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Month10"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Month11"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Month12") //$NON-NLS-1$
	};
	
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

	private static final int[][] MONTHLIMITS = {{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}, {0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}};
	private static final int[] MONTHVALUES = {0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4};
	
	private final int day;
	private final int month;
	private final int year;
	
	private CCDate(int d, int m, int y) {
		day = d;
		month = m;
		year = y;
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
		if (sqlRep.length() != 10) throw new DateFormatException(sqlRep);
		
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
		
		return new CCDate(d, m, y);
	}
	
	public static CCDate getMinimumDate() {
		return DATE_MIN;
	}
	
	public static CCDate getMaximumDate() { //Not really Maximum - just extreme high
		return DATE_MAX;
	}

	public String getMonthName() {
		return MONTHNAMES[month];
	}
	
	public static int getDaysOfMonth(int m, int y) {
		return MONTHLIMITS[ (isLeapYear(y)) ? (1) : (0) ][ m ];
	}
	
	public int getDaysOfMonth() {
		return getDaysOfMonth(month, year);
	}
	
	public static boolean isLeapYear(int y) {
		return ((y % 4 == 0 && y % 100 != 0) || y % 400 == 0);
	}
	
	public boolean isLeapYear() {
		return isLeapYear(year);
	}
	
	public int getWeekdayInt() {
	    int wkd = (((year - ((month<3)?1:0)) + (year - ((month<3)?1:0))/4 - (year - ((month<3)?1:0))/100 + (year - ((month<3)?1:0))/400 + MONTHVALUES[month - 1] + day) % 7);
	    
	    return (wkd < 1) ? (wkd+7) : (wkd);
	}
	
	public String getWeekday() {
		return WEEKDAYS[getWeekdayInt()];
	}
	
	public CCWeekday getWeekdayEnum() {
		return CCWeekday.find(getWeekdayInt());
	}
	
	public String getStringRepresentation(String fmt) {
		return StringSpecParser.build(fmt, this);
	}
	
	@SuppressWarnings("nls")
	@Override
	public String resolveStringSpecifier(char c, int count) {
		if (c == 'y') 
			return String.format("%0" + count + "d", year);
		
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
	
	public String getSimpleStringRepresentation() {
		return getStringRepresentation(STRINGREP_SIMPLE);
	}
	
	public String getLocalStringRepresentation() {
		return getStringRepresentation(STRINGREP_LOCAL);
	}
	
	public String getExtendedStringRepresentation() {
		return getStringRepresentation(STRINGREP_EXTENDED);
	}
	
	public String getSQLStringRepresentation() {
		return getStringRepresentation(STRINGREP_SQL);
	}
	
	public static boolean testparse(String rawData, String fmt) {
		return parse(rawData, fmt) != null;
	}
	
	public static CCDate parse(String rawData, String fmt) {
		return (CCDate)StringSpecParser.parse(rawData, fmt, CCDate.STATIC_SUPPLIER);
	}
	
	public CCDate getAdd(int d, int m, int y) {
		if (d < 0 || m < 0 || y < 0) {
			return null;
		}
		
		int newday = getDay();
		int newmonth = getMonth();
		int newyear = getYear();
		
		newyear += y;
		
		newmonth += m;
		
		while (newmonth > 12) {
			newmonth -= 12;
			newyear++;
		}
		
		newday += d;
		
		while (newday > getDaysOfMonth(newmonth, newyear)) {
			newday -= getDaysOfMonth(newmonth, newyear);
			newmonth++;
			
			while (newmonth > 12) {
				newmonth -= 12;
				newyear++;
			}
		}	
		
		return create(newday, newmonth, newyear);
	}
	
	public CCDate getSub(int d, int m, int y) {
		if (d < 0 || m < 0 || y < 0) {
			return null;
		}
		
		int newday = getDay();
		int newmonth = getMonth();
		int newyear = getYear();
		
		newyear -= y;
		
		newmonth -= m;
		
		while (newmonth <= 0) {
			newmonth += 12;
			newyear--;
		}
		
		newday -= d;
		
		while (newday <= 0) {
			newmonth--;
			newday += getDaysOfMonth(newmonth, newyear);
			
			
			while (newmonth <= 0) {
				newmonth += 12;
				newyear--;
			}
		}
		
		return create(newday, newmonth, newyear);
	}
	
	public CCDate getAddDay(int d) {
		if (d < 0) {
			return getSubDay(-d);
		}
		
		return getAdd(d, 0, 0);
	}
	
	public CCDate getSubDay(int d) {
		if (d < 0) {
			return getAddDay(-d);
		}
		
		return getSub(d, 0, 0);
	}
	
	public CCDate getAddMonth(int m) {
		if (m < 0) {
			return getSubMonth(-m);
		}
		
		return getAdd(0, m, 0);
	}
	
	public CCDate getSubMonth(int m) {
		if (m < 0) {
			return getAddMonth(-m);
		}
		
		return getSub(0, m, 0);
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
	
	public int getDayDifferenceTo(CCDate other) {
		if (isEquals(other)) {
			return 0;
		}
		
		CCDate temp = create(this);
		int c = 0;
		
		while (temp.isLessThan(other)) {
			temp = temp.getAddDay(1);
			c++;
		}
		
		while (temp.isGreaterThan(other)) {
			temp = temp.getSubDay(1);
			c--;
		}
		
		return c;
	}
	
	@Override
	public String toString() {
		return getSimpleStringRepresentation();
	}
	
	public int compare(CCDate other) {
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
		return (other instanceof CCDate) && ((CCDate)other).getDay() == getDay() && ((CCDate)other).getMonth() == getMonth() && ((CCDate)other).getYear() == getYear();
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
	
	public boolean isEquals(CCDate other) {
		return equals(other);
	}
	
	public boolean isGreaterEqualsThan(CCDate other) {
		return isGreaterThan(other) || isEquals(other);
	}
	
	public boolean isLessEqualsThan(CCDate other) {
		return isLessThan(other) || isEquals(other);
	}

	public static int compare(CCDate o1, CCDate o2) {
		return o1.compare(o2);
	}

	public boolean isMinimum() {
		return 1 == getDay() && 1 == getMonth() && YEAR_MIN == getYear();
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
	public int compareTo(CCDate o) {
		return compare(this, o);
	}
}
