package de.jClipCorn.util.sqlwrapper;

import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.datetime.CCDateTime;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("nls")
public class CCSQLStatement {
	public final String Source;
	public final PreparedStatement Statement;
	public final StatementType StatementType;
	public final HashMap<Integer, Tuple<CCSQLType, String>> MapPrepPositions;
	public final HashMap<String, Tuple<CCSQLType, Integer>> MapPrepFields;
	public final HashMap<Integer, Tuple<CCSQLType, String>> MapSelectPositions;
	public final HashMap<String, Tuple<CCSQLType, Integer>> MapSelectFields;

	public CCSQLStatement(StatementType tp, String src, PreparedStatement stmt, List<Tuple3<Integer, String, CCSQLType>> prep, List<Tuple3<Integer, String, CCSQLType>> sel) throws SQLWrapperException {
		Source = src;
		Statement = stmt;
		StatementType = tp;

		MapPrepPositions = new HashMap<>();
		MapPrepFields = new HashMap<>();
		for (Tuple3<Integer, String, CCSQLType> field : prep) {
			if (MapPrepPositions.containsKey(field.Item1)) throw new SQLWrapperException("Duplicate Position (Prep) in CCSQLStatement: " + field.Item1);
			if (MapPrepFields.containsKey(field.Item2))    throw new SQLWrapperException("Duplicate Position (Prep) in CCSQLStatement: " + field.Item2);

			MapPrepPositions.put(field.Item1, Tuple.Create(field.Item3, field.Item2));
			MapPrepFields.put(field.Item2,    Tuple.Create(field.Item3, field.Item1));
		}

		MapSelectPositions = new HashMap<>();
		MapSelectFields = new HashMap<>();
		for (Tuple3<Integer, String, CCSQLType> field : sel) {
			if (MapSelectPositions.containsKey(field.Item1)) throw new SQLWrapperException("Duplicate Position (Sel) in CCSQLStatement: " + field.Item1);
			if (MapSelectFields.containsKey(field.Item2))    throw new SQLWrapperException("Duplicate Position (Sel) in CCSQLStatement: " + field.Item2);

			MapSelectPositions.put(field.Item1, Tuple.Create(field.Item3, field.Item2));
			MapSelectFields.put(field.Item2,    Tuple.Create(field.Item3, field.Item1));
		}
	}

	public void clearParameters() throws SQLException {
		Statement.clearParameters();
	}

	public int executeUpdate() throws SQLException {
		CCLog.addSQL("ExecuteUpdate", StatementType, Source); //$NON-NLS-1$

		return Statement.executeUpdate();
	}

	public CCSQLResultSet executeQuery(CCDatabase db) throws SQLException {
		CCLog.addSQL("ExecuteQuery", StatementType, Source); //$NON-NLS-1$

		return new CCSQLResultSet(db, Statement.executeQuery(), this);
	}

	public boolean execute() throws SQLException {
		CCLog.addSQL("Execute", StatementType, Source); //$NON-NLS-1$

		return Statement.execute();
	}

	public void setInt(CCSQLColDef col, int value) throws SQLException, SQLWrapperException {
		Tuple<CCSQLType, Integer> idx = MapPrepFields.get(col.Name);

		if (idx == null) throw new SQLWrapperException("Field ["+col+"] not found in CCSQLStatement");
		if (!idx.Item1.isCallableAsInteger()) throw new SQLWrapperException("Field ["+col+"] has wrong type");

		Statement.setInt(idx.Item2, value);
	}

	public void setStr(CCSQLColDef col, String value) throws SQLException, SQLWrapperException {
		Tuple<CCSQLType, Integer> idx = MapPrepFields.get(col.Name);

		if (idx == null) throw new SQLWrapperException("Field ["+col+"] not found in CCSQLStatement");
		if (!idx.Item1.isCallableAsString()) throw new SQLWrapperException("Field ["+col+"] has wrong type");

		Statement.setString(idx.Item2, value);
	}

	public void setBoo(CCSQLColDef col, boolean value) throws SQLException, SQLWrapperException {
		Tuple<CCSQLType, Integer> idx = MapPrepFields.get(col.Name);

		if (idx == null) throw new SQLWrapperException("Field ["+col+"] not found in CCSQLStatement");
		if (!idx.Item1.isCallableAsBoolean()) throw new SQLWrapperException("Field ["+col+"] has wrong type");

		Statement.setBoolean(idx.Item2, value);
	}

	public void setLng(CCSQLColDef col, long value) throws SQLWrapperException, SQLException {
		Tuple<CCSQLType, Integer> idx = MapPrepFields.get(col.Name);

		if (idx == null) throw new SQLWrapperException("Field ["+col+"] not found in CCSQLStatement");
		if (!idx.Item1.isCallableAsLong()) throw new SQLWrapperException("Field ["+col+"] has wrong type");

		Statement.setLong(idx.Item2, value);
	}

	public void setSht(CCSQLColDef col, short value) throws SQLWrapperException, SQLException {
		Tuple<CCSQLType, Integer> idx = MapPrepFields.get(col.Name);

		if (idx == null) throw new SQLWrapperException("Field ["+col+"] not found in CCSQLStatement");
		if (!idx.Item1.isCallableAsShort()) throw new SQLWrapperException("Field ["+col+"] has wrong type");

		Statement.setShort(idx.Item2, value);
	}

	public void setBlb(CCSQLColDef col, byte[] value) throws SQLWrapperException, SQLException {
		Tuple<CCSQLType, Integer> idx = MapPrepFields.get(col.Name);

		if (idx == null) throw new SQLWrapperException("Field ["+col+"] not found in CCSQLStatement");
		if (!idx.Item1.isCallableAsBlob()) throw new SQLWrapperException("Field ["+col+"] has wrong type");

		Statement.setBytes(idx.Item2, value);
	}

	public void setCDT(CCSQLColDef col, CCDateTime value) throws SQLWrapperException, SQLException {
		Tuple<CCSQLType, Integer> idx = MapPrepFields.get(col.Name);

		if (idx == null) throw new SQLWrapperException("Field ["+col+"] not found in CCSQLStatement");
		if (!idx.Item1.isCallableAsString()) throw new SQLWrapperException("Field ["+col+"] has wrong type");

		Statement.setString(idx.Item2, value.getSQLStringRepresentation());
	}

	public void setFlt(CCSQLColDef col, double value) throws SQLWrapperException, SQLException {
		Tuple<CCSQLType, Integer> idx = MapPrepFields.get(col.Name);

		if (idx == null) throw new SQLWrapperException("Field ["+col+"] not found in CCSQLStatement");
		if (!idx.Item1.isCallableAsFloat()) throw new SQLWrapperException("Field ["+col+"] has wrong type");

		Statement.setDouble(idx.Item2, value);
	}

	public void setNull(CCSQLColDef col) throws SQLWrapperException, SQLException {
		Tuple<CCSQLType, Integer> idx = MapPrepFields.get(col.Name);

		if (idx == null) throw new SQLWrapperException("Field ["+col+"] not found in CCSQLStatement");

		Statement.setNull(idx.Item2, col.Type.getSQLType());
	}

	public void tryClose() throws SQLException {
		if (Statement == null) return;
		if (Statement.isClosed()) return;

		Statement.close();
	}

	public int executeQueryInt(CCDatabase db) throws SQLException {
		return getIntFromSet(executeQuery(db));
	}

	private static int getIntFromSet(CCSQLResultSet rs) throws SQLException {
		rs.next();
		int r = rs.getIntDirect(1);
		rs.close();
		return r;
	}

	public Tuple<CCSQLType, Integer> getSelectFieldIndex(CCSQLColDef col) {
		return MapSelectFields.get(col.Name);
	}

}
