package de.jClipCorn.util.datetime;

import de.jClipCorn.gui.localization.LocaleBundle;

public enum CCWeekday {
	UNKNOWN(0),
	MONDAY(1),
	TUESDAY(2),
	WEDNESDAY(3),
	THURSDAY(4),
	FRIDAY(3),
	SATURDAY(3),
	SUNDAY(3);

	private final static String NAMES[] = { 
			LocaleBundle.getString("CCDate.Month0"), //$NON-NLS-1$
			LocaleBundle.getString("CCDate.Day1"), //$NON-NLS-1$
			LocaleBundle.getString("CCDate.Day2"), //$NON-NLS-1$
			LocaleBundle.getString("CCDate.Day3"), //$NON-NLS-1$
			LocaleBundle.getString("CCDate.Day4"), //$NON-NLS-1$
			LocaleBundle.getString("CCDate.Day5"), //$NON-NLS-1$
			LocaleBundle.getString("CCDate.Day6"), //$NON-NLS-1$
			LocaleBundle.getString("CCDate.Day7") //$NON-NLS-1$
	};
	private int id;

	private CCWeekday(int val) {
		id = val;
	}

	public int asInt() {
		return id;
	}

	public static CCWeekday find(int val) {
		if (val >= 0 && val < CCWeekday.values().length) {
			return CCWeekday.values()[val]; // Geht nur wenn alle Zahlen nach der Reihe da sind
		}
		return null;
	}

	public String asString() {
		return NAMES[asInt()];
	}

	public static String[] getList() {
		return NAMES;
	}
}
