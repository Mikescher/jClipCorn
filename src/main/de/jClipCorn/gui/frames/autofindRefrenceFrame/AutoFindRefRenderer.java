package de.jClipCorn.gui.frames.autofindRefrenceFrame;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class AutoFindRefRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = -5000001785213618043L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		AutoFindRefElement element = (AutoFindRefElement) value;
		
		lbl.setText(element.local.getTitle());

		boolean istmdb = element.tmdbMeta != null && element.tmdbMeta.Source != null && element.tmdbMeta.Source.isSet();
		boolean isimdb = element.imdbMeta != null && element.imdbMeta.Source != null && element.imdbMeta.Source.isSet();
		
		if (istmdb || isimdb)
			lbl.setBackground(Color.GREEN);
		else
			lbl.setBackground(Color.RED);
		
		return lbl;
	}

}
