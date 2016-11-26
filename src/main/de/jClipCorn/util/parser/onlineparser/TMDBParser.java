package de.jClipCorn.util.parser.onlineparser;

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
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.BrowserLanguage;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.helper.HTTPUtilities;

public class TMDBParser {
	private final static String API_KEY = new String(Base64.getDecoder().decode("M2ZkYWMyNzcxYWY4ZjZlZjljNWY0ZDc4ZmEyOWRjZDUAAAA"), Charset.forName("UTF-8")).trim();  //$NON-NLS-1$//$NON-NLS-2$
	private final static String URL_BASE = "http://api.themoviedb.org/3/"; //$NON-NLS-1$

	private final static String URL_IMAGE_BASE = "http://image.tmdb.org/t/p/w342"; //$NON-NLS-1$
	
	private final static String URL_SEARCHMOVIE = "search/movie"; //$NON-NLS-1$
	private final static String URL_SEARCHSERIES = "search/tv"; //$NON-NLS-1$
	private final static String URL_SEARCHMULTI = "search/multi"; //$NON-NLS-1$
	private final static String URL_SEARCHCOVER = "/images"; //$NON-NLS-1$
	
	public static class TMDBSimpleResult {
		public final String ID;
		public final String Title;
		
		public TMDBSimpleResult(String id, String str) { ID=id; Title=str; }
	}
	
	public static class TMDBFullResult {
		public String ID;
		public String Title;
		public int Year;
		public int Score;
		public String CoverPath;
		public CCOnlineReference ImdbRef;
		public CCGenreList Genres;
		public int Length;
	}

	public static List<TMDBSimpleResult> searchMovies(String query) throws JSONException {
		return searchMovies(query, -1);
	}
	
	@SuppressWarnings("nls")
	public static List<TMDBSimpleResult> searchMovies(String query, int year) throws JSONException {
		String url = URL_BASE + URL_SEARCHMOVIE + "?api_key=" + API_KEY + "&query=" + HTTPUtilities.escapeURL(query);
		
		if (year > 0)
			url += "&year="+year;
		
		String json = HTTPUtilities.getRateLimitedHTML(url, false, false);
		try {
			JSONObject root = new JSONObject(new JSONTokener(json));
			
			JSONArray results = root.getJSONArray("results");
			
			List<TMDBSimpleResult> out = new ArrayList<>();
			for (int i = 0; i < results.length(); i++) {
				JSONObject result = results.getJSONObject(i);
		
				String title = result.getString("title");
				String id = "movie/" + result.getInt("id");
				
				out.add(new TMDBSimpleResult(id, title));
			}
			return out;
		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TMDBApiError", json), e);
			return new ArrayList<>();
		}
	}

	public static List<TMDBSimpleResult> searchSeries(String query) throws JSONException {
		return searchSeries(query, -1);
	}

	@SuppressWarnings("nls")
	public static List<TMDBSimpleResult> searchSeries(String query, int year) {
		String url = URL_BASE + URL_SEARCHSERIES + "?api_key=" + API_KEY + "&query=" + HTTPUtilities.escapeURL(query);
		
		if (year > 0)
			url += "&year="+year;
		
		String json = HTTPUtilities.getRateLimitedHTML(url, false, false);
		try {
			JSONObject root = new JSONObject(new JSONTokener(json));
			
			JSONArray results = root.getJSONArray("results");
			
			List<TMDBSimpleResult> out = new ArrayList<>();
			for (int i = 0; i < results.length(); i++) {
				JSONObject result = results.getJSONObject(i);
		
				String title = result.getString("name");
				String id = "tv/" + result.getInt("id");
				
				out.add(new TMDBSimpleResult(id, title));
			}
			return out;
		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TMDBApiError", json), e);
			return new ArrayList<>();
		}
	}

	public static List<TMDBSimpleResult> searchMulti(String query) throws JSONException {
		return searchMulti(query, -1);
	}

	@SuppressWarnings("nls")
	public static List<TMDBSimpleResult> searchMulti(String query, int year) {
		String url = URL_BASE + URL_SEARCHMULTI + "?api_key=" + API_KEY + "&query=" + HTTPUtilities.escapeURL(query);
		
		if (year > 0)
			url += "&year="+year;
		
		String json = HTTPUtilities.getRateLimitedHTML(url, false, false);
		try {
			JSONObject root = new JSONObject(new JSONTokener(json));
			
			JSONArray results = root.getJSONArray("results");
			
			List<TMDBSimpleResult> out = new ArrayList<>();
			for (int i = 0; i < results.length(); i++) {
				JSONObject result = results.getJSONObject(i);

				String type = result.getString("media_type");
				String title;
				if (type.equals("tv"))
					title = result.getString("name");
				else
					title = result.getString("title");
				
				String id = type + "/" + result.getInt("id");
				
				out.add(new TMDBSimpleResult(id, title));
			}
			return out;
		} catch (JSONException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TMDBApiError", json), e);
			return new ArrayList<>();
		}
	}
	
	public static TMDBFullResult getMetadata(String id) {
		if (CCProperties.getInstance().PROP_TMDB_LANGUAGE.getValue() == BrowserLanguage.ENGLISH) {
			
			// simple call
			
			return getMetadataInternal(id, BrowserLanguage.ENGLISH);
		} else {
			
			// specific call but fallback to en-Us for missing fields
			
			TMDBFullResult base = getMetadataInternal(id, BrowserLanguage.ENGLISH);
			TMDBFullResult ext = getMetadataInternal(id, CCProperties.getInstance().PROP_TMDB_LANGUAGE.getValue());
			
			if (ext.Title == null || ext.Title.isEmpty()) ext.Title = base.Title;
			if (ext.Year == 0) ext.Year = base.Year;
			if (ext.Length == 0) ext.Length = base.Length;
			if (ext.Title == null || ext.Title.isEmpty()) ext.CoverPath = base.CoverPath;
			if (ext.Score == 0) ext.Score = base.Score;
			if (ext.Genres == null || ext.Genres.isEmpty()) ext.Genres = base.Genres;
			if (ext.ImdbRef == null || ext.ImdbRef.isUnset()) ext.ImdbRef = base.ImdbRef;
			
			return ext;
		}
	}

	@SuppressWarnings("nls")
	public static TMDBFullResult getMetadataInternal(String id, BrowserLanguage lang) {
		String url = URL_BASE + id + "?api_key=" + API_KEY;
		
		if (CCProperties.getInstance().PROP_TMDB_LANGUAGE.getValue() != BrowserLanguage.ENGLISH) {
			url += "&language=" + CCProperties.getInstance().PROP_TMDB_LANGUAGE.getValue().asLanguageID();
		}
		
		String json = HTTPUtilities.getRateLimitedHTML(url, false, false);
		try {
			TMDBFullResult result = new TMDBFullResult();
			
			JSONObject root = new JSONObject(new JSONTokener(json));

			result.Title = hasString(root, "title") ? root.getString("title") : root.getString("name");
			result.Year = hasString(root, "release_date") ? CCDate.parse(root.getString("release_date"), "yyyy-MM-dd").getYear() : 0;
			result.Length = root.optInt("runtime", 0);
			result.CoverPath = hasString(root, "poster_path") ? (URL_IMAGE_BASE + root.getString("poster_path")) : "";
			result.Score = (int) Math.round(root.optDouble("vote_average", 0));
			
			result.Genres = new CCGenreList();
			if (root.has("genres")) {
				JSONArray jsonGenres = root.getJSONArray("genres");
				for (int i = 0; i < jsonGenres.length(); i++) {
					CCGenre g = CCGenre.parseFromTMDbID(jsonGenres.getJSONObject(i).getInt("id"));
					if (g.isValid())
						result.Genres.addGenre(g);
				}
			}
			
			if (hasString(root, "imdb_id")) 
				result.ImdbRef = CCOnlineReference.createIMDB(root.getString("imdb_id"));
			else
				result.ImdbRef = CCOnlineReference.createNone();
			
			return result;
			
		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TMDBApiError", json), e);
			return null;
		}
	}
	
	private static boolean hasString(JSONObject obj, String ident) {
		return obj.has(ident) && !obj.isNull(ident) && !obj.getString(ident).isEmpty();
	}

	public static CCOnlineReference findMovieDirect(String searchText) {
		return findMovieDirect(searchText, -1);
	}
	
	public static CCOnlineReference findMovieDirect(String searchText, int year) {
		List<TMDBSimpleResult> r0 = searchMovies(searchText, year);
		if (r0.isEmpty()) {
			return CCOnlineReference.createNone();
		}
		
		return CCOnlineReference.createTMDB(r0.get(0).ID);
	}

	public static CCOnlineReference findSeriesDirect(String searchText) {
		return findSeriesDirect(searchText, -1);
	}

	public static CCOnlineReference findSeriesDirect(String searchText, int year) {
		List<TMDBSimpleResult> r0 = searchSeries(searchText, year);
		if (r0.isEmpty()) {
			return CCOnlineReference.createNone();
		}
		
		return CCOnlineReference.createTMDB(r0.get(0).ID);
	}

	@SuppressWarnings("nls")
	public static List<String> findCovers(CCOnlineReference ref) {
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
}
