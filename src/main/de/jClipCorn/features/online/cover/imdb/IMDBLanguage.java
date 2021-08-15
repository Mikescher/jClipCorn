package de.jClipCorn.features.online.cover.imdb;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

public enum IMDBLanguage implements ContinoousEnum<IMDBLanguage> {
	GERMAN(0), 
	ENGLISH(1);

	private final static String[] NAMES = {
		LocaleBundle.getString("ImDBLanguage.GERMAN"),    //$NON-NLS-1$
		LocaleBundle.getString("ImDBLanguage.ENGLISH")    //$NON-NLS-1$
	};

	private int id;
	
	private static final EnumWrapper<IMDBLanguage> wrapper = new EnumWrapper<>(GERMAN);

	IMDBLanguage(int val) {
		id = val;
	}
	
	public static EnumWrapper<IMDBLanguage> getWrapper() {
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
