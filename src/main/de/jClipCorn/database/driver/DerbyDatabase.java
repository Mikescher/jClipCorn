package de.jClipCorn.database.driver;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.datatypes.Tuple;
import org.apache.commons.lang.NotImplementedException;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Database;

import java.io.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("nls")
public class DerbyDatabase extends GenericDatabase {
	
    public static final OutputStream DERBY_OUT = new OutputStream() {
        @Override
		public void write(int b) {
        	//IGNORE
        }
    };
	
	private final static String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	private final static String PROTOCOL = "jdbc:derby:";
	private final static String DB_NAME = "derby";

	private String database_username = "default";
	private String database_password = "";
	
	private Database structure; // XML-Template ...

	private final boolean _readonly;

	public DerbyDatabase(boolean ro) {
		_readonly = ro;
	}

	//@Override
	public String getDatabasePath(String dbPath) {
		return PROTOCOL + dbPath;
	}
	
	@Override
	public boolean createNewDatabase(String xmlPath, String dbPath) {
		try {
			parseXML(xmlPath);
			
			Platform platform = PlatformFactory.createNewPlatformInstance(DB_NAME);
			
			platform.createDatabase(DRIVER, getDatabasePath(dbPath), database_username, database_password, getUserPasswordProperties());
			
			establishDBConnection(dbPath);
			
			createTables(DB_NAME);
		} catch (Exception e) {
			lastError = e;
			return false;
		}
		return true;
	}

	@Override
	public boolean createNewDatabasefromResourceXML(String xmlResPath, String dbPath) {
		try {
			parseXMLfromResource(xmlResPath);
			
			Platform platform = PlatformFactory.createNewPlatformInstance(DB_NAME);
			
			platform.createDatabase(DRIVER, getDatabasePath(dbPath), database_username, database_password, getUserPasswordProperties());
			
			establishDBConnection(dbPath);
			
			createTables(DB_NAME);
		} catch (Exception e) {
			lastError = e;
			return false;
		}
		return true;
	}
	
	@Override
	public boolean databaseExists(String dbPath) {
		File f = new File(dbPath);
		return f.exists() && f.isDirectory();
	}
	
	@Override
	public void closeDBConnection(String dbPath, boolean cleanshutdown) throws SQLException {
		connection.close();

		try {
			if (cleanshutdown) {
				DriverManager.getConnection(PROTOCOL + ";shutdown=true");
			}
		} catch (SQLException e) {
			CCLog.addInformation(e);
			// This Exception is thrown when everything is ok, madness - i konw
			// Madness ???
			// This is DERBY
		}
		
		connection = null;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void establishDBConnection(String dbPath) throws SQLException {
		try {
			Class.forName(DRIVER).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			CCLog.addError(e);
		}
				
		connection = DriverManager.getConnection(getDatabasePath(dbPath), getUserPasswordProperties());
		
		connection.setAutoCommit(true);
		connection.setReadOnly(true);
	}
	
	/**
	 * Creates a Structure defined in the global variable structure in the Database
	 * 
	 * @param dbName Name of the Database
	 * @throws Exception Throws Exception if Tables couldnt be created
	 */
	protected void createTables(String dbName) throws Exception {
		Platform platform = PlatformFactory.createNewPlatformInstance(dbName);
		platform.createTables(connection, structure, false, true);
	}
	
	/**
	 * parses the structure from an XML-File
	 * @param xmlPath Path to the descriptive XML-File
	 */
	protected void parseXML(String xmlPath) {
		structure = new DatabaseIO().read(xmlPath);
	}
	
	/**
	 * parses the structure from an XML-Resource
	 * @param xmlResPath Path to the descriptive XML-Resource
	 */
	protected boolean parseXMLfromResource(String xmlResPath) {
		InputStream is = this.getClass().getResourceAsStream(xmlResPath);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader bf = new BufferedReader(isr);
		structure = new DatabaseIO().read( bf );
		try {
			bf.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return returns a Properties Object with Username an Password-Field in it
	 */
	private Properties getUserPasswordProperties() {
		Properties props = new Properties();
		props.put("user", database_username);
		props.put("password", database_password);
		return props;
	}

	@Override
	public boolean supportsDateType() {
		return false;
	}

	@Override
	public CCDatabaseDriver getDBType() {
		return CCDatabaseDriver.DERBY;
	}

	@Override
	public boolean isInMemory() {return false;}

	@Override
	public List<String> listTables() throws SQLException {
		return querySQL("select st.tablename from sys.systables st LEFT OUTER join sys.sysschemas ss on (st.schemaid = ss.schemaid) where (ss.schemaname = 'APP' OR ss.schemaname = 'DEFAULT') AND st.tabletype='T'", 1, a -> (String)a[0]);
	}

	@Override
	public List<String> listTrigger() throws SQLException {
		return querySQL("select triggername from sys.systriggers", 1, a -> (String)a[0]);
	}

	@Override
	public List<String> listViews() throws SQLException {
		return querySQL("select st.tablename from sys.systables st LEFT OUTER join sys.sysschemas ss on (st.schemaid = ss.schemaid) where (ss.schemaname = 'APP' OR ss.schemaname = 'DEFAULT') AND st.tabletype='V'", 1, a -> (String)a[0]);
	}

	@Override
	public List<Tuple<String, String>> listTriggerWithStatements() throws SQLException {
		throw new NotImplementedException();
	}
}
