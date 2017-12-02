package de.jClipCorn.database.util.backupManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import de.jClipCorn.Main;
import de.jClipCorn.database.util.ExportHelper;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.RegExHelper;

public class CCBackup {
	private final static String HEADER = "Backup Info File"; //$NON-NLS-1$
	
	private final static String PROP_NAME = "name"; //$NON-NLS-1$
	private final static String PROP_DATE = "date"; //$NON-NLS-1$
	private final static String PROP_PERSISTENT = "persistent"; //$NON-NLS-1$
	private final static String PROP_CCVERSION = "jcc-version"; //$NON-NLS-1$
	private final static String PROP_DBVERSION = "db-version"; //$NON-NLS-1$
	private final static String PROP_EXCLUDECOVERS = "exclude-covers"; //$NON-NLS-1$

	private final static String REGEXNAME = "(?<= \\[)[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{4}(?=\\])"; // (?<= \[)[0-9]{1,2}\.[0-9]{1,2}\.[0-9]{4}(?=\]) //$NON-NLS-1$
	
	private File archive;
	private Properties properties;
	
	public CCBackup(File archive) throws FileNotFoundException, IOException {
		this.archive = archive;
		if (! archive.exists()) throw new FileNotFoundException();
		
		Tuple<Properties, Boolean> result = loadProperties(archive);
		
		this.properties = result.Item1;
		
		if (! result.Item2) saveToFile();
	}

	public CCBackup(File archive, String name, CCDate date, boolean persistent, String jccversion, String dbversion, boolean excludeCovers) {
		this.archive = archive;

		properties = new Properties();

		properties.setProperty(PROP_NAME, name);
		properties.setProperty(PROP_DATE, date.toStringSerialize());
		properties.setProperty(PROP_PERSISTENT, persistent ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$
		properties.setProperty(PROP_CCVERSION, jccversion);
		properties.setProperty(PROP_DBVERSION, dbversion);
		properties.setProperty(PROP_EXCLUDECOVERS, excludeCovers ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public File getArchive() {
		return archive;
	}
	
	public void saveToFile() {
		try {
			Map<String, String> env = new HashMap<>(); 
			env.put("create", "false"); //$NON-NLS-1$ //$NON-NLS-2$
			URI uri = URI.create("jar:" + archive.toURI()); //$NON-NLS-1$
			try (FileSystem fs = FileSystems.newFileSystem(uri, env))
			{
			    try (Writer writer = Files.newBufferedWriter(fs.getPath(ExportHelper.FILENAME_BACKUPINFO), StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
					properties.store(writer, HEADER);
			    }
			}
			
			// delete old prop file if exits
			try {
				File externalInfoFile = getPropertyFileFor(archive);
				if (externalInfoFile.exists()) externalInfoFile.delete();
			} catch(Exception e) {
				CCLog.addError(e);
			}
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
		
		CCDate dateresult = CCDate.deserializeOrDefault(result, CCDate.getMinimumDate());
		
		return dateresult;
	}

	public void setDate(CCDate date) {
		properties.setProperty(PROP_DATE, date.toStringSerialize());
		
		saveToFile();
	}
	
	public boolean isPersistent() {
		String result = properties.getProperty(PROP_PERSISTENT);

		return "1".equals(result); //$NON-NLS-1$
	}

	public void setPersistent(boolean persistent) {
		properties.setProperty(PROP_PERSISTENT, persistent ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$
		
		saveToFile();
	}
	
	public String getCCVersion() {
		String result = properties.getProperty(PROP_CCVERSION);
		if (result == null) return ""; //$NON-NLS-1$
		return result;
	}

	public void setCCVersion(String jccversion) {
		properties.setProperty(PROP_CCVERSION, jccversion);
		
		saveToFile();
	}
	
	public String getDBVersion() {
		String result = properties.getProperty(PROP_DBVERSION);
		if (result == null) return ""; //$NON-NLS-1$
		return result;
	}

	public void setDBVersion(String dbversion) {
		properties.setProperty(PROP_DBVERSION, dbversion);
		
		saveToFile();
	}

	public boolean containsCovers() {
		String result = properties.getProperty(PROP_EXCLUDECOVERS);
		if (result == null) return true;
		return result.equals("0"); //$NON-NLS-1$
	}
	
	public boolean isExpired() {
		if (isPersistent()) return false;
		return getDate().getDayDifferenceTo(CCDate.getCurrentDate()) >= CCProperties.getInstance().PROP_BACKUP_LIFETIME.getValue();
	}

	public boolean delete() {
		try {
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

	private CCDate getBackupDateFromOldFileFormat(File f) {
		String sdate = RegExHelper.find(REGEXNAME, f.getName());

		try {
			return CCDate.deserialize(sdate);
		} catch (CCFormatException e) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotParseCCDate", sdate), e); //$NON-NLS-1$
			return CCDate.getMinimumDate();
		}
	}

	private File getPropertyFileFor(File archive) {
		return new File(PathFormatter.getWithoutExtension(archive.getAbsolutePath()) + "." + ExportHelper.EXTENSION_BACKUPPROPERTIES); //$NON-NLS-1$
	}

	private Tuple<Properties, Boolean> loadProperties(File f) {

		// load from info.ini in zip file
		try {
			Map<String, String> env = new HashMap<>(); 
			env.put("create", "false"); //$NON-NLS-1$ //$NON-NLS-2$
			URI uri = URI.create("jar:" + archive.toURI()); //$NON-NLS-1$
			try (FileSystem fs = FileSystems.newFileSystem(uri, env))
			{
			    if (Files.exists(fs.getPath(ExportHelper.FILENAME_BACKUPINFO))) {
					try (Reader reader =  Files.newBufferedReader(fs.getPath(ExportHelper.FILENAME_BACKUPINFO), StandardCharsets.UTF_8)) {
						Properties result1 = new Properties();
						result1.load(reader);
						return Tuple.Create(result1, true);
					}
			    }
			}
		} catch (IOException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateBackupInfo", getName()), e); //$NON-NLS-1$
		}
		
		
		// [Backwards compatibility] load from external info file - archives before 1.10.2
		File externalInfoFile = getPropertyFileFor(f);
		if (externalInfoFile.exists()) {
			Properties result2 = new Properties();
			try {
				FileInputStream stream = new FileInputStream(externalInfoFile);
				result2.load(stream);
				stream.close();
				return Tuple.Create(result2, false);
			} catch (IOException e) {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateBackupInfo", getName()), e); //$NON-NLS-1$
			}
		}
		
		
		// [Backwards compatibility] auto determine values - really old archives
		Properties result3 = new Properties();
		result3.setProperty(PROP_NAME, PathFormatter.getFilename(f.getAbsolutePath()));
		result3.setProperty(PROP_DATE, getBackupDateFromOldFileFormat(f).toStringSerialize());
		result3.setProperty(PROP_PERSISTENT, "0"); //$NON-NLS-1$
		result3.setProperty(PROP_CCVERSION, Main.VERSION);
		result3.setProperty(PROP_DBVERSION, Main.DBVERSION);
		return Tuple.Create(result3, false);
	}
}
