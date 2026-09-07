package de.jClipCorn.database.migration;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.statistics.snapshots.MigrationSnapshotEnv;
import de.jClipCorn.features.statistics.snapshots.StatSnapshotRebuilder;
import de.jClipCorn.util.filesystem.FSPath;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds main.STATSNAPSHOTS - a daily time-series of the collection - and fills it retroactively.
 *
 * The "over time" charts used to bucket every element at its add-date and add its *current* value, so a
 * movie re-encoded from 4 GB to 1 GB looked as if it had always been 1 GB and a deleted element was never
 * there at all. The snapshot table records what was actually true on each day instead.
 *
 * The retroactive fill replays the change archive (ClipCornHistory.db) backwards from the current state.
 * Days from the oldest archived change onward are exact; older days cannot be reconstructed, so for those
 * the state at the cutoff is back-projected using the add-dates and flagged EXACT=0.
 *
 * Unlike the other migrations this one calls into shared, non-frozen code (the snapshots package and the
 * domain enums it decodes with). That is deliberate: STATSNAPSHOTS holds derived data, so re-running the
 * rebuild later under a newer interpretation is correct, not a corruption.
 */
public class Migration_37_38 extends DBMigration {

	/**
	 * Frozen copy of what SQLBuilder.createSchema(TAB_STATSNAPSHOTS) emits today - deriving it live would
	 * make this migration create whatever shape a *later* migration defines, which that migration's own
	 * ALTER TABLE would then collide with. TestStatSnapshotMigration asserts the two still agree.
	 */
	@SuppressWarnings("nls")
	private static final String CREATE_SQL =
			"CREATE TABLE main.STATSNAPSHOTS(" +
			"[DATE] DATE NOT NULL PRIMARY KEY," +
			"EXACT BIT NOT NULL," +
			"MOV_COUNT INTEGER NOT NULL,MOV_BYTES BIGINT NOT NULL,MOV_MINUTES INTEGER NOT NULL," +
			"SER_COUNT INTEGER NOT NULL,SEA_COUNT INTEGER NOT NULL," +
			"EPI_COUNT INTEGER NOT NULL,EPI_BYTES BIGINT NOT NULL,EPI_MINUTES INTEGER NOT NULL," +
			"MOV_HISTOGRAMS TEXT NOT NULL,SER_HISTOGRAMS TEXT NOT NULL,SEA_HISTOGRAMS TEXT NOT NULL,EPI_HISTOGRAMS TEXT NOT NULL)";

	public Migration_37_38(GenericDatabase db, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "37"; //$NON-NLS-1$
	}

	@Override
	public String getToVersion() {
		return "38"; //$NON-NLS-1$
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return false; // nothing is dropped, renamed or rebuilt, and the new table is untracked
	}

	@Override
	@SuppressWarnings("nls")
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[UPGRADE v37 -> v38] Create main.STATSNAPSHOTS");

		db.executeSQLThrow(CREATE_SQL);

		CCLog.addInformation("[UPGRADE v37 -> v38] Rebuild the statistics snapshots (this can take a while)");

		new StatSnapshotRebuilder(new MigrationSnapshotEnv(db, databaseDirectory, databaseName)).rebuild(null);

		return new ArrayList<>();
	}
}
