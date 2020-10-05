package de.jClipCorn.util.http;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.exceptions.HTTPErrorCodeException;
import de.jClipCorn.util.stream.CCStreams;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

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
			WebConnectionLayer.RequestCountGet.incrementAndGet();
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
	public Tuple<String, List<Tuple<String, String>>> getUncaughtPostContent(URL url, String body) throws IOException, HTTPErrorCodeException {
		HttpURLConnection conn;
		BufferedReader rd = null;
		String line;
		StringBuilder resultbuilder = new StringBuilder();
		List<Tuple<String, String>> resultHeader = null;
		try {
			WebConnectionLayer.RequestCountPost.incrementAndGet();
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestProperty("User-Agent", "Mozilla/4.76");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Content-length", String.valueOf(body.getBytes(Str.UTF8).length));
			conn.setDoInput(true);
			conn.setDoOutput(true);

			try (OutputStream os = conn.getOutputStream())
			{
				os.write(body.getBytes(Str.UTF8));
			}

			conn.connect();

			String encoding = conn.getContentEncoding();
			if (encoding == null) encoding = "UTF-8";

			switch (conn.getResponseCode()) {
				case HttpURLConnection.HTTP_MOVED_PERM:
				case HttpURLConnection.HTTP_MOVED_TEMP:
					return getUncaughtPostContent(new URL(url, conn.getHeaderField("location")), body);

				case HTTPUtilities.HTTP_TOO_MANY_REQUESTS:
				case HttpURLConnection.HTTP_NOT_FOUND:
				case HttpURLConnection.HTTP_FORBIDDEN:
					throw new HTTPErrorCodeException(conn.getResponseCode());
			}

			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
			while ((line = rd.readLine()) != null) {
				resultbuilder.append("\n");
				resultbuilder.append(line);
			}
			rd.close();
			resultHeader = CCStreams.iterate(conn.getHeaderFields()).flatten(k -> CCStreams.iterate(k.getValue()).map(v -> Tuple.Create(k.getKey(), v))).enumerate();
			conn.disconnect();
		} finally {
			if (rd != null) {
				rd.close();
			}
		}
		return Tuple.Create(resultbuilder.toString(), resultHeader);
	}
	
	@Override
	public BufferedImage getImage(String urlToRead) {
		try {
			URL url = new URL(urlToRead);
			WebConnectionLayer.RequestCountImage.incrementAndGet();
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/4.76");

			conn.connect();

			try(InputStream is = conn.getInputStream()) {
				return ImageIO.read(is);
			}

		} catch (IOException | OutOfMemoryError e) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotGetImage", urlToRead));
			return null;
		}
	}
}
