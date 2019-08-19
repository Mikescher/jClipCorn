package de.jClipCorn.features.databaseErrors;

import java.awt.Component;
import java.io.File;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.gui.frames.editMovieFrame.EditMovieFrame;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.formatter.PathFormatter;

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

	public String getElement1RawName() {
		return convertToRawString(el1);
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

		return null;
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
	
	public boolean autoFix() {
		return errortype.fixError(this);
	}

	public boolean elementsEquals(DatabaseError e) {
		return el1 == e.getElement1() && el2 == e.getElement2();
	}
}
