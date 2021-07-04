package de.jClipCorn.features.backupManager;

import de.jClipCorn.Main;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.helper.RegExHelper;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CCBackup {
	private final static String HEADER = "Backup Info File"; //$NON-NLS-1$
	
	private final static String PROP_NAME = "name"; //$NON-NLS-1$
	private final static String PROP_DATE = "date"; //$NON-NLS-1$
	private final static String PROP_PERSISTENT = "persistent"; //$NON-NLS-1$
	private final static String PROP_CCVERSION = "jcc-version"; //$NON-NLS-1$
	private final static String PROP_DBVERSION = "db-version"; //$NON-NLS-1$
	private final static String PROP_EXCLUDECOVERS = "exclude-covers"; //$NON-NLS-1$

	private final static String REGEXNAME = "(?<= \\[)[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{4}(?=\\])"; // (?<= \[)[0-9]{1,2}\.[0-9]{1,2}\.[0-9]{4}(?=\]) //$NON-NLS-1$
	
	private final FSPath archive;
	private final Properties properties;
	
	public CCBackup(FSPath archive) throws IOException {
		this.archive = archive;
		if (! archive.exists()) throw new FileNotFoundException();
		
		Tuple<Properties, Boolean> result = loadProperties(archive);
		
		this.properties = result.Item1;
		
		if (! result.Item2) saveToFile();
	}

	public CCBackup(FSPath archive, String name, CCDate date, boolean persistent, String jccversion, String dbversion, boolean excludeCovers) {
		this.archive = archive;

		properties = new Properties();

		properties.setProperty(PROP_NAME, name);
		properties.setProperty(PROP_DATE, date.toStringSerialize());
		properties.setProperty(PROP_PERSISTENT, persistent ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$
		properties.setProperty(PROP_CCVERSION, jccversion);
		properties.setProperty(PROP_DBVERSION, dbversion);
		properties.setProperty(PROP_EXCLUDECOVERS, excludeCovers ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public FSPath getArchive() {
		return archive;
	}
	
	public boolean saveToFile() {
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
				var externalInfoFile = getPropertyFileFor(archive);
				if (externalInfoFile.exists()) externalInfoFile.deleteWithException();
			} catch(Exception e) {
				CCLog.addError(e);
			}

			return true;
		} catch (ReadOnlyFileSystemException e) {
			// "Bug" in JRE with zip files and network drives under Windows
			// https://stackoverflow.com/questions/24863465
			return saveToFileFallback();
		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateBackupInfo", getName()), e); //$NON-NLS-1$
			return false;
		}
	}

	public boolean saveToFileFallback() {
		try {
			var tempFile = SimpleFileUtils.getSystemTempFile("zip"); //$NON-NLS-1$

			FileUtils.copyFile(archive.toFile(), tempFile.toFile());

			Map<String, String> env = new HashMap<>();
			env.put("create", "false"); //$NON-NLS-1$ //$NON-NLS-2$
			URI uri = URI.create("jar:" + tempFile.toURI()); //$NON-NLS-1$
			try (FileSystem fs = FileSystems.newFileSystem(uri, env))
			{
				try (Writer writer = Files.newBufferedWriter(fs.getPath(ExportHelper.FILENAME_BACKUPINFO), StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
					properties.store(writer, HEADER);
				}
			}

			FileUtils.copyFile(tempFile.toFile(), archive.toFile());
			FileUtils.forceDelete(tempFile.toFile());

			// delete old prop file if exits
			try {
				var externalInfoFile = getPropertyFileFor(archive);
				if (externalInfoFile.exists()) externalInfoFile.deleteWithException();
			} catch(Exception e) {
				CCLog.addError(e);
			}

			return true;
		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateBackupInfo", getName()), e); //$NON-NLS-1$
			return false;
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

		return CCDate.deserializeOrDefault(result, CCDate.getMinimumDate());
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

	public CCFileSize getSize() {
		return archive.filesize();
	}

	private CCDate getBackupDateFromOldFileFormat(FSPath f) {
		String sdate = RegExHelper.find(REGEXNAME, f.getFilenameWithExt());

		try {
			return CCDate.deserialize(sdate);
		} catch (CCFormatException e) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotParseCCDate", sdate), e); //$NON-NLS-1$
			return CCDate.getMinimumDate();
		}
	}

	private FSPath getPropertyFileFor(FSPath archive) {
		return archive.replaceExtension(ExportHelper.EXTENSION_BACKUPPROPERTIES);
	}

	private Tuple<Properties, Boolean> loadProperties(FSPath f) {

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
		var externalInfoFile = getPropertyFileFor(f);
		if (externalInfoFile.exists()) {
			Properties result2 = new Properties();
			try (var stream = new FileInputStream(externalInfoFile.toFile())) {
				result2.load(stream);
				return Tuple.Create(result2, false);
			} catch (IOException e) {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotUpdateBackupInfo", getName()), e); //$NON-NLS-1$
			}
		}
		
		
		// [Backwards compatibility] auto determine values - really old archives
		Properties result3 = new Properties();
		result3.setProperty(PROP_NAME, f.getFilenameWithoutExt());
		result3.setProperty(PROP_DATE, getBackupDateFromOldFileFormat(f).toStringSerialize());
		result3.setProperty(PROP_PERSISTENT, "0"); //$NON-NLS-1$
		result3.setProperty(PROP_CCVERSION, Main.VERSION);
		result3.setProperty(PROP_DBVERSION, Main.DBVERSION);
		return Tuple.Create(result3, false);
	}
}
