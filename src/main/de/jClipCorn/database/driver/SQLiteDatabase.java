package de.jClipCorn.database.driver;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.FileLockedException;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FileLockManager;
import de.jClipCorn.util.sqlwrapper.CCSQLTableDef;
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

	public final static String USERDATA_DB_FILENAME = "ClipCornUserData.db";

	private final boolean _readonly;

	public SQLiteDatabase(boolean ro) {
		_readonly = ro;
	}

	public FSPath getDatabaseFilePath(FSPath dbDir, String dbName) {
		return dbDir.append(dbName, dbName + ".db");
	}

	public FSPath getUserDataFilePath(FSPath dbDir, String dbName) {
		return dbDir.append(dbName, USERDATA_DB_FILENAME);
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

			createSchema(DatabaseStructure.TABLES_MAIN);
			createSchema(DatabaseStructure.TABLES_USERDATA);
		} catch (Exception e) {
			lastError = e;
			return false;
		}
		return true;
	}

	public void createSchema(CCSQLTableDef[] tables) throws Exception {
		for (var tab: tables)
		{
			var sql = SQLBuilder.createSchema(tab).build(this::createPreparedStatement, new ArrayList<>());
			sql.execute();
			sql.tryClose();
		}
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

		// NOTE: do NOT enable WAL here - SQLite only guarantees an atomic commit across attached
		//       databases while every one of them is in rollback-journal mode, and a single entity
		//       save writes to both `main` and `userdata`.

		// Read-performance pragmas (connection-level, do NOT change the on-disk format - safe with file-locking & file-copy backups)
		executeSQLThrow("PRAGMA mmap_size = 268435456"); // 256MB memory-mapped I/O - speeds up (cold) reads by avoiding read() syscalls
		executeSQLThrow("PRAGMA cache_size = -65536");   // 64MB page-cache (default is ~2MB)
		executeSQLThrow("PRAGMA temp_store = MEMORY");   // keep temp b-trees (ORDER BY / sorter) in RAM

		attachUserData(dbDir, dbName);

		try
		{
			if (!createNew)
			{
				// Throw if newly created
				executeSQLThrow("SELECT * FROM " + DatabaseStructure.TAB_TEMP.qualifiedName() + " LIMIT 1");

				if (!_readonly)
				{
					// Test if writeable
					executeSQLThrow("REPLACE INTO main.[TEMP] (IKEY,IVALUE) VALUES ('RAND','" + Double.toString(Math.random()).substring(2) + "'),('ACCESS','"+ CCDateTime.getCurrentDateTime().toStringISO() +"')");
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

	private void attachUserData(FSPath dbDir, String dbName) throws SQLException {
		var udPath = getUserDataFilePath(dbDir, dbName);

		if (_readonly && !udPath.exists()) {
			// ATTACH cannot create the file in readonly mode - fall back to an empty in-memory user-db
			CCLog.addWarning("User-data database is missing, continuing with an empty in-memory one: " + udPath);
			executeSQLThrow("ATTACH DATABASE ':memory:' AS " + DatabaseStructure.SCHEMA_USERDATA);
			return;
		}

		executeSQLThrow("ATTACH DATABASE '" + udPath.toString().replace("'", "''") + "' AS " + DatabaseStructure.SCHEMA_USERDATA);
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
		var r = querySQL("SELECT name FROM main.sqlite_master WHERE type='trigger'", 1, a -> (String)a[0]);
		r.addAll(querySQL("SELECT name FROM userdata.sqlite_master WHERE type='trigger'", 1, a -> (String)a[0]));
		return r;
	}

	@Override
	public List<String> listViews() throws SQLException {
		return querySQL("SELECT name FROM sqlite_master WHERE type='view'", 1, a -> (String)a[0]);
	}

	@Override
	public List<Tuple<String, String>> listTriggerWithStatements() throws SQLException {
		var r = querySQL("SELECT [name], [sql] FROM main.sqlite_master WHERE type='trigger'", 2, a -> Tuple.Create((String)a[0], (String)a[1]));
		r.addAll(querySQL("SELECT [name], [sql] FROM userdata.sqlite_master WHERE type='trigger'", 2, a -> Tuple.Create((String)a[0], (String)a[1])));
		return r;
	}
}