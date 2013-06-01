package de.jClipCorn.gui.localization;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;

public class LocaleBundle {
	private final static int DEFAULT = 1;
	private final static String DEFAULT_BASENAME = "de.jClipCorn.gui.localization.locale"; //$NON-NLS-1$
	private final static Locale[] LOCALES = {Locale.getDefault(), new Locale("dl", "DL"), Locale.GERMAN, Locale.US}; //$NON-NLS-1$ //$NON-NLS-2$
	
	private static ResourceBundle bundle = null; 
	
	private static Locale getLocale() {
		return LOCALES[CCProperties.getInstance().PROP_UI_LANG.getValue()];
	}
	
	private static Locale getDefaultLocale() {
		return LOCALES[DEFAULT];
	}
	
	public static String getString(String ident) {
		try {
			if (bundle == null) {
				return ResourceBundle.getBundle(DEFAULT_BASENAME, getDefaultLocale()).getString(ident);
			} else {
				return bundle.getString(ident);
			}
		} catch (MissingResourceException e) {
			CCLog.addError(e);
			return ""; //$NON-NLS-1$
		}
	}
	
	public static String getFormattedString(String ident, Object... args) {
		return String.format(getString(ident), args);
	}

	public static void updateLang() {
		bundle = ResourceBundle.getBundle(DEFAULT_BASENAME, getLocale());
	}
	
	public static int getTranslationCount() {
		int c = 0;
		for (Enumeration<String> enum_s = ResourceBundle.getBundle(DEFAULT_BASENAME, getLocale()).getKeys(); enum_s.hasMoreElements(); enum_s.nextElement()) {
			c++;
		}
		
		return c;
	}
	
	public static Locale getCurrentLocale() {
		return getLocale();
	}
}
