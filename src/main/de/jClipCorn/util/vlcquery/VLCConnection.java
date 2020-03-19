package de.jClipCorn.util.vlcquery;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.HTTPErrorCodeException;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;
import de.jClipCorn.util.xml.CCXMLParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("nls")
public class VLCConnection {

	private static CCXMLParser getXML(String path) throws IOException, CCXMLException, HTTPErrorCodeException {
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
		finally
		{
			if (rd != null) try { rd.close(); } catch  (Throwable e) {  /**/ }
		}
	}

	public static VLCStatus getStatus()
	{
		if (!CCProperties.getInstance().PROP_VLC_ROBOT_ENABLED.getValue()) return new VLCStatus(VLCStatus.VLCPlayerStatus.DISABLED, -1, -1, null, null, new ArrayList<>(), null);

		try
		{

			CCXMLParser xmlStatus   = getXML("/requests/status.xml");   //$NON-NLS-1$
			CCXMLParser xmlPlaylist = getXML("/requests/playlist.xml"); //$NON-NLS-1$

			if (xmlStatus   == null) return new VLCStatus(VLCStatus.VLCPlayerStatus.NOT_RUNNING, -1, -1, null, null, new ArrayList<>(), null);
			if (xmlPlaylist == null) return new VLCStatus(VLCStatus.VLCPlayerStatus.NOT_RUNNING, -1, -1, null, null, new ArrayList<>(), null);

			List<VLCStatusPlaylistEntry> playlist = new ArrayList<>();
			VLCStatusPlaylistEntry activeEntry = null;


			CCStream<CCXMLElement> nodes = xmlPlaylist
					.getRoot()
					.getFirstChildByAttrOrThrow("node", "name", "Playlist")
					.getAllChildren("leaf");

			for (CCXMLElement node : nodes)
			{
				VLCStatusPlaylistEntry entry = new VLCStatusPlaylistEntry(
						node.getAttributeValueOrThrow("uri"),
						node.getAttributeValueOrThrow("name"),
						node.getAttributeIntValueOrThrow("duration"));

				playlist.add(entry);

				if (node.getAttributeValueOrDefault("current", "").equalsIgnoreCase("current")) activeEntry = entry;
			}

			String state = xmlStatus.getRoot().getFirstChildValueOrThrow("state"); //$NON-NLS-1$

			if ("playing".equalsIgnoreCase(state)) //$NON-NLS-1$
			{
				String filename = xmlStatus
						.getRoot()
						.getFirstChildOrThrow("information")                    //$NON-NLS-1$
						.getFirstChildByAttrOrThrow("category", "name", "meta") //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
						.getFirstChildByAttrOrThrow("info", "name", "filename") //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
						.getContent();

				String title = xmlStatus
						.getRoot()
						.getFirstChildOrThrow("information")                             //$NON-NLS-1$
						.getFirstChildByAttrOrThrow("category", "name", "meta")          //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
						.getFirstChildValueByAttrOrDefault("info", "name", "title", ""); //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$  //$NON-NLS-4$

				int time   = xmlStatus.getRoot().getFirstChildIntValueOrThrow("time"); //$NON-NLS-1$
				int length = xmlStatus.getRoot().getFirstChildIntValueOrThrow("length"); //$NON-NLS-1$

				return new VLCStatus(VLCStatus.VLCPlayerStatus.PLAYING, time, length, filename, title, playlist, activeEntry);
			}
			else if ("stopped".equalsIgnoreCase(state)) //$NON-NLS-1$
			{
				return new VLCStatus(VLCStatus.VLCPlayerStatus.STOPPED, -1, -1, null, null, playlist, activeEntry);
			}
			else throw new Exception("Unknown Status: " + state);

		} catch (ConnectException e) {
			CCLog.addError(e);
			return new VLCStatus(VLCStatus.VLCPlayerStatus.NOT_RUNNING, -1, -1, null, null, new ArrayList<>(), null);
		} catch (Exception e) {
			CCLog.addError(e);
			return new VLCStatus(VLCStatus.VLCPlayerStatus.ERROR, -1, -1, null, null, new ArrayList<>(), null);
		}
	}
}
