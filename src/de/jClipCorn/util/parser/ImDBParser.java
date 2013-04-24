package de.jClipCorn.util.parser;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.util.DoubleString;

public class ImDBParser {
	public final static ImDBLanguage LANGUAGE = ImDBLanguage.ENGLISH;
	
	public static String getSearchURL(String title, CCMovieTyp typ) { // TODO Invisible Setting for German / Englisch
		switch (LANGUAGE) {
		case GERMAN:
			return ImDBParser_Ger.getSearchURL(title, typ);
		case ENGLISH:
			return ImDBParser_Eng.getSearchURL(title, typ);
		default:
			return null;
		}
	}
	
	public static List<DoubleString> extractImDBLinks(String html) {
		switch (LANGUAGE) {
		case GERMAN:
			return ImDBParser_Ger.extractImDBLinks(html);
		case ENGLISH:
			return ImDBParser_Eng.extractImDBLinks(html);
		default:
			return null;
		}
	}
	
	public static String getTitle(String html) {
		switch (LANGUAGE) {
		case GERMAN:
			return ImDBParser_Ger.getTitle(html);
		case ENGLISH:
			return ImDBParser_Eng.getTitle(html);
		default:
			return null;
		}
	}
	
	public static int getYear(String html) {
		switch (LANGUAGE) {
		case GERMAN:
			return ImDBParser_Ger.getYear(html);
		case ENGLISH:
			return ImDBParser_Eng.getYear(html);
		default:
			return 0;
		}
	}
	
	public static int getRating(String html) {
		switch (LANGUAGE) {
		case GERMAN:
			return ImDBParser_Ger.getRating(html);
		case ENGLISH:
			return ImDBParser_Eng.getRating(html);
		default:
			return 0;
		}
	}
	
	public static int getLength(String html) {
		switch (LANGUAGE) {
		case GERMAN:
			return ImDBParser_Ger.getLength(html);
		case ENGLISH:
			return ImDBParser_Eng.getLength(html);
		default:
			return 0;
		}
	}
	
	public static Map<String, Integer> getFSKList(String html, String url) {
		switch (LANGUAGE) {
		case GERMAN:
			return ImDBParser_Ger.getFSKList(html);
		case ENGLISH:
			return ImDBParser_Eng.getFSKList(url);
		default:
			return null;
		}
	}
	
	public static CCMovieFSK getFSK(String html, String url) {
		switch (LANGUAGE) {
		case GERMAN:
			return ImDBParser_Ger.getFSK(html);
		case ENGLISH:
			return ImDBParser_Eng.getFSK(url);
		default:
			return null;
		}
	}
	
	public static CCMovieGenreList getGenres(String html) {
		switch (LANGUAGE) {
		case GERMAN:
			return ImDBParser_Ger.getGenres(html);
		case ENGLISH:
			return ImDBParser_Eng.getGenres(html);
		default:
			return null;
		}
	}
	
	public static BufferedImage getCover(String html) {
		switch (LANGUAGE) {
		case GERMAN:
			return ImDBParser_Ger.getCover(html);
		case ENGLISH:
			return ImDBParser_Eng.getCover(html);
		default:
			return null;
		}
	}

	public static BufferedImage getCoverDirekt(String html) {
		switch (LANGUAGE) {
		case GERMAN:
			return ImDBParser_Ger.getCoverDirekt(html);
		case ENGLISH:
			return ImDBParser_Eng.getCoverDirekt(html);
		default:
			return null;
		}
	}
}
