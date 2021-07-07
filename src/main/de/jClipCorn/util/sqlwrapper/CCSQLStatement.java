package de.jClipCorn.util.sqlwrapper;

import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.stream.CCStreams;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("nls")
public class CCSQLStatement {
	public final String Source;
	public final PreparedStatement Statement;
	private final StatementType StatementType;

	private final Map<Integer,     CCSQLColDef> MapPrepPositions;     // -> query input
	private final Map<CCSQLColDef, Integer>     MapPrepFields;        // -> query input
	private final Map<Integer,     CCSQLColDef> MapSelectPositions;   // -> query output
	private final Map<CCSQLColDef, Integer>     MapSelectFields;      // -> query output

	public CCSQLStatement(StatementType tp, String src, PreparedStatement stmt, List<Tuple<Integer, CCSQLColDef>> prep, List<Tuple<Integer, CCSQLColDef>> sel) throws SQLWrapperException {
		Source = src;
		Statement = stmt;
		StatementType = tp;

		MapPrepPositions = CCStreams.iterate(prep).toMap(p -> p.Item1, p -> p.Item2);
		MapPrepFields    = CCStreams.iterate(prep).toMap(p -> p.Item2, p -> p.Item1);

		if (MapPrepPositions.size() != prep.size()) throw new SQLWrapperException("Duplicate Position (Prep) in CCSQLStatement");
		if (MapPrepFields.size()    != prep.size()) throw new SQLWrapperException("Duplicate Position (Prep) in CCSQLStatement");

		MapSelectPositions = CCStreams.iterate(sel).toMap(p -> p.Item1, p -> p.Item2);
		MapSelectFields    = CCStreams.iterate(sel).toMap(p -> p.Item2, p -> p.Item1);

		if (MapSelectPositions.size() != sel.size()) throw new SQLWrapperException("Duplicate Position (Sel) in CCSQLStatement");
		if (MapSelectFields.size()    != sel.size()) throw new SQLWrapperException("Duplicate Position (Sel) in CCSQLStatement");
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
		var idx = MapPrepFields.get(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsInteger()) throw new SQLWrapperException("Field ["+col+"] has wrong type");

		Statement.setInt(idx, value);
	}

	public void setStr(CCSQLColDef col, String value) throws SQLException, SQLWrapperException {
		var idx = MapPrepFields.get(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsString()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		Statement.setString(idx, value);
	}

	public void setBoo(CCSQLColDef col, boolean value) throws SQLException, SQLWrapperException {
		var idx = MapPrepFields.get(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsBoolean()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		Statement.setBoolean(idx, value);
	}

	public void setLng(CCSQLColDef col, long value) throws SQLWrapperException, SQLException {
		var idx = MapPrepFields.get(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsLong()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		Statement.setLong(idx, value);
	}

	public void setSht(CCSQLColDef col, short value) throws SQLWrapperException, SQLException {
		var idx = MapPrepFields.get(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsShort()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		Statement.setShort(idx, value);
	}

	public void setBlb(CCSQLColDef col, byte[] value) throws SQLWrapperException, SQLException {
		var idx = MapPrepFields.get(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsBlob()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		Statement.setBytes(idx, value);
	}

	public void setCDT(CCSQLColDef col, CCDateTime value) throws SQLWrapperException, SQLException {
		var idx = MapPrepFields.get(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsString()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		Statement.setString(idx, value.toStringSQL());
	}

	public void setFlt(CCSQLColDef col, double value) throws SQLWrapperException, SQLException {
		var idx = MapPrepFields.get(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");
		if (!col.Type.isCallableAsFloat()) throw new SQLWrapperException("Field ["+col.Name+"] has wrong type");

		Statement.setDouble(idx, value);
	}

	public void setNull(CCSQLColDef col) throws SQLWrapperException, SQLException {
		var idx = MapPrepFields.get(col);

		if (idx == null) throw new SQLWrapperException("Field ["+col.Name+"] not found in CCSQLStatement");

		Statement.setNull(idx, col.Type.getSQLType());
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

	public Integer getSelectFieldIndex(CCSQLColDef col) {
		return MapSelectFields.get(col);
	}

	public CCSQLColDef[] getPreparedFields() {
		return MapPrepFields.keySet().toArray(new CCSQLColDef[0]);
	}
}
