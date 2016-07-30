package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class TableQualityFilter extends RowFilter<ClipTableModel, Object> {
	private CCMovieQuality defQuality;
	
	public TableQualityFilter(CCMovieQuality quality) {
		super();
		this.defQuality = quality;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return defQuality.equals(e.getValue(ClipTableModel.COLUMN_QUALITY));
	}
}
