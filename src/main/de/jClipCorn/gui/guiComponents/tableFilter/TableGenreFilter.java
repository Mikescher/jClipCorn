package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenreList;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class TableGenreFilter extends RowFilter<ClipTableModel, Object> {
	private CCMovieGenre defGenre;
	
	public TableGenreFilter(CCMovieGenre genre) {
		super();
		this.defGenre = genre;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return ((CCMovieGenreList) e.getValue(ClipTableModel.COLUMN_GENRE)).includes(defGenre);
	}
}
