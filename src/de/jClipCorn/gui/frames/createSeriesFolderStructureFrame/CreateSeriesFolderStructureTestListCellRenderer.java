package de.jClipCorn.gui.frames.createSeriesFolderStructureFrame;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JList;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultListCellRenderer;

import de.jClipCorn.util.PathFormatter;

public class CreateSeriesFolderStructureTestListCellRenderer extends SubstanceDefaultListCellRenderer{
	private static final long serialVersionUID = 4677604803615987519L;

	@SuppressWarnings("rawtypes")
	@Override
	public Component getListCellRendererComponent(JList paramlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		setText(PathFormatter.getRelative(((String)value).substring(1)));
		
		switch (((String) value).charAt(0)) {
		case '0':
			setForeground(Color.GREEN);
			break;
		case '1':
			setForeground(Color.YELLOW);
			break;
		case '2':
			setForeground(Color.RED);
			break;
		default:
			setForeground(Color.BLACK);
			break;
		}

		return this;
	}
}
