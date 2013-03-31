package de.jClipCorn.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.jClipCorn.gui.localization.LocaleBundle;

public class CCDate {
	public final static String STRINGREP_SIMPLE 	= "DD.MM.YYYY"; //$NON-NLS-1$
	public final static String STRINGREP_LOCAL 		= "DD.N.YYYY"; //$NON-NLS-1$
	public final static String STRINGREP_EXTENDED 	= LocaleBundle.getString("CCDate.STRINGREP_EXTENDED"); //$NON-NLS-1$
	public final static String STRINGREP_SQL 		= "YYYY-MM-DD"; //$NON-NLS-1$
	
	public static final int YEAR_MIN = 1900; 
	
	private static final String[] monNames = {
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
	
	private static final String[] weekDays = {		
		LocaleBundle.getString("CCDate.Month0"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Day1"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Day2"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Day3"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Day4"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Day5"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Day6"), //$NON-NLS-1$
		LocaleBundle.getString("CCDate.Day7") //$NON-NLS-1$
	};

	private static final int[][] monLims = {{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}, {0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}};
	private static final int[] monvaltab = {0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4};
	
	private int day = 1;
	private int month = 1;
	private int year = YEAR_MIN;
	
	public CCDate() { //TODO Make add / rem etc Methods FASTER (AND TEST THE SHIT OUT OF THEM (after the performance-boost))
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

	public CCDate(Date d) {
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		set(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
	}

	public String getMonthName() {
		return monNames[month];
	}
	
	public int getDaysOfMonth() {
		return monLims[ (isLeapYear()) ? (1) : (0) ][ month ];
	}
	
	public boolean isLeapYear() {
		return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0);
	}
	
	private int getWeekdayInt() {
	    int wkd = (((year - ((month<3)?1:0)) + (year - ((month<3)?1:0))/4 - (year - ((month<3)?1:0))/100 + (year - ((month<3)?1:0))/400 + monvaltab[month - 1] + day) % 7) - 1;
	    
	    return (wkd < 0) ? (wkd+7) : (wkd);
	}
	
	public String getWeekday() {
		return weekDays[getWeekdayInt()];
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
		String rst = ""; //$NON-NLS-1$
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
					
					rst += tmpformat;
					
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
					
					rst += tmpformat;
				}
				rst += c;
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
			
			rst += tmpformat;
		}
		
		return rst;
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
	
	public void add(int d, int m, int y) {
		addYear(y);
		addMonth(m);
		addDay(d);
	}
	
	public void rem(int d, int m, int y) {
		remYear(y);
		remMonth(m);
		remDay(d);
	}
	
	public void addDay(int d) {
		if (d < 0) {
			remDay(-d);
			return;
		}
		
		day += d;
		
		while (day > getDaysOfMonth()) {
			day -= getDaysOfMonth();
			addMonth(1);
		}
	}
	
	public void remDay(int d) {
		if (d < 0) {
			addDay(-d);
			return;
		}
		
		day -= d;
		
		while (day <= 0) {
			remMonth(1);
			day += getDaysOfMonth();
		}
	}
	
	public void addMonth(int m) {
		if (m < 0) {
			remMonth(-m);
			return;
		}
		
		month += m;
		while (month > 12) {
			addYear(1);
			month -= 12;
		}
	}
	
	public void remMonth(int m) {
		if (m < 0) {
			addMonth(-m);
			return;
		}
		
		month -= m;
		
		while(month < 1) {
			month += 12;
			remYear(1);
		}
	}
	
	public void addYear(int y) {
		if (y < 0) {
			remYear(-y);
			return;
		}
		
		year += y;
	}
	
	public void remYear(int y) {
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
			temp.remDay(1);
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
	
	public boolean equals(CCDate other) {
		return other.getDay() == getDay() && other.getMonth() == getMonth() && other.getYear() == getYear();
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
		return 1 == getDay() && 1 == getMonth() && YEAR_MIN == getYear(); //FAST - PERFORMANCE - SMACK YOU LIKE A BITCH
	}

	public CCDate copy() {
		return new CCDate(this);
	}
}
