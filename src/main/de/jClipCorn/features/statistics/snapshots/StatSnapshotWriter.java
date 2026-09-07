package de.jClipCorn.features.statistics.snapshots;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.adapter.CCDBUpdateAdapter;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.helper.Debouncer;

import java.util.Map;

/**
 * Keeps main.STATSNAPSHOTS current while the application runs.
 *
 * Writes are debounced: a change schedules the update five minutes out and every further change pushes
 * that back, so a bulk edit or a folder scan costs one write instead of hundreds. The listeners fire on
 * the EDT and only reschedule; the write itself happens on the debouncer's own thread.
 */
public class StatSnapshotWriter {

	private static final long DEBOUNCE_MILLIS = 5 * 60 * 1000L;

	private final CCDatabase _db;
	private final Debouncer  _debouncer;

	public StatSnapshotWriter(CCMovieList movielist, CCDatabase db) {
		_db        = db;
		_debouncer = new Debouncer("THREAD_STAT_SNAPSHOT_DEBOUNCE", DEBOUNCE_MILLIS, this::updateTodaySafe); //$NON-NLS-1$

		movielist.addChangeListener(new CCDBUpdateAdapter() {
			@Override public void onAddDatabaseElement(CCDatabaseElement el) { _debouncer.trigger(); }
			@Override public void onAddSeason(CCSeason el)                   { _debouncer.trigger(); }
			@Override public void onAddEpisode(CCEpisode el)                 { _debouncer.trigger(); }
			@Override public void onRemDatabaseElement(CCDatabaseElement el) { _debouncer.trigger(); }
			@Override public void onRemSeason(CCSeason el)                   { _debouncer.trigger(); }
			@Override public void onRemEpisode(CCEpisode el)                 { _debouncer.trigger(); }

			@Override
			public void onChangeDatabaseElement(CCDatabaseElement root, ICCDatabaseStructureElement actual, String[] props) {
				// nearly every property feeds some dimension, so there is nothing worth filtering here -
				// the debounce window is what keeps this cheap
				_debouncer.trigger();
			}
		});
	}

	/** Flushes a pending write and stops - has to run before the database connection is closed. */
	public void shutdown() {
		_debouncer.flushAndClose(5000);
	}

	private void updateTodaySafe() {
		try {
			updateToday();
		} catch (Exception e) {
			CCLog.addWarning("[SNAPSHOT] Failed to update today's statistics snapshot", e); //$NON-NLS-1$
		}
	}

	/**
	 * Records today's totals - as an update of today's row or as a new one. Nothing is written when the
	 * values match the newest row: the reader carries values forward, so an identical row is pure noise.
	 */
	public void updateToday() throws Exception {
		if (_db.isReadonly()) return;

		StatSnapshotAccumulator acc = new StatSnapshotAccumulator();
		for (Map.Entry<StatClass, Map<String, StatSnapshotRawRow>> e : StatSnapshotStateReader.readCurrentState(_db.getInternalDatabaseAccess()).entrySet()) {
			for (Map.Entry<String, StatSnapshotRawRow> r : e.getValue().entrySet()) acc.addRow(e.getKey(), r.getKey(), r.getValue());
		}

		CCDate         today = CCDate.getCurrentDate();
		CCStatSnapshot now   = acc.snapshot(today, true);
		CCStatSnapshot last  = _db.readLastStatSnapshot();

		if (last != null && last.Date.isGreaterThan(today)) {
			CCLog.addWarning("[SNAPSHOT] The newest statistics snapshot (" + last.Date.toStringSQL() + ") lies in the future"); //$NON-NLS-1$
		}

		if (last != null && last.valuesEqual(now)) return;

		_db.writeStatSnapshot(now);
	}
}
