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

	public AtomicInteger RequestCountGet   = new AtomicInteger();
	public AtomicInteger RequestCountPost  = new AtomicInteger();
	public AtomicInteger RequestCountImage = new AtomicInteger();

	public static WebConnectionLayer create(CCProperties ccprops)
	{
		var connReal = new RealWebConnection();
		connReal.init();

		if (ccprops.PROP_DEBUG_USE_HTTPCACHE.getValue())
		{
			var cache = new CachedWebConnection(connReal, ccprops);
			cache.init();
			return cache;
		}
		else
		{
			return connReal;
		}
	}

	public int getTotalRequestCount() {
		return RequestCountGet.get() + RequestCountPost.get() + RequestCountImage.get();
	}

	public abstract void init();
	
	public abstract String getUncaughtHTML(URL url, boolean stripLineBreaks) throws IOException, HTTPErrorCodeException;
	public abstract Tuple<String, List<Tuple<String, String>>> getUncaughtPostContent(URL url, String body) throws IOException, HTTPErrorCodeException;
	public abstract BufferedImage getImage(String urlToRead);
}
