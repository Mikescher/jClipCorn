package de.jClipCorn.util.parser.imagesearch.imageparser;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.util.datatypes.DoubleString;
import de.jClipCorn.util.helper.RegExHelper;
import de.jClipCorn.util.parser.onlineparser.ImDBParser;
import de.jClipCorn.util.parser.onlineparser.ImDBParser_Ger;
@SuppressWarnings("nls")
public class ImDBImageParser_Ger {
	private final static String COVER_URL_ALL_APPENDIX = "mediaindex";
	private final static String COVER_URL_POSTER_APPENDIX = "mediaindex?refine=poster";
	
	private final static String REGEX_THUMBLIST = "<div class=\"thumb_list\".+?</div>"; // <div class="thumb_list".+?</div>
	private final static String REGEX_IMAGELINK = "(?<= href=\"/rg/mediaindex/unknown-thumbnail)/media/rm[0-9]+/tt[0-9]+(?=\")"; // (?<= href="/rg/mediaindex/unknown-thumbnail)/media/rm[0-9]+/tt[0-9]+(?=")
	
	public static String getSearchURL(String title, CCMovieTyp typ) {
		return ImDBParser.getSearchURL(title, typ);
	}
	
	public static String getCoverUrlAll(String mainurl) {
		return mainurl.endsWith("/")? (mainurl + COVER_URL_ALL_APPENDIX) : (mainurl + '/' + COVER_URL_ALL_APPENDIX);
	}
	
	public static String getCoverUrlPoster(String mainurl) {
		return mainurl.endsWith("/")? (mainurl + COVER_URL_POSTER_APPENDIX) : (mainurl + '/' + COVER_URL_POSTER_APPENDIX);
	}
	
	public static String getFirstSearchResult(String html) {
		List<DoubleString> alds = ImDBParser.extractImDBLinks(html);
		if (!alds.isEmpty()) {
			return alds.get(0).get1();
		} else {
			return "";
		}
	}
	
	public static String getSecondSearchResult(String html) {
		List<DoubleString> alds = ImDBParser.extractImDBLinks(html);
		if (alds.size() >= 2) {
			return alds.get(1).get1();
		} else {
			return "";
		}
	}
	
	public static BufferedImage getMainpageImage(String html) {
		return ImDBParser.getCover(html);
	}
	
	public static BufferedImage getDirectImage(String html) {
		return ImDBParser.getCoverDirekt(html);
	}
	
	public static List<String> extractImageLinks(String html) {
		List<String> result = new ArrayList<>();
		
		String shortened = RegExHelper.find(REGEX_THUMBLIST, html);
		
		Pattern pat = Pattern.compile(REGEX_IMAGELINK);
		
		Matcher matcher = pat.matcher(shortened);
		
		while (matcher.find()) {
			result.add(ImDBParser_Ger.BASE_URL + matcher.group());
		}
		
		return result;
	}
}
