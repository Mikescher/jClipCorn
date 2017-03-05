package de.jClipCorn.online.metadata.tmdb;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.online.OnlineSearchType;
import de.jClipCorn.online.metadata.Metadataparser;
import de.jClipCorn.online.metadata.OnlineMetadata;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.BrowserLanguage;
import de.jClipCorn.properties.enumerations.MetadataParserImplementation;
import de.jClipCorn.util.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.stream.CCStreams;

public class TMDBParser extends Metadataparser {
	private final static String API_KEY = new String(Base64.getDecoder().decode("M2ZkYWMyNzcxYWY4ZjZlZjljNWY0ZDc4ZmEyOWRjZDUAAAA"), Charset.forName("UTF-8")).trim();  //$NON-NLS-1$//$NON-NLS-2$
	private final static String URL_BASE = "http://api.themoviedb.org/3/"; //$NON-NLS-1$

	private final static String URL_IMAGE_BASE = "http://image.tmdb.org/t/p/w342"; //$NON-NLS-1$
	
	private final static String URL_SEARCHMOVIE = "search/movie"; //$NON-NLS-1$
	private final static String URL_SEARCHSERIES = "search/tv"; //$NON-NLS-1$
	private final static String URL_SEARCHMULTI = "search/multi"; //$NON-NLS-1$
	private final static String URL_SEARCHCOVER = "/images"; //$NON-NLS-1$

	@Override
	public List<Tuple<String, CCOnlineReference>> searchByText(String text, OnlineSearchType type) {
		switch (type) {
		case MOVIES:
			return searchMovies(text, -1);
		case SERIES:
			return searchSeries(text, -1);
		case BOTH:
			return searchMulti(text, -1);
		default:
			CCLog.addDefaultSwitchError(this, type);
			return null;
		}
	}
	
	@SuppressWarnings("nls")
	private List<Tuple<String, CCOnlineReference>> searchMovies(String query, int year) throws JSONException {
		String url = URL_BASE + URL_SEARCHMOVIE + "?api_key=" + API_KEY + "&query=" + HTTPUtilities.escapeURL(query);
		
		if (year > 0) url += "&year="+year;
		
		String json = HTTPUtilities.getRateLimitedHTML(url, false, false);
		try {
			JSONObject root = new JSONObject(new JSONTokener(json));
			
			JSONArray results = root.getJSONArray("results");
			
			List<Tuple<String, CCOnlineReference>> out = new ArrayList<>();
			for (int i = 0; i < results.length(); i++) {
				JSONObject result = results.getJSONObject(i);
		
				String title = result.getString("title");
				CCOnlineReference id = CCOnlineReference.createTMDB("movie/" + result.getInt("id"));
				
				if (id != null) out.add(Tuple.Create(title, id));
			}
			
			return CCStreams.iterate(out).unique(p -> p.Item2).enumerate();
		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TMDBApiError", json), e);
			return new ArrayList<>();
		}
	}

	@SuppressWarnings("nls")
	private List<Tuple<String, CCOnlineReference>> searchSeries(String query, int year) {
		String url = URL_BASE + URL_SEARCHSERIES + "?api_key=" + API_KEY + "&query=" + HTTPUtilities.escapeURL(query);
		
		if (year > 0)
			url += "&year="+year;
		
		String json = HTTPUtilities.getRateLimitedHTML(url, false, false);
		try {
			JSONObject root = new JSONObject(new JSONTokener(json));
			
			JSONArray results = root.getJSONArray("results");

			List<Tuple<String, CCOnlineReference>> out = new ArrayList<>();
			for (int i = 0; i < results.length(); i++) {
				JSONObject result = results.getJSONObject(i);
		
				String title = result.getString("name");
				CCOnlineReference id = CCOnlineReference.createTMDB("tv/" + result.getInt("id"));

				if (id != null) out.add(Tuple.Create(title, id));
			}
			
			return CCStreams.iterate(out).unique(p -> p.Item2).enumerate();
		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TMDBApiError", json), e);
			return new ArrayList<>();
		}
	}

	@SuppressWarnings("nls")
	private List<Tuple<String, CCOnlineReference>> searchMulti(String query, int year) {
		String url = URL_BASE + URL_SEARCHMULTI + "?api_key=" + API_KEY + "&query=" + HTTPUtilities.escapeURL(query);
		
		if (year > 0)
			url += "&year="+year;
		
		String json = HTTPUtilities.getRateLimitedHTML(url, false, false);
		try {
			JSONObject root = new JSONObject(new JSONTokener(json));
			
			JSONArray results = root.getJSONArray("results");

			List<Tuple<String, CCOnlineReference>> out = new ArrayList<>();
			for (int i = 0; i < results.length(); i++) {
				JSONObject result = results.getJSONObject(i);

				String type = result.getString("media_type");
				String title;
				if (type.equals("tv"))
					title = result.getString("name");
				else
					title = result.getString("title");

				CCOnlineReference id = CCOnlineReference.createTMDB(type + "/" + result.getInt("id"));

				if (id != null) out.add(Tuple.Create(title, id));
			}
			
			return CCStreams.iterate(out).unique(p -> p.Item2).enumerate();
		} catch (JSONException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TMDBApiError", json), e);
			return new ArrayList<>();
		}
	}

	@Override
	public OnlineMetadata getMetadata(CCOnlineReference ref, boolean downloadCover) {
		if (CCProperties.getInstance().PROP_TMDB_LANGUAGE.getValue() == BrowserLanguage.ENGLISH) {
			
			// simple call
			
			return getMetadataInternal(ref, BrowserLanguage.ENGLISH, downloadCover);
		} else {
			// specific call but fallback to en-Us for missing fields
			
			OnlineMetadata base = getMetadataInternal(ref, BrowserLanguage.ENGLISH, downloadCover);
			OnlineMetadata ext = getMetadataInternal(ref, CCProperties.getInstance().PROP_TMDB_LANGUAGE.getValue(), downloadCover);
			
			ext.setMissingFields(base);
			
			return ext;
		}
	}

	@SuppressWarnings("nls")
	private OnlineMetadata getMetadataInternal(CCOnlineReference ref, BrowserLanguage lang, boolean downloadCover) {
		String urlRaw = URL_BASE + ref.id + "?api_key=" + API_KEY;
		
		String url = urlRaw;
		
		if (CCProperties.getInstance().PROP_TMDB_LANGUAGE.getValue() != BrowserLanguage.ENGLISH) {
			url += "&language=" + CCProperties.getInstance().PROP_TMDB_LANGUAGE.getValue().asLanguageID();
		}
		
		String json = HTTPUtilities.getRateLimitedHTML(url, false, false);
		try {
			OnlineMetadata result = new OnlineMetadata(ref);

			JSONObject root = new JSONObject(new JSONTokener(json));

			result.Title = hasString(root, "title") ? root.getString("title") : root.getString("name");
			
			result.Year = hasString(root, "release_date") ? CCDate.parse(root.getString("release_date"), "yyyy-MM-dd").getYear() : null;
			
			result.Length = root.optInt("runtime", 0);
			if (result.Length == 0) {
				result.Length = null;
				
				if (!url.equals(urlRaw)) {
					// sometimes runtime is only set in en-US (??)
					JSONObject fallback = new JSONObject(new JSONTokener(HTTPUtilities.getRateLimitedHTML(urlRaw, false, false)));
					if (fallback.optInt("runtime", 0) != 0) result.Length = fallback.optInt("runtime", 0);
				}
			}
			
			result.CoverURL = hasString(root, "poster_path") ? (URL_IMAGE_BASE + root.getString("poster_path")) : null;
			if (result.CoverURL != null && downloadCover) result.Cover = HTTPUtilities.getImage(result.CoverURL);
			
			result.OnlineScore = (int) Math.round(root.optDouble("vote_average", 0));
			if (result.OnlineScore == 0) result.OnlineScore = null;
			
			if (root.has("genres")) {
				result.Genres = new CCGenreList();
				JSONArray jsonGenres = root.getJSONArray("genres");
				for (int i = 0; i < jsonGenres.length(); i++) {
					CCGenre g = CCGenre.parseFromTMDbID(jsonGenres.getJSONObject(i).getInt("id"));
					if (g.isValid())
						result.Genres.addGenre(g);
				}
			}
			
			if (hasString(root, "imdb_id")) 
				result.AltRef = CCOnlineReference.createIMDB(root.getString("imdb_id"));
			
			return result;
			
		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TMDBApiError", json), e);
			return null;
		}
	}
	
	private static boolean hasString(JSONObject obj, String ident) {
		return obj.has(ident) && !obj.isNull(ident) && !obj.getString(ident).isEmpty();
	}

	public CCOnlineReference findMovieDirect(String searchText) {
		return findMovieDirect(searchText, -1);
	}
	
	private CCOnlineReference findMovieDirect(String searchText, int year) {
		List<Tuple<String, CCOnlineReference>> r0 = searchMovies(searchText, year);
		if (r0.isEmpty()) {
			return CCOnlineReference.createNone();
		}

		return r0.get(0).Item2;
	}

	public CCOnlineReference findSeriesDirect(String searchText) {
		return findSeriesDirect(searchText, -1);
	}

	private CCOnlineReference findSeriesDirect(String searchText, int year) {
		List<Tuple<String, CCOnlineReference>> r0 = searchSeries(searchText, year);
		if (r0.isEmpty()) {
			return CCOnlineReference.createNone();
		}
		
		return r0.get(0).Item2;
	}

	@SuppressWarnings("nls")
	public List<String> findCovers(CCOnlineReference ref) {
		String url = URL_BASE + ref.id + URL_SEARCHCOVER + "?api_key=" + API_KEY;
		
		String json = HTTPUtilities.getRateLimitedHTML(url, false, false);
		try {
			JSONObject root = new JSONObject(new JSONTokener(json));

			List<String> result = new ArrayList<>();
			if (root.has("posters")) {
				JSONArray cover = root.getJSONArray("posters");
				
				for (int i = 0; i < cover.length(); i++) {
					result.add(URL_IMAGE_BASE + cover.getJSONObject(i).getString("file_path"));
				}
			}
			return result;
			
		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TMDBApiError", json), e);
			return null;
		}
	}

	@Override
	public MetadataParserImplementation getImplType() {
		return MetadataParserImplementation.TMDB;
	}
}
