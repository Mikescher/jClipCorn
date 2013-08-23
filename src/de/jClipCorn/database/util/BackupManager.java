package de.jClipCorn.database.util;

import java.awt.Component;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import javax.swing.ProgressMonitor;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.RegExHelper;
import de.jClipCorn.util.listener.ProgressCallbackProgressMonitorHelper;

public class BackupManager {
	private final static String NAME = "%s [%s]"; //$NON-NLS-1$
	@SuppressWarnings("nls")
	private final static String REGEXNAME = "(?<= \\[)[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{4}(?=\\])"; // (?<= \[)[0-9]{1,2}\.[0-9]{1,2}\.[0-9]{4}(?=\])
	
	private final CCMovieList movielist;
	
	public BackupManager(CCMovieList ml) {
		this.movielist = ml;
	}

	public void start(Component c) {
		if (CCProperties.getInstance().PROP_BACKUP_CREATEBACKUPS.getValue()) {
			tryCreateBackup(c);
		}
		
		if (CCProperties.getInstance().PROP_BACKUP_AUTODELETEBACKUPS.getValue()) {
			tryDeleteOldBackups();
		}
	}
	
	private File getBackupDirectory() {
		File file = new File(PathFormatter.getRealSelfDirectory() + CCProperties.getInstance().PROP_BACKUP_FOLDERNAME.getValue() + '\\');
		
		if (! file.exists()) {
			file.mkdirs();
		}
		
		return file;
	}
	
	private CCDate getBackupDate(File f) {
		String sdate = RegExHelper.find(REGEXNAME, f.getName());
		if (CCDate.testparse(sdate, "D.M.Y")) { //$NON-NLS-1$
			return CCDate.parse(sdate, "D.M.Y"); //$NON-NLS-1$
		} else {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotParseCCDate", sdate)); //$NON-NLS-1$
			return null;
		}
	}
	
	/**
	 * Delete all old Backups
	 */
	private void tryDeleteOldBackups() {
		File[] backups = getBackupDirectory().listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return PathFormatter.getExtension(f.getAbsolutePath()).equalsIgnoreCase(ExportHelper.EXTENSION_BACKUP);
			}
		});
		
		int maxDiff = CCProperties.getInstance().PROP_BACKUP_LIFETIME.getValue();
		
		CCDate now = CCDate.getCurrentDate();

		for (File b : backups) {
			CCDate date = getBackupDate(b);
			if (date.getDayDifferenceTo(now) > maxDiff) {
				if (CCProperties.getInstance().ARG_READONLY) {
					CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
				} else {
					if (b.delete()) {
						CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.BackupDeleted", date)); //$NON-NLS-1$
					} else {
						CCLog.addError(LocaleBundle.getFormattedString("LogMessage.BackupNotDeleted", date)); //$NON-NLS-1$
					}
				}
			}
		}
	}
	
	/**
	 * Create Backup when the time has come
	 */
	private void tryCreateBackup(Component c) {
		int minDiff = CCProperties.getInstance().PROP_BACKUP_BACKUPTIME.getValue();
		CCDate lastBackup = CCProperties.getInstance().PROP_BACKUP_LASTBACKUP.getValue();
		CCDate now = CCDate.getCurrentDate();
		
		if (lastBackup.getDayDifferenceTo(now) > minDiff && movielist.getDatabaseDirectory().exists()) {
			createBackup(c);
		}
	}
	
	public void createBackup(Component c) {
		if (CCProperties.getInstance().ARG_READONLY) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			return;
		}
		CCLog.addInformation(LocaleBundle.getString("LogMessage.BackupStarted")); //$NON-NLS-1$
		
		ProgressMonitor monitor = DialogHelper.getLocalPersistentProgressMonitor(c, "MainFrame.backupRunning"); //$NON-NLS-1$
		
		CCDate now = CCDate.getCurrentDate();
		
		File file = new File(getBackupDirectory().getAbsolutePath() + '\\' + String.format(NAME, CCProperties.getInstance().PROP_DATABASE_NAME.getValue(), now.getSimpleStringRepresentation()) + '.' + ExportHelper.EXTENSION_BACKUP);
		
		try {
			FileOutputStream os = new FileOutputStream(file);
			ZipOutputStream zos = new ZipOutputStream(os);
			zos.setLevel(CCProperties.getInstance().PROP_BACKUP_COMPRESSION.getValue());
			
			ExportHelper.zipDir(movielist.getDatabaseDirectory().getParentFile(), movielist.getDatabaseDirectory(), zos, true, new ProgressCallbackProgressMonitorHelper(monitor));
			
			zos.close();
		} catch (FileNotFoundException e) {
			CCLog.addError(e);
		} catch (IOException e) {
			CCLog.addError(e);
		}
		
		CCLog.addInformation(LocaleBundle.getString("LogMessage.BackupCreated")); //$NON-NLS-1$
		CCProperties.getInstance().PROP_BACKUP_LASTBACKUP.setValue(CCDate.getCurrentDate());
	}
}
