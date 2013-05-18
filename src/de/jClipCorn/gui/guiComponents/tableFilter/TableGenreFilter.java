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
		CCMovieGenreList gl = (CCMovieGenreList) e.getValue(ClipTableModel.COLUMN_GENRE);
		for (int i = 0; i < gl.getGenreCount(); i++) {
			if (defGenre.equals(gl.getGenre(i))) {
				return true;
			}
		}
		
		return false;
	}
}
