package de.jClipCorn.database;

import java.sql.SQLException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class SQLiteDatabase extends GenericDatabase {
	@Override
	public boolean createNewDatabase(String xmlPath, String dbPath) {
		throw new NotImplementedException();
	}

	@Override
	public boolean createNewDatabasefromResourceXML(String xmlResPath, String dbPath) {
		throw new NotImplementedException();
	}
	
	@Override
	public boolean databaseExists(String dbPath) {
		throw new NotImplementedException();
	}
	
	@Override
	public void closeDBConnection(String dbPath, boolean cleanshutdown) throws SQLException {
		throw new NotImplementedException();
	}
	
	@Override
	public void establishDBConnection(String dbPath) throws SQLException {
		throw new NotImplementedException();
	}
}