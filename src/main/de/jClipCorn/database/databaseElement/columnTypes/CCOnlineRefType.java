package de.jClipCorn.database.databaseElement.columnTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Tuple;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.exceptions.OnlineRefFormatException;

public enum CCOnlineRefType implements ContinoousEnum<CCOnlineRefType> {
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

	private static final Pattern REGEX_PASTE_IMDB = Pattern.compile("^(http://|https://)?(www\\.)?imdb\\.(com|de)/title/(?<id>tt[0-9]+)(/.*)?$"); //$NON-NLS-1$
	private static final Pattern REGEX_PASTE_TMDB = Pattern.compile("^(http://|https://)?(www\\.)?themoviedb\\.org/(?<id>(movie|tv)/[0-9]+)(\\-.*)?(/.*)?$"); //$NON-NLS-1$
	private static final Pattern REGEX_PASTE_MYAL = Pattern.compile("^(http://|https://)?(www\\.)?myanimelist\\.net/anime/(?<id>[0-9]+)(/.*)?$"); //$NON-NLS-1$
	private static final Pattern REGEX_PASTE_PROX = Pattern.compile("^(http://|https://)?(www\\.)?proxer\\.me/info/(?<id>[0-9]+)(/.*)?$"); //$NON-NLS-1$
	
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

	private static EnumWrapper<CCOnlineRefType> wrapper = new EnumWrapper<>(NONE);

	private CCOnlineRefType(int val) {
		id = val;
	}
	
	public static EnumWrapper<CCOnlineRefType> getWrapper() {
		return wrapper;
	}
	
	@Override
	public int asInt() {
		return id;
	}

	public static int compare(CCUserScore s1, CCUserScore s2) {
		return Integer.compare(s1.asInt(), s2.asInt());
	}
	
	@Override
	public String[] getList() {
		return NAMES;
	}
	
	public String getIdentifier() {
		return IDENTIFIER[asInt()];
	}
	
	@Override
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
			return CachedResourceLoader.getIcon(Resources.ICN_REF_0.icon32x32);
		case IMDB:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_1.icon32x32);
		case AMAZON:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_2.icon32x32);
		case MOVIEPILOT:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_3.icon32x32);
		case THEMOVIEDB:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_4.icon32x32);
		case PROXERME:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_5.icon32x32);
		case MYANIMELIST:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_6.icon32x32);
		default:
			return null;
		}
	}

	public Icon getIcon16x16() {
		switch (this) {
		case NONE:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_0.icon16x16);
		case IMDB:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_1.icon16x16);
		case AMAZON:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_2.icon16x16);
		case MOVIEPILOT:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_3.icon16x16);
		case THEMOVIEDB:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_4.icon16x16);
		case PROXERME:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_5.icon16x16);
		case MYANIMELIST:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_6.icon16x16);
		default:
			return null;
		}
	}

	public Icon getIconButton() {
		switch (this) {
		case NONE:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_0_BUTTON);
		case IMDB:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_1_BUTTON);
		case AMAZON:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_2_BUTTON);
		case MOVIEPILOT:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_3_BUTTON);
		case THEMOVIEDB:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_4_BUTTON);
		case PROXERME:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_5_BUTTON);
		case MYANIMELIST:
			return CachedResourceLoader.getIcon(Resources.ICN_REF_6_BUTTON);
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

	@SuppressWarnings("nls")
	public static Tuple<CCOnlineRefType, String> extractType(String input) {
		Matcher matcher;
		
		matcher = REGEX_PASTE_IMDB.matcher(input);
		if (matcher.find()) {
			return Tuple.Create(CCOnlineRefType.IMDB, matcher.group("id"));
		}
		
		matcher = REGEX_PASTE_TMDB.matcher(input);
		if (matcher.find()) {
			return Tuple.Create(CCOnlineRefType.THEMOVIEDB, matcher.group("id"));
		}
		
		matcher = REGEX_PASTE_MYAL.matcher(input);
		if (matcher.find()) {
			return Tuple.Create(CCOnlineRefType.MYANIMELIST, matcher.group("id"));
		}
		
		matcher = REGEX_PASTE_PROX.matcher(input);
		if (matcher.find()) {
			return Tuple.Create(CCOnlineRefType.PROXERME, matcher.group("id"));
		}
		
		return null;
	}

	@Override
	public CCOnlineRefType[] evalues() {
		return CCOnlineRefType.values();
	}
}