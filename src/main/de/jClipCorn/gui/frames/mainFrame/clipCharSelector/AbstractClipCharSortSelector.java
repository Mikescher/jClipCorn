package de.jClipCorn.gui.frames.mainFrame.clipCharSelector;

import javax.swing.JToolBar;

import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.frames.mainFrame.clipTable.RowFilterSource;
import de.jClipCorn.gui.guiComponents.tableFilter.TableCharFilter;

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
			owner.getClipTable().setRowFilter(new TableCharFilter(search), RowFilterSource.CHARSELECTOR);
		}
	}
}
