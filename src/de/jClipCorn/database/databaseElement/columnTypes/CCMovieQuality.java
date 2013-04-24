package de.jClipCorn.database.databaseElement.columnTypes;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;

public enum CCMovieQuality {
	STREAM(0), 
	ONE_CD(1), 
	MULTIPLE_CD(2), 
	DVD(3),
	BLURAY(4);
	
	private static long FSIZE_MAX_STREAM = 512L * 1024 * 1024;	 	// 500 MB
	private static long FSIZE_MAX_CD = 2L * 1024 * 1024 * 1024;		// 2 GB
	private static long FSIZE_MAX_DVD = 5L * 1024 * 1024 * 1024; 	// 5 GB
	
	private final static String NAMES[] = {
		LocaleBundle.getString("CCMovieQuality.Quality0"),  //$NON-NLS-1$
		LocaleBundle.getString("CCMovieQuality.Quality1"),  //$NON-NLS-1$
		LocaleBundle.getString("CCMovieQuality.Quality2"),  //$NON-NLS-1$
		LocaleBundle.getString("CCMovieQuality.Quality3"),  //$NON-NLS-1$
		LocaleBundle.getString("CCMovieQuality.Quality4")   //$NON-NLS-1$
	};
	
	private int id;
	
	private CCMovieQuality(int val) {
		id = val;
	}
	
	public int asInt() {
		return id;
	}
	
	public String asString() {
		return NAMES[asInt()];
	}
	
	public static CCMovieQuality find(int val) {
		if (val >= 0 && val < CCMovieQuality.values().length) {
			return CCMovieQuality.values()[val]; // Geht nur wenn alle Zahlen nach der Reihe da sind
		}
		return null;
	}
	
	public static String[] getList() {
		return NAMES;
	}

	public static int compare(CCMovieQuality o1, CCMovieQuality o2) {
		return Integer.compare(o1.asInt(), o2.asInt());
	}
	
	public ImageIcon getIcon() {
		switch (this) {
		case STREAM:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_QUALITY_0);
		case ONE_CD:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_QUALITY_1);
		case MULTIPLE_CD:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_QUALITY_2);
		case DVD:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_QUALITY_3);
		case BLURAY:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_QUALITY_4);
		default:
			return null;
		}
	}
	
	public static CCMovieQuality getQualityForSize(CCMovieSize size, int partCount) {
		return getQualityForSize(size.getBytes(), partCount);
	}

	public static CCMovieQuality getQualityForSize(long size, int partCount) {
		if (size <= FSIZE_MAX_STREAM) {
			return CCMovieQuality.STREAM;
		} else if (size <= FSIZE_MAX_CD) {
			if (partCount == 1) {
				return CCMovieQuality.ONE_CD;
			} else {
				return CCMovieQuality.MULTIPLE_CD;
			}

		} else if (size <= FSIZE_MAX_DVD) {
			return CCMovieQuality.DVD;
		} else {
			return CCMovieQuality.BLURAY;
		}
	}
}