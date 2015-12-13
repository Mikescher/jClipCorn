package de.jClipCorn.util.parser.imageparser;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.helper.HTTPUtilities;
import de.jClipCorn.util.listener.ProgressCallbackListener;

@SuppressWarnings("nls")
public class GoogleImageParser {
	public final static String SEARCH_APPENDIX_1 = "cover";
	public final static String SEARCH_APPENDIX_2 = "poster";

	public final static int HTMLUNIT_JS_TIMEOUT = 5 * 1000; // 5s
	
	private final static String BASE_URL = "https://www.google.com";
	private final static String SEARCH_URL = "/search?q=%s&tbm=isch&tbm=isch&safe=off&num=16";

	private final static String JSOUP_SEARCHPAGE_IMAGE = "a[href*=imgurl]:has(img[src])"; // a[href*=imgurl]:has(img[src])
	
	public static String getSearchURL(String title, String appendix) {
		return String.format(BASE_URL + SEARCH_URL, HTTPUtilities.escapeURL(title + " " + appendix));
	}
	
	public static List<String> extractImageLinks(String html, int max, CopyOnWriteArrayList<String> exclusions, ProgressCallbackListener progress) {
		List<String> result = new ArrayList<>();
		
		Elements images = Jsoup.parse(html).select(JSOUP_SEARCHPAGE_IMAGE);
		
		int count = 0;
		for (Iterator<Element> it = images.iterator(); it.hasNext();) {
			if (count == max) return result;
			
			String url = it.next().attr("href");
			
			if (exclusions.contains(url)) continue;
			exclusions.add(url);
			
			if (! url.isEmpty()) {
				try {
					Map<String, String> parameter = HTTPUtilities.splitQuery(url);
					
					if (parameter.containsKey("imgurl") && !parameter.get("imgurl").isEmpty() && HTTPUtilities.isURL(parameter.get("imgurl"))) {
						count++;
						
						result.add(parameter.get("imgurl"));
					} else {
						CCLog.addError(LocaleBundle.getFormattedString("LogMessage.MalformedURL", url));
					}
				} catch (UnsupportedEncodingException | MalformedURLException e) {
					CCLog.addError(LocaleBundle.getFormattedString("LogMessage.MalformedURL", url), e);
				}
			}
			
			progress.step();
		}
		
		while (count != max) {
			progress.step();
			count++;
		}
		
		return result;
	}
}
