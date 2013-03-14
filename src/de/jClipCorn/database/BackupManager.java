package de.jClipCorn.database;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.PathFormatter;
import de.jClipCorn.util.RegExHelper;

public class BackupManager {
	private final static String EXTENSION = "jccbkp";  //$NON-NLS-1$
	private final static String NAME = "%s [%s]"; //$NON-NLS-1$
	@SuppressWarnings("nls")
	private final static String REGEXNAME = "(?<= \\[)[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{4}(?=\\])"; // (?<= \[)[0-9]{1,2}\.[0-9]{1,2}\.[0-9]{4}(?=\])
	
	private final CCMovieList movielist;
	
	public BackupManager(CCMovieList ml) {
		this.movielist = ml;
	}

	public void start() {
		if (CCProperties.getInstance().PROP_BACKUP_CREATEBACKUPS.getValue()) {
			tryCreateBackup();
		}
		
		if (CCProperties.getInstance().PROP_BACKUP_AUTODELETEBACKUPS.getValue()) {
			tryDeleteOldBackups();
		}
	}
	
	private File getBackupDirectory() {
		File f = new File(PathFormatter.getRealSelfDirectory() + CCProperties.getInstance().PROP_BACKUP_FOLDERNAME.getValue() + '\\');
		
		if (! f.exists()) {
			f.mkdirs();
		}
		
		return f;
	}
	
	private CCDate getBackupDate(File f) {
		String sdate = RegExHelper.find(REGEXNAME, f.getName());
		CCDate result = CCDate.getNewMinimumDate();
		if (result.parse(sdate, "D.M.Y")) { //$NON-NLS-1$
			return result;
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
				return PathFormatter.getExtension(f.getAbsolutePath()).equalsIgnoreCase("jccbkp"); //$NON-NLS-1$
			}
		});
		
		int maxDiff = CCProperties.getInstance().PROP_BACKUP_LIFETIME.getValue();
		
		CCDate now = new CCDate();
		
		for (File b : backups) {
			CCDate date = getBackupDate(b);
			if (date.getDayDifferenceTo(now) > maxDiff) {
				if (b.delete()) {
					CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.BackupDeleted", date)); //$NON-NLS-1$
				} else {
					CCLog.addError(LocaleBundle.getFormattedString("LogMessage.BackupNotDeleted", date)); //$NON-NLS-1$
				}
			}
		}
	}
	
	/**
	 * Create Backup when the time has come
	 */
	private void tryCreateBackup() {
		int minDiff = CCProperties.getInstance().PROP_BACKUP_BACKUPTIME.getValue();
		CCDate lastBackup = CCProperties.getInstance().PROP_BACKUP_LASTBACKUP.getValue();
		CCDate now = new CCDate();
		
		if (lastBackup.getDayDifferenceTo(now) > minDiff && movielist.getDatabaseDirectory().exists()) {
			createBackup();
		}
	}
	
	public void createBackup() {
		CCDate now = new CCDate();
		
		File b = new File(getBackupDirectory().getAbsolutePath() + '\\' + String.format(NAME, CCProperties.getInstance().PROP_DATABASE_NAME.getValue(), now.getSimpleStringRepresentation()) + '.' + EXTENSION);
		
		try {
			FileOutputStream os = new FileOutputStream(b);
			ZipOutputStream zos = new ZipOutputStream(os);
			zos.setLevel(CCProperties.getInstance().PROP_BACKUP_COMPRESSION.getValue());
			
			zipDir(movielist.getDatabaseDirectory().getParentFile(), movielist.getDatabaseDirectory(), zos);
			
			zos.close();
		} catch (FileNotFoundException e) {
			CCLog.addError(e);
		} catch (IOException e) {
			CCLog.addError(e);
		}
		
		CCLog.addInformation(LocaleBundle.getString("LogMessage.BackupCreated")); //$NON-NLS-1$
		CCProperties.getInstance().PROP_BACKUP_LASTBACKUP.setValue(new CCDate());
	}
	
	private static void zipDir(File owner, File zipDir, ZipOutputStream zos) {
		try {
			String[] dirList = zipDir.list();
			byte[] readBuffer = new byte[2156];
			int bytesIn = 0;
			for (int i = 0; i < dirList.length; i++) {
				File f = new File(zipDir, dirList[i]);
				if (f.isDirectory()) {
					zipDir(owner, f, zos);
					continue;
				}
				
				FileInputStream fis = new FileInputStream(f);
				ZipEntry anEntry = new ZipEntry(f.getAbsolutePath().replace(owner.getAbsolutePath() + '\\', "")); //$NON-NLS-1$
				zos.putNextEntry(anEntry);
				
				while ((bytesIn = fis.read(readBuffer)) != -1) {
					zos.write(readBuffer, 0, bytesIn);
				}
				fis.close();
			}
		} catch (Exception e) {
			CCLog.addError(e);
		}
	}
}
