package de.jClipCorn.features.databaseErrors;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.gui.frames.editMovieFrame.EditMovieFrame;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FilesystemUtils;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseError {	
	public final DatabaseErrorType ErrorType;
	public final Object Element1;
	public final Object Element2;

	public final Object Additional;

	public final List<Tuple<String, String>> Metadata;

	private final CCMovieList movielist;

	public static DatabaseError createNone(CCMovieList ml, DatabaseErrorType error, String... md) {
		return new DatabaseError(ml, error, null, null, null, md);
	}

	public static DatabaseError createSingle(CCMovieList ml, DatabaseErrorType error, Object element, String... md) {
		return new DatabaseError(ml, error, element, null, null, md);
	}

	public static DatabaseError createSingle(CCMovieList ml, DatabaseErrorType error, Object element, List<Tuple<String, String>> md) {
		return new DatabaseError(ml, error, element, null, null, md);
	}

	public static DatabaseError createSingleAdditional(CCMovieList ml, DatabaseErrorType error, Object element, Object additional, String... md) {
		return new DatabaseError(ml, error, element, null, additional, md);
	}

	public static DatabaseError createDouble(CCMovieList ml, DatabaseErrorType error, Object element1, Object element2, String... md) {
		return new DatabaseError(ml, error, element1, element2, null, md);
	}
	
	public static DatabaseError createDoubleAdditional(CCMovieList ml, DatabaseErrorType error, Object element1, Object element2, Object additional, String... md) {
		return new DatabaseError(ml, error, element1, element2, additional, md);
	}

	private DatabaseError(CCMovieList ml, DatabaseErrorType error, Object element1, Object element2, Object additional, String... md) {
		var mdlist = new ArrayList<Tuple<String, String>>();
		for (int i = 0; i+1 < md.length; i+=2) mdlist.add(Tuple.Create(md[i], md[i+1]));

		this.ErrorType  = error;
		this.Element1   = element1;
		this.Element2   = element2;
		this.Additional = additional;
		this.Metadata   = mdlist;
		this.movielist  = ml;
	}

	private DatabaseError(CCMovieList ml, DatabaseErrorType error, Object element1, Object element2, Object additional, List<Tuple<String, String>> md) {
		this.ErrorType  = error;
		this.Element1   = element1;
		this.Element2   = element2;
		this.Additional = additional;
		this.Metadata   = md;
		this.movielist  = ml;
	}
	
	public String getErrorString() {
		if (Additional == null) {
			return LocaleBundle.getString(String.format("CheckDatabaseDialog.Error.ERR_%02d", ErrorType.getType())); //$NON-NLS-1$
		} else {
			return LocaleBundle.getFormattedString(String.format("CheckDatabaseDialog.Error.ERR_%02d", ErrorType.getType()), Additional); //$NON-NLS-1$
		}
	}
	
	public String getElement1Name() {
		return convertToString(Element1);
	}

	public String getElement1RawName() {
		return convertToRawString(Element1);
	}
	
	public String getElement2Name() {
		return convertToString(Element2);
	}

	public String getFullErrorString() {
		StringBuilder errstr = new StringBuilder();
		
		errstr.append(getElement1Name());
		
		errstr.append(" "); //$NON-NLS-1$
		
		if (Element2 != null) {
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
		} else if (el instanceof File) {
			return "[" + ((File)el).getName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (el instanceof CCGroup) {
			return "[" + ((CCGroup)el).Name + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (el instanceof String) {
			return "[" + el + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (el instanceof Integer) {
			return "[" + el + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (el instanceof Exception) {
			return "[" + ((Exception)el).getClass().getSimpleName() + "|" + ((Exception)el).getMessage() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else if (el instanceof FSPath) {
			return "[" + (((FSPath)el).toAbsolutePathString()) + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			return "[?]"; //$NON-NLS-1$
		}
	}

	private String convertToRawString(Object el) {
		if (el instanceof CCMovie) return ((CCMovie)el).getCompleteTitle();
		if (el instanceof CCSeries) return ((CCSeries)el).getTitle();
		if (el instanceof CCSeason) return ((CCSeason)el).getTitle();
		if (el instanceof CCEpisode) return ((CCEpisode)el).getTitle();
		if (el instanceof File) return ((File)el).getName();
		if (el instanceof CCGroup) return ((CCGroup)el).Name;
		if (el instanceof String) return ((String)el);
		if (el instanceof Integer) return Integer.toString(((Integer)el));
		if (el instanceof Exception) return ((Exception)el).getClass().getSimpleName();
		if (el instanceof FSPath) return ((FSPath)el).toAbsolutePathString();

		return null;
	}
	
	@Override
	public String toString() {
		return getFullErrorString();
	}
	
	public void startEditing(Component owner) {
		if (Element1 instanceof CCMovie) {
			EditMovieFrame emf = new EditMovieFrame(owner, (CCMovie) Element1, null);
			emf.setVisible(true);
		} else if (Element1 instanceof CCSeries) {
			EditSeriesFrame esf = new EditSeriesFrame(owner, (CCSeries) Element1, null);
			esf.setVisible(true);
		} else if (Element1 instanceof CCSeason) {
			EditSeriesFrame esf = new EditSeriesFrame(owner, (CCSeason) Element1, null);
			esf.setVisible(true);
		} else if (Element1 instanceof CCEpisode) {
			EditSeriesFrame esf = new EditSeriesFrame(owner, (CCEpisode) Element1, null);
			esf.setVisible(true);
		} else if (Element1 instanceof File) {
			FilesystemUtils.showInExplorer(FSPath.create((File) Element1));
		} else if (Element1 instanceof FSPath) {
			FilesystemUtils.showInExplorer((FSPath) Element1);
		}

		if (Element2 != null)
		{
			if (Element2 instanceof CCMovie) {
				EditMovieFrame emf = new EditMovieFrame(owner, (CCMovie) Element2, null);
				emf.setVisible(true);
			} else if (Element2 instanceof CCSeries) {
				EditSeriesFrame esf = new EditSeriesFrame(owner, (CCSeries) Element2, null);
				esf.setVisible(true);
			} else if (Element2 instanceof CCSeason) {
				EditSeriesFrame esf = new EditSeriesFrame(owner, (CCSeason) Element2, null);
				esf.setVisible(true);
			} else if (Element2 instanceof CCEpisode) {
				EditSeriesFrame esf = new EditSeriesFrame(owner, (CCEpisode) Element2, null);
				esf.setVisible(true);
			} else if (Element2 instanceof File) {
				FilesystemUtils.showInExplorer(FSPath.create((File) Element2));
			} else if (Element2 instanceof FSPath) {
				FilesystemUtils.showInExplorer((FSPath) Element2);
			}
		}

	}
	
	public Object getElement1() {
		return Element1;
	}
	
	public Object getElement2() {
		return Element1;
	}
	
	public boolean isSingleError() {
		return Element2 == null;
	}
	
	public boolean isTypeOf(DatabaseErrorType det) {
		return det.getType() == ErrorType.getType();
	}
	
	public DatabaseErrorType getType() {
		return ErrorType;
	}
	
	public boolean isAutoFixable() {
		return ErrorType.isAutoFixable();
	}
	
	public boolean autoFix() {
		return ErrorType.fixError(movielist, this);
	}

	public boolean elementsEquals(DatabaseError e) {
		return Element1 == e.getElement1() && Element2 == e.getElement2();
	}
}
