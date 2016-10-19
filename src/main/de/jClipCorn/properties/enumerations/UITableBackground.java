package de.jClipCorn.properties.enumerations;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum UITableBackground implements ContinoousEnum<UITableBackground> {
	WHITE(0), 
	STRIPED(1), 
	SCORE(2);
	
	@SuppressWarnings("nls")
	private final static String NAMES[] = {
		LocaleBundle.getString("UITableBackground.Opt0"),
		LocaleBundle.getString("UITableBackground.Opt1"),
		LocaleBundle.getString("UITableBackground.Opt2"),
	};
	
	private int id;

	private static EnumWrapper<UITableBackground> wrapper = new EnumWrapper<>(WHITE);

	private UITableBackground(int val) {
		id = val;
	}
	
	public static EnumWrapper<UITableBackground> getWrapper() {
		return wrapper;
	}
	
	@Override
	public int asInt() {
		return id;
	}

	public static int compare(UITableBackground s1, UITableBackground s2) {
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
	public UITableBackground[] evalues() {
		return UITableBackground.values();
	}
}
