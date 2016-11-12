package de.jClipCorn.gui.frames.watchHistoryFrame;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import de.jClipCorn.util.datetime.CCDateTime;

public class WatchHistoryListCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = -9217162261427086100L;

	@SuppressWarnings("rawtypes")
	@Override
	public Component getListCellRendererComponent(JList paramlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		DefaultListCellRenderer result = (DefaultListCellRenderer) super.getListCellRendererComponent(paramlist, value, index, isSelected, cellHasFocus);
		
		result.setText(((CCDateTime)value).toStringUINormal());

		return result;
	}
}
