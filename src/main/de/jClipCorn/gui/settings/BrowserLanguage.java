package de.jClipCorn.gui.settings;

import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum BrowserLanguage implements ContinoousEnum<BrowserLanguage> {
	ENGLISH(0), 
	GERMAN(1), 
	CZECH(2),
	FRENCH(3), 
	ITALIAN(4), 
	JAPANESE(5), 
	RUSSIAN(6), 
	SPANISH(7), 
	TURKISH(8);
	
	@SuppressWarnings("nls")
	private final static String NAMES[] = {
		"English",
		"German",
		"Czech",
		"French",
		"Italian",
		"Japanese",
		"Russian",
		"Spanish",
		"Turkish",
	};
	
	@SuppressWarnings("nls")
	private final static String IDS[] = {
		"en-US", // yeah TMDB wants -US
		"de-DE",
		"cs-CZ",
		"fr-FR",
		"it-IT",
		"ja-JP",
		"ru-RU",
		"es-ES",
		"tr-TR",
	};
	
	private int id;

	private static EnumWrapper<BrowserLanguage> wrapper = new EnumWrapper<>(ENGLISH);

	private BrowserLanguage(int val) {
		id = val;
	}
	
	public static EnumWrapper<BrowserLanguage> getWrapper() {
		return wrapper;
	}
	
	@Override
	public int asInt() {
		return id;
	}

	public static int compare(BrowserLanguage s1, BrowserLanguage s2) {
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
	
	public String asLanguageID() {
		return IDS[asInt()];
	}

	@Override
	public BrowserLanguage[] evalues() {
		return BrowserLanguage.values();
	}
}
