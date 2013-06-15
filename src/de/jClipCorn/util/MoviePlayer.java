package de.jClipCorn.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;

public class MoviePlayer {
	@SuppressWarnings("nls")
	private final static String[] PATHS = {
		"Programme\\VideoLAN\\VLC\\vlc.exe", 
		"Program Files\\VideoLAN\\VLC\\vlc.exe", 
		"Programme (x86)\\VideoLAN\\VLC\\vlc.exe", 
		"Program Files (x86)\\VideoLAN\\VLC\\vlc.exe",
		"VideoLAN\\VLC\\vlc.exe",
		"VLC\\vlc.exe"
	};
	
	private final static String DRIVE_1 = "C:\\"; //$NON-NLS-1$
	private final static String DRIVE_2 = "H:\\"; //$NON-NLS-1$
	
	private static String lastVLCPath = null;
	
	public static void play(CCMovie mov) {
		List<String> al = new ArrayList<>();
		for (int i = 0; i < mov.getPartcount(); i++) {
			al.add(mov.getAbsolutePart(i));
		}
		play(al);
	}
	
	public static void play(CCEpisode ep) {
		play(ep.getAbsolutePart());
	}
	
	public static void play(String abspath) {
		List<String> al = new ArrayList<>();
		al.add(abspath);
		play(al);
	}
	
	@SuppressWarnings("nls")
	public static void play(List<String> abspaths) {
		List<String> parameters = new ArrayList<>();
		
		String vlc = getVLCPath();
		
		if (vlc == null || vlc.isEmpty()) {
			CCLog.addWarning(LocaleBundle.getString("LogMessage.VLCNotFound"));
			
			if (CCProperties.getInstance().PROP_PLAY_USESTANDARDONMISSINGVLC.getValue()) {
				try {
					for (String s : abspaths) {
						Desktop.getDesktop().open(new File(s));
					}
				} catch (IOException e) {
					CCLog.addError(e);
				}
			}
		} else {
			parameters.add(vlc);
			parameters.add("--playlist-enqueue");
			if (CCProperties.getInstance().PROP_PLAY_VLC_FULLSCREEN.getValue()) {
				parameters.add("--fullscreen");
			}
			if (! CCProperties.getInstance().PROP_PLAY_VLC_AUTOPLAY.getValue()) {
				parameters.add("--no-playlist-autostart");
			}
			
			for (String s : abspaths) {
				parameters.add("\"" + s.replace("/", "\\") + "\"");
			}
			
			try {
				new ProcessBuilder(parameters).start();
			} catch (IOException e) {
				CCLog.addError(e);
			}
		}
	}
	
	private static String getVLCPath() {
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
		
		for (String ss : PATHS) {
			String path = DRIVE_1 + ss;
			if (new File(path).exists()) {
				return lastVLCPath = path;
			}
		}
		
		for (String ss : PATHS) {
			String path = DRIVE_2 + ss;
			if (new File(path).exists()) {
				return lastVLCPath = path;
			}
		}
		
		for (File f : File.listRoots()) {
			if (! (f.getAbsolutePath().equals(DRIVE_1) | f.getAbsolutePath().equals(DRIVE_2))) {
				for (String ss : PATHS) {
					String path = f.getAbsolutePath() + ss;
					if (new File(path).exists()) {
						return lastVLCPath = path;
					}
				}
			}
		}
		lastVLCPath = null;
		return null;
	}
}
