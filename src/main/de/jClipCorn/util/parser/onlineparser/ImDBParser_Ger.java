package de.jClipCorn.util.parser.onlineparser;

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
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.datatypes.DoubleString;
import de.jClipCorn.util.helper.HTTPUtilities;
import de.jClipCorn.util.helper.RegExHelper;

@SuppressWarnings("nls")
public class ImDBParser_Ger {
	public final static String BASE_URL = "http://www.imdb.de";
	
	private final static String SEARCH_URL_A = "/find?s=tt&q=%s";
	private final static String SEARCH_URL_M = "/find?s=tt&ttype=ft&q=%s";
	private final static String SEARCH_URL_S = "/find?s=tt&ttype=tv&q=%s";
	private final static String REGEX_SEARCH_HTMl_A = "<a href[ ]*=[ ]*\"/title/tt[0-9]+/[^>]+\"[ ]*>(?![ ]*<.+>).+?</a>"; // <a href[ ]*=[ ]*"/title/tt[0-9]+/[^>]+"[ ]*>(?![ ]*<.+>).+?</a>
	private final static String REGEX_SEARCH_HREF_URL = "/title/tt[0-9]+/"; // /title/tt[0-9]+/
	private final static String REGEX_SEARCH_HREF_NAME = "[^>]+(?=(</a>))"; // [^>]+(?=(</a>))
	
	private final static String REGEX_TITLE = "(?<=<title>)(.+?)(?= \\([12][0-9]{3}\\)</title>)"; // (?<=<title>)(.+?)(?= \([12][0-9]{3}\)</title>)
	private final static String REGEX_YEAR = "(?<=\\()[12][0-9]{3}(?=\\)</title>)"; // (?<=\()[12][0-9]{3}(?=\)</title>
	private final static String REGEX_RATING = "(?<=(<b>))[0-9],[0-9](?=(/10</b>))"; // (?<=(<b>))[0-9],[0-9](?=(/10</b>))
	private final static String REGEX_LENGTH = "[0-9]+(?=( (M|m)in( |.)</div>))"; // [0-9]+(?=( (M|m)in( |.)</div>))
	private final static String REGEX_FSK = "(?<=(Altersfreigabe:</h5><div class=\"info-content\">))(.|\\s)*?(?=</div>)"; // (?<=(Altersfreigabe:</h5><div class="info-content">))(.|\s)*?(?=</div>)
	private final static String REGEX_FSK_SPLIT = "[\\s]*?\\|[\\s]*"; // [\s]*?\|[\s]*
	private final static String REGEX_FSK_BRACKETS = "\\([^\\)]*\\)"; // \([^\)]*\)
	private final static String REGEX_FSK_SHRINK = "<i>.*?</i>"; // <i>.*?</i>
	private final static String FSK_STANDARD_1 = "Deutschland";
	private final static String FSK_STANDARD_2 = "Bundesrepublik Deutschland";
	private final static String REGEX_GENRE = "(?<=<h5>Genre:</h5><div class=\"info-content\">)[^>]+(?=</div>)"; // (?<=<h5>Genre:</h5><div class="info-content">)[^>]+(?=</div>)
	private final static String REGEX_GENRE_SPLIT = "[ ]+\\|[ ]+"; // [ ]+\|[ ]+
	private final static String REGEX_COVER = "(?<=<a\\x20name=\"poster\"\\x20href=\")/rg/action-box-title/primary-photo/media/rm[0-9]+/tt[0-9]+(?=\"\\x20title=\")"; // (?<=<a\x20name="poster"\x20href=")/rg/action-box-title/primary-photo/media/rm[0-9]+/tt[0-9]+(?="\x20title=")
	private final static String REGEX_COVER_DIREKT_1 = "id=\"primary-img\"[^>]+src=\"[^\"]+\"[^>]*\\>"; // <img id="primary-img"[^>]+src="[^"]"[^>]\>
	private final static String REGEX_COVER_DIREKT_2 = "(?<=src=\")[^\"]+(?=\")"; // (?<=src=")[^"]+(?=")
	
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
	
	public static String getURL(CCOnlineReference ref) {
		return BASE_URL + "/title/" + ref.id;
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
	
	public static Map<String, Integer> getFSKList(String html, String url) {
		String allg = RegExHelper.find(REGEX_FSK, html);
		String[] genarr = allg.split(REGEX_FSK_SPLIT);
		
		HashMap<String, Integer> genmap = new HashMap<>();
		
		for (int i = 0; i < genarr.length; i++) {
			String s = genarr[i].replaceAll(REGEX_FSK_SHRINK, "").trim();
			
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
	
	public static CCMovieFSK getFSK(String html, String url) {
		Map<String, Integer> genmap = getFSKList(html, url);
		
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
				String regexhtmlfsk = RegExHelper.find(REGEX_FSK, html).trim();
				if (! regexhtmlfsk.isEmpty()) {
					CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotFindFSK", RegExHelper.find(REGEX_FSK, html)));
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
		
		String[] genres = regfind.split(REGEX_GENRE_SPLIT);
		
		for (String ng : genres) {
			CCMovieGenre genre = CCMovieGenre.parseFromIMDBName(ng);
			if (! genre.isEmpty()) {
				result.addGenre(genre);
			}
		}
		
		return result;
	}
	
	public static BufferedImage getCover(String html) {
		String cpageurl = BASE_URL + RegExHelper.find(REGEX_COVER, html);
		
		String cpagehtml = HTTPUtilities.getHTML(cpageurl, true, false);
		
		if (cpagehtml.isEmpty()) {
			return null;
		}
		
		return getCoverDirekt(cpagehtml);
	}

	public static BufferedImage getCoverDirekt(String html) {
		String curl = RegExHelper.find(REGEX_COVER_DIREKT_1, html);
		curl = RegExHelper.find(REGEX_COVER_DIREKT_2, curl);
		
		BufferedImage result = HTTPUtilities.getImage(curl);
		
		if (result == null) {
			return null;
		}

		return result;
	}
}
