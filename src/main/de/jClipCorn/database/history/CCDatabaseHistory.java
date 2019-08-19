package de.jClipCorn.database.history;

import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.database.driver.DatabaseStructure;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.sqlwrapper.CCSQLColDef;
import de.jClipCorn.util.sqlwrapper.CCSQLTableDef;
import de.jClipCorn.util.stream.CCStreams;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static de.jClipCorn.util.sqlwrapper.SQLBuilderHelper.forceSQLEscape;

public class CCDatabaseHistory {

	private final CCDatabase _db;
	
	public CCDatabaseHistory(CCDatabase db) {
		_db = db;
	}

	public boolean isHistoryActive() {
		String str = _db.getInformationFromDB(DatabaseStructure.INFOKEY_HISTORY);
		if ("0".equals(str)) return false; //$NON-NLS-1$
		if ("1".equals(str)) return true;  //$NON-NLS-1$

		CCLog.addError("Invalid value in Database for [INFOKEY_HISTORY]: '" + str + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		return false;
	}

	public List<Tuple<String, String>> createTriggerStatements() {
		List<Tuple<String, String>> result = new ArrayList<>();

		for(CCSQLTableDef tab : DatabaseStructure.TABLES) {
			if (tab == DatabaseStructure.TAB_HISTORY) continue;
			if (tab == DatabaseStructure.TAB_TEMP) continue;

			result.add(createTriggerOnAdd(tab));
			for(CCSQLColDef col : tab.Columns) result.add(createTriggerOnUpdate(tab, col));
			result.add(createTriggerOnDelete(tab));
		}

		return result;
	}

	@SuppressWarnings("nls")
	private Tuple<String, String> createTriggerOnAdd(CCSQLTableDef tab) {
		String triggerName = Str.format("JCCTRIGGER_AUTOHISTORY_ADD_{0}", tab.Name.toUpperCase()); //$NON-NLS-1$

		StringBuilder triggerbuilder = new StringBuilder();

		triggerbuilder.append("CREATE TRIGGER ").append(forceSQLEscape(triggerName)).append(" AFTER INSERT ON ").append(forceSQLEscape(tab.Name)).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		triggerbuilder.append("BEGIN").append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
		for (CCSQLColDef col : tab.getNonPrimaryColumns()) {
			triggerbuilder
					.append(" INSERT INTO HISTORY (").append("`TABLE`, `ID`, `DATE`, `ACTION`, `FIELD`, `OLD`, `NEW`").append(") VALUES (") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					.append("'").append(tab.Name).append("', ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("NEW.").append(forceSQLEscape(tab.Primary.Name)).append(", ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW'), ") //$NON-NLS-1$
					.append("'ADD', ") //$NON-NLS-1$
					.append("'").append(col.Name).append("', ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("NULL, ") //$NON-NLS-1$
					.append("NEW.").append(forceSQLEscape(col.Name)) //$NON-NLS-1$
					.append(");\n"); //$NON-NLS-1$
		}
		triggerbuilder.append("END").append("\n"); //$NON-NLS-1$ //$NON-NLS-2$

		return Tuple.Create(triggerName, triggerbuilder.toString());
	}

	@SuppressWarnings("nls")
	private Tuple<String, String> createTriggerOnUpdate(CCSQLTableDef tab, CCSQLColDef col) {
		String triggerName = Str.format("JCCTRIGGER_AUTOHISTORY_UPD_{0}_{1}", tab.Name.toUpperCase(), col.Name.toUpperCase().replace('.', '-')); //$NON-NLS-1$

		StringBuilder triggerbuilder = new StringBuilder();

		triggerbuilder
				.append("CREATE TRIGGER ") //$NON-NLS-1$
				.append(forceSQLEscape(triggerName))
				.append(" AFTER UPDATE ON ") //$NON-NLS-1$
				.append(forceSQLEscape(tab.Name))
				.append(" "); //$NON-NLS-1$
		triggerbuilder
				.append("WHEN NOT (COALESCE(OLD.") //$NON-NLS-1$
				.append(forceSQLEscape(col.Name))
				.append(" = NEW.") //$NON-NLS-1$
				.append(forceSQLEscape(col.Name))
				.append(", 1=0) OR (COALESCE(OLD.") //$NON-NLS-1$
				.append(forceSQLEscape(col.Name))
				.append(", NEW.") //$NON-NLS-1$
				.append(forceSQLEscape(col.Name))
				.append(") IS NULL))") //$NON-NLS-1$
				.append("\n"); //$NON-NLS-1$

		triggerbuilder.append("BEGIN").append("\n"); //$NON-NLS-1$ //$NON-NLS-2$

		triggerbuilder
				.append(" INSERT INTO HISTORY (").append("`TABLE`, `ID`, `DATE`, `ACTION`, `FIELD`, `OLD`, `NEW`").append(") VALUES (") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				.append("'").append(tab.Name).append("', ") //$NON-NLS-1$ //$NON-NLS-2$
				.append("OLD.").append(forceSQLEscape(tab.Primary.Name)).append(", ") //$NON-NLS-1$ //$NON-NLS-2$
				.append("STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW'), ") //$NON-NLS-1$
				.append("'UPDATE', ") //$NON-NLS-1$
				.append("'").append(col.Name).append("', ") //$NON-NLS-1$ //$NON-NLS-2$
				.append("OLD.").append(forceSQLEscape(col.Name)).append(", ") //$NON-NLS-1$ //$NON-NLS-2$
				.append("NEW.").append(forceSQLEscape(col.Name)) //$NON-NLS-1$
				.append(");\n"); //$NON-NLS-1$

		triggerbuilder.append("END").append("\n"); //$NON-NLS-1$ //$NON-NLS-2$

		return Tuple.Create(triggerName, triggerbuilder.toString());
	}

	@SuppressWarnings("nls")
	private Tuple<String, String> createTriggerOnDelete(CCSQLTableDef tab) {
		String triggerName = Str.format("JCCTRIGGER_AUTOHISTORY_REM_{0}", tab.Name.toUpperCase()); //$NON-NLS-1$

		StringBuilder triggerbuilder = new StringBuilder();

		triggerbuilder.append("CREATE TRIGGER ").append(forceSQLEscape(triggerName)).append(" BEFORE DELETE ON ").append(forceSQLEscape(tab.Name)).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		triggerbuilder.append("BEGIN").append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
		for (CCSQLColDef col : tab.getNonPrimaryColumns()) {
			triggerbuilder
					.append(" INSERT INTO HISTORY (").append("`TABLE`, `ID`, `DATE`, `ACTION`, `FIELD`, `OLD`, `NEW`").append(") VALUES (") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					.append("'").append(tab.Name).append("', ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("OLD.").append(forceSQLEscape(tab.Primary.Name)).append(", ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW'), ") //$NON-NLS-1$
					.append("'DELETE', ") //$NON-NLS-1$
					.append("'").append(col.Name).append("', ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("OLD.").append(forceSQLEscape(col.Name)).append(", ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("NULL") //$NON-NLS-1$
					.append(");\n"); //$NON-NLS-1$
		}
		triggerbuilder.append("END").append("\n"); //$NON-NLS-1$ //$NON-NLS-2$

		return Tuple.Create(triggerName, triggerbuilder.toString());
	}

	@SuppressWarnings("nls")
	public boolean testTrigger(boolean active, RefParam<String> error) {
		try {
			List<Tuple<String, String>> triggerDB = _db.listTrigger();
			List<Tuple<String, String>> triggerOK = createTriggerStatements();

			if (active) {
				for (Tuple<String, String> t : triggerOK) {
					Tuple<String, String> db = CCStreams.iterate(triggerDB).singleOrNull(p -> Str.equals(p.Item1, t.Item1));
					if (db != null) { error.Value = Str.format("Trigger [{0}] exists", t.Item1); return false; } //$NON-NLS-1$
				}
			} else {
				for (Tuple<String, String> t : triggerOK) {
					Tuple<String, String> db = CCStreams.iterate(triggerDB).singleOrNull(p -> Str.equals(p.Item1, t.Item1));
					if (db == null) { error.Value = Str.format("Trigger [{0}] not found", t.Item1); return false; } //$NON-NLS-1$
					if (!Str.equals(db.Item2, t.Item2)) { error.Value = Str.format("Trigger [{0}] has wrong code", t.Item1); return false; } //$NON-NLS-1$
				}
			}
		} catch (SQLException e) {
			error.Value = Str.format("Exception '{0}' thrown", e.getClass().getSimpleName()); //$NON-NLS-1$
			return false;
		}

		return true;
	}

	public int getCount() {
		return _db.getHistoryCount();
	}

	public void createTrigger() {
		
	}
}
