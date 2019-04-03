package de.jClipCorn.gui.frames.customFilterEditDialog;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import de.jClipCorn.features.table.filter.AbstractCustomFilter;

public class CustomFilterEditFilterComboboxRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = -1317965356139131912L;

	@Override
	public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    	comp.setText(((AbstractCustomFilter)value).getPrecreateName());
        return comp;
	}
}
