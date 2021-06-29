package de.jClipCorn.features.backupManager;

import de.jClipCorn.Globals;
import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.helper.ThreadUtils;
import de.jClipCorn.util.lambda.Func0to0;
import de.jClipCorn.util.lambda.Func0to0WithIOException;
import de.jClipCorn.util.lambda.Func1to0;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.ProgressCallbackProgressMonitorHelper;
import de.jClipCorn.util.listener.ProgressCallbackSink;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipOutputStream;

public class BackupManager {
	private final static String BACKUPFILENAME = "jCC-Backup %s_%d." + ExportHelper.EXTENSION_BACKUP; //$NON-NLS-1$
	private final static String BACKUPNAME = "Backup of %s"; //$NON-NLS-1$

	private static BackupManager instance = null;

	private final CCMovieList movielist;

	private final AtomicBoolean isInitialised = new AtomicBoolean(false);

	private List<CCBackup> backuplist = new ArrayList<>();

	public BackupManager(CCMovieList ml) {
		this.movielist = ml;
		instance = this;
	}

	public void init() {
		Globals.TIMINGS.start(Globals.TIMING_LOAD_INIT_BACKUPMANAGER);
		{
			if (CCProperties.getInstance().PROP_LOADING_INITBACKUPMANAGERASYNC.getValue()) {
				new Thread(() ->
				{
					ThreadUtils.safeSleep(Globals.ASYNC_TIME_OFFSET_BACKUPMANAGER);
					Globals.TIMINGS.start(Globals.TIMING_BACKGROUND_INITBACKUPMANAGER);
					initBackupsList();
					Globals.TIMINGS.stop(Globals.TIMING_BACKGROUND_INITBACKUPMANAGER);

				}, "INIT_BACKUP_MANAGER").start(); //$NON-NLS-1$
			} else {
				initBackupsList();
			}
		}
		Globals.TIMINGS.stop(Globals.TIMING_LOAD_INIT_BACKUPMANAGER);
	}

	private FSPath getBackupDirectory() {
		var d = FilesystemUtils.getRealSelfDirectory().append(CCProperties.getInstance().PROP_BACKUP_FOLDERNAME.getValue());

		if (!d.exists()) d.mkdirsSafe();

		return d;
	}

	private FSPath[] getArchiveFiles() {
		return getBackupDirectory().list(f -> f.getExtension().equalsIgnoreCase(ExportHelper.EXTENSION_BACKUP)).toArray(new FSPath[0]);
	}

	private void initBackupsList() {
		try {
			FSPath[] archives = getArchiveFiles();

			for (FSPath f : archives) {
				try {
					CCBackup backup = new CCBackup(f);

					backuplist.add(backup);
				} catch (Exception e) {
					CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorInitBackupList", f.getFilenameWithExt()), e); //$NON-NLS-1$
				}
			}
		} finally {
			isInitialised.set(true);
		}
	}

	public void doActions(Component c) {
		if (movielist.isReadonly()) return;

		if (CCProperties.getInstance().PROP_BACKUP_CREATEBACKUPS.getValue() && needsCreateBackup()) {
			Globals.TIMINGS.start(Globals.TIMING_LOAD_CREATEBACKUP);
			waitForInitialized(() -> tryCreateBackup(c));
			Globals.TIMINGS.stop(Globals.TIMING_LOAD_CREATEBACKUP);
		}

		if (CCProperties.getInstance().PROP_BACKUP_AUTODELETEBACKUPS.getValue()) {
			runWhenInitialized(this::tryDeleteOldBackups);
		}
	}

	private void tryDeleteOldBackups() {
		for (int i = backuplist.size() - 1; i >= 0; i--) {
			CCBackup backup = backuplist.get(i);

			if (backup.isExpired()) deleteBackup(backup);
		}
	}

	public void deleteBackupWithWait(CCBackup bkp) {
		waitForInitialized(() -> deleteBackup(bkp));
	}

	private void deleteBackup(CCBackup bkp) {
		String name = bkp.getName();
		if (bkp.delete()) {
			backuplist.remove(bkp);
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.BackupDeleted", name)); //$NON-NLS-1$
		} else {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.BackupNotDeleted", name)); //$NON-NLS-1$
		}
	}

	private boolean needsCreateBackup() {
		int minDiff = CCProperties.getInstance().PROP_BACKUP_BACKUPTIME.getValue();
		CCDate lastBackup = CCProperties.getInstance().PROP_BACKUP_LASTBACKUP.getValue();
		CCDate now = CCDate.getCurrentDate();

		return lastBackup.getDayDifferenceTo(now) > minDiff && movielist.getDatabaseDirectory().exists();
	}

	private void tryCreateBackup(Component c) {
		if (needsCreateBackup()) createBackup(c);
	}

	private FSPath getNewBackupName() {
		var dir = getBackupDirectory();

		int id = 0;
		String now = CCDate.getCurrentDate().toStringSQL();

		for (;;)
		{
			var f = dir.append(String.format(BACKUPFILENAME, now, id));
			if (!f.exists()) return f;
			id++;
		}
	}

	public String getStandardBackupname() {
		return String.format(BACKUPNAME, CCProperties.getInstance().PROP_DATABASE_NAME.getValue());
	}

	public void createBackupWithWait(Component c) {
		waitForInitialized(() -> createBackup(c));
	}

	public void createBackupWithWait(Component c, String name, boolean persistent) {
		waitForInitialized(() -> createBackup(c, name, persistent));
	}

	private void createBackup(Component c) {
		createBackup(c, getStandardBackupname(), false);
	}

	private void createBackup(Component c, String name, boolean persistent) {
		createBackup(c, name, CCDate.getCurrentDate(), persistent, Main.VERSION, Main.DBVERSION);
	}

	public void createMigrationBackupWithWait(String oldVersion) throws IOException {
		waitForInitialized_IO(() ->
		{
			String name = "Automatic backup (database migration from " + oldVersion + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			createBackupInternal(name, CCDate.getCurrentDate(), false, Main.VERSION, Main.DBVERSION, new ProgressCallbackSink(), true);
		});
	}

	private void createBackup(Component c, String name, CCDate date, boolean persistent, String jccversion, String dbversion) {
		if (movielist.isReadonly()) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			return;
		}

		CCLog.addInformation(LocaleBundle.getString("LogMessage.BackupStarted")); //$NON-NLS-1$

		ProgressMonitor monitor = DialogHelper.getLocalPersistentProgressMonitor(c, "MainFrame.backupRunning"); //$NON-NLS-1$

		try {
			createBackupInternal(name, date, persistent, jccversion, dbversion, new ProgressCallbackProgressMonitorHelper(monitor), false);
		} catch (Exception | Error e) {
			CCLog.addError(e);
			monitor.setProgress(monitor.getMaximum());
			monitor.close();
			return;
		}

		monitor.setProgress(monitor.getMaximum());
		monitor.close();

		CCLog.addInformation(LocaleBundle.getString("LogMessage.BackupCreated")); //$NON-NLS-1$
		CCProperties.getInstance().PROP_BACKUP_LASTBACKUP.setValue(CCDate.getCurrentDate());
	}

	private void createBackupInternal(String name, CCDate date, boolean persistent, String jccversion, String dbversion, ProgressCallbackListener mon, boolean forceExcludeCovers) throws IOException {
		var file = getNewBackupName();

		FileOutputStream os = new FileOutputStream(file.toFile());
		ZipOutputStream zos = new ZipOutputStream(os);
		zos.setLevel(CCProperties.getInstance().PROP_BACKUP_COMPRESSION.getValue());

		final List<String> excludedFolders = new ArrayList<>();
		final List<String> excludedFiles   = new ArrayList<>();

		boolean excludeCovers = forceExcludeCovers || (CCProperties.getInstance().PROP_BACKUP_EXCLUDECOVERS.getValue() && !persistent);

		if (excludeCovers) {
			movielist.getCoverCache().getBackupExclusions(excludedFolders, excludedFiles);
		}

		ExportHelper.zipDir(
			movielist.getDatabaseDirectory().getParent(),
			movielist.getDatabaseDirectory(), 
			zos, 
			true, 
			(f) -> testExclusion(excludedFolders, excludedFiles, f),
			mon);

		zos.close();

		CCBackup backup = new CCBackup(file, name, date, persistent, jccversion, dbversion, excludeCovers);
		boolean ok = backup.saveToFile();
		if (!ok) {
			if (file.exists()) FileUtils.deleteQuietly(file.toFile());
			throw new IOException("saveToFile failed"); //$NON-NLS-1$
		}
		backuplist.add(backup);
	}

	private boolean testExclusion(List<String> excludedFolders, List<String> excludedFiles, FSPath f) {
		if (f.isDirectory())
		{
			for (String ex : excludedFolders) if (f.getDirectoryName().equalsIgnoreCase(ex)) return true;
		}
		else
		{
			for (String ex : excludedFiles) if (f.getFilenameWithExt().equalsIgnoreCase(ex)) return true;
		}
		return false;
	}

	public boolean restoreBackupWithWait(Component c, CCBackup bkp) {
		if (movielist.isReadonly()) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			return false;
		}

		waitForInitialized(Func0to0.NOOP);

		CCLog.addInformation(LocaleBundle.getString("LogMessage.RestoreStarted")); //$NON-NLS-1$

		var archive    = bkp.getArchive();
		var directory  = movielist.getDatabaseDirectory();
		var directoryP = directory.getParent();

		ProgressMonitor monitor = DialogHelper.getLocalPersistentProgressMonitor(c, "BackupsManagerFrame.dialogs.restoreRunning1"); //$NON-NLS-1$

		if (!FilesystemUtils.deleteFolderContent(directory, true, new ProgressCallbackProgressMonitorHelper(monitor))) {
			CCLog.addFatalError(LocaleBundle.getString("LogMessage.RestoreFailed")); //$NON-NLS-1$
			return false;
		}

		monitor = DialogHelper.getLocalPersistentProgressMonitor(c, "BackupsManagerFrame.dialogs.restoreRunning2"); //$NON-NLS-1$

		if (!ExportHelper.unzipBackupDir(archive, directoryP, new ProgressCallbackProgressMonitorHelper(monitor))) {
			CCLog.addFatalError(LocaleBundle.getString("LogMessage.RestoreFailed")); //$NON-NLS-1$
			return false;
		}

		CCLog.addInformation(LocaleBundle.getString("LogMessage.RestoreFinished")); //$NON-NLS-1$
		return true;
	}

	public static BackupManager getInstanceDirect() {
		return instance; // Can be null
	}

	public static BackupManager getInstanceWithProgress(Component c) {
		if (instance == null) return null;
		if (instance.isInitialised.get()) return instance;

		ProgressMonitor monitor = DialogHelper.getLocalPersistentIndeterminateProgressMonitor(c, "MainFrame.backupInitialising"); //$NON-NLS-1$
		instance.waitForInitialized(Func0to0.NOOP);
		monitor.close();

		return instance; // Can be null
	}


	public List<CCBackup> getBackupListWithWait() {
		instance.waitForInitialized(Func0to0.NOOP);
		return backuplist;
	}

	private void waitForInitialized(Func0to0 action) {
		while (!isInitialised.get()) ThreadUtils.safeSleep(25);
		action.invoke();
	}

	private void waitForInitialized_IO(Func0to0WithIOException action) throws IOException {
		while (!isInitialised.get()) ThreadUtils.safeSleep(25);
		action.invoke();
	}

	private void runWhenInitialized(Func0to0 action) {
		if(isInitialised.get()) {
			action.invoke();
		} else {
			new Thread(() -> waitForInitialized(action), "BACKUPMANAGER_DEFERRED_ACTION").start(); //$NON-NLS-1$
		}
	}

	public void runWhenInitializedWithProgress(Component c, Func1to0<BackupManager> action) {
		if(isInitialised.get()) {
			action.invoke(this);
		} else {
			ProgressMonitor monitor = DialogHelper.getLocalPersistentIndeterminateProgressMonitor(c, "MainFrame.backupInitialising"); //$NON-NLS-1$

			monitor.close();
			new Thread(() ->
			{
				waitForInitialized(Func0to0.NOOP);
				SwingUtils.invokeLater(() ->
				{
					monitor.close();
					action.invoke(this);
				});

			}, "BACKUPMANAGER_DEFERRED_ACTION").start(); //$NON-NLS-1$
		}
	}
}
