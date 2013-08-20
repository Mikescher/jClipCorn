package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public abstract class AbstractCustomFilter {
	public abstract boolean include(Entry<? extends ClipTableModel, ? extends Object> e);
	
	public abstract String getName();
}
