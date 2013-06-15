package de.jClipCorn.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.filechooser.FileSystemView;

import de.jClipCorn.Main;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;


public class DriveMap {
	private final static String REGEX_DRIVENAME_ESCAPE = " \\([A-Z]:\\)"; //$NON-NLS-1$
	
	private final static int MAX_DELAY_COUNT = 24; // = 24s
	
	private static Map<Character, String> driveNameMap = null;
	private static Map<String, Character> driveLetterMap = null;
	private static boolean created = false;
	private static boolean is_creating = false;
	private static int current_delay = 0;
	
	public static Character getDriveLetter(String name) {
		if (! isCreated()) {
			if (isCreating()) {
				waitForCreateFinished();
			} else {
				createMap();
			}
		}
		
		Character letter = driveLetterMap.get(name);
		
		return (letter == null) ? ('#') : (letter);
	}
	
	@SuppressWarnings("nls")
	public static String getDriveName(Character letter) {
		if (! isCreated()) {
			if (isCreating()) {
				waitForCreateFinished();
			} else {
				createMap();
			}
		}
		
		String name = driveNameMap.get(letter);
		
		return (name == null) ? ("") : (name);
	}
	
	public static boolean hasDriveName(Character letter) {
		if (! isCreated()) {
			if (isCreating()) {
				waitForCreateFinished();
			} else {
				createMap();
			}
		}
		
		String name = driveNameMap.get(letter);
		
		return name != null;
	}

	private static void createMap() {
		is_creating = true;
		created = false;
		
		long sss = System.currentTimeMillis();
		driveNameMap = new HashMap<>();
		driveLetterMap = new HashMap<>();
		
		for (File root : File.listRoots()) {
			if(isFileSystem(root)) {
				String drive = getDriveName(root);
				Character letter = root.getAbsolutePath().charAt(0);
				driveNameMap.put(letter, drive);
				driveLetterMap.put(drive, letter);
			}
		}
		
		created = true;
		is_creating = false;
		
		if (Main.DEBUG) {
			System.out.println("[DBG] FileSystem scanned in " + ((System.currentTimeMillis() - sss) / 100) / 10.0 + "s"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	private static String getDriveName(File f) {
		String drive =  FileSystemView.getFileSystemView().getSystemDisplayName(f);
		return RegExHelper.replace(REGEX_DRIVENAME_ESCAPE, drive, ""); //$NON-NLS-1$
	}
	
	private static boolean isFileSystem(File f) {
		return FileSystemView.getFileSystemView().isFileSystemRoot(f);
	}
	
	public static void preScan() {
		(new Thread(new Runnable() {
			@Override
			public void run() {
				createMap();
			}
		}, "THREAD_SCAN_FILESYSTEM")).start(); //$NON-NLS-1$
	}

	public static boolean isCreated() {
		return created;
	}
	
	public static boolean isCreating() {
		return is_creating;
	}
	
	private static void waitForCreateFinished() {
		while(isCreating()) {
			if (current_delay > MAX_DELAY_COUNT) {
				CCLog.addError(LocaleBundle.getString("LogMessage.DriveMapTookTooLong")); //$NON-NLS-1$
				return;
			}
			
			CCLog.addInformation(LocaleBundle.getString("LogMessage.DriveMapDelayed")); //$NON-NLS-1$

			try {
				Thread.sleep(1000);
				current_delay++;
			} catch (InterruptedException e) {
				CCLog.addError(e);
			}
		}
	}
}
