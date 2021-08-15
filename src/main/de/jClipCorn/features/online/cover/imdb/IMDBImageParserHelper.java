package de.jClipCorn.features.online.cover.imdb;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.OnlineSearchType;

import java.awt.image.BufferedImage;
import java.util.List;

public abstract class IMDBImageParserHelper {
	public abstract String getSearchURL(String title, OnlineSearchType typ);
	public abstract String getCoverUrlAll(String mainurl);
	public abstract String getCoverUrlPoster(String mainurl);
	public abstract String getFirstSearchResult(String html);
	public abstract String getSecondSearchResult(String html);
	public abstract BufferedImage getMainpageImage(String html);
	public abstract BufferedImage getDirectImage(String html);
	public abstract List<String> extractImageLinks(String html);
	
	public static IMDBImageParserHelper GetConfiguredHelper(CCMovieList ml) {
		IMDBLanguage lang = IMDBLanguage.getWrapper().findOrNull(ml.ccprops().PROP_PARSEIMDB_LANGUAGE.getValue());
		
		switch (lang) {
		case GERMAN:
			return new IMDBImageParserHelperGerman(ml);
		case ENGLISH:
			return new IMDBImageParserHelperEnglish(ml);
		default:
			CCLog.addDefaultSwitchError(IMDBImageParserHelper.class, lang);
			return null;
		}
	}
}
