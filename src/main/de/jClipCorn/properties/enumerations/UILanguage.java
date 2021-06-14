package de.jClipCorn.properties.enumerations;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

public enum UILanguage implements ContinoousEnum<UILanguage> {
	DEFAULT(0), 
	DUAL(1), 
	GERMAN(2), 
	ENGLISCH(3);
	
	@SuppressWarnings("nls")
	private final static String[] NAMES = {
		LocaleBundle.getString("UILanguage.Opt0"),
		LocaleBundle.getString("UILanguage.Opt1"),
		LocaleBundle.getString("UILanguage.Opt2"),
		LocaleBundle.getString("UILanguage.Opt3"),
	};
	
	private int id;

	private static final EnumWrapper<UILanguage> wrapper = new EnumWrapper<>(DEFAULT);

	private UILanguage(int val) {
		id = val;
	}
	
	public static EnumWrapper<UILanguage> getWrapper() {
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

	public static int compare(UILanguage s1, UILanguage s2) {
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
	public UILanguage[] evalues() {
		return UILanguage.values();
	}
}
