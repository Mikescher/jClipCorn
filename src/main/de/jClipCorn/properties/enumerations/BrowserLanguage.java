package de.jClipCorn.properties.enumerations;

import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

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
	private final static String[] NAMES = {
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
	private final static String[] LANGUAGE_IDS = {
		"en",
		"de",
		"cs",
		"fr",
		"it",
		"ja",
		"ru",
		"es",
		"tr",
	};
	
	@SuppressWarnings("nls")
	private final static String[] COUNTRY_IDS = {
		"US", // yeah TMDB wants -US
		"DE",
		"CZ",
		"FR",
		"IT",
		"JP",
		"RU",
		"ES",
		"TR",
	};
	
	private final int id;

	private static final EnumWrapper<BrowserLanguage> wrapper = new EnumWrapper<>(ENGLISH);

	private BrowserLanguage(int val) {
		id = val;
	}
	
	public static EnumWrapper<BrowserLanguage> getWrapper() {
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
	
	public String asDinIsoID() {
		return asLanguageID() + "-" + asCountryID(); //$NON-NLS-1$
	}
	
	public String asLanguageID() {
		return LANGUAGE_IDS[asInt()];
	}
	
	public String asCountryID() {
		return COUNTRY_IDS[asInt()];
	}

	@Override
	public BrowserLanguage[] evalues() {
		return BrowserLanguage.values();
	}
}
