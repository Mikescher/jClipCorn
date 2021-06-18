package de.jClipCorn.database.driver;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.FileLockedException;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.FileLockManager;
import de.jClipCorn.util.helper.SimpleFileUtils;
import de.jClipCorn.util.parser.TurbineParser;
import org.sqlite.SQLiteException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("nls")
public class SQLiteDatabase extends GenericDatabase {

	private final static String DRIVER = "org.sqlite.JDBC";
	private final static String PROTOCOL = "jdbc:sqlite:";

	private final boolean _readonly;

	public SQLiteDatabase(boolean ro) {
		_readonly = ro;
	}

	public String getDatabaseFilePath(String dbDir, String dbName) {
		return PathFormatter.combine(dbDir, dbName, dbName + ".db");
	}
	
	@Override
	public boolean createNewDatabase(String xmlPath, String dbDir, String dbName) {
		String dbFilePath = getDatabaseFilePath(dbDir, dbName);
		
		try {
			if (databaseExists(dbDir, dbName)) throw new FileAlreadyExistsException(dbFilePath);
			if (FileLockManager.isLocked(dbFilePath)) throw new FileLockedException(dbFilePath);
			
			PathFormatter.createFolders(dbFilePath);
			
			if (! FileLockManager.tryLockFile(dbFilePath, true)) {
				throw new Exception("Cannot lock databasefile");
			}
			
			establishDBConnection(dbDir, dbName);

			TurbineParser turb = new TurbineParser(SimpleFileUtils.readUTF8TextFile(xmlPath));
			turb.parse();
			turb.create(this);
		} catch (Exception e) {
			lastError = e;
			return false;
		}
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean createNewDatabasefromResourceXML(String xmlResPath, String dbDir, String dbName) {
		String dbFilePath = getDatabaseFilePath(dbDir, dbName);
		
		try {
			try {
				Class.forName(DRIVER).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				CCLog.addError(e);
			}
			
			if (databaseExists(dbDir, dbName)) throw new FileAlreadyExistsException(dbFilePath);
			if (FileLockManager.isLocked(dbFilePath)) throw new FileLockedException(dbFilePath);
			
			PathFormatter.createFolders(getDatabaseFilePath(dbDir, dbName));
			
			if (! FileLockManager.tryLockFile(dbFilePath, true)) {
				throw new Exception("Cannot lock databasefile");
			}
			
			connection = DriverManager.getConnection(PROTOCOL + dbFilePath);

			connection.setAutoCommit(true);
			connection.setReadOnly(_readonly);

			TurbineParser turb = new TurbineParser(SimpleFileUtils.readTextResource(xmlResPath, getClass()));
			turb.parse();
			turb.create(this);
		} catch (Exception e) {
			lastError = e;
			return false;
		}
		return true;
	}
	
	@Override
	public boolean databaseExists(String dbDir, String dbName) {
		return new File(getDatabaseFilePath(dbDir, dbName)).exists();
	}
	
	@Override
	public void closeDBConnection(String dbDir, String dbName, boolean cleanshutdown) throws SQLException {
		boolean unlockresult;
		try {
			unlockresult = FileLockManager.unlockFile(getDatabaseFilePath(dbDir, dbName));
			
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
	@SuppressWarnings("deprecation")
	public void establishDBConnection(String dbDir, String dbName) throws Exception {
		String dbFilePath = getDatabaseFilePath(dbDir, dbName);
		
		try {
			Class.forName(DRIVER).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			CCLog.addError(e);
		}
		
		if (!databaseExists(dbDir, dbName)) throw new FileNotFoundException(dbFilePath);
		if (FileLockManager.isLocked(dbFilePath)) throw new FileLockedException(dbFilePath);
		
		if (!FileLockManager.tryLockFile(dbFilePath, true)) {
			throw new Exception("Cannot lock databasefile");
		}

		Properties cfg = new Properties();
		if (_readonly) cfg.setProperty("open_mode", "1");  // (1 == readonly)

		connection = DriverManager.getConnection(PROTOCOL + dbFilePath, cfg);
		
		connection.setAutoCommit(true);

		// Set pragmas
		executeSQLThrow("PRAGMA recursive_triggers = true"); // otherwise "REPLACE INTO x" doesn't work with the history trigger

		try
		{
			// Test if newly created
			executeSQLThrow("SELECT * FROM " + DatabaseStructure.TAB_TEMP.Name + " LIMIT 1");

			if (!_readonly)
			{
				// Test if writeable
				executeSQLThrow("REPLACE INTO [TEMP] (IKEY,IVALUE) VALUES ('RAND','" + Double.toString(Math.random()).substring(2) + "'),('ACCESS','"+ CCDateTime.getCurrentDateTime().toStringISO() +"')");
			}
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

	@Override
	public List<Tuple<String, String>> listTriggerWithStatements() throws SQLException {
		return querySQL("SELECT [name], [sql] FROM sqlite_master WHERE type='trigger'", 2, a -> Tuple.Create((String)a[0], (String)a[1]));
	}
}