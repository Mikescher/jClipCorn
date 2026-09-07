package de.jClipCorn.features.statistics.snapshots;

import de.jClipCorn.util.lambda.Func1to0;

import java.util.List;
import java.util.Map;

/**
 * Everything {@link StatSnapshotRebuilder} needs from the outside world.
 *
 * Two implementations: one that runs inside the schema migration (raw SQL, no CCMovieList, a temporary
 * connection to the archive) and one that runs in the live application.
 */
public interface IStatSnapshotEnv {

	/** The current state of every tracked element, by kind and id. */
	Map<StatClass, Map<String, StatSnapshotRawRow>> readCurrentState() throws Exception;

	/**
	 * Feeds the consumer every relevant change, newest first, as
	 * {@code [TABLE, ID, DATE, ACTION, FIELD, OLD, NEW]}. Emits nothing when there is no usable archive.
	 */
	void streamHistory(Func1to0<String[]> consumer) throws Exception;

	/** UTC SQL timestamp of the oldest row in the archive, or null when there is none. */
	String earliestHistoryTimestamp() throws Exception;

	/** Replaces the whole table - a rebuild is always a full replacement, and therefore idempotent. */
	void writeSnapshots(List<CCStatSnapshot> rows) throws Exception;
}
