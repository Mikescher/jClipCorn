package de.jClipCorn.util;

import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import de.jClipCorn.Globals;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.mainFrame.clipStatusbar.ClipStatusBar;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.datatypes.Tuple4;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.ProcessHelper;
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
		waitOrCreate();

		synchronized (_scanLock) {
			Character letter = driveLabelToLetterMap.get(name);
			return (letter == null) ? ('#') : (letter);
		}
	}

	public static Character getDriveLetterByUNC(String netident) {
		waitOrCreate();

		synchronized (_scanLock) {
			Character letter = driveUNCToLetterMap.get(netident.toLowerCase());
			return (letter == null) ? ('#') : (letter);
		}

	}

	public static String getDriveLabel(Character letter) {
		waitOrCreate();

		synchronized (_scanLock) {
			Tuple<String, String> info = driveMap.get(letter);
			return (info == null) ? Str.Empty : (info.Item1);
		}
	}

	public static String getDriveUNC(Character letter) {
		waitOrCreate();

		synchronized (_scanLock) {
			Tuple<String, String> info = driveMap.get(letter);
			return (info == null || info.Item2 == null) ? Str.Empty : (info.Item2);
		}
	}
	
	public static boolean hasDriveLabel(Character letter) {
		waitOrCreate();

		synchronized (_scanLock) {
			Tuple<String, String> name = driveMap.get(letter);
			return name != null && !Str.isNullOrWhitespace(name.Item1);
		}
	}

	public static boolean hasDriveUNC(Character letter) {
		waitOrCreate();

		synchronized (_scanLock) {
			Tuple<String, String> name = driveMap.get(letter);
			return name != null && !Str.isNullOrWhitespace(name.Item2);
		}
	}

	private static void waitOrCreate() {
		if (!is_created) {
			if (is_creating) {
				waitForCreateFinished();
			} else {
				createMap();
			}
		}
	}

	private static void createMap() {
		Globals.TIMINGS.start(Globals.TIMING_BACKGROUND_SCAN_DRIVES);
		
		is_creating = true;
		is_created = false;

		triggerOnChanged();

		Map<Character, Tuple<String, String>> threadDriveMap = new HashMap<>();
		Map<String, Character> threadDriveLabelToLetterMap   = new HashMap<>();
		Map<String, Character> threadDriveUNCToLetterMap     = new HashMap<>();

		List<FileStore> fstores;
		try {
			fstores = ApplicationHelper.isWindows() ? CCStreams.iterate(FileSystems.getDefault().getFileStores()).enumerate() : new ArrayList<>();
		} catch (Exception e) {
			fstores = new ArrayList<>();
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotEnumerateFileStores", e)); //$NON-NLS-1$
		}

		for (File root : File.listRoots()) {
			if(isFileSystem(root)) {
				String drive = getDriveName(root);
				String net = getDriveNetworkIdent(fstores, root, drive);
				Character letter = root.getAbsolutePath().charAt(0);

				if (!ApplicationHelper.isWindows()) net = Str.Empty;
				if (Str.isNullOrWhitespace(net)) net = Str.Empty;
				if (!net.startsWith("\\\\")) net = Str.Empty; //$NON-NLS-1$

				threadDriveMap.put(letter, Tuple.Create(drive, net));
				threadDriveLabelToLetterMap.put(cleanDriveName(drive), letter);
				if (!Str.isNullOrWhitespace(net)) threadDriveUNCToLetterMap.put(net.toLowerCase(), letter);
			}
		}

		if (ApplicationHelper.isWindows() && CCProperties.getInstance().PROP_DRIVEMAP_REMOUNT_NETDRIVES.getValue())
		{
			autoMountWinNetDrives(threadDriveMap, threadDriveLabelToLetterMap, threadDriveUNCToLetterMap);
		}

		synchronized (_scanLock) {
			driveMap              = threadDriveMap;
			driveLabelToLetterMap = threadDriveLabelToLetterMap;
			driveUNCToLetterMap   = threadDriveUNCToLetterMap;
		}
		
		is_created = true;
		is_creating = false;

		triggerOnChanged();

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
				CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotEnumerateFileStores", e)); //$NON-NLS-1$
			}

			for (File root : File.listRoots()) {
				if(isFileSystem(root)) {
					String drive = getDriveName(root);
					String net = getDriveNetworkIdent(fstores, root, drive);
					Character letter = root.getAbsolutePath().charAt(0);

					if (!ApplicationHelper.isWindows()) net = Str.Empty;
					if (Str.isNullOrWhitespace(net)) net = Str.Empty;
					if (!net.startsWith("\\\\")) net = Str.Empty; //$NON-NLS-1$

					threadDriveMap.put(letter, Tuple.Create(drive, net));
					threadDriveLabelToLetterMap.put(cleanDriveName(drive), letter);
					if (!Str.isNullOrWhitespace(net)) threadDriveUNCToLetterMap.put(net.toLowerCase(), letter);
				}
			}

			if (ApplicationHelper.isWindows() && CCProperties.getInstance().PROP_DRIVEMAP_REMOUNT_NETDRIVES.getValue())
			{
				autoMountWinNetDrives(threadDriveMap, threadDriveLabelToLetterMap, threadDriveUNCToLetterMap);
			}

			synchronized (_scanLock) {
				driveMap.putAll(threadDriveMap);
				driveLabelToLetterMap.putAll(threadDriveLabelToLetterMap);
				driveUNCToLetterMap.putAll(threadDriveUNCToLetterMap);
			}

			triggerOnChanged();

			long delta = System.currentTimeMillis() - startTime;
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.DriveMapRescan", delta)); //$NON-NLS-1$
		} finally {
			is_rescanning = false;

			triggerOnChanged();
		}
	}

	@SuppressWarnings("nls")
	private static void autoMountWinNetDrives(Map<Character, Tuple<String, String>> driveMap, Map<String, Character> driveLabelToLetterMap, Map<String, Character> driveUNCToLetterMap)
	{
		try
		{
			for (Tuple4<String, String, String, String> d : ApplicationHelper.getNetUse())
			{
				char letter = d.Item2.charAt(0);
				String net = d.Item3;

				if (driveMap.containsKey(letter)) continue;
				if (!new File(net).exists()) continue;

				Tuple3<Integer, String, String> ex = ProcessHelper.procExec("net", "use", d.Item2, net);
				if (ex.Item1 != 0)
				{
					CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.RemountFailed", d.Item2, net), new Exception(ex.Item3));
					continue;
				}

				File root = new File(d.Item2);
				String drive = getDriveName(root);

				driveMap.put(letter, Tuple.Create(drive, net));
				driveLabelToLetterMap.put(cleanDriveName(drive), letter);
				driveUNCToLetterMap.put(net.toLowerCase(), letter);

				CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.MountSuccess", d.Item2, net, drive));
			}
		}
		catch (Exception e)
		{
			CCLog.addError(e);
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

	@SuppressWarnings({ "deprecation", "restriction" })
	private static String getDriveNetworkIdent(List<FileStore> fsl, File f, String name) {
		try {
			for (FileStore fs : fsl) {
				if (fs.name().equals(name)) {
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

	private static void waitForCreateFinished() {
		while(is_creating) {
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
		if (! is_created && is_creating) {
			waitForCreateFinished();
		}
	}

	public static void conditionalRescan() {
		if (! is_created) return;
		if (is_creating) return;
		if (is_rescanning) return;

		if (System.currentTimeMillis() - lastScanStart <= CCProperties.getInstance().PROP_MIN_DRIVEMAP_RESCAN_TIME.getValue()) return;
		synchronized (_rescanLock) {
			if (is_rescanning) return;
			if (System.currentTimeMillis() - lastScanStart <= CCProperties.getInstance().PROP_MIN_DRIVEMAP_RESCAN_TIME.getValue()) return;

			lastScanStart = System.currentTimeMillis();
			is_rescanning = true;

			triggerOnChanged();

			new Thread(DriveMap::updateMap, "THREAD_RESCAN_FILESYSTEM").start(); //$NON-NLS-1$
		}

	}

	public static String getStatus()
	{
		if (! is_created)  return LocaleBundle.getString("DriveMap.Status.NotCreated"); //$NON-NLS-1$
		if (is_creating)   return LocaleBundle.getString("DriveMap.Status.Creating"); //$NON-NLS-1$
		if (is_rescanning) return LocaleBundle.getString("DriveMap.Status.Rescanning"); //$NON-NLS-1$

		return LocaleBundle.getString("DriveMap.Status.Idle"); //$NON-NLS-1$
	}

	public static List<Tuple3<Character, String, String>> getCopy() {
		if (! is_created)  return new ArrayList<>();
		if (is_creating)   return new ArrayList<>();
		if (is_rescanning) return new ArrayList<>();


		synchronized (_scanLock) {
			return CCStreams
					.iterate(driveMap)
					.map(p -> Tuple3.Create(p.getKey(), p.getValue().Item1, p.getValue().Item2))
					.autosortByProperty(p -> p.Item1)
					.enumerate();
		}
	}

	private static void triggerOnChanged() {
		SwingUtilities.invokeLater(() ->
		{
			MainFrame inst = MainFrame.getInstance();
			if (inst != null) {
				ClipStatusBar sb = inst.getStatusBar();
				if (sb != null) sb.updateLables_DriveScan();
			}
		});
	}
}
