package de.jClipCorn.gui.frames.importElementsFrame;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.xml.CCXMLElement;

public class ElementCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = -7918867084204290868L;

    @Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    	String val = ((CCXMLElement)value).getAttributeValueOrDefault("title", Str.Empty); //$NON-NLS-1$
        return super.getListCellRendererComponent(list, val, index, isSelected, cellHasFocus);
    }
}
