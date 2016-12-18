package de.jClipCorn.util.http;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.exceptions.HTTPErrorCodeException;

public abstract class WebConnectionLayer {

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
	
	public abstract void init();
	
	public abstract String getUncaughtHTML(URL url, boolean stripLineBreaks) throws IOException, HTTPErrorCodeException;
	public abstract BufferedImage getImage(String urlToRead);
}
