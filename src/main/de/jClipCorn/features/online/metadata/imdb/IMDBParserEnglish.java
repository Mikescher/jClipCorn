package de.jClipCorn.features.online.metadata.imdb;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.OnlineSearchType;
import de.jClipCorn.features.online.cover.imdb.AgeRatingParser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.helper.RegExHelper;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("nls")
public class IMDBParserEnglish extends IMDBParserCommon {
	public final static String BASE_URL = "http://www.imdb.com";
	
	private static final String SEARCH_URL_A = "/find?s=tt&q=%s";
	private static final String SEARCH_URL_M = "/find?s=tt&ttype=ft&q=%s";
	private static final String SEARCH_URL_S = "/find?s=tt&ttype=tv&q=%s";
	
	private static final String REGEX_FSK_BRACKETS = "\\([^\\)]*\\)";           // \([^\)]*\)
	private static final String REGEX_COVER_URL = "/media/rm[0-9]+?/tt[0-9]+";  // /media/rm[0-9]+?/tt[0-9]+
	private static final String REGEX_SEARCH_URL = "/title/tt[0-9]+/";          // /title/tt[0-9]+/
	private static final String REGEX_ALT_YEAR = ".*\\(([0-9]{4})\\)";          // .*\(([0-9]{4})\)
	private static final String REGEX_YEAR_SIMPLE = "\\([12][0-9]{3}\\)";       // \([12][0-9]{3}\)
	
	private static final String FSK_STANDARD_1 = "Germany";
	private static final String FSK_STANDARD_2 = "West Germany";
	
	private static final String JSOUP_SEARCH_HTML_A = "a[href~=/title/tt[0-9]+/.*]:matches(.+)";
	private static final String JSOUP_TITLE = "h1.header span[itemprop=name][class=itemprop]";
	private static final String JSOUP_YEAR = "h1.header span.nobr:matches(\\([12][0-9]{3}\\))"; // h1.header span.nobr:matches(\([12][0-9]{3}\))
	private static final String JSOUP_RATING = "span[itemprop=ratingValue]:matches([0-9][,\\.]?[0-9]?)"; // span[itemprop=ratingValue]:matches([0-9][,\.]?[0-9]?)
	private static final String JSOUP_LENGTH = "time[itemprop=duration]:matches([0-9]+ (M|m)in)";
	private static final String JSOUP_LENGTH_2 = "time[itemprop=duration]:matches(([0-9]+)[Hh] ([0-9]+)[Mm]in)";
	private static final String JSOUP_FSK = "h5:matches(Certification.*) + div.info-content a[href]";
	private static final String JSOUP_GENRE = "div[itemprop=genre] a";
	private static final String JSOUP_COVER = "div[class=image] a[href~=/media/rm[0-9]+?/tt[0-9]+.*]";
	public  static final String JSOUP_COVER_DIRECT = "img#primary-img[src]";

	private static final String JSOUP_ALT_TITLE = "h1[itemprop=name]";
	private static final String JSOUP_ALT_YEAR = "meta[property=og:title][content~=.*\\([0-9]{4}\\)]"; // meta[property=og:title][content~=.*\([0-9]{4}\)]
	private static final String JSOUP_ALT_COVER = "div[class=poster] a[href~=/media/rm[0-9]+?/tt[0-9]+.*]";
	private static final String JSOUP_ALT_COVER_2 = "div[class=poster] a img[src*=/images/]";

	public IMDBParserEnglish(CCMovieList ml) {
		super(ml);
	}

	@Override
	public String getSearchURL(String title, OnlineSearchType typ) {
		switch (typ) {
		case MOVIES:
			return String.format(BASE_URL + SEARCH_URL_M, HTTPUtilities.escapeURL(title));
		case SERIES:
			return String.format(BASE_URL + SEARCH_URL_S, HTTPUtilities.escapeURL(title));
		case BOTH:
			return String.format(BASE_URL + SEARCH_URL_A, HTTPUtilities.escapeURL(title));
		default:
			CCLog.addDefaultSwitchError(this, typ);
			return null;
		}
	}
	
	@Override
	protected String getURL(CCSingleOnlineReference ref) {
		return BASE_URL + "/title/" + ref.id;
	}
	
	@Override
	public List<Tuple<String, CCSingleOnlineReference>> extractImDBLinks(String html) {
		Document doc = Jsoup.parse(html);
		
		Elements searchresults = doc.select(JSOUP_SEARCH_HTML_A);
		
		List<Tuple<String, CCSingleOnlineReference>> result = new ArrayList<>();
		
		for (Element sresult : searchresults) {
			String name = sresult.text();
			String link = BASE_URL + RegExHelper.find(REGEX_SEARCH_URL, sresult.attr("href"));
			
			CCSingleOnlineReference ref = CCSingleOnlineReference.createIMDB(extractOnlineID(link));
			
			if (ref != null) result.add(Tuple.Create(name, ref));
		}
		
		return CCStreams.iterate(result).unique(p -> p.Item2).enumerate();
	}
	
	@Override
	protected String getTitle(String html) {
		String cnt = getContentBySelector(html, JSOUP_TITLE);
		
		if (cnt.isEmpty()) {
			cnt = getContentBySelector(html, JSOUP_ALT_TITLE);
			cnt = RegExHelper.replace(REGEX_YEAR_SIMPLE, cnt, "");
			cnt = cnt.trim();
		}
		
		return cnt;
	}
	
	@Override
	protected Integer getYear(String html) {
		String y = StringUtils.strip(getContentBySelector(html, JSOUP_YEAR).trim(), "()");
		
		try {
			return Integer.parseInt(y);
		} catch (NumberFormatException e) {
			y = getAttrBySelector(html, JSOUP_ALT_YEAR, "content").trim();
			
			y = RegExHelper.find(REGEX_ALT_YEAR, y, 1);
			if (! y.isEmpty()) {
				return Integer.parseInt(y);
			}
			
			return null;
		}
	}
	
	@Override
	protected Integer getRating(String html) {
		String y = getContentBySelector(html, JSOUP_RATING);
		
		try {
			return (int) Math.round(Double.parseDouble(y.replace(',', '.')));
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	@Override
	protected Integer getLength(String html) {
		String y = RegExHelper.find("[0-9]*", getContentBySelector(html, JSOUP_LENGTH));
		
		if (y == null || "".equals(y)){
			y = getContentBySelector(html, JSOUP_LENGTH_2);

			String sh = RegExHelper.find("([0-9]+)[Hh] ([0-9]+)[Mm]in", y, 1);
			String sm = RegExHelper.find("([0-9]+)[Hh] ([0-9]+)[Mm]in", y, 2);
			
			try {
				return Integer.parseInt(sh)*60 + Integer.parseInt(sm);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		
		try {
			return Integer.parseInt(y);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	@Override
	protected Map<String, Integer> getFSKList(String html, String url) {
		if (url.endsWith("/")) 
			url += "parentalguide";
		else
			url += "/parentalguide";
		
		String fsk_html = HTTPUtilities.getHTML(movielist, url, true, false);
		
		List<String> genarr = getContentListBySelector(fsk_html, JSOUP_FSK);
		
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
		
		if (genmap.isEmpty()) return null;
		return genmap;
	}

	@Override
	protected CCFSK getFSK(Map<String, Integer> genmap, String html, String url) {
		if (genmap.get(FSK_STANDARD_1) != null) {
			return CCFSK.getNearest(genmap.get(FSK_STANDARD_1));
		} else if (genmap.get(FSK_STANDARD_2) != null) {
			return CCFSK.getNearest(genmap.get(FSK_STANDARD_2));
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
			
			return CCFSK.getNearest((int) sum);
		}
	}
	
	@Override
	protected CCGenreList getGenres(String html) {
		List<String> regfind = getContentListBySelector(html, JSOUP_GENRE);

		List<CCGenre> genres = CCStreams
				.iterate(regfind)
				.map(c -> CCGenre.parseFromIMDBName(c.trim()))
				.flatten(CCStreams::iterate)
				.unique()
				.enumerate();
		
		return new CCGenreList(genres);
	}
	
	@Override
	public String getCoverURL(String html) {
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
					
					return find;
				}

				urlStart = url.lastIndexOf("@");
				if (!url.isEmpty() && urlStart > 0 && urlEnd > 0) {
					find = url.replace(url.substring(urlStart+2, urlEnd), "");
					
					return find;
				}
			}
		}
			
		String cpageurl = BASE_URL + find;
		
		String cpagehtml = HTTPUtilities.getHTML(movielist, cpageurl, true, false);
		
		if (cpagehtml.isEmpty()) {
			return null;
		}
		
		return getAttrBySelector(cpagehtml, JSOUP_COVER_DIRECT, "src");
	}
	
	private String getContentBySelector(String html, String cssQuery) {
		Element e = Jsoup.parse(html).select(cssQuery).first();
		return (e == null) ? "" : e.text();
	}
	
	private String getAttrBySelector(String html, String cssQuery, String attr) {
		Element e = Jsoup.parse(html).select(cssQuery).first();
		return (e == null) ? "" : e.attr(attr);
	}
	
	private List<String> getContentListBySelector(String html, String cssQuery) {
		Elements elst = Jsoup.parse(html).select(cssQuery);
		
		ArrayList<String> result = new ArrayList<>();
		
		for (Element e : elst) {
			result.add(e.text());
		}
		
		return result;
	}
}
