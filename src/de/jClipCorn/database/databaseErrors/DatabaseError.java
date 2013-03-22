package de.jClipCorn.database.databaseErrors;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.localization.LocaleBundle;

public class DatabaseError {
	public static int ERROR_INCONTINUOUS_GENRELIST = 1;
	public static int ERROR_WRONG_GENREID = 2;
	public static int ERROR_WRONG_FILESIZE = 3;
	public static int ERROR_NOGENRE_SET = 4;
	public static int ERROR_NOCOVERSET = 5;
	public static int ERROR_COVER_NOT_FOUND = 6;
	public static int ERROR_TITLE_NOT_SET = 7;
	public static int ERROR_ZYKLUSNUMBER_IS_ZERO = 8;
	public static int ERROR_ZYKLUSNUMBER_IS_NEGONE = 9;
	public static int ERROR_INCONTINUOUS_PARTS = 10;
	public static int ERROR_FORMAT_NOT_FOUND_IN_PARTS = 11;
	public static int ERROR_PATH_NOT_FOUND = 12;
	public static int ERROR_WRONG_ADDDATE = 13;
	public static int ERROR_WRONG_LASTVIEWEDDATE = 14;
	public static int ERROR_LASTVIEWED_AND_ISVIEWED_IS_PARADOX = 15;
	public static int ERROR_NOT_TRIMMED = 16;
	public static int ERROR_DUPLICATE_COVERLINK = 17;
	
	private final int error;
	private final Object el1;
	private final Object el2;
	
	private final Object additional;
	
	public static DatabaseError createSingle(int error, Object element) {
		return new DatabaseError(error, element, null, null);
	}
	
	public static DatabaseError createSingleAdditional(int error, Object element, Object additional) {
		return new DatabaseError(error, element, null, additional);
	}
	
	public static DatabaseError createDouble(int error, Object element1, Object element2) {
		return new DatabaseError(error, element1, element2, null);
	}
	
	public static DatabaseError createDoubleAdditional(int error, Object element1, Object element2, Object additional) {
		return new DatabaseError(error, element1, element2, additional);
	}

	private DatabaseError(int error, Object element1, Object element2, Object additional) {
		this.error = error;
		this.el1 = element1;
		this.el2 = element2;
		this.additional = additional;
	}
	
	public String getErrorString() {
		if (additional == null) {
			return LocaleBundle.getString(String.format("CheckDatabaseDialog.Error.ERR_%02d", error)); //$NON-NLS-1$
		} else {
			return LocaleBundle.getFormattedString(String.format("CheckDatabaseDialog.Error.ERR_%02d", error), additional); //$NON-NLS-1$
		}
	}

	public String getFullErrorString() {
		String r = ""; //$NON-NLS-1$
		
		if (el1 instanceof CCMovie) {
			r += "[" + ((CCMovie)el1).getLocalID() + "] (" + ((CCMovie)el1).getCompleteTitle() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else if (el1 instanceof CCSeries) {
			r += "[" + ((CCSeries)el1).getLocalID() + "] (" + ((CCSeries)el1).getTitle() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else if (el1 instanceof CCSeason) {
			r += "[" + ((CCSeason)el1).getSeasonID() + "] (" + ((CCSeason)el1).getSeries().getTitle() + ")(" + ((CCSeason)el1).getTitle() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		} else if (el1 instanceof CCEpisode) {
			r += "[" + ((CCEpisode)el1).getEpisode() + "] (" + ((CCEpisode)el1).getSeries().getTitle() + ")(" + ((CCEpisode)el1).getSeason().getTitle() + ")(" + ((CCEpisode)el1).getTitle() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		}
		
		r += " "; //$NON-NLS-1$
		
		if (el2 != null) {
			if (el2 instanceof CCMovie) {
				r += "[" + ((CCMovie) el1).getLocalID() + "] (" + ((CCMovie) el1).getCompleteTitle() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else if (el2 instanceof CCSeries) {
				r += "[" + ((CCSeries) el1).getLocalID() + "] (" + ((CCSeries) el1).getTitle() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else if (el2 instanceof CCSeason) {
				r += "[" + ((CCSeason) el1).getSeasonID() + "] (" + ((CCSeason) el1).getSeries().getTitle() + ")(" + ((CCSeason) el1).getTitle() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			} else if (el2 instanceof CCEpisode) {
				r += "[" + ((CCEpisode) el1).getEpisode() + "] (" + ((CCEpisode) el1).getSeries().getTitle() + ")(" + ((CCEpisode) el1).getSeason().getTitle() + ")(" + ((CCEpisode) el1).getTitle() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			}
			
			r += " "; //$NON-NLS-1$
		}
		
		r += getErrorString();
		
		return r;
	}
}
