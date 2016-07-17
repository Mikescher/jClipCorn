package de.jClipCorn.util.parser;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenreList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.helper.HTTPUtilities;

public class TMDBParser {
	private final static String API_KEY = new String(Base64.getDecoder().decode("M2ZkYWMyNzcxYWY4ZjZlZjljNWY0ZDc4ZmEyOWRjZDUAAAA"), Charset.forName("UTF-8")).trim();  //$NON-NLS-1$//$NON-NLS-2$
	private final static String URL_BASE = "http://api.themoviedb.org/3/"; //$NON-NLS-1$

	private final static String URL_IMAGE_BASE = "http://image.tmdb.org/t/p/w342"; //$NON-NLS-1$
	
	private final static String URL_SEARCHMOVIE = "search/movie"; //$NON-NLS-1$
	private final static String URL_SEARCHSERIES = "search/tv"; //$NON-NLS-1$
	private final static String URL_SEARCHMULTI = "search/multi"; //$NON-NLS-1$
	
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
		public String ImdbID;
		public CCMovieGenreList Genres;
		public int Length;
	}
	
	@SuppressWarnings("nls")
	public static List<TMDBSimpleResult> searchMovies(String query) throws JSONException {
		String url = URL_BASE + URL_SEARCHMOVIE + "?api_key=" + API_KEY + "&query=" + HTTPUtilities.escapeURL(query);
		
		String json = HTTPUtilities.getHTML(url, false, false);
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

	@SuppressWarnings("nls")
	public static List<TMDBSimpleResult> searchSeries(String query) {
		String url = URL_BASE + URL_SEARCHSERIES + "?api_key=" + API_KEY + "&query=" + HTTPUtilities.escapeURL(query);
		
		String json = HTTPUtilities.getHTML(url, false, false);
		try {
			JSONObject root = new JSONObject(new JSONTokener(json));
			
			JSONArray results = root.getJSONArray("results");
			
			List<TMDBSimpleResult> out = new ArrayList<>();
			for (int i = 0; i < results.length(); i++) {
				JSONObject result = results.getJSONObject(i);
		
				String title = result.getString("title");
				String id = "tv/" + result.getInt("id");
				
				out.add(new TMDBSimpleResult(id, title));
			}
			return out;
		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TMDBApiError", json), e);
			return new ArrayList<>();
		}
	}

	@SuppressWarnings("nls")
	public static List<TMDBSimpleResult> searchMulti(String query) {
		String url = URL_BASE + URL_SEARCHMULTI + "?api_key=" + API_KEY + "&query=" + HTTPUtilities.escapeURL(query);
		
		String json = HTTPUtilities.getHTML(url, false, false);
		try {
			JSONObject root = new JSONObject(new JSONTokener(json));
			
			JSONArray results = root.getJSONArray("results");
			
			List<TMDBSimpleResult> out = new ArrayList<>();
			for (int i = 0; i < results.length(); i++) {
				JSONObject result = results.getJSONObject(i);
		
				String title = result.getString("title");
				String type = result.getString("media_type");
				String id = type + "/" + result.getInt("id");
				
				out.add(new TMDBSimpleResult(id, title));
			}
			return out;
		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TMDBApiError", json), e);
			return new ArrayList<>();
		}
	}
	
	@SuppressWarnings("nls")
	public static TMDBFullResult getMovieMetadata(String id) {
		String url = URL_BASE + id + "?api_key=" + API_KEY;
		
		String json = HTTPUtilities.getHTML(url, false, false);
		try {
			TMDBFullResult result = new TMDBFullResult();
			
			JSONObject root = new JSONObject(new JSONTokener(json));

			result.Title = root.getString("title");
			result.Year = CCDate.parse(root.getString("release_date"), "yyyy-MM-dd").getYear();
			result.Length = root.getInt("runtime");
			result.CoverPath = URL_IMAGE_BASE + root.getString("poster_path");
			result.Score = (int) Math.round(root.getDouble("vote_average"));
			
			result.Genres = new CCMovieGenreList();
			JSONArray jsonGenres = root.getJSONArray("genres");
			for (int i = 0; i < jsonGenres.length(); i++) {
				CCMovieGenre g = CCMovieGenre.parseFromTMDbID(jsonGenres.getJSONObject(i).getInt("id"));
				if (g.isValid())
					result.Genres.addGenre(g);
			}
			
			result.ImdbID = root.getString("imdb_id");
			
			return result;
			
		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TMDBApiError", json), e);
			return null;
		}
	}
}
