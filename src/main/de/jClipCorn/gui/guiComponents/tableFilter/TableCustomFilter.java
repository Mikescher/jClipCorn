package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class TableCustomFilter extends RowFilter<ClipTableModel, Object> {
	private AbstractCustomFilter filter;
	
	public TableCustomFilter(AbstractCustomFilter filter) {
		super();
		this.filter = filter;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return filter.include(e);
	}
}
