package de.jClipCorn.gui.frames.customFilterEditDialog;

import de.jClipCorn.features.table.filter.AbstractCustomFilter;

import javax.swing.*;
import java.awt.*;

public class CustomFilterEditFilterComboboxRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = -1317965356139131912L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    	comp.setText(((AbstractCustomFilter)value).getPrecreateName());
        return comp;
	}
}
