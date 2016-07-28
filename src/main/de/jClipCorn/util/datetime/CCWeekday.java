package de.jClipCorn.util.datetime;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum CCWeekday implements ContinoousEnum<CCWeekday> {
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
	
	private static EnumWrapper<CCWeekday> wrapper = new EnumWrapper<>(UNKNOWN);

	private CCWeekday(int val) {
		id = val;
	}
	
	public static EnumWrapper<CCWeekday> getWrapper() {
		return wrapper;
	}

	@Override
	public int asInt() {
		return id;
	}

	@Override
	public String asString() {
		return NAMES[asInt()];
	}

	@Override
	public String[] getList() {
		return NAMES;
	}

	@Override
	public CCWeekday[] evalues() {
		return CCWeekday.values();
	}
}
