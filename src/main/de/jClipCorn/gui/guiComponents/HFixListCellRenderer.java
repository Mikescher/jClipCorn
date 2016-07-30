package de.jClipCorn.gui.guiComponents;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class HFixListCellRenderer extends DefaultListCellRenderer { // Do zero-Height Lines when an Element wants to display an empty String
	private static final long serialVersionUID = -2401531671611260785L;

    @Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    	if (value.toString().isEmpty()) {
    		return super.getListCellRendererComponent(list, " ", index, isSelected, cellHasFocus); //$NON-NLS-1$
    	} else {
    		return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    	}
    }
}
