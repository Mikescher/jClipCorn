package de.jClipCorn.util;

import de.jClipCorn.gui.localization.LocaleBundle;

public class TimeIntervallFormatter {
	private static String STR_MINUTE 	= LocaleBundle.getString("TimeIntervallFormatter.Minute"); //$NON-NLS-1$
	private static String STR_HOUR 		= LocaleBundle.getString("TimeIntervallFormatter.Hour"); //$NON-NLS-1$
	private static String STR_DAY 		= LocaleBundle.getString("TimeIntervallFormatter.Day"); //$NON-NLS-1$
	private static String STR_YEAR 		= LocaleBundle.getString("TimeIntervallFormatter.Year"); //$NON-NLS-1$
	
	private static String STR_MINUTES 	= LocaleBundle.getString("TimeIntervallFormatter.Minutes"); //$NON-NLS-1$
	private static String STR_HOURS 	= LocaleBundle.getString("TimeIntervallFormatter.Hours"); //$NON-NLS-1$
	private static String STR_DAYS 		= LocaleBundle.getString("TimeIntervallFormatter.Days"); //$NON-NLS-1$
	private static String STR_YEARS 	= LocaleBundle.getString("TimeIntervallFormatter.Years"); //$NON-NLS-1$
	
	private static String STR_MINUTES_P	= LocaleBundle.getString("TimeIntervallFormatter.Minute_p"); //$NON-NLS-1$
	private static String STR_HOURS_P 	= LocaleBundle.getString("TimeIntervallFormatter.Hour_p"); //$NON-NLS-1$
	
	public static String formatShort(int mins) {
		return mins + " min."; //$NON-NLS-1$
	}
	
	public static String format(int mins) {
		String res = ""; //$NON-NLS-1$
		
		int m = mins % 60;
		mins -= m;
		mins /= 60;
		int h = mins % 24;
		mins -= h;
		mins /= 24;
		int d = mins % 365;
		mins -= d;
		mins /= 365;
		int y = mins;
		
		boolean render = false;
		if (y != 0 || render) {
			if (y != 1) {
				res += y + " " + STR_YEARS + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				res += y + " " + STR_YEAR + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			}
			render = true;
		}
		
		if (d != 0 || render) {
			if (d != 1) {
				res += d + " " + STR_DAYS + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				res += d + " " + STR_DAY + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			}
			render = true;
		}
		
		if (h != 0 || render) {
			if (h != 1) {
				res += h + " " + STR_HOURS + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				res += h + " " + STR_HOUR + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			}
			render = true;
		}
		
		if (m != 0 || render) {
			if (m != 1) {
				res += m + " " + STR_MINUTES; //$NON-NLS-1$
			} else {
				res += m + " " + STR_MINUTE; //$NON-NLS-1$
			}
			render = true;
		}
		
		return res;
	}
	
	public static String formatPointed(int mins) {
		String res = ""; //$NON-NLS-1$
		
		int m = mins % 60;
		mins -= m;
		mins /= 60;
		int h = mins % 24;
		mins -= h;
		mins /= 24;
		int d = mins % 365;
		mins -= d;
		mins /= 365;
		int y = mins;
		
		boolean render = false;
		if (y != 0 || render) {
			if (y != 1) {
				res += y + " " + STR_YEARS + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				res += y + " " + STR_YEAR + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			}
			render = true;
		}
		
		if (d != 0 || render) {
			if (d != 1) {
				res += d + " " + STR_DAYS + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				res += d + " " + STR_DAY + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			}
			render = true;
		}
		
		if (h != 0 || render) {
			res += h + " " + STR_HOURS_P + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			render = true;
		}
		
		if (m != 0 || render) {
			res += m + " " + STR_MINUTES_P; //$NON-NLS-1$
			render = true;
		}
		
		return res;
	}
}
