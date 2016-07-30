package de.jClipCorn.util.parser.onlineparser;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum ImDBLanguage implements ContinoousEnum<ImDBLanguage> {
	GERMAN(0), 
	ENGLISH(1);

	private final static String NAMES[] = {
		LocaleBundle.getString("ImDBLanguage.GERMAN"),    //$NON-NLS-1$
		LocaleBundle.getString("ImDBLanguage.ENGLISH")    //$NON-NLS-1$
	};

	private int id;
	
	private static EnumWrapper<ImDBLanguage> wrapper = new EnumWrapper<>(GERMAN);

	private ImDBLanguage(int val) {
		id = val;
	}
	
	public static EnumWrapper<ImDBLanguage> getWrapper() {
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
	public ImDBLanguage[] evalues() {
		return ImDBLanguage.values();
	}
}
