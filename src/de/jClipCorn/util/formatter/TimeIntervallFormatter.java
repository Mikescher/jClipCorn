package de.jClipCorn.util.formatter;

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
		
		int fullmins = mins % 60;
		mins -= fullmins;
		mins /= 60;
		int fullhours = mins % 24;
		mins -= fullhours;
		mins /= 24;
		int fulldays = mins % 365;
		mins -= fulldays;
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
		
		if (fulldays != 0 || render) {
			if (fulldays != 1) {
				res += fulldays + " " + STR_DAYS + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				res += fulldays + " " + STR_DAY + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			}
			render = true;
		}
		
		if (fullhours != 0 || render) {
			if (fullhours != 1) {
				res += fullhours + " " + STR_HOURS + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				res += fullhours + " " + STR_HOUR + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			}
			render = true;
		}
		
		if (fullmins != 0 || render) {
			if (fullmins != 1) {
				res += fullmins + " " + STR_MINUTES; //$NON-NLS-1$
			} else {
				res += fullmins + " " + STR_MINUTE; //$NON-NLS-1$
			}
			render = true;
		}
		
		return res;
	}
	
	public static String formatPointed(int mins) {
		String res = ""; //$NON-NLS-1$
		
		int fullmins = mins % 60;
		mins -= fullmins;
		mins /= 60;
		int fullhours = mins % 24;
		mins -= fullhours;
		mins /= 24;
		int fulldays = mins % 365;
		mins -= fulldays;
		mins /= 365;
		int fullyears = mins;
		
		boolean render = false;
		if (fullyears != 0 || render) {
			if (fullyears != 1) {
				res += fullyears + " " + STR_YEARS + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				res += fullyears + " " + STR_YEAR + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			}
			render = true;
		}
		
		if (fulldays != 0 || render) {
			if (fulldays != 1) {
				res += fulldays + " " + STR_DAYS + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				res += fulldays + " " + STR_DAY + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			}
			render = true;
		}
		
		if (fullhours != 0 || render) {
			res += fullhours + " " + STR_HOURS_P + ", "; //$NON-NLS-1$ //$NON-NLS-2$
			render = true;
		}
		
		if (fullmins != 0 || render) {
			res += fullmins + " " + STR_MINUTES_P; //$NON-NLS-1$
			render = true;
		}
		
		return res;
	}
}
