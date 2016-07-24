package de.jClipCorn.database;

import java.sql.SQLException;

public class StubDatabase extends GenericDatabase {

	@Override
	public boolean createNewDatabase(String xmlPath, String dbPath) {
		return true;
	}

	@Override
	public boolean createNewDatabasefromResourceXML(String xmlResPath, String dbPath) {
		return true;
	}

	@Override
	public boolean supportsDateType() {
		return true;
	}

	@Override
	public void establishDBConnection(String dbPath) throws Exception {
		// NOP
	}

	@Override
	public void closeDBConnection(String dbPath, boolean cleanshutdown) throws SQLException {
		// NOP
	}

	@Override
	public boolean databaseExists(String dbPath) {
		return true;
	}

	@Override
	public String GetDBTypeName() {
		return "__STUB__"; //$NON-NLS-1$
	}

}