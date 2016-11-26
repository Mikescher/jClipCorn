package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineScore;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class TableOnlinescoreFilter extends RowFilter<ClipTableModel, Object> {
	private CCOnlineScore defScore;
	
	public TableOnlinescoreFilter(CCOnlineScore score) {
		super();
		this.defScore = score;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return defScore.equals(e.getValue(ClipTableModel.COLUMN_ONLINESCORE));
	}
}
