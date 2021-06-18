package de.jClipCorn.database.driver;

import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.datatypes.Tuple;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StubDatabase extends GenericDatabase {

	@Override
	public boolean createNewDatabase(String xmlPath, String dbDir, String dbName) {
		return true;
	}

	@Override
	public boolean createNewDatabasefromResourceXML(String xmlResPath, String dbDir, String dbName) {
		return true;
	}

	@Override
	public boolean supportsDateType() {
		return true;
	}

	@Override
	public void establishDBConnection(String dbDir, String dbName) throws Exception {
		// NOP
	}

	@Override
	public void closeDBConnection(String dbDir, String dbName, boolean cleanshutdown) throws SQLException {
		// NOP
	}

	@Override
	public boolean databaseExists(String dbDir, String dbName) {
		return true;
	}

	@Override
	public CCDatabaseDriver getDBType() {
		return CCDatabaseDriver.STUB;
	}

	@Override
	public boolean isInMemory() {return true;}

	@Override
	public List<String> listTables() throws SQLException {
		return new ArrayList<>();
	}

	@Override
	public List<String> listTrigger() throws SQLException {
		return new ArrayList<>();
	}

	@Override
	public List<String> listViews() throws SQLException {
		return new ArrayList<>();
	}

	@Override
	public List<Tuple<String, String>> listTriggerWithStatements() throws SQLException {
		return new ArrayList<>();
	}
}
