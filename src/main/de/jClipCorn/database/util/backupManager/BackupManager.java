package de.jClipCorn.database.util.backupManager;

import java.awt.Component;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.swing.ProgressMonitor;

import de.jClipCorn.Globals;
import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.util.ExportHelper;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.ProgressCallbackProgressMonitorHelper;
import de.jClipCorn.util.listener.ProgressCallbackSink;

public class BackupManager {
	private final static String BACKUPFILENAME = "jCC-Backup %s_%d." + ExportHelper.EXTENSION_BACKUP; //$NON-NLS-1$
	private final static String BACKUPNAME = "Backup of %s"; //$NON-NLS-1$

	private static BackupManager instance = null;

	private final CCMovieList movielist;

	private List<CCBackup> backuplist = new ArrayList<>();

	public BackupManager(CCMovieList ml) {
		this.movielist = ml;
		instance = this;

		initBackupsList();
	}

	private File getBackupDirectory() {
		File file = new File(PathFormatter.combineAndAppend(PathFormatter.getRealSelfDirectory(), CCProperties.getInstance().PROP_BACKUP_FOLDERNAME.getValue()));

		if (!file.exists()) {
			file.mkdirs();
		}

		return file;
	}

	private File[] getArchiveFiles() {
		File[] result = getBackupDirectory().listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return PathFormatter.getExtension(f.getAbsolutePath()).equalsIgnoreCase(ExportHelper.EXTENSION_BACKUP);
			}
		});

		return result;
	}

	private void initBackupsList() {
		Globals.TIMINGS.start(Globals.TIMING_INIT_BACKUPMANAGER);
		
		File[] archives = getArchiveFiles();
		
		for (File f : archives) {
			try {
				CCBackup backup = new CCBackup(f);

				backuplist.add(backup);
			} catch (IOException e) {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorInitBackupList", f.getName()), e); //$NON-NLS-1$
			}
		}

		Globals.TIMINGS.stop(Globals.TIMING_INIT_BACKUPMANAGER);
	}

	public void doActions(Component c) {
		if (!CCProperties.getInstance().ARG_READONLY) {
			if (CCProperties.getInstance().PROP_BACKUP_CREATEBACKUPS.getValue()) {
				tryCreateBackup(c);
			}

			if (CCProperties.getInstance().PROP_BACKUP_AUTODELETEBACKUPS.getValue()) {
				tryDeleteOldBackups();
			}
		}
	}

	private void tryDeleteOldBackups() {
		for (int i = backuplist.size() - 1; i >= 0; i--) {
			CCBackup backup = backuplist.get(i);

			if (backup.isExpired()) {
				deleteBackup(backup);
			}
		}
	}

	public void deleteBackup(CCBackup bkp) {
		String name = bkp.getName();
		if (bkp.delete()) {
			backuplist.remove(bkp);
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.BackupDeleted", name)); //$NON-NLS-1$
		} else {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.BackupNotDeleted", name)); //$NON-NLS-1$
		}
	}

	private void tryCreateBackup(Component c) {
		int minDiff = CCProperties.getInstance().PROP_BACKUP_BACKUPTIME.getValue();
		CCDate lastBackup = CCProperties.getInstance().PROP_BACKUP_LASTBACKUP.getValue();
		CCDate now = CCDate.getCurrentDate();

		if (lastBackup.getDayDifferenceTo(now) > minDiff && movielist.getDatabaseDirectory().exists()) {
			createBackup(c);
		}
	}

	private File getNewBackupName() {
		String prefix = PathFormatter.appendSeparator(getBackupDirectory().getAbsolutePath());
		int id = 0;
		String now = CCDate.getCurrentDate().toStringSQL();

		for (;;) {
			File f = new File(prefix + String.format(BACKUPFILENAME, now, id));
			if (!f.exists())
				return f;
			id++;
		}
	}

	public String getStandardBackupname() {
		return String.format(BACKUPNAME, CCProperties.getInstance().PROP_DATABASE_NAME.getValue());
	}

	public void createBackup(Component c) {
		createBackup(c, getStandardBackupname(), false);
	}

	public void createBackup(Component c, String name, boolean persistent) {
		createBackup(c, name, CCDate.getCurrentDate(), persistent, Main.VERSION, Main.DBVERSION);
	}

	public void createMigrationBackup(String oldVersion) throws IOException {
		String name = "Automatic backup (database migration from " + oldVersion + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		
		createBackupInternal(name, CCDate.getCurrentDate(), false, Main.VERSION, Main.DBVERSION, new ProgressCallbackSink(), true);
	}

	public void createBackup(Component c, String name, CCDate date, boolean persistent, String jccversion, String dbversion) {
		if (CCProperties.getInstance().ARG_READONLY) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			return;
		}

		CCLog.addInformation(LocaleBundle.getString("LogMessage.BackupStarted")); //$NON-NLS-1$

		ProgressMonitor monitor = DialogHelper.getLocalPersistentProgressMonitor(c, "MainFrame.backupRunning"); //$NON-NLS-1$

		try {
			createBackupInternal(name, date, persistent, jccversion, dbversion, new ProgressCallbackProgressMonitorHelper(monitor), false);
		} catch (IOException e) {
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
		File file = getNewBackupName();

		FileOutputStream os = new FileOutputStream(file);
		ZipOutputStream zos = new ZipOutputStream(os);
		zos.setLevel(CCProperties.getInstance().PROP_BACKUP_COMPRESSION.getValue());

		final List<String> excludedFolders = new ArrayList<>();
		final List<String> excludedFiles   = new ArrayList<>();

		boolean excludeCovers = forceExcludeCovers || (CCProperties.getInstance().PROP_BACKUP_EXCLUDECOVERS.getValue() && !persistent);

		if (excludeCovers) {
			movielist.getCoverCache().getBackupExclusions(excludedFolders, excludedFiles);
		}

		ExportHelper.zipDir(
			movielist.getDatabaseDirectory().getParentFile(), 
			movielist.getDatabaseDirectory(), 
			zos, 
			true, 
			(f) -> testExclusion(excludedFolders, excludedFiles, f),
			mon);

		zos.close();

		CCBackup backup = new CCBackup(file, name, date, persistent, jccversion, dbversion, excludeCovers);
		backup.saveToFile();
		backuplist.add(backup);
	}

	private boolean testExclusion(List<String> excludedFolders, List<String> excludedFiles, File f) {
		if (f.isDirectory()) {
			for (String ex : excludedFolders) {
				if (f.getName().equalsIgnoreCase(ex)) return true;
			}
		} else {
			for (String ex : excludedFiles) {
				if (f.getName().equalsIgnoreCase(ex)) return true;
			}
		}
		return false;
	}

	public boolean restoreBackup(Component c, CCBackup bkp) {
		if (CCProperties.getInstance().ARG_READONLY) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			return false;
		}
		CCLog.addInformation(LocaleBundle.getString("LogMessage.RestoreStarted")); //$NON-NLS-1$

		File archive = bkp.getArchive();
		File directory = movielist.getDatabaseDirectory();
		File directoryP = directory.getParentFile();

		ProgressMonitor monitor = DialogHelper.getLocalPersistentProgressMonitor(c, "BackupsManagerFrame.dialogs.restoreRunning1"); //$NON-NLS-1$

		if (!PathFormatter.deleteFolderContent(directory, true, new ProgressCallbackProgressMonitorHelper(monitor))) {
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

	public static BackupManager getInstance() {
		return instance; // Can be null
	}

	public List<CCBackup> getBackupList() {
		return backuplist;
	}
}
