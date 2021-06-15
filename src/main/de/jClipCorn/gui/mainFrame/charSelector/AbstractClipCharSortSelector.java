package de.jClipCorn.gui.mainFrame.charSelector;

import de.jClipCorn.features.table.filter.customFilter.CustomCharFilter;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.mainFrame.table.RowFilterSource;

import javax.swing.*;

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
			owner.getClipTable().setRowFilter(CustomCharFilter.create(owner.getMovielist(), search), RowFilterSource.CHARSELECTOR);
		}
	}
}
