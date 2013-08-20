package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenre;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;

public class CustomGenreFilter extends AbstractCustomFilter {
	private CCMovieGenre genre = CCMovieGenre.GENRE_000;
	
	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return genre.equals(e.getValue(ClipTableModel.COLUMN_GENRE));
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Genre", genre); //$NON-NLS-1$
	}

	public CCMovieGenre getGenre() {
		return genre;
	}

	public void setGenre(CCMovieGenre genre) {
		this.genre = genre;
	}
	
	@Override
	public int getID() {
		return 6;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(genre.asInt()+"");
		b.append("]");
		
		return b.toString();
	}
	
	@Override
	public boolean importFromString(String txt) {
		
	}
}
