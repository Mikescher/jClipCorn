package de.jClipCorn.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.jClipCorn.gui.localization.LocaleBundle;

public final class CCDate {
	public final static String STRINGREP_SIMPLE 	= "DD.MM.YYYY"; //$NON-NLS-1$
	public final static String STRINGREP_SIMPLESHORT = "DD.MM.YY"; //$NON-NLS-1$
	public final static String STRINGREP_LOCAL 		= "DD.N.YYYY"; //$NON-NLS-1$
	public final static String STRINGREP_EXTENDED 	= LocaleBundle.getString("CCDate.STRINGREP_EXTENDED"); //$NON-NLS-1$
	public final static String STRINGREP_SQL 		= "YYYY-MM-DD"; //$NON-NLS-1$
	
	public final static long MILLISECONDS_PER_DAY = 86400000;
	
	public static final int YEAR_MIN = 1900; 
	public static final int YEAR_MAX = 9999; 
	
	public static final CCDate DATE_MIN = new CCDate(1, 1, YEAR_MIN);
	public static final CCDate DATE_MAX = new CCDate(31, 12, YEAR_MAX);
	
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
	
	private int getWeekdayInt() {
	    int wkd = (((year - ((month<3)?1:0)) + (year - ((month<3)?1:0))/4 - (year - ((month<3)?1:0))/100 + (year - ((month<3)?1:0))/400 + MONTHVALUES[month - 1] + day) % 7);
	    
	    return (wkd < 1) ? (wkd+7) : (wkd);
	}
	
	public String getWeekday() {
		return WEEKDAYS[getWeekdayInt()];
	}
	
	/**
	 * @param fmt Format STyle
	 * YYYY	=> YEAR
	 * MMM 	=> MONTH (005)
	 * MM  	=> MONTH (05)
	 * M   	=> MONTH (5)
	 * D	=> DAY
	 * N	=> MONTHNAME
	 * W	=> WEEKDAY
	 * @return
	 */
	public String getStringRepresentation(String fmt) {
		StringBuilder repbuilder = new StringBuilder();
		char actualCounter = '-';
		int counter = 0;
		char c;
		
		for (int p = 0;p<fmt.length();p++) {
			c = fmt.charAt(p);
			if (c == 'Y' || c == 'M' || c == 'D' || c == 'N' || c == 'W') {
				if (counter == 0 || actualCounter == c) {
					counter++;
					actualCounter = c;
				} else {
					String tmpformat = ""; //$NON-NLS-1$
					
					if (actualCounter == 'Y') {
						tmpformat = String.format("%0" + counter + "d", year); //$NON-NLS-1$ //$NON-NLS-2$
					} else if (actualCounter == 'M') {
						tmpformat = String.format("%0" + counter + "d", month); //$NON-NLS-1$ //$NON-NLS-2$
					} else if (actualCounter == 'D') {
						tmpformat = String.format("%0" + counter + "d", day); //$NON-NLS-1$ //$NON-NLS-2$
					} else if (actualCounter == 'N') {
						tmpformat = getMonthName();
					} else if (actualCounter == 'W') {
						tmpformat = getWeekday();
					}
					
					repbuilder.append(tmpformat);
					
					counter = 1;
					actualCounter = c;
				}
			} else {
				if (counter > 0) {
					String tmpformat = ""; //$NON-NLS-1$
					
					if (actualCounter == 'Y') {
						tmpformat = String.format("%0" + counter + "d", year); //$NON-NLS-1$ //$NON-NLS-2$
					} else if (actualCounter == 'M') {
						tmpformat = String.format("%0" + counter + "d", month); //$NON-NLS-1$ //$NON-NLS-2$
					} else if (actualCounter == 'D') {
						tmpformat = String.format("%0" + counter + "d", day); //$NON-NLS-1$ //$NON-NLS-2$
					} else if (actualCounter == 'N') {
						tmpformat = getMonthName();
					} else if (actualCounter == 'W') {
						tmpformat = getWeekday();
					}
					
					repbuilder.append(tmpformat);
				}
				repbuilder.append(c);
				counter = 0;
				actualCounter = '-';
			}
		}
		
		if (counter > 0) {
			String tmpformat = ""; //$NON-NLS-1$
			
			if (actualCounter == 'Y') {
				tmpformat = String.format("%0" + counter + "d", year); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (actualCounter == 'M') {
				tmpformat = String.format("%0" + counter + "d", month); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (actualCounter == 'D') {
				tmpformat = String.format("%0" + counter + "d", day); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (actualCounter == 'N') {
				tmpformat = getMonthName();
			} else if (actualCounter == 'W') {
				tmpformat = getWeekday();
			}
			
			repbuilder.append(tmpformat);
		}
		
		return repbuilder.toString();
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
	
	/**
	 * @param rawData parseable DATA
	 * @param fmt Format of rawData 
	 * eg "D/M/Y"
	 * D	=> DAY
	 * M	=> MONTH
	 * Y	=> YEAR
	 * @return
	 */
	public static CCDate parse(String rawData, String fmt) {
		char c;
		int rp = 0;
		int td = 1;
		int tm = 1;
		int ty = YEAR_MIN;
		
		rawData += '\0';
		
		for (int p = 0;p<fmt.length();p++) {
			c = fmt.charAt(p);
			while((p+1)<fmt.length() && fmt.charAt(p+1) == fmt.charAt(p) && (c == 'D' || c == 'M' || c == 'Y')) {
				p++;
			}
			
			if (c == 'D' || c == 'M' || c == 'Y') {
				String drep = ""; //$NON-NLS-1$
				
				if (Character.isDigit(rawData.charAt(rp))) {
					drep += rawData.charAt(rp);
					rp++;
				} else {
					return null;
				}
				
				for (; Character.isDigit(rawData.charAt(rp)) ; rp++) {
					drep += rawData.charAt(rp);
				}
				
				if (c == 'D') {
					td = Integer.parseInt(drep);
				} else if (c == 'M') {
					tm = Integer.parseInt(drep);	
				} else if (c == 'Y') {
					ty = Integer.parseInt(drep);	
				}
			}  else {
				if (rawData.charAt(rp) == c) {
					rp++;
				} else {
					return null;
				}
			}
		}
		
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
		}
		
		return create(td, tm, ty);
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
	
	public static boolean test() {
		boolean succ = true;
		
		CCDate tdate = getMinimumDate();
		succ &= tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 1900;
		
		tdate = tdate.getAddDay(1);
		succ &= tdate.getDay() == 2 && tdate.getMonth() == 1 && tdate.getYear() == 1900;
		
		tdate = tdate.getSubDay(1);
		succ &= tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 1900;
		
		tdate = tdate.getAddMonth(1);
		succ &= tdate.getDay() == 1 && tdate.getMonth() == 2 && tdate.getYear() == 1900;
		
		tdate = tdate.getSubMonth(1);
		succ &= tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 1900;
		
		tdate = tdate.getAddYear(1);
		succ &= tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 1901;
		
		tdate = tdate.getSubYear(1);
		succ &= tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 1900;
		
		tdate = tdate.getAddDay(365);
		succ &= tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 1901;
		
		tdate = tdate.getAddDay(150);
		succ &= tdate.getDay() == 31 && tdate.getMonth() == 5 && tdate.getYear() == 1901;
		succ &= tdate.getWeekdayInt() == 5;
		
		tdate = tdate.getAddDay(1096);
		succ &= tdate.getDay() == 31 && tdate.getMonth() == 5 && tdate.getYear() == 1904;
		succ &= tdate.isLeapYear();
		
		tdate = tdate.getAdd(215, 12, 94);
		succ &= tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 2000;
		
		tdate = tdate.getSetDay(2);
		succ &= tdate.getDay() == 2 && tdate.getMonth() == 1 && tdate.getYear() == 2000;
		succ &= tdate.getWeekdayInt() == 7;
		
		tdate = tdate.getAddDay(1);
		succ &= tdate.getDay() == 3 && tdate.getMonth() == 1 && tdate.getYear() == 2000;
		succ &= tdate.getWeekdayInt() == 1;
		succ &= tdate.isLeapYear();
		
		tdate = tdate.getAddMonth(12);
		succ &= tdate.getDay() == 3 && tdate.getMonth() == 1 && tdate.getYear() == 2001;
		succ &= ! tdate.isLeapYear();
		
		return succ;
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
	
	public boolean isValidDate() {
		if (! (day > 0 && day <= getDaysOfMonth())) {
			return false;
		}
		
		if (! (month > 0 && month <= 12)) {
			return false;
		}
		
		if (! (year >= YEAR_MIN)) {
			return false;
		}
		
		return true;
	}
}
