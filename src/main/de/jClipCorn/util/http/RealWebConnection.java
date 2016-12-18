package de.jClipCorn.util.http;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.exceptions.HTTPErrorCodeException;

@SuppressWarnings("nls")
public class RealWebConnection extends WebConnectionLayer {
	@Override
	public void init() {
		// NOP
	}

	@Override
	public String getUncaughtHTML(URL url, boolean stripLineBreaks) throws IOException, HTTPErrorCodeException {
		HttpURLConnection conn = null;
		BufferedReader rd = null;
		String line;
		StringBuilder resultbuilder = new StringBuilder();
		boolean first = true;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.addRequestProperty("User-Agent", "Mozilla/4.76");
			String encoding = conn.getContentEncoding();
			if (encoding == null) {
				encoding = "UTF-8";
			}

			switch (conn.getResponseCode()) {
			case HttpURLConnection.HTTP_MOVED_PERM:
			case HttpURLConnection.HTTP_MOVED_TEMP:
				return getUncaughtHTML(new URL(url, conn.getHeaderField("location")), stripLineBreaks);
			case HTTPUtilities.HTTP_TOO_MANY_REQUESTS:
			case HttpURLConnection.HTTP_NOT_FOUND:
			case HttpURLConnection.HTTP_FORBIDDEN:
				throw new HTTPErrorCodeException(conn.getResponseCode());
			}

			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
			while ((line = rd.readLine()) != null) {
				if (!first && !stripLineBreaks) {
					resultbuilder.append("\n");
				}
				resultbuilder.append(line);
				first = false;
			}
			rd.close();
			conn.disconnect();
		} finally {
			if (rd != null) {
				rd.close();
			}
		}
		return HTTPUtilities.descapeHTML(resultbuilder.toString());
	}
	
	@Override
	public BufferedImage getImage(String urlToRead) {
		try {
			URL url = new URL(urlToRead);
			return ImageIO.read(url);
		} catch (IOException e) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotGetImage", urlToRead));
			return null;
		} catch (OutOfMemoryError e) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotGetImage", urlToRead));
			return null;
		}
	}
}
