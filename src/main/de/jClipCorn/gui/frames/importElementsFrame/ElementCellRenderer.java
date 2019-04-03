package de.jClipCorn.gui.frames.importElementsFrame;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.jdom2.Element;

public class ElementCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = -7918867084204290868L;

    @Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    	String val = ((Element)value).getAttributeValue("title"); //$NON-NLS-1$
        return super.getListCellRendererComponent(list, val, index, isSelected, cellHasFocus);
    }
}
