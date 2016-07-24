package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class TableFormatFilter extends RowFilter<ClipTableModel, Object> {
	private CCMovieFormat defFormat;
	
	public TableFormatFilter(CCMovieFormat format) {
		super();
		this.defFormat = format;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return defFormat.equals(e.getValue(ClipTableModel.COLUMN_FORMAT));
	}
}