package de.jClipCorn.database.databaseErrors;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.lambda.Func1to1;

public class DatabaseErrorType {
	public static DatabaseErrorType ERROR_INCONTINUOUS_GENRELIST = new DatabaseErrorType(1, DatabaseAutofixer::fixError_Incontinous_Genrelist);
	public static DatabaseErrorType ERROR_WRONG_GENREID = new DatabaseErrorType(2, null);
	public static DatabaseErrorType ERROR_WRONG_FILESIZE = new DatabaseErrorType(3, DatabaseAutofixer::fixError_Wrong_Filesize);
	public static DatabaseErrorType ERROR_NOGENRE_SET = new DatabaseErrorType(4, null);
	public static DatabaseErrorType ERROR_NOCOVERSET = new DatabaseErrorType(5, null);
	public static DatabaseErrorType ERROR_COVER_NOT_FOUND = new DatabaseErrorType(6, null);
	public static DatabaseErrorType ERROR_TITLE_NOT_SET = new DatabaseErrorType(7, null);
	public static DatabaseErrorType ERROR_ZYKLUSNUMBER_IS_NEGONE = new DatabaseErrorType(8, null);
	public static DatabaseErrorType ERROR_ZYKLUSTITLE_IS_EMPTY = new DatabaseErrorType(9, null);
	public static DatabaseErrorType ERROR_INCONTINUOUS_PARTS = new DatabaseErrorType(10, DatabaseAutofixer::fixError_Incontinous_Parts);
	public static DatabaseErrorType ERROR_FORMAT_NOT_FOUND_IN_PARTS = new DatabaseErrorType(11, DatabaseAutofixer::fixError_Format_Not_Found);
	public static DatabaseErrorType ERROR_PATH_NOT_FOUND = new DatabaseErrorType(12, null);
	public static DatabaseErrorType ERROR_WRONG_ADDDATE = new DatabaseErrorType(13, null);
	public static DatabaseErrorType ERROR_WRONG_LASTVIEWEDDATE = new DatabaseErrorType(14, null);
	public static DatabaseErrorType ERROR_LASTVIEWED_AND_ISVIEWED_IS_PARADOX = new DatabaseErrorType(15, null);
	public static DatabaseErrorType ERROR_NOT_TRIMMED = new DatabaseErrorType(16, DatabaseAutofixer::fixError_Not_Trimmed);
	public static DatabaseErrorType ERROR_DUPLICATE_COVERLINK = new DatabaseErrorType(17, null);
	public static DatabaseErrorType ERROR_ZYKLUS_ENDS_WITH_ROMAN = new DatabaseErrorType(18, null);
	public static DatabaseErrorType ERROR_WRONG_QUALITY = new DatabaseErrorType(19, DatabaseAutofixer::fixError_Wrong_Quality);
	public static DatabaseErrorType ERROR_DUPLICATE_TITLE = new DatabaseErrorType(20, null);
	public static DatabaseErrorType ERROR_DUPLICATE_FILELINK = new DatabaseErrorType(21, null);
	public static DatabaseErrorType ERROR_WRONG_FILENAME = new DatabaseErrorType(22, DatabaseAutofixer::fixError_Wrong_Filename);
	public static DatabaseErrorType ERROR_NONLINKED_COVERFILE = new DatabaseErrorType(23, null);
	//public static DatabaseErrorType ERROR_IMPOSSIBLE_WATCH_LATER = new DatabaseErrorType(24, DatabaseAutofixer::fixError_Impossible_WatchLater);
	public static DatabaseErrorType ERROR_LASTWATCHED_TOO_OLD = new DatabaseErrorType(25, null);
	public static DatabaseErrorType ERROR_INVALID_SERIES_STRUCTURE = new DatabaseErrorType(26, null);
	public static DatabaseErrorType ERROR_IMPOSSIBLE_WATCH_NEVER = new DatabaseErrorType(27, DatabaseAutofixer::fixError_Impossible_WatchNever);
	public static DatabaseErrorType ERROR_DUPLICATE_GENRE = new DatabaseErrorType(28, DatabaseAutofixer::fixError_Duplicate_Genre);
	public static DatabaseErrorType ERROR_INVALID_CHARS_IN_PATH = new DatabaseErrorType(29, DatabaseAutofixer::fixError_Invalid_Chars);
	public static DatabaseErrorType ERROR_INVALID_ONLINEREF = new DatabaseErrorType(30, null);
	//public static DatabaseErrorType ERROR_SERIES_HAS_HISTORY = new DatabaseErrorType(31);
	public static DatabaseErrorType ERROR_HISTORY_BUT_UNVIEWED = new DatabaseErrorType(32, null);
	public static DatabaseErrorType ERROR_INVALID_HISTORY = new DatabaseErrorType(33, null);
	public static DatabaseErrorType ERROR_INVALID_GROUPLIST = new DatabaseErrorType(34, null);
	public static DatabaseErrorType ERROR_INVALID_GROUP = new DatabaseErrorType(35, null);
	public static DatabaseErrorType ERROR_DUPLICATE_REF = new DatabaseErrorType(36, null);
	public static DatabaseErrorType ERROR_VIEWED_BUT_NO_HISTORY = new DatabaseErrorType(37, DatabaseAutofixer::fixError_MissingHistory);
	public static DatabaseErrorType ERROR_NON_CONTINOOUS_EPISODES = new DatabaseErrorType(38, null);
	public static DatabaseErrorType ERROR_COVER_TOO_SMALL = new DatabaseErrorType(39, DatabaseAutofixer::fixError_CoverTooSmall);
	public static DatabaseErrorType ERROR_COVER_TOO_BIG = new DatabaseErrorType(40, DatabaseAutofixer::fixError_CoverTooBig);
	public static DatabaseErrorType ERROR_GROUP_NESTING_TOO_DEEP = new DatabaseErrorType(41, null);
	public static DatabaseErrorType ERROR_INVALID_GROUP_PARENT = new DatabaseErrorType(42, null);
	public static DatabaseErrorType ERROR_UNUSED_GROUP = new DatabaseErrorType(43,  DatabaseAutofixer::fixError_UnusedGroup);

	private final int type;
	
	private int count = 0;
	
	private final Func1to1<DatabaseError, Boolean> autoFixFunction;

	public DatabaseErrorType(int ptype, Func1to1<DatabaseError, Boolean> fix) {
		super();
		
		this.type = ptype;
		this.autoFixFunction = fix;
	}

	public DatabaseErrorType(DatabaseErrorType ptype) {
		super();
		
		this.type = ptype.getType();
		this.count = ptype.count;
		this.autoFixFunction = ptype.autoFixFunction;
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
		return autoFixFunction != null;
	}

	public boolean fixError(DatabaseError e) {
		if (autoFixFunction == null) return false;
		if (e.getType().type != this.type) return false;
		
		return autoFixFunction.invoke(e);
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
