package de.jClipCorn.database.driver;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.gui.log.CCLog;

/**
 * A little Wrapper for embedded JavaDB (derby) Databases
 * 
 * @author Mike Schwörer
 *
 */
@SuppressWarnings("nls")
public abstract class GenericDatabase {
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
	
	protected Exception lastError = null;
	
	protected Connection connection = null; // Database

	/**
	 * creates a new Instance of DerbyDatabase
	 */
	public GenericDatabase() {

	}

	/**
	 * Creates a new Database
	 * 
	 * @param xmlPath Path to the descriptive XML-File
	 * @param dbPath Path to the Database-Folder
	 * @param dbName Name of the Database
	 * @return true if Database was successfull created
	 */
	public abstract boolean createNewDatabase(String xmlPath, String dbPath);
	
	/**
	 * Creates a new Database
	 * 
	 * @param xmlPath Path to the descriptive XML-File
	 * @param dbPath Path to the Database-Folder
	 * @param dbName Name of the Database
	 * @return true if Database was successfull created
	 */
	public abstract boolean createNewDatabasefromResourceXML(String xmlResPath, String dbPath);
	
	/**
	 * @return true if the database has a native DATE type
	 */
	public abstract boolean supportsDateType();
	
	/**
	 * Connects to an existing Database
	 * 
	 * @param dbPath Path to the Database-Folder
	 * @throws Exception Throw an Exception if the Connection couldn't be established
	 */
	public abstract void establishDBConnection(String dbPath) throws Exception;
	
	public abstract void closeDBConnection(String dbPath, boolean cleanshutdown) throws SQLException;
	
	/**
	 * Checks if a Database exists
	 * 
	 * @param dbPath  Path to the Database-Folder
	 * @return true if the Database exists
	 */
	public abstract boolean databaseExists(String dbPath);

	/**
	 * Returns a list of all Tables in the Database
	 * 
	 * @param type Filters Databses by Type - NULL for no Filter
	 * @return List of all Tables or NULL if not connected
	 * @throws SQLException Throws an Exception if getTables fails
	 */
	public List<String> getAllTables(String type) throws SQLException {
		List<String> result = new ArrayList<>();
		
		if (isConnected()) {
			String s[] = null;
			if (type != null) {
				s = new String[1];
				s[0] = type;
			}
			
			DatabaseMetaData md = connection.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", s);
			while (rs.next()) {
				result.add(rs.getString(3));
			}
			rs.close();
			
			return result;
		} else {
			return null;
		}
	}

	public void setLastError(Exception e) {
		lastError = e;
	}
	
	/**
	 * Lists all Columns in a table
	 * 
	 * @param table Name of the Table
	 * @return an AraryList of All Column-Names
	 * @throws SQLException Throws Exception if Table is non existent
	 */
	public List<String> getColumns(String table) throws SQLException {
		List<String> result = new ArrayList<>();
		
		if (isConnected()) {
			DatabaseMetaData md = connection.getMetaData();
			ResultSet rs = md.getColumns(null, null, table, "%");
			while (rs.next()) {
				result.add(rs.getString(4));
			}
			rs.close();
			
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
	public List<Integer> getColumnTypes(String table) throws SQLException {
		ArrayList<Integer> result = new ArrayList<>();
		
		if (isConnected()) {
			DatabaseMetaData md = connection.getMetaData();
			ResultSet rs = md.getColumns(null, null, table, "%");
			while (rs.next()) {
				result.add(rs.getInt(5));
			}
			rs.close();
			
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
		StringBuilder sqlbuilder = new StringBuilder();
		
		sqlbuilder.append("INSERT INTO ");
		sqlbuilder.append(table);
		sqlbuilder.append(" VALUES (");
		
		List<Integer> s = getColumnTypes(table);
		
		for (int i = 0; i < s.size(); i++) {
			if (s.get(i) == FIELDTYPE_VARCHAR || s.get(i) == FIELDTYPE_LONGVARCHAR) {
				sqlbuilder.append("''");
			} else {
				sqlbuilder.append("0");
			}
			if (i+1 < s.size()) {
				sqlbuilder.append(", ");
			}
		}
		sqlbuilder.append(")");
		if (! executeSQL(sqlbuilder.toString())) {
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
			rs.close();
			
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
		try {
			Statement s = connection.createStatement();
			s.execute(sql);
			s.close();
			return true;
		} catch (SQLException e) {
			lastError = e;
			CCLog.addError(e);
			return false;
		}
	}

	public void executeSQLThrow(String sql) throws SQLException {
		Statement s = connection.createStatement();
		s.execute(sql);
		s.close();
	}
	
	/**
	 * Executes an SQL-Statement and returns a single Value frm the Resultset
	 * 
	 * @param sql The SQL-Statement
	 * @param column the specific Column for the return Value
	 * @return The return Value
	 */
	public Object querySingleSQL(String sql, int column) {
		try {
			Statement s = connection.createStatement();
			ResultSet rs = s.executeQuery(sql);
			
			Object ret = null;
			if (rs.next()) {
				ret = rs.getObject(column + 1);
			}
			rs.close();
			s.close();
			
			return ret;
		} catch (SQLException e) {
			return null;
		}
	}
	
	/**
	 * Executes an SQL-Statement and returns a single Value frm the Resultset
	 * 
	 * @param sql The SQL-Statement
	 * @param column the specific Column for the return Value
	 * @return The return Value
	 * @throws SQLException 
	 */
	public Object querySingleSQLThrow(String sql, int column) throws SQLException {
		Statement s = connection.createStatement();
		ResultSet rs = s.executeQuery(sql);
		
		Object ret = null;
		if (rs.next()) {
			ret = rs.getObject(column + 1);
		}
		rs.close();
		s.close();
		
		return ret;
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
	 * Executes an SQL-Statement and returns a single String-Value frm the Resultset
	 * 
	 * @param sql The SQL-Statement
	 * @param column the specific Column for the return Value
	 * @return The return Value
	 */
	public String querySingleStringSQLThrow(String sql, int column) throws SQLException {
		return (String) querySingleSQLThrow(sql, column);
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
	public List<Object> getSingleRow(String table, int row, String orderColumn, boolean ascend) {
		List<Object> result = new ArrayList<>();
		int actRow = -1;
		String sql = "SELECT * FROM " + table + " ORDER BY " + orderColumn + ((ascend)?" ASC":" DESC");
		
		try {
			Statement s = connection.createStatement();
			ResultSet rs = s.executeQuery(sql);
			
			while(actRow < row) {
				if (! rs.next()) {
					rs.close();
					s.close();
					return null;
				}
				actRow++;
			}
			
			int colCount = rs.getMetaData().getColumnCount();
			for(int i = 0; i < colCount; i++) {
				result.add(rs.getObject(i+1));
			}
			rs.close();
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
		List<Object> comprow = getSingleRow(table, row, orderColumn, ascend);
		
		StringBuilder sqlbuilder = new StringBuilder();
		
		if(newVal instanceof String) {
			sqlbuilder.append(String.format("UPDATE %s SET %s='%s' WHERE", table, changedColumm, newVal));
		} else {
			sqlbuilder.append(String.format("UPDATE %s SET %s=%s WHERE", table, changedColumm, newVal));
		}
		
		for(int i = 0; i < comprow.size(); i++) {
			if (comprow.get(i) instanceof String) {
				try {
					sqlbuilder.append(String.format(" %s='%s'", getColumns(table).get(i), comprow.get(i)));
				} catch (SQLException e) {
					CCLog.addError(e);
					return false;
				}
			} else {
				try {
					sqlbuilder.append(String.format(" %s=%s", getColumns(table).get(i), comprow.get(i)));
				} catch (SQLException e) {
					CCLog.addError(e);
					return false;
				}
			}
			if (i+1 < comprow.size()) {
				sqlbuilder.append(" AND");
			}
		}
		
		if (! executeSQL(sqlbuilder.toString())) {
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
		List<Object> comprow = getSingleRow(table, row, orderColumn, ascend);
		List<Integer> types;
		
		try {
			types = getColumnTypes(table);
		} catch (SQLException e1) {
			return false;
		}
		
		StringBuilder sqlbuilder = new StringBuilder();

		sqlbuilder.append(String.format("DELETE FROM %s WHERE", table));

		for (int i = 0; i < comprow.size(); i++) {
			if (types.get(i) == FIELDTYPE_LONGVARCHAR || types.get(i) == FIELDTYPE_VARCHAR || types.get(i) == FIELDTYPE_DATE) {
				try {
					sqlbuilder.append(String.format(" %s='%s'", getColumns(table).get(i), comprow.get(i)));
				} catch (SQLException e) {
					CCLog.addError(e);
					return false;
				}
			} else {
				try {
					sqlbuilder.append(String.format(" %s=%s", getColumns(table).get(i), comprow.get(i)));
				} catch (SQLException e) {
					CCLog.addError(e);
					return false;
				}
			}
			if (i + 1 < comprow.size()) {
				sqlbuilder.append(" AND");
			}
		}

		if (! executeSQL(sqlbuilder.toString())) {
			CCLog.addError(getLastError());
			return false;
		}

		return true;
	}

	/**
	 * @return true if Databse is connected
	 */
	public boolean isConnected() {
		return connection != null;
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
		try {
			return connection.prepareStatement(sql);
		} catch (SQLException e) {
			CCLog.addDebug("Cannot prepare statement: '"+sql + "' cause of " + e.getMessage());
			
			throw e;
		}
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

	public abstract String GetDBTypeName();

	public abstract boolean IsInMemory();
}

