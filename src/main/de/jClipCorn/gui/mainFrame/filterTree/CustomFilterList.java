package de.jClipCorn.gui.mainFrame.filterTree;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.table.filter.customFilter.operators.CustomAndOperator;
import de.jClipCorn.features.table.filter.customFilter.operators.CustomOperator;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.datatypes.Tuple4;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomFilterList extends ArrayList<CustomFilterObject> {
	private static final long serialVersionUID = -1658394481177986689L;

	public static String NAME_TEMPORARY = "{{temporary}}"; //$NON-NLS-1$

	private final CCMovieList movielist;

	private int maxId = 0;

	// snapshot of the database state at the time of the last load()/save(), keyed by filter id -> (name, definition)
	private final Map<Integer, Tuple<String, String>> dbSnapshot = new LinkedHashMap<>();

	public CustomFilterList(CCMovieList ml) {
		movielist = ml;
	}

	public void save() {
		if (movielist.isReadonly()) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			return;
		}

		List<Tuple4<Integer, Integer, String, String>> rows = new ArrayList<>();
		Map<Integer, Tuple<String, String>> newState = new LinkedHashMap<>();

		for (int i = 0; i < size(); i++) {
			CustomFilterObject fo = get(i);

			if (fo.getID() <= 0) fo.setID(++maxId);

			String def = fo.getFilter().exportToString();

			rows.add(Tuple4.Create(fo.getID(), i, fo.getName(), def));
			newState.put(fo.getID(), Tuple.Create(fo.getName(), def));
		}

		movielist.writeCustomFilterRows(rows);

		logChanges(newState);

		// the new state is now the persisted database state
		dbSnapshot.clear();
		dbSnapshot.putAll(newState);
	}

	public void load() {
		clear();
		maxId = 0;
		dbSnapshot.clear();

		for (Tuple3<Integer, String, String> row : movielist.getCustomFilterRows()) {
			int id         = row.Item1;
			String name    = row.Item2;
			String def     = row.Item3;

			CustomOperator op = new CustomAndOperator(movielist);
			if (op.importFromString(def)) {
				add(new CustomFilterObject(id, name, op));
				if (id > maxId) maxId = id;
				dbSnapshot.put(id, Tuple.Create(name, def));
			} else {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotParseFilterList", size()+1)); //$NON-NLS-1$
			}
		}

		CCLog.addDebug(size() + " Filters loaded from Database"); //$NON-NLS-1$
	}

	/**
	 * Diffs the new filter state against the snapshot of the persisted database state and writes one
	 * transaction-log line per added/changed/deleted filter (including the previous and new value).
	 */
	private void logChanges(Map<Integer, Tuple<String, String>> newState) {
		for (Map.Entry<Integer, Tuple<String, String>> e : newState.entrySet()) {
			int id = e.getKey();
			Tuple<String, String> nw  = e.getValue();
			Tuple<String, String> old = dbSnapshot.get(id);

			if (old == null) {
				movielist.logCustomFilterAdded(id, nw.Item1, fmt(nw));
			} else if (!equalsState(old, nw)) {
				movielist.logCustomFilterChanged(id, nw.Item1, fmt(old), fmt(nw));
			}
		}

		for (Map.Entry<Integer, Tuple<String, String>> e : dbSnapshot.entrySet()) {
			if (!newState.containsKey(e.getKey())) {
				Tuple<String, String> old = e.getValue();
				movielist.logCustomFilterDeleted(e.getKey(), old.Item1, fmt(old));
			}
		}
	}

	private static boolean equalsState(Tuple<String, String> a, Tuple<String, String> b) {
		return a.Item1.equals(b.Item1) && a.Item2.equals(b.Item2);
	}

	private static String fmt(Tuple<String, String> t) {
		return t.Item1 + "\t" + t.Item2; //$NON-NLS-1$
	}
}
