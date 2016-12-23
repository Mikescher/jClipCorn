package de.jClipCorn.util.http;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.exceptions.HTTPErrorCodeException;

@SuppressWarnings("nls")
public class HTTPUtilities {
	public final static int HTTP_TOO_MANY_REQUESTS = 429;
	
	private final static int MAX_CONNECTION_TRY = 6;
	private final static int MAX_RATELIMIT_TIME = 10*1000;

	public static String getRateLimitedHTML(String urlToRead, boolean stripLineBreaks, boolean followRedirects) {
		if (urlToRead.isEmpty()) {
			return "";
		}
		
		int reconnects = 0;
		int delaySum = 0;
		int nextDelay = 750;

		try {
			URL url = new URL(urlToRead);

			while (true) {
				try {
					return WebConnectionLayer.instance.getUncaughtHTML(url, stripLineBreaks);
				} catch (HTTPErrorCodeException e) {
					if (e.Errorcode == HTTP_TOO_MANY_REQUESTS)
					{
						reconnects++;

						delaySum += nextDelay;
						
						if (delaySum > MAX_RATELIMIT_TIME) {
							CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotGetHTML", urlToRead), e);
							return "";
						}
						
						try { 
							Thread.sleep(nextDelay); 
						} catch (InterruptedException x) { 
							//NOP 
						}
						
						nextDelay *= 2;
					} else {
						reconnects++;
						if (reconnects >= MAX_CONNECTION_TRY) {
							CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotGetHTML", urlToRead), e);
							return "";
						}
					}
				} catch (IOException e) {
					reconnects++;
					if (reconnects >= MAX_CONNECTION_TRY) {
						CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotGetHTML", urlToRead), e);
						return "";
					}
				}
			}

		} catch (MalformedURLException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotGetHTML", urlToRead), e);
			return "";
		}
	}
	
	public static String getHTML(String urlToRead, boolean stripLineBreaks, boolean followRedirects) {
		if (urlToRead.isEmpty()) {
			return "";
		}

		try {
			URL url = new URL(urlToRead);

			for (int i = 0; i < MAX_CONNECTION_TRY; i++) {
				try {
					return WebConnectionLayer.instance.getUncaughtHTML(url, stripLineBreaks);
				} catch (IOException | HTTPErrorCodeException e) {
					if ((i + 1) == MAX_CONNECTION_TRY) {
						CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotGetHTML", urlToRead), e);
					}
				}
			}
		} catch (MalformedURLException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotGetHTML", urlToRead), e);
			return "";
		}

		return "";
	}

	public static String escapeURL(String url) {
		url = url.trim();

		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return url;
		}
	}

	public static String descapeHTML(String html) {
		html = html.trim();

		return StringEscapeUtils.unescapeHtml(html);
	}

	public static boolean openInBrowser(String url) {
		if (java.awt.Desktop.isDesktopSupported()) {
			java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

			try {
				java.net.URI uri = new java.net.URI(url);
				desktop.browse(uri);
			} catch (URISyntaxException e) {
				CCLog.addError(LocaleBundle.getString("LogMessage.WrongFormattedURI"), e);
				return false;
			} catch (IOException e) {
				CCLog.addError(LocaleBundle.getString("LogMessage.CouldNotLaunchWebbrowser"), e);
				return false;
			}
		} else {
			CCLog.addError(LocaleBundle.getString("LogMessage.UnsupportedDestkop"));
			return false;
		}

		return true;
	}

	public static BufferedImage getImage(String urlToRead) {
		return WebConnectionLayer.instance.getImage(urlToRead);
	}

	public static String getJavascriptHTML(String urlToRead, int jsTimeout) {
		//Otherwise we will get flooded with log warnings -.-
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);

		try (WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38)) {
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
			webClient.getOptions().setThrowExceptionOnScriptError(false);

			HtmlPage page = webClient.getPage(urlToRead);

			webClient.waitForBackgroundJavaScript(jsTimeout);

			return page.asXml();
		} catch (FailingHttpStatusCodeException | IOException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotGetHTML", urlToRead), e);
			return "";
		}
	}

	public static Map<String, String> splitQuery(String spec) throws UnsupportedEncodingException, MalformedURLException {
		return splitQuery(new URL(spec));
	}

	// http://stackoverflow.com/questions/13592236/parse-the-uri-string-into-name-value-collection-in-java
	public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
	    Map<String, String> query_pairs = new LinkedHashMap<>();
	    String query = url.getQuery();
	    String[] pairs = query.split("&");
	    
	    for (String pair : pairs) {
	        int idx = pair.indexOf("=");
	        query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
	    }
	    
	    return query_pairs;
	}

	public static boolean isURL(String spec) {
		try {
			new URL(spec);
		} catch (MalformedURLException e) {
			return false;
		}
		
		return true;
	}

	public static void searchInBrowser(String text) {
		openInBrowser("https://www.google.de/search?q=" + escapeURL(text));
	}
}