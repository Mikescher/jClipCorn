package de.jClipCorn.properties.enumerations;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum AniListTitleLang implements ContinoousEnum<AniListTitleLang> {
	PREFERRED(0),
	ENGLISH(1),
	NATIVE(2);

	@SuppressWarnings("nls")
	private final static String NAMES[] = {
		LocaleBundle.getString("AniListTitleLang.Opt0"),
		LocaleBundle.getString("AniListTitleLang.Opt1"),
		LocaleBundle.getString("AniListTitleLang.Opt2"),
	};

	private int id;

	private static EnumWrapper<AniListTitleLang> wrapper = new EnumWrapper<>(PREFERRED);

	private AniListTitleLang(int val) {
		id = val;
	}
	
	public static EnumWrapper<AniListTitleLang> getWrapper() {
		return wrapper;
	}
	
	@Override
	public int asInt() {
		return id;
	}

	public static int compare(AniListTitleLang s1, AniListTitleLang s2) {
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
	public AniListTitleLang[] evalues() {
		return AniListTitleLang.values();
	}
}
