package de.jClipCorn.features.table.filter;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.gui.guiComponents.jCCPrimaryTable.JCCPrimaryTableModel;
import de.jClipCorn.properties.enumerations.MainFrameColumn;

import javax.swing.*;

public class TableCustomFilter extends RowFilter<JCCPrimaryTableModel<CCDatabaseElement, MainFrameColumn>, Object> {
	private final AbstractCustomFilter filter;
	
	public TableCustomFilter(AbstractCustomFilter filter) {
		super();
		this.filter = filter;
	}
	
	public AbstractCustomFilter getFilter() {
		return filter;
	}

	@Override
	public boolean include(Entry<? extends JCCPrimaryTableModel<CCDatabaseElement, MainFrameColumn>, ?> e) {
		CCDatabaseElement elem = (CCDatabaseElement)e.getValue(0);
		
		return filter.includes(elem);
	}
}
