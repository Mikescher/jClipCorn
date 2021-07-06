package de.jClipCorn.database.driver;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.sqlwrapper.SQLBuilder;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("nls")
public class MemoryDatabase extends GenericDatabase {

	private final static String DRIVER = "org.sqlite.JDBC";
	private final static String PROTOCOL = "jdbc:sqlite::memory:";
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean createNewDatabase(FSPath dbDir, String dbName) {
		try {
			Class.forName(DRIVER).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			CCLog.addError(e);
		}
		
		try {
			connection = DriverManager.getConnection(PROTOCOL);
			connection.setAutoCommit(true);

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
		return false;
	}
	
	@Override
	public void closeDBConnection(FSPath dbDir, String dbName, boolean cleanshutdown) throws SQLException {
        if(connection != null) {
            connection.close();
        }
	}
	
	@Override
	public void establishDBConnection(FSPath dbDir, String dbName) throws Exception {
		throw new Exception("Cannot connect to memory-only database");
	}

	@Override
	public boolean supportsDateType() {
		return false;
	}

	@Override
	public CCDatabaseDriver getDBType() {
		return CCDatabaseDriver.INMEMORY;
	}

	@Override
	public boolean isInMemory() {return true;}

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