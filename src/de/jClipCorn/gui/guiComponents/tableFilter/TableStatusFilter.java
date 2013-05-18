package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieStatus;
import de.jClipCorn.database.databaseElement.columnTypes.CombinedMovieQuality;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class TableStatusFilter extends RowFilter<ClipTableModel, Object> {
	private CCMovieStatus defStatus;
	
	public TableStatusFilter(CCMovieStatus quality) {
		super();
		this.defStatus = quality;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return defStatus.equals(((CombinedMovieQuality)e.getValue(ClipTableModel.COLUMN_QUALITY)).getStatus());
	}
}
