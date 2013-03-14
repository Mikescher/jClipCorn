package de.jClipCorn.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Database;

import de.jClipCorn.gui.log.CCLog;

/**
 * A little Wrapper for embedded JavaDB (derby) Databases
 * 
 * @author Mike Schwörer
 *
 */
@SuppressWarnings("nls")
public class DerbyDatabase {
	public final static String TABLETYPE_TABLE = "TABLE";
	public final static String TABLETYPE_SYNONYM = "SYNONYM";
	public final static String TABLETYPE_VIEW = "VIEW";
	public final static String TABLETYPE_SYSTEM_TABLE = "SYSTEM TABLE";
	public final static String TABLETYPE_GLOBAL_TEMPORARY = "GLOBAL TEMPORARY";
	public final static String TABLETYPE_LOCAL_TEMPORARY = "LOCAL TEMPORARY";
	public final static String TABLETYPE_ALIAS = "ALIAS";
	
	public static final int FIELDTYPE_BIT = -7;
	public static final int FIELDTYPE_TINYINT = -6;
	public static final int FIELDTYPE_SMALLINT = 5;
	public static final int FIELDTYPE_INTEGER = 4;
	public static final int FIELDTYPE_BIGINT = -5;
	public static final int FIELDTYPE_FLOAT = 6;
	public static final int FIELDTYPE_REAL = 7;
	public static final int FIELDTYPE_DOUBLE = 8;
	public static final int FIELDTYPE_NUMERIC = 2;
	public static final int FIELDTYPE_DECIMAL = 3;
	public static final int FIELDTYPE_CHAR = 1;
	public static final int FIELDTYPE_VARCHAR = 12;
	public static final int FIELDTYPE_LONGVARCHAR = -1;
	public static final int FIELDTYPE_DATE = 91;
	public static final int FIELDTYPE_TIME = 92;
	public static final int FIELDTYPE_TIMESTAMP = 93;
	public static final int FIELDTYPE_BINARY = -2;
	public static final int FIELDTYPE_VARBINARY = -3;
	public static final int FIELDTYPE_LONGVARBINARY = -4;
	public static final int FIELDTYPE_NULL = 0;
	public static final int FIELDTYPE_BOOLEAN = 16;
	
	private final static String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	private final static String PROTOCOL = "jdbc:derby:";
	private final static String DB_NAME = "derby";
	
    public static final OutputStream DERBY_OUT = new OutputStream() {
        @Override
		public void write(int b) {
        	//IGNORE
        }
    };
	
	protected Exception lastError = null;

	private String database_username = "default";
	private String database_password = "";
	
	private Database structure; // XML-Template ...
	private Connection connection = null; // Database

	/**
	 * creates a new Instance of DerbyDatabase
	 */
	public DerbyDatabase() {

	}

	/**
	 * Creates a new Database
	 * 
	 * @param xmlPath Path to the descriptive XML-File
	 * @param dbPath Path to the Database-Folder
	 * @param dbName Name of the Database
	 * @return true if Database was successfull created
	 */
	public boolean createNewDatabase(String xmlPath, String dbPath) {
		try {
			parseXML(xmlPath);
			
			Platform platform = PlatformFactory.createNewPlatformInstance(DB_NAME);
			
			platform.createDatabase(DRIVER, PROTOCOL + dbPath, database_username, database_password, getUserPasswordProperties());
			
			establishDBConnection(dbPath);
			
			createTables(DB_NAME);
		} catch (Exception e) {
			lastError = e;
			return false;
		}
		return true;
	}
	
	/**
	 * Creates a new Database
	 * 
	 * @param xmlPath Path to the descriptive XML-File
	 * @param dbPath Path to the Database-Folder
	 * @param dbName Name of the Database
	 * @return true if Database was successfull created
	 */
	public boolean createNewDatabasefromResourceXML(String xmlResPath, String dbPath) {
		try {
			parseXMLfromResource(xmlResPath);
			
			Platform platform = PlatformFactory.createNewPlatformInstance(DB_NAME);
			
			platform.createDatabase(DRIVER, PROTOCOL + dbPath, database_username, database_password, getUserPasswordProperties());
			
			establishDBConnection(dbPath);
			
			createTables(DB_NAME);
		} catch (Exception e) {
			lastError = e;
			return false;
		}
		return true;
	}
	
	/**
	 * Connects to an existing Database
	 * 
	 * @param dbPath Path to the Database-Folder
	 * @throws Exception Throw an Exception if the Connection couldn't be established
	 */
	public void establishDBConnection(String dbPath) throws SQLException {
		//Class.forName(DRIVER).newInstance();
				
		connection = DriverManager.getConnection(PROTOCOL + dbPath, getUserPasswordProperties());
		
		connection.setAutoCommit(true);
	}
	
	/**
	 * Checks if a Database exists
	 * 
	 * @param dbPath  Path to the Database-Folder
	 * @return true if the Database exists
	 */
	public boolean databaseExists(String dbPath) {
		try {
			DriverManager.getConnection(PROTOCOL + dbPath, getUserPasswordProperties());
			return true;
		} catch (SQLException e) {
			return false;
		}
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

	/**
	 * Returns a list of all Tables in the Database
	 * 
	 * @param type Filters Databses by Type - NULL for no Filter
	 * @return List of all Tables or NULL if not connected
	 * @throws SQLException Throws an Exception if getTables fails
	 */
	public ArrayList<String> getAllTables(String type) throws SQLException {
		ArrayList<String> result = new ArrayList<String>();
		
		String s[];
		if (type != null) {
			s = new String[1];
			s[0] = type;
		} else {
			s = null;
		}
		
		if (isConnected()) {
			DatabaseMetaData md = connection.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", s);
			while (rs.next()) {
				result.add(rs.getString(3));
			}
			return result;
		} else {
			return null;
		}
	}
	
	/**
	 * Lists all Columns in a table
	 * 
	 * @param table Name of the Table
	 * @return an AraryList of All Column-Names
	 * @throws SQLException Throws Exception if Table is non existent
	 */
	public ArrayList<String> getColumns(String table) throws SQLException {
		ArrayList<String> result = new ArrayList<String>();
		
		if (isConnected()) {
			DatabaseMetaData md = connection.getMetaData();
			ResultSet rs = md.getColumns(null, null, table, "%");
			while (rs.next()) {
				result.add(rs.getString(4));
			}
			return result;
		} else {
			return null;
		}
	}
	
	/**
	 * Lists all the types of all Columns in a table
	 * 
	 * @param table Name of the Table
	 * @return an AraryList of All Column-Types
	 * @throws SQLException Throws Exception if Table is non existent
	 */
	public ArrayList<Integer> getColumnTypes(String table) throws SQLException {
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		if (isConnected()) {
			DatabaseMetaData md = connection.getMetaData();
			ResultSet rs = md.getColumns(null, null, table, "%");
			while (rs.next()) {
				result.add(rs.getInt(5));
			}
			return result;
		} else {
			return null;
		}
	}
	
	/**
	 * Ads an New Empty Row to the Table
	 * 
	 * @param table The table which should get altered
	 * @throws SQLException Throws an exception if the table is non-existent
	 */
	public void addEmptyRow(String table) throws Exception {
		String sql = "INSERT INTO " + table + " VALUES (";
		ArrayList<Integer> s = getColumnTypes(table);
		
		for (int i = 0; i < s.size(); i++) {
			if (s.get(i) == FIELDTYPE_VARCHAR || s.get(i) == FIELDTYPE_LONGVARCHAR) {
				sql += "''";
			} else {
				sql += "0";
			}
			if (i+1 < s.size()) {
				sql += ", ";
			}
		}
		sql += ")";
		if (! executeSQL(sql)) {
			throw getLastError();
		}
	}
	
	/**
	 * Returns the Count of Columns of a specific table
	 * 
	 * @param table Name of the Table
	 * @return Column Count ofthe Table
	 * @throws SQLException Throws Exception if Table is non existent
	 */
	public int getColumnCount(String table) throws SQLException {
		int c = 0;
		if (isConnected()) {
			DatabaseMetaData md = connection.getMetaData();
			ResultSet rs = md.getColumns(null, null, table, "%");
			while (rs.next()) {
				c++;
			}
			return c;
		} else {
			return -1;
		}
	}
	
	/**
	 * Counts the Rows of a specific table
	 * 
	 * @param table Name of the Table
	 * @return the row count of teh table
	 */
	public int getRowCount(String table){
		int count = querySingleIntSQL("SELECT COUNT(*) FROM " + table, 0);
		return count;
	}
	
	/**
	 * Executes an SQL-Statement without return-Value
	 * 
	 * @param sql The SQL-Statement
	 */
	public boolean executeSQL(String sql) {
		Statement s;
		try {
			s = connection.createStatement();
			s.execute(sql);
			s.close();
			return true;
		} catch (SQLException e) {
			lastError = e;
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Executes an SQL-Statement and returns a single Value frm the Resultset
	 * 
	 * @param sql The SQL-Statement
	 * @param column the specific Column for the return Value
	 * @return The return Value
	 */
	public Object querySingleSQL(String sql, int column) {
		Object ret;
		Statement s;
		try {
			s = connection.createStatement();
			ResultSet rs = s.executeQuery(sql);
			if (rs.next()) {
				ret = rs.getObject(column + 1);
			} else {
				ret = null;
			}
			s.close();
			return ret;
		} catch (SQLException e) {
			return null;
		}
	}
	
	/**
	 * Executes an SQL-Statement and returns a single Integer-Value frm the Resultset
	 * 
	 * @param sql The SQL-Statement
	 * @param column the specific Column for the return Value
	 * @return The return Value
	 */
	public int querySingleIntSQL(String sql, int column) {
		return (Integer) querySingleSQL(sql, column);
	}
	
	/**
	 * Executes an SQL-Statement and returns a single String-Value frm the Resultset
	 * 
	 * @param sql The SQL-Statement
	 * @param column the specific Column for the return Value
	 * @return The return Value
	 */
	public String querySingleStringSQL(String sql, int column) {
		return (String) querySingleSQL(sql, column);
	}
	
	/**
	 * returns a Single Row identified by a Row.ID
	 * 
	 * @param table the Table for the result
	 * @param row the row-number
	 * @param orderColumn the column that is used to order the table
	 * @param ascend true if the table is sorted ASCEND
	 * @return an ArrayList of the Row
	 */
	public ArrayList<Object> getSingleRow(String table, int row, String orderColumn, boolean ascend) {
		ArrayList<Object> result = new ArrayList<Object>();
		int actRow = -1;
		String sql = "SELECT * FROM " + table + " ORDER BY " + orderColumn + ((ascend)?" ASC":" DESC");
		
		try {
			Statement s = connection.createStatement();
			ResultSet rs = s.executeQuery(sql);
			
			while(actRow < row) {
				if (! rs.next()) {
					return null;
				}
				actRow++;
			}
			
			int colCount = rs.getMetaData().getColumnCount();
			for(int i = 0; i < colCount; i++) {
				result.add(rs.getObject(i+1));
			}
			s.close();
			return result;
		} catch (SQLException e) {
			return null;
		}
	}
	
	/**
	 * Changes one field of a table based on its row-id
	 * 
	 * @param table table in which the change occurs
	 * @param row the row to change
	 * @param orderColumn the column to order
	 * @param ascend ascend or descend order
	 * @param changedColumm the column to change
	 * @param newVal the new Value
	 * @return false on error
	 */
	public boolean alterTableRowField(String table, int row, String orderColumn, boolean ascend, String changedColumm, Object newVal) {
		ArrayList<Object> comprow = getSingleRow(table, row, orderColumn, ascend);
		String sql;
		
		if(newVal instanceof String) {
			sql = String.format("UPDATE %s SET %s='%s' WHERE", table, changedColumm, newVal);
		} else {
			sql = String.format("UPDATE %s SET %s=%s WHERE", table, changedColumm, newVal);
		}
		
		for(int i = 0; i < comprow.size(); i++) {
			if (comprow.get(i) instanceof String) {
				try {
					sql += String.format(" %s='%s'", getColumns(table).get(i), comprow.get(i));
				} catch (SQLException e) {
					CCLog.addError(e);
					return false;
				}
			} else {
				try {
					sql += String.format(" %s=%s", getColumns(table).get(i), comprow.get(i));
				} catch (SQLException e) {
					CCLog.addError(e);
					return false;
				}
			}
			if (i+1 < comprow.size()) {
				sql += " AND";
			}
		}
		
		if (! executeSQL(sql)) {
			CCLog.addError(getLastError());
			return false;
		}
		
		return true;
	}
	
	/**
	 * deletes a complete row from a table
	 * 
	 * @param table table in which the change occurs
	 * @param row the row to delete
	 * @param orderColumn the COlumn to order the Table
	 * @param ascend ascend or descend order
	 * @return false on error
	 */
	public boolean deleteTableRow(String table, int row, String orderColumn, boolean ascend) {
		ArrayList<Object> comprow = getSingleRow(table, row, orderColumn, ascend);
		ArrayList<Integer> types;
		try {
			types = getColumnTypes(table);
		} catch (SQLException e1) {
			return false;
		}
		
		String sql;

		sql = String.format("DELETE FROM %s WHERE", table);

		for (int i = 0; i < comprow.size(); i++) {
			if (types.get(i) == FIELDTYPE_LONGVARCHAR || types.get(i) == FIELDTYPE_VARCHAR || types.get(i) == FIELDTYPE_DATE) {
				try {
					sql += String.format(" %s='%s'", getColumns(table).get(i), comprow.get(i));
				} catch (SQLException e) {
					CCLog.addError(e);
					return false;
				}
			} else {
				try {
					sql += String.format(" %s=%s", getColumns(table).get(i), comprow.get(i));
				} catch (SQLException e) {
					CCLog.addError(e);
					return false;
				}
			}
			if (i + 1 < comprow.size()) {
				sql += " AND";
			}
		}

		if (! executeSQL(sql)) {
			CCLog.addError(getLastError());
			return false;
		}

		return true;
	}
	
	/**
	 * Creates a Structure defined in the global variable structure in the Database
	 * 
	 * @param dbName Name of the Database
	 * @throws Exception Throws Exception if Tables couldnt be created
	 */
	private void createTables(String dbName) throws Exception {
		Platform platform = PlatformFactory.createNewPlatformInstance(dbName);
		platform.createTables(connection, structure, false, true);
	}
	
	/**
	 * parses the structure from an XML-File
	 * @param xmlPath Path to the descriptive XML-File
	 */
	private void parseXML(String xmlPath) {
		structure = new DatabaseIO().read(xmlPath);
	}
	
	/**
	 * parses the structure from an XML-Resource
	 * @param xmlPath Path to the descriptive XML-Resource
	 */
	private void parseXMLfromResource(String xmlResPath) {
		InputStream is = this.getClass().getResourceAsStream(xmlResPath);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader bf = new BufferedReader(isr);
		structure = new DatabaseIO().read( bf );
	}

	/**
	 * @return true if Databse is connected
	 */
	public boolean isConnected() {
		return connection != null;
	}

	/**
	 * @return the Username for the current Database
	 */
	public String getDatabase_username() {
		return database_username;
	}

	/**
	 * Sets the Username for the current Database
	 * 
	 * @param database_username new Username
	 */
	public void setDatabase_username(String database_username) {
		this.database_username = database_username;
	}

	/**
	 * @return the Username for the current Database
	 */
	public String getDatabase_password() {
		return database_password;
	}

	/**
	 *  Sets the Password for the current Database
	 *  
	 * @param database_password new Password
	 */
	public void setDatabase_password(String database_password) {
		this.database_password = database_password;
	}
	
	/**
	 * @return returns the last Error
	 */
	public Exception getLastError() {
		return lastError;
	}
	
	/**
	 * @return returns a new Statement for SQL-Exekution
	 * @throws SQLException
	 */
	public Statement createStatement() throws SQLException {
		return connection.createStatement();
	}
	
	/**
	 * @return returns a new prepared Statement for SQL-Exekution
	 * @throws SQLException
	 */
	public PreparedStatement createPreparedStatement(String sql) throws SQLException {
		return connection.prepareStatement(sql);
	}
	
	/**
	 * @return an new read-Only and scrollable Statement for SQL-Exekution
	 * @throws SQLException
	 */
	public Statement createReadOnlyStatement() throws SQLException {
		return connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}
	
	/**
	 * Deletes ALL content from a specific table
	 * 
	 * @param table the table to delete
	 */
	public void clearTable(String table) {
		if (! executeSQL(String.format("TRUNCATE TABLE %s", table))) {
			CCLog.addError(getLastError());
		}
	}
}

