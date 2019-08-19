package de.jClipCorn.database.driver;

import de.jClipCorn.util.lambda.Func1to1;

import java.sql.SQLException;
import java.util.List;

/// Read only
/// Only for special-case-usage (eg in db validator)
/// (!) Not for normal access
public interface PublicDatabaseInterface {
	List<Object[]> querySQL(String sql, int columnCount) throws SQLException;
	<T> List<T> querySQL(String sql, int columnCount, Func1to1<Object[], T> conv) throws SQLException;
	Object querySingleSQLThrow(String sql, int column) throws SQLException;
	int querySingleIntSQLThrow(String sql, int column) throws SQLException;
	String querySingleStringSQLThrow(String sql, int column) throws SQLException;
}
