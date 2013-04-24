package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class TableLanguageFilter extends RowFilter<ClipTableModel, Object> {
	private CCMovieLanguage defLanguage;
	
	public TableLanguageFilter(CCMovieLanguage language) {
		super();
		this.defLanguage = language;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return defLanguage.equals(e.getValue(5));
	}
}
