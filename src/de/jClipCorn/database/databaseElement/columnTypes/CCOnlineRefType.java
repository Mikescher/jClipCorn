package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.util.exceptions.OnlineRefFormatException;

public enum CCOnlineRefType {
	NONE(0),
	IMDB(1),
	AMAZON(2),
	MOVIEPILOT(3),
	THEMOVIEDB(4);
	
	private final static String IDENTIFIER[] = {
		"",   			//$NON-NLS-1$
		"amzn",   		//$NON-NLS-1$
		"imdb",   		//$NON-NLS-1$
		"moviepilot",   //$NON-NLS-1$
		"tmdb",   		//$NON-NLS-1$
	};
	
	private final static String NAMES[] = {
		"",   				//$NON-NLS-1$
		"Amazon",   		//$NON-NLS-1$
		"IMDb",   			//$NON-NLS-1$
		"Moviepilot",   	//$NON-NLS-1$
		"TheMovieDatabase", //$NON-NLS-1$
	};
	
	private int id;
	
	private CCOnlineRefType(int val) {
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
		return IDENTIFIER;
	}
	
	public String getIdentifier() {
		return IDENTIFIER[asInt()];
	}
	
	public String asString() {
		return NAMES[asInt()];
	}

	public static CCOnlineRefType parse(String strtype) throws OnlineRefFormatException {
		for (int i = 0; i < IDENTIFIER.length; i++) {
			if (IDENTIFIER[i].equalsIgnoreCase(strtype)) return CCOnlineRefType.values()[i];
		}
		
		throw new OnlineRefFormatException(strtype);
	}
}