package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class TableScoreFilter extends RowFilter<ClipTableModel, Object> {
	private CCUserScore defScore;
	
	public TableScoreFilter(CCUserScore score) {
		super();
		this.defScore = score;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return defScore.equals(e.getValue(ClipTableModel.COLUMN_SCORE));
	}
}
