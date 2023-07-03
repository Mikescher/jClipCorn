package de.jClipCorn.properties.enumerations;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

public enum SeriesViewCountMode implements ContinoousEnum<SeriesViewCountMode> {
	AGGREGATE_MIN(0),
	AGGREGATE_MAX(1),
	AGGREGATE_AVG(2),
	TIME_RATIO(3);

	@SuppressWarnings("nls")
	private final static String[] NAMES = {
		LocaleBundle.getString("SeriesViewCountMode.Opt0"),
		LocaleBundle.getString("SeriesViewCountMode.Opt1"),
		LocaleBundle.getString("SeriesViewCountMode.Opt2"),
		LocaleBundle.getString("SeriesViewCountMode.Opt3"),
	};

	private final int id;

	private static final EnumWrapper<SeriesViewCountMode> wrapper = new EnumWrapper<>(AGGREGATE_MIN);

	SeriesViewCountMode(int val) {
		id = val;
	}
	
	public static EnumWrapper<SeriesViewCountMode> getWrapper() {
		return wrapper;
	}

	@Override
	public IEnumWrapper wrapper() {
		return getWrapper();
	}
	
	@Override
	public int asInt() {
		return id;
	}

	public static int compare(SeriesViewCountMode s1, SeriesViewCountMode s2) {
		return Integer.compare(s1.asInt(), s2.asInt());
	}
	
	@Override
	public String[] getList() {
		return NAMES;
	}
	
	@Override
	public String asString() {
		return NAMES[asInt()];
	}

	@Override
	public SeriesViewCountMode[] evalues() {
		return SeriesViewCountMode.values();
	}
}
