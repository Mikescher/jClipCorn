package de.jClipCorn.util;

import de.jClipCorn.Globals;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.mainFrame.statusbar.ClipStatusBar;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.datatypes.Tuple4;
import de.jClipCorn.util.helper.*;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriveMap {
	private final static String REGEX_DRIVENAME_ESCAPE = " \\([A-Z]:\\)$";             //$NON-NLS-1$      // \([A-Z]:\)$
	private final static String REGEX_NETNAME_ESCAPE   = " \\(\\\\\\\\[\\\\\\w]+\\)$"; //$NON-NLS-1$      // \(\\\\[\\\w]+\)$
	private final static String REGEX_INVALID_DRIVE_CHARS = "[^A-Za-z0-9_ \\-]";       //$NON-NLS-1$      // [^A-Za-z0-9_ \-]
	
	private final static int MAX_DELAY_COUNT = 24; // = 24s
	
	private Map<Character, Tuple<String, String>> driveMap = null;
	private Map<String, Character> driveLabelToLetterMap = null;
	private Map<String, Character> driveUNCToLetterMap = null;

	private volatile boolean is_created    = false;
	private volatile boolean is_creating   = false;
	private volatile boolean is_rescanning = false;

	private int current_delay = 0;

	private long lastScanStart = 0;

	private final Object _rescanLock = new Object();
	private final Object _scanLock = new Object();

	private final CCProperties owner;

	public DriveMap(CCProperties ccp) {
		owner = ccp;
	}

	public CCProperties ccprops() {
		return owner;
	}

	public Character getDriveLetterByLabel(String name) {
		waitOrCreate();

		synchronized (_scanLock) {
			Character letter = driveLabelToLetterMap.get(name);
			return (letter == null) ? ('#') : (letter);
		}
	}

	public Character getDriveLetterByUNC(String netident) {
		waitOrCreate();

		synchronized (_scanLock) {
			Character letter = driveUNCToLetterMap.get(netident.toLowerCase());
			return (letter == null) ? ('#') : (letter);
		}

	}

	public String getDriveLabel(Character letter) {
		waitOrCreate();

		synchronized (_scanLock) {
			Tuple<String, String> info = driveMap.get(letter);
			return (info == null) ? Str.Empty : (info.Item1);
		}
	}

	public String getDriveUNC(Character letter) {
		waitOrCreate();

		synchronized (_scanLock) {
			Tuple<String, String> info = driveMap.get(letter);
			return (info == null || info.Item2 == null) ? Str.Empty : (info.Item2);
		}
	}
	
	public boolean hasDriveLabel(Character letter) {
		waitOrCreate();

		synchronized (_scanLock) {
			Tuple<String, String> name = driveMap.get(letter);
			return name != null && !Str.isNullOrWhitespace(name.Item1);
		}
	}

	public boolean hasDriveUNC(Character letter) {
		waitOrCreate();

		synchronized (_scanLock) {
			Tuple<String, String> name = driveMap.get(letter);
			return name != null && !Str.isNullOrWhitespace(name.Item2);
		}
	}

	private void waitOrCreate() {
		if (!is_created) {
			if (is_creating) {
				waitForCreateFinished();
			} else {
				createMap();
			}
		}
	}

	private void createMap() {
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

		var netuse = ApplicationHelper.getNetUseSafe();

		for (File root : File.listRoots()) {
			if(isFileSystem(root)) {
				String drive = getDriveName(root);
				String net = getDriveNetworkIdents(fstores, netuse, root, drive);
				Character letter = root.getAbsolutePath().charAt(0);

				if (!ApplicationHelper.isWindows()) net = Str.Empty;
				if (Str.isNullOrWhitespace(net)) net = Str.Empty;
				if (!net.startsWith("\\\\")) net = Str.Empty; //$NON-NLS-1$

				threadDriveMap.put(letter, Tuple.Create(drive, net));
				threadDriveLabelToLetterMap.put(cleanDriveName(drive), letter);
				if (!Str.isNullOrWhitespace(net)) threadDriveUNCToLetterMap.put(net.toLowerCase(), letter);
			}
		}

		if (ApplicationHelper.isWindows() && ccprops().PROP_DRIVEMAP_REMOUNT_NETDRIVES.getValue())
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

	private String getDriveNetworkIdents(List<FileStore> fstores, List<Tuple4<String, String, String, String>> netuse, File f, String name)
	{
		try {
			for (var fs : fstores) {
				if (fs.name().equals(name))
				{
					for (var nu : netuse)
					{
						if (Str.equals(nu.Item2, f.getAbsolutePath().substring(0, 2))) return nu.Item3;
					}
				}
			}
			return null;
		} catch (Exception e) {
			CCLog.addError(e);
			return null;
		}
	}

	private void updateMap() {
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
					String net = ApplicationHelper.isWindows() ? WindowsJNAHelper.getDriveNetworkIdent(fstores, root, drive) : Str.Empty;
					Character letter = root.getAbsolutePath().charAt(0);

					if (Str.isNullOrWhitespace(net)) net = Str.Empty;
					if (!net.startsWith("\\\\")) net = Str.Empty; //$NON-NLS-1$

					threadDriveMap.put(letter, Tuple.Create(drive, net));
					threadDriveLabelToLetterMap.put(cleanDriveName(drive), letter);
					if (!Str.isNullOrWhitespace(net)) threadDriveUNCToLetterMap.put(net.toLowerCase(), letter);
				}
			}

			if (ApplicationHelper.isWindows() && ccprops().PROP_DRIVEMAP_REMOUNT_NETDRIVES.getValue())
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
	private void autoMountWinNetDrives(Map<Character, Tuple<String, String>> driveMap, Map<String, Character> driveLabelToLetterMap, Map<String, Character> driveUNCToLetterMap)
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

	private String getDriveName(File f) {
		String drive =  FileSystemView.getFileSystemView().getSystemDisplayName(f);

		drive = RegExHelper.replace(REGEX_DRIVENAME_ESCAPE, drive, "");      //$NON-NLS-1$
		drive = RegExHelper.replace(REGEX_NETNAME_ESCAPE, drive, ""); //$NON-NLS-1$

		return drive;
	}

	private String cleanDriveName(String drive) {
		drive = RegExHelper.replace(REGEX_INVALID_DRIVE_CHARS, drive, "_"); //$NON-NLS-1$
		return drive;
	}

	private boolean isFileSystem(File f) {
		return FileSystemView.getFileSystemView().isFileSystemRoot(f);
	}
	
	public void preScan() {
		new Thread(this::createMap, "THREAD_SCAN_FILESYSTEM").start(); //$NON-NLS-1$
	}

	private void waitForCreateFinished() {
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
	
	public void tryWait() {
		if (! is_created && is_creating) {
			waitForCreateFinished();
		}
	}

	public void conditionalRescan() {
		if (! is_created) return;
		if (is_creating) return;
		if (is_rescanning) return;

		if (System.currentTimeMillis() - lastScanStart <= ccprops().PROP_MIN_DRIVEMAP_RESCAN_TIME.getValue()) return;
		synchronized (_rescanLock) {
			if (is_rescanning) return;
			if (System.currentTimeMillis() - lastScanStart <= ccprops().PROP_MIN_DRIVEMAP_RESCAN_TIME.getValue()) return;

			lastScanStart = System.currentTimeMillis();
			is_rescanning = true;

			triggerOnChanged();

			new Thread(this::updateMap, "THREAD_RESCAN_FILESYSTEM").start(); //$NON-NLS-1$
		}

	}

	public String getStatus()
	{
		if (! is_created)  return LocaleBundle.getString("DriveMap.Status.NotCreated"); //$NON-NLS-1$
		if (is_creating)   return LocaleBundle.getString("DriveMap.Status.Creating"); //$NON-NLS-1$
		if (is_rescanning) return LocaleBundle.getString("DriveMap.Status.Rescanning"); //$NON-NLS-1$

		return LocaleBundle.getString("DriveMap.Status.Idle"); //$NON-NLS-1$
	}

	public List<Tuple3<Character, String, String>> getCopy() {
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

	private void triggerOnChanged() {
		SwingUtils.invokeLater(() ->
		{
			MainFrame inst = MainFrame.getInstance();
			if (inst != null) {
				ClipStatusBar sb = inst.getStatusBar();
				if (sb != null) sb.updateLables_DriveScan();
			}
		});
	}

	public void initForTests(Tuple3<Character, String, String>... entries) {
		driveMap              = new HashMap<>();
		driveLabelToLetterMap = new HashMap<>();
		driveUNCToLetterMap   = new HashMap<>();

		for (var e : entries)
		{
			driveMap.put(e.Item1, Tuple.Create(e.Item2, e.Item3));
			driveLabelToLetterMap.put(e.Item2, e.Item1);
			driveUNCToLetterMap.put(e.Item3, e.Item1);
		}

		is_created = true;
		is_creating = false;

		triggerOnChanged();
	}
}
