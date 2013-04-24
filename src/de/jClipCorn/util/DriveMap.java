package de.jClipCorn.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.filechooser.FileSystemView;

import de.jClipCorn.Main;


public class DriveMap {
	private final static String REGEX_DRIVENAME_ESCAPE = " \\([A-Z]:\\)"; //$NON-NLS-1$
	
	private static Map<Character, String> driveNameMap = null;
	private static Map<String, Character> driveLetterMap = null;
	private static boolean created = false;
	
	public static Character getDriveLetter(String name) {
		if (driveLetterMap == null) {
			createMap();
		}
		
		Character letter = driveLetterMap.get(name);
		
		return (letter == null) ? ('#') : (letter);
	}
	
	@SuppressWarnings("nls")
	public static String getDriveName(Character letter) {
		if (driveNameMap == null) {
			createMap();
		}
		
		String name = driveNameMap.get(letter);
		
		return (name == null) ? ("") : (name);
	}

	private static void createMap() {
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

	public static  boolean isCreated() {
		return created;
	}
}
