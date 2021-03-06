package de.jClipCorn.features.table.filter;

import javax.swing.RowFilter;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.gui.mainFrame.table.ClipTableModel;

public class TableCustomFilter extends RowFilter<ClipTableModel, Object> {
	private AbstractCustomFilter filter;
	
	public TableCustomFilter(AbstractCustomFilter filter) {
		super();
		this.filter = filter;
	}
	
	public AbstractCustomFilter getFilter() {
		return filter;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		CCDatabaseElement elem = (CCDatabaseElement)e.getValue(ClipTableModel.COLUMN_TITLE);
		
		return filter.includes(elem);
	}
}
