package de.jClipCorn.util;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.types.NamedPathVar;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.helper.ApplicationHelper;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MoviePlayer {
	@SuppressWarnings("nls")
	private final static String[] PATHS_WIN = {
		FilesystemUtils.combineWithFSPathSeparator("Programme", "VideoLAN", "VLC", "vlc.exe"),
		FilesystemUtils.combineWithFSPathSeparator("Program Files", "VideoLAN", "VLC", "vlc.exe"),
		FilesystemUtils.combineWithFSPathSeparator("Programme (x86)", "VideoLAN", "VLC", "vlc.exe"),
		FilesystemUtils.combineWithFSPathSeparator("Program Files (x86)", "VideoLAN", "VLC", "vlc.exe"),
		FilesystemUtils.combineWithFSPathSeparator("VideoLAN", "VLC", "vlc.exe"),
		FilesystemUtils.combineWithFSPathSeparator("VLC", "vlc.exe")
	};

	@SuppressWarnings("nls")
	private final static String[] PATHS_NIX = {
		"/bin/vlc",
		"/usr/bin/vlc",
	};
	
	private final static String DRIVE_1 = "C:" + FSPath.SEPERATOR; //$NON-NLS-1$
	private final static String DRIVE_2 = "H:" + FSPath.SEPERATOR; //$NON-NLS-1$
	
	private static String lastVLCPath = null;
	
	public static void play(CCMovie mov, NamedPathVar player) {
		List<FSPath> al = new ArrayList<>();
		for (var p: mov.getParts()) al.add(p.toFSPath());
		play(al, player);
	}
	
	public static void play(CCEpisode ep, NamedPathVar player) {
		play(ep.Part.get().toFSPath(), player);
	}
	
	public static void play(FSPath abspath, NamedPathVar player) {
		List<FSPath> al = new ArrayList<>();
		al.add(abspath);
		play(al, player);
	}

	@SuppressWarnings("nls")
	public static List<String> getParameters(String path, NamedPathVar player) {
		List<String> parameters = new ArrayList<>();

		parameters.add(path);

		if (player != null)
		{
			if (!Str.isNullOrWhitespace(player.Arguments)) parameters.addAll(List.of(player.Arguments.split(" ")));

			return parameters;
		}
		else
		{
			parameters.add("--no-random");
			parameters.add("--no-loop");
			parameters.add("--no-repeat");
			parameters.add("--playlist-enqueue");

			if (CCProperties.getInstance().PROP_PLAY_VLC_FULLSCREEN.getValue()) parameters.add("--fullscreen");
			if (! CCProperties.getInstance().PROP_PLAY_VLC_AUTOPLAY.getValue()) parameters.add("--no-playlist-autostart");
			if (CCProperties.getInstance().PROP_PLAY_VLCSINGLEINSTANCEMODE.getValue()) parameters.add("--one-instance");

			if (CCProperties.getInstance().PROP_VLC_ROBOT_ENABLED.getValue())
			{
				parameters.add("--http-host=127.0.0.1");
				parameters.add("--http-port=" + CCProperties.getInstance().PROP_VLC_ROBOT_PORT.getValue());
				parameters.add("--http-password=" + CCProperties.getInstance().PROP_VLC_ROBOT_PASSWORD.getValue());
			}

			return parameters;
		}
	}

	@SuppressWarnings("nls")
	public static void play(List<FSPath> abspaths, NamedPathVar player) {
		String playerpath = (player == null) ? getVLCPath() : player.Path;
		
		if (Str.isNullOrWhitespace(playerpath)) {
			CCLog.addWarning(LocaleBundle.getString("LogMessage.VLCNotFound"));
			
			if (CCProperties.getInstance().PROP_PLAY_USESTANDARDONMISSINGVLC.getValue()) {
				try {
					for (var s : abspaths) {
						Desktop.getDesktop().open(s.toFile());
					}
				} catch (IOException | IllegalArgumentException e) {
					CCLog.addError(e);
				}
			}
		} else {
			List<String> parameters = getParameters(playerpath, player);
			
			for (var abspath : abspaths) {
				if (ApplicationHelper.isWindows()) {
					parameters.add("\"" + abspath.toAbsolutePathString() + "\"");
				} else {
					parameters.add(abspath.toAbsolutePathString());
				}
			}
			
			try {

				ProcessBuilder pb = new ProcessBuilder(parameters);
				pb.redirectOutput(new File(ApplicationHelper.getNullFile()));
				pb.redirectError(new File(ApplicationHelper.getNullFile()));
				pb.start();

			} catch (IOException e) {
				CCLog.addError(e);
			}
		}
	}
	
	public static String getVLCPath() {
		String vlcpath = CCProperties.getInstance().PROP_PLAY_VLC_PATH.getValue();
		
		if (lastVLCPath != null) {
			if (new File(lastVLCPath).exists()) {
				return lastVLCPath;
			}
		}
		
		if (! vlcpath.isEmpty()) {
			if (new File(vlcpath).exists()) {
				return lastVLCPath = vlcpath;
			}
		}
		
		if (ApplicationHelper.isWindows())
		{
			for (String ss : PATHS_WIN) {
				String path = DRIVE_1 + ss;
				if (new File(path).exists()) {
					return lastVLCPath = path;
				}
			}
			
			for (String ss : PATHS_WIN) {
				String path = DRIVE_2 + ss;
				if (new File(path).exists()) {
					return lastVLCPath = path;
				}
			}
			
			for (File f : File.listRoots()) {
				if (! (f.getAbsolutePath().equals(DRIVE_1) | f.getAbsolutePath().equals(DRIVE_2))) {
					for (String ss : PATHS_WIN) {
						String path = f.getAbsolutePath() + ss;
						if (new File(path).exists()) {
							return lastVLCPath = path;
						}
					}
				}
			}
		} else if (ApplicationHelper.isUnix()) {
			
			for (String path : PATHS_NIX) {
				if (new File(path).exists()) {
					return lastVLCPath = path;
				}
			}
		}
		
		lastVLCPath = null;
		return null;
	}
}
