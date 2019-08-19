package de.jClipCorn.util.sqlwrapper;

import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.util.datatypes.DoubleString;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.lambda.Func1to1WithGenericException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("nls")
public class SQLBuilder {

	private final StatementType _type;

	private String _table = null;
	private List<Tuple<DoubleString, CCSQLType>> _fields = new ArrayList<>();
	private List<Tuple<DoubleString, CCSQLType>> _whereClauses = new ArrayList<>();
	private List<Tuple<String, CCSQLType>> _selectFields = new ArrayList<>();
	private String _customSQL = null;
	private String _orderField = null;
	private SQLOrder _orderDirection = null;

	private SQLBuilder(StatementType st) {
		_type = st;
	}

	public static SQLBuilder createInsert(CCSQLTableDef tab) {
		SQLBuilder b = new SQLBuilder(StatementType.INSERT);
		b._table = tab.Name;
		return b;
	}

	public static SQLBuilder createUpdate(CCSQLTableDef tab) {
		SQLBuilder b = new SQLBuilder(StatementType.UPDATE);
		b._table = tab.Name;
		return b;
	}

	public static SQLBuilder createInsertOrReplace(CCSQLTableDef tab) {
		SQLBuilder b = new SQLBuilder(StatementType.REPLACE);
		b._table = tab.Name;
		return b;
	}

	public static SQLBuilder createDelete(CCSQLTableDef tab) {
		SQLBuilder b = new SQLBuilder(StatementType.DELETE);
		b._table = tab.Name;
		return b;
	}

	public static SQLBuilder createSelect(CCSQLTableDef tab) {
		SQLBuilder b = new SQLBuilder(StatementType.SELECT);
		b._table = tab.Name;
		return b;
	}

	public static SQLBuilder createCustom(CCSQLTableDef tab) {
		SQLBuilder b = new SQLBuilder(StatementType.CUSTOM);
		b._table = tab.Name;
		return b;
	}

	public SQLBuilder addPreparedField(CCSQLColDef field) throws SQLWrapperException {
		if (_type != StatementType.INSERT && _type != StatementType.UPDATE && _type != StatementType.REPLACE) throw new SQLWrapperException("Cannot [addPreparedField] on type " + _type);

		_fields.add(Tuple.Create(new DoubleString(field.Name, "?"), field.Type));

		return this;
	}

	public SQLBuilder addPreparedFields(CCSQLColDef... fields) throws SQLWrapperException {
		for (CCSQLColDef f : fields) addPreparedField(f);
		return this;
	}

	public SQLBuilder setSQL(String s, String... objList) throws SQLWrapperException {
		if (_type != StatementType.CUSTOM) throw new SQLWrapperException("Cannot [setSQL] on type " + _type);

		for (int i = 0; i < objList.length; i++) s = s.replace("{"+i+"}", objList[i]); //$NON-NLS-1$  //$NON-NLS-2$
		s = s.replace("{TAB}", _table); //$NON-NLS-1$

		_customSQL = s;

		return this;
	}

	public SQLBuilder addPreparedWhereCondition(CCSQLColDef field) throws SQLWrapperException {
		if (_type != StatementType.UPDATE && _type != StatementType.SELECT && _type != StatementType.DELETE) throw new SQLWrapperException("Cannot [addPreparedWhereCondition] on type " + _type);

		_whereClauses.add(Tuple.Create(new DoubleString(field.Name, "?"), field.Type));

		return this;
	}

	public SQLBuilder addSelectField(CCSQLColDef field) throws SQLWrapperException {
		if (_type != StatementType.SELECT) throw new SQLWrapperException("Cannot [addSelectField] on type " + _type);

		_selectFields.add(Tuple.Create(field.Name, field.Type));

		return this;
	}

	public SQLBuilder addSelectFields(CCSQLColDef... fields) throws SQLWrapperException {
		for (CCSQLColDef f : fields) addSelectField(f);
		return this;
	}

	public SQLBuilder setOrder(CCSQLColDef col, SQLOrder ord) throws SQLWrapperException {
		if (_type != StatementType.SELECT) throw new SQLWrapperException("Cannot [setOrder] on type " + _type);

		_orderField = col.Name;
		_orderDirection = ord;

		return this;
	}

	public CCSQLStatement build(CCDatabase db, ArrayList<CCSQLStatement> collector) throws SQLWrapperException, SQLException {
		return build(db::createPreparedStatement, collector);
	}

	public CCSQLStatement build(Func1to1WithGenericException<String, PreparedStatement, SQLException> fn, ArrayList<CCSQLStatement> collector) throws SQLWrapperException, SQLException {
		switch (_type) {
			case SELECT:  { CCSQLStatement r = buildSelect(fn);          collector.add(r); return r; }
			case UPDATE:  { CCSQLStatement r = buildUpdate(fn);          collector.add(r); return r; }
			case REPLACE: { CCSQLStatement r = buildInsertOrReplace(fn); collector.add(r); return r; }
			case DELETE:  { CCSQLStatement r = buildDelete(fn);          collector.add(r); return r; }
			case INSERT:  { CCSQLStatement r = buildInsert(fn);          collector.add(r); return r; }
			case CUSTOM:  { CCSQLStatement r = buildCustom(fn);          collector.add(r); return r; }
			default: throw new SQLWrapperException("Unknown CC:StatementType := " + _type);
		}
	}

	private CCSQLStatement buildCustom(Func1to1WithGenericException<String, PreparedStatement, SQLException> fn) throws SQLWrapperException, SQLException {
		if (_customSQL == null)	throw new SQLWrapperException("No SQL source set");

		return new CCSQLStatement( StatementType.CUSTOM, _customSQL, fn.invoke(_customSQL), new ArrayList<>(), new ArrayList<>());
	}

	@SuppressWarnings("nls")
	private CCSQLStatement buildInsert(Func1to1WithGenericException<String, PreparedStatement, SQLException> fn) throws SQLException, SQLWrapperException {
		StringBuilder cnames = new StringBuilder();
		StringBuilder cvals = new StringBuilder();

		for (int i = 0; i < _fields.size(); i++) {
			if (i > 0) {
				cnames.append(", ");
				cvals.append(", ");
			}
			cnames.append(SQLBuilderHelper.sqlEscape(_fields.get(i).Item1.get1()));
			cvals.append(SQLBuilderHelper.sqlEscape(_fields.get(i).Item1.get2()));
		}

		int prepIdx = 1;

		List<Tuple3<Integer, String, CCSQLType>> fields = new ArrayList<>();
		for (Tuple<DoubleString, CCSQLType> field : _fields) {
			if (!field.Item1.get2().equals("?")) continue; //$NON-NLS-1$
			fields.add(Tuple3.Create(prepIdx, field.Item1.get1(), field.Item2));
			prepIdx++;
		}

		String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", SQLBuilderHelper.sqlEscape(_table), cnames.toString(), cvals.toString());

		return new CCSQLStatement(_type, sql, fn.invoke(sql), fields, new ArrayList<>());
	}

	@SuppressWarnings("nls")
	private CCSQLStatement buildUpdate(Func1to1WithGenericException<String, PreparedStatement, SQLException> fn) throws SQLException, SQLWrapperException {
		StringBuilder assigns = new StringBuilder();

		for (int i = 0; i < _fields.size(); i++) {
			if (i > 0) assigns.append(", ");

			assigns.append(SQLBuilderHelper.sqlEscape(_fields.get(i).Item1.get1()));
			assigns.append("=");
			assigns.append(SQLBuilderHelper.sqlEscape(_fields.get(i).Item1.get2()));
		}

		int prepIdx = 1;
		List<Tuple3<Integer, String, CCSQLType>> fields = new ArrayList<>();

		for (Tuple<DoubleString, CCSQLType> field : _fields) {
			if (!field.Item1.get2().equals("?")) continue; //$NON-NLS-1$
			fields.add(Tuple3.Create(prepIdx, field.Item1.get1(), field.Item2));
			prepIdx++;
		}

		String sql = String.format("UPDATE %s SET %s", SQLBuilderHelper.sqlEscape(_table), assigns.toString());

		if (!_whereClauses.isEmpty()) {
			sql += " WHERE ";

			StringBuilder wconds = new StringBuilder();
			for (int i = 0; i < _whereClauses.size(); i++) {
				if (i > 0) wconds.append(" AND ");

				wconds.append('(');
				wconds.append(SQLBuilderHelper.sqlEscape(_whereClauses.get(i).Item1.get1()));
				wconds.append('=');
				wconds.append(SQLBuilderHelper.sqlEscape(_whereClauses.get(i).Item1.get2()));
				wconds.append(')');
			}

			for (Tuple<DoubleString, CCSQLType> clause : _whereClauses) {
				if (!clause.Item1.get2().equals("?")) continue; //$NON-NLS-1$
				fields.add(Tuple3.Create(prepIdx, clause.Item1.get1(), clause.Item2));
				prepIdx++;
			}

			sql += wconds.toString();
		}

		return new CCSQLStatement(_type, sql, fn.invoke(sql), fields, new ArrayList<>());
	}

	@SuppressWarnings("nls")
	private CCSQLStatement buildInsertOrReplace(Func1to1WithGenericException<String, PreparedStatement, SQLException> fn) throws SQLException, SQLWrapperException {
		StringBuilder assigns1 = new StringBuilder();
		StringBuilder assigns2 = new StringBuilder();

		for (int i = 0; i < _fields.size(); i++) {
			if (i > 0) assigns1.append(", ");
			assigns1.append(SQLBuilderHelper.sqlEscape(_fields.get(i).Item1.get1()));

			if (i > 0) assigns2.append(", ");
			assigns2.append(SQLBuilderHelper.sqlEscape(_fields.get(i).Item1.get2()));
		}

		int prepIdx = 1;
		List<Tuple3<Integer, String, CCSQLType>> fields = new ArrayList<>();

		for (Tuple<DoubleString, CCSQLType> field : _fields) {
			if (!field.Item1.get2().equals("?")) continue; //$NON-NLS-1$
			fields.add(Tuple3.Create(prepIdx, field.Item1.get1(), field.Item2));
			prepIdx++;
		}

		String sql = String.format("INSERT OR REPLACE INTO %s (%s) VALUES (%s)", SQLBuilderHelper.sqlEscape(_table), assigns1.toString(), assigns2.toString());

		if (!_whereClauses.isEmpty()) throw new SQLWrapperException("SQLStatementType.REPLACE does not allow WHERE clauses");

		return new CCSQLStatement(_type, sql, fn.invoke(sql), fields, new ArrayList<>());
	}

	@SuppressWarnings("nls")
	private CCSQLStatement buildDelete(Func1to1WithGenericException<String, PreparedStatement, SQLException> fn) throws SQLException, SQLWrapperException {
		String sql = String.format("DELETE FROM %s", _table);

		int prepIdx = 1;
		List<Tuple3<Integer, String, CCSQLType>> fields = new ArrayList<>();

		if (!_whereClauses.isEmpty()) {
			sql += " WHERE ";

			StringBuilder wconds = new StringBuilder();
			for (int i = 0; i < _whereClauses.size(); i++) {
				if (i > 0) wconds.append(" AND ");

				wconds.append('(');
				wconds.append(SQLBuilderHelper.sqlEscape(_whereClauses.get(i).Item1.get1()));
				wconds.append('=');
				wconds.append(SQLBuilderHelper.sqlEscape(_whereClauses.get(i).Item1.get2()));
				wconds.append(')');
			}

			for (Tuple<DoubleString, CCSQLType> clause : _whereClauses) {
				if (!clause.Item1.get2().equals("?")) continue; //$NON-NLS-1$
				fields.add(Tuple3.Create(prepIdx, clause.Item1.get1(), clause.Item2));
				prepIdx++;
			}

			sql += wconds.toString();
		}

		return new CCSQLStatement(_type, sql, fn.invoke(sql), fields, new ArrayList<>());
	}

	@SuppressWarnings("nls")
	private CCSQLStatement buildSelect(Func1to1WithGenericException<String, PreparedStatement, SQLException> fn) throws SQLException, SQLWrapperException {
		StringBuilder assigns = new StringBuilder();

		List<Tuple3<Integer, String, CCSQLType>> selectFields = new ArrayList<>();

		for (int i = 0; i < _selectFields.size(); i++) {
			if (i > 0) assigns.append(", ");

			assigns.append(SQLBuilderHelper.sqlEscape(_selectFields.get(i).Item1));

			selectFields.add(Tuple3.Create(i+1, _selectFields.get(i).Item1, _selectFields.get(i).Item2));
		}

		int prepIdx = 1;
		List<Tuple3<Integer, String, CCSQLType>> fields = new ArrayList<>();

		String sql = String.format("SELECT %s FROM %s", assigns.toString(), SQLBuilderHelper.sqlEscape(_table));

		if (!_whereClauses.isEmpty()) {
			sql += " WHERE ";

			StringBuilder wconds = new StringBuilder();
			for (int i = 0; i < _whereClauses.size(); i++) {
				if (i > 0) wconds.append(" AND ");

				wconds.append('(');
				wconds.append(SQLBuilderHelper.sqlEscape(_whereClauses.get(i).Item1.get1()));
				wconds.append('=');
				wconds.append(SQLBuilderHelper.sqlEscape(_whereClauses.get(i).Item1.get2()));
				wconds.append(')');
			}

			for (Tuple<DoubleString, CCSQLType> clause : _whereClauses) {
				if (!clause.Item1.get2().equals("?")) continue; //$NON-NLS-1$
				fields.add(Tuple3.Create(prepIdx, clause.Item1.get1(), clause.Item2));
				prepIdx++;
			}

			sql += wconds.toString();
		}

		if (_orderField != null && _orderDirection != null) {
			sql += String.format(" ORDER BY %s %s", SQLBuilderHelper.sqlEscape(_orderField), _orderDirection == SQLOrder.ASC ? "ASC" : "DESC");
		}

		return new CCSQLStatement(_type, sql, fn.invoke(sql), fields, selectFields);
	}
}
