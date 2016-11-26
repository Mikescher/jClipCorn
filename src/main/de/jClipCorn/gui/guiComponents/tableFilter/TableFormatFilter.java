package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class TableFormatFilter extends RowFilter<ClipTableModel, Object> {
	private CCFileFormat defFormat;
	
	public TableFormatFilter(CCFileFormat format) {
		super();
		this.defFormat = format;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return defFormat.equals(e.getValue(ClipTableModel.COLUMN_FORMAT));
	}
}
