package de.jClipCorn.util.parser.imageparser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.HTTPUtilities;

@SuppressWarnings("nls")
public class GoogleImageParser {
	private final static String KEY_DATA = "responseData";
	private final static String KEY_RESULTS = "results";
	private final static String KEY_URL = "unescapedUrl";
	
	private final static String SEARCH_APPENDIX = " cover";
	
	private final static String BASE_URL = "https://ajax.googleapis.com";
	private final static String SEARCH_URL = "/ajax/services/search/images?v=1.0&rsz=8&q=%s&userip=127.0.0.1";
	
	public static String getSearchURL(String title) {
		return String.format(BASE_URL + SEARCH_URL, HTTPUtilities.escapeURL(title + SEARCH_APPENDIX));
	}
	
	public static ArrayList<String> extractImageLinks(String json) {
		ArrayList<String> result = new ArrayList<>();
		
		JSONObject jobj;
		try {
			jobj = new JSONObject(json);
		} catch (JSONException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotParseJSON", json), e); //$NON-NLS-1$
			return result;
		}
		
		JSONArray jsonresults;
		try {
			jsonresults = jobj.getJSONObject(KEY_DATA).getJSONArray(KEY_RESULTS);
		} catch (JSONException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotParseJSON", json), e); //$NON-NLS-1$
			return result;
		}
		
		for (int i = 0; i < jsonresults.length(); i++) {
			try {
				String nurl = jsonresults.getJSONObject(i).getString(KEY_URL);
				result.add(nurl);
			} catch (JSONException e) {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotParseJSON", json), e); //$NON-NLS-1$
				return result;
			}
		}
		
		return result;
	}
}
