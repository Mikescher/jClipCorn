package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTags;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class TableTagFilter extends RowFilter<ClipTableModel, Object> {
	private int defTag;
	
	public TableTagFilter(int ptag) {
		super();
		this.defTag = ptag;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return ((CCMovieTags)e.getValue(ClipTableModel.COLUMN_TAGS)).getTag(defTag);
	}
}
