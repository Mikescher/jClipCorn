package de.jClipCorn.gui.frames.createSeriesFolderStructureFrame;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import de.jClipCorn.util.formatter.PathFormatter;

public class CreateSeriesFolderStructureTestListCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 4677604803615987519L;

	@SuppressWarnings("rawtypes")
	@Override
	public Component getListCellRendererComponent(JList paramlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		DefaultListCellRenderer result = (DefaultListCellRenderer) super.getListCellRendererComponent(paramlist, value, index, isSelected, cellHasFocus);
		
		result.setText(PathFormatter.getRelative(((String)value).substring(1)));
		
		switch (((String) value).charAt(0)) {
		case '0':
			result.setForeground(Color.GREEN);
			break;
		case '1':
			result.setForeground(Color.ORANGE);
			break;
		case '2':
			result.setForeground(Color.RED);
			break;
		default:
			result.setForeground(Color.BLACK);
			break;
		}
		
		if (! isSelected && ! cellHasFocus) {
			result.setBackground(Color.BLACK);
		}

		return result;
	}
}
