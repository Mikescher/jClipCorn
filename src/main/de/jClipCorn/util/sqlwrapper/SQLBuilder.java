package de.jClipCorn.util.sqlwrapper;

import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.lambda.Func1to1WithGenericException;
import de.jClipCorn.util.stream.CCStreams;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("nls")
public class SQLBuilder {

	private final StatementType _type;
	private final CCSQLTableDef _table;

	private final List<CCSQLColDef>                 _fields           = new ArrayList<>();
	private final List<CCSQLColDef>                 _whereClauses     = new ArrayList<>();
	private final List<CCSQLColDef>                 _selectFields     = new ArrayList<>();
	private final List<Tuple<Integer, CCSQLColDef>> _customPrepFields = new ArrayList<>();
	private final List<Tuple<Integer, CCSQLColDef>> _customSelFields  = new ArrayList<>();

	private Tuple<CCSQLColDef, SQLOrder> _order = null;

	private String _customSQL = null;

	private SQLBuilder(StatementType st, CCSQLTableDef tab) {
		_type = st;
		_table = tab;
	}

	public static SQLBuilder createInsert(CCSQLTableDef tab) {
		return new SQLBuilder(StatementType.INSERT, tab);
	}

	public static SQLBuilder createUpdate(CCSQLTableDef tab) {
		return new SQLBuilder(StatementType.UPDATE, tab);
	}

	public static SQLBuilder createInsertOrReplace(CCSQLTableDef tab) {
		return new SQLBuilder(StatementType.REPLACE, tab);
	}

	public static SQLBuilder createDelete(CCSQLTableDef tab) {
		return new SQLBuilder(StatementType.DELETE, tab);
	}

	public static SQLBuilder createSelect(CCSQLTableDef tab) {
		return new SQLBuilder(StatementType.SELECT, tab);
	}

	public static SQLBuilder createCustom(CCSQLTableDef tab) {
		return new SQLBuilder(StatementType.CUSTOM, tab);
	}

	public static SQLBuilder createSchema(CCSQLTableDef tab) {
		return new SQLBuilder(StatementType.TABSCHEMA, tab);
	}

	public static SQLBuilder createInsertSingle(CCSQLTableDef tab) throws SQLWrapperException {
		var v = createInsert(tab);
		for (var col: tab.Columns) v = v.addPreparedField(col);
		return v;
	}

	public static SQLBuilder createUpdateSingle(CCSQLTableDef tab, CCSQLColDef where) throws SQLWrapperException {
		var v = createUpdate(tab);
		v = v.addPreparedWhereCondition(where);
		for (var col: tab.Columns) if (col != where) v = v.addPreparedField(col);
		return v;
	}

	public static SQLBuilder createSelectAll(CCSQLTableDef tab) throws SQLWrapperException {
		var v = createSelect(tab);
		for (var col: tab.Columns) v = v.addSelectField(col);
		return v;
	}

	public static SQLBuilder createSelectSingle(CCSQLTableDef tab, CCSQLColDef where) throws SQLWrapperException {
		var v = createSelect(tab);
		v = v.addPreparedWhereCondition(where);
		for (var col: tab.Columns) v = v.addSelectField(col);
		return v;
	}

	public SQLBuilder addPreparedField(CCSQLColDef field) throws SQLWrapperException {
		if (_type != StatementType.INSERT && _type != StatementType.UPDATE && _type != StatementType.REPLACE) throw new SQLWrapperException("Cannot [addPreparedField] on type " + _type);
		if (!_table.contains(field)) throw new SQLWrapperException("Field ["+field.Name+"] is not part of table " + _table.Name);

		_fields.add(field);

		return this;
	}

	public SQLBuilder addPreparedFields(CCSQLColDef... fields) throws SQLWrapperException {
		for (CCSQLColDef f : fields) addPreparedField(f);
		return this;
	}

	public SQLBuilder setSQL(String s, String... objList) throws SQLWrapperException {
		if (_type != StatementType.CUSTOM) throw new SQLWrapperException("Cannot [setSQL] on type " + _type);

		for (int i = 0; i < objList.length; i++) s = s.replace("{"+i+"}", objList[i]); //$NON-NLS-1$  //$NON-NLS-2$
		s = s.replace("{TAB}", _table.Name); //$NON-NLS-1$

		_customSQL = s;

		return this;
	}

	public SQLBuilder addPreparedWhereCondition(CCSQLColDef field) throws SQLWrapperException {
		if (_type != StatementType.UPDATE && _type != StatementType.SELECT && _type != StatementType.DELETE) throw new SQLWrapperException("Cannot [addPreparedWhereCondition] on type " + _type);
		if (!_table.contains(field)) throw new SQLWrapperException("Field ["+field.Name+"] is not part of table " + _table.Name);

		_whereClauses.add(field);

		return this;
	}

	public SQLBuilder addSelectField(CCSQLColDef field) throws SQLWrapperException {
		if (_type != StatementType.SELECT) throw new SQLWrapperException("Cannot [addSelectField] on type " + _type);
		if (!_table.contains(field)) throw new SQLWrapperException("Field ["+field.Name+"] is not part of table " + _table.Name);

		_selectFields.add(field);

		return this;
	}

	public SQLBuilder addSelectFields(CCSQLColDef... fields) throws SQLWrapperException {
		for (CCSQLColDef f : fields) addSelectField(f);
		return this;
	}

	public SQLBuilder remSelectField(CCSQLColDef field) throws SQLWrapperException {
		if (_type != StatementType.SELECT) throw new SQLWrapperException("Cannot [remSelectField] on type " + _type);
		if (!_table.contains(field)) throw new SQLWrapperException("Field ["+field.Name+"] is not part of table " + _table.Name);

		var rm = CCStreams.iterate(_selectFields).singleOrNull(p -> p == field);
		if (rm == null) throw new SQLWrapperException("Cannot [remSelectField]: (not found)");

		var ok = _selectFields.remove(rm);
		if (!ok) throw new SQLWrapperException("Cannot [remSelectField]: (failed)");

		return this;
	}

	public SQLBuilder setOrder(CCSQLColDef col, SQLOrder ord) throws SQLWrapperException {
		if (_type != StatementType.SELECT) throw new SQLWrapperException("Cannot [setOrder] on type " + _type);

		_order = Tuple.Create(col, ord);

		return this;
	}

	public SQLBuilder setCustomSelectField(int pos, CCSQLColDef field) {
		_customSelFields.add(Tuple.Create(pos, field));
		return this;
	}

	public SQLBuilder setCustomPrepared(int pos, CCSQLColDef field) {
		_customPrepFields.add(Tuple.Create(pos, field));
		return this;
	}

	public CCSQLStatement build(CCDatabase db, ArrayList<CCSQLStatement> collector) throws SQLWrapperException, SQLException {
		return build(db::createPreparedStatement, collector);
	}

	public CCSQLStatement build(Func1to1WithGenericException<String, PreparedStatement, SQLException> fn, ArrayList<CCSQLStatement> collector) throws SQLWrapperException, SQLException {
		switch (_type) {
			case SELECT:    { CCSQLStatement r = buildSelect(fn);          collector.add(r); return r; }
			case UPDATE:    { CCSQLStatement r = buildUpdate(fn);          collector.add(r); return r; }
			case REPLACE:   { CCSQLStatement r = buildInsertOrReplace(fn); collector.add(r); return r; }
			case DELETE:    { CCSQLStatement r = buildDelete(fn);          collector.add(r); return r; }
			case INSERT:    { CCSQLStatement r = buildInsert(fn);          collector.add(r); return r; }
			case TABSCHEMA: { CCSQLStatement r = buildTableSchema(fn);     collector.add(r); return r; }
			case CUSTOM:    { CCSQLStatement r = buildCustom(fn);          collector.add(r); return r; }
			default: throw new SQLWrapperException("Unknown CC:StatementType := " + _type);
		}
	}

	private CCSQLStatement buildCustom(Func1to1WithGenericException<String, PreparedStatement, SQLException> fn) throws SQLWrapperException, SQLException {
		if (_customSQL == null)	throw new SQLWrapperException("No SQL source set");

		return new CCSQLStatement( StatementType.CUSTOM, _customSQL, fn.invoke(_customSQL), _customPrepFields, _customSelFields);
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
			cnames.append(SQLBuilderHelper.sqlEscape(_fields.get(i).Name));
			cvals.append("?");
		}

		int prepIdx = 1;

		List<Tuple<Integer, CCSQLColDef>> fields = new ArrayList<>();
		for (var field : _fields) {
			fields.add(Tuple.Create(prepIdx, field));
			prepIdx++;
		}

		String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", SQLBuilderHelper.sqlEscape(_table.Name), cnames.toString(), cvals.toString());

		return new CCSQLStatement(_type, sql, fn.invoke(sql), fields, new ArrayList<>());
	}

	@SuppressWarnings("nls")
	private CCSQLStatement buildUpdate(Func1to1WithGenericException<String, PreparedStatement, SQLException> fn) throws SQLException, SQLWrapperException {
		StringBuilder assigns = new StringBuilder();

		for (int i = 0; i < _fields.size(); i++) {
			if (i > 0) assigns.append(", ");

			assigns.append(SQLBuilderHelper.sqlEscape(_fields.get(i).Name));
			assigns.append("=");
			assigns.append("?");
		}

		int prepIdx = 1;
		List<Tuple<Integer, CCSQLColDef>> fields = new ArrayList<>();

		for (var field : _fields) {
			fields.add(Tuple.Create(prepIdx, field));
			prepIdx++;
		}

		String sql = String.format("UPDATE %s SET %s", SQLBuilderHelper.sqlEscape(_table.Name), assigns.toString());

		if (!_whereClauses.isEmpty()) {
			sql += " WHERE ";

			StringBuilder wconds = new StringBuilder();
			for (int i = 0; i < _whereClauses.size(); i++) {
				if (i > 0) wconds.append(" AND ");

				wconds.append('(');
				wconds.append(SQLBuilderHelper.sqlEscape(_whereClauses.get(i).Name));
				wconds.append('=');
				wconds.append("?");
				wconds.append(')');
			}

			for (var clause : _whereClauses) {
				fields.add(Tuple.Create(prepIdx, clause));
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
			assigns1.append(SQLBuilderHelper.sqlEscape(_fields.get(i).Name));

			if (i > 0) assigns2.append(", ");
			assigns2.append("?");
		}

		int prepIdx = 1;
		List<Tuple<Integer, CCSQLColDef>> fields = new ArrayList<>();

		for (var field : _fields) {
			fields.add(Tuple.Create(prepIdx, field));
			prepIdx++;
		}

		String sql = String.format("INSERT OR REPLACE INTO %s (%s) VALUES (%s)", SQLBuilderHelper.sqlEscape(_table.Name), assigns1.toString(), assigns2.toString());

		if (!_whereClauses.isEmpty()) throw new SQLWrapperException("SQLStatementType.REPLACE does not allow WHERE clauses");

		return new CCSQLStatement(_type, sql, fn.invoke(sql), fields, new ArrayList<>());
	}

	@SuppressWarnings("nls")
	private CCSQLStatement buildDelete(Func1to1WithGenericException<String, PreparedStatement, SQLException> fn) throws SQLException, SQLWrapperException {
		String sql = String.format("DELETE FROM %s", _table.Name);

		int prepIdx = 1;
		List<Tuple<Integer, CCSQLColDef>> fields = new ArrayList<>();

		if (!_whereClauses.isEmpty()) {
			sql += " WHERE ";

			StringBuilder wconds = new StringBuilder();
			for (int i = 0; i < _whereClauses.size(); i++) {
				if (i > 0) wconds.append(" AND ");

				wconds.append('(');
				wconds.append(SQLBuilderHelper.sqlEscape(_whereClauses.get(i).Name));
				wconds.append('=');
				wconds.append("?");
				wconds.append(')');
			}

			for (var clause : _whereClauses) {
				fields.add(Tuple.Create(prepIdx, clause));
				prepIdx++;
			}

			sql += wconds.toString();
		}

		return new CCSQLStatement(_type, sql, fn.invoke(sql), fields, new ArrayList<>());
	}

	@SuppressWarnings("nls")
	private CCSQLStatement buildSelect(Func1to1WithGenericException<String, PreparedStatement, SQLException> fn) throws SQLException, SQLWrapperException {
		StringBuilder assigns = new StringBuilder();

		List<Tuple<Integer, CCSQLColDef>> selectFields = new ArrayList<>();

		for (int i = 0; i < _selectFields.size(); i++) {
			if (i > 0) assigns.append(", ");

			assigns.append(SQLBuilderHelper.sqlEscape(_selectFields.get(i).Name));

			selectFields.add(Tuple.Create(i+1, _selectFields.get(i)));
		}

		int prepIdx = 1;
		List<Tuple<Integer, CCSQLColDef>> fields = new ArrayList<>();

		String sql = String.format("SELECT %s FROM %s", assigns.toString(), SQLBuilderHelper.sqlEscape(_table.Name));

		if (!_whereClauses.isEmpty()) {
			sql += " WHERE ";

			StringBuilder wconds = new StringBuilder();
			for (int i = 0; i < _whereClauses.size(); i++) {
				if (i > 0) wconds.append(" AND ");

				wconds.append('(');
				wconds.append(SQLBuilderHelper.sqlEscape(_whereClauses.get(i).Name));
				wconds.append('=');
				wconds.append("?");
				wconds.append(')');
			}

			for (var clause : _whereClauses) {
				fields.add(Tuple.Create(prepIdx, clause));
				prepIdx++;
			}

			sql += wconds.toString();
		}

		if (_order != null) {
			sql += String.format(" ORDER BY %s %s", SQLBuilderHelper.sqlEscape(_order.Item1.Name), _order.Item2 == SQLOrder.ASC ? "ASC" : "DESC");
		}

		return new CCSQLStatement(_type, sql, fn.invoke(sql), fields, selectFields);
	}

	@SuppressWarnings("nls")
	private CCSQLStatement buildTableSchema(Func1to1WithGenericException<String, PreparedStatement, SQLException> fn) throws SQLException, SQLWrapperException {

		List<String> colSQL = new ArrayList<>();

		for (var column : _table.Columns) {
			colSQL.add(getSQLCreateColumn(_table, column));
		}

		for (var fkey : _table.ForeignKeys) {
			colSQL.add(getSQLCreateForeignKey(fkey));
		}

		var sql = "CREATE TABLE " + SQLBuilderHelper.sqlEscape(_table.Name) + "(" + String.join(",", colSQL) + ")";

		return new CCSQLStatement(_type, sql, fn.invoke(sql), new ArrayList<>(), new ArrayList<>());
	}

	private String getSQLCreateColumn(CCSQLTableDef tab, CCSQLColDef col) {
		String tabSQL = SQLBuilderHelper.sqlEscape(col.Name) + " " + col.Type.toSQL();

		if (tab.Primary == col) {
			tabSQL += " PRIMARY KEY";
		} else if (col.NonNullable) {
			tabSQL += " NOT NULL";
		}

		return tabSQL;
	}

	private String getSQLCreateForeignKey(CCSQLFKey fkey) {
		return "FOREIGN KEY(" + SQLBuilderHelper.sqlEscape(fkey.ColumnLocal.Name) + ") REFERENCES " + SQLBuilderHelper.sqlEscape(fkey.TableForeign.Name) + "(" + SQLBuilderHelper.sqlEscape(fkey.ColumnForeign.Name) + ")";
	}
}
