package de.jClipCorn.gui.settings;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum DoubleClickAction implements ContinoousEnum<DoubleClickAction> {
	PLAY(0), 
	PREVIEW(1), 
	EDIT(2);
	
	@SuppressWarnings("nls")
	private final static String NAMES[] = {
		LocaleBundle.getString("DoubleClickAction.Opt0"),
		LocaleBundle.getString("DoubleClickAction.Opt1"),
		LocaleBundle.getString("DoubleClickAction.Opt2"),
	};
	
	private int id;

	private static EnumWrapper<DoubleClickAction> wrapper = new EnumWrapper<>(PLAY);

	private DoubleClickAction(int val) {
		id = val;
	}
	
	public static EnumWrapper<DoubleClickAction> getWrapper() {
		return wrapper;
	}
	
	@Override
	public int asInt() {
		return id;
	}

	public static int compare(DoubleClickAction s1, DoubleClickAction s2) {
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
	public DoubleClickAction[] evalues() {
		return DoubleClickAction.values();
	}
}
