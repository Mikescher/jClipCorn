package de.jClipCorn.util.sqlwrapper;

import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.DateFormatException;

import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("nls")
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
		var idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsInteger()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		return _data.getInt(idx);
	}

	public String getString(CCSQLColDef col) throws SQLWrapperException, SQLException {
		var idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsString()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		return _data.getString(idx);
	}

	public boolean getBoolean(CCSQLColDef col) throws SQLWrapperException, SQLException {
		var idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsBoolean()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		return _data.getBoolean(idx);
	}

	public long getLong(CCSQLColDef col) throws SQLWrapperException, SQLException {
		var idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsLong()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		return _data.getLong(idx);
	}

	public short getShort(CCSQLColDef col) throws SQLWrapperException, SQLException {
		var idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsShort()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		return _data.getShort(idx);
	}

	public CCDate getDate(CCSQLColDef col) throws SQLWrapperException, SQLException, DateFormatException {
		var idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsDate()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		if (_database.supportsDateType()) {
			return CCDate.create(_data.getDate(idx));
		} else {
			return CCDate.createFromSQL(_data.getString(idx));
		}
	}

	public byte[] getBlob(CCSQLColDef col) throws SQLException, SQLWrapperException {
		var idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsBlob()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		return _data.getBytes(idx);
	}

	public CCDateTime getDateTime(CCSQLColDef col) throws SQLWrapperException, SQLException, CCFormatException {
		var idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsString()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		return CCDateTime.createFromSQL(_data.getString(idx));
	}

	public double getFloat(CCSQLColDef col) throws SQLWrapperException, SQLException {
		var idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsFloat()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		return _data.getDouble(idx);
	}

	public Double getNullableFloat(CCSQLColDef col) throws SQLWrapperException, SQLException {
		var idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsFloat()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		double v = _data.getDouble(idx);
		if (_data.wasNull()) return null;
		return v;
	}

	public Integer getNullableInt(CCSQLColDef col) throws SQLWrapperException, SQLException {
		var idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsInteger()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		int v = _data.getInt(idx);
		if (_data.wasNull()) return null;
		return v;
	}

	public Long getNullableLong(CCSQLColDef col) throws SQLWrapperException, SQLException {
		var idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsLong()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		long v = _data.getLong(idx);
		if (_data.wasNull()) return null;
		return v;
	}

	public String getNullableString(CCSQLColDef col) throws SQLWrapperException, SQLException {
		var idx = _statement.getSelectFieldIndex(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsString()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		String v = _data.getString(idx);
		if (_data.wasNull()) return null;
		return v;
	}

	public void close() throws SQLException {
		_data.close();
	}
}
