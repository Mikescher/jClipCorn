package de.jClipCorn.gui.frames.createSeriesFolderStructureFrame;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.JList;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultListCellRenderer;

import de.jClipCorn.util.PathFormatter;

public class CreateSeriesFolderStructureTestListCellRenderer extends SubstanceDefaultListCellRenderer{
	private static final long serialVersionUID = 4677604803615987519L;

	@SuppressWarnings("rawtypes")
	@Override
	public Component getListCellRendererComponent(JList paramlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		setText(PathFormatter.getRelative(((File)value).getAbsolutePath()));
		
		setForeground(((File)value).exists() ? Color.RED : Color.GREEN);

		return this;
	}
}
