package de.jClipCorn.online.metadata.imdb;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.online.OnlineSearchType;
import de.jClipCorn.online.cover.imdb.IMDBLanguage;
import de.jClipCorn.online.metadata.Metadataparser;
import de.jClipCorn.online.metadata.OnlineMetadata;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.MetadataParserImplementation;
import de.jClipCorn.util.Tuple;
import de.jClipCorn.util.helper.HTTPUtilities;

public abstract class IMDBParserCommon extends Metadataparser {
	
	private final Pattern REGEX_IMDB_ID = Pattern.compile("^.*imdb\\.com/[a-z]+/(tt[0-9]+)(/.*)?$", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

	public static IMDBParserCommon GetConfiguredParser() {
		IMDBLanguage lang = IMDBLanguage.getWrapper().find(CCProperties.getInstance().PROP_PARSEIMDB_LANGUAGE.getValue());
		
		switch (lang) {
		case GERMAN:
			return new IMDBParserGerman();
		case ENGLISH:
			return new IMDBParserEnglish();
		default:
			CCLog.addDefaultSwitchError(IMDBParserCommon.class, lang);
			return null;
		}
	}
	
	@Override
	public List<Tuple<String, CCOnlineReference>> searchByText(String text, OnlineSearchType type) {
		String url = getSearchURL(text, type);
		String html = HTTPUtilities.getHTML(url, true, true);
		List<Tuple<String, CCOnlineReference>> res = extractImDBLinks(html);
		
		return res;
	}

	protected String extractOnlineID(String url) {
		Matcher m = REGEX_IMDB_ID.matcher(url);
		
		if (m.matches()) {
			return m.group(1);
		}
		
		return null;
	}

	@Override
	public OnlineMetadata getMetadata(CCOnlineReference ref, boolean downloadCover) {
		final String url = getURL(ref);

		String html = HTTPUtilities.getHTML(url, true, true);

		OnlineMetadata result = new OnlineMetadata(ref);
		
		result.Title = getTitle(html);
		result.Year = getYear(html);
		result.OnlineScore = getRating(html);
		result.Length = getLength(html);
		result.Genres = getGenres(html);
		result.CoverURL = getCoverURL(html);
		if (downloadCover && result.CoverURL != null)result.Cover = HTTPUtilities.getImage(result.CoverURL);
		result.FSKList = getFSKList(html, url);
		if (result.FSKList != null) result.FSK = getFSK(result.FSKList, html, url);
		
		return result;
	}
	
	public CCOnlineReference getFirstResultReference(String title, OnlineSearchType type) {
		String url = getSearchURL(title, type);
		String html = HTTPUtilities.getHTML(url, true, true);
		final List<Tuple<String,CCOnlineReference>> res = extractImDBLinks(html);
		
		if (res.isEmpty()) return CCOnlineReference.createNone();
		
		return res.get(0).Item2;
	}

	@Override
	public MetadataParserImplementation getImplType() {
		return MetadataParserImplementation.IMDB;
	}

	public abstract String getSearchURL(String title, OnlineSearchType typ);
	public abstract List<Tuple<String, CCOnlineReference>> extractImDBLinks(String html);

	protected abstract String getURL(CCOnlineReference ref);
	
	protected abstract String getTitle(String html);
	protected abstract Integer getYear(String html);
	protected abstract Integer getRating(String html);
	protected abstract Integer getLength(String html);
	protected abstract Map<String, Integer> getFSKList(String html, String url);
	protected abstract CCGenreList getGenres(String html);
	protected abstract CCFSK getFSK(Map<String, Integer> genmap, String html, String url);
	public abstract String getCoverURL(String html);
}
