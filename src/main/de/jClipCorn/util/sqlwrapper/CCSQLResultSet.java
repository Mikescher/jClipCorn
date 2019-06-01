package de.jClipCorn.util.sqlwrapper;

import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.DateFormatException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CCSQLResultSet {
	private final ResultSet _data;
	private final CCSQLStatement _statement;
	private final CCDatabase _database;

	public CCSQLResultSet(CCDatabase db, ResultSet d, CCSQLStatement stmt) {
		_data      = d;
		_statement = stmt;
		_database  = db;
	}

	public boolean next() throws SQLException {
		return _data.next();
	}

	public int getIntDirect(int i) throws SQLException {
		return _data.getInt(i);
	}

	public String getStringDirect(int i) throws SQLException {
		return _data.getString(i);
	}

	public int getInt(CCSQLColDef col) throws SQLWrapperException, SQLException {
		Tuple<CCSQLType, Integer> idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col+"] not found in CCSQLStatement");
		if (!idx.Item1.isCallableAsInteger()) throw new SQLWrapperException("Field ["+col+"] has wrong type");

		return _data.getInt(idx.Item2);
	}

	public String getString(CCSQLColDef col) throws SQLWrapperException, SQLException {
		Tuple<CCSQLType, Integer> idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col+"] not found in CCSQLStatement");
		if (!idx.Item1.isCallableAsString()) throw new SQLWrapperException("Field ["+col+"] has wrong type");

		return _data.getString(idx.Item2);
	}

	public boolean getBoolean(CCSQLColDef col) throws SQLWrapperException, SQLException {
		Tuple<CCSQLType, Integer> idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col+"] not found in CCSQLStatement");
		if (!idx.Item1.isCallableAsBoolean()) throw new SQLWrapperException("Field ["+col+"] has wrong type");

		return _data.getBoolean(idx.Item2);
	}

	public long getLong(CCSQLColDef col) throws SQLWrapperException, SQLException {
		Tuple<CCSQLType, Integer> idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col+"] not found in CCSQLStatement");
		if (!idx.Item1.isCallableAsLong()) throw new SQLWrapperException("Field ["+col+"] has wrong type");

		return _data.getLong(idx.Item2);
	}

	public short getShort(CCSQLColDef col) throws SQLWrapperException, SQLException {
		Tuple<CCSQLType, Integer> idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col+"] not found in CCSQLStatement");
		if (!idx.Item1.isCallableAsShort()) throw new SQLWrapperException("Field ["+col+"] has wrong type");

		return _data.getShort(idx.Item2);
	}

	public CCDate getDate(CCSQLColDef col) throws SQLWrapperException, SQLException, DateFormatException {
		Tuple<CCSQLType, Integer> idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col+"] not found in CCSQLStatement");
		if (!idx.Item1.isCallableAsDate()) throw new SQLWrapperException("Field ["+col+"] has wrong type");

		if (_database.supportsDateType()) {
			return CCDate.create(_data.getDate(idx.Item2));
		} else {
			return CCDate.createFromSQL(_data.getString(idx.Item2));
		}
	}

	public void close() throws SQLException {
		_data.close();
	}
}
