package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class TableViewedFilter extends RowFilter<ClipTableModel, Object> {
	private boolean defViewed;
	
	public TableViewedFilter(boolean isViewed) {
		super();
		this.defViewed = isViewed;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return ! defViewed ^ ((Boolean)e.getValue(ClipTableModel.COLUMN_VIEWED));
	}
}
