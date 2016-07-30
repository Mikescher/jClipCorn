package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class TableGroupFilter extends RowFilter<ClipTableModel, Object> {
	private CCGroup defGroup;
	
	public TableGroupFilter(CCGroup group) {
		super();
		this.defGroup = group;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return ((CCDatabaseElement)e.getValue(ClipTableModel.COLUMN_TITLE)).getGroups().contains(defGroup);
	}
}
