package de.jClipCorn.database.driver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.sql.DriverManager;
import java.sql.SQLException;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.exceptions.FileLockedException;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.FileLockManager;
import de.jClipCorn.util.helper.SimpleFileUtils;
import de.jClipCorn.util.parser.TurbineParser;

@SuppressWarnings("nls")
public class SQLiteDatabase extends GenericDatabase {

	private final static String DRIVER = "org.sqlite.JDBC";
	private final static String PROTOCOL = "jdbc:sqlite:";

	public String getDatabaseFilePath(String dbPath) {
		return PathFormatter.combine(dbPath, dbPath + ".db");
	}
	
	@Override
	public boolean createNewDatabase(String xmlPath, String dbPath) {
		String dbFilePath = getDatabaseFilePath(dbPath);
		
		try {
			if (databaseExists(dbPath)) throw new FileAlreadyExistsException(dbFilePath);
			if (FileLockManager.isLocked(dbFilePath)) throw new FileLockedException(dbFilePath);
			
			PathFormatter.createFolders(dbFilePath);
			
			if (! FileLockManager.tryLockFile(dbFilePath, true)) {
				throw new Exception("Cannot lock databasefile");
			}
			
			establishDBConnection(dbPath);

			TurbineParser turb = new TurbineParser(SimpleFileUtils.readUTF8TextFile(xmlPath), this);
			turb.parse();
			turb.create();
		} catch (Exception e) {
			lastError = e;
			return false;
		}
		return true;
	}

	@Override
	public boolean createNewDatabasefromResourceXML(String xmlResPath, String dbPath) {
		String dbFilePath = getDatabaseFilePath(dbPath);
		
		try {
			try {
				Class.forName(DRIVER).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				CCLog.addError(e);
			}
			
			if (databaseExists(dbPath)) throw new FileAlreadyExistsException(dbFilePath);
			if (FileLockManager.isLocked(dbFilePath)) throw new FileLockedException(dbFilePath);
			
			PathFormatter.createFolders(getDatabaseFilePath(dbPath));
			
			if (! FileLockManager.tryLockFile(dbFilePath, true)) {
				throw new Exception("Cannot lock databasefile");
			}
			
			connection = DriverManager.getConnection(PROTOCOL + dbFilePath);
			connection.setAutoCommit(true);

			TurbineParser turb = new TurbineParser(SimpleFileUtils.readTextResource(xmlResPath, getClass()), this);
			turb.parse();
			turb.create();
		} catch (Exception e) {
			lastError = e;
			return false;
		}
		return true;
	}
	
	@Override
	public boolean databaseExists(String dbPath) {
		return new File(getDatabaseFilePath(dbPath)).exists();
	}
	
	@Override
	public void closeDBConnection(String dbPath, boolean cleanshutdown) throws SQLException {
		boolean unlockresult;
		try {
			unlockresult = FileLockManager.unlockFile(getDatabaseFilePath(dbPath));
			
			if (! unlockresult) {
				CCLog.addWarning("Cannot unlock database file");
			}
		} catch (IOException e) {
			CCLog.addWarning("Cannot unlock database file", e);
		}
		
        if(connection != null) {
            connection.close();
        }
	}
	
	@Override
	public void establishDBConnection(String dbPath) throws Exception {
		String dbFilePath = getDatabaseFilePath(dbPath);
		
		try {
			Class.forName(DRIVER).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			CCLog.addError(e);
		}
		
		if (!databaseExists(dbPath)) throw new FileNotFoundException(dbFilePath);
		if (FileLockManager.isLocked(dbFilePath)) throw new FileLockedException(dbFilePath);
		
		if (!FileLockManager.tryLockFile(dbFilePath, true)) {
			throw new Exception("Cannot lock databasefile");
		}
		
		connection = DriverManager.getConnection(PROTOCOL + dbFilePath);
		
		connection.setAutoCommit(true);
		
		// Test if newly created
		executeSQLThrow("SELECT * FROM " + Statements.TAB_INFO + " LIMIT 1");
		
		// Test if writeable
		executeSQLThrow("REPLACE INTO " + Statements.TAB_INFO + " (" + Statements.COL_INFO_KEY.Name + ", " + Statements.COL_INFO_VALUE.Name + ") VALUES ('" + CCDatabase.INFOKEY_RAND + "', '" + Math.random() + "')");
	}

	@Override
	public boolean supportsDateType() {
		return false;
	}

	@Override
	public CCDatabaseDriver getDBType() {
		return CCDatabaseDriver.SQLITE;
	}

	@Override
	public boolean isInMemory() {return false;}
}