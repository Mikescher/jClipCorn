package de.jClipCorn.util;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.types.NamedPathVar;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ApplicationHelper;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MoviePlayer {
	@SuppressWarnings("nls")
	private final static String[] PATHS_WIN = {
		PathFormatter.combine("Programme", "VideoLAN", "VLC", "vlc.exe"),
		PathFormatter.combine("Program Files", "VideoLAN", "VLC", "vlc.exe"),
		PathFormatter.combine("Programme (x86)", "VideoLAN", "VLC", "vlc.exe"),
		PathFormatter.combine("Program Files (x86)", "VideoLAN", "VLC", "vlc.exe"),
		PathFormatter.combine("VideoLAN", "VLC", "vlc.exe"),
		PathFormatter.combine("VLC", "vlc.exe")
	};

	@SuppressWarnings("nls")
	private final static String[] PATHS_NIX = {
			"/bin/vlc",
			"/usr/bin/vlc",
	};
	
	private final static String DRIVE_1 = "C:" + PathFormatter.SEPERATOR; //$NON-NLS-1$
	private final static String DRIVE_2 = "H:" + PathFormatter.SEPERATOR; //$NON-NLS-1$
	
	private static String lastVLCPath = null;
	
	public static void play(CCMovie mov, NamedPathVar player) {
		List<String> al = new ArrayList<>();
		for (int i = 0; i < mov.getPartcount(); i++) {
			al.add(mov.getAbsolutePart(i));
		}
		play(al, player);
	}
	
	public static void play(CCEpisode ep, NamedPathVar player) {
		play(ep.getAbsolutePart(), player);
	}
	
	public static void play(String abspath, NamedPathVar player) {
		List<String> al = new ArrayList<>();
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
	public static void play(List<String> abspaths, NamedPathVar player) {
		String playerpath = (player == null) ? getVLCPath() : player.Path;
		
		if (Str.isNullOrWhitespace(playerpath)) {
			CCLog.addWarning(LocaleBundle.getString("LogMessage.VLCNotFound"));
			
			if (CCProperties.getInstance().PROP_PLAY_USESTANDARDONMISSINGVLC.getValue()) {
				try {
					for (String s : abspaths) {
						Desktop.getDesktop().open(new File(s));
					}
				} catch (IOException | IllegalArgumentException e) {
					CCLog.addError(e);
				}
			}
		} else {
			List<String> parameters = getParameters(playerpath, player);
			
			for (String abspath : abspaths) {
				if (ApplicationHelper.isWindows()) {
					parameters.add("\"" + abspath + "\"");
				} else {
					parameters.add(abspath);
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
