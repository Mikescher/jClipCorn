package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class TableTypFilter extends RowFilter<ClipTableModel, Object> {
	private CCMovieTyp defTyp;
	
	public TableTypFilter(CCMovieTyp typ) {
		super();
		this.defTyp = typ;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return ((CCDatabaseElement)e.getValue(ClipTableModel.COLUMN_TITLE)).getType() == defTyp;
	}
}
