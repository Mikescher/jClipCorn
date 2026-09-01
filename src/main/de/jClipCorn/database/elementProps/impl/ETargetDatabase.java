package de.jClipCorn.database.elementProps.impl;

/**
 * Which of the two database files a property is stored in.
 *
 * Deliberately not a function of {@link EPropertyType}: {@code GROUPS} and {@code ADDDATE} are
 * user metadata but are shared, and the subjective file references ({@code PARTS}, {@code PART1},
 * {@code MEDIAINFO.CDATE/MDATE}) are shared too - resolving a shared {@code CCPath} against the
 * local filesystem is the job of the hostname-scoped path variables.
 */
public enum ETargetDatabase {
	MAIN,
	USERDATA,
	BOTH;

	public static ETargetDatabase getDefaultFor(EPropertyType t) {
		switch (t) {
			case USER_METADATA:             return USERDATA;
			case DATABASE_PRIMARY_ID:       return BOTH;
			case OBJECTIVE_METADATA:
			case LOCAL_FILE_REF_SUBJECTIVE:
			case LOCAL_FILE_REF_OBJECTIVE:
			case DATABASE_REF:
			case DATABASE_READONLY:         return MAIN;
		}

		throw new Error("Unknown EPropertyType := " + t); //$NON-NLS-1$
	}
}
