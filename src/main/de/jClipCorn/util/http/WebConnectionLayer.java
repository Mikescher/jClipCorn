package de.jClipCorn.util.http;

import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.exceptions.HTTPErrorCodeException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class WebConnectionLayer {

	public static AtomicInteger RequestCountGet   = new AtomicInteger();
	public static AtomicInteger RequestCountPost  = new AtomicInteger();
	public static AtomicInteger RequestCountImage = new AtomicInteger();

	private static WebConnectionLayer connReal;
	private static WebConnectionLayer connCache;
	
	public static WebConnectionLayer instance;
	
	static {
		connReal = new RealWebConnection();
		connCache = new CachedWebConnection(connReal);
		
		if (CCProperties.getInstance().PROP_DEBUG_USE_HTTPCACHE.getValue()) {
			instance = connCache;
		} else {
			instance = connReal;
		}
		
		instance.init();
	}

	public static int getTotalRequestCount() {
		return RequestCountGet.get() + RequestCountPost.get() + RequestCountImage.get();
	}

	public abstract void init();
	
	public abstract String getUncaughtHTML(URL url, boolean stripLineBreaks) throws IOException, HTTPErrorCodeException;
	public abstract Tuple<String, List<Tuple<String, String>>> getUncaughtPostContent(URL url, String body) throws IOException, HTTPErrorCodeException;
	public abstract BufferedImage getImage(String urlToRead);
}
