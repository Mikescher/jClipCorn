package de.jClipCorn.properties.enumerations;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

public enum UITableBackground implements ContinoousEnum<UITableBackground> {
	WHITE(0, 1),
	STRIPED(1, 2),
	SCORE(2, 3),
	DEFAULT(3, 0);
	
	@SuppressWarnings("nls")
	private final static String[] NAMES = {
		LocaleBundle.getString("UITableBackground.Opt0"),
		LocaleBundle.getString("UITableBackground.Opt1"),
		LocaleBundle.getString("UITableBackground.Opt2"),
		LocaleBundle.getString("UITableBackground.Opt3"),
	};

	private final int id;
	private final int order;

	private static final EnumWrapper<UITableBackground> wrapper = new EnumWrapper<>(WHITE, p -> p.order);

	private UITableBackground(int val, int ord) {
		id    = val;
		order = ord;
	}
	
	public static EnumWrapper<UITableBackground> getWrapper() {
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
