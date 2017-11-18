package de.jClipCorn.online.cover.imdb;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.online.OnlineSearchType;
import de.jClipCorn.online.metadata.imdb.IMDBParserCommon;
import de.jClipCorn.online.metadata.imdb.IMDBParserEnglish;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.helper.RegExHelper;
import de.jClipCorn.util.http.HTTPUtilities;
@SuppressWarnings("nls")
public class IMDBImageParserHelperEnglish extends IMDBImageParserHelper{
	private final static String COVER_URL_ALL_APPENDIX = "mediaindex";
	private final static String COVER_URL_POSTER_APPENDIX = "mediaindex?refine=poster";
	
	private final static String REGEX_THUMBLIST = "<div class=\"thumb_list\".+?</div>"; // <div class="thumb_list".+?</div>
	private final static String REGEX_IMAGELINK = "(?<= href=\"/rg/mediaindex/unknown-thumbnail)/media/rm[0-9]+/tt[0-9]+(?=\")"; // (?<= href="/rg/mediaindex/unknown-thumbnail)/media/rm[0-9]+/tt[0-9]+(?=")

	private static final IMDBParserCommon parser = new IMDBParserEnglish();
	
	@Override
	public String getSearchURL(String title, OnlineSearchType typ) {
		return parser.getSearchURL(title, typ);
	}
	
	@Override
	public String getCoverUrlAll(String mainurl) {
		return mainurl.endsWith("/")? (mainurl + COVER_URL_ALL_APPENDIX) : (mainurl + '/' + COVER_URL_ALL_APPENDIX);
	}
	
	@Override
	public String getCoverUrlPoster(String mainurl) {
		return mainurl.endsWith("/")? (mainurl + COVER_URL_POSTER_APPENDIX) : (mainurl + '/' + COVER_URL_POSTER_APPENDIX);
	}
	
	@Override
	public String getFirstSearchResult(String html) {
		List<Tuple<String, CCOnlineReference>> alds = parser.extractImDBLinks(html);
		if (!alds.isEmpty()) {
			return alds.get(0).Item2.getURL();
		} else {
			return "";
		}
	}
	
	@Override
	public String getSecondSearchResult(String html) {
		List<Tuple<String, CCOnlineReference>> alds = parser.extractImDBLinks(html);
		if (alds.size() >= 2) {
			return alds.get(1).Item2.getURL();
		} else {
			return "";
		}
	}
	
	@Override
	public BufferedImage getMainpageImage(String html) {
		String url = parser.getCoverURL(html);
		if (url == null) return null;
		return HTTPUtilities.getImage(url);
	}
	
	@Override
	public BufferedImage getDirectImage(String html) {
		Element e = Jsoup.parse(html).select(IMDBParserEnglish.JSOUP_COVER_DIRECT).first();
		String curl = (e == null) ? "" : e.attr("src");
		
		if (curl.trim().isEmpty()) {
			return null;
		}
		
		BufferedImage result = HTTPUtilities.getImage(curl);
		
		if (result == null) {
			return null;
		}

		return result;
	}
	
	@Override
	public List<String> extractImageLinks(String html) {
		List<String> result = new ArrayList<>();
		
		String shortened = RegExHelper.find(REGEX_THUMBLIST, html);
		
		Pattern pat = Pattern.compile(REGEX_IMAGELINK);
		
		Matcher matcher = pat.matcher(shortened);
		
		while (matcher.find()) {
			result.add(IMDBParserEnglish.BASE_URL + matcher.group());
		}
		
		return result;
	}
}
