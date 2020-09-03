package de.jClipCorn.database.history;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.database.driver.DatabaseStructure;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.ProgressCallbackSink;
import de.jClipCorn.util.sqlwrapper.CCSQLColDef;
import de.jClipCorn.util.sqlwrapper.CCSQLTableDef;
import de.jClipCorn.util.stream.CCStreams;

import java.sql.SQLException;
import java.util.*;

import static de.jClipCorn.util.sqlwrapper.SQLBuilderHelper.forceSQLEscape;

public class CCDatabaseHistory {
	private final static int MERGE_DIFF_TIME  = 15 * 60; // sec
	private final static int MERGE_MAX_TIME   = 45 * 60; // sec
	private final static int MERGE_SHORT_TIME = 8;       // sec

	private final CCDatabase _db;

	public CCDatabaseHistory(CCDatabase db) {
		_db = db;
	}

	public boolean isHistoryActive() {
		String str = _db.readInformationFromDB(DatabaseStructure.INFOKEY_HISTORY, "MISSING_ENTRY"); //$NON-NLS-1$
		if ("0".equals(str)) return false; //$NON-NLS-1$
		if ("1".equals(str)) return true;  //$NON-NLS-1$

		CCLog.addError("Invalid value in Database for [INFOKEY_HISTORY]: '" + str + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		return false;
	}

	public static List<Tuple<String, String>> createTriggerStatements() {
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
	private static Tuple<String, String> createTriggerOnAdd(CCSQLTableDef tab) {
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
	private static Tuple<String, String> createTriggerOnUpdate(CCSQLTableDef tab, CCSQLColDef col) {
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
	private static Tuple<String, String> createTriggerOnDelete(CCSQLTableDef tab) {
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
	public boolean testTrigger(boolean active, RefParam<String> referror) {

		List<String> errors = new ArrayList<>();

		try {
			List<Tuple<String, String>> triggerDB = _db.listTrigger();
			List<Tuple<String, String>> triggerOK = createTriggerStatements();

			if (active) {
				for (Tuple<String, String> t : triggerOK) {
					Tuple<String, String> db = CCStreams.iterate(triggerDB).singleOrNull(p -> Str.equals(p.Item1, t.Item1));
					if (db == null)
						errors.add(Str.format("Trigger [{0}] not found", t.Item1)); //$NON-NLS-1$
					else if (!Str.equals(db.Item2.replace("\r", "").trim(), t.Item2.replace("\r", "").trim())) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						errors.add(Str.format("Trigger [{0}] has wrong code", t.Item1)); //$NON-NLS-1$
				}
			} else {
				for (Tuple<String, String> t : triggerOK) {
					Tuple<String, String> db = CCStreams.iterate(triggerDB).singleOrNull(p -> Str.equals(p.Item1, t.Item1));
					if (db != null) errors.add(Str.format("Trigger [{0}] exists", t.Item1)); //$NON-NLS-1$
				}
			}
		} catch (SQLException e) {
			errors.add(Str.format("Exception '{0}' thrown", e.getClass().getSimpleName())); //$NON-NLS-1$
		}

		if (errors.size()>0) {
			referror.Value = CCStreams.iterate(errors).stringjoin(p->p, "\n"); //$NON-NLS-1$
			return false;
		} else {
			referror.Value = Str.Empty;
			return true;
		}
	}

	public int getCount() {
		return _db.getHistoryCount();
	}

	public void enableTrigger() throws SQLException {
		List<Tuple<String, String>> triggerDB = _db.listTrigger();

		List<Tuple<String, String>> triggerStatements = createTriggerStatements();

		for (Tuple<String, String> trigger : triggerStatements) {

			Tuple<String, String> dbMatch = CCStreams.iterate(triggerDB).firstOrNull(p -> p.Item1.equalsIgnoreCase(trigger.Item1));
			if (dbMatch != null) _db.deleteTrigger(dbMatch.Item1, false);

			_db.createTrigger(trigger.Item2);
		}

		_db.writeInformationToDB(DatabaseStructure.INFOKEY_HISTORY, "1"); //$NON-NLS-1$
	}

	public void disableTrigger() throws SQLException {
		List<Tuple<String, String>> triggerDB = _db.listTrigger();

		List<Tuple<String, String>> triggerStatements = createTriggerStatements();

		_db.writeInformationToDB(DatabaseStructure.INFOKEY_HISTORY, "0"); //$NON-NLS-1$

		for (Tuple<String, String> trigger : triggerStatements) {
			Tuple<String, String> dbMatch = CCStreams.iterate(triggerDB).firstOrNull(p -> p.Item1.equalsIgnoreCase(trigger.Item1));
			if (dbMatch != null) _db.deleteTrigger(dbMatch.Item1, false);
		}
	}

	public List<CCCombinedHistoryEntry> query(boolean excludeViewedOnly, boolean excludeInfoIDChanges, boolean excludeOrderingChanges, boolean mergeAggressive, CCDateTime start, ProgressCallbackListener lst) throws CCFormatException {
		return query(excludeViewedOnly, excludeInfoIDChanges, excludeOrderingChanges, mergeAggressive, start, lst, null);
	}

	public List<CCCombinedHistoryEntry> query(boolean excludeViewedOnly, boolean excludeInfoIDChanges, boolean excludeOrderingChanges, boolean mergeAggressive, CCDateTime start, ProgressCallbackListener lst, String idfilter) throws CCFormatException {
		if (lst == null) lst = new ProgressCallbackSink();

		List<CCCombinedHistoryEntry> result  = new ArrayList<>();
		List<CCCombinedHistoryEntry> backlog = new ArrayList<>();

		List<String[]> rawdata = _db.queryHistory(start, idfilter);
		lst.setMax(rawdata.size());
		for (String[] raw : rawdata) {
			lst.step();

			CCHistoryTable  table     = CCHistoryTable.getWrapper().findByTextOrException(raw[0]);
			String          id        = raw[1];
			CCDateTime      timestamp = CCDateTime.createFromUTCSQL(raw[2], TimeZone.getDefault());
			CCHistoryAction action    = CCHistoryAction.getWrapper().findByTextOrException(raw[3]);
			String          field     = raw[4];
			String          oldvalue  = raw[5];
			String          newvalue  = raw[6];

			if (table == CCHistoryTable.COVERS && Str.equals(field, "PREVIEW")) continue; //$NON-NLS-1$

			CCCombinedHistoryEntry base = CCStreams.iterate(backlog).reverse().firstOrNull(p -> shouldCombine(p, table, id, field, action, timestamp, oldvalue));
			if (base == null)
			{
				List<CCCombinedHistoryEntry> drop1 = CCStreams.iterate(backlog).filter(p -> p.Table == table && Str.equals(p.ID, id)).enumerate();
				backlog.removeAll(drop1);
				result.addAll(drop1);

				CCCombinedHistoryEntry newentry = new CCCombinedHistoryEntry();
				newentry.Table = table;
				newentry.ID = id;
				newentry.Timestamp1 = timestamp;
				newentry.Timestamp2 = timestamp;
				newentry.Action = action;
				newentry.Changes.add(new CCHistorySingleChange(field, oldvalue, newvalue));
				newentry.HistoryRowCount = 1;

				List<CCCombinedHistoryEntry> drop2 = CCStreams.iterate(backlog).filter(p -> CCDateTime.diffInSeconds(newentry.Timestamp1, p.Timestamp2)>MERGE_DIFF_TIME).enumerate();
				backlog.removeAll(drop2);
				result.addAll(drop2);

				backlog.add(newentry);
			}
			else
			{
				base.Timestamp2 = timestamp;

				CCHistorySingleChange basechange = CCStreams.iterate(base.Changes).firstOrNull(p -> Str.equals(p.Field, field));
				if (basechange != null && mergeAggressive)
				{
					basechange.NewValue = newvalue;
					if (base.Action == CCHistoryAction.REMOVE && action == CCHistoryAction.INSERT) base.Action = CCHistoryAction.UPDATE;
				}
				else
				{
					base.Changes.add(new CCHistorySingleChange(field, oldvalue, newvalue));
				}
				base.HistoryRowCount++;
			}
		}

		result.addAll(backlog);

		result.sort(Comparator.comparing(o -> o.Timestamp1));

		for (int i=1; i<result.size(); i++)
		{
			CCCombinedHistoryEntry e1 = result.get(i-1);
			CCCombinedHistoryEntry e2 = result.get(i);

 			if (mergeAggressive &&
 				e1.Table == e2.Table &&
				Str.equals(e1.ID, e2.ID) &&
				e1.Action == CCHistoryAction.REMOVE &&
				e2.Action == CCHistoryAction.INSERT &&
				e1.Changes.size() == e2.Changes.size() &&
				CCDateTime.diffInSeconds(e1.Timestamp1, e2.Timestamp2) < MERGE_SHORT_TIME)
			{
				CCCombinedHistoryEntry e3 = new CCCombinedHistoryEntry();
				e3.Table           = e1.Table;
				e3.ID              = e1.ID;
				e3.Timestamp1      = e1.Timestamp1;
				e3.Timestamp2      = e2.Timestamp2;
				e3.Action          = CCHistoryAction.UPDATE;
				e3.HistoryRowCount = e1.HistoryRowCount + e2.HistoryRowCount;
				boolean nomerge = false;
				for (CCHistorySingleChange c1 : e1.Changes) {
					Optional<CCHistorySingleChange> c2 = e2.Changes.stream().filter(c -> Str.equals(c.Field, c1.Field)).findFirst();
					if (!c2.isPresent()) { nomerge = true; break; }
					e3.Changes.add(new CCHistorySingleChange(c1.Field, c1.OldValue, c2.get().NewValue));
				}
				if (nomerge) continue;

				result.set(i-1, null);
				result.set(i, e3);
			}
		}
		result.removeIf(Objects::isNull);

		for (CCCombinedHistoryEntry e : result) {
			if (e.Action == CCHistoryAction.UPDATE) e.Changes.removeIf(p -> Str.equals(p.OldValue, p.NewValue));
		}

		result.removeIf(p -> p.Changes.size()==0);

		if (excludeViewedOnly)      result.removeIf(CCCombinedHistoryEntry::isTrivialViewedChangesOnly);
		if (excludeInfoIDChanges)   result.removeIf(CCCombinedHistoryEntry::isIDChangeOnly);
		if (excludeOrderingChanges) result.removeIf(CCCombinedHistoryEntry::isGroupOrderingChange);

		HashMap<Integer, ICCDatabaseStructureElement> elements = new HashMap<>();
		for (CCDatabaseElement e : CCMovieList.getInstance().iteratorElements()) elements.put(e.getLocalID(), e);
		for (CCSeason e : CCMovieList.getInstance().iteratorSeasons()) elements.put(e.getLocalID(), e);
		for (CCEpisode e : CCMovieList.getInstance().iteratorEpisodes()) elements.put(e.getLocalID(), e);

		for (CCCombinedHistoryEntry e : result) e.setSourceLink(elements);

		return result;
	}

	private boolean shouldCombine(CCCombinedHistoryEntry base, CCHistoryTable table, String id, String field, CCHistoryAction action, CCDateTime date, String oldValue)
	{
		if (base.Table != table) return false;
		if (!Str.equals(base.ID, id)) return false;

		boolean a_ii = (base.Action == CCHistoryAction.INSERT && action == CCHistoryAction.INSERT);
		boolean a_uu = (base.Action == CCHistoryAction.UPDATE && action == CCHistoryAction.UPDATE);
		boolean a_rr = (base.Action == CCHistoryAction.REMOVE && action == CCHistoryAction.REMOVE);
		boolean a_iu = (base.Action == CCHistoryAction.INSERT && action == CCHistoryAction.UPDATE);
		boolean a_ri = (base.Action == CCHistoryAction.REMOVE && action == CCHistoryAction.INSERT);

		if (a_ri)
		{
			if (! (table == CCHistoryTable.INFO && base.Changes.size() == 1 && Str.equals(base.Changes.get(0).Field, field))) return false;
		}
		else
		{
			if (!(a_ii || a_uu || a_rr || a_iu)) return false;
		}

		int diff = CCDateTime.diffInSeconds(base.Timestamp2, date);

		if (base.Action == CCHistoryAction.INSERT && Str.equals(field, "VIEWED")         && diff > MERGE_SHORT_TIME) return false; //$NON-NLS-1$
		if (base.Action == CCHistoryAction.INSERT && Str.equals(field, "VIEWED_HISTORY") && diff > MERGE_SHORT_TIME) return false; //$NON-NLS-1$

		if (diff > MERGE_DIFF_TIME) return false;
		if (CCDateTime.diffInSeconds(base.Timestamp1, date) > MERGE_MAX_TIME) return false;

		if (a_iu)
		{
			if (!(Str.isNullOrEmpty(oldValue) || oldValue.equals("0") || oldValue.equals("-1") || oldValue.equals("1900-01-01"))) return false; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		return true;
	}
}
