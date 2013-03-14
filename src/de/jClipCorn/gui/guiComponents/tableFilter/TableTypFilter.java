package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

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
		return defTyp.asInt() == ((String)e.getValue(1)).charAt(0) - '0';
	}
}
