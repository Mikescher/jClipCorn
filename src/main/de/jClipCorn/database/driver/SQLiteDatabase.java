package de.jClipCorn.database.driver;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.FileLockedException;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FileLockManager;
import de.jClipCorn.util.sqlwrapper.SQLBuilder;
import org.sqlite.SQLiteException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
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

	public FSPath getDatabaseFilePath(FSPath dbDir, String dbName) {
		return dbDir.append(dbName, dbName + ".db");
	}
	
	@Override
	public boolean createNewDatabase(FSPath dbDir, String dbName) {
		var dbFilePath = getDatabaseFilePath(dbDir, dbName);
		
		try {
			if (databaseExists(dbDir, dbName)) throw new FileAlreadyExistsException(dbFilePath.toString());
			if (FileLockManager.isLocked(dbFilePath)) throw new FileLockedException(dbFilePath.toString());

			dbFilePath.createFolders();

			if (! FileLockManager.tryLockFile(dbFilePath, true)) {
				throw new Exception("Cannot lock databasefile");
			}
			
			open(dbDir, dbName, true);

			for (var tab: DatabaseStructure.TABLES)
			{
				var sql = SQLBuilder.createSchema(tab).build(this::createPreparedStatement, new ArrayList<>());
				sql.execute();
				sql.tryClose();
			}
		} catch (Exception e) {
			lastError = e;
			return false;
		}
		return true;
	}

	@Override
	public boolean databaseExists(FSPath dbDir, String dbName) {
		return getDatabaseFilePath(dbDir, dbName).exists();
	}
	
	@Override
	public void closeDBConnection(FSPath dbDir, String dbName, boolean cleanshutdown) throws SQLException {
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
	public void establishDBConnection(FSPath dbDir, String dbName) throws Exception {
		open(dbDir, dbName, false);
	}

	@SuppressWarnings("deprecation")
	private void open(FSPath dbDir, String dbName, boolean createNew) throws Exception {
		var dbFilePath = getDatabaseFilePath(dbDir, dbName);

		if (_readonly && createNew) throw new Exception("Cannot create new DB in readonly mode");

		try {
			Class.forName(DRIVER).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			CCLog.addError(e);
		}

		if (!createNew)
		{
			if (!databaseExists(dbDir, dbName)) throw new FileNotFoundException(dbFilePath.toString());
			if (FileLockManager.isLocked(dbFilePath)) throw new FileLockedException(dbFilePath.toString());

			if (!FileLockManager.tryLockFile(dbFilePath, true)) {
				throw new Exception("Cannot lock databasefile");
			}
		}

		Properties cfg = new Properties();
		if (_readonly) cfg.setProperty("open_mode", "1");  // (1 == readonly)

		connection = DriverManager.getConnection(PROTOCOL + dbFilePath, cfg);
		
		connection.setAutoCommit(true);

		// Set pragmas
		executeSQLThrow("PRAGMA recursive_triggers = true"); // otherwise "REPLACE INTO x" doesn't work with the history trigger

		try
		{
			if (!createNew)
			{
				// Throw if newly created
				executeSQLThrow("SELECT * FROM " + DatabaseStructure.TAB_TEMP.Name + " LIMIT 1");

				if (!_readonly)
				{
					// Test if writeable
					executeSQLThrow("REPLACE INTO [TEMP] (IKEY,IVALUE) VALUES ('RAND','" + Double.toString(Math.random()).substring(2) + "'),('ACCESS','"+ CCDateTime.getCurrentDateTime().toStringISO() +"')");
				}
			}
		}
		catch (SQLiteException e)
		{
			if (e.getMessage().contains("no such table: TEMP"))
			{
				try
				{
					if (Integer.parseInt(querySingleStringSQL("SELECT IVALUE FROM INFO WHERE IKEY='VERSION_DB'", 0)) < 14) return; // before there was a TEMP table - will be migrated
				}
				catch (Exception e2)
				{
					// ...
				}
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