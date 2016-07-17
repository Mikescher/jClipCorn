package de.jClipCorn.util.parser.imageparser;

import java.awt.image.BufferedImage;
import java.util.List;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.util.datatypes.DoubleString;
import de.jClipCorn.util.parser.onlineparser.ImDBParser;
@SuppressWarnings("nls")
public class ImDBImageParser {
	public static String getSearchURL(String title, CCMovieTyp typ) {
		switch (ImDBParser.LANGUAGE) {
		case GERMAN:
			return ImDBImageParser_Ger.getSearchURL(title, typ);
		case ENGLISH:
			return ImDBImageParser_Eng.getSearchURL(title, typ);
		default:
			return null;
		}
	}
	
	public static String getCoverUrlAll(String mainurl) {
		switch (ImDBParser.LANGUAGE) {
		case GERMAN:
			return ImDBImageParser_Ger.getCoverUrlAll(mainurl);
		case ENGLISH:
			return ImDBImageParser_Eng.getCoverUrlAll(mainurl);
		default:
			return null;
		}
	}
	
	public static String getCoverUrlPoster(String mainurl) {
		switch (ImDBParser.LANGUAGE) {
		case GERMAN:
			return ImDBImageParser_Ger.getCoverUrlPoster(mainurl);
		case ENGLISH:
			return ImDBImageParser_Eng.getCoverUrlPoster(mainurl);
		default:
			return null;
		}
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
		switch (ImDBParser.LANGUAGE) {
		case GERMAN:
			return ImDBImageParser_Ger.getSecondSearchResult(html);
		case ENGLISH:
			return ImDBImageParser_Eng.getSecondSearchResult(html);
		default:
			return null;
		}
	}
	
	public static BufferedImage getMainpageImage(String html) {
		switch (ImDBParser.LANGUAGE) {
		case GERMAN:
			return ImDBImageParser_Ger.getMainpageImage(html);
		case ENGLISH:
			return ImDBImageParser_Eng.getMainpageImage(html);
		default:
			return null;
		}
	}
	
	public static BufferedImage getDirectImage(String html) {
		switch (ImDBParser.LANGUAGE) {
		case GERMAN:
			return ImDBImageParser_Ger.getDirectImage(html);
		case ENGLISH:
			return ImDBImageParser_Eng.getDirectImage(html);
		default:
			return null;
		}
	}
	
	public static List<String> extractImageLinks(String html) {
		switch (ImDBParser.LANGUAGE) {
		case GERMAN:
			return ImDBImageParser_Ger.extractImageLinks(html);
		case ENGLISH:
			return ImDBImageParser_Eng.extractImageLinks(html);
		default:
			return null;
		}
	}
}
