package de.jClipCorn.util;

import java.util.Calendar;

import de.jClipCorn.util.exceptions.TimeFormatException;

public class CCTime implements Comparable<CCTime> {
	public static final CCTime MIDNIGHT = new CCTime(0, 0, 0);

	public final static String STRINGREP_SHORT  = "HH:MM"; //$NON-NLS-1$
	public final static String STRINGREP_SIMPLE = "HH:MM:SS"; //$NON-NLS-1$
	public final static String STRINGREP_SQL    = "HH:MM:SS"; //$NON-NLS-1$
	public final static String STRINGREP_AMPM   = "PP:MM:SS A"; //$NON-NLS-1$
	
	private int hour;
	private int min;
	private int sec;
	
	public CCTime() {
		Calendar cal = Calendar.getInstance();
		hour = cal.get(Calendar.HOUR);
		if (cal.get(Calendar.AM_PM) == Calendar.PM) hour += 12;
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
	
	public boolean set(int h, int m, int s) {
		boolean succ = true;
		succ &= setHour(h);
		succ &= setMinute(m);
		succ &= setSecond(s);
		return succ;
	}
	
	public boolean setHour(int h) {
		if (h >= 0 && h < 24) {
			hour = h;
			return true;
		}
		return false;
	}
	
	public boolean setMinute(int m) {
		if (m >= 0 && m < 60) {
			min = m;
			return true;
		}
		return false;
	}
	
	public boolean setSecond(int s) {
		if (s >= 0 && s < 60) {
			sec = s;
			return true;
		}
		return false;
	}
	
	public void add(int s, int m, int h) {
		addSeconds(s);
		addMinutes(m);
		addHours(h);
	}
	
	public void addSeconds(int s){
		while(sec + s > 59) {
			s -= 60 - sec;
			sec = 0;
			addMinutes(1);
		}
		sec += s;
	}
	
	public void addMinutes(int m){
		while(sec + m > 59) {
			m -= 60 - min;
			min = 0;
			addHours(1);
		}
		sec += m;
	}
	
	public void addHours(int h){
		while(hour + h > 23) {
			h -= 24-hour;
			hour = 0;
		}
		hour += h;
	}
	
	public void rem(int s, int m, int h) {
		remSeconds(s);
		remMinutes(m);
		remHours(h);
	}
	
	public void remSeconds(int s){
		while(sec - s < 0) {
			s -= sec;
			sec = 59;
			remMinutes(1);
		}
		sec -= s;
	}
	
	public void remMinutes(int m){
		while(sec - m < 0) {
			m -= min;
			min = 59;
			remHours(1);
		}
		sec -= m;
	}
	
	public void remHours(int h){
		while(hour - h < 0) {
			h -= hour;
			hour = 23;
		}
		hour -= h;
	}
	
	/**
	 * @param fmt Format STyle
	 * HH	=> HOUR
	 * PP	=> HOUR (12-Hour Format)
	 * MM 	=> MINUTE (005)
	 * MM  	=> MINUTE (05)
	 * S   	=> SECOND
	 * A	=> AM / PM
	 * @return
	 */
	public String getStringRepresentation(String fmt) {
		StringBuilder repbuilder = new StringBuilder();
		
		char actualCounter = '-';
		int counter = 0;
		char c;
		
		for (int p = 0;p<fmt.length();p++) {
			c = fmt.charAt(p);
			if (c == 'H' || c == 'M' || c == 'S') {
				if (counter == 0 || actualCounter == c) {
					counter++;
					actualCounter = c;
				} else {
					String tmpformat = ""; //$NON-NLS-1$
					
					if (actualCounter == 'H') {
						tmpformat = String.format("%0" + counter + "d", getHours()); //$NON-NLS-1$ //$NON-NLS-2$
					} else if (actualCounter == 'M') {
						tmpformat = String.format("%0" + counter + "d", getMinutes()); //$NON-NLS-1$ //$NON-NLS-2$
					} else if (actualCounter == 'S') {
						tmpformat = String.format("%0" + counter + "d", getSeconds()); //$NON-NLS-1$ //$NON-NLS-2$
					} else if (actualCounter == 'P') {
						tmpformat = String.format("%0" + counter + "d", getAMPMHours()); //$NON-NLS-1$ //$NON-NLS-2$
					} else if (actualCounter == 'A') {
						tmpformat = String.format("%0" + counter + "d", getAMPMString()); //$NON-NLS-1$ //$NON-NLS-2$
					}
					
					repbuilder.append(tmpformat);
					
					counter = 1;
					actualCounter = c;
				}
			} else {
				if (counter > 0) {
					String tmpformat = ""; //$NON-NLS-1$
					
					if (actualCounter == 'H') {
						tmpformat = String.format("%0" + counter + "d", getHours()); //$NON-NLS-1$ //$NON-NLS-2$
					} else if (actualCounter == 'M') {
						tmpformat = String.format("%0" + counter + "d", getMinutes()); //$NON-NLS-1$ //$NON-NLS-2$
					} else if (actualCounter == 'S') {
						tmpformat = String.format("%0" + counter + "d", getSeconds()); //$NON-NLS-1$ //$NON-NLS-2$
					} else if (actualCounter == 'P') {
						tmpformat = String.format("%0" + counter + "d", getAMPMHours()); //$NON-NLS-1$ //$NON-NLS-2$
					} else if (actualCounter == 'A') {
						tmpformat = String.format("%0" + counter + "d", getAMPMString()); //$NON-NLS-1$ //$NON-NLS-2$
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
			
			if (actualCounter == 'H') {
				tmpformat = String.format("%0" + counter + "d", getHours()); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (actualCounter == 'M') {
				tmpformat = String.format("%0" + counter + "d", getMinutes()); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (actualCounter == 'S') {
				tmpformat = String.format("%0" + counter + "d", getSeconds()); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (actualCounter == 'P') {
				tmpformat = String.format("%0" + counter + "d", getAMPMHours()); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (actualCounter == 'A') {
				tmpformat = String.format("%0" + counter + "d", getAMPMString()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			repbuilder.append(tmpformat);
		}
		
		return repbuilder.toString();
	}
	
	/**
	 * @param rawData parseable DATA
	 * @param fmt Format of rawData 
	 * eg "H:M:S"
	 * H	=> HOUR
	 * M	=> MINUTE
	 * S	=> SECOND
	 * @return
	 */
	public static CCTime parse(String rawData, String fmt) {
		CCTime c = new CCTime();
		c.parseData(rawData, fmt);
		return c;
	}
	
	/**
	 * @param rawData parseable DATA
	 * @param fmt Format of rawData 
	 * eg "H:M:S"
	 * H	=> HOUR
	 * M	=> MINUTE
	 * S	=> SECOND
	 * @return
	 */
	public boolean parseData(String rawData, String fmt) {
		char c;
		int rp = 0;
		
		int th = hour;
		int tm = min;
		int ts = sec;
		
		rawData += '\0';
		
		for (int p = 0;p<fmt.length();p++) {
			c = fmt.charAt(p);
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
				
				if (c == 'S') {
					ts = Integer.parseInt(drep);
				} else if (c == 'M') {
					tm = Integer.parseInt(drep);	
				} else if (c == 'H') {
					th = Integer.parseInt(drep);	
				}
			}  else {
				if (rawData.charAt(rp) == c) {
					rp++;
				} else {
					return false;
				}
			}
		}
		
		return set(th, tm, ts);
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
		
		return new CCTime(h, m, s);
	}

	public static CCTime getCurrentTime() {
		return new CCTime();
	}
	
	public String getSQLStringRepresentation() {
		return getStringRepresentation(STRINGREP_SQL);
	}
	
	public String getSimpleStringRepresentation() {
		return getStringRepresentation(STRINGREP_SIMPLE);
	}
	
	public String getShortStringRepresentation() {
		return getStringRepresentation(STRINGREP_SHORT);
	}
	
	public String getAMPMStringRepresentation() {
		return getStringRepresentation(STRINGREP_AMPM);
	}
	
	private String getAMPMString() {
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
		return (other instanceof CCTime) && ((CCTime)other).getSeconds() == getSeconds() && ((CCTime)other).getMinutes() == getMinutes() && ((CCTime)other).getHours() == getHours();
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
}