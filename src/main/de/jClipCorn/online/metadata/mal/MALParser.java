package de.jClipCorn.online.metadata.mal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.online.OnlineSearchType;
import de.jClipCorn.online.metadata.Metadataparser;
import de.jClipCorn.online.metadata.OnlineMetadata;
import de.jClipCorn.properties.enumerations.MetadataParserImplementation;
import de.jClipCorn.util.Tuple;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;

public class MALParser extends Metadataparser {
	private final static String SEARCH_URL = "https://myanimelist.net/search/all?q=%s"; //$NON-NLS-1$

	private static final Pattern REGEX_MYAL = Pattern.compile("^(http://|https://)?(www\\.)?myanimelist\\.net/anime/(?<id>[0-9]+)(/.*)?$"); //$NON-NLS-1$
	private static final Pattern REGEX_BORDER = Pattern.compile("^(?<ident>[A-Za-z]+):(<?content>.+)$"); //$NON-NLS-1$

	private final static String SOUP_SEARCH = ".content-result a[href^=https://myanimelist.net/anime/]"; //$NON-NLS-1$

	private final static String SOUP_TITLE = "h1 span[itemprop=name]"; //$NON-NLS-1$
	private final static String SOUP_COVER = "img.ac"; //$NON-NLS-1$
	private final static String SOUP_SCORE = "div.score"; //$NON-NLS-1$
	private final static String SOUP_BORDER = "#content .borderClass div"; //$NON-NLS-1$
	
	@Override
	public List<Tuple<String, CCOnlineReference>> searchByText(String title, OnlineSearchType type) {
		String url = String.format(SEARCH_URL, HTTPUtilities.escapeURL(title));
		String html = HTTPUtilities.getHTML(url, true, true);

		List<Tuple<String, CCOnlineReference>> result = new ArrayList<>();
		
		Document soup = Jsoup.parse(html);

		for (Element elem : soup.select(SOUP_SEARCH)) {
			String name = elem.text();
			if (name.trim().isEmpty()) continue;
			
			Matcher matcher = REGEX_MYAL.matcher(elem.attr("href")); //$NON-NLS-1$
			if (matcher.find()) result.add(Tuple.Create(name, CCOnlineReference.createMyAnimeList(matcher.group("id")))); //$NON-NLS-1$
		}
		
		return CCStreams.iterate(result).unique(p -> p.Item2).enumerate();
	}

	@Override
	public MetadataParserImplementation getImplType() {
		return MetadataParserImplementation.MAL;
	}

	@Override
	public OnlineMetadata getMetadata(CCOnlineReference ref, boolean downloadCover) {
		String html = HTTPUtilities.getHTML(ref.getURL(), true, true);
		Document soup = Jsoup.parse(html);

		OnlineMetadata result = new OnlineMetadata(ref);
		
		result.Title = getContentBySelector(soup, SOUP_TITLE);
		result.CoverURL = getAttrBySelector(soup, SOUP_COVER, "src"); //$NON-NLS-1$
		result.OnlineScore = getRoundedIntBySelector(soup, SOUP_SCORE);

		for (Element elem : soup.select(SOUP_BORDER)) {
			Matcher matcher = REGEX_BORDER.matcher(elem.text());
			if (!matcher.find()) continue;

			String ident   = matcher.group("ident"); //$NON-NLS-1$
			String content = matcher.group("content"); //$NON-NLS-1$
			
			if (ident.equalsIgnoreCase("Aired")) {
				
			} else if (ident.equalsIgnoreCase("Premiered")) {
				
			} else if (ident.equalsIgnoreCase("Genres")) {
				
				result.Genres = new CCGenreList(CCStreams.iterate(content.split(",")).map(c -> CCGenre.parseFromMAL(c)).filter(f -> !f.isEmpty()).enumerate());
				
			} else if (ident.equalsIgnoreCase("Rating")) {
				
			}
		}

		if (downloadCover && result.CoverURL != null)result.Cover = HTTPUtilities.getImage(result.CoverURL);
		
		return null;
	}
	
	private String getContentBySelector(Document soup, String cssQuery) {
		Element e = soup.select(cssQuery).first();
		return (e == null) ? "" : e.text(); //$NON-NLS-1$
	}
	
	private String getAttrBySelector(Document soup, String cssQuery, String attr) {
		Element e = soup.select(cssQuery).first();
		return (e == null) ? "" : e.attr(attr); //$NON-NLS-1$
	}
	
	protected Integer getRoundedIntBySelector(Document soup, String cssQuery) {
		String y = getContentBySelector(soup, cssQuery);
		
		try {
			return (int) Math.round(Double.parseDouble(y.replace(',', '.')));
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
