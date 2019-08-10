package de.jClipCorn.database.driver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.exceptions.FileLockedException;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.FileLockManager;
import de.jClipCorn.util.helper.SimpleFileUtils;
import de.jClipCorn.util.parser.TurbineParser;
import org.sqlite.SQLiteException;

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

		// Set pragmas
		executeSQLThrow("PRAGMA recursive_triggers = true"); // otherwise "REPLACE INTO x" doesn't work with the history trigger

		try
		{
			// Test if newly created
			executeSQLThrow("SELECT * FROM " + Statements.TAB_TEMP + " LIMIT 1");

			// Test if writeable
			executeSQLThrow("REPLACE INTO " + Statements.TAB_TEMP + " (" + Statements.COL_TEMP_KEY.Name + ", " + Statements.COL_TEMP_VALUE.Name + ") VALUES ('" + "RAND" + "', '" + Math.random() + "')");
		}
		catch (SQLiteException e)
		{
			if (e.getMessage().contains("no such table: TEMP"))
			{
				if (Integer.parseInt(querySingleStringSQL("SELECT IVALUE FROM INFO WHERE IKEY='VERSION_DB'", 0)) < 14) return; // before there was a TEMP table - will be migrated
			}

			throw e;
		}
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
	public boolean isInMemory() { return false; }

	@Override
	public List<String> listTables() throws SQLException {
		return querySQL("SELECT name FROM sqlite_master WHERE type='table'", 1, a -> (String)a[0]);
	}

	@Override
	public List<String> listTrigger() throws SQLException {
		return querySQL("SELECT name FROM sqlite_master WHERE type='trigger'", 1, a -> (String)a[0]);
	}

	@Override
	public List<String> listViews() throws SQLException {
		return querySQL("SELECT name FROM sqlite_master WHERE type='view'", 1, a -> (String)a[0]);
	}
}