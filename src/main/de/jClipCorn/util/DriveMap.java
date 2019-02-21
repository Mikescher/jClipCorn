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
import de.jClipCorn.properties.CCProperties;
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

	private static volatile boolean is_created    = false;
	private static volatile boolean is_creating   = false;
	private static volatile boolean is_rescanning = false;

	private static int current_delay = 0;

	private static long lastScanStart = 0;

	private static final Object _rescanLock = new Object();
	private static final Object _scanLock = new Object();

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
		Globals.TIMINGS.start(Globals.TIMING_BACKGROUND_SCAN_DRIVES);
		
		is_creating = true;
		is_created = false;

		Map<Character, Tuple<String, String>> threadDriveMap = new HashMap<>();
		Map<String, Character> threadDriveLabelToLetterMap   = new HashMap<>();
		Map<String, Character> threadDriveUNCToLetterMap     = new HashMap<>();

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

				threadDriveMap.put(letter, Tuple.Create(drive, net));
				threadDriveLabelToLetterMap.put(cleanDriveName(drive), letter);
				if (!Str.isNullOrWhitespace(net)) threadDriveUNCToLetterMap.put(net.toLowerCase(), letter);
			}
		}

		synchronized (_scanLock) {
			driveMap              = threadDriveMap;
			driveLabelToLetterMap = threadDriveLabelToLetterMap;
			driveUNCToLetterMap   = threadDriveUNCToLetterMap;
		}
		
		is_created = true;
		is_creating = false;

		Globals.TIMINGS.stop(Globals.TIMING_BACKGROUND_SCAN_DRIVES);
	}

	private static void updateMap() {
		try {
			long startTime = lastScanStart = System.currentTimeMillis();

			Map<Character, Tuple<String, String>> threadDriveMap = new HashMap<>();
			Map<String, Character> threadDriveLabelToLetterMap   = new HashMap<>();
			Map<String, Character> threadDriveUNCToLetterMap     = new HashMap<>();

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

					threadDriveMap.put(letter, Tuple.Create(drive, net));
					threadDriveLabelToLetterMap.put(cleanDriveName(drive), letter);
					if (!Str.isNullOrWhitespace(net)) threadDriveUNCToLetterMap.put(net.toLowerCase(), letter);
				}
			}

			synchronized (_scanLock) {
				driveMap.putAll(threadDriveMap);
				driveLabelToLetterMap.putAll(threadDriveLabelToLetterMap);
				driveUNCToLetterMap.putAll(threadDriveUNCToLetterMap);
			}

			long delta = System.currentTimeMillis() - startTime;
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.DriveMapRescan", delta));
		} finally {
			is_rescanning = false;
		}
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
		return is_created;
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

	public static void conditionalRescan() {
		if (! isCreated()) return;
		if (isCreating()) return;
		if (is_rescanning) return;

		if (System.currentTimeMillis() - lastScanStart <= CCProperties.getInstance().PROP_MIN_DRIVEMAP_RESCAN_TIME.getValue()) return;
		synchronized (_rescanLock) {
			if (is_rescanning) return;
			if (System.currentTimeMillis() - lastScanStart <= CCProperties.getInstance().PROP_MIN_DRIVEMAP_RESCAN_TIME.getValue()) return;

			lastScanStart = System.currentTimeMillis();
			is_rescanning = true;
			new Thread(DriveMap::updateMap, "THREAD_RESCAN_FILESYSTEM").start();
		}

	}
}
