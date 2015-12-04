package de.jClipCorn.gui.localization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;

public class LocaleBundle {
	private final static int DEFAULT = 1;
	private final static String DEFAULT_BASENAME = "de.jClipCorn.gui.localization.locale"; //$NON-NLS-1$
	private final static Locale[] LOCALES = {Locale.getDefault(), new Locale("dl", "DL"), Locale.GERMAN, Locale.US}; //$NON-NLS-1$ //$NON-NLS-2$

	public final static int LOCALE_SYSTEMDEFAULT = 0;
	public final static int LOCALE_DUAL          = 1;
	public final static int LOCALE_GERMAN        = 2;
	public final static int LOCALE_ENGLISCH      = 3;
	
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
					
					for (int c = 0; c < depth; c++) builder.append("   ");
					builder.append(newPrefix.substring(newPrefix.lastIndexOf('.')+1, newPrefix.length()));
					if (bundle.containsKey(s)) builder.append("   (\"" + getString(s).replaceAll("\r\n", "\\\\r\\\\n") + "\")");
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
		System.out.println(getTranslationTree());
	}
}
