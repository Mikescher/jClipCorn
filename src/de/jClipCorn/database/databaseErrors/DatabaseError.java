package de.jClipCorn.database.databaseErrors;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	private final DatabaseErrorType errortype;
	private final Object el1;
	private final Object el2;
	
	private final Object additional;
	
	public static DatabaseError createSingle(DatabaseErrorType error, Object element) {
		return new DatabaseError(error, element, null, null);
	}
	
	public static DatabaseError createSingleAdditional(DatabaseErrorType error, Object element, Object additional) {
		return new DatabaseError(error, element, null, additional);
	}
	
	public static DatabaseError createDouble(DatabaseErrorType error, Object element1, Object element2) {
		return new DatabaseError(error, element1, element2, null);
	}
	
	public static DatabaseError createDoubleAdditional(DatabaseErrorType error, Object element1, Object element2, Object additional) {
		return new DatabaseError(error, element1, element2, additional);
	}

	private DatabaseError(DatabaseErrorType error, Object element1, Object element2, Object additional) {
		this.errortype = error;
		this.el1 = element1;
		this.el2 = element2;
		this.additional = additional;
	}
	
	public String getErrorString() {
		if (additional == null) {
			return LocaleBundle.getString(String.format("CheckDatabaseDialog.Error.ERR_%02d", errortype.getType())); //$NON-NLS-1$
		} else {
			return LocaleBundle.getFormattedString(String.format("CheckDatabaseDialog.Error.ERR_%02d", errortype.getType()), additional); //$NON-NLS-1$
		}
	}
	
	public String getElement1Name() {
		return convertToString(el1);
	}
	
	public String getElement2Name() {
		return convertToString(el2);
	}

	public String getFullErrorString() {
		StringBuilder errstr = new StringBuilder();
		
		errstr.append(getElement1Name());
		
		errstr.append(" "); //$NON-NLS-1$
		
		if (el2 != null) {
			errstr.append(getElement2Name());
			
			errstr.append(" "); //$NON-NLS-1$
		}
		
		errstr.append(getErrorString());
		
		return errstr.toString();
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
		} else if (el1 instanceof File) {
			return "[" + ((File)el).getName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
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
		} else if (el1 instanceof File) {
			PathFormatter.showInExplorer((File) el1);
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
	
	public boolean isTypeOf(DatabaseErrorType det) {
		return det.getType() == errortype.getType();
	}
	
	public DatabaseErrorType getType() {
		return errortype;
	}
	
	public boolean isAutoFixable() {
		return errortype.isAutoFixable();
	}

	public boolean elementsEquals(DatabaseError e) {
		return el1 == e.getElement1() && el2 == e.getElement2();
	}

	public boolean autoFix() {
		if (isTypeOf(DatabaseErrorType.ERROR_INCONTINUOUS_GENRELIST)) {
			return fixError_Incontinous_Genrelist();
		} else if (isTypeOf(DatabaseErrorType.ERROR_WRONG_FILESIZE)) {
			return fixError_Wrong_Filesize();
		} else if (isTypeOf(DatabaseErrorType.ERROR_INCONTINUOUS_PARTS)) {
			return fixError_Incontinous_Parts();
		} else if (isTypeOf(DatabaseErrorType.ERROR_FORMAT_NOT_FOUND_IN_PARTS)) {
			return fixError_Format_Not_Found();
		} else if (isTypeOf(DatabaseErrorType.ERROR_NOT_TRIMMED)) {
			return fixError_Not_Trimmed();
		} else if (isTypeOf(DatabaseErrorType.ERROR_WRONG_QUALITY)) {
			return fixError_Wrong_Quality();
		} else if (isTypeOf(DatabaseErrorType.ERROR_WRONG_FILENAME)) {
			return fixError_Wrong_Filename();
		}
		
		return false;
	}
	
	private boolean fixError_Incontinous_Genrelist() {
		if (el1 instanceof CCDatabaseElement) {
			CCDatabaseElement elem = (CCDatabaseElement) getElement1();
			CCMovieGenreList ls = new CCMovieGenreList();
			
			for (int i = 0; i < CCMovieGenreList.getMaxListSize(); i++) {
				if (! elem.getGenre(i).isEmpty()) {
					ls.addGenre(elem.getGenre(i));
				}
			}
			
			elem.setGenres(ls);
			
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
			
			if (size.getBytes() == 0) {
				return false;
			}
			
			((CCMovie) el1).setFilesize(size);
			
			return true;
		} else if (el1 instanceof CCSeries) {
			return false;
		} else if (el1 instanceof CCSeason) {
			return false;
		} else if (el1 instanceof CCEpisode) {
			long size = FileSizeFormatter.getFileSize(((CCEpisode) el1).getAbsolutePart());
			
			if (size == 0) {
				return false;
			}
			
			((CCEpisode)el1).setFilesize(size);
			
			return true;
		}
		
		return false;
	}
	
	private boolean fixError_Incontinous_Parts() {
		if (el1 instanceof CCMovie) {
			List<String> parts = new ArrayList<>();
			
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
			((CCSeries)el1).setTitle(((CCSeries)el1).getTitle().trim());
			return true;
		} else if (el1 instanceof CCSeason) {
			((CCSeason)el1).setTitle(((CCSeason)el1).getTitle().trim());
			return true;
		} else if (el1 instanceof CCEpisode) {
			((CCEpisode)el1).setTitle(((CCEpisode)el1).getTitle().trim());
			return true;
		}
		
		return false;
	}
	
	private boolean fixError_Wrong_Quality() {
		if (el1 instanceof CCMovie) {
			((CCMovie)el1).setQuality(CCMovieQuality.calculateQuality(((CCMovie)el1).getFilesize(), ((CCMovie)el1).getLength(), ((CCMovie)el1).getPartcount()));
			return true;
		} else if (el1 instanceof CCSeries) {
			return false;
		} else if (el1 instanceof CCSeason) {
			return false;
		} else if (el1 instanceof CCEpisode) {
			((CCEpisode)el1).setQuality(CCMovieQuality.calculateQuality(((CCEpisode)el1).getFilesize(), ((CCMovie)el1).getLength(), 1));
			return true;
		}
		
		return false;
	}
	
	private boolean fixError_Wrong_Filename() {
		if (el1 instanceof CCMovie) {
			CCMovie mov = ((CCMovie)el1);
			
			for (int i = 0; i < mov.getPartcount(); i++) {
				String opath = mov.getAbsolutePart(i);
				File fold = new File(opath);
				if (! fold.exists()) {
					return false;
				}
				String npath = PathFormatter.rename(opath, mov.generateFilename(i));
				File fnew = new File(npath);
				
				boolean succ = fold.renameTo(fnew);
				mov.setPart(i, PathFormatter.getRelative(npath));
				
				if (! succ) {
					return false;
				}
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
}
