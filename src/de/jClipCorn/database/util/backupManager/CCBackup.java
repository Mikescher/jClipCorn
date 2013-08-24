package de.jClipCorn.database.util.backupManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.formatter.FileSizeFormatter;

public class CCBackup {
	private final static String HEADER = "Backup Info File"; //$NON-NLS-1$
	
	private final static String PROP_NAME = "name"; //$NON-NLS-1$
	private final static String PROP_DATE = "date"; //$NON-NLS-1$
	private final static String PROP_PERSISTENT = "persistent"; //$NON-NLS-1$
	private final static String PROP_CCVERSION = "jcc-version"; //$NON-NLS-1$
	private final static String PROP_DBVERSION = "db-version"; //$NON-NLS-1$
	
	private File archive;
	private File propertiesFile;
	private Properties properties;
	
	public CCBackup(File archive, File properties) throws FileNotFoundException, IOException {
		this.archive = archive;
		if (! archive.exists()) throw new FileNotFoundException();
		
		this.propertiesFile = properties;
		
		this.properties = new Properties();
		
		if (propertiesFile.exists()) {
			FileInputStream stream = new FileInputStream(properties);
			this.properties.load(stream);
			stream.close();
		}
	}

	public File getArchive() {
		return archive;
	}

	public File getPropertiesFile() {
		return propertiesFile;
	}
	
	private void saveToFile() {
		try {
			FileOutputStream stream = new FileOutputStream(propertiesFile);
			properties.store(stream, HEADER);
			stream.close();
		} catch (IOException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateBackupInfo", getName()), e); //$NON-NLS-1$
		}
	}
	
	public String getName() {
		String result = properties.getProperty(PROP_NAME);
		if (result == null) return ""; //$NON-NLS-1$
		return result;
	}

	public void setName(String name) {
		properties.setProperty(PROP_NAME, name);
		
		saveToFile();
	}
	
	public CCDate getDate() {
		String result = properties.getProperty(PROP_DATE);
		if (result == null) return CCDate.getMinimumDate();
		
		CCDate dateresult = CCDate.parse(result, "D.M.Y"); //$NON-NLS-1$
		if (dateresult == null) return CCDate.getMinimumDate();
		
		return dateresult;
	}

	public void setDate(CCDate date) {
		properties.setProperty(PROP_DATE, date.getSimpleStringRepresentation());
		
		saveToFile();
	}
	
	public boolean isPersistent() {
		String result = properties.getProperty(PROP_PERSISTENT);

		return "1".equals(result); //$NON-NLS-1$
	}

	public void setPersistent(boolean pers) {
		properties.setProperty(PROP_PERSISTENT, pers ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$
		
		saveToFile();
	}
	
	public String getCCVersion() {
		String result = properties.getProperty(PROP_CCVERSION);
		if (result == null) return ""; //$NON-NLS-1$
		return result;
	}

	public void setCCVersion(String vers) {
		properties.setProperty(PROP_CCVERSION, vers);
		
		saveToFile();
	}
	
	public String getDBVersion() {
		String result = properties.getProperty(PROP_DBVERSION);
		if (result == null) return ""; //$NON-NLS-1$
		return result;
	}

	public void setDBVersion(String vers) {
		properties.setProperty(PROP_DBVERSION, vers);
		
		saveToFile();
	}
	
	public boolean isExpired() {
		if (isPersistent()) return false;
		return getDate().getDayDifferenceTo(CCDate.getCurrentDate()) > CCProperties.getInstance().PROP_BACKUP_LIFETIME.getValue();
	}

	public boolean delete() {
		try {
			Files.delete(propertiesFile.toPath());
			Files.delete(archive.toPath());
		} catch (IOException e) {
			CCLog.addError(e);
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	public long getSize() {
		return FileSizeFormatter.getFileSize(archive);
	}
}
