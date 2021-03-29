package de.jClipCorn.gui.mainFrame.charSelector;

import javax.swing.JToolBar;

import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.mainFrame.table.RowFilterSource;
import de.jClipCorn.features.table.filter.customFilter.CustomCharFilter;

public class AbstractClipCharSortSelector extends JToolBar {
	private static final long serialVersionUID = -8270219279263812975L;
	
	protected final MainFrame owner;	
	
	public AbstractClipCharSortSelector(MainFrame mf) {
		super();
		this.owner = mf;
	}
	
	protected void onClick(String search) {
		if (search == null) {
			owner.getClipTable().setRowFilter(null, RowFilterSource.CHARSELECTOR);
		} else {
			owner.getClipTable().setRowFilter(CustomCharFilter.create(search), RowFilterSource.CHARSELECTOR);
		}
	}
}
