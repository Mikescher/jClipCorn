package de.jClipCorn.database.driver;

import java.sql.DriverManager;
import java.sql.SQLException;

import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.gui.settings.CCDatabaseDriver;
import de.jClipCorn.util.helper.TextFileUtils;
import de.jClipCorn.util.parser.TurbineParser;

@SuppressWarnings("nls")
public class MemoryDatabase extends GenericDatabase {

	private final static String DRIVER = "org.sqlite.JDBC";
	private final static String PROTOCOL = "jdbc:sqlite::memory:";
	
	@Override
	public boolean createNewDatabase(String xmlPath, String dbPath) {
		try {
			Class.forName(DRIVER).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			CCLog.addError(e);
		}
		
		try {
			connection = DriverManager.getConnection(PROTOCOL);
			connection.setAutoCommit(true);

			TurbineParser turb = new TurbineParser(TextFileUtils.readUTF8TextFile(xmlPath), this);
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
		try {
			Class.forName(DRIVER).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			CCLog.addError(e);
		}
		
		try {
			connection = DriverManager.getConnection(PROTOCOL);
			connection.setAutoCommit(true);

			TurbineParser turb = new TurbineParser(TextFileUtils.readTextResource(xmlResPath, getClass()), this);
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
		return false;
	}
	
	@Override
	public void closeDBConnection(String dbPath, boolean cleanshutdown) throws SQLException {
        if(connection != null) {
            connection.close();
        }
	}
	
	@Override
	public void establishDBConnection(String dbPath) throws Exception {
		throw new Exception("Cannot connect to memory-only database");
	}

	@Override
	public boolean supportsDateType() {
		return false;
	}

	@Override
	public String GetDBTypeName() {
		return CCDatabaseDriver.INMEMORY.asString();
	}
}