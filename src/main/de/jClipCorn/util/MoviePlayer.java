package de.jClipCorn.util;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.types.NamedPathVar;
import de.jClipCorn.util.datatypes.Tuple;
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
	
	private static Tuple<FSPath, String[]> lastVLCPath = Tuple.Create(FSPath.Empty, new String[0]);
	
	public static boolean play(CCMovie mov, NamedPathVar player) {
		List<FSPath> al = new ArrayList<>();
		for (var p: mov.getParts()) al.add(p.toFSPath(mov.ccprops()));
		return play(al, player, mov.getMovieList().ccprops());
	}
	
	public static boolean play(CCEpisode ep, NamedPathVar player) {
		return play(ep.Part.get().toFSPath(ep.ccprops()), player, ep.getMovieList().ccprops());
	}
	
	public static boolean play(FSPath abspath, NamedPathVar player, CCProperties ccprops) {
		List<FSPath> al = new ArrayList<>();
		al.add(abspath);
		return play(al, player, ccprops);
	}

	@SuppressWarnings("nls")
	public static List<String> getParameters(Tuple<FSPath, String[]> pathAndArgs, CCProperties ccprops, boolean isvlc) {
		List<String> parameters = new ArrayList<>();

		parameters.add(pathAndArgs.Item1.toString());
		parameters.addAll(List.of(pathAndArgs.Item2));

		if (isvlc)
		{
			parameters.add("--no-random");
			parameters.add("--no-loop");
			parameters.add("--no-repeat");
			parameters.add("--playlist-enqueue");

			if (ccprops.PROP_PLAY_VLC_FULLSCREEN.getValue()) parameters.add("--fullscreen");
			if (!ccprops.PROP_PLAY_VLC_AUTOPLAY.getValue()) parameters.add("--no-playlist-autostart");
			if (ccprops.PROP_PLAY_VLCSINGLEINSTANCEMODE.getValue()) parameters.add("--one-instance");

			if (ccprops.PROP_VLC_ROBOT_ENABLED.getValue())
			{
				parameters.add("--http-host=127.0.0.1");
				parameters.add("--http-port=" + ccprops.PROP_VLC_ROBOT_PORT.getValue());
				parameters.add("--http-password=" + ccprops.PROP_VLC_ROBOT_PASSWORD.getValue());
			}
		}

		return parameters;
	}

	@SuppressWarnings("nls")
	public static boolean play(List<FSPath> abspaths, NamedPathVar player, CCProperties ccprops)  {
		var playerpath = (player == null) ? getVLCPath(ccprops) : Tuple.Create(player.Path, FSPath.splitArguments(player.Arguments));
		
		if (FSPath.isNullOrEmpty(playerpath.Item1)) {
			CCLog.addWarning(LocaleBundle.getString("LogMessage.VLCNotFound"));
			
			if (ccprops.PROP_PLAY_USESTANDARDONMISSINGVLC.getValue()) {
				try {
					for (var s : abspaths) {
						Desktop.getDesktop().open(s.toFile());
					}
					return true;
				} catch (IOException | IllegalArgumentException e) {
					CCLog.addError(e);
					return false;
				}
			} else {
				return false;
			}
		} else {
			List<String> parameters = getParameters(playerpath, ccprops, player == null);
			
			for (var abspath : abspaths) {

				if (ccprops.PROP_PLAY_FAILONMISSINGFILES.getValue() && !abspath.fileExists()) {
					CCLog.addError(LocaleBundle.getString("LogMessage.VLCPlayFileNotFound"));
					return false;
				}

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
				return true;

			} catch (IOException e) {
				CCLog.addError(e);
				return false;
			}
		}
	}
	
	public static Tuple<FSPath, String[]> getVLCPath(CCProperties ccprops) {
		if (!FSPath.isNullOrEmpty(lastVLCPath.Item1)) {
			if (lastVLCPath.Item1.exists()) {
				return lastVLCPath;
			}
		}

		var vlcpath = ccprops.PROP_PLAY_VLC_PATH.getValue().getPathAndArgs();
		
		if (! vlcpath.Item1.isEmpty() && vlcpath.Item1.exists()) {
			return lastVLCPath = vlcpath;
		}
		
		if (ApplicationHelper.isWindows())
		{
			for (String ss : PATHS_WIN) {
				var path = FSPath.create(DRIVE_1 + ss);
				if (path.exists()) {
					return lastVLCPath = Tuple.Create(path, new String[0]);
				}
			}
			
			for (String ss : PATHS_WIN) {
				var path = FSPath.create(DRIVE_2 + ss);
				if (path.exists()) {
					return lastVLCPath = Tuple.Create(path, new String[0]);
				}
			}
			
			for (File f : File.listRoots()) {
				if (! (f.getAbsolutePath().equals(DRIVE_1) | f.getAbsolutePath().equals(DRIVE_2))) {
					for (String ss : PATHS_WIN) {
						var path = FSPath.create(f.getAbsolutePath() + ss);
						if (path.exists()) {
							return lastVLCPath = Tuple.Create(path, new String[0]);
						}
					}
				}
			}
		} else if (ApplicationHelper.isUnix()) {
			
			for (String npath : PATHS_NIX) {
				var path = FSPath.create(npath);
				if (path.exists()) {
					return lastVLCPath = Tuple.Create(path, new String[0]);
				}
			}
		}

		return lastVLCPath = Tuple.Create(FSPath.Empty, new String[0]);
	}
}
