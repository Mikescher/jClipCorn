package de.jClipCorn.database.databaseErrors;

import java.awt.Component;
import java.util.ArrayList;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.gui.frames.editMovieFrame.EditMovieFrame;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.FileSizeFormatter;
import de.jClipCorn.util.PathFormatter;

public class DatabaseError {
	public static int ERROR_INCONTINUOUS_GENRELIST = 1;
	public static int ERROR_WRONG_GENREID = 2;
	public static int ERROR_WRONG_FILESIZE = 3;
	public static int ERROR_NOGENRE_SET = 4;
	public static int ERROR_NOCOVERSET = 5;
	public static int ERROR_COVER_NOT_FOUND = 6;
	public static int ERROR_TITLE_NOT_SET = 7;
	public static int ERROR_ZYKLUSNUMBER_IS_NEGONE = 8;
	public static int ERROR_ZYKLUSTITLE_IS_EMPTY = 9;
	public static int ERROR_INCONTINUOUS_PARTS = 10;
	public static int ERROR_FORMAT_NOT_FOUND_IN_PARTS = 11;
	public static int ERROR_PATH_NOT_FOUND = 12;
	public static int ERROR_WRONG_ADDDATE = 13;
	public static int ERROR_WRONG_LASTVIEWEDDATE = 14;
	public static int ERROR_LASTVIEWED_AND_ISVIEWED_IS_PARADOX = 15;
	public static int ERROR_NOT_TRIMMED = 16;
	public static int ERROR_DUPLICATE_COVERLINK = 17;
	public static int ERROR_ZYKLUS_ENDS_WITH_ROMAN = 18;
	public static int ERROR_WRONG_QUALITY = 19;
	public static int ERROR_DUPLICATE_TITLE = 20;
	public static int ERROR_DUPLICATE_FILELINK = 21;
	
	private final int errorcode;
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
		this.errorcode = error;
		this.el1 = element1;
		this.el2 = element2;
		this.additional = additional;
	}
	
	public String getErrorString() {
		if (additional == null) {
			return LocaleBundle.getString(String.format("CheckDatabaseDialog.Error.ERR_%02d", errorcode)); //$NON-NLS-1$
		} else {
			return LocaleBundle.getFormattedString(String.format("CheckDatabaseDialog.Error.ERR_%02d", errorcode), additional); //$NON-NLS-1$
		}
	}
	
	public String getElement1Name() {
		return convertToString(el1);
	}
	
	public String getElement2Name() {
		return convertToString(el2);
	}

	public String getFullErrorString() {
		String r = ""; //$NON-NLS-1$
		
		r = getElement1Name();
		
		r += " "; //$NON-NLS-1$
		
		if (el2 != null) {
			r += getElement2Name();
			
			r += " "; //$NON-NLS-1$
		}
		
		r += getErrorString();
		
		return r;
	}

	private String convertToString(Object el) {
		if (el instanceof CCMovie) {
			return "[" + ((CCMovie)el).getCompleteTitle() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (el instanceof CCSeries) {
			return "[" + ((CCSeries)el).getTitle() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (el instanceof CCSeason) {
			return "[" + ((CCSeason)el).getSeries().getTitle() + " (" + ((CCSeason)el).getTitle() + ")]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else if (el instanceof CCEpisode) {
			return "[" + ((CCEpisode)el).getSeries().getTitle() + " (" + ((CCEpisode)el).getSeason().getTitle() + ")(" + ((CCEpisode)el).getTitle() + ")]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		} else {
			return "[?]"; //$NON-NLS-1$
		}
	}
	
	@Override
	public String toString() {
		return getFullErrorString();
	}
	
	public void startEditing(Component owner) {
		if (el1 instanceof CCMovie) {
			EditMovieFrame emf = new EditMovieFrame(owner, (CCMovie) el1, null);
			emf.setVisible(true);
		} else if (el1 instanceof CCSeries) {
			EditSeriesFrame esf = new EditSeriesFrame(owner, (CCSeries) el1, null);
			esf.setVisible(true);
		} else if (el1 instanceof CCSeason) {
			EditSeriesFrame esf = new EditSeriesFrame(owner, (CCSeason) el1, null);
			esf.setVisible(true);
		} else if (el1 instanceof CCEpisode) {
			EditSeriesFrame esf = new EditSeriesFrame(owner, (CCEpisode) el1, null);
			esf.setVisible(true);
		}
	}
	
	public Object getElement1() {
		return el1;
	}
	
	public Object getElement2() {
		return el1;
	}
	
	public boolean isSingleError() {
		return el2 == null;
	}
	
	public boolean isAutoFixable() {
		if (! isSingleError()) {
			return false;
		}
		
		if (errorcode == ERROR_INCONTINUOUS_GENRELIST) {
			return true;
		} else if (errorcode == ERROR_WRONG_FILESIZE) {
			return true;
		} else if (errorcode == ERROR_INCONTINUOUS_PARTS) {
			return true;
		} else if (errorcode == ERROR_FORMAT_NOT_FOUND_IN_PARTS) {
			return true;
		} else if (errorcode == ERROR_NOT_TRIMMED) {
			return true;
		} else if (errorcode == ERROR_WRONG_QUALITY) {
			return true;
		}
		
		return false;
	}

	public boolean elementsEquals(DatabaseError e) {
		return el1 == e.getElement1() && el2 == e.getElement2();
	}

	public boolean autoFix() {
		if (errorcode == ERROR_INCONTINUOUS_GENRELIST) {
			return fixError_Incontinous_Genrelist();
		} else if (errorcode == ERROR_WRONG_FILESIZE) {
			return fixError_Wrong_Filesize();
		} else if (errorcode == ERROR_INCONTINUOUS_PARTS) {
			return fixError_Incontinous_Parts();
		} else if (errorcode == ERROR_FORMAT_NOT_FOUND_IN_PARTS) {
			return fixError_Format_Not_Found();
		} else if (errorcode == ERROR_NOT_TRIMMED) {
			return fixError_Not_Trimmed();
		} else if (errorcode == ERROR_WRONG_QUALITY) {
			return fixError_Wrong_Quality();
		}
		
		return false;
	}
	
	private boolean fixError_Incontinous_Genrelist() {
		if (el1 instanceof CCDatabaseElement) {
			CCDatabaseElement m = (CCDatabaseElement) getElement1();
			CCMovieGenreList ls = new CCMovieGenreList();
			
			for (int i = 0; i < CCMovieGenreList.getMaxListSize(); i++) {
				if (! m.getGenre(i).isEmpty()) {
					ls.addGenre(m.getGenre(i));
				}
			}
			
			m.setGenres(ls);
			
			return true;
		}
		
		return false;
	}
	
	private boolean fixError_Wrong_Filesize() {
		if (el1 instanceof CCMovie) {
			CCMovieSize size = new CCMovieSize();
			for (int i = 0; i < ((CCMovie) el1).getPartcount(); i++) {
				size.add(FileSizeFormatter.getFileSize(((CCMovie) el1).getAbsolutePart(i)));
			}
			((CCMovie) el1).setFilesize(size);
			
			return true;
		} else if (el1 instanceof CCSeries) {
			return false;
		} else if (el1 instanceof CCSeason) {
			return false;
		} else if (el1 instanceof CCEpisode) {
			((CCEpisode)el1).setFilesize(FileSizeFormatter.getFileSize(((CCEpisode) el1).getPart()));
			
			return true;
		}
		
		return false;
	}
	
	private boolean fixError_Incontinous_Parts() {
		if (el1 instanceof CCMovie) {
			ArrayList<String> parts = new ArrayList<>();
			
			for (int i = 0; i < ((CCMovie)el1).getPartcount(); i++) {
				if (! ((CCMovie)el1).getPart(i).isEmpty()) {
					parts.add(((CCMovie)el1).getPart(i));
				}
			}
			
			for (int i = 0; i < CCMovie.PARTCOUNT_MAX; i++) {
				((CCMovie)el1).resetPart(i);
			}
			
			for (int i = 0; i < parts.size(); i++) {
				((CCMovie)el1).setPart(i, parts.get(i));
			}
			
			return true;
		} else if (el1 instanceof CCSeries) {
			return false;
		} else if (el1 instanceof CCSeason) {
			return false;
		} else if (el1 instanceof CCEpisode) {
			return false;
		}
		
		return false;
	}
	
	private boolean fixError_Format_Not_Found() {
		if (el1 instanceof CCMovie) {
			((CCMovie)el1).setFormat(CCMovieFormat.getMovieFormat(PathFormatter.getExtension(((CCMovie)el1).getAbsolutePart(0))));
			return true;
		} else if (el1 instanceof CCSeries) {
			return false;
		} else if (el1 instanceof CCSeason) {
			return false;
		} else if (el1 instanceof CCEpisode) {
			((CCEpisode)el1).setFormat(CCMovieFormat.getMovieFormat(PathFormatter.getExtension(((CCEpisode)el1).getAbsolutePart())));
			return true;
		}
		
		return false;
	}
	
	private boolean fixError_Not_Trimmed() {
		if (el1 instanceof CCMovie) {
			((CCMovie)el1).setTitle(((CCMovie)el1).getTitle().trim());
			((CCMovie)el1).setZyklusTitle(((CCMovie)el1).getZyklus().getTitle().trim());
			return true;
		} else if (el1 instanceof CCSeries) {
			((CCSeries)el1).setTitle(((CCSeries)el1).getTitle());
			return true;
		} else if (el1 instanceof CCSeason) {
			((CCSeason)el1).setTitle(((CCSeason)el1).getTitle());
			return true;
		} else if (el1 instanceof CCEpisode) {
			((CCEpisode)el1).setTitle(((CCEpisode)el1).getTitle());
			return true;
		}
		
		return false;
	}
	
	private boolean fixError_Wrong_Quality() {
		if (el1 instanceof CCMovie) {
			((CCMovie)el1).setQuality(CCMovieQuality.getQualityForSize(((CCMovie)el1).getFilesize(), ((CCMovie)el1).getPartcount()));
			return true;
		} else if (el1 instanceof CCSeries) {
			return false;
		} else if (el1 instanceof CCSeason) {
			return false;
		} else if (el1 instanceof CCEpisode) {
			((CCEpisode)el1).setQuality(CCMovieQuality.getQualityForSize(((CCEpisode)el1).getFilesize(), 1));
			return true;
		}
		
		return false;
	}
}
