package de.jClipCorn.online.metadata.imdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.online.OnlineSearchType;
import de.jClipCorn.online.cover.imdb.AgeRatingParser;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.helper.RegExHelper;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.stream.CCStreams;

@SuppressWarnings("nls")
public class IMDBParserGerman extends IMDBParserCommon {
	public final static String BASE_URL = "http://www.imdb.de";
	
	private static final String SEARCH_URL_A = "/find?s=tt&q=%s";
	private static final String SEARCH_URL_M = "/find?s=tt&ttype=ft&q=%s";
	private static final String SEARCH_URL_S = "/find?s=tt&ttype=tv&q=%s";
	private static final String REGEX_SEARCH_HTMl_A = "<a href[ ]*=[ ]*\"/title/tt[0-9]+/[^>]+\"[ ]*>(?![ ]*<.+>).+?</a>"; // <a href[ ]*=[ ]*"/title/tt[0-9]+/[^>]+"[ ]*>(?![ ]*<.+>).+?</a>
	private static final String REGEX_SEARCH_HREF_URL = "/title/tt[0-9]+/"; // /title/tt[0-9]+/
	private static final String REGEX_SEARCH_HREF_NAME = "[^>]+(?=(</a>))"; // [^>]+(?=(</a>))
	
	private static final String REGEX_TITLE = "(?<=<title>)(.+?)(?= \\([12][0-9]{3}\\)</title>)"; // (?<=<title>)(.+?)(?= \([12][0-9]{3}\)</title>)
	private static final String REGEX_YEAR = "(?<=\\()[12][0-9]{3}(?=\\)</title>)"; // (?<=\()[12][0-9]{3}(?=\)</title>
	private static final String REGEX_RATING = "(?<=(<b>))[0-9],[0-9](?=(/10</b>))"; // (?<=(<b>))[0-9],[0-9](?=(/10</b>))
	private static final String REGEX_LENGTH = "[0-9]+(?=( (M|m)in( |.)</div>))"; // [0-9]+(?=( (M|m)in( |.)</div>))
	private static final String REGEX_FSK = "(?<=(Altersfreigabe:</h5><div class=\"info-content\">))(.|\\s)*?(?=</div>)"; // (?<=(Altersfreigabe:</h5><div class="info-content">))(.|\s)*?(?=</div>)
	private static final String REGEX_FSK_SPLIT = "[\\s]*?\\|[\\s]*"; // [\s]*?\|[\s]*
	private static final String REGEX_FSK_BRACKETS = "\\([^\\)]*\\)"; // \([^\)]*\)
	private static final String REGEX_FSK_SHRINK = "<i>.*?</i>"; // <i>.*?</i>
	private static final String FSK_STANDARD_1 = "Deutschland";
	private static final String FSK_STANDARD_2 = "Bundesrepublik Deutschland";
	private static final String REGEX_GENRE = "(?<=<h5>Genre:</h5><div class=\"info-content\">)[^>]+(?=</div>)"; // (?<=<h5>Genre:</h5><div class="info-content">)[^>]+(?=</div>)
	private static final String REGEX_GENRE_SPLIT = "[ ]+\\|[ ]+"; // [ ]+\|[ ]+
	private static final String REGEX_COVER = "(?<=<a\\x20name=\"poster\"\\x20href=\")/rg/action-box-title/primary-photo/media/rm[0-9]+/tt[0-9]+(?=\"\\x20title=\")"; // (?<=<a\x20name="poster"\x20href=")/rg/action-box-title/primary-photo/media/rm[0-9]+/tt[0-9]+(?="\x20title=")
	public  static final String REGEX_COVER_DIREKT_1 = "id=\"primary-img\"[^>]+src=\"[^\"]+\"[^>]*\\>"; // <img id="primary-img"[^>]+src="[^"]"[^>]\>
	public  static final String REGEX_COVER_DIREKT_2 = "(?<=src=\")[^\"]+(?=\")"; // (?<=src=")[^"]+(?=")
	
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
	public List<Tuple<String, CCOnlineReference>> extractImDBLinks(String html) {
		List<Tuple<String, CCOnlineReference>> result = new ArrayList<>();
		
		Pattern pat = Pattern.compile(REGEX_SEARCH_HTMl_A);
		
		Matcher matcher = pat.matcher(html);
		
		while (matcher.find()) {
			String name = getNameFromHREF(matcher.group());
			String link = getLinkFromHREF(matcher.group());
			
			CCOnlineReference ref = CCOnlineReference.createIMDB(extractOnlineID(link));
			
			if (ref != null) result.add(Tuple.Create(name, ref));
		}
		
		return CCStreams.iterate(result).unique(p -> p.Item2).enumerate();
	}
	
	@Override
	protected String getURL(CCOnlineReference ref) {
		return BASE_URL + "/title/" + ref.id;
	}
	
	protected String getLinkFromHREF(String href) {
		return BASE_URL + RegExHelper.find(REGEX_SEARCH_HREF_URL, href.trim()).trim();
	}
	
	protected String getNameFromHREF(String href) {
		return RegExHelper.find(REGEX_SEARCH_HREF_NAME, href.trim()).trim();
	}
	
	@Override
	protected String getTitle(String html) {
		return RegExHelper.find(REGEX_TITLE, html.trim()).trim();
	}
	
	@Override
	protected Integer getYear(String html) {
		String y = RegExHelper.find(REGEX_YEAR, html.trim()).trim();
		
		try {
			return Integer.parseInt(y);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	@Override
	protected Integer getRating(String html) {
		String y = RegExHelper.find(REGEX_RATING, html.trim()).trim();
		
		try {
			return (int) Math.round(Double.parseDouble(y.replace(',', '.')));
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	@Override
	protected Integer getLength(String html) {
		String y = RegExHelper.find(REGEX_LENGTH, html.trim()).trim();
		
		try {
			return Integer.parseInt(y);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	@Override
	protected Map<String, Integer> getFSKList(String html, String url) {
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
				String regexhtmlfsk = RegExHelper.find(REGEX_FSK, html).trim();
				if (! regexhtmlfsk.isEmpty()) {
					CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotFindFSK", RegExHelper.find(REGEX_FSK, html)));
				}
				return null;
			}
			
			sum = Math.round(sum/count);
			
			return CCFSK.getNearest((int) sum);
		}
	}
	
	@Override
	protected CCGenreList getGenres(String html) {
		String regfind = RegExHelper.find(REGEX_GENRE, html);
		
		CCGenreList result = new CCGenreList();
		
		String[] genres = regfind.split(REGEX_GENRE_SPLIT);
		
		for (String ng : genres) {
			CCGenre genre = CCGenre.parseFromIMDBName(ng);
			if (! genre.isEmpty()) {
				result.addGenre(genre);
			}
		}
		
		return result;
	}
	
	@Override
	public String getCoverURL(String html) {
		String cpageurl = BASE_URL + RegExHelper.find(REGEX_COVER, html);
		
		String cpagehtml = HTTPUtilities.getHTML(cpageurl, true, false);
		
		if (cpagehtml.isEmpty()) {
			return null;
		}

		String curl = RegExHelper.find(REGEX_COVER_DIREKT_1, cpagehtml);
		curl = RegExHelper.find(REGEX_COVER_DIREKT_2, curl);

		return curl;
	}
}
