package de.jClipCorn.database.driver;

import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.filesystem.FSPath;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StubDatabase extends GenericDatabase {

	@Override
	public boolean createNewDatabase(FSPath xmlPath, FSPath dbDir, String dbName) {
		return true;
	}

	@Override
	public boolean createNewDatabasefromResourceXML(String xmlResPath, FSPath dbDir, String dbName) {
		return true;
	}

	@Override
	public boolean supportsDateType() {
		return true;
	}

	@Override
	public void establishDBConnection(FSPath dbDir, String dbName) throws Exception {
		// NOP
	}

	@Override
	public void closeDBConnection(FSPath dbDir, String dbName, boolean cleanshutdown) throws SQLException {
		// NOP
	}

	@Override
	public boolean databaseExists(FSPath dbDir, String dbName) {
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
