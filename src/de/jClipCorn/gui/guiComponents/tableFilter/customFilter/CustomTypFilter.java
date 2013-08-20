package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;

public class CustomTypFilter extends AbstractCustomFilter {
	private CCMovieTyp typ = CCMovieTyp.MOVIE;
	
	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return ((CCDatabaseElement)e.getValue(ClipTableModel.COLUMN_TITLE)).getType() == typ;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Typ", typ); //$NON-NLS-1$
	}

	public CCMovieTyp getTyp() {
		return typ;
	}

	public void setTyp(CCMovieTyp typ) {
		this.typ = typ;
	}
}
