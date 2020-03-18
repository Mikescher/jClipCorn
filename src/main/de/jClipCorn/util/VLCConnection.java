package de.jClipCorn.util;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.exceptions.HTTPErrorCodeException;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.xml.CCXMLParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings("nls")
public class VLCConnection {

	public static CCXMLParser getXML(String path) {
		HttpURLConnection conn = null;
		BufferedReader rd = null;
		boolean first = true;
		try
		{
			if (ApplicationHelper.isWindows() && !ApplicationHelper.isProcessRunning("vlc.exe")) return null;
			if (ApplicationHelper.isUnix()    && !ApplicationHelper.isProcessRunning("vlc")) return null;
			if (ApplicationHelper.isMac()     && !ApplicationHelper.isProcessRunning("vlc")) return null;

			conn = (HttpURLConnection) new URL("http://127.0.0.1:" + CCProperties.getInstance().PROP_VLC_ROBOT_PORT.getValue() + path).openConnection();

			conn.setRequestMethod("GET");

			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.addRequestProperty("User-Agent", "Mozilla/4.76");
			conn.addRequestProperty("Authorization", "Basic " + Str.toBase64(":" + CCProperties.getInstance().PROP_VLC_ROBOT_PASSWORD.getValue()));

			String encoding = conn.getContentEncoding();
			if (encoding == null) encoding = "UTF-8";

			switch (conn.getResponseCode()) {
				case HttpURLConnection.HTTP_MOVED_PERM:
				case HttpURLConnection.HTTP_MOVED_TEMP:
				case HttpURLConnection.HTTP_NOT_FOUND:
				case HttpURLConnection.HTTP_FORBIDDEN:
				case HTTPUtilities.HTTP_TOO_MANY_REQUESTS:
					throw new HTTPErrorCodeException(conn.getResponseCode());
			}

			String line;
			StringBuilder resultbuilder = new StringBuilder();
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
			while ((line = rd.readLine()) != null) {
				if (!first) resultbuilder.append("\n");
				resultbuilder.append(line);
				first = false;
			}
			rd.close();
			conn.disconnect();

			return CCXMLParser.parse(resultbuilder.toString());
		}
		catch (Exception e)
		{
			CCLog.addDebug(e.toString());
			return null;
		}
		finally
		{
			if (rd != null) try { rd.close(); } catch  (Throwable e) {  /**/ }
		}
	}

}
