package de.jClipCorn.features.databaseErrors;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.lambda.Func2to1;

public class DatabaseErrorType {
	public final static DatabaseErrorType ERROR_INCONTINUOUS_GENRELIST             = new DatabaseErrorType(1, DatabaseAutofixer::fixError_Incontinous_Genrelist);
	public final static DatabaseErrorType ERROR_WRONG_GENREID                      = new DatabaseErrorType(2, null);
	public final static DatabaseErrorType ERROR_WRONG_FILESIZE                     = new DatabaseErrorType(3, DatabaseAutofixer::fixError_Wrong_Filesize);
	public final static DatabaseErrorType ERROR_NOGENRE_SET                        = new DatabaseErrorType(4, null);
	public final static DatabaseErrorType ERROR_NOCOVERSET                         = new DatabaseErrorType(5, null);
	public final static DatabaseErrorType ERROR_COVER_NOT_FOUND                    = new DatabaseErrorType(6, null);
	public final static DatabaseErrorType ERROR_TITLE_NOT_SET                      = new DatabaseErrorType(7, null);
	public final static DatabaseErrorType ERROR_ZYKLUSNUMBER_IS_NEGONE             = new DatabaseErrorType(8, null);
	public final static DatabaseErrorType ERROR_ZYKLUSTITLE_IS_EMPTY               = new DatabaseErrorType(9, null);
	public final static DatabaseErrorType ERROR_INCONTINUOUS_PARTS                 = new DatabaseErrorType(10, DatabaseAutofixer::fixError_Incontinous_Parts);
	public final static DatabaseErrorType ERROR_FORMAT_NOT_FOUND_IN_PARTS          = new DatabaseErrorType(11, DatabaseAutofixer::fixError_Format_Not_Found);
	public final static DatabaseErrorType ERROR_PATH_NOT_FOUND                     = new DatabaseErrorType(12, null);
	public final static DatabaseErrorType ERROR_WRONG_ADDDATE                      = new DatabaseErrorType(13, null);
	public final static DatabaseErrorType ERROR_WRONG_LASTVIEWEDDATE               = new DatabaseErrorType(14, null);
	//public final static DatabaseErrorType ERROR_LASTVIEWED_AND_ISVIEWED_IS_PARADOX = new DatabaseErrorType(15, null);
	public final static DatabaseErrorType ERROR_NOT_TRIMMED                        = new DatabaseErrorType(16, DatabaseAutofixer::fixError_Not_Trimmed);
	public final static DatabaseErrorType ERROR_DUPLICATE_COVERLINK                = new DatabaseErrorType(17, null);
	public final static DatabaseErrorType ERROR_ZYKLUS_ENDS_WITH_ROMAN             = new DatabaseErrorType(18, null);
	//public final static DatabaseErrorType ERROR_WRONG_QUALITY                      = new DatabaseErrorType(19, DatabaseAutofixer::fixError_Wrong_Quality);
	public final static DatabaseErrorType ERROR_DUPLICATE_TITLE                    = new DatabaseErrorType(20, null);
	public final static DatabaseErrorType ERROR_DUPLICATE_FILELINK                 = new DatabaseErrorType(21, null);
	public final static DatabaseErrorType ERROR_WRONG_FILENAME                     = new DatabaseErrorType(22, DatabaseAutofixer::fixError_Wrong_Filename);
	public final static DatabaseErrorType ERROR_NONLINKED_COVERFILE                = new DatabaseErrorType(23, null);
	//public final static DatabaseErrorType ERROR_IMPOSSIBLE_WATCH_LATER           = new DatabaseErrorType(24, DatabaseAutofixer::fixError_Impossible_WatchLater);
	public final static DatabaseErrorType ERROR_LASTWATCHED_TOO_OLD                = new DatabaseErrorType(25, null);
	public final static DatabaseErrorType ERROR_INVALID_SERIES_STRUCTURE           = new DatabaseErrorType(26, null);
	public final static DatabaseErrorType ERROR_IMPOSSIBLE_WATCH_NEVER             = new DatabaseErrorType(27, DatabaseAutofixer::fixError_Impossible_WatchNever);
	public final static DatabaseErrorType ERROR_DUPLICATE_GENRE                    = new DatabaseErrorType(28, DatabaseAutofixer::fixError_Duplicate_Genre);
	public final static DatabaseErrorType ERROR_INVALID_CHARS_IN_PATH              = new DatabaseErrorType(29, null);
	public final static DatabaseErrorType ERROR_INVALID_ONLINEREF                  = new DatabaseErrorType(30, null);
	//public final static DatabaseErrorType ERROR_SERIES_HAS_HISTORY               = new DatabaseErrorType(31);
	public final static DatabaseErrorType ERROR_HISTORY_BUT_UNVIEWED               = new DatabaseErrorType(32, null);
	public final static DatabaseErrorType ERROR_INVALID_HISTORY                    = new DatabaseErrorType(33, null);
	public final static DatabaseErrorType ERROR_INVALID_GROUPLIST                  = new DatabaseErrorType(34, null);
	public final static DatabaseErrorType ERROR_INVALID_GROUP                      = new DatabaseErrorType(35, null);
	public final static DatabaseErrorType ERROR_DUPLICATE_REF                      = new DatabaseErrorType(36, null);
	public final static DatabaseErrorType ERROR_VIEWED_BUT_NO_HISTORY              = new DatabaseErrorType(37, DatabaseAutofixer::fixError_MissingHistory);
	public final static DatabaseErrorType ERROR_NON_CONTINOOUS_EPISODES            = new DatabaseErrorType(38, null);
	public final static DatabaseErrorType ERROR_COVER_TOO_SMALL                    = new DatabaseErrorType(39, DatabaseAutofixer::fixError_CoverTooSmall);
	public final static DatabaseErrorType ERROR_COVER_TOO_BIG                      = new DatabaseErrorType(40, DatabaseAutofixer::fixError_CoverTooBig);
	public final static DatabaseErrorType ERROR_GROUP_NESTING_TOO_DEEP             = new DatabaseErrorType(41, null);
	public final static DatabaseErrorType ERROR_INVALID_GROUP_PARENT               = new DatabaseErrorType(42, null);
	public final static DatabaseErrorType ERROR_UNUSED_GROUP                       = new DatabaseErrorType(43, DatabaseAutofixer::fixError_UnusedGroup);
	public final static DatabaseErrorType ERROR_TAG_NOT_VALID_ON_MOVIE             = new DatabaseErrorType(44, null);
	public final static DatabaseErrorType ERROR_TAG_NOT_VALID_ON_SERIES            = new DatabaseErrorType(45, null);
	public final static DatabaseErrorType ERROR_TAG_NOT_VALID_ON_EPISODE           = new DatabaseErrorType(46, null);
	public final static DatabaseErrorType ERROR_NON_NORMALIZED_PATH                = new DatabaseErrorType(47, DatabaseAutofixer::fixError_NonNormalizedPath);
	public final static DatabaseErrorType ERROR_NO_LANG                            = new DatabaseErrorType(48, null);
	public final static DatabaseErrorType ERROR_LANG_MUTED_SUBSET                  = new DatabaseErrorType(49, null);
	public final static DatabaseErrorType ERROR_COVER_CHECKSUM_MISMATCH            = new DatabaseErrorType(50, null);
	public final static DatabaseErrorType ERROR_COVER_DIMENSIONS_MISMATCH          = new DatabaseErrorType(51, null);
	public final static DatabaseErrorType ERROR_COVER_FILESIZE_MISMATCH            = new DatabaseErrorType(52, null);
	public final static DatabaseErrorType ERROR_DUPLICATE_COVERID                  = new DatabaseErrorType(53, null);
	public final static DatabaseErrorType ERROR_COVERID_NOT_FOUND                  = new DatabaseErrorType(54, null);
	public final static DatabaseErrorType ERROR_UNUSED_COVER_ENTRY                 = new DatabaseErrorType(55, null);
	public final static DatabaseErrorType ERROR_DUPLICATE_REFERENCES_COVER_FILE    = new DatabaseErrorType(56, null);
	public final static DatabaseErrorType ERROR_MEDIAINFO_UNSET                    = new DatabaseErrorType(57, null);
	public final static DatabaseErrorType ERROR_MEDIAINFO_INVALID                  = new DatabaseErrorType(58, null);
	public final static DatabaseErrorType ERROR_MEDIAINFO_SIZE_MISMATCH            = new DatabaseErrorType(59, DatabaseAutofixer::fixError_MediaInfoFilesizeMismatch);
	public final static DatabaseErrorType ERROR_MEDIAINFO_LENGTH_MISMATCH          = new DatabaseErrorType(60, null);
	public final static DatabaseErrorType ERROR_MEDIAINFO_FILE_CHANGED             = new DatabaseErrorType(61, null);
	public final static DatabaseErrorType ERROR_DB_EXCEPTION                       = new DatabaseErrorType(62, null);
	public final static DatabaseErrorType ERROR_DB_DUPLICATE_ID                    = new DatabaseErrorType(63, null);
	public final static DatabaseErrorType ERROR_DB_TOO_LARGE_ID                    = new DatabaseErrorType(64, null);
	//public final static DatabaseErrorType ERROR_DB_TYPE_SID_MISMATCH             = new DatabaseErrorType(65, null);
	public final static DatabaseErrorType ERROR_DB_MISSING_SERIES                  = new DatabaseErrorType(66, null);
	public final static DatabaseErrorType ERROR_DB_MISSING_SEASON                  = new DatabaseErrorType(67, null);
	public final static DatabaseErrorType ERROR_DB_MULTI_REF_COVER                 = new DatabaseErrorType(68, null);
	public final static DatabaseErrorType ERROR_DB_UNUSED_COVERID                  = new DatabaseErrorType(69, null);
	public final static DatabaseErrorType ERROR_DB_DANGLING_COVERID                = new DatabaseErrorType(70, null);
	public final static DatabaseErrorType ERROR_HTRIGGER_ENABLED_ERR               = new DatabaseErrorType(71, null);
	public final static DatabaseErrorType ERROR_HTRIGGER_DISABLED_ERR              = new DatabaseErrorType(72, null);
	public final static DatabaseErrorType ERROR_INVALID_CHARACTERS                 = new DatabaseErrorType(73, DatabaseAutofixer::fixError_InvalidCharacters);
	//public final static DatabaseErrorType ERROR_MEDIAINFO_FILE_ATTR_CHANGED      = new DatabaseErrorType(74, null);
	public final static DatabaseErrorType ERROR_MEDIAINFO_CDATE_CHANGED            = new DatabaseErrorType(75, DatabaseAutofixer::fixError_MediaInfoCDate);
	public final static DatabaseErrorType ERROR_MEDIAINFO_MDATE_CHANGED            = new DatabaseErrorType(76, DatabaseAutofixer::fixError_MediaInfoMDate);
	public final static DatabaseErrorType ERROR_DUPLICATE_FILE                     = new DatabaseErrorType(77, null);
	public final static DatabaseErrorType ERROR_INVALID_HASH                       = new DatabaseErrorType(78, null);
	public final static DatabaseErrorType ERROR_IMPOSSIBLE_HASH                    = new DatabaseErrorType(79, null);
	public final static DatabaseErrorType ERROR_DB_TOO_LARGE_COVERID               = new DatabaseErrorType(80, null);
	public final static DatabaseErrorType ERROR_SUBTITLE_MUTED                     = new DatabaseErrorType(81, null);
	public final static DatabaseErrorType ERROR_INVALID_ONLINESCORE                = new DatabaseErrorType(82, null);
	public final static DatabaseErrorType ERROR_COMMENT_WITHOUT_RATING             = new DatabaseErrorType(83, null);
	public final static DatabaseErrorType ERROR_EMPTY_DIRECTORY                    = new DatabaseErrorType(84, DatabaseAutofixer::fixError_EmptyDirectory);
	public final static DatabaseErrorType ERROR_IMPOSSIBLE_WATCH_CANCELLED         = new DatabaseErrorType(85, DatabaseAutofixer::fixError_Impossible_WatchCancelled);

	private final int type;

	private final Func2to1<CCMovieList, DatabaseError, Boolean> autoFixFunction;

	private DatabaseErrorType(int ptype, Func2to1<CCMovieList, DatabaseError, Boolean> fix) {
		super();

		this.type = ptype;
		this.autoFixFunction = fix;
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
		return LocaleBundle.getString(String.format("CheckDatabaseDialog.Errornames.ERR_%02d", type));
	}

	public boolean isAutoFixable() {
		return autoFixFunction != null;
	}

	public boolean fixError(CCMovieList ml, DatabaseError e) {
		if (autoFixFunction == null) return false;
		if (e.getType().type != this.type) return false;
		
		return autoFixFunction.invoke(ml, e);
	}
}
