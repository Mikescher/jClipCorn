package de.jClipCorn.features.statistics.snapshots;

import de.jClipCorn.util.Str;

/**
 * The four element kinds a snapshot aggregates separately.
 *
 * {@link #TableName} is the value the history triggers write into {@code HISTORY.TABLE}. Note that the
 * userdata tables carry the *same* name as their main-schema counterparts (TAB_UD_MOVIES.Name is
 * literally "MOVIES"), so a history row only says which element kind it belongs to - which schema it
 * came from is implied by its FIELD, and the two column-name sets are disjoint.
 */
public enum StatClass {
	MOVIE  ("MOVIES"),   //$NON-NLS-1$
	SERIES ("SERIES"),   //$NON-NLS-1$
	SEASON ("SEASONS"),  //$NON-NLS-1$
	EPISODE("EPISODES"); //$NON-NLS-1$

	public final String TableName;

	StatClass(String tableName) {
		TableName = tableName;
	}

	/** Whether elements of this kind carry a file, i.e. contribute to the byte and minute totals. */
	public boolean isPlayable() {
		return this == MOVIE || this == EPISODE;
	}

	public static StatClass byTableName(String name) {
		for (StatClass c : values()) if (Str.equals(c.TableName, name)) return c;
		return null;
	}
}
