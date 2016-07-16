package de.jClipCorn.database.databaseElement.columnTypes;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;

public enum CCMovieOnlineScore {
	STARS_0_0(0),
	STARS_0_5(1),
	STARS_1_0(2),
	STARS_1_5(3),
	STARS_2_0(4),
	STARS_2_5(5),
	STARS_3_0(6),
	STARS_3_5(7),
	STARS_4_0(8),
	STARS_4_5(9),
	STARS_5_0(10);
	
	private int id;
	
	private CCMovieOnlineScore(int val) {
		id = val;
	}
	
	public int asInt() {
		return id;
	}
	
	public static CCMovieOnlineScore find(int val) {
		if (val >= 0 && val < CCMovieOnlineScore.values().length) {
			return CCMovieOnlineScore.values()[val]; // Geht nur wenn alle Zahlen nach der Reihe da sind
		}
		return null;
	}

	public static int compare(CCMovieOnlineScore o1, CCMovieOnlineScore o2) {
		return Integer.compare(o1.asInt(), o2.asInt());
	}
	
	public ImageIcon getIcon() {
		switch (this) {
		case STARS_0_0:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_0);
		case STARS_0_5:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_1);
		case STARS_1_0:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_2);
		case STARS_1_5:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_3);
		case STARS_2_0:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_4);
		case STARS_2_5:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_5);
		case STARS_3_0:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_6);
		case STARS_3_5:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_7);
		case STARS_4_0:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_8);
		case STARS_4_5:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_9);
		case STARS_5_0:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_10);
		default:
			return null;
		}
	}
}
