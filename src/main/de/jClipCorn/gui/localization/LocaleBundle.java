package de.jClipCorn.gui.localization;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;

import java.util.*;

public class LocaleBundle {
	private final static int DEFAULT = 1;
	private final static String DEFAULT_BASENAME = "de.jClipCorn.gui.localization.locale"; //$NON-NLS-1$
	private final static Locale[] LOCALES = {Locale.getDefault(), new Locale("dl", "DL"), Locale.GERMAN, Locale.US}; //$NON-NLS-1$ //$NON-NLS-2$
	
	private static ResourceBundle bundle = null; 
	
	private static Locale getLocale(CCProperties ccprops) {
		return LOCALES[ccprops.PROP_UI_LANG.getValue().asInt()];
	}

	private static Locale getLocale(int langID) {
		return LOCALES[langID];
	}

	public static Locale[] listLocales() {
		return LOCALES;
	}

	private static Locale getDefaultLocale() {
		return LOCALES[DEFAULT];
	}
	
	public static String getStringOrDefault(String ident, String def) {
		try {
			if (ident.startsWith("@")) return ident.substring(1); //$NON-NLS-1$
			
			if (bundle == null) {
				return ResourceBundle.getBundle(DEFAULT_BASENAME, getDefaultLocale()).getString(ident);
			} else {
				if (! bundle.containsKey(ident)) return def;
				
				return bundle.getString(ident);
			}
		} catch (MissingResourceException e) {
			CCLog.addError(e);
			return ""; //$NON-NLS-1$
		}
	}

	public static String getStringOrDefaultInLocale(Locale loc, String ident, String def) {
		try {
			if (ident.startsWith("@")) return ident.substring(1); //$NON-NLS-1$

			if (! ResourceBundle.getBundle(DEFAULT_BASENAME, loc).containsKey(ident)) return def;
			return ResourceBundle.getBundle(DEFAULT_BASENAME, loc).getString(ident);
		} catch (MissingResourceException e) {
			CCLog.addError(e);
			return ""; //$NON-NLS-1$
		}
	}

	@SuppressWarnings("nls")
	public static String getString(String ident) {
		try {
			if (ident.startsWith("@")) return ident.substring(1);

			if (bundle == null) {
				return ResourceBundle.getBundle(DEFAULT_BASENAME, getDefaultLocale()).getString(ident);
			} else {
				return bundle.getString(ident);
			}
		} catch (MissingResourceException e) {
			if (ident.startsWith("CCLog.")) return Str.Empty; // prevent infinite log recursion if _no_ locales were found

			CCLog.addError(e);
			return Str.Empty;
		}
	}

	@SuppressWarnings("nls")
	public static String getStringInLocale(Locale loc, String ident) {
		try {
			if (ident.startsWith("@")) return ident.substring(1);
			return ResourceBundle.getBundle(DEFAULT_BASENAME, loc).getString(ident);
		} catch (MissingResourceException e) {
			if (ident.startsWith("CCLog.")) return Str.Empty; // prevent infinite log recursion if _no_ locales were found

			CCLog.addError(e);
			return Str.Empty;
		}
	}
	
	public static String getFormattedString(String ident, Object... args) {
		return String.format(getString(ident), args);
	}
	
	public static String getMFFormattedString(String ident, Object... args) {
		return Str.format(getString(ident), args);
	}
	
	public static String getDeformattedString(String ident) {
		String val = getString(ident);
		
		StringBuilder builder = new StringBuilder();
		
		int pos = 0;
		boolean isSpecial = false;
		while (pos < val.length()) {
			boolean skip = false;
			char c = val.charAt(pos);
			
			if (isSpecial) {
				isSpecial = false;
				if (Character.isLetter(c)) {
					skip = true;
					builder.deleteCharAt(builder.length() - 1);
				}
			} else {
				if (c == '%') 
					isSpecial = true;
			}
			
			if (! skip) {
				builder.append(c);
			}
			
			pos++;
			
		}
		return builder.toString();
	}

	public static void updateLang(CCProperties ccprops) {
		bundle = ResourceBundle.getBundle(DEFAULT_BASENAME, getLocale(ccprops));
	}

	public static void updateLangManual(int overrideLang) {
		bundle = ResourceBundle.getBundle(DEFAULT_BASENAME, getLocale(overrideLang));
	}
	
	public static int getTranslationCount(CCProperties ccprops) {
		int c = 0;
		for (Enumeration<String> enum_s = ResourceBundle.getBundle(DEFAULT_BASENAME, getLocale(ccprops)).getKeys(); enum_s.hasMoreElements(); enum_s.nextElement()) {
			c++;
		}
		
		return c;
	}
	
	public static Locale getCurrentLocale(CCProperties ccprops) {
		return getLocale(ccprops);
	}
	
	@SuppressWarnings("nls")
	private static void addSubListToBuilder(StringBuilder builder, String prefix, List<String> list, int depth) {
		list.remove(prefix);
		
		boolean finished = false;
		while(! finished) {
			finished = true;
			for (int i = 0; i < list.size(); i++) {
				String s = list.get(i);
				if (s.startsWith(prefix)) {
					finished = false;
					String newPrefix = s.substring(0, prefix.length());

					if (s.indexOf('.', newPrefix.length()+1) > 0)
						newPrefix = s.substring(0, s.indexOf('.', newPrefix.length()+1));
					else
						newPrefix = s;

					builder.append(Str.repeat("   ", Math.max(0, depth)));
					builder.append(newPrefix.substring(newPrefix.lastIndexOf('.')+1));
					if (bundle.containsKey(s)) builder.append("   (\"").append(getString(s).replaceAll("\r\n", "\\\\r\\\\n")).append("\")");
					builder.append("\r\n");
					addSubListToBuilder(builder, newPrefix, list, depth + 1);
					break;
				}
			}
		}
	}
	
	private static String getTranslationTree() {
		Enumeration<String> enumer = bundle.getKeys();
		List<String> list = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		
		while (enumer.hasMoreElements()) {
			String s = enumer.nextElement();
			
			list.add(s);
		}
		
		Collections.sort(list);
		
		addSubListToBuilder(builder, "", list, 0); //$NON-NLS-1$
		
		return builder.toString();
	}
	
	public static void printTranslationTree() {
		CCLog.addDebug(getTranslationTree());
	}
}
