package de.jClipCorn.database.util;

import java.util.regex.Pattern;

public final class CCOnlineRefTypeHelper {
	public static final Pattern REGEX_NONE = Pattern.compile("^$");                               //$NON-NLS-1$
	public static final Pattern REGEX_IMDB = Pattern.compile("^tt[0-9]+$");                       //$NON-NLS-1$
	public static final Pattern REGEX_AMZN = Pattern.compile("^[0-9A-Za-z]+$");                   //$NON-NLS-1$
	public static final Pattern REGEX_MVPT = Pattern.compile("^(movies|serie)/[0-9A-Za-z\\-]+$"); //$NON-NLS-1$
	public static final Pattern REGEX_TMDB = Pattern.compile("^(movie|tv)/[0-9]+$");              //$NON-NLS-1$
	public static final Pattern REGEX_PROX = Pattern.compile("^[0-9]+$");                         //$NON-NLS-1$
	public static final Pattern REGEX_MYAL = Pattern.compile("^[0-9]+$");                         //$NON-NLS-1$
	public static final Pattern REGEX_ANIL = Pattern.compile("^[0-9]+$");                         //$NON-NLS-1$
	public static final Pattern REGEX_ANPL = Pattern.compile("^[0-9A-Za-z\\-]+$");                //$NON-NLS-1$
	public static final Pattern REGEX_KISU = Pattern.compile("^[0-9A-Za-z\\-]+$");                //$NON-NLS-1$

	public static final Pattern REGEX_PASTE_IMDB = Pattern.compile("^(http://|https://)?(www\\.)?imdb\\.(com|de)/title/(?<id>tt[0-9]+)(/.*)?(\\?.*)?$");                 //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_MVPT = Pattern.compile("^(http://|https://)?(www\\.)?moviepilot\\.de/(?<id>[movies|serie]/[0-9A-Za-z\\-]+)(/.*)?(\\?.*)?$"); //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_TMDB = Pattern.compile("^(http://|https://)?(www\\.)?themoviedb\\.org/(?<id>(movie|tv)/[0-9]+)(\\-.*)?(/.*)?(\\?.*)?$");     //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_MYAL = Pattern.compile("^(http://|https://)?(www\\.)?myanimelist\\.net/anime/(?<id>[0-9]+)(/.*)?(\\?.*)?$");                 //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_PROX = Pattern.compile("^(http://|https://)?(www\\.)?proxer\\.me/info/(?<id>[0-9]+)(/.*)?(\\?.*)?$");                        //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_ANIL = Pattern.compile("^(http://|https://)?(www\\.)?anilist\\.co/anime/(?<id>[0-9]+)(/.*)?(\\?.*)?$");                      //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_ANPL = Pattern.compile("^(http://|https://)?(www\\.)?anime\\-planet\\.com/anime/(?<id>[0-9A-Za-z\\-]+)(/.*)?(\\?.*)?$");     //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_KISU = Pattern.compile("^(http://|https://)?(www\\.)?kitsu\\.io/anime/(?<id>[0-9A-Za-z\\-]+)(/.*)?(\\?.*)?$");               //$NON-NLS-1$
}
