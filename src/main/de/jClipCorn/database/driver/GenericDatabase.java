package de.jClipCorn.database.driver;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.sqlwrapper.StatementType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("nls")
public abstract class GenericDatabase implements PublicDatabaseInterface {
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

	public abstract boolean createNewDatabase(FSPath dbDir, String dbName);

	public abstract boolean supportsDateType();

	public abstract void establishDBConnection(FSPath dbDir, String dbName) throws Exception;
	
	public abstract void closeDBConnection(FSPath dbDir, String dbName, boolean cleanshutdown) throws SQLException;

	public abstract boolean databaseExists(FSPath dbDir, String dbName);

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

	public int getRowCount(String table){
		return querySingleIntSQL("SELECT COUNT(*) FROM " + table, 0);
	}

	public boolean executeSQL(String sql) {
		try {
			Statement s = connection.createStatement();
			CCLog.addSQL("ExecuteSQL", StatementType.CUSTOM, sql);
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
		CCLog.addSQL("ExecuteSQLThrow", StatementType.CUSTOM, sql);
		s.execute(sql);
		s.close();
	}

	@Override
	public List<Object[]> querySQL(String sql, int columnCount) throws SQLException {
		Statement s = connection.createStatement();
		CCLog.addSQL("QuerySQL", StatementType.CUSTOM, sql);
		ResultSet rs = s.executeQuery(sql);

		List<Object[]> result = new ArrayList<>();
		while (rs.next()) {
			Object[] o = new Object[columnCount];
			for (int i = 0; i < columnCount; i++) o[i] = rs.getObject(i+1);
			result.add(o);
		}
		rs.close();
		s.close();

		return result;
	}

	@Override
	public <T> List<T> querySQL(String sql, int columnCount, Func1to1<Object[], T> conv) throws SQLException {
		Statement s = connection.createStatement();
		CCLog.addSQL("QuerySQL", StatementType.CUSTOM, sql);
		ResultSet rs = s.executeQuery(sql);

		List<T> result = new ArrayList<>();
		while (rs.next()) {
			Object[] o = new Object[columnCount];
			for (int i = 0; i < columnCount; i++) o[i] = rs.getObject(i+1);
			result.add(conv.invoke(o));
		}
		rs.close();
		s.close();

		return result;
	}
	
	public Object querySingleSQL(String sql, int columnIndex) {
		try {
			Statement s = connection.createStatement();
			CCLog.addSQL("QuerySingleSQL", StatementType.CUSTOM, sql);
			ResultSet rs = s.executeQuery(sql);
			
			Object ret = null;
			if (rs.next()) {
				ret = rs.getObject(columnIndex + 1);
			}
			rs.close();
			s.close();
			
			return ret;
		} catch (SQLException e) {
			return null;
		}
	}

	@Override
	public Object querySingleSQLThrow(String sql, int columnIndex) throws SQLException {
		Statement s = connection.createStatement();
		CCLog.addSQL("QuerySingleSQLThrow", StatementType.CUSTOM, sql);
		ResultSet rs = s.executeQuery(sql);
		
		Object ret = null;
		if (rs.next()) {
			ret = rs.getObject(columnIndex + 1);
		}
		rs.close();
		s.close();
		
		return ret;
	}

	public int querySingleIntSQL(String sql, int columnIndex) {
		return (Integer) querySingleSQL(sql, columnIndex);
	}

	@Override
	public int querySingleIntSQLThrow(String sql, int columnIndex) throws SQLException {
		return (Integer) querySingleSQLThrow(sql, columnIndex);
	}

	public String querySingleStringSQL(String sql, int columnIndex) {
		return (String) querySingleSQL(sql, columnIndex);
	}

	@Override
	public String querySingleStringSQLThrow(String sql, int columnIndex) throws SQLException {
		return (String) querySingleSQLThrow(sql, columnIndex);
	}

	public List<Object> getSingleRow(String table, int row, String orderColumn, boolean ascend) {
		List<Object> result = new ArrayList<>();
		int actRow = -1;
		String sql = "SELECT * FROM " + table + " ORDER BY " + orderColumn + ((ascend)?" ASC":" DESC");
		
		try {
			Statement s = connection.createStatement();
			CCLog.addSQL("GetSingleRow", StatementType.CUSTOM, sql);
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

	public boolean isConnected() {
		return connection != null;
	}

	public Exception getLastError() {
		return lastError;
	}

	public Statement createStatement() throws SQLException {
		return connection.createStatement();
	}

	public PreparedStatement createPreparedStatement(String sql) throws SQLException {
		try {
			return connection.prepareStatement(sql);
		} catch (SQLException e) {
			CCLog.addDebug("Cannot prepare statement: '"+sql + "' cause of " + e.getMessage());
			
			throw e;
		}
	}

	public Statement createReadOnlyStatement() throws SQLException {
		return connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}

	public void clearTable(String table) {
		if (! executeSQL(String.format("TRUNCATE TABLE %s", table))) {
			CCLog.addError(getLastError());
		}
	}

	public String getDBTypeName()
	{
		return getDBType().asString();
	}

	public abstract CCDatabaseDriver getDBType();

	public abstract boolean isInMemory();

	public abstract List<String> listTables() throws SQLException;
	public abstract List<String> listTrigger() throws SQLException;
	public abstract List<Tuple<String, String>> listTriggerWithStatements() throws SQLException;
	public abstract List<String> listViews() throws SQLException;
}

