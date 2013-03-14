package de.jClipCorn.database.databaseElement.columnTypes;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;

public enum CCMovieLanguage {
	GERMAN(0),
	ENGLISH(1),
	MUTED(2),
	FRENCH(3);

	private final static String names[] = {
		LocaleBundle.getString("CCMovieLanguage.German"),  //$NON-NLS-1$
		LocaleBundle.getString("CCMovieLanguage.English"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieLanguage.Muted"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieLanguage.French")   //$NON-NLS-1$
	};
	
	private int id;
	
	CCMovieLanguage(int val) {
		id = val;
	}
	
	public int asInt() {
		return id;
	}
	
	public String asString() {
		return names[asInt()];
	}
	
	public static CCMovieLanguage find(int val) {
		if (val >= 0 && val < CCMovieLanguage.values().length) {
			return CCMovieLanguage.values()[val]; // Geht nur wenn alle Zahlen nach der Reihe da sind
		}
		return null;
	}
	
	public static String[] getList() {
		return names;
	}

	public static int compare(CCMovieLanguage o1, CCMovieLanguage o2) {
		return Integer.compare(o1.asInt(), o2.asInt());
	}
	
	public ImageIcon getIcon() {
		switch (this) {
		case GERMAN:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_LANGUAGE_0);
		case ENGLISH:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_LANGUAGE_1);
		case MUTED:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_LANGUAGE_2);
		case FRENCH:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_LANGUAGE_3);
		default:
			return null;
		}
	}
}
