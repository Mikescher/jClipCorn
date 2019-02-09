package de.jClipCorn.util;

import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.filechooser.FileSystemView;

import de.jClipCorn.Globals;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.RegExHelper;
import de.jClipCorn.util.stream.CCStreams;

public class DriveMap {
	private final static String REGEX_DRIVENAME_ESCAPE = " \\([A-Z]:\\)$";             //$NON-NLS-1$      // \([A-Z]:\)$
	private final static String REGEX_NETNAME_ESCAPE   = " \\(\\\\\\\\[\\\\\\w]+\\)$"; //$NON-NLS-1$      // \(\\\\[\\\w]+\)$
	private final static String REGEX_INVALID_DRIVE_CHARS = "[^A-Za-z0-9_ \\-]";       //$NON-NLS-1$      // [^A-Za-z0-9_ \-]
	
	private final static int MAX_DELAY_COUNT = 24; // = 24s
	
	private static Map<Character, Tuple<String, String>> driveMap = null;
	private static Map<String, Character> driveLabelToLetterMap = null;
	private static Map<String, Character> driveUNCToLetterMap = null;
	private static boolean created = false;
	private static boolean is_creating = false;
	private static int current_delay = 0;
	
	public static Character getDriveLetterByLabel(String name) {
		if (! isCreated()) {
			if (isCreating()) {
				waitForCreateFinished();
			} else {
				createMap();
			}
		}
		
		Character letter = driveLabelToLetterMap.get(name);
		
		return (letter == null) ? ('#') : (letter);
	}

	public static Character getDriveLetterByUNC(String netident) {
		if (! isCreated()) {
			if (isCreating()) {
				waitForCreateFinished();
			} else {
				createMap();
			}
		}

		Character letter = driveUNCToLetterMap.get(netident.toLowerCase());

		return (letter == null) ? ('#') : (letter);

	}

	public static String getDriveLabel(Character letter) {
		if (! isCreated()) {
			if (isCreating()) {
				waitForCreateFinished();
			} else {
				createMap();
			}
		}

		Tuple<String, String> info = driveMap.get(letter);

		return (info == null) ? Str.Empty : (info.Item1);
	}

	public static String getDriveUNC(Character letter) {
		if (! isCreated()) {
			if (isCreating()) {
				waitForCreateFinished();
			} else {
				createMap();
			}
		}

		Tuple<String, String> info = driveMap.get(letter);

		return (info == null || info.Item2 == null) ? Str.Empty : (info.Item2);
	}
	
	public static boolean hasDriveLabel(Character letter) {
		if (! isCreated()) {
			if (isCreating()) {
				waitForCreateFinished();
			} else {
				createMap();
			}
		}

		Tuple<String, String> name = driveMap.get(letter);
		
		return name != null && !Str.isNullOrWhitespace(name.Item1);
	}

	public static boolean hasDriveUNC(Character letter) {
		if (! isCreated()) {
			if (isCreating()) {
				waitForCreateFinished();
			} else {
				createMap();
			}
		}

		Tuple<String, String> name = driveMap.get(letter);

		return name != null && !Str.isNullOrWhitespace(name.Item2);
	}

	private static void createMap() {
		Globals.TIMINGS.start(Globals.TIMING_SCAN_DRIVES);
		
		is_creating = true;
		created = false;
		
		driveMap = new HashMap<>();
		driveLabelToLetterMap = new HashMap<>();
		driveUNCToLetterMap = new HashMap<>();

		List<FileStore> fstores;
		try {
			fstores = ApplicationHelper.isWindows() ? CCStreams.iterate(FileSystems.getDefault().getFileStores()).enumerate() : new ArrayList<>();
		} catch (Exception e) {
			fstores = new ArrayList<>();
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotEnumerateFileStores", e));
		}

		for (File root : File.listRoots()) {
			if(isFileSystem(root)) {
				String drive = getDriveName(root);
				String net = getDriveNetworkIdent(fstores, root, drive);
				Character letter = root.getAbsolutePath().charAt(0);

				if (!ApplicationHelper.isWindows()) net = Str.Empty;
				if (Str.isNullOrWhitespace(net)) net = Str.Empty;
				if (!net.startsWith("\\\\")) net = Str.Empty;

				driveMap.put(letter, Tuple.Create(drive, net));
				driveLabelToLetterMap.put(cleanDriveName(drive), letter);
				if (!Str.isNullOrWhitespace(net)) driveUNCToLetterMap.put(net.toLowerCase(), letter);
			}
		}
		
		created = true;
		is_creating = false;

		Globals.TIMINGS.stop(Globals.TIMING_SCAN_DRIVES);
	}

	private static String getDriveName(File f) {
		String drive =  FileSystemView.getFileSystemView().getSystemDisplayName(f);

		drive = RegExHelper.replace(REGEX_DRIVENAME_ESCAPE, drive, "");      //$NON-NLS-1$
		drive = RegExHelper.replace(REGEX_NETNAME_ESCAPE, drive, ""); //$NON-NLS-1$

		return drive;
	}

	private static String cleanDriveName(String drive) {
		drive = RegExHelper.replace(REGEX_INVALID_DRIVE_CHARS, drive, "_"); //$NON-NLS-1$
		return drive;
	}

	private static String getDriveNetworkIdent(List<FileStore> fsl, File f, String name) {
		try {
			for (FileStore fs : fsl) {
				if (fs.name().equals(name)) {
					//noinspection deprecation
					return (String) sun.awt.shell.ShellFolder.getShellFolder(f).getFolderColumnValue(6);
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	private static boolean isFileSystem(File f) {
		return FileSystemView.getFileSystemView().isFileSystemRoot(f);
	}
	
	public static void preScan() {
		new Thread(DriveMap::createMap, "THREAD_SCAN_FILESYSTEM").start(); //$NON-NLS-1$
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
	
	public static void tryWait() {
		if (! isCreated() && isCreating()) {
			waitForCreateFinished();
		}
	}
}
