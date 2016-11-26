package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class TableGenreFilter extends RowFilter<ClipTableModel, Object> {
	private CCGenre defGenre;
	
	public TableGenreFilter(CCGenre genre) {
		super();
		this.defGenre = genre;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return ((CCGenreList) e.getValue(ClipTableModel.COLUMN_GENRE)).includes(defGenre);
	}
}
