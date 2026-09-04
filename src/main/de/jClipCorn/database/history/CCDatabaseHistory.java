package de.jClipCorn.database.history;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.database.driver.DatabaseStructure;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datatypes.Tuple3;
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
	private final static int MERGE_DIFF_TIME        = 15 * 60; // sec
	private final static int MERGE_MAX_TIME         = 45 * 60; // sec
	private final static int MERGE_MAX_LOOKBHEHIND  = 512;
	private final static int MERGE_AGGR_SHORT_TIME  = 8;       // sec
	private final static int MERGE_AGGR_LOOKAHEAD   = 10;

	private final CCDatabase _db;

	public CCDatabaseHistory(CCDatabase db) {
		_db = db;
	}

	public boolean isHistoryActive() {
		String str = _db.readUserDataInformationFromDB(DatabaseStructure.INFOKEY_HISTORY, "MISSING_ENTRY"); //$NON-NLS-1$
		if ("0".equals(str)) return false; //$NON-NLS-1$
		if ("1".equals(str)) return true;  //$NON-NLS-1$

		CCLog.addError("Invalid value in Database for [INFOKEY_HISTORY]: '" + str + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		return false;
	}

	private final static List<CCSQLTableDef> UNTRACKED_TABLES = List.of(
			DatabaseStructure.TAB_HISTORY,    DatabaseStructure.TAB_UD_HISTORY,
			DatabaseStructure.TAB_TEMP,       DatabaseStructure.TAB_UD_TEMP,
			DatabaseStructure.TAB_FILTERS,    DatabaseStructure.TAB_PROPERTIES);

	/**
	 * These rows exist only while some user property differs from its default - creating or dropping
	 * one is an update of the element, not an addition/removal of the element.
	 */
	private final static List<CCSQLTableDef> SPARSE_TABLES = List.of(
			DatabaseStructure.TAB_UD_MOVIES,  DatabaseStructure.TAB_UD_SERIES,
			DatabaseStructure.TAB_UD_SEASONS, DatabaseStructure.TAB_UD_EPISODES);

	public static List<Tuple3<String, String, String>> createTriggerStatements() {
		List<Tuple3<String, String, String>> result = new ArrayList<>();

		for(CCSQLTableDef tab : CCStreams.iterate(DatabaseStructure.TABLES_MAIN).append(DatabaseStructure.TABLES_USERDATA)) {
			if (UNTRACKED_TABLES.contains(tab)) continue;

			result.add(createTriggerOnAdd(tab));
			for(CCSQLColDef col : tab.Columns) result.add(createTriggerOnUpdate(tab, col));
			result.add(createTriggerOnDelete(tab));
		}

		return result;
	}

	/**
	 * SQLite drops the schema qualifier again when it stores the DDL, so the statement that has to be
	 * executed and the statement {@link #testTrigger} later compares against are not the same string.
	 */
	private static Tuple3<String, String, String> withSchema(String name, CCSQLTableDef tab, String sql) {
		String prefix = "CREATE TRIGGER "; //$NON-NLS-1$
		return Tuple3.Create(name, prefix + tab.Schema + "." + sql.substring(prefix.length()), sql); //$NON-NLS-1$
	}

	/** The trigger name has to encode the schema - both files hold a MOVIES table. */
	private static String triggerName(String action, CCSQLTableDef tab, String suffix) {
		return Str.format("JCCTRIGGER_AUTOHISTORY_{0}_{1}_{2}{3}", action, tab.Schema.toUpperCase(), tab.Name.toUpperCase(), suffix); //$NON-NLS-1$
	}

	/**
	 * The value {@code field} was born with, as recorded by the ADD trigger (which writes OLD=NULL),
	 * or null if this entry holds no insert for it. Never a literal list of defaults - whatever
	 * {@code CCDatabase.addEmptyXRow} writes has to stay mergeable without a second place to update.
	 */
	private static String insertedValue(CCCombinedHistoryEntry base, String field) {
		CCHistorySingleChange c = CCStreams.iterate(base.Changes).firstOrNull(p -> Str.equals(p.Field, field) && p.OldValue == null);
		return (c == null) ? null : c.NewValue;
	}

	/** The value a missing sparse row stands for, or null for columns that are not part of one. */
	@SuppressWarnings("nls")
	private static String sparseDefault(String column) {
		switch (column) {
			case "VIEWED_HISTORY": return CCDateTimeList.createEmpty().asJSONArray();
			case "TAGS":           return CCTagList.EMPTY.asJSONArray();
			case "SCORE":          return Integer.toString(CCUserScore.RATING_NO.asInt());
			case "SCORECOMMENT":   return Str.Empty;
			default:               return null;
		}
	}

	@SuppressWarnings("nls")
	private static String sparseDefaultSQL(CCSQLColDef col) {
		String value = sparseDefault(col.Name);
		if (value == null) throw new Error("Unknown sparse default value for column " + col.Name);

		if (col.Type.isCallableAsInteger()) return value;

		return "'" + value.replace("'", "''") + "'";
	}

	@SuppressWarnings("nls")
	private static Tuple3<String, String, String> createTriggerOnAdd(CCSQLTableDef tab) {
		String triggerName = triggerName("ADD", tab, Str.Empty); //$NON-NLS-1$

		boolean sparse = SPARSE_TABLES.contains(tab);

		StringBuilder triggerbuilder = new StringBuilder();

		triggerbuilder.append("CREATE TRIGGER ").append(forceSQLEscape(triggerName)).append(" AFTER INSERT ON ").append(forceSQLEscape(tab.Name)).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		triggerbuilder.append("BEGIN").append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
		for (CCSQLColDef col : tab.getNonPrimaryColumns()) {
			triggerbuilder
					.append(" INSERT INTO HISTORY (").append("`TABLE`, `ID`, `DATE`, `ACTION`, `FIELD`, `OLD`, `NEW`").append(") VALUES (") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					.append("'").append(tab.Name).append("', ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("NEW.").append(forceSQLEscape(tab.Primary.Name)).append(", ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW'), ") //$NON-NLS-1$
					.append(sparse ? "'UPDATE', " : "'ADD', ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("'").append(col.Name).append("', ") //$NON-NLS-1$ //$NON-NLS-2$
					.append(sparse ? sparseDefaultSQL(col) : "NULL").append(", ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("NEW.").append(forceSQLEscape(col.Name)) //$NON-NLS-1$
					.append(");\n"); //$NON-NLS-1$
		}
		triggerbuilder.append("END").append("\n"); //$NON-NLS-1$ //$NON-NLS-2$

		return withSchema(triggerName, tab, triggerbuilder.toString());
	}

	@SuppressWarnings("nls")
	private static Tuple3<String, String, String> createTriggerOnUpdate(CCSQLTableDef tab, CCSQLColDef col) {
		String triggerName = triggerName("UPD", tab, "_" + col.Name.toUpperCase().replace('.', '-')); //$NON-NLS-1$ //$NON-NLS-2$

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

		return withSchema(triggerName, tab, triggerbuilder.toString());
	}

	@SuppressWarnings("nls")
	private static Tuple3<String, String, String> createTriggerOnDelete(CCSQLTableDef tab) {
		String triggerName = triggerName("REM", tab, Str.Empty); //$NON-NLS-1$

		boolean sparse = SPARSE_TABLES.contains(tab);

		StringBuilder triggerbuilder = new StringBuilder();

		triggerbuilder.append("CREATE TRIGGER ").append(forceSQLEscape(triggerName)).append(" BEFORE DELETE ON ").append(forceSQLEscape(tab.Name)).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		triggerbuilder.append("BEGIN").append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
		for (CCSQLColDef col : tab.getNonPrimaryColumns()) {
			triggerbuilder
					.append(" INSERT INTO HISTORY (").append("`TABLE`, `ID`, `DATE`, `ACTION`, `FIELD`, `OLD`, `NEW`").append(") VALUES (") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					.append("'").append(tab.Name).append("', ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("OLD.").append(forceSQLEscape(tab.Primary.Name)).append(", ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW'), ") //$NON-NLS-1$
					.append(sparse ? "'UPDATE', " : "'DELETE', ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("'").append(col.Name).append("', ") //$NON-NLS-1$ //$NON-NLS-2$
					.append("OLD.").append(forceSQLEscape(col.Name)).append(", ") //$NON-NLS-1$ //$NON-NLS-2$
					.append(sparse ? sparseDefaultSQL(col) : "NULL") //$NON-NLS-1$
					.append(");\n"); //$NON-NLS-1$
		}
		triggerbuilder.append("END").append("\n"); //$NON-NLS-1$ //$NON-NLS-2$

		return withSchema(triggerName, tab, triggerbuilder.toString());
	}

	@SuppressWarnings("nls")
	public boolean testTrigger(boolean active, RefParam<String> referror) {

		List<String> errors = new ArrayList<>();

		try {
			List<Tuple<String, String>> triggerDB = _db.listTrigger();
			List<Tuple3<String, String, String>> triggerOK = createTriggerStatements();

			if (active) {
				for (Tuple3<String, String, String> t : triggerOK) {
					Tuple<String, String> db = CCStreams.iterate(triggerDB).singleOrNull(p -> Str.equals(p.Item1, t.Item1));
					if (db == null)
						errors.add(Str.format("Trigger [{0}] not found", t.Item1)); //$NON-NLS-1$
					else if (!Str.equals(db.Item2.replace("\r", "").trim(), t.Item3.replace("\r", "").trim())) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						errors.add(Str.format("Trigger [{0}] has wrong code", t.Item1)); //$NON-NLS-1$
				}
			} else {
				for (Tuple3<String, String, String> t : triggerOK) {
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

		// drop all existing auto-history triggers
		for (Tuple<String, String> dbTrigger : triggerDB) {
			if (dbTrigger.Item1.startsWith("JCCTRIGGER_")) _db.deleteTrigger(dbTrigger.Item1, false); //$NON-NLS-1$
		}

		for (Tuple3<String, String, String> trigger : createTriggerStatements()) {
			_db.createTrigger(trigger.Item2);
		}

		_db.writeUserDataInformationToDB(DatabaseStructure.INFOKEY_HISTORY, "1"); //$NON-NLS-1$
		_db.getHistoryDatabase().writeInfo(DatabaseStructure.INFOKEY_HISTORY, "1"); //$NON-NLS-1$
	}

	public void disableTrigger() throws SQLException {
		List<Tuple<String, String>> triggerDB = _db.listTrigger();

		_db.writeUserDataInformationToDB(DatabaseStructure.INFOKEY_HISTORY, "0"); //$NON-NLS-1$
		_db.getHistoryDatabase().writeInfo(DatabaseStructure.INFOKEY_HISTORY, "0"); //$NON-NLS-1$

		// drop all existing auto-history triggers
		for (Tuple<String, String> dbTrigger : triggerDB) {
			if (dbTrigger.Item1.startsWith("JCCTRIGGER_")) _db.deleteTrigger(dbTrigger.Item1, false); //$NON-NLS-1$
		}
	}

	public Tuple<List<CCCombinedHistoryEntry>, Integer> query(CCMovieList ml, boolean excludeViewedOnly, boolean excludeOrderingChanges, boolean mergeAggressive, CCDateTime start, Opt<Integer> limit, ProgressCallbackListener lst) throws CCFormatException {
		return query(ml, excludeViewedOnly, excludeOrderingChanges, mergeAggressive, start, limit, lst, null);
	}

	public Tuple<List<CCCombinedHistoryEntry>, Integer> query(CCMovieList ml, boolean excludeViewedOnly, boolean excludeOrderingChanges, boolean mergeAggressive, CCDateTime start, Opt<Integer> limit, ProgressCallbackListener lst, String idfilter) throws CCFormatException {
		if (lst == null) lst = new ProgressCallbackSink();

		List<CCCombinedHistoryEntry> result  = new ArrayList<>();
		List<CCCombinedHistoryEntry> backlog = new ArrayList<>();

		List<String[]> rawdata = _db.queryHistory(start, limit, idfilter);

		// the query returns newest-first (so a limit keeps the newest rows), the merge walks oldest-first
		rawdata = CCStreams.iterate(rawdata).reverse().autosortByProperty(p -> p[2]).enumerate();

		lst.setMax( rawdata.size() + (mergeAggressive?rawdata.size():0) );
		for (String[] raw : rawdata) {
			lst.step();

			CCHistoryTable  table     = CCHistoryTable.getWrapper().findByTextOrNull(raw[0]);
			if (table == null) continue; // skip history entries of other tables

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
				// remove previous entries with the same [TABLE+ID] (= same row)
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

				// remove previous entries that are too old and cann no longer be merged realistically
				List<CCCombinedHistoryEntry> drop2 = CCStreams.iterate(backlog).filter(p -> CCDateTime.diffInSeconds(p.Timestamp2, newentry.Timestamp1)>MERGE_DIFF_TIME).enumerate();
				backlog.removeAll(drop2);
				result.addAll(drop2);

				// remove old entries until backlog is at most $MERGE_MAX_LOOKBHEHIND entries big (improves calc time)
				if (backlog.size() > MERGE_MAX_LOOKBHEHIND) backlog = backlog.subList(backlog.size() - MERGE_MAX_LOOKBHEHIND, backlog.size());

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

		if (mergeAggressive)
		{
			// Merge [REM]+[INS]
			for (int bi=0; bi<result.size()-1; bi++)
			{
				CCCombinedHistoryEntry e1 = result.get(bi);
				if (e1 == null) continue;

				lst.step(e1.HistoryRowCount);

				if (e1.Action != CCHistoryAction.REMOVE) continue;

				for (int la = 1; la <= MERGE_AGGR_LOOKAHEAD; la++)
				{
					if (bi+la >= result.size()) continue;

					CCCombinedHistoryEntry e2 = result.get(bi+la);
					if (e2 == null) continue;

					if (e1.Table == e2.Table &&
						e1.Action == CCHistoryAction.REMOVE &&
						e2.Action == CCHistoryAction.INSERT &&
						Str.equals(e1.ID, e2.ID) &&
						e1.Changes.size() == e2.Changes.size() &&
						CCDateTime.diffInSeconds(e1.Timestamp1, e2.Timestamp2) < MERGE_AGGR_SHORT_TIME)
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
							if (c2.isEmpty()) { nomerge = true; break; }
							e3.Changes.add(new CCHistorySingleChange(c1.Field, c1.OldValue, c2.get().NewValue));
						}
						if (!nomerge)
						{
							result.set(bi, e3);
							result.set(bi+la, null);
							break;
						}
					}
				}
			}
		}

		result.removeIf(Objects::isNull);

		for (CCCombinedHistoryEntry e : result) {
			if (e.Action == CCHistoryAction.UPDATE) e.Changes.removeIf(p -> Str.equals(p.OldValue, p.NewValue));
		}

		result.removeIf(p -> p.Changes.size()==0);

		if (excludeViewedOnly)      result.removeIf(CCCombinedHistoryEntry::isTrivialViewedChangesOnly);
		if (excludeOrderingChanges) result.removeIf(CCCombinedHistoryEntry::isGroupOrderingChange);

		HashMap<CCUUID, ICCDatabaseStructureElement> elements = new HashMap<>();
		for (CCDatabaseElement e : ml.iteratorElements()) elements.put(e.getID(), e);
		for (CCSeason e : ml.iteratorSeasons()) elements.put(e.getID(), e);
		for (CCEpisode e : ml.iteratorEpisodes()) elements.put(e.getID(), e);

		for (CCCombinedHistoryEntry e : result) e.setSourceLink(elements);

		lst.stepToMax();


		return Tuple.Create(result, rawdata.size());
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

			return true;
		}
		else if (a_ii || a_uu || a_rr || a_iu)
		{
			int diff = CCDateTime.diffInSeconds(base.Timestamp2, date);

			if (base.Action == CCHistoryAction.INSERT && Str.equals(field, "VIEWED")         && diff > MERGE_AGGR_SHORT_TIME) return false; //$NON-NLS-1$
			if (base.Action == CCHistoryAction.INSERT && Str.equals(field, "VIEWED_HISTORY") && diff > MERGE_AGGR_SHORT_TIME) return false; //$NON-NLS-1$

			if (diff > MERGE_DIFF_TIME) return false;
			if (CCDateTime.diffInSeconds(base.Timestamp1, date) > MERGE_MAX_TIME) return false;

			if (a_iu)
			{
				// a row is born holding placeholder values, so an update away from one still belongs to the insert
				if (!(Str.isNullOrEmpty(oldValue) || Str.equals(oldValue, insertedValue(base, field)) || Str.equals(oldValue, sparseDefault(field)))) return false;
			}

			return true;
		}
		else
		{
			return false;
		}

	}
}
