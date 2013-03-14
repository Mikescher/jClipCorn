package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFSK;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class TableFSKFilter extends RowFilter<ClipTableModel, Object> {
	private CCMovieFSK defFSK;
	
	public TableFSKFilter(CCMovieFSK fsk) {
		super();
		this.defFSK = fsk;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return defFSK.equals(e.getValue(11));
	}
}
