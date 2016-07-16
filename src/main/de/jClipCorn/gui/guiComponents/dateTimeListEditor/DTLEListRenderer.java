package de.jClipCorn.gui.guiComponents.dateTimeListEditor;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.jClipCorn.util.datetime.CCDateTime;

public class DTLEListRenderer implements ListCellRenderer<CCDateTime> {

	protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
	
	@Override
	public Component getListCellRendererComponent(JList<? extends CCDateTime> list, CCDateTime value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		if (value.isMidnight())
			renderer.setText(value.getSimpleDateStringRepresentation());
		else
			renderer.setText(value.getSimpleShortStringRepresentation());
		
		return renderer;
	}

}
