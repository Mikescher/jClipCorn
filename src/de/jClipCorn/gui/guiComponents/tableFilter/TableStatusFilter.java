package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieStatus;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class TableStatusFilter extends RowFilter<ClipTableModel, Object> {
	private CCMovieStatus defStatus;
	
	public TableStatusFilter(CCMovieStatus quality) {
		super();
		this.defStatus = quality;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		if (e.getValue(4) instanceof CCMovieQuality) {
			return defStatus == CCMovieStatus.STATUS_OK;
		} else {
			return defStatus.equals(((CCMovie)e.getValue(4)).getStatus());
		}
	}
}
