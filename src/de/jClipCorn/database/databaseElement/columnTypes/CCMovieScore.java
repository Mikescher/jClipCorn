package de.jClipCorn.database.databaseElement.columnTypes;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;

public enum CCMovieScore {
	RATING_0(0),	// Fucking bullshit crap
	RATING_I(1),	// bad movie
	RATING_II(2),	// not recommended
	RATING_III(3),	// good enough to watch
	RATING_IV(4),	// recommended - good movie
	RATING_V(5),	// I f*** love this piece of movie-artwork
	RATING_NO(6);	// Unrated
	
	private final static String NAMES[] = {
		LocaleBundle.getString("CCMovieScore.R0"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.R1"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.R2"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.R3"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.R4"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.R5"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.RNO")   //$NON-NLS-1$
	};
	
	private int id;
	
	private CCMovieScore(int val) {
		id = val;
	}
	
	public int asInt() {
		return id;
	}
	
	public static CCMovieScore find(int val) {
		if (val >= 0 && val < CCMovieScore.values().length) {
			return CCMovieScore.values()[val]; // Geht nur wenn alle Zahlen nach der Reihe da sind
		}
		return null;
	}

	public static int compare(CCMovieScore s1, CCMovieScore s2) {
		return Integer.compare(s1.asInt(), s2.asInt());
	}
	
	public static String[] getList() {
		return NAMES;
	}
	
	public String asString() {
		return NAMES[asInt()];
	}
	
	public ImageIcon getIcon() {
		switch (this) {
		case RATING_0:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_SCORE_0);
		case RATING_I:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_SCORE_1);
		case RATING_II:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_SCORE_2);
		case RATING_III:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_SCORE_3);
		case RATING_IV:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_SCORE_4);
		case RATING_V:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_SCORE_5);
		case RATING_NO:
		default:
			return null;
		}
	}
	
	public String getIconName() {
		switch (this) {
		case RATING_0:
			return Resources.ICN_TABLE_SCORE_0;
		case RATING_I:
			return Resources.ICN_TABLE_SCORE_1;
		case RATING_II:
			return Resources.ICN_TABLE_SCORE_2;
		case RATING_III:
			return Resources.ICN_TABLE_SCORE_3;
		case RATING_IV:
			return Resources.ICN_TABLE_SCORE_4;
		case RATING_V:
			return Resources.ICN_TABLE_SCORE_5;
		case RATING_NO:
		default:
			return null;
		}
	}
	
}
