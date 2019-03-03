package de.jClipCorn.properties.enumerations;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum AddDateAlgorithm implements ContinoousEnum<AddDateAlgorithm> {
	OLDEST_DATE(0), 
	NEWEST_DATE(1), 
	AVERAGE_DATE(2),
	NEWEST_BY_SEASON(3);
	
	@SuppressWarnings("nls")
	private final static String[] NAMES = {
		LocaleBundle.getString("AddDateAlgorithm.Opt0"),
		LocaleBundle.getString("AddDateAlgorithm.Opt1"),
		LocaleBundle.getString("AddDateAlgorithm.Opt2"),
		LocaleBundle.getString("AddDateAlgorithm.Opt3"),
	};
	
	private int id;

	private static EnumWrapper<AddDateAlgorithm> wrapper = new EnumWrapper<>(OLDEST_DATE);

	private AddDateAlgorithm(int val) {
		id = val;
	}
	
	public static EnumWrapper<AddDateAlgorithm> getWrapper() {
		return wrapper;
	}
	
	@Override
	public int asInt() {
		return id;
	}

	public static int compare(AddDateAlgorithm s1, AddDateAlgorithm s2) {
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
	public AddDateAlgorithm[] evalues() {
		return AddDateAlgorithm.values();
	}
}
