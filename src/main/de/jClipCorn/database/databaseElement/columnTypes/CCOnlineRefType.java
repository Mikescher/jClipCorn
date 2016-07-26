package de.jClipCorn.database.databaseElement.columnTypes;

import java.util.regex.Pattern;

import javax.swing.Icon;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.exceptions.OnlineRefFormatException;

public enum CCOnlineRefType {
	NONE(0),
	IMDB(1),
	AMAZON(2),
	MOVIEPILOT(3),
	THEMOVIEDB(4),
	PROXERME(5),
	MYANIMELIST(6);
	
	private static final Pattern REGEX_IMDB = Pattern.compile("^tt[0-9]+$"); //$NON-NLS-1$
	private static final Pattern REGEX_AMZN = Pattern.compile("^[A-Z0-9]+$"); //$NON-NLS-1$
	private static final Pattern REGEX_MVPT = Pattern.compile("^(movies|serie)/.+$"); //$NON-NLS-1$
	private static final Pattern REGEX_TMDB = Pattern.compile("^(movie|tv)/[0-9]+$"); //$NON-NLS-1$
	private static final Pattern REGEX_PROX = Pattern.compile("^[0-9]+$"); //$NON-NLS-1$
	private static final Pattern REGEX_MYAL = Pattern.compile("^[0-9]+$"); //$NON-NLS-1$
	
	private final static String IDENTIFIER[] = {
		"",   			//$NON-NLS-1$
		"imdb",   		//$NON-NLS-1$
		"amzn",   		//$NON-NLS-1$
		"mvpt",  		//$NON-NLS-1$
		"tmdb",   		//$NON-NLS-1$
		"prox",   		//$NON-NLS-1$
		"myal",   		//$NON-NLS-1$
	};
	
	private final static String NAMES[] = {
		LocaleBundle.getString("CCOnlineRefType.NONE"),	//$NON-NLS-1$
		LocaleBundle.getString("CCOnlineRefType.IMDB"), //$NON-NLS-1$
		LocaleBundle.getString("CCOnlineRefType.AMAZON"), //$NON-NLS-1$
		LocaleBundle.getString("CCOnlineRefType.MOVIEPILOT"), //$NON-NLS-1$
		LocaleBundle.getString("CCOnlineRefType.THEMOVIEDB"), //$NON-NLS-1$
		LocaleBundle.getString("CCOnlineRefType.PROXERME"), //$NON-NLS-1$
		LocaleBundle.getString("CCOnlineRefType.MYANIMELIST"), //$NON-NLS-1$
	};
	
	private int id;
	
	private CCOnlineRefType(int val) {
		id = val;
	}
	
	public int asInt() {
		return id;
	}
	
	public static CCOnlineRefType find(int val) {
		if (val >= 0 && val < CCOnlineRefType.values().length) {
			return CCOnlineRefType.values()[val]; // Geht nur wenn alle Zahlen nach der Reihe da sind
		}
		return null;
	}

	public static int compare(CCMovieScore s1, CCMovieScore s2) {
		return Integer.compare(s1.asInt(), s2.asInt());
	}
	
	public static String[] getList() {
		return NAMES;
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

	public Icon getIcon() {
		switch (this) {
		case NONE:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_0_BIG);
		case IMDB:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_1_BIG);
		case AMAZON:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_2_BIG);
		case MOVIEPILOT:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_3_BIG);
		case THEMOVIEDB:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_4_BIG);
		case PROXERME:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_5_BIG);
		case MYANIMELIST:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_6_BIG);
		default:
			return null;
		}
	}

	public Icon getIcon16x16() {
		switch (this) {
		case NONE:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_0_SMALL);
		case IMDB:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_1_SMALL);
		case AMAZON:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_2_SMALL);
		case MOVIEPILOT:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_3_SMALL);
		case THEMOVIEDB:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_4_SMALL);
		case PROXERME:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_5_SMALL);
		case MYANIMELIST:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_6_SMALL);
		default:
			return null;
		}
	}

	public Icon getIconButton() {
		switch (this) {
		case NONE:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_0_BUTTON);
		case IMDB:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_1_BUTTON);
		case AMAZON:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_2_BUTTON);
		case MOVIEPILOT:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_3_BUTTON);
		case THEMOVIEDB:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_4_BUTTON);
		case PROXERME:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_5_BUTTON);
		case MYANIMELIST:
			return CachedResourceLoader.getImageIcon(Resources.ICN_REF_6_BUTTON);
		default:
			return null;
		}
	}
	
	public boolean isValid(String id) {
		switch (this) {
		case NONE:
			return id.isEmpty();
		case IMDB:
			return REGEX_IMDB.matcher(id).matches();
		case AMAZON:
			return REGEX_AMZN.matcher(id).matches();
		case MOVIEPILOT:
			return REGEX_MVPT.matcher(id).matches();
		case THEMOVIEDB:
			return REGEX_TMDB.matcher(id).matches();
		case PROXERME:
			return REGEX_PROX.matcher(id).matches();
		case MYANIMELIST:
			return REGEX_MYAL.matcher(id).matches();
		default:
			return false;
		}
	}

	public static CCOnlineRefType guessType(String id) {
		CCOnlineRefType result = null;
		
		for (CCOnlineRefType reftype : values()) {
			if (reftype.isValid(id)) {
				if (result != null) return null;
				result = reftype;
			}
		}
		return result;
	}
}