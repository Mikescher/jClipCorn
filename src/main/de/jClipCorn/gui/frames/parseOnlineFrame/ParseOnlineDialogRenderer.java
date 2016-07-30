package de.jClipCorn.gui.frames.parseOnlineFrame;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class ParseOnlineDialogRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = -7161746402093910645L;

	@Override
	public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		ParseOnlineDialogElement el = (ParseOnlineDialogElement) value;

		lbl.setText(el.Title.toString());
		lbl.setIcon(el.Reference.getIcon16x16());
		
		return lbl;
	}

}
