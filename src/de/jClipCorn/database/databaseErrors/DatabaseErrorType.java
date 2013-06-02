package de.jClipCorn.database.databaseErrors;

import java.security.InvalidParameterException;

import de.jClipCorn.gui.localization.LocaleBundle;

public class DatabaseErrorType {
	private static int ERRORTYPE_COUNT = 23;
	
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
	
	private final int type;
	private int count = 0;
	
	public DatabaseErrorType(int ptype) {
		this.type = ptype;
		
		if (type <= 0 || type > ERRORTYPE_COUNT) {
			throw new InvalidParameterException("Errortype: " + ptype); //$NON-NLS-1$
		}
	}
	
	public DatabaseErrorType(DatabaseErrorType ptype) {
		this.type = ptype.getType();
		this.count = ptype.count;
		
		if (type <= 0 || type > ERRORTYPE_COUNT) {
			throw new InvalidParameterException("Errortype: " + ptype); //$NON-NLS-1$
		}
	}

	public int getType() {
		return type;
	}
	
	@Override
	public boolean equals(Object e) {
		return e instanceof DatabaseErrorType && ((DatabaseErrorType)e).getType() == this.getType();
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