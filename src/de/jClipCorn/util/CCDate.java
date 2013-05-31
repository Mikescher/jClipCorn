package de.jClipCorn.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.jClipCorn.gui.localization.LocaleBundle;

public class CCDate {
	public final static String STRINGREP_SIMPLE 	= "DD.MM.YYYY"; //$NON-NLS-1$
	public final static String STRINGREP_LOCAL 		= "DD.N.YYYY"; //$NON-NLS-1$
	public final static String STRINGREP_EXTENDED 	= LocaleBundle.getString("CCDate.STRINGREP_EXTENDED"); //$NON-NLS-1$
	public final static String STRINGREP_SQL 		= "YYYY-MM-DD"; //$NON-NLS-1$
	
	public final static long MILLISECONDS_PER_DAY = 86400000;
	
	public static final int YEAR_MIN = 1900; 
	
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
	
	private int day = 1;
	private int month = 1;
	private int year = YEAR_MIN;
	
	public CCDate() {
		Calendar c = Calendar.getInstance();
		set(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));
	}
	
	public CCDate(int d, int m, int y) {
		set(d, m, y);
	}
	
	public CCDate(CCDate d) {
		set(d.getDay(), d.getMonth(), d.getYear());
	}
	
	public static CCDate getNewMinimumDate() {
		return new CCDate(1, 1, YEAR_MIN);
	}
	
	public static CCDate getNewMaximumDate() { //Not really Maximum - just extreme high
		return new CCDate(31, 12, 9999);
	}

	public CCDate(Date d) {
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		set(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
	}

	public String getMonthName() {
		return MONTHNAMES[month];
	}
	
	public int getDaysOfMonth() {
		return MONTHLIMITS[ (isLeapYear()) ? (1) : (0) ][ month ];
	}
	
	public boolean isLeapYear() {
		return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0);
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
	
	/**
	 * @param rawData parseable DATA
	 * @param fmt Format of rawData 
	 * eg "D/M/Y"
	 * D	=> DAY
	 * M	=> MONTH
	 * Y	=> YEAR
	 * @return
	 */
	public boolean parse(String rawData, String fmt) {
		char c;
		int rp = 0;
		int td = day;
		int tm = month;
		int ty = year;
		
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
					return false;
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
					return false;
				}
			}
		}
		
		return set(td, tm, ty);
	}
	
	public boolean setDay(int d) {
		if (d > 0 && d <= getDaysOfMonth()) {
			day = d;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean setMonth(int m) {
		if (m > 0 && m <= 12) {
			month = m;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean setYear(int y) {
		if (y >= YEAR_MIN) {
			year = y;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean set(int d, int m, int y) {
		boolean succ = true;
		succ &= setYear(y);
		succ &= setMonth(m);
		succ &= setDay(d);
		return succ;
	}
	
	public boolean set(CCDate d) {
		return set(d.getDay(), d.getMonth(), d.getYear());
	}
	
	public void add(int d, int m, int y) {
		addYear(y);
		addMonth(m);
		addDay(d);
	}
	
	public void sub(int d, int m, int y) {
		subYear(y);
		subMonth(m);
		subDay(d);
	}
	
	public void addDay(int d) {
		if (d < 0) {
			subDay(-d);
			return;
		}
		
		day += d;
		
		while (day > getDaysOfMonth()) {
			day -= getDaysOfMonth();
			addMonth(1);
		}
	}
	
	public void subDay(int d) {
		if (d < 0) {
			addDay(-d);
			return;
		}
		
		day -= d;
		
		while (day <= 0) {
			subMonth(1);
			day += getDaysOfMonth();
		}
	}
	
	public void addMonth(int m) {
		if (m < 0) {
			subMonth(-m);
			return;
		}
		
		month += m;
		while (month > 12) {
			addYear(1);
			month -= 12;
		}
	}
	
	public void subMonth(int m) {
		if (m < 0) {
			addMonth(-m);
			return;
		}
		
		month -= m;
		
		while(month < 1) {
			month += 12;
			subYear(1);
		}
	}
	
	public void addYear(int y) {
		if (y < 0) {
			subYear(-y);
			return;
		}
		
		year += y;
	}
	
	public void subYear(int y) {
		if (y < 0) {
			addYear(-y);
			return;
		}
		
		year -= y;
		if (year < YEAR_MIN) {
			year = YEAR_MIN;
		}
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
		
		CCDate temp = new CCDate(this);
		int c = 0;
		
		while (temp.isLessThan(other)) {
			temp.addDay(1);
			c++;
		}
		
		while (temp.isGreaterThan(other)) {
			temp.subDay(1);
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
		
		CCDate tdate = new CCDate(1, 1, 1900);
		succ &= tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 1900;
		
		tdate.addDay(1);
		succ &= tdate.getDay() == 2 && tdate.getMonth() == 1 && tdate.getYear() == 1900;
		
		tdate.subDay(1);
		succ &= tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 1900;
		
		tdate.addMonth(1);
		succ &= tdate.getDay() == 1 && tdate.getMonth() == 2 && tdate.getYear() == 1900;
		
		tdate.subMonth(1);
		succ &= tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 1900;
		
		tdate.addYear(1);
		succ &= tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 1901;
		
		tdate.subYear(1);
		succ &= tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 1900;
		
		tdate.addDay(365);
		succ &= tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 1901;
		
		tdate.addDay(150);
		succ &= tdate.getDay() == 31 && tdate.getMonth() == 5 && tdate.getYear() == 1901;
		succ &= tdate.getWeekdayInt() == 5;
		
		tdate.addDay(1096);
		succ &= tdate.getDay() == 31 && tdate.getMonth() == 5 && tdate.getYear() == 1904;
		succ &= tdate.isLeapYear();
		
		tdate.add(215, 12, 94);
		succ &= tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 2000;
		
		tdate.setDay(2);
		succ &= tdate.getDay() == 2 && tdate.getMonth() == 1 && tdate.getYear() == 2000;
		succ &= tdate.getWeekdayInt() == 7;
		
		tdate.addDay(1);
		succ &= tdate.getDay() == 3 && tdate.getMonth() == 1 && tdate.getYear() == 2000;
		succ &= tdate.getWeekdayInt() == 1;
		succ &= tdate.isLeapYear();
		
		tdate.addMonth(12);
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

	public CCDate copy() {
		return new CCDate(this);
	}
	
	public static CCDate getMinDate(List<CCDate> datelist) {
		CCDate min = getNewMaximumDate();
		
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
		
		CCDate result = new CCDate(min);
		result.addDay(sum);
		
		return result;
	}

	public void reset() {
		set(1, 1, YEAR_MIN);
	}
	
	public long asMilliseconds() {
		return new CCDate(1, 1, 1970).getDayDifferenceTo(this) * MILLISECONDS_PER_DAY;
	}
}
