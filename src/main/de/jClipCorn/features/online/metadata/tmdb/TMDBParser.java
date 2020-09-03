package de.jClipCorn.features.online.metadata.tmdb;

import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.OnlineSearchType;
import de.jClipCorn.features.online.cover.imdb.AgeRatingParser;
import de.jClipCorn.features.online.metadata.Metadataparser;
import de.jClipCorn.features.online.metadata.OnlineMetadata;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.BrowserLanguage;
import de.jClipCorn.properties.enumerations.MetadataParserImplementation;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.stream.CCStreams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class TMDBParser extends Metadataparser {
	private final static String API_KEY = new String(Base64.getDecoder().decode("M2ZkYWMyNzcxYWY4ZjZlZjljNWY0ZDc4ZmEyOWRjZDUAAAA"), StandardCharsets.UTF_8).trim();  //$NON-NLS-1$
	private final static String URL_BASE = "http://api.themoviedb.org/3/"; //$NON-NLS-1$

	private final static String URL_IMAGE_BASE = "http://image.tmdb.org/t/p/original"; //$NON-NLS-1$
	
	private final static String URL_SEARCHMOVIE = "search/movie"; //$NON-NLS-1$
	private final static String URL_SEARCHSERIES = "search/tv"; //$NON-NLS-1$
	private final static String URL_SEARCHMULTI = "search/multi"; //$NON-NLS-1$
	private final static String URL_SEARCHCOVER = "/images"; //$NON-NLS-1$

	@Override
	public List<Tuple<String, CCSingleOnlineReference>> searchByText(String text, OnlineSearchType type) {
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
	private List<Tuple<String, CCSingleOnlineReference>> searchMovies(String query, int year) throws JSONException {
		String url = URL_BASE + URL_SEARCHMOVIE + "?api_key=" + API_KEY + "&query=" + HTTPUtilities.escapeURL(query);
		
		if (year > 0) url += "&year="+year;
		
		String json = HTTPUtilities.getRateLimitedHTML(url, false, false);
		try {
			JSONObject root = new JSONObject(new JSONTokener(json));
			
			JSONArray results = root.getJSONArray("results");
			
			List<Tuple<String, CCSingleOnlineReference>> out = new ArrayList<>();
			for (int i = 0; i < results.length(); i++) {
				JSONObject result = results.getJSONObject(i);
		
				String title = result.getString("title");
				CCSingleOnlineReference id = CCSingleOnlineReference.createTMDB("movie/" + result.getInt("id"));

				out.add(Tuple.Create(title, id));
			}
			
			return CCStreams.iterate(out).unique(p -> p.Item2).enumerate();
		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TMDBApiError", json), e);
			return new ArrayList<>();
		}
	}

	@SuppressWarnings("nls")
	private List<Tuple<String, CCSingleOnlineReference>> searchSeries(String query, int year) {
		String url = URL_BASE + URL_SEARCHSERIES + "?api_key=" + API_KEY + "&query=" + HTTPUtilities.escapeURL(query);
		
		if (year > 0)
			url += "&year="+year;
		
		String json = HTTPUtilities.getRateLimitedHTML(url, false, false);
		try {
			JSONObject root = new JSONObject(new JSONTokener(json));
			
			JSONArray results = root.getJSONArray("results");

			List<Tuple<String, CCSingleOnlineReference>> out = new ArrayList<>();
			for (int i = 0; i < results.length(); i++) {
				JSONObject result = results.getJSONObject(i);
		
				String title = result.getString("name");
				CCSingleOnlineReference id = CCSingleOnlineReference.createTMDB("tv/" + result.getInt("id"));

				out.add(Tuple.Create(title, id));
			}
			
			return CCStreams.iterate(out).unique(p -> p.Item2).enumerate();
		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TMDBApiError", json), e);
			return new ArrayList<>();
		}
	}

	@SuppressWarnings("nls")
	private List<Tuple<String, CCSingleOnlineReference>> searchMulti(String query, int year) {
		String url = URL_BASE + URL_SEARCHMULTI + "?api_key=" + API_KEY + "&query=" + HTTPUtilities.escapeURL(query);
		
		if (year > 0)
			url += "&year="+year;
		
		String json = HTTPUtilities.getRateLimitedHTML(url, false, false);
		try {
			JSONObject root = new JSONObject(new JSONTokener(json));
			
			JSONArray results = root.getJSONArray("results");

			List<Tuple<String, CCSingleOnlineReference>> out = new ArrayList<>();
			for (int i = 0; i < results.length(); i++) {
				JSONObject result = results.getJSONObject(i);

				String type = result.getString("media_type");
				String title;
				if (type.equals("tv"))
					title = result.getString("name");
				else
					title = result.getString("title");

				CCSingleOnlineReference id = CCSingleOnlineReference.createTMDB(type + "/" + result.getInt("id"));

				out.add(Tuple.Create(title, id));
			}
			
			return CCStreams.iterate(out).unique(p -> p.Item2).enumerate();
		} catch (JSONException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TMDBApiError", json), e);
			return new ArrayList<>();
		}
	}

	@Override
	public OnlineMetadata getMetadata(CCSingleOnlineReference ref, boolean downloadCover) {
		if (CCProperties.getInstance().PROP_TMDB_LANGUAGE.getValue() == BrowserLanguage.ENGLISH) {
			
			// simple call
			
			return getMetadataInternal(ref, BrowserLanguage.ENGLISH, downloadCover);
		} else {
			// specific call but fallback to en-Us for missing fields
			
			OnlineMetadata base = getMetadataInternal(ref, BrowserLanguage.ENGLISH, downloadCover);
			OnlineMetadata ext = getMetadataInternal(ref, CCProperties.getInstance().PROP_TMDB_LANGUAGE.getValue(), downloadCover);
			
			if (ext == null) return base;
			
			ext.setMissingFields(base);
			
			return ext;
		}
	}

	@SuppressWarnings("nls")
	private OnlineMetadata getMetadataInternal(CCSingleOnlineReference ref, BrowserLanguage lang, boolean downloadCover) {
		String urlRaw = URL_BASE + ref.id + "?api_key=" + API_KEY;
		
		String url = urlRaw;
		
		if (CCProperties.getInstance().PROP_TMDB_LANGUAGE.getValue() != BrowserLanguage.ENGLISH) {
			url += "&language=" + CCProperties.getInstance().PROP_TMDB_LANGUAGE.getValue().asDinIsoID();
		}
		
		url += "&append_to_response=release_dates,content_ratings";
		
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

				List<CCGenre> genres = CCStreams
						.iterate(root.getJSONArray("genres"))
						.cast(JSONObject.class)
						.map(c -> CCGenre.parseFromTMDbID(c.getInt("id")))
						.flatten(CCStreams::iterate)
						.unique()
						.enumerate();
				
				result.Genres = new CCGenreList(genres);
			}
			
			if (hasString(root, "imdb_id")) 
				result.AltRef = CCSingleOnlineReference.createIMDB(root.getString("imdb_id"));
			
			if (result.FSKList == null && root.has("release_dates")) {
				JSONObject releases = root.getJSONObject("release_dates");
				if (releases.has("results")) {
					JSONArray results = releases.getJSONArray("results");
					
					result.FSKList = new HashMap<>();

					for (int i = 0; i < results.length(); i++) {

						JSONObject resObj = results.getJSONObject(i);
						
						String release_country = resObj.isNull("iso_3166_1") ? Str.Empty : resObj.getString("iso_3166_1");
						
						JSONArray release_dates = resObj.getJSONArray("release_dates");
						
						if (release_dates.length() == 1) {

							JSONObject rdObj = release_dates.getJSONObject(0);
							
							String release_lang = rdObj.isNull("iso_639_1") ? Str.Empty : rdObj.getString("iso_639_1");
							String release_cert = rdObj.getString("certification");
							if (release_cert.isEmpty()) continue;
							
							int release_age = AgeRatingParser.getMinimumAge(release_cert, url);
							if (release_age < 0) continue;
							
							if (release_country.equals(CCProperties.getInstance().PROP_TMDB_LANGUAGE.getValue().asCountryID())) result.FSK = CCFSK.getNearest(release_age);
							
							String iso = release_lang + "-" + release_country;
							if (release_lang.isEmpty()) iso = release_country;
							
							result.FSKList.put(iso, release_age);
							
						} else {
							
							for (int j = 0; j < release_dates.length(); j++) {

								JSONObject rdObj = release_dates.getJSONObject(j);
								
								String release_lang = rdObj.isNull("iso_639_1") ? Str.Empty : rdObj.getString("iso_639_1");
								String release_cert = rdObj.getString("certification");
								String release_note = hasString(rdObj, "note") ? rdObj.getString("note") : "";
								if (release_cert.isEmpty()) continue;

								int release_age = AgeRatingParser.getMinimumAge(release_cert, url);
								if (release_age < 0) continue;
								
								if (release_country.equals(CCProperties.getInstance().PROP_TMDB_LANGUAGE.getValue().asCountryID())) result.FSK = CCFSK.getNearest(release_age);

								String iso = release_lang + "-" + release_country;
								if (release_lang.isEmpty()) iso = release_country;
								
								if (release_note.isEmpty())
									iso +=  " (" + (j+1) + ")";
								else
									iso +=  " (" + (j+1) + ": " + release_note + ")";
								
								result.FSKList.put(iso, release_age);
								
							}
							
						}
						
					}
				}
			}
			
			if (result.FSKList == null && root.has("content_ratings")) {

				JSONObject ratings = root.getJSONObject("content_ratings");
				if (ratings.has("results")) {
					JSONArray results = ratings.getJSONArray("results");
					
					result.FSKList = new HashMap<>();

					for (int i = 0; i < results.length(); i++) {
						String release_country = results.getJSONObject(i).getString("iso_3166_1");
						String release_cert = results.getJSONObject(i).getString("rating");
						if (release_cert.isEmpty()) continue;
						
						int release_age = AgeRatingParser.getMinimumAge(release_cert, url);
						if (release_age < 0) continue;
						
						if (release_country.equals(CCProperties.getInstance().PROP_TMDB_LANGUAGE.getValue().asCountryID())) result.FSK = CCFSK.getNearest(release_age);
						
						result.FSKList.put(release_country, release_age);
					}
				}
			}
			
			if (result.FSK == null && result.FSKList != null && result.FSKList.size() > 0) {

				int count = 0;
				double sum = 0;
				for (Integer geni: result.FSKList.values()) {
					count++;
					sum += geni;
				}
				
				if (count <= 0) {
					CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotFindFSK", url));
				} else {
					sum = Math.round(sum/count);
					
					result.FSK = CCFSK.getNearest((int) sum);
				}
			}
			
			return result;
			
		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TMDBApiError", json), e);
			return null;
		}
	}
	
	private static boolean hasString(JSONObject obj, String ident) {
		return obj.has(ident) && !obj.isNull(ident) && !obj.getString(ident).isEmpty();
	}

	public CCSingleOnlineReference findMovieDirect(String searchText) {
		return findMovieDirect(searchText, -1);
	}
	
	private CCSingleOnlineReference findMovieDirect(String searchText, int year) {
		List<Tuple<String, CCSingleOnlineReference>> r0 = searchMovies(searchText, year);
		if (r0.isEmpty()) return CCSingleOnlineReference.EMPTY;

		return r0.get(0).Item2;
	}

	public CCSingleOnlineReference findSeriesDirect(String searchText) {
		return findSeriesDirect(searchText, -1);
	}

	private CCSingleOnlineReference findSeriesDirect(String searchText, int year) {
		List<Tuple<String, CCSingleOnlineReference>> r0 = searchSeries(searchText, year);
		if (r0.isEmpty()) return CCSingleOnlineReference.EMPTY;
		
		return r0.get(0).Item2;
	}

	@SuppressWarnings("nls")
	public List<String> findCovers(CCSingleOnlineReference ref) {
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
