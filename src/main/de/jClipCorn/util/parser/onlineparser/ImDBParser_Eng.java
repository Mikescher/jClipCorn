package de.jClipCorn.util.parser.onlineparser;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
public class ImDBParser_Eng {
	public final static String BASE_URL = "http://www.imdb.com";
	
	private final static String SEARCH_URL_A = "/find?s=tt&q=%s";
	private final static String SEARCH_URL_M = "/find?s=tt&ttype=ft&q=%s";
	private final static String SEARCH_URL_S = "/find?s=tt&ttype=tv&q=%s";
	
	private final static String REGEX_FSK_BRACKETS = "\\([^\\)]*\\)";           // \([^\)]*\)
	private final static String REGEX_COVER_URL = "/media/rm[0-9]+?/tt[0-9]+";  // /media/rm[0-9]+?/tt[0-9]+
	private final static String REGEX_SEARCH_URL = "/title/tt[0-9]+/";          // /title/tt[0-9]+/
	private final static String REGEX_ALT_YEAR = ".*\\(([0-9]{4})\\)";          // .*\(([0-9]{4})\)
	private final static String REGEX_YEAR_SIMPLE = "\\([12][0-9]{3}\\)";       // \([12][0-9]{3}\)
	
	private final static String FSK_STANDARD_1 = "Germany";
	private final static String FSK_STANDARD_2 = "West Germany";
	private final static String FSK_URL = "%sparentalguide";
	
	private final static String JSOUP_SEARCH_HTML_A = "a[href~=/title/tt[0-9]+/.*]:matches(.+)";
	private final static String JSOUP_TITLE = "h1.header span[itemprop=name][class=itemprop]";
	private final static String JSOUP_YEAR = "h1.header span.nobr:matches(\\([12][0-9]{3}\\))"; // h1.header span.nobr:matches(\([12][0-9]{3}\))
	private final static String JSOUP_RATING = "span[itemprop=ratingValue]:matches([0-9][,\\.]?[0-9]?)"; // span[itemprop=ratingValue]:matches([0-9][,\.]?[0-9]?)
	private final static String JSOUP_LENGTH = "time[itemprop=duration]:matches([0-9]+ (M|m)in)";
	private final static String JSOUP_LENGTH_2 = "time[itemprop=duration]:matches(([0-9]+)[Hh] ([0-9]+)[Mm]in)";
	private final static String JSOUP_FSK = "h5:matches(Certification.*) + div.info-content a[href]";
	private final static String JSOUP_GENRE = "div[itemprop=genre] a";
	private final static String JSOUP_COVER = "div[class=image] a[href~=/media/rm[0-9]+?/tt[0-9]+.*]";
	private final static String JSOUP_COVER_DIRECT = "img#primary-img[src]";

	private final static String JSOUP_ALT_TITLE = "h1[itemprop=name]";
	private final static String JSOUP_ALT_YEAR = "meta[property=og:title][content~=.*\\([0-9]{4}\\)]"; // meta[property=og:title][content~=.*\([0-9]{4}\)]
	private final static String JSOUP_ALT_COVER = "div[class=poster] a[href~=/media/rm[0-9]+?/tt[0-9]+.*]";
	private final static String JSOUP_ALT_COVER_2 = "div[class=poster] a img[src*=/images/]";
	
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
		Document doc = Jsoup.parse(html);
		
		Elements searchresults = doc.select(JSOUP_SEARCH_HTML_A);
		
		List<DoubleString> result = new ArrayList<>();
		
		for (Element sresult : searchresults) {
			result.add(new DoubleString(BASE_URL + RegExHelper.find(REGEX_SEARCH_URL, sresult.attr("href")), sresult.text()));
		}
		
		removeDuplicate(result);
		
		return result;
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
		String cnt = getContentBySelector(html, JSOUP_TITLE);
		
		if (cnt.isEmpty()) {
			cnt = getContentBySelector(html, JSOUP_ALT_TITLE);
			cnt = RegExHelper.replace(REGEX_YEAR_SIMPLE, cnt, "");
			cnt = cnt.trim();
		}
		
		return cnt;
	}
	
	public static int getYear(String html) {
		String y = StringUtils.strip(getContentBySelector(html, JSOUP_YEAR).trim(), "()");
		
		try {
			return Integer.parseInt(y);
		} catch (NumberFormatException e) {
			y = getAttrBySelector(html, JSOUP_ALT_YEAR, "content").trim();
			
			y = RegExHelper.find(REGEX_ALT_YEAR, y, 1);
			if (! y.isEmpty()) {
				return Integer.parseInt(y);
			}
			
			return 0;
		}
	}
	
	public static int getRating(String html) {
		String y = getContentBySelector(html, JSOUP_RATING);
		
		try {
			return (int) Math.round(Double.parseDouble(y.replace(',', '.')));
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public static int getLength(String html) {
		String y = RegExHelper.find("[0-9]*", getContentBySelector(html, JSOUP_LENGTH));
		
		if (y == null || "".equals(y)){
			y = getContentBySelector(html, JSOUP_LENGTH_2);

			String sh = RegExHelper.find("([0-9]+)[Hh] ([0-9]+)[Mm]in", y, 1);
			String sm = RegExHelper.find("([0-9]+)[Hh] ([0-9]+)[Mm]in", y, 2);
			
			try {
				return Integer.parseInt(sh)*60 + Integer.parseInt(sm);
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		
		try {
			return Integer.parseInt(y);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public static Map<String, Integer> getFSKList(String url) {
		url = String.format(FSK_URL, url);
		
		String html = HTTPUtilities.getHTML(url, true, false);
		
		List<String> genarr = getContentListBySelector(html, JSOUP_FSK);
		
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
				CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotFindFSK", url));
				return null;
			}
			
			sum = Math.round(sum/count);
			
			return CCMovieFSK.getNearest((int) sum);
		}
	}
	
	public static CCMovieGenreList getGenres(String html) {
		List<String> regfind = getContentListBySelector(html, JSOUP_GENRE);
		
		CCMovieGenreList result = new CCMovieGenreList();
		
		for (String ng : regfind) {
			CCMovieGenre genre = CCMovieGenre.parseFromIMDBName(ng.trim());
			if (! genre.isEmpty()) {
				result.addGenre(genre);
			}
		}
		
		return result;
	}
	
	public static BufferedImage getCover(String html) {
		String find = RegExHelper.find(REGEX_COVER_URL, getAttrBySelector(html, JSOUP_COVER, "href"));
		
		if (find.isEmpty()) {
			find = RegExHelper.find(REGEX_COVER_URL, getAttrBySelector(html, JSOUP_ALT_COVER, "href"));
		}
		
		if (find.isEmpty()) {
			String url = getAttrBySelector(html, JSOUP_ALT_COVER_2, "src");
			if (url != null) {
				int urlStart = url.lastIndexOf("@@");
				int urlEnd = url.lastIndexOf(".");
				if (!url.isEmpty() && urlStart > 0 && urlEnd > 0) {
					find = url.replace(url.substring(urlStart+2, urlEnd), "");
					
					return HTTPUtilities.getImage(find);
				}

				urlStart = url.lastIndexOf("@");
				if (!url.isEmpty() && urlStart > 0 && urlEnd > 0) {
					find = url.replace(url.substring(urlStart+2, urlEnd), "");
					
					return HTTPUtilities.getImage(find);
				}
			}
		}
			
		String cpageurl = BASE_URL + find;
		
		String cpagehtml = HTTPUtilities.getHTML(cpageurl, true, false);
		
		if (cpagehtml.isEmpty()) {
			return null;
		}
		
		return getCoverDirekt(cpagehtml);
	}

	public static BufferedImage getCoverDirekt(String html) {
		String curl = getAttrBySelector(html, JSOUP_COVER_DIRECT, "src");
		
		if (curl.trim().isEmpty()) {
			return null;
		}
		
		BufferedImage result = HTTPUtilities.getImage(curl);
		
		if (result == null) {
			return null;
		}

		return result;
	}
	
	public static String getContentBySelector(String html, String cssQuery) {
		Element e = Jsoup.parse(html).select(cssQuery).first();
		return (e == null) ? "" : e.text();
	}
	
	public static String getAttrBySelector(String html, String cssQuery, String attr) {
		Element e = Jsoup.parse(html).select(cssQuery).first();
		return (e == null) ? "" : e.attr(attr);
	}
	
	public static List<String> getContentListBySelector(String html, String cssQuery) {
		Elements elst = Jsoup.parse(html).select(cssQuery);
		
		ArrayList<String> result = new ArrayList<>();
		
		for (Element e : elst) {
			result.add(e.text());
		}
		
		return result;
	}
}
