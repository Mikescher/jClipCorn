package de.jClipCorn.database.databaseElement.columnTypes;

import java.util.regex.Pattern;

import javax.swing.Icon;

import de.jClipCorn.util.exceptions.OnlineRefFormatException;

public class CCOnlineReference {
	private static final Pattern REGEX_IMDB = Pattern.compile("^tt[0-9]+$"); //$NON-NLS-1$
	private static final Pattern REGEX_AMZN = Pattern.compile("^[A-Z0-9]+$"); //$NON-NLS-1$
	private static final Pattern REGEX_MVPT = Pattern.compile("^(movie|serie)/.+$"); //$NON-NLS-1$
	private static final Pattern REGEX_TMDB = Pattern.compile("^(movie|tv)/[0-9]+$"); //$NON-NLS-1$
	private static final Pattern REGEX_PROX = Pattern.compile("^[0-9]+$"); //$NON-NLS-1$
	private static final Pattern REGEX_MYAL = Pattern.compile("^[0-9]+$"); //$NON-NLS-1$
	
	public final CCOnlineRefType type;
	public final String id;
	
	public CCOnlineReference(CCOnlineRefType type, String id) {
		this.type = type;
		this.id = id;
	}

	public static CCOnlineReference createNone() {
		return new CCOnlineReference(CCOnlineRefType.NONE, ""); //$NON-NLS-1$
	}

	public static CCOnlineReference createIMDB(String id) {
		return new CCOnlineReference(CCOnlineRefType.IMDB, id);
	}

	public static CCOnlineReference createAmazon(String id) {
		return new CCOnlineReference(CCOnlineRefType.AMAZON, id);
	}

	public static CCOnlineReference createMoviepilot(String id) {
		return new CCOnlineReference(CCOnlineRefType.MOVIEPILOT, id);
	}

	public static CCOnlineReference createTMDB(String id) {
		return new CCOnlineReference(CCOnlineRefType.THEMOVIEDB, id);
	}

	public static CCOnlineReference createProxer(String id) {
		return new CCOnlineReference(CCOnlineRefType.PROXERME, id);
	}

	public static CCOnlineReference createMyAnimeList(String id) {
		return new CCOnlineReference(CCOnlineRefType.MYANIMELIST, id);
	}

	public String toSerializationString() {
		if (type == CCOnlineRefType.NONE) return ""; //$NON-NLS-1$
		return type.getIdentifier() + ":" + id; //$NON-NLS-1$
	}

	public static CCOnlineReference parse(String data) throws OnlineRefFormatException {
		if (data.isEmpty()) return CCOnlineReference.createNone();
		
		int idx = data.indexOf(':');

		String strtype = data.substring(0, idx);
		String strid = data.substring(idx+1);
		
		CCOnlineRefType type = CCOnlineRefType.parse(strtype);
		
		return new CCOnlineReference(type, strid);
	}

	public boolean isUnset() {
		return !isSet();
	}

	public boolean isSet() {
		return type != CCOnlineRefType.NONE & !id.trim().isEmpty();
	}

	@SuppressWarnings("nls")
	public String getURL() {
		switch (type) {
		case NONE:
			return null;
		case IMDB:
			return "http://www.imdb.com/title/" + id;
		case AMAZON:
			return "https://www.amazon.de/dp/" + id;
		case MOVIEPILOT:
			return "https://www.moviepilot.de/" + id;
		case THEMOVIEDB:
			return "https://www.themoviedb.org/" + id;
		case PROXERME:
			return "http://proxer.me/info/" + id;
		case MYANIMELIST:
			return "http://myanimelist.net/anime/" + id + "/";
		default:
			return null;
		}
	}

	public Icon getIcon() {
		return type.getIcon();
	}

	public Icon getIcon16x16() {
		return type.getIcon16x16();
	}

	public Icon getIconButton() {
		return type.getIconButton();
	}

	public boolean isValid() {
		switch (type) {
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

	@Override
	public int hashCode() {
		return id.hashCode() ^ 13 * type.asInt();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CCOnlineReference))
			return false;
		if (obj == this)
			return true;

		return ((CCOnlineReference)obj).id.equals(id) && ((CCOnlineReference)obj).type == type;
	}
}
