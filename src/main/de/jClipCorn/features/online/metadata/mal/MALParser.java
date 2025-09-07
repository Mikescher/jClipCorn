package de.jClipCorn.features.online.metadata.mal;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCStringList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.online.OnlineSearchType;
import de.jClipCorn.features.online.cover.imdb.AgeRatingParser;
import de.jClipCorn.features.online.metadata.Metadataparser;
import de.jClipCorn.features.online.metadata.OnlineMetadata;
import de.jClipCorn.properties.enumerations.MetadataParserImplementation;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.helper.RegExHelper;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.stream.CCStreams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MALParser extends Metadataparser {
	private final static String SEARCH_URL = "https://myanimelist.net/search/all?q=%s"; //$NON-NLS-1$

	private static final Pattern REGEX_MYAL = Pattern.compile("^(http://|https://)?(www\\.)?myanimelist\\.net/anime/(?<id>[0-9]+)(/.*)?$"); //$NON-NLS-1$
	private static final Pattern REGEX_BORDER = Pattern.compile("^(?<ident>[A-Za-z]+):(?<content>.+)$"); //$NON-NLS-1$
	private static final Pattern REGEX_AIRED = Pattern.compile("^\\s*[A-Za-z]{3}\\s+[0-9]{1,2},\\s+(?<year>[12][0-9]{3})(?:(?:\\s+to\\s+[A-Za-z]{3}\\s+[0-9]{1,2},\\s+[12][0-9]{3})|(?:\\s+to\\s+\\?))?\\s*$"); //$NON-NLS-1$
	private static final Pattern REGEX_PREMIERED = Pattern.compile("^\\s*(?<season>Summer|Fall|Winter|Spring)\\s*(?<year>[12][0-9]{3})\\s*$"); //$NON-NLS-1$
	private static final Pattern REGEX_RATING = Pattern.compile("^\\s*(?<rating>[A-Z0-9\\-]+) - .*$"); //$NON-NLS-1$
	private static final Pattern REGEX_DURATION = Pattern.compile("^\\s*(?:(?<h>[0-9]+)\\s+hr\\.\\s+)?\\s*(?<m>[0-9]+)\\s+min\\.\\s*(per ep\\.)\\s*$"); //$NON-NLS-1$

	private final static String SOUP_SEARCH = ".content-result a[href^=https://myanimelist.net/anime/]"; //$NON-NLS-1$

	private final static String SOUP_TITLE = ".title-name"; //$NON-NLS-1$
	private final static String SOUP_COVER = "*[itemprop=image]"; //$NON-NLS-1$
	private final static String SOUP_SCORE = "div.score"; //$NON-NLS-1$
	private final static String SOUP_GENRES = "*[itemprop=genre]"; //$NON-NLS-1$
	private final static String SOUP_BORDER = "#content .borderClass div"; //$NON-NLS-1$

	public MALParser(CCMovieList ml) {
		super(ml);
	}

	@Override
	public List<Tuple<String, CCSingleOnlineReference>> searchByText(String title, OnlineSearchType type) {
		String url = String.format(SEARCH_URL, HTTPUtilities.escapeURL(title));
		String html = HTTPUtilities.getHTML(movielist, url, true, true);

		List<Tuple<String, CCSingleOnlineReference>> result = new ArrayList<>();
		
		Document soup = Jsoup.parse(html);

		for (Element elem : soup.select(SOUP_SEARCH)) {
			String name = elem.text();
			if (name.trim().isEmpty()) continue;
			
			Matcher matcher = REGEX_MYAL.matcher(elem.attr("href")); //$NON-NLS-1$
			if (matcher.find()) result.add(Tuple.Create(name, CCSingleOnlineReference.createMyAnimeList(matcher.group("id")))); //$NON-NLS-1$
		}
		
		return CCStreams.iterate(result).unique(p -> p.Item2).enumerate();
	}

	@Override
	public MetadataParserImplementation getImplType() {
		return MetadataParserImplementation.MAL;
	}

	@Override
	@SuppressWarnings("nls")
	public OnlineMetadata getMetadata(CCSingleOnlineReference ref, boolean downloadCover) {
		String url = ref.getURL(ccprops());
		String html = HTTPUtilities.getHTML(movielist, url, true, true);
		Document soup = Jsoup.parse(html);

		OnlineMetadata result = new OnlineMetadata(ref);
		
		result.Title = getContentBySelector(soup, SOUP_TITLE);
		result.CoverURL = getAttrBySelector(soup, SOUP_COVER, "data-src");

		var os = getFloatBySelector(soup, SOUP_SCORE);

		result.OnlineScore = (os == null) ? null : CCOnlineScore.create((short)Math.round(os * 100), (short)1000);

		for (Element elem : soup.select(SOUP_BORDER)) {
			Matcher matcher = REGEX_BORDER.matcher(elem.text());
			if (!matcher.find()) continue;

			String ident   = matcher.group("ident");
			String content = matcher.group("content").trim();
			
			if (ident.equalsIgnoreCase("Aired")) {
				
				String y = RegExHelper.getGroup(REGEX_AIRED, content, "year");
				if (y != null) result.Year = Integer.parseInt(y);
				
			} else if (ident.equalsIgnoreCase("Premiered")) {
				
				String y = RegExHelper.getGroup(REGEX_PREMIERED, content, "year");
				if (y != null) result.Year = Integer.parseInt(y);
				
				String season = RegExHelper.getGroup(REGEX_PREMIERED, content, "season");
				if (season != null && y != null) {
					result.AnimeSeason = CCStringList.create(season + " " + y);
				}

			} else if (ident.equalsIgnoreCase("Studios")) {

				result.AnimeStudio = CCStringList.create(content);

			} else if (ident.equalsIgnoreCase("Rating")) {
				
				String r = RegExHelper.getGroup(REGEX_RATING, content, "rating");
				if (r != null) result.FSK = AgeRatingParser.getFSK(r, url);
				
			} else if (ident.equalsIgnoreCase("Duration")) {
				
				String rh = RegExHelper.getGroup(REGEX_DURATION, content, "h");
				String rm = RegExHelper.getGroup(REGEX_DURATION, content, "m");
				if (rm != null) {
					result.Length = Integer.parseInt(rm) + 60*(rh != null ? Integer.parseInt(rh) : 0);
				}
				
			}
		}

		var genres = new ArrayList<CCGenre>();
		genres.add(CCGenre.GENRE_022);
		for (Element elem: soup.select(SOUP_GENRES) ) {
			genres.addAll(CCStreams.iterate(CCGenre.parseFromMAL(elem.text().trim())).toList());
		}

		result.Genres = new CCGenreList(CCStreams.iterate(genres).unique().toList());

		if (downloadCover && !Str.isNullOrWhitespace(result.CoverURL)) result.Cover = HTTPUtilities.getImage(movielist, result.CoverURL);
		
		return result;
	}
	
	private String getContentBySelector(Document soup, String cssQuery) {
		Element e = soup.select(cssQuery).first();
		return (e == null) ? "" : e.text(); //$NON-NLS-1$
	}
	
	private String getAttrBySelector(Document soup, String cssQuery, String attr) {
		Element e = soup.select(cssQuery).first();
		return (e == null) ? "" : e.attr(attr); //$NON-NLS-1$
	}
	
	protected Double getFloatBySelector(Document soup, String cssQuery) {
		String y = getContentBySelector(soup, cssQuery);
		
		try {
			return Double.parseDouble(y.replace(',', '.'));
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
