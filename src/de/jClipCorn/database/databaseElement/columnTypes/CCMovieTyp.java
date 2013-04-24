package de.jClipCorn.database.databaseElement.columnTypes;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;

public enum CCMovieTyp {
	MOVIE(0),
	SERIES(1);
	
	private final static String NAMES[] = {
		LocaleBundle.getString("CCMovieTyp.Movie"),  //$NON-NLS-1$
		LocaleBundle.getString("CCMovieTyp.Series") //$NON-NLS-1$
	};
	
	private int id;
	
	private CCMovieTyp(int val) {
		id = val;
	}
	
	public int asInt() {
		return id;
	}
	
	public String asString() {
		return NAMES[asInt()];
	}
	
	public static CCMovieTyp find(int val) {
		if (val >= 0 && val < CCMovieFormat.values().length) {
			return CCMovieTyp.values()[val]; // Geht nur wenn alle Zahlen nach der Reihe da sind
		}
		return null;
	}
	
	public static String[] getList() {
		return NAMES;
	}
	
	public ImageIcon getIcon() {
		switch (this) {
		case MOVIE:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_MOVIE );
		case SERIES:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_SERIES);
		default:
			return null;
		}
		
	}
}
