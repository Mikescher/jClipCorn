package de.jClipCorn.util.vlcquery;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.exceptions.HTTPErrorCodeException;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.WindowsJNAHelper;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;
import de.jClipCorn.util.xml.CCXMLParser;
import org.apache.commons.text.StringEscapeUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("nls")
public class VLCConnection {

	// https://wiki.videolan.org/VLC_HTTP_requests/

	private final CCMovieList movielist;

	public VLCConnection(CCMovieList ml) {
		movielist = ml;
	}

	public CCProperties ccprops() {
		return movielist.ccprops();
	}

	private CCXMLParser getXML(String path) throws IOException, CCXMLException, HTTPErrorCodeException {
		HttpURLConnection conn = null;
		BufferedReader rd = null;
		boolean first = true;
		try
		{
			if (ApplicationHelper.isWindows() && !ApplicationHelper.isProcessRunning("vlc.exe")) return null;
			if (ApplicationHelper.isUnix()    && !ApplicationHelper.isProcessRunning("vlc")) return null;
			if (ApplicationHelper.isMac()     && !ApplicationHelper.isProcessRunning("vlc")) return null;

			conn = (HttpURLConnection) new URL("http://127.0.0.1:" + ccprops().PROP_VLC_ROBOT_PORT.getValue() + path).openConnection();

			conn.setRequestMethod("GET");

			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.addRequestProperty("User-Agent", "Mozilla/4.76");
			conn.addRequestProperty("Authorization", "Basic " + Str.toBase64(":" + ccprops().PROP_VLC_ROBOT_PASSWORD.getValue()));

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

	public VLCStatus getStatus()
	{
		if (!ccprops().PROP_VLC_ROBOT_ENABLED.getValue())
			return new VLCStatus(VLCPlayerStatus.DISABLED, -1, -1, null, null, new ArrayList<>(), null, false, 0, false, false, false, null);

		try
		{
			CCXMLParser xmlStatus   = getXML("/requests/status.xml");   //$NON-NLS-1$
			CCXMLParser xmlPlaylist = getXML("/requests/playlist.xml"); //$NON-NLS-1$

			if (xmlStatus   == null) return new VLCStatus(VLCPlayerStatus.NOT_RUNNING, -1, -1, null, null, new ArrayList<>(), null, false, 0, false, false, false, null);
			if (xmlPlaylist == null) return new VLCStatus(VLCPlayerStatus.NOT_RUNNING, -1, -1, null, null, new ArrayList<>(), null, false, 0, false, false, false, null);

			Rectangle rect = WindowsJNAHelper.getSingleWindowPositionOrNull("vlc.exe");

			var playlistTuple = parsePlaylist(xmlPlaylist);
			List<VLCStatusPlaylistEntry> playlist = playlistTuple.Item2;
			VLCStatusPlaylistEntry activeEntry = playlistTuple.Item1;

			String state = xmlStatus.getRoot().getFirstChildValueOrThrow("state"); //$NON-NLS-1$

			boolean fullscreen = xmlStatus.getRoot().getFirstChildValueOrThrow("fullscreen").equalsIgnoreCase("true");
			int volume = xmlStatus.getRoot().getFirstChildIntValueOrThrow("volume");
			boolean random = xmlStatus.getRoot().getFirstChildValueOrThrow("random").equalsIgnoreCase("true");
			boolean loop = xmlStatus.getRoot().getFirstChildValueOrThrow("loop").equalsIgnoreCase("true");
			boolean repeat = xmlStatus.getRoot().getFirstChildValueOrThrow("repeat").equalsIgnoreCase("true");

			if ("playing".equalsIgnoreCase(state)) //$NON-NLS-1$
			{
				String filename = xmlStatus
						.getRoot()
						.getFirstChildOrThrow("information")                    //$NON-NLS-1$
						.getFirstChildByAttrOrThrow("category", "name", "meta") //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
						.getFirstChildByAttrOrThrow("info", "name", "filename") //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
						.getContent();

				if (filename.contains("&#")) filename = StringEscapeUtils.unescapeHtml4(filename);

				String title = xmlStatus
						.getRoot()
						.getFirstChildOrThrow("information")                             //$NON-NLS-1$
						.getFirstChildByAttrOrThrow("category", "name", "meta")          //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
						.getFirstChildValueByAttrOrDefault("info", "name", "title", ""); //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$  //$NON-NLS-4$

				int time   = xmlStatus.getRoot().getFirstChildIntValueOrThrow("time"); //$NON-NLS-1$
				int length = xmlStatus.getRoot().getFirstChildIntValueOrThrow("length"); //$NON-NLS-1$

				if (length == -1 && activeEntry != null && activeEntry.Length != -1) length = activeEntry.Length;

				return new VLCStatus(VLCPlayerStatus.PLAYING, time, length, filename, title, playlist, activeEntry, fullscreen, volume, random, loop, repeat, rect);
			}
			else if ("paused".equalsIgnoreCase(state)) //$NON-NLS-1$
			{
				String filename = xmlStatus
						.getRoot()
						.getFirstChildOrThrow("information")                    //$NON-NLS-1$
						.getFirstChildByAttrOrThrow("category", "name", "meta") //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
						.getFirstChildByAttrOrThrow("info", "name", "filename") //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
						.getContent();

				if (filename.contains("&#")) filename = StringEscapeUtils.unescapeHtml4(filename);

				String title = xmlStatus
						.getRoot()
						.getFirstChildOrThrow("information")                             //$NON-NLS-1$
						.getFirstChildByAttrOrThrow("category", "name", "meta")          //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
						.getFirstChildValueByAttrOrDefault("info", "name", "title", ""); //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$  //$NON-NLS-4$

				int time   = xmlStatus.getRoot().getFirstChildIntValueOrThrow("time"); //$NON-NLS-1$
				int length = xmlStatus.getRoot().getFirstChildIntValueOrThrow("length"); //$NON-NLS-1$

				if (length == -1 && activeEntry != null && activeEntry.Length != -1) length = activeEntry.Length;

				return new VLCStatus(VLCPlayerStatus.PAUSED, time, length, filename, title, playlist, activeEntry, fullscreen, volume, random, loop, repeat, rect);
			}
			else if ("stopped".equalsIgnoreCase(state)) //$NON-NLS-1$
			{
				return new VLCStatus(VLCPlayerStatus.STOPPED, -1, -1, null, null, playlist, activeEntry, fullscreen, volume, random, loop, repeat, rect);
			}
			else throw new Exception("Unknown Status: " + state);

		} catch (ConnectException e) {
			CCLog.addDebug(e.toString());
			return new VLCStatus(VLCPlayerStatus.NOT_RUNNING, -1, -1, null, null, new ArrayList<>(), null, false, 0, false, false, false, null);
		} catch (Exception e) {
			CCLog.addError(e);
			return new VLCStatus(VLCPlayerStatus.ERROR, -1, -1, null, null, new ArrayList<>(), null, false, 0, false, false, false, null);
		}
	}

	private Tuple<VLCStatusPlaylistEntry, List<VLCStatusPlaylistEntry>> parsePlaylist(CCXMLParser xmlPlaylist) throws CCXMLException {
		List<VLCStatusPlaylistEntry> playlist = new ArrayList<>();
		VLCStatusPlaylistEntry activeEntry = null;

		CCStream<CCXMLElement> nodes = xmlPlaylist
				.getRoot()
				.getFirstChildOrThrow("node") // name="playlist"   or   name="Wiedergabeliste"
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
		return Tuple.Create(activeEntry, playlist);
	}

	public boolean toggleRandom() {
		try	{
			getXML("/requests/status.xml?command=pl_random");
			return true;
		} catch (Exception e) {
			CCLog.addError(e);
			return false;
		}
	}

	public boolean toggleLoop() {
		try	{
			getXML("/requests/status.xml?command=pl_loop");
			return true;
		} catch (Exception e) {
			CCLog.addError(e);
			return false;
		}
	}

	public boolean toggleRepeat() {
		try	{
			getXML("/requests/status.xml?command=pl_repeat");
			return true;
		} catch (Exception e) {
			CCLog.addError(e);
			return false;
		}
	}

	public boolean toggleFullscreen() {
		try	{
			getXML("/requests/status.xml?command=fullscreen");
			return true;
		} catch (Exception e) {
			CCLog.addError(e);
			return false;
		}
	}

	public boolean play() {
		try	{
			getXML("/requests/status.xml?command=pl_play");
			return true;
		} catch (Exception e) {
			CCLog.addError(e);
			return false;
		}
	}

	public boolean playID(int id) {
		try	{
			getXML("/requests/status.xml?command=pl_play&id="+id);
			return true;
		} catch (Exception e) {
			CCLog.addError(e);
			return false;
		}
	}

	public boolean pause() {
		try	{
			getXML("/requests/status.xml?command=pl_pause");
			return true;
		} catch (Exception e) {
			CCLog.addError(e);
			return false;
		}
	}

	public boolean stop() {
		try	{
			getXML("/requests/status.xml?command=pl_stop");
			return true;
		} catch (Exception e) {
			CCLog.addError(e);
			return false;
		}
	}

	public String enqueue(FSPath filepath, boolean startPlayback)
	{
		try
		{
			var uri = HTTPUtilities.getURLEncodedFilePath(filepath);

			if (startPlayback)
			{
				getXML("/requests/status.xml?command=in_play&input=" + uri);
			}
			else
			{
				getXML("/requests/status.xml?command=in_enqueue&input=" + uri);
			}

			CCXMLParser xmlPlaylist = getXML("/requests/playlist.xml"); //$NON-NLS-1$
			if (xmlPlaylist == null) return null;

			var playlist = parsePlaylist(xmlPlaylist);
			return CCStreams.iterate(playlist.Item2).lastOrNull().Uri;
		}
		catch (Exception e)
		{
			CCLog.addError(e);
			return null;
		}
	}

	public void startPlayer()
	{
		var vlcPathAndArgs = MoviePlayer.getVLCPath(ccprops());

		if (FSPath.isNullOrEmpty(vlcPathAndArgs.Item1)) {
			CCLog.addWarning(LocaleBundle.getString("LogMessage.VLCNotFound")); //$NON-NLS-1$
			return;
		}

		try {
			List<String> parameters = MoviePlayer.getParameters(vlcPathAndArgs, ccprops(), true);

			ProcessBuilder pb = new ProcessBuilder(parameters);
			pb.redirectOutput(new File(ApplicationHelper.getNullFile()));
			pb.redirectError(new File(ApplicationHelper.getNullFile()));
			pb.start();
		} catch (IOException e) {
			CCLog.addError(e);
		}
	}

	public void setWindowPosition(Rectangle rect)
	{
		WindowsJNAHelper.setSingleWindowPositionOrNull("vlc.exe", rect);
	}
}
