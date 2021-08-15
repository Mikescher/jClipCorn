package de.jClipCorn.properties.enumerations;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

public enum DisplayDateAlgorithm implements ContinoousEnum<DisplayDateAlgorithm> {
	LAST_VIEWED(0), 
	FIRST_VIEWED(1), 
	AVERAGE(2);
	
	@SuppressWarnings("nls")
	private final static String[] NAMES = {
		LocaleBundle.getString("DisplayDateAlgorithm.Opt0"),
		LocaleBundle.getString("DisplayDateAlgorithm.Opt1"),
		LocaleBundle.getString("DisplayDateAlgorithm.Opt2"),
	};
	
	private int id;

	private static final EnumWrapper<DisplayDateAlgorithm> wrapper = new EnumWrapper<>(LAST_VIEWED);

	DisplayDateAlgorithm(int val) {
		id = val;
	}
	
	public static EnumWrapper<DisplayDateAlgorithm> getWrapper() {
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

	public static int compare(DisplayDateAlgorithm s1, DisplayDateAlgorithm s2) {
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
	public DisplayDateAlgorithm[] evalues() {
		return DisplayDateAlgorithm.values();
	}
}
