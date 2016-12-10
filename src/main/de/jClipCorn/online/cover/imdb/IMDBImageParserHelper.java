package de.jClipCorn.online.cover.imdb;

import java.awt.image.BufferedImage;
import java.util.List;

import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.online.OnlineSearchType;
import de.jClipCorn.properties.CCProperties;

public abstract class IMDBImageParserHelper {
	public abstract String getSearchURL(String title, OnlineSearchType typ);
	public abstract String getCoverUrlAll(String mainurl);
	public abstract String getCoverUrlPoster(String mainurl);
	public abstract String getFirstSearchResult(String html);
	public abstract String getSecondSearchResult(String html);
	public abstract BufferedImage getMainpageImage(String html);
	public abstract BufferedImage getDirectImage(String html);
	public abstract List<String> extractImageLinks(String html);
	
	public static IMDBImageParserHelper GetConfiguredHelper() {
		IMDBLanguage lang = IMDBLanguage.getWrapper().find(CCProperties.getInstance().PROP_PARSEIMDB_LANGUAGE.getValue());
		
		switch (lang) {
		case GERMAN:
			return new IMDBImageParserHelperGerman();
		case ENGLISH:
			return new IMDBImageParserHelperEnglish();
		default:
			CCLog.addDefaultSwitchError(IMDBImageParserHelper.class, lang);
			return null;
		}
	}
}
