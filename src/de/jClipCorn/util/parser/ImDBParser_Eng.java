package de.jClipCorn.util.parser;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.DoubleString;
import de.jClipCorn.util.helper.HTTPUtilities;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.helper.RegExHelper;

@SuppressWarnings("nls")
public class ImDBParser_Eng {
	public final static String BASE_URL = "http://www.imdb.com";
	
	private final static String SEARCH_URL_A = "/find?s=tt&q=%s";
	private final static String SEARCH_URL_M = "/find?s=tt&ttype=ft&q=%s";
	private final static String SEARCH_URL_S = "/find?s=tt&ttype=tv&q=%s";
	private final static String REGEX_SEARCH_HTMl_A = "<a href[ ]*=[ ]*\"/title/tt[0-9]+/[^>]+\"[ ]*>(?![ ]*<.+>).+?</a>"; // <a href[ ]*=[ ]*"/title/tt[0-9]+/[^>]+"[ ]*>(?![ ]*<.+>).+?</a>
	private final static String REGEX_SEARCH_HREF_URL = "/title/tt[0-9]+/"; // /title/tt[0-9]+/
	private final static String REGEX_SEARCH_HREF_NAME = "[^>]+(?=(</a>))"; // [^>]+(?=(</a>))
	
	private final static String REGEX_TITLE = "(?<=<title>)(.+?)(?= \\([12][0-9]{3}\\) - IMDb</title>)"; // (?<=<title>)(.+?)(?= \([12][0-9]{3}\) - IMDb</title>)
	private final static String REGEX_YEAR = "(?<=\\()[12][0-9]{3}(?=\\) - IMDb</title>)"; // (?<=\()[12][0-9]{3}(?=\) - IMDb</title>)
	private final static String REGEX_RATING = "(?<=<span itemprop=\\\"ratingValue\\\">)[0-9][,\\.][0-9](?=</span>)"; // (?<=<span itemprop=\"ratingValue\">)[0-9][,\.][0-9](?=</span>)
	private final static String REGEX_LENGTH = "[0-9]+(?=( (M|m)in</time>))"; // [0-9]+(?=( (M|m)in</time>))
	private final static String REGEX_FSK = "(?<=Certification:</h5><div class=\"info-content\">).*?(?=</div>)"; // (?<=Certification:</h5><div class="info-content">).*?(?=</div>)
	private final static String REGEX_FSK_SPLIT = "(?<=\">).*?(?=</a>)"; // (?<=">).*?(?=</a>)
	private final static String REGEX_FSK_BRACKETS = "\\([^\\)]*\\)"; // \([^\)]*\)
	private final static String FSK_STANDARD_1 = "Germany";
	private final static String FSK_STANDARD_2 = "West Germany";
	private final static String REGEX_GENRE = "(?<=<h4 class=\\\"inline\\\">Genres:</h4>).*?(?=</div>)"; // (?<=<h4 class=\"inline\">Genres:</h4>).*?(?=</div>)
	private final static String REGEX_GENRE_FIND = "(?<=> ).+?(?=</a>)"; // (?<=> ).+?(?=</a>)
	private final static String REGEX_COVER = "(?<=<div class=\"image\"><a href=\")/media/rm[0-9]+?/tt[0-9]+?(?=\\?)"; // (?<=<div class="image"><a href=")/media/rm[0-9]+?/tt[0-9]+?(?=\?)
	private final static String REGEX_COVER_DIREKT_1 = "<img id=\"primary-img\"[^>]+src=\"[^\"]+\"[^>]*\\>"; // <img id="primary-img"[^>]+src="[^"]"[^>]\>
	private final static String REGEX_COVER_DIREKT_2 = "(?<=src=\")[^\"]+(?=\")"; // (?<=src=")[^"]+(?=")
	private final static String FSK_URL = "%sparentalguide";
	
	public static String getSearchURL(String title, CCMovieTyp typ) {
		if (typ == null) {
			return String.format(BASE_URL + SEARCH_URL_A, HTTPUtilities.escapeURL(title));
		}
		
		switch (typ) {
		case MOVIE:
			return String.format(BASE_URL + SEARCH_URL_M, HTTPUtilities.escapeURL(title));
		case SERIES:
			return String.format(BASE_URL + SEARCH_URL_S, HTTPUtilities.escapeURL(title));
		default:
			return null;
		}
	}
	
	public static List<DoubleString> extractImDBLinks(String html) {
		List<DoubleString> result = new ArrayList<>();
		
		Pattern pat = Pattern.compile(REGEX_SEARCH_HTMl_A);
		
		Matcher matcher = pat.matcher(html);
		
		while (matcher.find()) {
			result.add(new DoubleString(getLinkFromHREF(matcher.group()), getNameFromHREF(matcher.group())));
		}
		
		removeDuplicate(result);
		
		return result;
	}
	
	private static String getLinkFromHREF(String href) {
		return BASE_URL + RegExHelper.find(REGEX_SEARCH_HREF_URL, href.trim()).trim();
	}
	
	private static String getNameFromHREF(String href) {
		return RegExHelper.find(REGEX_SEARCH_HREF_NAME, href.trim()).trim();
	}
	
	private static <T> void removeDuplicate(List<T> arlList) {
		Set<T> set = new HashSet<>();
		List<T> newList = new ArrayList<>();
		
		for (Iterator<T> iter = arlList.iterator(); iter.hasNext();) {
			T element = iter.next();
			if (set.add(element)) {
				newList.add(element);
			}
		}
		
		arlList.clear();
		arlList.addAll(newList);
	}
	
	public static String getTitle(String html) {
		return RegExHelper.find(REGEX_TITLE, html.trim()).trim();
	}
	
	public static int getYear(String html) {
		String y = RegExHelper.find(REGEX_YEAR, html.trim()).trim();
		
		try {
			return Integer.parseInt(y);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public static int getRating(String html) {
		String y = RegExHelper.find(REGEX_RATING, html.trim()).trim();
		
		try {
			return (int) Math.round(Double.parseDouble(y.replace(',', '.')));
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public static int getLength(String html) {
		String y = RegExHelper.find(REGEX_LENGTH, html.trim()).trim();
		
		try {
			return Integer.parseInt(y);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public static Map<String, Integer> getFSKList(String url) {
		url = String.format(FSK_URL, url);
		
		String html = HTTPUtilities.getHTML(url, true);
		
		String allg = RegExHelper.find(REGEX_FSK, html);
		List<String> genarr = RegExHelper.findAll(REGEX_FSK_SPLIT, allg);
		
		HashMap<String, Integer> genmap = new HashMap<>();
		
		for (String s : genarr) {
			String[] split = s.split(":");
			
			if (split.length != 2) {
				continue;
			}
			
			split[1].replaceAll(REGEX_FSK_BRACKETS, "");

			int ihf = AgeRatingParser.getMinimumAge(split[1], url);

			if (ihf >= 0) {
				genmap.put(split[0], ihf);
			}
		}
		
		return genmap;
	}
	
	public static CCMovieFSK getFSK(String url) {
		Map<String, Integer> genmap = getFSKList(url);
		
		if (genmap.get(FSK_STANDARD_1) != null) {
			return CCMovieFSK.getNearest(genmap.get(FSK_STANDARD_1));
		} else if (genmap.get(FSK_STANDARD_2) != null) {
			return CCMovieFSK.getNearest(genmap.get(FSK_STANDARD_2));
		} else { // Get Average from all other Countries
			int count = 0;
			double sum = 0;
			for (Integer geni: genmap.values()) {
				count++;
				sum += geni;
			}
			
			if (count <= 0) {
				String regexhtmlfsk = RegExHelper.find(REGEX_FSK, url).trim();
				if (! regexhtmlfsk.isEmpty()) {
					CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotFindFSK", RegExHelper.find(REGEX_FSK, url)));
				}
				return null;
			}
			
			sum = Math.round(sum/count);
			
			return CCMovieFSK.getNearest((int) sum);
		}
	}
	
	public static CCMovieGenreList getGenres(String html) {
		String regfind = RegExHelper.find(REGEX_GENRE, html);
		
		CCMovieGenreList result = new CCMovieGenreList();
		
		List<String> genres = RegExHelper.findAll(REGEX_GENRE_FIND, regfind);
		
		for (String ng : genres) {
			CCMovieGenre genre = CCMovieGenre.parseFromIMDBName(ng.trim());
			if (! genre.isEmpty()) {
				result.addGenre(genre);
			}
		}
		
		return result;
	}
	
	public static BufferedImage getCover(String html) {
		String cpageurl = BASE_URL + RegExHelper.find(REGEX_COVER, html);
		
		String cpagehtml = HTTPUtilities.getHTML(cpageurl, true);
		
		if (cpagehtml.isEmpty()) {
			return null;
		}
		
		return getCoverDirekt(cpagehtml);
	}

	public static BufferedImage getCoverDirekt(String html) {
		String curl = RegExHelper.find(REGEX_COVER_DIREKT_1, html);
		curl = RegExHelper.find(REGEX_COVER_DIREKT_2, curl);
		
		if (curl.trim().isEmpty()) {
			return null;
		}
		
		BufferedImage result = HTTPUtilities.getImage(curl);
		
		if (result == null) {
			return null;
		}
		
		result = ImageUtilities.resizeCoverImage(result);
		
		return result;
	}
}
