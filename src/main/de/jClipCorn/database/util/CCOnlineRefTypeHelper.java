package de.jClipCorn.database.util;

import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.BrowserLanguage;

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
	public static final Pattern REGEX_ANDB = Pattern.compile("^[0-9]+$");                         //$NON-NLS-1$
	public static final Pattern REGEX_TVDB = Pattern.compile("^[0-9A-Za-z\\-]+$");                //$NON-NLS-1$
	public static final Pattern REGEX_MAZE = Pattern.compile("^[0-9]+$");                         //$NON-NLS-1$
	public static final Pattern REGEX_WKDE = Pattern.compile("^[^;:/?#]+$");                      //$NON-NLS-1$
	public static final Pattern REGEX_WKEN = Pattern.compile("^[^;:/?#]+$");                      //$NON-NLS-1$
	public static final Pattern REGEX_OFDB = Pattern.compile("^[0-9]+$");                         //$NON-NLS-1$

	public static final Pattern REGEX_PASTE_IMDB = Pattern.compile("^(https?://)?(www\\.)?imdb\\.(com|de)/title/(?<id>tt[0-9]+)(/.*)?(\\?.*)?(#.*)?$");                                             //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_MVPT = Pattern.compile("^(https?://)?(www\\.)?moviepilot\\.de/(?<id>(movies|serie)/[0-9A-Za-z\\-]+)(/.*)?(\\?.*)?(#.*)?$");                             //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_TMDB = Pattern.compile("^(https?://)?(www\\.)?themoviedb\\.org/(?<id>(movie|tv)/[0-9]+)(-.*)?(/.*)?(\\?.*)?(#.*)?$");                                   //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_MYAL = Pattern.compile("^(https?://)?(www\\.)?myanimelist\\.net/anime/(?<id>[0-9]+)(/.*)?(\\?.*)?(#.*)?$");                                             //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_PROX = Pattern.compile("^(https?://)?(www\\.)?proxer\\.(me|net)/info/(?<id>[0-9]+)(/.*)?(\\?.*)?(#.*)?$");                                                    //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_ANIL = Pattern.compile("^(https?://)?(www\\.)?anilist\\.co/anime/(?<id>[0-9]+)(/.*)?(\\?.*)?(#.*)?$");                                                  //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_ANPL = Pattern.compile("^(https?://)?(www\\.)?anime-planet\\.com/anime/(?<id>[0-9A-Za-z\\-]+)(/.*)?(\\?.*)?(#.*)?$");                                   //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_KISU = Pattern.compile("^(https?://)?(www\\.)?kitsu\\.io/anime/(?<id>[0-9A-Za-z\\-]+)(/.*)?(\\?.*)?(#.*)?$");                                           //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_ANDB = Pattern.compile("^(https?://)?(www\\.)?anidb\\.net/((anime/)|(a)|(perl-bin/animedb\\.pl\\?show=anime&aid=))(?<id>[0-9]+)(/.*)?(\\?.*)?(#.*)?$"); //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_TVDB = Pattern.compile("^(https?://)?(www\\.)?thetvdb\\.com/series/(?<id>[0-9A-Za-z\\-]+)(/.*)?(\\?.*)?(#.*)?$");                                       //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_MAZE = Pattern.compile("^(https?://)?(www\\.)?tvmaze\\.com/shows/(?<id>[0-9]+)(/.*)?(\\?.*)?(#.*)?$");                                                  //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_WKDE = Pattern.compile("^(https?://)?(www\\.)?de.wikipedia\\.org/wiki/(?<id>[^;:/?#]+)(#.*)?$");                                                        //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_WKEN = Pattern.compile("^(https?://)?(www\\.)?en.wikipedia\\.org/wiki/(?<id>[^;:/?#]+)(#.*)?$");                                                        //$NON-NLS-1$
	public static final Pattern REGEX_PASTE_OFDB = Pattern.compile("^(https?://)?(www\\.)?(ssl\\.)?ofdb\\.de/film/(?<id>[0-9]+),.*(#.*)?$");                                                        //$NON-NLS-1$

	@SuppressWarnings("nls")
	public static String getURL(CCSingleOnlineReference ref) {
		BrowserLanguage lang = CCProperties.getInstance().PROP_TMDB_LANGUAGE.getValue();

		switch (ref.type) {
			case NONE:
				return null;
			case IMDB:
				return "https://www.imdb.com/title/" + ref.id;
			case AMAZON:
				return "https://www.amazon.com/dp/" + ref.id;
			case MOVIEPILOT:
				return "https://www.moviepilot.de/" + ref.id;
			case THEMOVIEDB:
				if (lang == BrowserLanguage.ENGLISH)
					return "https://www.themoviedb.org/" + ref.id;
				else
					return "https://www.themoviedb.org/" + ref.id + "?language=" + lang.asDinIsoID();
			case PROXERME:
				return "https://proxer.net/info/" + ref.id;
			case MYANIMELIST:
				return "https://myanimelist.net/anime/" + ref.id + "/";
			case ANILIST:
				return "https://anilist.co/anime/" + ref.id + "/";
			case ANIMEPLANET:
				return "https://www.anime-planet.com/anime/" + ref.id;
			case KITSU:
				return "https://kitsu.io/anime/" + ref.id;
			case ANIDB:
				return "https://anidb.net/a" + ref.id;
			case THETVDB:
				return "https://www.thetvdb.com/series/" + ref.id;
			case TVMAZE:
				return "https://www.tvmaze.com/shows/" + ref.id;
			case WIKIDE:
				return "https://de.wikipedia.org/wiki/" + ref.id;
			case WIKIEN:
				return "https://en.wikipedia.org/wiki/" + ref.id;
			case OFDB:
				return "https://ssl.ofdb.de/film/"+ ref.id + ",";
			default:
				CCLog.addDefaultSwitchError(ref, ref.type);
				return null;
		}
	}
}
