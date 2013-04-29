package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class TableZyklusFilter extends RowFilter<ClipTableModel, Object> {
	private String defZyklus;
	
	public TableZyklusFilter(String zyklus) {
		super();
		this.defZyklus = zyklus;
	}

	public TableZyklusFilter(CCMovieZyklus zyklus) {
		super();
		this.defZyklus = zyklus.getTitle();
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return defZyklus.equals(((CCMovieZyklus)e.getValue(3)).getTitle());
	}
}
