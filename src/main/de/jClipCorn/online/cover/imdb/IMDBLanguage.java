package de.jClipCorn.online.cover.imdb;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum IMDBLanguage implements ContinoousEnum<IMDBLanguage> {
	GERMAN(0), 
	ENGLISH(1);

	private final static String[] NAMES = {
		LocaleBundle.getString("ImDBLanguage.GERMAN"),    //$NON-NLS-1$
		LocaleBundle.getString("ImDBLanguage.ENGLISH")    //$NON-NLS-1$
	};

	private int id;
	
	private static EnumWrapper<IMDBLanguage> wrapper = new EnumWrapper<>(GERMAN);

	private IMDBLanguage(int val) {
		id = val;
	}
	
	public static EnumWrapper<IMDBLanguage> getWrapper() {
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
	public IMDBLanguage[] evalues() {
		return IMDBLanguage.values();
	}
}
