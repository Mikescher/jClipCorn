package de.jClipCorn.util.datetime;

import de.jClipCorn.gui.localization.LocaleBundle;

public class CCChronos {
	static class TimeWithOverflow
	{
		public final int DayDiff, Hour, Minute, Second;
		public TimeWithOverflow(int daydiff, int hour, int minute, int second) { this.DayDiff = daydiff; this.Hour = hour; this.Minute = minute; this.Second = second; }
	}

	static class Date
	{
		public final int Year, Month, Day;
		public Date(int year, int month, int day) { Year = year; Month = month; Day = day; }
	}

	static class DateTime
	{
		public final int Year, Month, Day, Hour, Minute, Second;
		public DateTime(int year, int month, int day, int hour, int minute, int second) { Year = year; Month = month; Day = day; this.Hour = hour; this.Minute = minute; this.Second = second; }
	}

	private static final String[] MONTHNAMES =
	{
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

	private static final int[][] MONTHLIMITS = {{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}, {0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}};

	public static boolean isLeapYear(int y) {
		return ((y % 4 == 0 && y % 100 != 0) || y % 400 == 0);
	}

	public static String getMonthName(int month) {
		return MONTHNAMES[month];
	}

	public static int getDaysOfMonth(int m, int y) {
		return MONTHLIMITS[ (isLeapYear(y)) ? (1) : (0) ][ m ];
	}

	public static int getDaysOfYear(int y) {
		return isLeapYear(y) ? 366 : 365;
	}

	// src_* must be in valid range
	// delta_* can be anything
	public static TimeWithOverflow addLowerHalf(int src_hour, int src_minute, int src_second, int delta_hour, int delta_minute, int delta_second)
	{
		if (src_hour   < 0 || src_hour   > 24) throw new IllegalArgumentException();
		if (src_minute < 0 || src_minute > 60) throw new IllegalArgumentException();
		if (src_second < 0 || src_second > 60) throw new IllegalArgumentException();

		int newday     = 0;
		int newhours   = src_hour   + delta_hour;
		int newminutes = src_minute + delta_minute;
		int newseconds = src_second + delta_second;

		if (newseconds >= 60) {
			newminutes += (newseconds - newseconds % 60)/60;
			newseconds = newseconds % 60;
		} else if (newseconds < 0) {
			newminutes -= ((-newseconds) + (60 - (-newseconds) % 60)) / 60;
			newseconds = 60 - (-newseconds) % 60;
			if (newseconds==60) { newseconds=0; newminutes++; }
		}

		if (newminutes >= 60) {
			newhours += (newminutes - newminutes % 60)/60;
			newminutes = newminutes % 60;
		} else if (newminutes < 0) {
			newhours -= ((-newminutes) + (60 - (-newminutes) % 60)) / 60;
			newminutes = 60 - (-newminutes) % 60;
			if (newminutes==60) { newminutes=0; newhours++; }
		}

		if (newhours >= 24) {
			newday += (newhours - newhours % 24)/24;
			newhours = newhours % 24;
		} else if (newhours < 0) {
			newday -= ((-newhours) + (24 - (-newhours) % 24)) / 24;
			newhours = 24 - (-newhours) % 24;
			if (newhours==24) { newhours=0; newday++; }
		}

		return new TimeWithOverflow(newday, newhours, newminutes, newseconds);
	}

	public static Date addUpperHalf(int src_year, int src_month, int src_day, int delta_year, int delta_month, int delta_day)
	{
		int newyear    = src_year      + delta_year;
		int newmonth   = (src_month-1) + delta_month;
		int newday     = (src_day-1)   + delta_day;

		if (newmonth >= 12) {
			newyear += (newmonth - newmonth % 12)/12;
			newmonth = newmonth % 12;
		} else if (newmonth < 0) {
			newyear -= ((-newmonth) + (12 - (-newmonth) % 12)) / 12;
			newmonth = 12 - (-newmonth) % 12;
			if (newmonth==12) { newmonth=0; newyear++; }
		}

		while (newday >= getDaysOfMonth(newmonth+1, newyear)) {
			newday -= getDaysOfMonth(newmonth+1, newyear);
			newmonth++;

			if (newmonth >= 12) {
				newyear += (newmonth - newmonth % 12)/12;
				newmonth = newmonth % 12;
			} else if (newmonth < 0) {
				newyear -= ((-newmonth) + (12 - (-newmonth) % 12)) / 12;
				newmonth = 12 - (-newmonth) % 12;
				if (newmonth==12) { newmonth=0; newyear++; }
			}
		}
		while (newday < 0) {
			newmonth--;

			if (newmonth >= 12) {
				newyear += (newmonth - newmonth % 12)/12;
				newmonth = newmonth % 12;
			} else if (newmonth < 0) {
				newyear -= ((-newmonth) + (12 - (-newmonth) % 12)) / 12;
				newmonth = 12 - (-newmonth) % 12;
				if (newmonth==12) { newmonth=0; newyear++; }
			}

			newday += getDaysOfMonth(newmonth+1, newyear);
		}

		return new Date(newyear, newmonth+1, newday+1);
	}

	public static DateTime addFull(int src_year, int src_month, int src_day, int src_hour, int src_minute, int src_second, int delta_year, int delta_month, int delta_day, int delta_hour, int delta_minute, int delta_second) {
		var lower = addLowerHalf(src_hour, src_minute, src_second, delta_hour, delta_minute, delta_second);
		var upper = addUpperHalf(src_year, src_month, src_day, delta_year, delta_month, delta_day + lower.DayDiff);

		return new DateTime(upper.Year, upper.Month, upper.Day, lower.Hour, lower.Minute, lower.Second);
	}

	// return days([a] - [b])
	public static int subtractUpperHalf(int a_year, int a_month, int a_day, int b_year, int b_month, int b_day) {
		int yy = b_year;
		int mm = b_month;
		int dd = b_day;

		var totaldays = 0;

		// [0] Force day = 1

		totaldays = -(dd-1);
		dd = 1;

		// [1] months

		while (mm != a_month)
		{
			var sign = Integer.signum(a_month - mm);
			totaldays += sign * getDaysOfMonth(Math.min(mm, mm+sign), yy);
			mm += sign;
		}

		// [2] years

		while (yy != a_year)
		{
			var sign = Integer.signum(a_year - yy);
			totaldays += sign * getDaysOfYear(((mm <= 2) ^ (sign<0)) ? yy : yy+sign);
			yy += sign;
		}

		// [3] days

		totaldays += (a_day - dd);
		dd = a_day;

		return totaldays;
	}

}
