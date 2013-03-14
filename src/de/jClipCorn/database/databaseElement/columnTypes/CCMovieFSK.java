package de.jClipCorn.database.databaseElement.columnTypes;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;

public enum CCMovieFSK {
	RATING_0(0), // keine Alterschbeschräkung
	RATING_I(1), // ab 6 Jahren
	RATING_II(2), // ab 12 Jahren
	RATING_III(3), // ab 16 Jahren
	RATING_IV(4); // keine Jugendfreigabe

	private final static String names[] = { LocaleBundle.getString("CCMovieFSK.FSK0"), //$NON-NLS-1$
			LocaleBundle.getString("CCMovieFSK.FSK1"), //$NON-NLS-1$
			LocaleBundle.getString("CCMovieFSK.FSK2"), //$NON-NLS-1$
			LocaleBundle.getString("CCMovieFSK.FSK3"), //$NON-NLS-1$
			LocaleBundle.getString("CCMovieFSK.FSK4") }; //$NON-NLS-1$
	private final static int ages[] = { 0, 6, 12, 16, 18 };
	private int id;

	CCMovieFSK(int val) {
		id = val;
	}

	public int asInt() {
		return id;
	}

	public static CCMovieFSK find(int val) {
		if (val >= 0 && val < CCMovieFormat.values().length) {
			return CCMovieFSK.values()[val]; // Geht nur wenn alle Zahlen nach der Reihe da sind
		}
		return null;
	}

	public String asString() {
		return names[asInt()];
	}

	public static String[] getList() {
		return names;
	}

	public static CCMovieFSK getNearest(int age) {
		int val = -1;
		int max = Integer.MAX_VALUE;

		for (int i = 0; i < ages.length; i++) {
			if (Math.abs(age - ages[i]) <= max) { // <= is used to prioritize the higher Ratings (Age=9 will result in FSK-12, rathrer than FSK-6)
				val = i;
				max = Math.abs(age - ages[i]);
			}
		}

		return find(val);
	}

	public static int compare(CCMovieFSK o1, CCMovieFSK o2) {
		return Integer.compare(o1.asInt(), o2.asInt());
	}

	public ImageIcon getIcon() {
		switch (this) {
		case RATING_0:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_FSK_0);
		case RATING_I:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_FSK_1);
		case RATING_II:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_FSK_2);
		case RATING_III:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_FSK_3);
		case RATING_IV:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_FSK_4);
		default:
			return null;
		}
	}
}
