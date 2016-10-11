package de.jClipCorn.database.databaseErrors;

import de.jClipCorn.gui.localization.LocaleBundle;

public class DatabaseErrorType {
	public static DatabaseErrorType ERROR_INCONTINUOUS_GENRELIST = new DatabaseErrorType(1);
	public static DatabaseErrorType ERROR_WRONG_GENREID = new DatabaseErrorType(2);
	public static DatabaseErrorType ERROR_WRONG_FILESIZE = new DatabaseErrorType(3);
	public static DatabaseErrorType ERROR_NOGENRE_SET = new DatabaseErrorType(4);
	public static DatabaseErrorType ERROR_NOCOVERSET = new DatabaseErrorType(5);
	public static DatabaseErrorType ERROR_COVER_NOT_FOUND = new DatabaseErrorType(6);
	public static DatabaseErrorType ERROR_TITLE_NOT_SET = new DatabaseErrorType(7);
	public static DatabaseErrorType ERROR_ZYKLUSNUMBER_IS_NEGONE = new DatabaseErrorType(8);
	public static DatabaseErrorType ERROR_ZYKLUSTITLE_IS_EMPTY = new DatabaseErrorType(9);
	public static DatabaseErrorType ERROR_INCONTINUOUS_PARTS = new DatabaseErrorType(10);
	public static DatabaseErrorType ERROR_FORMAT_NOT_FOUND_IN_PARTS = new DatabaseErrorType(11);
	public static DatabaseErrorType ERROR_PATH_NOT_FOUND = new DatabaseErrorType(12);
	public static DatabaseErrorType ERROR_WRONG_ADDDATE = new DatabaseErrorType(13);
	public static DatabaseErrorType ERROR_WRONG_LASTVIEWEDDATE = new DatabaseErrorType(14);
	public static DatabaseErrorType ERROR_LASTVIEWED_AND_ISVIEWED_IS_PARADOX = new DatabaseErrorType(15);
	public static DatabaseErrorType ERROR_NOT_TRIMMED = new DatabaseErrorType(16);
	public static DatabaseErrorType ERROR_DUPLICATE_COVERLINK = new DatabaseErrorType(17);
	public static DatabaseErrorType ERROR_ZYKLUS_ENDS_WITH_ROMAN = new DatabaseErrorType(18);
	public static DatabaseErrorType ERROR_WRONG_QUALITY = new DatabaseErrorType(19);
	public static DatabaseErrorType ERROR_DUPLICATE_TITLE = new DatabaseErrorType(20);
	public static DatabaseErrorType ERROR_DUPLICATE_FILELINK = new DatabaseErrorType(21);
	public static DatabaseErrorType ERROR_WRONG_FILENAME = new DatabaseErrorType(22);
	public static DatabaseErrorType ERROR_NONLINKED_COVERFILE = new DatabaseErrorType(23);
	public static DatabaseErrorType ERROR_IMPOSSIBLE_WATCH_LATER = new DatabaseErrorType(24);
	public static DatabaseErrorType ERROR_LASTWATCHED_TOO_OLD = new DatabaseErrorType(25);
	public static DatabaseErrorType ERROR_INVALID_SERIES_STRUCTURE = new DatabaseErrorType(26);
	public static DatabaseErrorType ERROR_IMPOSSIBLE_WATCH_NEVER = new DatabaseErrorType(27);
	public static DatabaseErrorType ERROR_DUPLICATE_GENRE = new DatabaseErrorType(28);
	public static DatabaseErrorType ERROR_INVALID_CHARS_IN_PATH = new DatabaseErrorType(29);
	public static DatabaseErrorType ERROR_INVALID_ONLINEREF = new DatabaseErrorType(30);
	//public static DatabaseErrorType ERROR_SERIES_HAS_HISTORY = new DatabaseErrorType(31);
	public static DatabaseErrorType ERROR_HISTORY_BUT_UNVIEWED = new DatabaseErrorType(32);
	public static DatabaseErrorType ERROR_INVALID_HISTORY = new DatabaseErrorType(33);
	public static DatabaseErrorType ERROR_INVALID_GROUPLIST = new DatabaseErrorType(34);
	public static DatabaseErrorType ERROR_INVALID_GROUP = new DatabaseErrorType(35);
	public static DatabaseErrorType ERROR_DUPLICATE_REF = new DatabaseErrorType(36);
	public static DatabaseErrorType ERROR_VIEWED_BUT_NO_HISTORY = new DatabaseErrorType(37);

	private final int type;
	private int count = 0;

	public DatabaseErrorType(int ptype) {
		this.type = ptype;
	}

	public DatabaseErrorType(DatabaseErrorType ptype) {
		this.type = ptype.getType();
		this.count = ptype.count;
	}

	public int getType() {
		return type;
	}

	@Override
	public boolean equals(Object e) {
		return e instanceof DatabaseErrorType && ((DatabaseErrorType) e).getType() == this.getType();
	}

	@Override
	public int hashCode() {
		return type;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		if (count == 0) {
			return LocaleBundle.getString(String.format("CheckDatabaseDialog.Errornames.ERR_%02d", type));
		} else {
			return LocaleBundle.getString(String.format("CheckDatabaseDialog.Errornames.ERR_%02d", type)) + " (" + count + ")";
		}
	}

	public boolean isAutoFixable() {
		if (equals(ERROR_INCONTINUOUS_GENRELIST)) {
			return true;
		} else if (equals(ERROR_WRONG_FILESIZE)) {
			return true;
		} else if (equals(ERROR_INCONTINUOUS_PARTS)) {
			return true;
		} else if (equals(ERROR_FORMAT_NOT_FOUND_IN_PARTS)) {
			return true;
		} else if (equals(ERROR_NOT_TRIMMED)) {
			return true;
		} else if (equals(ERROR_WRONG_QUALITY)) {
			return true;
		} else if (equals(ERROR_WRONG_FILENAME)) {
			return true;
		} else if (equals(ERROR_IMPOSSIBLE_WATCH_LATER)) {
			return true;
		} else if (equals(ERROR_IMPOSSIBLE_WATCH_NEVER)) {
			return true;
		} else if (equals(ERROR_DUPLICATE_GENRE)) {
			return true;
		} else if (equals(ERROR_INVALID_CHARS_IN_PATH)) {
			return true;
		}

		return false;
	}

	public void incCount() {
		count++;
	}

	public void setCount(int c) {
		count = c;
	}

	public int getCount() {
		return count;
	}

	public DatabaseErrorType copy(int pcount) {
		DatabaseErrorType det = new DatabaseErrorType(this);
		det.setCount(pcount);
		return det;
	}

	public DatabaseErrorType copy() {
		return new DatabaseErrorType(this);
	}
}
