package de.jClipCorn.database.util.backupManager;

import java.awt.Component;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipOutputStream;

import javax.swing.ProgressMonitor;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.util.ExportHelper;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.RegExHelper;
import de.jClipCorn.util.listener.ProgressCallbackProgressMonitorHelper;

public class BackupManager {
	private final static String BACKUPFILENAME = "jCC-Backup %s_%d." + ExportHelper.EXTENSION_BACKUP; //$NON-NLS-1$
	private final static String BACKUPNAME = "Backup of %s"; //$NON-NLS-1$
	private final static String REGEXNAME = "(?<= \\[)[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{4}(?=\\])"; // (?<= \[)[0-9]{1,2}\.[0-9]{1,2}\.[0-9]{4}(?=\]) //$NON-NLS-1$
	
	private static BackupManager instance = null;
	
	private final CCMovieList movielist;
	
	private List<CCBackup> backuplist = new ArrayList<>();
	
	public BackupManager(CCMovieList ml) {
		this.movielist = ml;
		instance = this;
		
		initBackupsList();
	}
	
	private File getBackupDirectory() {
		File file = new File(PathFormatter.getRealSelfDirectory() + CCProperties.getInstance().PROP_BACKUP_FOLDERNAME.getValue() + '\\');
		
		if (! file.exists()) {
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
	
	private File getPropertyFileFor(File archive) {
		return new File(PathFormatter.getWithoutExtension(archive.getAbsolutePath()) + "." + ExportHelper.EXTENSION_BACKUPPROPERTIES); //$NON-NLS-1$
	}
	
	private Properties getPropertyFileClassFor(File archive) {
		File prop = getPropertyFileFor(archive);
		
		if (prop.exists()) {
			Properties result = new Properties();
			try {
				result.load(new FileInputStream(prop));
			} catch (IOException e) {
				return null;
			}
			return result;
		} else  {
			return null;
		}
	}
	
	private CCDate getBackupDateFromOldFileFormat(File f) {
		String sdate = RegExHelper.find(REGEXNAME, f.getName());
		if (CCDate.testparse(sdate, "D.M.Y")) { //$NON-NLS-1$
			return CCDate.parse(sdate, "D.M.Y"); //$NON-NLS-1$
		} else {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotParseCCDate", sdate)); //$NON-NLS-1$
			return CCDate.getMinimumDate();
		}
	}
	
	private void initBackupsList() {
		File[] archives = getArchiveFiles();
		
		for (File f : archives) {
			Properties prop  = getPropertyFileClassFor(f);
			File propfile = getPropertyFileFor(f);
			
			boolean doRecreatePropFile = prop == null;

			try {
				CCBackup backup = new CCBackup(f, propfile);

				if (doRecreatePropFile) { // Only for Backwards compatibility
					String filename = PathFormatter.getFilename(f.getAbsolutePath());
					backup.setName(filename);
					backup.setDate(getBackupDateFromOldFileFormat(f));
					backup.setPersistent(false);
					backup.setCCVersion(Main.VERSION);
					backup.setDBVersion(Main.DBVERSION);
				}
				
				backuplist.add(backup);
			} catch (IOException e) {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorInitBackupList", f.getName()), e); //$NON-NLS-1$
			}
		}
	}
	
	public void doActions(Component c) {
		if (CCProperties.getInstance().PROP_BACKUP_CREATEBACKUPS.getValue()) {
			tryCreateBackup(c);
		}
		
		if (CCProperties.getInstance().PROP_BACKUP_AUTODELETEBACKUPS.getValue()) {
			tryDeleteOldBackups();
		}
	}

	private void tryDeleteOldBackups() {
		for (int i = backuplist.size()-1; i >= 0 ; i--) {
			CCBackup backup = backuplist.get(i);
			
			if (backup.isExpired()) {
				String name = backup.getName();
				if (backup.delete()) {
					backuplist.remove(i);
					CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.BackupDeleted", name)); //$NON-NLS-1$
				} else {
					CCLog.addError(LocaleBundle.getFormattedString("LogMessage.BackupNotDeleted", name)); //$NON-NLS-1$
				}
			}
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
		String prefix = getBackupDirectory().getAbsolutePath() + '\\';
		int id = 0;
		String now = CCDate.getCurrentDate().getSimpleStringRepresentation();
		
		for(;;) {
			File f = new File(prefix + String.format(BACKUPFILENAME, now, id));
			if (! f.exists()) return f;
			id++;
		}
	}
	
	public void createBackup(Component c) {
		createBackup(c, String.format(BACKUPNAME, CCProperties.getInstance().PROP_DATABASE_NAME.getValue()), CCDate.getCurrentDate(), false, Main.VERSION, Main.DBVERSION);
	}
	
	public void createBackup(Component c, String name, CCDate date, boolean persistent, String jccversion, String dbversion) {
		if (CCProperties.getInstance().ARG_READONLY) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			return;
		}
		CCLog.addInformation(LocaleBundle.getString("LogMessage.BackupStarted")); //$NON-NLS-1$
		
		ProgressMonitor monitor = DialogHelper.getLocalPersistentProgressMonitor(c, "MainFrame.backupRunning"); //$NON-NLS-1$
		
		File file = getNewBackupName();
		
		try {
			FileOutputStream os = new FileOutputStream(file);
			ZipOutputStream zos = new ZipOutputStream(os);
			zos.setLevel(CCProperties.getInstance().PROP_BACKUP_COMPRESSION.getValue());
			
			ExportHelper.zipDir(movielist.getDatabaseDirectory().getParentFile(), movielist.getDatabaseDirectory(), zos, true, new ProgressCallbackProgressMonitorHelper(monitor));
			
			zos.close();
			
			CCBackup backup = new CCBackup(file, getPropertyFileFor(file));
			backup.setName(name);
			backup.setDate(date);
			backup.setPersistent(persistent);
			backup.setCCVersion(jccversion);
			backup.setDBVersion(dbversion);
			backuplist.add(backup);
		} catch (FileNotFoundException e) {
			CCLog.addError(e);
		} catch (IOException e) {
			CCLog.addError(e);
		}
		
		CCLog.addInformation(LocaleBundle.getString("LogMessage.BackupCreated")); //$NON-NLS-1$
		CCProperties.getInstance().PROP_BACKUP_LASTBACKUP.setValue(CCDate.getCurrentDate());
	}
	
	public static BackupManager getInstance() {
		return instance; // Can be null
	}
}
