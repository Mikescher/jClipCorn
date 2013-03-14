package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.database.databaseElement.CCMovie;
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
		if (e.getValue(4) instanceof CCMovieQuality) {
			return defQuality.equals((e.getValue(4)));
		} else {
			return defQuality.equals(((CCMovie)e.getValue(4)).getQuality());
		}
	}
}
